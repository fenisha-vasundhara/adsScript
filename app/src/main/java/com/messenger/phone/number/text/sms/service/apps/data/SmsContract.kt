package com.messenger.phone.number.text.sms.service.apps.data

import android.net.Uri

object SmsContract {
    @JvmField
    val ALL_SMS_URI = Uri.parse("content://sms/")
    const val SMS_SELECTION = "address = ? "
    const val SMS_SELECTION_ID = "_id = ? "
    const val COLUMN_ID = "_id"
    const val SMS_SELECTION_SEARCH = "address LIKE ? OR body LIKE ?"
    const val SORT_DESC = "date DESC"
}