package com.messenger.phone.number.text.sms.service.apps.Repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.messenger.phone.number.text.sms.service.apps.data.GetContactNumber
import com.messenger.phone.number.text.sms.service.apps.modelClass.Contact
import javax.inject.Inject

class GetContactNumberRepo @Inject constructor(var getContactNumber: GetContactNumber) {

    var list = MutableLiveData<List<Contact>>()
    val livelist: LiveData<List<Contact>>
        get() = list

    suspend fun getContactNumberRepo() {
        val result = getContactNumber.getmobilenumber()
        list.postValue(result)
    }

}