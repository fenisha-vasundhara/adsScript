package com.messenger.phone.number.text.sms.service.apps.modelClass

data class MessageTraslationModel(
    var traslationmessage: String,
    var message: String,
    var speaktraslationmessage: Boolean = false,
    var speakmessage: Boolean = false
)