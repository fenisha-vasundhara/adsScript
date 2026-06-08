package com.messenger.phone.number.text.sms.service.apps.Notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.messenger.phone.number.text.sms.service.apps.ApplicationClass.MessagerApplication
import com.messenger.phone.number.text.sms.service.apps.CommanClass.MARK_AS_READ
import com.messenger.phone.number.text.sms.service.apps.CommanClass.NOTIFICATION_CHANNEL
import com.messenger.phone.number.text.sms.service.apps.CommanClass.THREAD_ID
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.dialNumber
import com.messenger.phone.number.text.sms.service.apps.CommanClass.markThreadAsRead
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
import com.messenger.phone.number.text.sms.service.apps.ThredCallback.ensureBackgroundThread
import com.messenger.phone.number.text.sms.service.apps.sms.SmsUtils.deleteConversation
import com.messenger.phone.number.text.sms.service.apps.sms.SmsUtils.deleteSMS
import com.simplemobiletools.commons.extensions.notificationManager
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MarkAsReadReceiver() : BroadcastReceiver() {

    @Inject
    lateinit var repo: MessagerDatabaseRepo

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            MARK_AS_READ -> {
                CoroutineScope(Dispatchers.IO).launch {

                    val threadId = intent.getLongExtra(THREAD_ID, 0L)
                    val messageid = intent.getLongExtra("messageid", 0L)
                    val mobileNumber = intent.getStringExtra("mobileNumber") ?: ""

                    context.notificationManager.cancel(threadId.hashCode())
                    when (context.config.NotiButton2) {
                        "Mark as read" -> {
                            repo.setisoldmessageRepo(false, threadId)
                            repo.setisoldmessageCountRepo(0, threadId)
                            context.markThreadAsRead(threadId)
                        }

                        "Delete" -> {
                            CoroutineScope(Dispatchers.IO).launch {
                                Log.d("434234", "onReceive: <---------> MarkAsReadReceiver ${messageid}")
                                context.deleteSMS(messageid.toInt())
                                repo.deleteMessagerRepo(messageid.toInt())
                            }
                        }
                    }
                }
            }
        }
    }
}