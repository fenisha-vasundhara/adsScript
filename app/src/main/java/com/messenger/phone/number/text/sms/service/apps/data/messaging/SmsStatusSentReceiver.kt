package com.messenger.phone.number.text.sms.service.apps.data.messaging

import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Telephony
import android.provider.Telephony.Sms
import android.telephony.SmsManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import com.messenger.phone.number.text.sms.service.apps.ApplicationClass.MessagerApplication.Companion.singleton
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getMessageRecipientAddress
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getMyContactsCursor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getNameFromAddress
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getNotificationBitmap
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getThreadId
import com.messenger.phone.number.text.sms.service.apps.CommanClass.messagingUtils
import com.messenger.phone.number.text.sms.service.apps.Notification.notificationProvider
import com.messenger.phone.number.text.sms.service.apps.ThredCallback.ensureBackgroundThread
import com.messenger.phone.number.text.sms.service.apps.data.SimpleContactsHelper
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** Handles updating databases and states when a SMS message is sent. */
@AndroidEntryPoint
class SmsStatusSentReceiver : SendStatusReceiver() {


    @Inject
    lateinit var notificationProvider: notificationProvider

    private var message: Conversation? = null

//    private var conversation: Conversation? = null

    @Inject
    lateinit var repo: MessagerDatabaseRepo

    override fun updateAndroidDatabase(context: Context, intent: Intent, receiverResultCode: Int) {

        try {
            val messageUri: Uri? = intent.data
            val resultCode = resultCode
            val messagingUtils = context.messagingUtils

            val type = if (resultCode == Activity.RESULT_OK) {
                Sms.MESSAGE_TYPE_SENT
            } else {
                Sms.MESSAGE_TYPE_FAILED
            }
            messagingUtils.updateSmsMessageSendingStatus(messageUri, type)

            messagingUtils.maybeShowErrorToast(
                resultCode = resultCode,
                errorCode = intent.getIntExtra(EXTRA_ERROR_CODE, NO_ERROR_CODE)
            )
            CoroutineScope(Dispatchers.IO).launch {
                val messageid = messageUri?.lastPathSegment?.toLongOrNull()
                    ?: return@launch
                Log.d("jigar", "onReceive: <--------------> 1 Error message ${message==null}")
                Log.d("jigar", "onReceive: <--------------> 2 Error messageid ${messageid}")
                when (resultCode) {
                    AppCompatActivity.RESULT_OK -> {
                        singleton?.let {
                            it.updatemessagestatusfrommessageRepoApp("SMS delivered", messageid)
                        }
                    }

                    SmsManager.RESULT_ERROR_GENERIC_FAILURE -> {
                        singleton?.let {
                            it.updatemessagestatusfrommessageRepoApp(
                                "Generic failure",
                                messageid
                            )
                        }
                        val conversationList = repo.getallconversationOnlyListrepo()
                        conversationList.forEach {
                            if (it.threadId != null) {
                                if (it.messageId==messageid){
                                    message = it
                                }
                            }
                        }
                        message?.let {
                            if (it.threadId != null){
                                val photoUri = SimpleContactsHelper(context).getPhotoUriFromPhoneNumber(it.phoneNumber)
                                val bitmap = context.getNotificationBitmap(photoUri)
                                notificationProvider.sendnotificationforfailedmessage(
                                    threadId = it.threadId,
                                    smsaddress = it.title?:"Not Fount",
                                    smsnumber = it.phoneNumber?:"Not Fount",
                                    smsbody = it.snippet?:"Not Fount",
                                    bitmap = bitmap
                                )
                            }

                        }
                    }

                    SmsManager.RESULT_ERROR_NO_SERVICE -> {
                        singleton?.let {
                            it.updatemessagestatusfrommessageRepoApp("No service", messageid)
                        }
                        val conversationList = repo.getallconversationOnlyListrepo()
                        conversationList.forEach {
                            if (it.threadId != null) {
                                if (it.messageId==messageid){
                                    message = it
                                }
                            }
                        }
                        message?.let {
                            if (it.threadId != null){
                                val photoUri = SimpleContactsHelper(context).getPhotoUriFromPhoneNumber(it.phoneNumber)
                                val bitmap = context.getNotificationBitmap(photoUri)
                                notificationProvider.sendnotificationforfailedmessage(
                                    threadId = it.threadId,
                                    smsaddress = it.title?:"Not Fount",
                                    smsnumber = it.phoneNumber?:"Not Fount",
                                    smsbody = it.snippet?:"Not Fount",
                                    bitmap = bitmap
                                )
                            }

                        }
                    }

                    SmsManager.RESULT_ERROR_NULL_PDU -> {
                        singleton?.let {
                            it.updatemessagestatusfrommessageRepoApp("Null PDU", messageid)
                        }
                        val conversationList = repo.getallconversationOnlyListrepo()
                        conversationList.forEach {
                            if (it.threadId != null) {
                                if (it.messageId==messageid){
                                    message = it
                                }
                            }
                        }
                        message?.let {
                            if (it.threadId != null){
                                val photoUri = SimpleContactsHelper(context).getPhotoUriFromPhoneNumber(it.phoneNumber)
                                val bitmap = context.getNotificationBitmap(photoUri)
                                notificationProvider.sendnotificationforfailedmessage(
                                    threadId = it.threadId,
                                    smsaddress = it.title?:"Not Fount",
                                    smsnumber = it.phoneNumber?:"Not Fount",
                                    smsbody = it.snippet?:"Not Fount",
                                    bitmap = bitmap
                                )
                            }

                        }
                    }

                    SmsManager.RESULT_ERROR_RADIO_OFF -> {
                        singleton?.let {
                            it.updatemessagestatusfrommessageRepoApp("Radio off", messageid)
                        }
                        val conversationList = repo.getallconversationOnlyListrepo()
                        conversationList.forEach {
                            if (it.threadId != null) {
                                if (it.messageId==messageid){
                                    message = it
                                }
                            }
                        }
                        message?.let {
                            if (it.threadId != null){
                                val photoUri = SimpleContactsHelper(context).getPhotoUriFromPhoneNumber(it.phoneNumber)
                                val bitmap = context.getNotificationBitmap(photoUri)
                                notificationProvider.sendnotificationforfailedmessage(
                                    threadId = it.threadId,
                                    smsaddress = it.title?:"Not Fount",
                                    smsnumber = it.phoneNumber?:"Not Fount",
                                    smsbody = it.snippet?:"Not Fount",
                                    bitmap = bitmap
                                )
                            }

                        }
                    }

                    else -> {
                        singleton?.let {
                            it.updatemessagestatusfrommessageRepoApp("Error", messageid)
                        }
                        val conversationList = repo.getallconversationOnlyListrepo()
                        conversationList.forEach {
                            if (it.threadId != null) {
                                if (it.messageId==messageid){
                                    message = it
                                }
                            }
                        }
                        message?.let {
                            if (it.threadId != null){
                                val photoUri = SimpleContactsHelper(context).getPhotoUriFromPhoneNumber(it.phoneNumber)
                                val bitmap = context.getNotificationBitmap(photoUri)
                                notificationProvider.sendnotificationforfailedmessage(
                                    threadId = it.threadId,
                                    smsaddress = it.title?:"Not Fount",
                                    smsnumber = it.phoneNumber?:"Not Fount",
                                    smsbody = it.snippet?:"Not Fount",
                                    bitmap = bitmap
                                )
                            }

                        }
                    }
                }
            }
        } catch (e: Exception) {
        }

    }

