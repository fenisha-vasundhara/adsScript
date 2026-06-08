package com.messenger.phone.number.text.sms.service.apps.interfaceClass

interface ContactNumberClick {
    fun onClick(mobilenumber: String, pos: Int, name: String)
    fun OnLongClick()
}