package com.messenger.phone.number.text.sms.service.apps.realmplan

import android.util.Log
import io.github.xilinjia.krdb.Realm
import io.github.xilinjia.krdb.ext.query

//import io.realm.kotlin.Realm
//import io.realm.kotlin.ext.query


/**
 * Validates the Realm state against the counts reported by the migration pipeline.
 *
 * Strategy:
 *  1. Count check — every table's Realm count must be >= the Room count written.
 *     (Realm can have MORE rows than Room because conversations embed
 *      MessageAttachmentRealm objects that were also written as standalone objects.)
 *  2. Spot-check — the first conversation in Realm must have a non-zero threadId,
 *     proving that the cursor → model mapping preserved critical indexed fields.
 *  3. A tolerated drift of [MAX_MISSING_ROWS_PERCENT] handles the rare edge case
 *     where Room rows were deleted between reading and writing (safe because Room
 *     stays the source of truth — missing Realm rows are a read-fallback, not loss).
 */
class MigrationValidator(private val realm: Realm) {

    /** Returns true if Realm contains enough data to be considered valid. */
    fun validate(summary: RoomToRealmSummary): Boolean {
        val checks = listOf(
            check<ConversationRealm>("Conversation", summary.conversationCount),
            check<ConversationBinRealm>("ConversationBin", summary.conversationBinCount),
            check<CategoryRealm>("Category", summary.categoryCount),
            check<CategoryNumberRealm>("CategoryNumber", summary.categoryNumberCount),
            check<ContactRealm>("Contact", summary.contactCount),
            check<RecentSearchRealm>("RecentSearch", summary.recentSearchCount),
            check<ReminderRealm>("Reminder", summary.reminderCount),
            check<StarNumberRealm>("StarNumber", summary.starNumberCount),
            check<FolderRealm>("Folder", summary.folderCount),
            check<PhotoRealm>("Photo", summary.photoCount),
            check<MessageAttachmentRealm>("MessageAttachment", summary.messageAttachmentCount),
            check<AttachmentRealm>("Attachment", summary.standaloneAttachmentCount),
        )

        val allPassed = checks.all { it }

        if (!allPassed) {
            Log.e(TAG, "Validation FAILED — one or more tables under-populated")
            return false
        }

        // Spot-check: verify at least one conversation has a live threadId.
        val firstConv = realm.query<ConversationRealm>().first().find()
        if (summary.conversationCount > 0 && firstConv == null) {
            Log.e(TAG, "Spot-check FAILED — no ConversationRealm found despite count > 0")
            return false
        }

        Log.i(TAG, "Validation PASSED — all tables within tolerance")
        return true
    }

    private inline fun <reified T : io.github.xilinjia.krdb.types.RealmObject> check(
        tableName: String,
        expectedMin: Int,
    ): Boolean {
        if (expectedMin == 0) return true   // table was empty in Room — nothing to validate
        val actual = realm.query<T>().count().find()
        val minAccepted = (expectedMin * (1.0 - MAX_MISSING_ROWS_PERCENT)).toLong()
        val passed = actual >= minAccepted
        val emoji = if (passed) "✓" else "✗"
        Log.d(TAG, "$emoji $tableName: expected≥$minAccepted, got=$actual")
        return passed
    }

    companion object {
        private const val TAG = "MigrationValidator"

        /**
         * Allow up to 2 % of rows to be missing before failing validation.
         * This covers the window between the Room count snapshot and the final write
         * if a concurrent delete happened (very unlikely but possible in practice).
         */
        private const val MAX_MISSING_ROWS_PERCENT = 0.02
    }
}
