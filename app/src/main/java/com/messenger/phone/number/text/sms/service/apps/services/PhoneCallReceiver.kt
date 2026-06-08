package com.messenger.phone.number.text.sms.service.apps.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import kotlin.math.log

class PhoneCallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            com.messenger.phone.number.text.sms.service.apps.adsnew.Constants.isAdsClicking = true
        }
    }
}