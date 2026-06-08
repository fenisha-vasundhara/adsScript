package com.messenger.phone.number.text.sms.service.apps.realmplan

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.paging.PagingSource
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversationbin
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
//import io.realm.kotlin.Realm
//import io.realm.kotlin.ext.query
//import io.realm.kotlin.query.Sort
import io.github.xilinjia.krdb.Realm
import io.github.xilinjia.krdb.ext.query
import io.github.xilinjia.krdb.query.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOn

/**
 * Realm-backed reads + Room-backed writes.
 *
 * Reads come from the Realm replica written by [RoomToRealmMigrator].
 * All mutations are delegated to [MessagerDatabaseRepo] (Room) so the source
 * of truth doesn't change until a full dual-write cutover is implemented.
 *
 * This class is intentionally NOT a Hilt @Singleton — it should be obtained
 * through [ConversationDataSourceProvider] which applies the feature flag.
 */
class RealmConversationDataSource(
    private val realm: Realm,
    private val repo: MessagerDatabaseRepo,
) : ConversationDataSourcePlan {

    // ── Reads (Realm) ────────────────────────────────────────────────────────

    override fun observeUnarchivedConversations(): LiveData<List<Conversation>> =
        realm.query<ConversationRealm>(
            "isarchived == false AND isblocknumber == false " +
                "AND isPrivateChat == false AND isScheduled == false"
        )
            .sort("time", Sort.DESCENDING)
            .asFlow()
            .map { change -> change.list.map { it.toConversationSummary() } }
            .flowOn(Dispatchers.Default)
            .asLiveData(context = Dispatchers.Default)

    override fun pagedUnarchivedConversations(): PagingSource<Int, Conversation> =
        RealmConversationPagingSource(realm)

    override fun observeMessages(threadId: Long): LiveData<List<Conversation>> =
        realm.query<ConversationRealm>()
            .sort("time", Sort.ASCENDING)
            .asFlow()
            .map { change ->
                change.list
                    .filter { it.threadId == threadId }
                    .map { it.toConversation() }
            }
            .asLiveData()

    override fun observeDeletedMessages(threadId: Long): LiveData<List<Conversationbin>> =
        realm.query<ConversationBinRealm>()
            .sort("time", Sort.ASCENDING)
            .asFlow()
            .map { change ->
                change.list
                    .filter { it.threadId == threadId }
                    .map { it.toConversationBin() }
            }
            .asLiveData()

    // ── Writes (Room — delegated until dual-write is enabled) ────────────────

    override suspend fun insertConversation(conversation: Conversation): Long =
        repo.insertmessage(conversation)

    override suspend fun insertConversations(conversations: List<Conversation>) =
        repo.insertOrUpdateList(conversations)

    override suspend fun moveThreadToRecycleBin(threadId: Long) =
        repo.moveConversationsToRecycleBin(listOf(threadId))

    override suspend fun archiveThreads(threadIds: List<String>) =
        repo.archivConversationRepo(threadIds)

    override suspend fun unarchiveThreads(threadIds: List<String>) =
        repo.removearchivConversationRepo(threadIds)

    override suspend fun setPrivate(threadIds: List<String>, enabled: Boolean) {
        if (enabled) repo.PrivacyConversationRepo(threadIds)
        else repo.removePrivacyConversationRepo(threadIds)
    }

    override suspend fun setBlocked(threadId: Long, blocked: Boolean) {
        if (blocked) repo.addnumbertoblockRepo(threadId)
        else repo.removenumbertoblockRepo(threadId)
    }

    override suspend fun markThreadRead(threadId: Long) {
        repo.setisoldmessageRepo(false, threadId)
        repo.setisoldmessageCountRepo(0, threadId)
    }
}
