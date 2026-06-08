package com.messenger.phone.number.text.sms.service.apps.interfaceClass

import com.messenger.phone.number.text.sms.service.apps.data.SimpleContact


interface ContactNumberClickNew {
    fun onClick(mobilenumber: ArrayList<SimpleContact>, pos: Int, name: String)
    fun OnLongClick()
}