package com.messenger.phone.number.text.sms.service.apps.sms

import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.provider.Telephony
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import com.messenger.phone.number.text.sms.service.apps.CommanClass.fetchContactIdFromPhoneNumber
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getMessageSendSim
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getSendMessageSettings
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isLongMmsMessage
import com.messenger.phone.number.text.sms.service.apps.CommanClass.messagingUtils
import com.messenger.phone.number.text.sms.service.apps.CommanClass.showErrorToastMess
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.data.Attachment
import com.messenger.phone.number.text.sms.service.apps.data.messaging.SmsException
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class SendSMSManager @Inject constructor(@ApplicationContext var context: Context) {

    private var simID: Int = 0
    private var conversation: Conversation? = null

    @Inject
    lateinit var messagerDatabaseRepo: MessagerDatabaseRepo

    var ismessageid: Long? = null

    @SuppressLint("DiscouragedPrivateApi", "UnspecifiedRegisterReceiverFlag")
    fun sendSMS(mobilenumber: String, message: String, tredid: Long?) {

//        Toast.makeText(context, "" + tredid, Toast.LENGTH_SHORT).show()


        val values = ContentValues().apply {
            put(Telephony.Sms.ADDRESS, mobilenumber)
            put(Telephony.Sms.BODY, message)
            put(Telephony.Sms.THREAD_ID, tredid)
        }

        val newUri = context.contentResolver.insert(Telephony.Sms.Sent.CONTENT_URI, values)
        val insertedId = newUri!!.lastPathSegment!!.toLong()

        val c: Date = Calendar.getInstance().time


        conversation = Conversation(
            0,
            c.time.toString(),
            false,
            fetchContactIdFromPhoneNumber(mobilenumber, context)!!,
            null,
            false,
            mobilenumber!!,
            message,
            c.time,
            2,
            mobilenumber.isDigitsOnly(),
            null,
            messageId = insertedId,
            threadId = tredid
        )

        CoroutineScope(Dispatchers.IO).launch {
            ismessageid = messagerDatabaseRepo.insertmessage(conversation!!)
            conversation = null
            ismessageid?.let { messagerDatabaseRepo.updatemessagestatusRepo("Sending", it) }
        }


        val SENT = "SMS_SENT"
        val DELIVERED = "SMS_DELIVERED"
        val sentPI = PendingIntent.getBroadcast(
            context, 0,
            Intent(SENT), PendingIntent.FLAG_IMMUTABLE
        )
        val deliveredPI = PendingIntent.getBroadcast(
            context, 0,
            Intent(DELIVERED), PendingIntent.FLAG_IMMUTABLE
        )

        //---when the SMS has been sent---
        context.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(arg0: Context, arg1: Intent) {
                when (resultCode) {
                    AppCompatActivity.RESULT_OK -> {

                    }

                    SmsManager.RESULT_ERROR_GENERIC_FAILURE -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            ismessageid?.let { messagerDatabaseRepo.updatemessagestatusRepo("Generic failure", it) }
                            conversation = null
                        }

                    }

                    SmsManager.RESULT_ERROR_NO_SERVICE -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            ismessageid?.let { messagerDatabaseRepo.updatemessagestatusRepo("No service", it) }
                            conversation = null
                        }
                    }

                    SmsManager.RESULT_ERROR_NULL_PDU -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            ismessageid?.let { messagerDatabaseRepo.updatemessagestatusRepo("Null PDU", it) }
                            conversation = null
                        }

                    }

                    SmsManager.RESULT_ERROR_RADIO_OFF -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            ismessageid?.let { messagerDatabaseRepo.updatemessagestatusRepo("Radio off", it) }
                            conversation = null
                        }
                    }

                    else -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            ismessageid?.let { messagerDatabaseRepo.updatemessagestatusRepo("Error", it) }
                            conversation = null
                        }
                    }
                }
            }
        }, IntentFilter(SENT))

        //---when the SMS has been delivered---
        context.registerReceiver(object : BroadcastReceiver() {
            @SuppressLint("UnspecifiedRegisterReceiverFlag")
            override fun onReceive(arg0: Context, arg1: Intent) {
                when (resultCode) {
                    AppCompatActivity.RESULT_OK -> {
                        context.unregisterReceiver(this)
                        CoroutineScope(Dispatchers.IO).launch {
                            ismessageid?.let { messagerDatabaseRepo.updatemessagestatusRepo("SMS delivered", it) }
                            conversation = null
                        }
                    }

                    AppCompatActivity.RESULT_CANCELED -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            ismessageid?.let { messagerDatabaseRepo.updatemessagestatusRepo("SMS not delivered", it) }
                            conversation = null
                        }
                    }
                }
            }
        }, IntentFilter(DELIVERED))

        val method = Class.forName("android.telephony.SubscriptionManager").getDeclaredMethod("getSubId", Int::class.java)
        method.isAccessible = true
        if (getMessageSendSim(context) == 3 || getMessageSendSim(context) == 0) {
            simID = 0
        } else if (getMessageSendSim(context) == 1) {
            simID = 1
        }

        simID = 0

        try {
            val param = method.invoke(null, simID) as IntArray
            val inst: Int = param[0]
            val smsMan = SmsManager.getSmsManagerForSubscriptionId(inst)
            smsMan.sendTextMessage(mobilenumber, null, message, sentPI, deliveredPI)
        } catch (e: Exception) {
            if (simID == 0) {
                try {
                    simID = 1
                    val param = method.invoke(null, simID) as IntArray
                    val inst: Int = param[0]
                    val smsMan = SmsManager.getSmsManagerForSubscriptionId(inst)
                    smsMan.sendTextMessage(mobilenumber, null, message, sentPI, deliveredPI)
                } catch (e: java.lang.Exception) {

                    CoroutineScope(Dispatchers.IO).launch {
                        ismessageid?.let { messagerDatabaseRepo.updatemessagestatusRepo("Error", it) }
                        conversation = null
                    }
                    Toast.makeText(context, context.resources.getString(R.string.model_download_failed), Toast.LENGTH_SHORT).show()

                }
            } else {
                try {
                    simID = 0
                    val param = method.invoke(null, simID) as IntArray
                    val inst: Int = param[0]
                    val smsMan = SmsManager.getSmsManagerForSubscriptionId(inst)
                    smsMan.sendTextMessage(mobilenumber, null, message, sentPI, deliveredPI)
                } catch (e: java.lang.Exception) {
                    CoroutineScope(Dispatchers.IO).launch {
                        ismessageid?.let { messagerDatabaseRepo.updatemessagestatusRepo("Error", it) }
                        conversation = null
                    }
                    Toast.makeText(context, context.getString(R.string.Something_Went_Wrong), Toast.LENGTH_SHORT).show()
                }
            }
        }

