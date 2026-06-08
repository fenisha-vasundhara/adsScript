package com.messenger.phone.number.text.sms.service.apps.services

import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.telephony.SmsManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.SCHEDULED_MESSAGE_ID
import com.messenger.phone.number.text.sms.service.apps.CommanClass.THREAD_ID
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.sms.SendSMSManager
import com.simplemobiletools.commons.extensions.showErrorToast
import com.simplemobiletools.commons.extensions.toast
import com.simplemobiletools.commons.helpers.ensureBackgroundThread
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NewMessageTraslateMessageReceiver : BroadcastReceiver() {


    private var message: Conversation? = null

    @Inject
    lateinit var repo: MessagerDatabaseRepo

    @Inject
    lateinit var sendSMSManager: SendSMSManager


    override fun onReceive(context: Context, intent: Intent) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "txt.translate.messenger:scheduled.message.receiver")
        wakelock.acquire(3000)

//        context.toast("Done")

        ensureBackgroundThread {
            handleIntent(context, intent)
        }
    }

    private fun handleIntent(context: Context, intent: Intent) {
//        val threadId = intent.getLongExtra(THREAD_ID, 0L)
//        val messageId = intent.getLongExtra(SCHEDULED_MESSAGE_ID, 0L)
//        message = repo.getScheduledMessageWithIdRepo(threadId, messageId)

        try {
            val subscriptionId = SmsManager.getDefaultSmsSubscriptionId()
//            Handler(Looper.getMainLooper()).post {
//                message?.let {
//                    sendSMSManager.sendMessageCompat(it.snippet, listOf(it.phoneNumber), subscriptionId, arrayListOf())
//                }
//
//            }
//            context.toast("messagetimedone")
//            CoroutineScope(Dispatchers.IO).launch {
//                repo.removedeletemessageRepo(messageId)
//            }
        } catch (e: Exception) {
            context.showErrorToast(e)
        } catch (e: Error) {
            context.showErrorToast(e.localizedMessage ?: context.getString(R.string.unknown_error_occurred))
        }
    }
}
