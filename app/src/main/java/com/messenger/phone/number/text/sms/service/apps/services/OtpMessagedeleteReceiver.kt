package com.messenger.phone.number.text.sms.service.apps.services

import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import com.messenger.phone.number.text.sms.service.apps.sms.SmsUtils.deleteConversation
import com.simplemobiletools.commons.extensions.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OtpMessagedeleteReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repo: MessagerDatabaseRepo

    var selecteditemmain: ArrayList<String> = arrayListOf()


    override fun onReceive(context: Context, intent: Intent) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "txt.otp.messenger:scheduled.message.receiver")
        wakelock.acquire(3000)
        handeoptdelete(context)
    }

    private fun handeoptdelete(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            repo.getUserOTPMessageListrepo("otp").forEach {
                selecteditemmain.add(it.threadId.toString())
            }
            selecteditemmain.forEachIndexed { index, conversation ->
                conversation.let {
                    context.deleteConversation(it.toLong())
                }
            }
            try {
                repo.deleteConversationRepo(selecteditemmain)

            }catch (e: Exception){

            }
        }
    }
}
