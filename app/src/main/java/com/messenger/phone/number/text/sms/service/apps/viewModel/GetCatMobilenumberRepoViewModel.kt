package com.messenger.phone.number.text.sms.service.apps.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
import com.messenger.phone.number.text.sms.service.apps.modelClass.CategoryNumber
import com.messenger.phone.number.text.sms.service.apps.modelClass.Contact

class GetCatMobilenumberRepoViewModel(var repo: MessagerDatabaseRepo, var catName: String) : ViewModel() {

    var list = MediatorLiveData<List<Contact>>()
    val livelist: LiveData<List<Contact>>
        get() = list

    init {
        list.addSource(repo.getCatMobilenumberRepo(catName), Observer {
            list.postValue(it)
        })
    }

}