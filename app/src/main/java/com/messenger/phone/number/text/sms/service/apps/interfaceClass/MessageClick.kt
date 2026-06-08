package com.messenger.phone.number.text.sms.service.apps.interfaceClass

import com.messenger.phone.number.text.sms.service.apps.adapter.MainMassageAdapter
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation

interface MessageClick {
    fun onClick(mobilenumber: Long?, pos: Int, title: String, phoneNumber: String, holder: MainMassageAdapter.MainMassageAdapterViewHolder, list: ArrayList<Conversation>, position: Int)
}