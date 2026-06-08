package com.messenger.phone.number.text.sms.service.apps.realmplan

import com.messenger.phone.number.text.sms.service.apps.data.Attachment
import com.messenger.phone.number.text.sms.service.apps.data.MessageAttachment
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversationbin

// ── ConversationRealm → Conversation ─────────────────────────────────────────

fun ConversationRealm.toConversation(): Conversation = Conversation(
    id                          = id,
    date                        = date,
    read                        = read,
    title                       = title,
    photoUri                    = photoUri,
    usesCustomTitle             = usesCustomTitle,
    phoneNumber                 = phoneNumber,
    snippet                     = snippet,
    time                        = time,
    type                        = type,
    isnumaric                   = isnumaric,
    messageStatus               = messageStatus,
    isnewmessage                = isnewmessage,
    newMessageCount             = newMessageCount,
    messageId                   = messageId,
    threadId                    = threadId,
    isarchived                  = isarchived,
    ispinned                    = ispinned,
    pinneddate                  = pinneddate,
    isblocknumber               = isblocknumber,
    isexpandmessageview         = isexpandmessageview,
    is_scheduled                = isScheduled,          // field name differs
    isMessagefound              = isMessagefound,
    isPrivateChat               = isPrivateChat,
    draftmessage                = draftmessage,
    messagetype                 = messagetype,
    messageotp                  = messageotp,
    shownotification            = shownotification,
    messagetraslateshow         = messagetraslateshow,
    messagetraslationanimationshow = messagetraslationanimationshow,
    isgroupmessage              = isgroupmessage,
    groupName                   = groupName,
    CategoryName                = categoryName,         // case differs
    isnewmessagescroll          = isnewmessagescroll,
    isonlyselectedthem          = isonlyselectedthem,
    themenumber                 = themenumber,
    customtimeuri               = customtimeuri,
    isbanneradshow              = isbanneradshow,
    messagewithattachment       = messagewithattachment?.toMessageAttachment(),
)

// Summary mapper for conversation-list screens.
// Keeps the "has attachment" signal without traversing attachment lists on the UI path.
fun ConversationRealm.toConversationSummary(): Conversation = Conversation(
    id                          = id,
    date                        = date,
    read                        = read,
    title                       = title,
    photoUri                    = photoUri,
    usesCustomTitle             = usesCustomTitle,
    phoneNumber                 = phoneNumber,
    snippet                     = snippet,
    time                        = time,
    type                        = type,
    isnumaric                   = isnumaric,
    messageStatus               = messageStatus,
    isnewmessage                = isnewmessage,
    newMessageCount             = newMessageCount,
    messageId                   = messageId,
    threadId                    = threadId,
    isarchived                  = isarchived,
    ispinned                    = ispinned,
    pinneddate                  = pinneddate,
    isblocknumber               = isblocknumber,
    isexpandmessageview         = isexpandmessageview,
    is_scheduled                = isScheduled,
    isMessagefound              = isMessagefound,
    isPrivateChat               = isPrivateChat,
    draftmessage                = draftmessage,
    messagetype                 = messagetype,
    messageotp                  = messageotp,
    shownotification            = shownotification,
    messagetraslateshow         = messagetraslateshow,
    messagetraslationanimationshow = messagetraslationanimationshow,
    isgroupmessage              = isgroupmessage,
    groupName                   = groupName,
    CategoryName                = categoryName,
    isnewmessagescroll          = isnewmessagescroll,
    isonlyselectedthem          = isonlyselectedthem,
    themenumber                 = themenumber,
    customtimeuri               = customtimeuri,
    isbanneradshow              = isbanneradshow,
    messagewithattachment       = messagewithattachment?.let {
        MessageAttachment(
            id = it.id,
            text = it.text,
            attachments = arrayListOf(),
        )
    },
)

// ── ConversationBinRealm → Conversationbin ───────────────────────────────────

