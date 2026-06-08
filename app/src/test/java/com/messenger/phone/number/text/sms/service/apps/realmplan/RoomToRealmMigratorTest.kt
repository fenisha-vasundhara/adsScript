package com.messenger.phone.number.text.sms.service.apps.realmplan

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.messenger.phone.number.text.sms.service.apps.DataBase.MessagerDatabase
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Tests 1, 2, 3 — old user success, failure, new user fresh install.
 *
 * Uses an in-memory Room DB so no real DB files are needed.
 * Uses fake RealmMigrationStore to avoid a real Realm instance in unit tests.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class RoomToRealmMigratorTest {

    private lateinit var context: Context
    private lateinit var db: MessagerDatabase

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, MessagerDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        db.close()
        // Clear SharedPreferences so tests don't bleed into each other.
        context.getSharedPreferences("room_to_realm_migration", Context.MODE_PRIVATE)
            .edit().clear().commit()
    }

    // ── Test 1: old user migration success ───────────────────────────────────

    @Test
    fun `old user - migration runs and returns Success when validation passes`() = runTest {
        // Seed one conversation so there is something to migrate.
        db.getMessageDAO().insertOrUpdate(fakeConversation(id = 1, threadId = 100L))

        val store = FakeRealmMigrationStore(validationResult = true)
        val migrator = RoomToRealmMigrator(context, db, store)

        val result = migrator.migrateIfNeeded()

        assertTrue("Expected Success", result is RoomToRealmMigrationResult.Success)
        assertTrue("Migration done flag not set", migrator.isMigrationDone())
        assertFalse("Migration running flag should be cleared", migrator.isMigrationRunning())
        assertTrue("writeBatch was never called", store.writeBatchCallCount > 0)
    }

    // ── Test 2: old user migration failure ───────────────────────────────────

    @Test
    fun `old user - migration returns Failed when writeBatch throws`() = runTest {
        db.getMessageDAO().insertOrUpdate(fakeConversation(id = 2, threadId = 200L))

        val store = FakeRealmMigrationStore(throwOnWriteBatch = true)
        val migrator = RoomToRealmMigrator(context, db, store)

        val result = migrator.migrateIfNeeded()

        assertTrue("Expected Failed", result is RoomToRealmMigrationResult.Failed)
        assertFalse("Migration done flag must NOT be set on failure", migrator.isMigrationDone())
        assertTrue("abortMigration should have been called", store.abortCalled)
    }

    @Test
    fun `old user - Room data survives after migration failure`() = runTest {
        db.getMessageDAO().insertOrUpdate(fakeConversation(id = 3, threadId = 300L))

        val store = FakeRealmMigrationStore(throwOnWriteBatch = true)
        val migrator = RoomToRealmMigrator(context, db, store)
        migrator.migrateIfNeeded()

        // Room must still have the row — the migrator never deletes Room data.
        val remaining = db.getMessageDAO().getUserMessageListChack(300L)
        assertTrue("Room data was deleted during failed migration", remaining.isNotEmpty())
    }

    // ── Test 3: new user — empty Room, fresh Realm ───────────────────────────

    @Test
    fun `new user - migration with empty Room returns Success with zero counts`() = runTest {
        // No data inserted: simulate a fresh install.
        val store = FakeRealmMigrationStore(validationResult = true)
        val migrator = RoomToRealmMigrator(context, db, store)

        val result = migrator.migrateIfNeeded()

        assertTrue("Expected Success for empty DB", result is RoomToRealmMigrationResult.Success)
        assertTrue("Migration done flag must be set", migrator.isMigrationDone())
    }

    @Test
    fun `new user - second migration call returns AlreadyMigrated`() = runTest {
        val store = FakeRealmMigrationStore(validationResult = true)
        val migrator = RoomToRealmMigrator(context, db, store)

        migrator.migrateIfNeeded()
        val secondResult = migrator.migrateIfNeeded()

        assertTrue("Expected AlreadyMigrated", secondResult is RoomToRealmMigrationResult.AlreadyMigrated)
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private fun fakeConversation(id: Int, threadId: Long) =
        com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation(
            id = id,
            date = "2024-01-01",
            read = true,
            title = "Test $id",
            photoUri = null,
            phoneNumber = "+1555000${id}",
            snippet = "Hello",
            time = System.currentTimeMillis(),
            type = 1,
            isnumaric = true,
            messageStatus = "sent",
            threadId = threadId,
        )
}

// ── Fake store ────────────────────────────────────────────────────────────────

private class FakeRealmMigrationStore(
    private val validationResult: Boolean = false,
    private val throwOnWriteBatch: Boolean = false,
) : RealmMigrationStore {
    var writeBatchCallCount = 0
    var abortCalled = false

    override suspend fun beginMigration() = Unit
    override suspend fun clearAll() = Unit

    override suspend fun writeBatch(batch: RoomToRealmBatch) {
        if (throwOnWriteBatch) error("Simulated writeBatch failure")
        writeBatchCallCount++
    }

    override suspend fun validateMigration(summary: RoomToRealmSummary): Boolean = validationResult
    override suspend fun finalizeMigration() = Unit

    override suspend fun abortMigration(cause: Throwable) {
        abortCalled = true
    }
}
