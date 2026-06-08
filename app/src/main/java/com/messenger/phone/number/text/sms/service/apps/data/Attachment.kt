package com.messenger.phone.number.text.sms.service.apps.data

import android.net.Uri
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "attachments", indices = [(Index(value = ["message_id"], unique = true))])
data class Attachment(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @ColumnInfo(name = "message_id") var messageId: Long,
    @ColumnInfo(name = "uri_string") var uriString: String,
    @ColumnInfo(name = "mimetype") var mimetype: String,
    @ColumnInfo(name = "width") var width: Int,
    @ColumnInfo(name = "height") var height: Int,
    @ColumnInfo(name = "filename") var filename: String) {

    fun getUri() = Uri.parse(uriString)
}
