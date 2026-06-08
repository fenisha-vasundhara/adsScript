package com.messenger.phone.number.text.sms.service.apps.AfterCallScreen.Receiver

import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.Telephony
import android.telephony.SmsManager
import android.util.Log
import com.demo.adsmanage.Commen.log
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ADDRESS_SEPARATOR
import com.messenger.phone.number.text.sms.service.apps.CommanClass.SCHEDULED_MESSAGE_ID
import com.messenger.phone.number.text.sms.service.apps.CommanClass.THREAD_ID
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getAddresses
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getNotificationBitmap
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getSendMessageSettings
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getThreadId
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getThreadParticipants
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getThreadTitle
import com.messenger.phone.number.text.sms.service.apps.CommanClass.insertSmsMessageForGroup
import com.messenger.phone.number.text.sms.service.apps.Notification.notificationProvider
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.data.SimpleContactsHelper
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.sms.SendSMSManager
import com.simplemobiletools.commons.extensions.showErrorToast
import com.simplemobiletools.commons.extensions.toInt
import com.simplemobiletools.commons.extensions.toast
import com.simplemobiletools.commons.helpers.ensureBackgroundThread
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class ScheduledRemainderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repo: MessagerDatabaseRepo

    @Inject
    lateinit var notificationProvider: notificationProvider

    override fun onReceive(context: Context, intent: Intent) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakelock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "txt.messenger:scheduled.message.receiver"
        )
        wakelock.acquire(3000)

        ensureBackgroundThread {
            handleIntent(context, intent)
        }
    }

    private fun handleIntent(context: Context, intent: Intent) {
        val notificariontitle = intent.getStringExtra("remindertitle")
        val reminderid = intent.getStringExtra("reminderid")
        "remainder data <--------------------> notificariontitle <-----> ${notificariontitle} reminderid <-----> ${reminderid}".log()
        CoroutineScope(Dispatchers.IO).launch {
            val data = repo.getRemainderMessageRepo(reminderid!!.toLong())
            "remainder data <--------------------> data <-----> ${data}".log()
            data?.remindertitle?.let { notificationProvider.SendRemainderNotification(it, 5445454) }
            data?.id?.let { repo.deleteRemainderRepo(it) }
        }
    }
}
