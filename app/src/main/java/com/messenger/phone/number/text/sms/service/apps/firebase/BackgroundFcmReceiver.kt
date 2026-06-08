package com.messenger.phone.number.text.sms.service.apps.firebase

import android.content.Context
import android.content.Intent
import androidx.legacy.content.WakefulBroadcastReceiver
import com.demo.adsmanage.Commen.log

class BackgroundFcmReceiver : WakefulBroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        val extras = intent?.extras?.keySet()
        if (extras != null) {
            for (key in extras) {
                "BackgroundFcmReceiver setting <-----------------> 6 ${key}".log()
            }
        }
    }
}