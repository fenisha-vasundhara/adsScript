package com.messenger.phone.number.text.sms.service.apps.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
import com.messenger.phone.number.text.sms.service.apps.modelClass.Category
import com.messenger.phone.number.text.sms.service.apps.modelClass.TabCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GetAllCatViewModel @Inject constructor(var repo: MessagerDatabaseRepo) : ViewModel() {

    var list = MediatorLiveData<List<Category>>()
    val livecatlist: LiveData<List<Category>>
        get() = list
    var tablist: ArrayList<Category> = arrayListOf()
    var isdatabaseemty: Boolean = true

    init {

        tablist.clear()
        list.postValue(arrayListOf())

        tablist.add(Category(1, "All", filterName = "All", true))
        tablist.add(Category(2, "Personal", filterName = "Personal", true))
        tablist.add(Category(3, "Transaction", filterName = "Transaction", true))
        tablist.add(Category(4, "OTPs", filterName = "otp", true))
        tablist.add(Category(4, "Offers", filterName = "Offers", true))

        list.addSource(repo.getallcatrepo(), Observer {
            list.postValue(it)
        })
    }


}