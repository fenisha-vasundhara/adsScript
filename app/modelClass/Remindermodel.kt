package com.messenger.phone.number.text.sms.service.apps.modelClass

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminder")
data class Remindermodel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var remindertitle: String? = null,  // Nullable, adjust as necessary
    var reminderstartdate: String? = null,  // Nullable, adjust as necessary
    var reminderenddate: String? = null,  // Nullable, adjust as necessary
    var selected: Boolean = false,  // Maps to INTEGER in the DB
)