fun ConversationBinRealm.toConversationBin(): Conversationbin = Conversationbin(
    id                          = id,
    date                        = date,
    draftmessage                = draftmessage,
    messagetype                 = messagetype,
    messageotp                  = messageotp,
    groupName                   = groupName,
    CategoryName                = categoryName,
    customtimeuri               = customtimeuri,
    phoneNumber                 = phoneNumber,
    snippet                     = snippet,
    title                       = title,
    photoUri                    = photoUri,
    messageStatus               = messageStatus,
    isbanneradshow              = isbanneradshow,
    isnewmessagescroll          = isnewmessagescroll,
    isonlyselectedthem          = isonlyselectedthem,
    shownotification            = shownotification,
    messagetraslateshow         = messagetraslateshow,
    messagetraslationanimationshow = messagetraslationanimationshow,
    isgroupmessage              = isgroupmessage,
    isblocknumber               = isblocknumber,
    isexpandmessageview         = isexpandmessageview,
    is_scheduled                = isScheduled,
    isMessagefound              = isMessagefound,
    isPrivateChat               = isPrivateChat,
    isarchived                  = isarchived,
    ispinned                    = ispinned,
    isnewmessage                = isnewmessage,
    isnumaric                   = isnumaric,
    read                        = read,
    usesCustomTitle             = usesCustomTitle,
    messageId                   = messageId,
    threadId                    = threadId,
    time                        = time,
    pinneddate                  = pinneddate,
    type                        = type,
    newMessageCount             = newMessageCount,
    themenumber                 = themenumber,
    messagewithattachment       = messagewithattachment?.toMessageAttachment(),
)

// ── MessageAttachmentRealm → MessageAttachment ───────────────────────────────

fun MessageAttachmentRealm.toMessageAttachment(): MessageAttachment = MessageAttachment(
    id          = id,
    text        = text,
    attachments = ArrayList(attachments.map { it.toAttachment() }),
)

fun AttachmentRealm.toAttachment(): Attachment = Attachment(
    id          = roomId,
    messageId   = messageId,
    uriString   = uriString,
    mimetype    = mimetype,
    width       = width,
    height      = height,
    filename    = filename,
)

// ── Conversation → ConversationRealm ─────────────────────────────────────────

fun Conversation.toConversationRealm(overrideId: Int = -1): ConversationRealm {
    val obj = ConversationRealm()
    obj.id = when {
        overrideId >= 0 -> overrideId
        id > 0 -> id
        else -> messageId?.toInt()?.takeIf { it > 0 }
            ?: (System.currentTimeMillis() and 0x7FFF_FFFFL).toInt()
    }
    obj.date = date
    obj.read = read
    obj.title = title
    obj.photoUri = photoUri
    obj.usesCustomTitle = usesCustomTitle
    obj.phoneNumber = phoneNumber
    obj.snippet = snippet
    obj.time = time
    obj.type = type
    obj.isnumaric = isnumaric
    obj.messageStatus = messageStatus
    obj.isnewmessage = isnewmessage
    obj.newMessageCount = newMessageCount
    obj.messageId = messageId
    obj.threadId = threadId
    obj.isarchived = isarchived
    obj.ispinned = ispinned
    obj.pinneddate = pinneddate
    obj.isblocknumber = isblocknumber
    obj.isexpandmessageview = isexpandmessageview
    obj.isScheduled = is_scheduled
    obj.isMessagefound = isMessagefound
    obj.isPrivateChat = isPrivateChat
    obj.draftmessage = draftmessage
    obj.messagetype = messagetype
    obj.messageotp = messageotp
    obj.shownotification = shownotification
    obj.messagetraslateshow = messagetraslateshow
    obj.messagetraslationanimationshow = messagetraslationanimationshow
    obj.isgroupmessage = isgroupmessage
    obj.groupName = groupName
    obj.categoryName = CategoryName
    obj.isnewmessagescroll = isnewmessagescroll
    obj.isonlyselectedthem = isonlyselectedthem
    obj.themenumber = themenumber
    obj.customtimeuri = customtimeuri
    obj.isbanneradshow = isbanneradshow
    return obj
}

// ── Conversationbin → ConversationBinRealm ───────────────────────────────────

