package com.messenger.phone.number.text.sms.service.apps.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log

class SmsDelBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("", "onReceive: SmsDelBroadcastReceiver <---------> ")
        if (intent.action == Telephony.Sms.Intents.SMS_DELIVER_ACTION) {
            val bundle = intent.extras
            val pdus = bundle?.get("pdus") as Array<Any>?

            pdus?.let {
                for (pdu in it) {
                    val smsMessage = SmsMessage.createFromPdu(pdu as ByteArray)
                    val messageBody = smsMessage.messageBody
                    val sender = smsMessage.originatingAddress
                }
            }
        }
    }
}