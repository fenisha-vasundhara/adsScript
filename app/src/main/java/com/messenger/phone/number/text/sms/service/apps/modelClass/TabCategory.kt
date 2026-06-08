package com.messenger.phone.number.text.sms.service.apps.modelClass

import androidx.room.Entity
import androidx.room.PrimaryKey

data class TabCategory(
    val id: Int,
    val catName: String,
    val filterName: String,
    val isDefaultCategory: Boolean = false
)
