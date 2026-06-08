package com.messenger.phone.number.text.sms.service.apps.modelClass

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "starnumber")
data class StarNumber(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var date: String,
    var read: Boolean,
    var title: String,
    var photoUri: String?,
    var usesCustomTitle: Boolean = false,
    var phoneNumber: String,
    var snippet: String,
    var time: Long?,
    var type: Int?,
    var isnumaric: Boolean
)
