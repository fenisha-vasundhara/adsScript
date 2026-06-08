package com.messenger.phone.number.text.sms.service.apps.services

import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.messenger.phone.number.text.sms.service.apps.CommanClass.deleteAllSms
import com.messenger.phone.number.text.sms.service.apps.DI.RefreshAllData
import com.messenger.phone.number.text.sms.service.apps.sms.SmsUtils.deleteSMS
import com.simplemobiletools.commons.extensions.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class AutoMessagedeleteReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repo: MessagerDatabaseRepo

    var selecteditemmain: ArrayList<String> = arrayListOf()


    override fun onReceive(context: Context, intent: Intent) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "txt.messenger:auto.delete.message.receiver")
        wakelock.acquire(3000)
        CoroutineScope(Dispatchers.IO).launch {
            deleteMessages(context)
        }
//        GlobalScope.launch {
//            withContext(Dispatchers.IO) {
//                val listdata = repo.getallconversationOnlyListrepo()
//                listdata.toList().forEachIndexed { index, conversation ->
//                    val isnewmessage = conversation.isnewmessage
//                    if (isnewmessage == true) {
//                        selecteditemmain.add(conversation.threadId.toString())
//                    }
//                    if (!selecteditemmain.contains(conversation.threadId.toString())) {
//                        repo.deleteMessagerRepo(conversation.id)
//                        conversation.messageId?.toInt()?.let { it1 -> context.deleteSMS(it1) }
//                    }
//                }
//            }
//        }
    }

    private suspend fun deleteMessages(context: Context) {
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
//                repo.deleteMessagerRepo(conversation.id)
                conversation.messageId?.toInt()?.let { messageId ->
                    context.deleteSMS(messageId)
                    selecteditemmain.add(messageId.toLong().toString())
//                    repo.deleteMessagemessidRepo(messageId.toLong())
                }
            }
        }

        if (selecteditemmain.isNotEmpty()) {
            repo.deleteMessagemessidRepo(selecteditemmain)
        }
    }

    suspend fun deleteAllMessage(context: Context) {
        deleteAllSms(context)
    }
}
