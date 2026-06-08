package com.messenger.phone.number.text.sms.service.apps.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import com.google.android.datatransport.runtime.scheduling.persistence.EventStoreModule_PackageNameFactory.packageName


class SMSReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("eefefrefe", "onReceive: <-------------> 23234")
        if (context.packageName != Telephony.Sms.getDefaultSmsPackage(context)) {
            return
        }
        Log.d("eefefrefe", "onReceive: action<-------------> ${context.packageName != Telephony.Sms.getDefaultSmsPackage(context)}")
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val bundle = intent.extras
            if (bundle != null) {
                val pdus = bundle.get("pdus") as Array<*>
                val messages = arrayOfNulls<SmsMessage>(pdus.size)

//                for (i in pdus.indices) {
//                    messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
//                }
//
//                for (message in messages) {
//                    val messageBody = message?.messageBody
//                    val sender = message?.originatingAddress
//
//                    Log.d("eefefrefe", "onReceive: <-------------> ")
////                    Toast.makeText(context, "hiii", Toast.LENGTH_SHORT).show()
//                }
            }
        }
    }
}
