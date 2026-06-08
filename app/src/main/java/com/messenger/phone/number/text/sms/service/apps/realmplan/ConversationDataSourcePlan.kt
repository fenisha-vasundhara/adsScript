package com.messenger.phone.number.text.sms.service.apps.realmplan

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversationbin

/**
 * This is the seam the app is missing today.
 *
 * The current Room repository exposes hundreds of methods directly against a
 * concrete DAO. That makes a storage swap painful. The safest migration path is
 * to define a focused contract around hot chat operations, keep Room behind one
 * implementation, then add Realm behind another.
 */
interface ConversationDataSourcePlan {
    fun observeUnarchivedConversations(): LiveData<List<Conversation>>
    fun pagedUnarchivedConversations(): PagingSource<Int, Conversation>
    fun observeMessages(threadId: Long): LiveData<List<Conversation>>
    fun observeDeletedMessages(threadId: Long): LiveData<List<Conversationbin>>

    suspend fun insertConversation(conversation: Conversation): Long
    suspend fun insertConversations(conversations: List<Conversation>)
    suspend fun moveThreadToRecycleBin(threadId: Long)
    suspend fun archiveThreads(threadIds: List<String>)
    suspend fun unarchiveThreads(threadIds: List<String>)
    suspend fun setPrivate(threadIds: List<String>, enabled: Boolean)
    suspend fun setBlocked(threadId: Long, blocked: Boolean)
    suspend fun markThreadRead(threadId: Long)
}

/**
 * Recommended next refactor after the migration seam exists:
 * - Rename MessagerDatabaseRepo to RoomConversationDataSource
 * - Introduce ConversationRepository that depends on ConversationDataSourcePlan
 * - Move non-chat tables into separate data sources instead of one giant DAO facade
 */
object RepositoryCutoverPlan
