package com.messenger.phone.number.text.sms.service.apps.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversationbin
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GetAllConversationDeleteViewModel @Inject constructor(messagerDatabaseRepo: MessagerDatabaseRepo) : ViewModel() {

    var GetAllConversationlist = MediatorLiveData<List<Conversationbin>>()
    val GetAllConversationlivelist: LiveData<List<Conversationbin>>
        get() = GetAllConversationlist

    init {
        GetAllConversationlist.addSource(messagerDatabaseRepo.getalldeleteconversationunrepo(), Observer {
            GetAllConversationlist.postValue(it)
        })
    }

}