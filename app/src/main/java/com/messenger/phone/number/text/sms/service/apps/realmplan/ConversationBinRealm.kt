package com.messenger.phone.number.text.sms.service.apps.realmplan

//import io.realm.kotlin.types.RealmObject
//import io.realm.kotlin.types.annotations.Index
//import io.realm.kotlin.types.annotations.PrimaryKey
import io.github.xilinjia.krdb.types.RealmObject
import io.github.xilinjia.krdb.types.annotations.Index
import io.github.xilinjia.krdb.types.annotations.PrimaryKey

class ConversationBinRealm : RealmObject {
    @PrimaryKey
    var id: Int = 0

    var date: String? = null
    var draftmessage: String? = null
    var messagetype: String? = null
    var messageotp: String? = null
    var groupName: String? = null

    @Index
    var categoryName: String? = null

    var customtimeuri: String? = null

    @Index
    var phoneNumber: String? = null

    var snippet: String? = null
    var title: String? = null
    var photoUri: String? = null
    var messageStatus: String? = null
    var isbanneradshow: Boolean = false
    var isnewmessagescroll: Boolean = false
    var isonlyselectedthem: Boolean = false
    var shownotification: Boolean = true
    var messagetraslateshow: Boolean = false
    var messagetraslationanimationshow: Boolean = false
    var isgroupmessage: Boolean = false
    var isblocknumber: Boolean = false
    var isexpandmessageview: Boolean = true
    var isScheduled: Boolean = false
    var isMessagefound: Boolean = false
    var isPrivateChat: Boolean = false
    var isarchived: Boolean = false
    var ispinned: Boolean = false
    var isnewmessage: Boolean = false
    var isnumaric: Boolean = false
    var read: Boolean = false
    var usesCustomTitle: Boolean = false

    @Index
    var messageId: Long? = 0L

    @Index
    var threadId: Long? = 0L

    @Index
    var time: Long? = 0L

    var pinneddate: Long? = 0L
    var type: Int? = 0
    var newMessageCount: Int? = 0
    var themenumber: Int? = 0
    var messagewithattachment: MessageAttachmentRealm? = null
}
