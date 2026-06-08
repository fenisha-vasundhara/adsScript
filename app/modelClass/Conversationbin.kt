package com.messenger.phone.number.text.sms.service.apps.modelClass

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.messenger.phone.number.text.sms.service.apps.DataBase.Converters
import com.messenger.phone.number.text.sms.service.apps.data.Attachment
import com.messenger.phone.number.text.sms.service.apps.data.MessageAttachment
@Entity()
data class Conversationbin(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,//DONE
    var date: String? = null,//DONE
    var draftmessage: String? = null,//DONE
    var messagetype: String? = null,//DONE
    var messageotp: String? = null,//DONE
    var groupName: String? = null,//DONE
    var CategoryName: String? = null,//DONE
    var customtimeuri: String? = null,//DONE
    var phoneNumber: String? = null,//DONE
    var snippet: String? = null,//DONE
    var title: String? = null,//DONE
    var photoUri: String? = null,//DONE
    var messageStatus: String? = null,//DONE
    var isbanneradshow: Boolean = false,//DONE
    var isnewmessagescroll: Boolean = false,//DONE
    var isonlyselectedthem: Boolean = false,//DONE
    var shownotification: Boolean = true,//DONE
    var messagetraslateshow: Boolean = false,//DONE
    var messagetraslationanimationshow: Boolean = false,//DONE
    var isgroupmessage: Boolean = false,//DONE
    var isblocknumber: Boolean = false,//DONE
    var isexpandmessageview: Boolean = true,//DONE
    var is_scheduled: Boolean = false,//DONE
    var isMessagefound: Boolean = false,//DONE
    var isPrivateChat: Boolean = false,//DONE
    var isarchived: Boolean = false,//DONE
    var ispinned: Boolean = false,//DONE
    var isnewmessage: Boolean = false,//DONE
    var isnumaric: Boolean = false,//DONE
    var read: Boolean = false,//DONE
    var usesCustomTitle: Boolean = false,//DONE
    var messageId: Long? = 0L,//DONE
    val threadId: Long? = 0L,//DONE
    var time: Long? = 0L,//DONE
    var pinneddate: Long? = 0L,//DONE
    var type: Int? = 0,//DONE
    var newMessageCount: Int? = 0,//DONE
    var themenumber: Int? = 0,//DONE
    @TypeConverters(Converters::class)
    @ColumnInfo(name = "messagewithattachment")
    var messagewithattachment: MessageAttachment? = null,//DONE
)
