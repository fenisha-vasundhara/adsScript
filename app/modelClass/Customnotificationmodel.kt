package com.messenger.phone.number.text.sms.service.apps.modelClass

data class Customnotificationmodel(
    var ringtonename: String,
    var ringtonepath: String,
    var ringtonecontentpath: String,
    var ringtonisplay: Boolean = false,
    var selected: Boolean = false
)