//        val sms = SmsManager.getDefault()
//        sms.sendTextMessage(mobilenumber, null, message, sentPI, deliveredPI)


    }

    suspend fun sendMessageCompat(
        text: String,
        addresses: List<String>,
        subId: Int?,
        attachments: List<Attachment>,
        isgroupmessage: Boolean,
        isfaildmessage: Boolean = false,
        messageid: Long = 0L,
        messagetime : Long = 0L
    ) {
        val settings = context.getSendMessageSettings()
        if (subId != null) {
            settings.subscriptionId = subId
        }
        val messagingUtils = context.messagingUtils

        val isMms = attachments.isNotEmpty() || context.isLongMmsMessage(text, settings) || addresses.size > 1 && settings.group

        if (isMms) {
            if (attachments.isNotEmpty()) {
                val lastIndex = attachments.lastIndex
                if (attachments.size > 1) {
                    for (i in 0 until lastIndex) {
                        val attachment = attachments[i]
                        messagingUtils.sendMmsMessage("", addresses, attachment, settings)
                    }
                }
                val lastAttachment = attachments[lastIndex]
                messagingUtils.sendMmsMessage(text, addresses, lastAttachment, settings)
            } else {
                messagingUtils.sendMmsMessage(text, addresses, null, settings)
            }
        } else {

            Log.d("hello", "sendMessageCompat: <------------------------> 2")
            Log.d("sendSMSManager", "setMessage:sendSMSManager <------------> isfaildmessage  3")
            try {
                messagingUtils.sendSmsMessage(
                    text,
                    addresses.toSet(),
                    settings.subscriptionId,
                    settings.deliveryReports,
                    isgroupmessage,
                    isfaildmessage,
                    messageid = messageid,
                    messagetime
                    )
            } catch (e: SmsException) {
            } catch (e: Exception) {
                Log.d("4324234", "sendMessageCompat: error <----------> " + e.localizedMessage)
                context.showErrorToastMess(e)
            }
        }
    }

}