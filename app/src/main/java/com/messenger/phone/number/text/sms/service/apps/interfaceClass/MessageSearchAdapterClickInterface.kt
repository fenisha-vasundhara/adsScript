package com.messenger.phone.number.text.sms.service.apps.interfaceClass

import com.messenger.phone.number.text.sms.service.apps.modelClass.Contact
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation

interface MessageSearchAdapterClickInterface {

    fun MessageSearchAdapterOnClick(position: Int, list: ArrayList<Conversation>)

}