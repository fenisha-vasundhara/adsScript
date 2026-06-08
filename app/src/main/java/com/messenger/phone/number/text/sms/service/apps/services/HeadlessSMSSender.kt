package com.messenger.phone.number.text.sms.service.apps.services

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.telephony.TelephonyManager
import android.text.TextUtils


class HeadlessSMSSender : Service() {

    private fun getRecipients(uri: Uri): String {
        val base: String = uri.schemeSpecificPart
        val pos = base.indexOf('?')
        return if (pos == -1) base else base.substring(0, pos)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.action !in arrayOf(Intent.ACTION_SENDTO,
                TelephonyManager.ACTION_RESPOND_VIA_MESSAGE)) {
            return super.onStartCommand(intent, flags, startId)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}