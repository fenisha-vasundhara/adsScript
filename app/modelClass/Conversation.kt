package com.messenger.phone.number.text.sms.service.apps.modelClass
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.messenger.phone.number.text.sms.service.apps.DataBase.Converters
import com.messenger.phone.number.text.sms.service.apps.data.MessageAttachment

@Entity()
data class Conversation(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var date: String,
    var read: Boolean,
    var title: String,
    var photoUri: String?,
    var usesCustomTitle: Boolean = false,
    var phoneNumber: String,
    var snippet: String,
    var time: Long?,
    var type: Int?,
    var isnumaric: Boolean,
    var messageStatus: String?,
    var isnewmessage: Boolean? = false,
    var newMessageCount: Int? = 0,
    var messageId: Long? = 0L,
    val threadId: Long? = 0L,
    var isarchived: Boolean = false,
    var ispinned: Boolean = false,
    var pinneddate: Long? = 0L,
    var isblocknumber: Boolean = false,
    var isexpandmessageview: Boolean = true,
    var is_scheduled: Boolean = false,
    var isMessagefound: Boolean = false,
    var isPrivateChat: Boolean = false,
    var draftmessage: String? = null,
    var messagetype: String? = null,
    var messageotp: String? = null,
    var shownotification: Boolean = true,
    var messagetraslateshow: Boolean = false,
    var messagetraslationanimationshow: Boolean = false,
    var isgroupmessage: Boolean = false,
    var groupName: String? = null,
    var CategoryName: String? = null,
    var isnewmessagescroll: Boolean = false,
    var isonlyselectedthem: Boolean = false,
    var themenumber: Int? = 0,
    var customtimeuri: String? = null,
    var isbanneradshow: Boolean = false,
    @TypeConverters(Converters::class)
    @ColumnInfo(name = "messagewithattachment")
    var messagewithattachment: MessageAttachment? = null,
)
