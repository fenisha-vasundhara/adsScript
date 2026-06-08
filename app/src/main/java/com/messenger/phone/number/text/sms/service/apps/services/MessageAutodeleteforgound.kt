package com.messenger.phone.number.text.sms.service.apps.services

import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import com.messenger.phone.number.text.sms.service.apps.sms.SmsUtils.deleteSMS
import com.simplemobiletools.commons.extensions.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MessageAutodeleteforgound : Service() {

    @Inject
    lateinit var repo: MessagerDatabaseRepo

    private var selecteditemmain: ArrayList<String> = arrayListOf()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Acquire a wake lock
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "txt.messenger:auto.delete.message.receiver")
        wakelock.acquire(3000)
        CoroutineScope(Dispatchers.IO).launch {
            deleteMessages()
        }
//        stopSelf()
        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private suspend fun deleteMessages() {
        val listData = repo.getallconversationOnlyListrepo()
        val selectedThreadIds = listData.mapNotNull { conversation ->
            if (conversation.isnewmessage == true) {
                conversation.threadId.toString()
            } else {
                null
            }
        }

        listData.forEach { conversation ->
            if (!selectedThreadIds.contains(conversation.threadId.toString())) {
                Log.d("selectedThreadIds", "deleteMessages: selectedThreadIds <---------> ${conversation.id}")
                conversation.messageId?.toInt()?.let { messageId ->
                    deleteSMS(messageId)
                    selecteditemmain.add(messageId.toLong().toString())
                }
            }
        }

        if (selecteditemmain.isNotEmpty()) {
            repo.deleteMessagemessidRepo(selecteditemmain)
        }
    }
}
