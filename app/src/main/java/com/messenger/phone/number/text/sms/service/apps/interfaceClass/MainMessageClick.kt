package com.messenger.phone.number.text.sms.service.apps.interfaceClass

import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation

interface MainMessageClick {
    fun onMainClick(position: Int, list: ArrayList<Conversation>, selecteditem: ArrayList<Conversation>)
}