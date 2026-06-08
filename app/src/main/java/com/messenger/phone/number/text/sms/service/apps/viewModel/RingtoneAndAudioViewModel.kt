package com.messenger.phone.number.text.sms.service.apps.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messenger.phone.number.text.sms.service.apps.Repo.RingtoneAndAudioRepo
import com.messenger.phone.number.text.sms.service.apps.modelClass.Category
import com.messenger.phone.number.text.sms.service.apps.modelClass.Customnotificationmodel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RingtoneAndAudioViewModel @Inject constructor(var ringtoneAndAudioRepo: RingtoneAndAudioRepo) :
    ViewModel() {

    var listring = MediatorLiveData<List<Customnotificationmodel>>()
    val liveeinglist: LiveData<List<Customnotificationmodel>>
        get() = listring
    var selectedname = ""

    fun getAllAudioandNotification() {
        viewModelScope.launch {
            refreshaudiodata()
            listring.addSource(ringtoneAndAudioRepo.livelist, Observer {
                it.forEachIndexed { index, customnotificationmodel ->
                    if (customnotificationmodel.ringtonename == selectedname) {
                        customnotificationmodel.selected = true
                    } else {
                        customnotificationmodel.selected = false
                    }
                }
                listring.postValue(it)
            })
        }
    }

    fun refreshaudiodata() {
        CoroutineScope(Dispatchers.IO).launch { ringtoneAndAudioRepo.getNotificationAndAudio() }
    }

}