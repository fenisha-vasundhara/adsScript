package com.messenger.phone.number.text.sms.service.apps.modelClass

import androidx.annotation.Keep

@Keep
data class SmsData(
    val sms: List<SmsMessage>
)

@Keep
data class SmsMessage(
    val address: String,
    val body: String,
    val date: Long,
    val date_sent: Long,
    val locked: Int,
    val read: Int,
    val status: Int,
    val sub_id: Int,
    val type: Int,
    val protocol: String? = null
)