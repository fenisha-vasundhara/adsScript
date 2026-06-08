package com.messenger.phone.number.text.sms.service.apps.modelClass

data class ProfileNotificationmodel(
    val tredid: String,
    var iscstomselected: Boolean = false,
    var isdefault: Boolean = false,
    var notificationname: String = "",
    var notificationUri: String = "",
    var allprofile: Boolean = false
)