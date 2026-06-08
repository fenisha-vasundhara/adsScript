package com.messenger.phone.number.text.sms.service.apps.Dialog.feedback

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeedbackRequest(
    @SerialName("app_name")
    val appName: String,
    @SerialName("app_version")
    val appVersion: String,
    @SerialName("name")
    val name: String,
    @SerialName("email_id")
    val email_id: String,
    @SerialName("feedback")
    val feedback: String
)
