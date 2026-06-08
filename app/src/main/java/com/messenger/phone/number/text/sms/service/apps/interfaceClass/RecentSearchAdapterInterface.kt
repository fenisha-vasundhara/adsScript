package com.messenger.phone.number.text.sms.service.apps.interfaceClass

import com.messenger.phone.number.text.sms.service.apps.modelClass.Recentsearch

interface RecentSearchAdapterInterface {

    fun onRecentSearchClick(postion: Int, recentlist: ArrayList<Recentsearch>)

}