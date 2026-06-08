package com.messenger.phone.number.text.sms.service.apps.data

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class ConversationNew(
    var threadId: Long,
    var snippet: String,
    var date: Int,
    var read: Boolean,
    var title: String,
    var photoUri: String,
    var isGroupConversation: Boolean,
    var phoneNumber: String,
    var isScheduled: Boolean = false,
    var usesCustomTitle: Boolean = false
) {

    companion object {
        fun areItemsTheSame(old: ConversationNew, new: ConversationNew): Boolean {
            return old.threadId == new.threadId
        }

        fun areContentsTheSame(old: ConversationNew, new: ConversationNew): Boolean {
            return old.snippet == new.snippet &&
                    old.date == new.date &&
                    old.read == new.read &&
                    old.title == new.title &&
                    old.photoUri == new.photoUri &&
                    old.isGroupConversation == new.isGroupConversation &&
                    old.phoneNumber == new.phoneNumber
        }
    }
}