fun Conversationbin.toConversationBinRealm(overrideId: Int = -1): ConversationBinRealm {
    val obj = ConversationBinRealm()
    obj.id = when {
        overrideId >= 0 -> overrideId
        id > 0 -> id
        else -> messageId?.toInt()?.takeIf { it > 0 }
            ?: (System.currentTimeMillis() and 0x7FFF_FFFFL).toInt()
    }
    obj.date = date
    obj.draftmessage = draftmessage
    obj.messagetype = messagetype
    obj.messageotp = messageotp
    obj.groupName = groupName
    obj.categoryName = CategoryName
    obj.customtimeuri = customtimeuri
    obj.phoneNumber = phoneNumber
    obj.snippet = snippet
    obj.title = title
    obj.photoUri = photoUri
    obj.messageStatus = messageStatus
    obj.isbanneradshow = isbanneradshow
    obj.isnewmessagescroll = isnewmessagescroll
    obj.isonlyselectedthem = isonlyselectedthem
    obj.shownotification = shownotification
    obj.messagetraslateshow = messagetraslateshow
    obj.messagetraslationanimationshow = messagetraslationanimationshow
    obj.isgroupmessage = isgroupmessage
    obj.isblocknumber = isblocknumber
    obj.isexpandmessageview = isexpandmessageview
    obj.isScheduled = is_scheduled
    obj.isMessagefound = isMessagefound
    obj.isPrivateChat = isPrivateChat
    obj.isarchived = isarchived
    obj.ispinned = ispinned
    obj.isnewmessage = isnewmessage
    obj.isnumaric = isnumaric
    obj.read = read
    obj.usesCustomTitle = usesCustomTitle
    obj.messageId = messageId
    obj.threadId = threadId
    obj.time = time
    obj.pinneddate = pinneddate
    obj.type = type
    obj.newMessageCount = newMessageCount
    obj.themenumber = themenumber
    return obj
}

// ── ConversationRealm → ConversationBinRealm (Realm-native move-to-bin) ──────

fun ConversationRealm.toBinRealm(): ConversationBinRealm {
    val obj = ConversationBinRealm()
    obj.id = id
    obj.date = date
    obj.draftmessage = draftmessage
    obj.messagetype = messagetype
    obj.messageotp = messageotp
    obj.groupName = groupName
    obj.categoryName = categoryName
    obj.customtimeuri = customtimeuri
    obj.phoneNumber = phoneNumber
    obj.snippet = snippet
    obj.title = title
    obj.photoUri = photoUri
    obj.messageStatus = messageStatus
    obj.isbanneradshow = isbanneradshow
    obj.isnewmessagescroll = isnewmessagescroll
    obj.isonlyselectedthem = isonlyselectedthem
    obj.shownotification = shownotification
    obj.messagetraslateshow = messagetraslateshow
    obj.messagetraslationanimationshow = messagetraslationanimationshow
    obj.isgroupmessage = isgroupmessage
    obj.isblocknumber = isblocknumber
    obj.isexpandmessageview = isexpandmessageview
    obj.isScheduled = isScheduled
    obj.isMessagefound = isMessagefound
    obj.isPrivateChat = isPrivateChat
    obj.isarchived = isarchived
    obj.ispinned = ispinned
    obj.isnewmessage = isnewmessage ?: false
    obj.isnumaric = isnumaric
    obj.read = read
    obj.usesCustomTitle = usesCustomTitle
    obj.messageId = messageId
    obj.threadId = threadId
    obj.time = time
    obj.pinneddate = pinneddate
    obj.type = type
    obj.newMessageCount = newMessageCount
    obj.themenumber = themenumber
    return obj
}

// ── Conversation → toConversationBin (for RecycleBin writes that still go Room) ──

fun Conversation.toConversationBin(): Conversationbin = Conversationbin(
    id                          = 0,
    date                        = date,
    draftmessage                = draftmessage,
    messagetype                 = messagetype,
    messageotp                  = messageotp,
    groupName                   = groupName,
    CategoryName                = CategoryName,
    customtimeuri               = customtimeuri,
    phoneNumber                 = phoneNumber,
    snippet                     = snippet,
    title                       = title,
    photoUri                    = photoUri,
    messageStatus               = messageStatus,
    isbanneradshow              = isbanneradshow,
    isnewmessagescroll          = isnewmessagescroll,
    isonlyselectedthem          = isonlyselectedthem,
    shownotification            = shownotification,
    messagetraslateshow         = messagetraslateshow,
    messagetraslationanimationshow = messagetraslationanimationshow,
    isgroupmessage              = isgroupmessage,
    isblocknumber               = isblocknumber,
    isexpandmessageview         = isexpandmessageview,
    is_scheduled                = is_scheduled,
    isMessagefound              = isMessagefound,
    isPrivateChat               = isPrivateChat,
    isarchived                  = isarchived,
    ispinned                    = ispinned,
    isnewmessage                = isnewmessage ?: false,
    isnumaric                   = isnumaric,
    read                        = read,
    usesCustomTitle             = usesCustomTitle,
    messageId                   = messageId,
    threadId                    = threadId,
    time                        = time,
    pinneddate                  = pinneddate,
    type                        = type,
    newMessageCount             = newMessageCount,
    themenumber                 = themenumber,
    messagewithattachment       = messagewithattachment,
)
