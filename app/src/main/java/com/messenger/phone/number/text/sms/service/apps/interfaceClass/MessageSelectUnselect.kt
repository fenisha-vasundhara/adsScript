package com.messenger.phone.number.text.sms.service.apps.interfaceClass

import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation

interface MessageSelectUnselect {
    fun onMessageSelect(pos: Int, snippet: String, selectedMessageList: ArrayList<Conversation>)
}