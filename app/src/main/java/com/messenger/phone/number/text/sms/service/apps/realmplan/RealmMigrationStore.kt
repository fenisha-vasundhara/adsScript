package com.messenger.phone.number.text.sms.service.apps.realmplan

import io.github.xilinjia.krdb.Realm
import io.github.xilinjia.krdb.UpdatePolicy
//import io.realm.kotlin.Realm
//import io.realm.kotlin.UpdatePolicy

/**
 * Write target for the migration pipeline.
 *
 * NoOpRealmMigrationStore keeps the app fully Room-backed during development.
 * LiveRealmMigrationStore is wired once the Realm instance is available.
 */
interface RealmMigrationStore {
    suspend fun beginMigration()
    suspend fun clearAll()
    suspend fun writeBatch(batch: RoomToRealmBatch)
    suspend fun validateMigration(summary: RoomToRealmSummary): Boolean
    suspend fun finalizeMigration()
    suspend fun abortMigration(cause: Throwable)
}

// ── No-op (safe default while Room is still the source of truth) ──────────────

class NoOpRealmMigrationStore : RealmMigrationStore {
    override suspend fun beginMigration() = Unit
    override suspend fun clearAll() = Unit
    override suspend fun writeBatch(batch: RoomToRealmBatch) = Unit
    override suspend fun validateMigration(summary: RoomToRealmSummary): Boolean = false
    override suspend fun finalizeMigration() = Unit
    override suspend fun abortMigration(cause: Throwable) = Unit
}

// ── Live implementation backed by krdb Realm ──────────────────────────────────

class LiveRealmMigrationStore(private val realm: Realm) : RealmMigrationStore {

    override suspend fun beginMigration() = Unit // Realm is already open

    override suspend fun clearAll() {
        realm.write { deleteAll() }
    }

    override suspend fun writeBatch(batch: RoomToRealmBatch) {
        realm.write {
            batch.conversations.forEach         { copyToRealm(it, UpdatePolicy.ALL) }
            batch.conversationBins.forEach      { copyToRealm(it, UpdatePolicy.ALL) }
            batch.categories.forEach            { copyToRealm(it, UpdatePolicy.ALL) }
            batch.categoryNumbers.forEach       { copyToRealm(it, UpdatePolicy.ALL) }
            batch.contacts.forEach              { copyToRealm(it, UpdatePolicy.ALL) }
            batch.recentSearches.forEach        { copyToRealm(it, UpdatePolicy.ALL) }
            batch.reminders.forEach             { copyToRealm(it, UpdatePolicy.ALL) }
            batch.starNumbers.forEach           { copyToRealm(it, UpdatePolicy.ALL) }
            batch.folders.forEach               { copyToRealm(it, UpdatePolicy.ALL) }
            batch.photos.forEach                { copyToRealm(it, UpdatePolicy.ALL) }
            batch.messageAttachments.forEach    { copyToRealm(it, UpdatePolicy.ALL) }
            batch.standaloneAttachments.forEach { copyToRealm(it, UpdatePolicy.ALL) }
        }
    }

    override suspend fun validateMigration(summary: RoomToRealmSummary): Boolean =
        MigrationValidator(realm).validate(summary)

    override suspend fun finalizeMigration() = Unit // Realm writes are ACID, nothing to flush

    override suspend fun abortMigration(cause: Throwable) {
        runCatching { realm.write { deleteAll() } }
    }
}

// ── Batch payload ─────────────────────────────────────────────────────────────

data class RoomToRealmBatch(
    val conversations: List<ConversationRealm> = emptyList(),
    val conversationBins: List<ConversationBinRealm> = emptyList(),
    val categories: List<CategoryRealm> = emptyList(),
    val categoryNumbers: List<CategoryNumberRealm> = emptyList(),
    val contacts: List<ContactRealm> = emptyList(),
    val recentSearches: List<RecentSearchRealm> = emptyList(),
    val reminders: List<ReminderRealm> = emptyList(),
    val starNumbers: List<StarNumberRealm> = emptyList(),
    val folders: List<FolderRealm> = emptyList(),
    val photos: List<PhotoRealm> = emptyList(),
    val messageAttachments: List<MessageAttachmentRealm> = emptyList(),
    val standaloneAttachments: List<AttachmentRealm> = emptyList()
)

data class RoomToRealmSummary(
    val conversationCount: Int = 0,
    val conversationBinCount: Int = 0,
    val categoryCount: Int = 0,
    val categoryNumberCount: Int = 0,
    val contactCount: Int = 0,
    val recentSearchCount: Int = 0,
    val reminderCount: Int = 0,
    val starNumberCount: Int = 0,
    val folderCount: Int = 0,
    val photoCount: Int = 0,
    val messageAttachmentCount: Int = 0,
    val standaloneAttachmentCount: Int = 0
)

sealed interface RoomToRealmMigrationResult {
    data object Success : RoomToRealmMigrationResult
    data object AlreadyMigrated : RoomToRealmMigrationResult
    data class Failed(val cause: Throwable) : RoomToRealmMigrationResult
}
