package com.messenger.phone.number.text.sms.service.apps.services

import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import com.messenger.phone.number.text.sms.service.apps.Notification.notificationProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AutoNotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repo: MessagerDatabaseRepo

    var selecteditemmain: ArrayList<String> = arrayListOf()

    @Inject
    lateinit var notificationProvider: notificationProvider


    override fun onReceive(context: Context, intent: Intent) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "txt.Auto.messenger:Notification.message.receiver")
        wakelock.acquire(3000)
        handeNotification(context, intent)
    }

    private fun handeNotification(context: Context, intent: Intent) {
        CoroutineScope(Dispatchers.IO).launch {
            val intro = intent.getIntExtra("intro", -1)
            val requestCode = intent.getIntExtra("requestCode", -1)
//            notificationProvider.sendAutonotification(intro,requestCode)
        }
    }
}
