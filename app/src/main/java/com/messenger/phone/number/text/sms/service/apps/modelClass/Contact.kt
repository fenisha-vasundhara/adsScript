package com.messenger.phone.number.text.sms.service.apps.modelClass

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contact")
data class Contact(
    var name: String,
    val number: String,
    val contactId: Int,
    val onlynumber: String,
    var CatName : String = "No",
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
)