    override fun updateAppDatabase(context: Context, intent: Intent, receiverResultCode: Int) {
        val messageUri = intent.data
        if (messageUri != null) {
            val messageId = messageUri.lastPathSegment?.toLong() ?: 0L
            ensureBackgroundThread {
                val type = if (receiverResultCode == Activity.RESULT_OK) {
                    Sms.MESSAGE_TYPE_SENT
                } else {
                    showSendingFailedNotification(context, messageId)
                    Sms.MESSAGE_TYPE_FAILED
                }
                Log.d("jiugar", "updateAppDatabase: kkk <--------------> 1 ${type}")
            }
        }
    }

    private fun showSendingFailedNotification(context: Context, messageId: Long) {
        Handler(Looper.getMainLooper()).post {
            if (ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                return@post
            }
            val privateCursor =
                context.getMyContactsCursor(favoritesOnly = false, withPhoneNumbersOnly = true)
            ensureBackgroundThread {
                val address = context.getMessageRecipientAddress(messageId)
                val threadId = context.getThreadId(address)
                val recipientName = context.getNameFromAddress(address, privateCursor)
            }
        }
    }

    @SuppressLint("NewApi")
    fun Context.getThreadId(address: String): Long {
        return try {
            Telephony.Threads.getOrCreateThreadId(this, address)
        } catch (e: Exception) {
            0L
        }
    }

    suspend fun getThreadIdFromMessageId(context: Context, messageId: Long): Long? =
        withContext(Dispatchers.IO) {
            val uri = Uri.parse("content://sms/$messageId")
            val projection = arrayOf("thread_id")

            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val threadIdIndex = cursor.getColumnIndex("thread_id")
                    return@withContext if (threadIdIndex != -1) cursor.getLong(threadIdIndex) else null
                }
            }
            return@withContext null
        }
}
