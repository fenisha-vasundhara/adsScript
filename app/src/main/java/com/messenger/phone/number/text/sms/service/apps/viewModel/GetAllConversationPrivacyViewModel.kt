package com.messenger.phone.number.text.sms.service.apps.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GetAllConversationPrivacyViewModel @Inject constructor(messagerDatabaseRepo: MessagerDatabaseRepo) : ViewModel() {

    var GetAllConversationlist = MediatorLiveData<List<Conversation>>()
    val GetAllConversationlivelist: LiveData<List<Conversation>>
        get() = GetAllConversationlist

    init {
        GetAllConversationlist.addSource(messagerDatabaseRepo.getallconversationunprivacyOrnotrepo(), Observer {
            GetAllConversationlist.postValue(it)
        })
    }

}