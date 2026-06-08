package com.messenger.phone.number.text.sms.service.apps.realmplan

//import io.realm.kotlin.types.RealmObject
//import io.realm.kotlin.types.annotations.Index
//import io.realm.kotlin.types.annotations.PrimaryKey
import io.github.xilinjia.krdb.types.RealmObject
import io.github.xilinjia.krdb.types.annotations.Index
import io.github.xilinjia.krdb.types.annotations.PrimaryKey
class ConversationRealm : RealmObject {
    @PrimaryKey
    var id: Int = 0

    var date: String = ""
    var read: Boolean = false
    var title: String = ""
    var photoUri: String? = null
    var usesCustomTitle: Boolean = false

    @Index
    var phoneNumber: String = ""

    var snippet: String = ""

    @Index
    var time: Long? = null

    var type: Int? = null
    var isnumaric: Boolean = false
    var messageStatus: String? = null
    var isnewmessage: Boolean? = false
    var newMessageCount: Int? = 0

    @Index
    var messageId: Long? = 0L

    @Index
    var threadId: Long? = 0L

    var isarchived: Boolean = false
    var ispinned: Boolean = false
    var pinneddate: Long? = 0L
    var isblocknumber: Boolean = false
    var isexpandmessageview: Boolean = true
    var isScheduled: Boolean = false
    var isMessagefound: Boolean = false
    var isPrivateChat: Boolean = false
    var draftmessage: String? = null
    var messagetype: String? = null
    var messageotp: String? = null
    var shownotification: Boolean = true
    var messagetraslateshow: Boolean = false
    var messagetraslationanimationshow: Boolean = false
    var isgroupmessage: Boolean = false
    var groupName: String? = null

    @Index
    var categoryName: String? = null

    var isnewmessagescroll: Boolean = false
    var isonlyselectedthem: Boolean = false
    var themenumber: Int? = 0
    var customtimeuri: String? = null
    var isbanneradshow: Boolean = false

    // Linked object — mirrors the Room shape. Not embedded so it can also be queried standalone.
    var messagewithattachment: MessageAttachmentRealm? = null
}
