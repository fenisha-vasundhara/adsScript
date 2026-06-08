package com.messenger.phone.number.text.sms.service.apps.Notification

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.RemoteInput
import com.messenger.phone.number.text.sms.service.apps.CommanClass.REPLY
import com.messenger.phone.number.text.sms.service.apps.CommanClass.THREAD_ID
import com.messenger.phone.number.text.sms.service.apps.CommanClass.THREAD_NUMBER
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.fetchContactIdFromPhoneNumber
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getNotificationBitmap
import com.messenger.phone.number.text.sms.service.apps.CommanClass.markThreadAsRead
import com.messenger.phone.number.text.sms.service.apps.CommanClass.notificationHelper
import com.messenger.phone.number.text.sms.service.apps.CommanClass.removeDiacriticsIfNeeded
import com.messenger.phone.number.text.sms.service.apps.CommanClass.subscriptionManagerCompat
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
import com.messenger.phone.number.text.sms.service.apps.sms.SendSMSManager
import com.simplemobiletools.commons.extensions.showErrorToast
import com.simplemobiletools.commons.helpers.SimpleContactsHelper
import com.simplemobiletools.commons.helpers.ensureBackgroundThread
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DirectReplyReceiver : BroadcastReceiver() {
    @SuppressLint("MissingPermission")

    @Inject
    lateinit var sendSMSManager: SendSMSManager

    @Inject
    lateinit var repo: MessagerDatabaseRepo


    override fun onReceive(context: Context, intent: Intent) {
        val address = intent.getStringExtra(THREAD_NUMBER)
        val threadId = intent.getLongExtra(THREAD_ID, 0L)
        val messageid = intent.getLongExtra("messageid", 0L)

        var body = RemoteInput.getResultsFromIntent(intent)?.getCharSequence(REPLY)?.toString() ?: return


        body = context.removeDiacriticsIfNeeded(body)


        if (address != null) {
            var subscriptionId: Int? = null
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            val availableSIMs = context.subscriptionManagerCompat().activeSubscriptionInfoList
            if ((availableSIMs?.size ?: 0) > 1) {
                val currentSIMCardIndex = context.config.getUseSIMIdAtNumber(address)
                val wantedId = availableSIMs?.getOrNull(currentSIMCardIndex)
                if (wantedId != null) {
                    subscriptionId = SmsManager.getDefaultSmsSubscriptionId()
                }
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    sendSMSManager.sendMessageCompat(body, listOf(address), subscriptionId, emptyList(),false)
                } catch (e: Exception) {
                    context.showErrorToast(e)
                }

                val photoUri = SimpleContactsHelper(context).getPhotoUriFromPhoneNumber(address)
                val bitmap = context.getNotificationBitmap(photoUri)
                Handler(Looper.getMainLooper()).post {
                    context.notificationHelper.showMessageNotification(address, body, threadId, bitmap, fetchContactIdFromPhoneNumber(address, context), alertOnlyOnce = true, messageid = messageid)
                }

                repo.setisoldmessageRepo(false, threadId)
                repo.setisoldmessageCountRepo(0, threadId)
                context.markThreadAsRead(threadId)
            }
        }
    }
}
