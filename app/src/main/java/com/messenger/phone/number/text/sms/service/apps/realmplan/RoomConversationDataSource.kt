package com.messenger.phone.number.text.sms.service.apps.realmplan

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversationbin
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo

/**
 * Room-backed implementation of [ConversationDataSourcePlan].
 * Every call delegates to the existing [MessagerDatabaseRepo] — no behaviour changes.
 * This class exists so [ConversationDataSourceProvider] can swap Realm in without
 * touching any ViewModel or Fragment code.
 */
class RoomConversationDataSource(
    private val repo: MessagerDatabaseRepo,
) : ConversationDataSourcePlan {

    // ── Reads ────────────────────────────────────────────────────────────────

    override fun observeUnarchivedConversations(): LiveData<List<Conversation>> =
        repo.getallconversationunarchivrepo()

    override fun pagedUnarchivedConversations(): PagingSource<Int, Conversation> =
        repo.getPaginatedConversations()

    override fun observeMessages(threadId: Long): LiveData<List<Conversation>> =
        repo.getAllUserMessagerepo(threadId)

    override fun observeDeletedMessages(threadId: Long): LiveData<List<Conversationbin>> =
        repo.getUserDeleteMessagerepo(threadId)

    // ── Writes ───────────────────────────────────────────────────────────────

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
