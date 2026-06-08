package com.messenger.phone.number.text.sms.service.apps.realmplan

import android.content.Context
import android.util.Log
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.messenger.phone.number.text.sms.service.apps.BuildConfig
import io.github.xilinjia.krdb.Realm
import io.github.xilinjia.krdb.ext.query
//import io.realm.kotlin.Realm
//import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.first

/**
 * Debug-only helper for testing the Room→Realm migration on a real device.
 *
 * NEVER call any method here in release builds — every public entry point is
 * guarded by [BuildConfig.DEBUG].
 *
 * ── HOW TO RUN A CLEAN MIGRATION TEST ────────────────────────────────────────
 *
 * 1.  Install the debug build on a device that already has Room data (old user).
 * 2.  In your debug Activity/Fragment, call [resetAndReschedule] once.
 * 3.  Observe [MigrationStateHolder.state] in your UI or logcat tag "MigrationDebug".
 * 4.  After the worker finishes (logcat: "Migration completed successfully"),
 *     call [printRealmCounts] to verify row counts match Room.
 * 5.  Optionally call [compareFirstConversation] for a field-level diff.
 *
 * ── HOW TO TEST FAILURE RECOVERY ─────────────────────────────────────────────
 *
 * 1.  Call [resetAndReschedule].
 * 2.  Kill the app mid-migration (adb shell am force-stop <package>).
 * 3.  Relaunch — WorkManager automatically retries with exponential back-off.
 * 4.  Verify that Room data is still intact and the app works normally.
 *
 * ── HOW TO TEST ON A FRESH INSTALL (NEW USER) ────────────────────────────────
 *
 * Fresh installs have an empty Room DB. Migration runs, writes 0 rows, marks
 * itself done. Call [printRealmCounts] — all values should be 0.
 *
 * ── HOW TO TEST AN OLDER APK UPGRADE ─────────────────────────────────────────
 *
 * 1.  Install an older production APK on the device (which has existing Room data).
 * 2.  adb install -r <new-debug-apk>
 * 3.  Room DB migrations run automatically (versions 1→37 already applied).
 * 4.  On first launch the worker is enqueued with a 30-second delay.
 * 5.  Check logcat and [printRealmCounts].
 */
object RealmMigrationDebugHelper {

    private const val TAG = "MigrationDebug"

    /**
     * Clears the migration done/running flags AND cancels any pending WorkManager job,
     * then immediately re-enqueues it. Use this to re-run the migration from scratch.
     *
     * ONLY works in DEBUG builds.
     */
    fun resetAndReschedule(context: Context, migrator: RoomToRealmMigrator, realm: Realm) {
        check(BuildConfig.DEBUG) { "resetAndReschedule must not be called in release builds" }

        Log.w(TAG, "⚠️  Resetting migration state for debug re-run")

        // 1. Clear the SharedPreferences flags so migrateIfNeeded() runs again.
        migrator.clearMigrationFlagsForDebugOnly()

        // 2. Delete all Realm data so we start from a clean slate.
        realm.writeBlocking { deleteAll() }
        Log.i(TAG, "Realm contents wiped")

        // 3. Cancel the old worker if still queued/running.
        WorkManager.getInstance(context).cancelUniqueWork(RoomToRealmWorker.WORK_NAME)

        // 4. Re-enqueue with a fresh request.
        RoomToRealmWorker.enqueue(context)
        Log.i(TAG, "Worker re-enqueued — migration will start in ~30 seconds")
    }

    /**
     * Prints the count of every Realm table to logcat.
     * Call after migration completes to verify parity with Room.
     */
    fun printRealmCounts(realm: Realm) {
        check(BuildConfig.DEBUG) { "printRealmCounts must not be called in release builds" }

        Log.i(TAG, "──── Realm table counts ────────────────────────────────")
        Log.i(TAG, "  Conversation        : ${realm.query<ConversationRealm>().count().find()}")
        Log.i(TAG, "  ConversationBin     : ${realm.query<ConversationBinRealm>().count().find()}")
        Log.i(TAG, "  Category            : ${realm.query<CategoryRealm>().count().find()}")
        Log.i(TAG, "  CategoryNumber      : ${realm.query<CategoryNumberRealm>().count().find()}")
        Log.i(TAG, "  Contact             : ${realm.query<ContactRealm>().count().find()}")
        Log.i(TAG, "  RecentSearch        : ${realm.query<RecentSearchRealm>().count().find()}")
        Log.i(TAG, "  Reminder            : ${realm.query<ReminderRealm>().count().find()}")
        Log.i(TAG, "  StarNumber          : ${realm.query<StarNumberRealm>().count().find()}")
        Log.i(TAG, "  Folder              : ${realm.query<FolderRealm>().count().find()}")
        Log.i(TAG, "  Photo               : ${realm.query<PhotoRealm>().count().find()}")
        Log.i(TAG, "  MessageAttachment   : ${realm.query<MessageAttachmentRealm>().count().find()}")
        Log.i(TAG, "  Attachment          : ${realm.query<AttachmentRealm>().count().find()}")
        Log.i(TAG, "────────────────────────────────────────────────────────")
    }

    /**
     * Logs the first ConversationRealm's key fields to verify field mapping is correct.
     */
    fun compareFirstConversation(realm: Realm) {
        check(BuildConfig.DEBUG) { "compareFirstConversation must not be called in release builds" }

        val c = realm.query<ConversationRealm>().first().find()
        if (c == null) {
            Log.w(TAG, "No ConversationRealm found — table is empty")
            return
        }
        Log.i(TAG, "First ConversationRealm:")
        Log.i(TAG, "  id          = ${c.id}")
        Log.i(TAG, "  threadId    = ${c.threadId}")
        Log.i(TAG, "  messageId   = ${c.messageId}")
        Log.i(TAG, "  phoneNumber = ${c.phoneNumber}")
        Log.i(TAG, "  snippet     = ${c.snippet.take(60)}")
        Log.i(TAG, "  time        = ${c.time}")
        Log.i(TAG, "  isarchived  = ${c.isarchived}")
        Log.i(TAG, "  categoryName= ${c.categoryName}")
        Log.i(TAG, "  hasAttachment=${c.messagewithattachment != null}")
    }

    /**
     * Dumps the current WorkManager state of the migration job.
     */
    suspend fun printWorkerState(context: Context) {
        check(BuildConfig.DEBUG) { "printWorkerState must not be called in release builds" }

        val infos = WorkManager.getInstance(context)
            .getWorkInfosForUniqueWork(RoomToRealmWorker.WORK_NAME)
            .get()

        if (infos.isEmpty()) {
            Log.w(TAG, "No WorkInfo found for '${RoomToRealmWorker.WORK_NAME}'")
            return
        }
        infos.forEach { info ->
            Log.i(TAG, "Worker state: ${info.state} | attempts: ${info.runAttemptCount} | id: ${info.id}")
        }
    }
}
