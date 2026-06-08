package com.messenger.phone.number.text.sms.service.apps.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.telephony.SmsManager
import android.telephony.SubscriptionManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.klinker.android.send_message.Settings
import com.klinker.android.send_message.Transaction
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import com.klinker.android.send_message.Message as SuperMessage


class SenderService: Service() {



    override fun onCreate() {

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

}
