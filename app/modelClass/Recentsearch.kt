package com.messenger.phone.number.text.sms.service.apps.modelClass

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Recentsearch(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val recentsearch: String
)