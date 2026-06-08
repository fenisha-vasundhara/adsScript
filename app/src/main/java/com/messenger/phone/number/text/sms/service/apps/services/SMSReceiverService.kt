package com.messenger.phone.number.text.sms.service.apps.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder

class SMSReceiverService: Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}