package com.messenger.phone.number.text.sms.service.apps.interfaceClass

import com.messenger.phone.number.text.sms.service.apps.modelClass.Category

interface catAdapterLongClick {

    fun onLongClick(position: Int, list: ArrayList<Category>)
    fun onClick(list: ArrayList<Category>, position: Int)
}