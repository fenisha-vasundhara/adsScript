package com.messenger.phone.number.text.sms.service.apps.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation

class GetUserConversationViewModel(var messagerDatabaseRepo: MessagerDatabaseRepo, var userphoneNumber: String) : ViewModel() {

    var GetUesrConversationlist = MediatorLiveData<List<Conversation>>()
    val GetUserConversationlivelist: LiveData<List<Conversation>>
        get() = GetUesrConversationlist


}