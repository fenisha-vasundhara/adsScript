package com.messenger.phone.number.text.sms.service.apps.CustomGallery.modelclass

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Photo(
    @PrimaryKey(autoGenerate = true)
    val id: Int?=0,
    val path: String,
    val position: Int,
    var selected: Boolean,
    var lastModifieddate: Long
)