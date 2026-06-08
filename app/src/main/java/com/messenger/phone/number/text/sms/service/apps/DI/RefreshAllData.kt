package com.messenger.phone.number.text.sms.service.apps.DI

import android.widget.Toast
import com.messenger.phone.number.text.sms.service.apps.ApplicationClass.MessagerApplication.Companion.singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RefreshAllData() {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            singleton?.chackDataBase()
        }
    }

}