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
class GetAllPersnalConversationViewModel @Inject constructor(messagerDatabaseRepo: MessagerDatabaseRepo) : ViewModel() {

    var GetAllPersnalConversationlist = MediatorLiveData<List<Conversation>>()
    val GetAllPersnalConversationlivelist: LiveData<List<Conversation>>
        get() = GetAllPersnalConversationlist

    init {
        GetAllPersnalConversationlist.addSource(messagerDatabaseRepo.getuserpersonalMessasgeRepo(), Observer {
            GetAllPersnalConversationlist.postValue(it)
        })
    }

}