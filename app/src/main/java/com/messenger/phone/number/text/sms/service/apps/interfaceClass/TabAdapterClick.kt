package com.messenger.phone.number.text.sms.service.apps.interfaceClass

import com.messenger.phone.number.text.sms.service.apps.modelClass.Category
import com.messenger.phone.number.text.sms.service.apps.modelClass.TabCategory

interface TabAdapterClick {

    fun onClickTab(tabname: String, position: Int, list: ArrayList<Category>)

}