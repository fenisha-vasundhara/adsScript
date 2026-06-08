package com.messenger.phone.number.text.sms.service.apps.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
import com.messenger.phone.number.text.sms.service.apps.modelClass.StarNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GetAllStarContactViewModel @Inject constructor(var repo: MessagerDatabaseRepo) : ViewModel() {

    var list = MediatorLiveData<List<StarNumber>>()
    val livelist: LiveData<List<StarNumber>>
        get() = list

    init {
        list.addSource(repo.getAllStarContactRepo(), Observer {
            list.postValue(it)
        })
    }

}