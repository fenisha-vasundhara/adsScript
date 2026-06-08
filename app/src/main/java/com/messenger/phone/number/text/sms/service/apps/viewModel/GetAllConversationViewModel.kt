package com.messenger.phone.number.text.sms.service.apps.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.realmplan.ConversationDataSourceProvider
import com.messenger.phone.number.text.sms.service.apps.realmplan.RealmFeatureFlag
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.Lazy
import io.github.xilinjia.krdb.Realm
import javax.inject.Inject


@HiltViewModel
class GetAllConversationViewModel @Inject constructor(
    messagerDatabaseRepo: MessagerDatabaseRepo,
    @ApplicationContext context: Context,
    realm: Lazy<Realm>,
) : ViewModel() {

    private val sortedConversations = MediatorLiveData<List<Conversation>>()
    private val sortedConversationsAlldata = MediatorLiveData<List<Conversation>>()

    init {
        val realmInstance = if (RealmFeatureFlag(context).useRealmReads) {
            runCatching { realm.get() }.getOrNull()
        } else {
            null
        }
        val dataSource = ConversationDataSourceProvider.get(context, messagerDatabaseRepo, realmInstance)

        sortedConversations.addSource(dataSource.observeUnarchivedConversations()) { conversations ->
            val sortedList = conversations.distinctBy { it.threadId }
                .sortedWith(
                    compareByDescending<Conversation> {
                        context.config.pinnedConversations.contains(it.threadId.toString())
                    }.thenByDescending { it.date }
                )
            sortedConversations.postValue(sortedList)
        }

        sortedConversationsAlldata.addSource(dataSource.observeUnarchivedConversations()) { conversations ->
            sortedConversationsAlldata.postValue(conversations.orEmpty())
        }
    }

    val GetAllConversationlivelist: LiveData<List<Conversation>>
        get() = sortedConversations

    val GetAllConversationlivelistAlldata: LiveData<List<Conversation>>
        get() = sortedConversationsAlldata

}
