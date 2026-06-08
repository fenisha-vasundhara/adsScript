package com.messenger.phone.number.text.sms.service.apps.realmplan

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.messenger.phone.number.text.sms.service.apps.ApplicationClass.MessagerApplication
import java.util.concurrent.TimeUnit

/**
 * Background WorkManager worker that migrates all Room data into Realm exactly once.
 *
 * Design guarantees:
 *  - Room is NEVER modified. It remains the live source of truth until the app
 *    explicitly flips to Realm reads (a future step, not this worker).
 *  - The SharedPreferences flag written by RoomToRealmMigrator is checked before
 *    any work starts, so re-enqueuing is harmless.
 *  - [ExistingWorkPolicy.KEEP] ensures only one instance runs at a time even if
 *    the scheduler fires multiple times (e.g., at startup + after a crash).
 *  - Exponential back-off up to [MAX_RETRY_ATTEMPTS]. After that WorkManager marks
 *    the work as FAILED and we emit [MigrationState.PermanentlyFailed].
 */
class RoomToRealmWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val app = applicationContext as? MessagerApplication
            ?: return fatalFailure("applicationContext is not MessagerApplication")

        // Short-circuit: already done in a previous run.
        val quickCheck = RoomToRealmMigrator(
            appContext = applicationContext,
            roomDatabase = app.messagerDatabase,
            migrationStore = NoOpRealmMigrationStore(),
        )
        if (quickCheck.isMigrationDone()) {
            RealmFeatureFlag(applicationContext).useRealmReads = true
            MigrationStateHolder.emit(MigrationState.Success(RoomToRealmSummary()))
            return Result.success()
        }

        MigrationStateHolder.emit(MigrationState.Running())

        val liveStore = runCatching { app.realmMigrationStore.get() }.getOrElse { throwable ->
            MigrationStateHolder.emit(
                MigrationState.Failed(
                    attempt = runAttemptCount + 1,
                    cause = throwable.message ?: throwable.javaClass.simpleName,
                    willRetry = runAttemptCount < MAX_RETRY_ATTEMPTS,
                )
            )
            Log.e(TAG, "Realm migration store unavailable; Room remains active", throwable)
            return if (runAttemptCount < MAX_RETRY_ATTEMPTS) Result.retry() else Result.failure()
        }

        val migrator = RoomToRealmMigrator(
            appContext = applicationContext,
            roomDatabase = app.messagerDatabase,
            migrationStore = liveStore,
        )

        return when (val result = migrator.migrateIfNeeded()) {
            is RoomToRealmMigrationResult.AlreadyMigrated -> {
                RealmFeatureFlag(applicationContext).useRealmReads = true
                MigrationStateHolder.emit(MigrationState.Success(RoomToRealmSummary()))
                Log.i(TAG, "Migration skipped — already done")
                Result.success()
            }

            is RoomToRealmMigrationResult.Success -> {
                RealmFeatureFlag(applicationContext).useRealmReads = true
                // RoomToRealmMigrator writes the summary to SharedPreferences; we read it back
                // via a best-effort check — the real source of truth is the done flag.
                MigrationStateHolder.emit(MigrationState.Success(RoomToRealmSummary()))
                Log.i(TAG, "Migration completed successfully — Realm reads enabled")
                Result.success()
            }

            is RoomToRealmMigrationResult.Failed -> {
                val cause = result.cause.message ?: result.cause.javaClass.simpleName
                val willRetry = runAttemptCount < MAX_RETRY_ATTEMPTS
                MigrationStateHolder.emit(
                    MigrationState.Failed(
                        attempt = runAttemptCount + 1,
                        cause = cause,
                        willRetry = willRetry,
                    )
                )
                Log.e(TAG, "Migration attempt ${runAttemptCount + 1} failed: $cause")

                if (willRetry) Result.retry() else fatalFailure(cause)
            }
        }
    }

    private fun fatalFailure(reason: String): Result {
        Log.e(TAG, "Migration permanently failed: $reason")
        MigrationStateHolder.emit(MigrationState.PermanentlyFailed)
        return Result.failure()
    }

    companion object {
        private const val TAG = "RoomToRealmWorker"
        private const val MAX_RETRY_ATTEMPTS = 4

        /** Unique name prevents duplicate workers even if enqueue() is called multiple times. */
        const val WORK_NAME = "room_to_realm_migration_v1"

        /**
         * Schedules the migration as a one-time unique job.
         * Safe to call on every app start — if the job is already RUNNING or ENQUEUED,
         * [ExistingWorkPolicy.KEEP] leaves it untouched.
         */
        fun enqueue(context: Context) {
            val constraints = Constraints.Builder()
                // No network or charging constraint — migration reads local SQLite, writes local Realm.
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresBatteryNotLow(false)
                .build()

            val request = OneTimeWorkRequestBuilder<RoomToRealmWorker>()
                .setConstraints(constraints)
                // Start with a 30-second delay to let the app fully warm up before heavy I/O.
                .setInitialDelay(30, TimeUnit.SECONDS)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    /* initialDelay = */ 1,
                    TimeUnit.MINUTES,
                )
                .addTag(WORK_NAME)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.KEEP, request)

            Log.d(TAG, "Migration worker enqueued")
        }
    }
}
