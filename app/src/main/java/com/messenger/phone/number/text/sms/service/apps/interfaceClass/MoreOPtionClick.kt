package com.messenger.phone.number.text.sms.service.apps.interfaceClass

import com.messenger.phone.number.text.sms.service.apps.adapter.MainMassageAdapter
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation

interface MoreOPtionClick {
    fun onClickMenu(position: Int, list: ArrayList<Conversation>, holder: MainMassageAdapter.MainMassageAdapterViewHolder)
}