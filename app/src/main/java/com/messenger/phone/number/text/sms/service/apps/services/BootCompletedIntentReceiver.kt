package com.messenger.phone.number.text.sms.service.apps.services

import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import com.messenger.phone.number.text.sms.service.apps.CommanClass.AutonotificationStat
import com.messenger.phone.number.text.sms.service.apps.CommanClass.fetchContactIdFromPhoneNumber
import com.messenger.phone.number.text.sms.service.apps.CommanClass.scheduleMessage
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.simplemobiletools.commons.extensions.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootCompletedIntentReceiver : BroadcastReceiver() {

    @Inject
    lateinit var messagerDatabaseRepo: MessagerDatabaseRepo

    var scheduledmessagelist: ArrayList<Conversation> = arrayListOf()

    private lateinit var conversation: Conversation

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onReceive(context: Context, intent: Intent) {
        if ("android.intent.action.BOOT_COMPLETED" == intent.action) {
            val permission = Manifest.permission.SCHEDULE_EXACT_ALARM
            if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED
            ) {
                context.AutonotificationStat(0, 0L, 1)
                messagerDatabaseRepo.getalldataconversationrepo().observeForever {
                    setMyalarm(context, it)
                }
            }

        }
    }

    fun setMyalarm(context: Context, conversations: List<Conversation>) {
        conversations.forEachIndexed { index, conversation ->
            setscheduledmessage(context, conversation)
        }
    }

    private fun setscheduledmessage(context: Context, conversations: Conversation) {
        CoroutineScope(Dispatchers.IO).launch {
            with(conversations) {
                if (conversations.is_scheduled)
                    conversation = this.phoneNumber.let {
                        Conversation(
                            0,
                            this.date,
                            false,
                            this.title,
                            null,
                            false,
                            it,
                            this.snippet,
                            this.time, 2,
                            it.isDigitsOnly(),
                            "ScheduledMessage",
                            messageId = this.messageId,
                            threadId = this.threadId,
                            is_scheduled = true
                        )
                    }
            }
            try {
                context.scheduleMessage(conversation)
            } catch (_: Exception) {
            }
        }
    }
}