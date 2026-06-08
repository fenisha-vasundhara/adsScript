package com.messenger.phone.number.text.sms.service.apps.Repo

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getAllCacheAudioModels
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getNotificationSounds
import com.messenger.phone.number.text.sms.service.apps.modelClass.Contact
import com.messenger.phone.number.text.sms.service.apps.modelClass.Customnotificationmodel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class RingtoneAndAudioRepo @Inject constructor(@ApplicationContext var context: Context) {
    var list = MutableLiveData<List<Customnotificationmodel>>()
    val livelist: LiveData<List<Customnotificationmodel>>
        get() = list
    var allaudiolist: ArrayList<Customnotificationmodel> = arrayListOf()

    suspend fun getNotificationAndAudio() {
        allaudiolist.clear()
        allaudiolist.addAll(context.getAllCacheAudioModels())
        allaudiolist.addAll(context.getNotificationSounds())
        list.postValue(allaudiolist)
    }

}