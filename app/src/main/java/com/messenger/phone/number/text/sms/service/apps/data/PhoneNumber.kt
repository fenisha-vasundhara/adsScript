package com.messenger.phone.number.text.sms.service.apps.data

data class PhoneNumber(var value: String, var type: Int, var label: String, var normalizedNumber: String, var isPrimary: Boolean = false)

