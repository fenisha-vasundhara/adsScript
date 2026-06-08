package com.messenger.phone.number.text.sms.service.apps.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
import com.messenger.phone.number.text.sms.service.apps.modelClass.Category
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.modelClass.Recentsearch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GetAllRecentSearchViewModel @Inject constructor(var repo: MessagerDatabaseRepo) : ViewModel() {

    var GetAllRecentSearchlist = MediatorLiveData<List<Recentsearch>>()
    val GetAllRecentSearchlivelist: LiveData<List<Recentsearch>>
        get() = GetAllRecentSearchlist

    init {
        GetAllRecentSearchlist.addSource(repo.getRecentSearchRepo(), Observer {
            GetAllRecentSearchlist.postValue(it)
        })
    }


}