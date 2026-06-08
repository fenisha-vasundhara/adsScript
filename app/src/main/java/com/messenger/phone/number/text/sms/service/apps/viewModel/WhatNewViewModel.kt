package com.messenger.phone.number.text.sms.service.apps.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messenger.phone.number.text.sms.service.apps.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WhatNewViewModel @Inject constructor() : ViewModel() {

    var listdata: ArrayList<String> = arrayListOf()
    var whatsnewlist = MutableLiveData<List<String>>()
    val whatsnewlivelist: LiveData<List<String>>
        get() = whatsnewlist

    fun getwhatsnew(context: Context) {
        listdata.clear()
        viewModelScope.launch(Dispatchers.IO) {
            listdata.add(context.resources.getString(R.string.Privacy_chat))
            listdata.add(context.resources.getString(R.string.two_Step_Verification_new))
            listdata.add(context.resources.getString(R.string.Conversation_Setting))
            listdata.add(context.resources.getString(R.string.Message_Corner))
            listdata.add(context.resources.getString(R.string.color_theme))
            listdata.add(context.resources.getString(R.string.Auto_Reply))
            listdata.add(context.resources.getString(R.string.Driving_Mode))
            listdata.add(context.resources.getString(R.string.Translate_Conversation))
            whatsnewlist.postValue(listdata)
        }


    }

}