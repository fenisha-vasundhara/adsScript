package com.messenger.phone.number.text.sms.service.apps.DataBase

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.messenger.phone.number.text.sms.service.apps.data.Attachment
import com.messenger.phone.number.text.sms.service.apps.data.MessageAttachment
import com.messenger.phone.number.text.sms.service.apps.data.SimpleContact

class Converters {

    private val gson = Gson()
    private val attachmentType = object : TypeToken<List<Attachment>>() {}.type
    private val simpleContactType = object : TypeToken<List<SimpleContact>>() {}.type
    private val messageAttachmentType = object : TypeToken<MessageAttachment?>() {}.type
    private val maxMessageAttachmentJsonChars = 1_000_000

    @TypeConverter
    fun jsonToAttachmentList(value: String?): ArrayList<Attachment>? {
        return value?.let { gson.fromJson<ArrayList<Attachment>>(value, attachmentType) }

    }

    @TypeConverter
    fun attachmentListToJson(list: ArrayList<Attachment>?): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    fun jsonToSimpleContactList(value: String?): ArrayList<SimpleContact>? {
        return value?.let {
             gson.fromJson<ArrayList<SimpleContact>>(value, simpleContactType)
        }
    }

    @TypeConverter
    fun simpleContactListToJson(list: ArrayList<SimpleContact>?): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    fun jsonToMessageAttachment(value: String?): MessageAttachment? {
        if (value.isNullOrEmpty()) return null
        if (value.length > maxMessageAttachmentJsonChars) return null
        return try {
            gson.fromJson<MessageAttachment>(value, messageAttachmentType)
        } catch (_: OutOfMemoryError) {
            null
        } catch (_: Exception) {
            null
        }
    }

    @TypeConverter
    fun messageAttachmentToJson(messageAttachment: MessageAttachment?): String? {
        if (messageAttachment == null) return null
        val json = gson.toJson(messageAttachment)
        return if (json.length > maxMessageAttachmentJsonChars) null else json
    }

}
