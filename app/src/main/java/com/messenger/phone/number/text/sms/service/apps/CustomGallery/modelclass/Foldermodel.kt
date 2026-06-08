package com.messenger.phone.number.text.sms.service.apps.CustomGallery.modelclass

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Foldermodel(
    @PrimaryKey(autoGenerate = true)
    var id: Int?,
    var foldername: String,
    var folderimgcount: String,
    var folderthumimage: String,
    var folderpath: String? = null
)

