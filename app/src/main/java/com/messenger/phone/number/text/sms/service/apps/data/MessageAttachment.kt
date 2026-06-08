package com.messenger.phone.number.text.sms.service.apps.data

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "message_attachments")
data class MessageAttachment(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "text") var text: String,
    @ColumnInfo(name = "attachments") var attachments: ArrayList<Attachment>)
