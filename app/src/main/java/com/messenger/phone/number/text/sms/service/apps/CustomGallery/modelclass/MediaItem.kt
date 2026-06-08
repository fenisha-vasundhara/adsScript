package com.messenger.phone.number.text.sms.service.apps.CustomGallery.modelclass

data class MediaItem(
    val id: Long?,
    val path: String,
    val position: Int,
    val selected: Boolean,
    val lastModifiedDate: Long,
    val isPhoto: Boolean
)