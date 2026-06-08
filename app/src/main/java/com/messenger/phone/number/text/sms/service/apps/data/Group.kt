package com.messenger.phone.number.text.sms.service.apps.data

import com.messenger.phone.number.text.sms.service.apps.CommanClass.FIRST_GROUP_ID
import java.io.Serializable

data class Group(
    var id: Long?,
    var title: String,
    var contactsCount: Int = 0
) : Serializable {

    fun addContact() = contactsCount++

    fun getBubbleText() = title

    fun isPrivateSecretGroup() = id ?: 0 >= FIRST_GROUP_ID
}