package com.messenger.phone.number.text.sms.service.apps.modelClass

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val catName: String,
    val filterName: String? = null,
    val isDefaultCategory: Boolean = false
)
