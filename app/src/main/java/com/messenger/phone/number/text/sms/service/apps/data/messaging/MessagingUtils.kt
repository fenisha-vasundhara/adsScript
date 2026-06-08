package com.messenger.phone.number.text.sms.service.apps.data.messaging

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.provider.Telephony.Sms
import android.telephony.SmsMessage
import android.util.Log
import com.demo.adsmanage.helper.logD
import com.klinker.android.send_message.Message
import com.klinker.android.send_message.Settings
import com.klinker.android.send_message.Transaction
import com.messenger.phone.number.text.sms.service.apps.CommanClass.FILE_SIZE_NONE
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getThreadId
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getFileSizeFromUri
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isPlainTextMimeType
import com.messenger.phone.number.text.sms.service.apps.CommanClass.showErrorToastMess
import com.messenger.phone.number.text.sms.service.apps.CommanClass.smsSender
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.data.Attachment
import com.messenger.phone.number.text.sms.service.apps.data.messaging.SmsException.Companion.ERROR_PERSISTING_MESSAGE
import com.simplemobiletools.commons.extensions.showErrorToast
import java.io.ByteArrayOutputStream
import kotlin.io.DEFAULT_BUFFER_SIZE
import kotlin.math.min

class MessagingUtils(val context: Context) {

    private var messageUri: Uri? = null

    /**
     * Insert an SMS to the given URI with thread_id specified.
     */

    private fun insertSmsMessage2(
        subId: Int, dest: String, text: String, timestamp: Long, threadId: Long,
        status: Int = Sms.STATUS_NONE, type: Int = Sms.MESSAGE_TYPE_OUTBOX, messageId: Long? = null
    ): Uri {
        val response: Uri?
        val values = ContentValues().apply {
            put(Sms.ADDRESS, dest)
            put(Sms.DATE, timestamp)
            put(Sms.READ, 1)
            put(Sms.SEEN, 1)
            put(Sms.BODY, text)

            // insert subscription id only if it is a valid one.
            if (subId != Settings.DEFAULT_SUBSCRIPTION_ID) {
                put(Sms.SUBSCRIPTION_ID, subId)
            }

            if (status != Sms.STATUS_NONE) {
                put(Sms.STATUS, status)
            }
            if (type != Sms.MESSAGE_TYPE_ALL) {
                put(Sms.TYPE, type)
            }
            if (threadId != -1L) {
                put(Sms.THREAD_ID, threadId)
            }
        }

        try {
            if (messageId != null) {
                val selection = "${Sms._ID} = ?"
                val selectionArgs = arrayOf(messageId.toString())
                val count = context.contentResolver.update(Sms.CONTENT_URI, values, selection, selectionArgs)
                if (count > 0) {
                    response = Uri.parse("${Sms.CONTENT_URI}/${messageId}")
                } else {
                    response = null
                }
            } else {
                response = context.contentResolver.insert(Sms.CONTENT_URI, values)
            }
        } catch (e: Exception) {
            throw SmsException(ERROR_PERSISTING_MESSAGE, e)
        }
        return response ?: throw SmsException(ERROR_PERSISTING_MESSAGE)
    }

    private fun insertOrUpdateSmsMessage(
        messageId: Long,
        subId: Int,
        dest: String,
        text: String,
        timestamp: Long,
        threadId: Long,
        status: Int = Sms.STATUS_NONE,
        type: Int = Sms.MESSAGE_TYPE_OUTBOX
    ): Uri {
        val values = ContentValues().apply {
            put(Sms.ADDRESS, dest)
            put(Sms.DATE, timestamp)
            put(Sms.READ, 1)
            put(Sms.SEEN, 1)
            put(Sms.BODY, text)

            // insert subscription id only if it is a valid one.
            if (subId != Settings.DEFAULT_SUBSCRIPTION_ID) {
                put(Sms.SUBSCRIPTION_ID, subId)
            }

            if (status != Sms.STATUS_NONE) {
                put(Sms.STATUS, status)
            }
            if (type != Sms.MESSAGE_TYPE_ALL) {
                put(Sms.TYPE, type)
            }
            if (threadId != -1L) {
                put(Sms.THREAD_ID, threadId)
            }
        }

        val uri: Uri = ContentUris.withAppendedId(Sms.CONTENT_URI, messageId)

        try {
            val rowsAffected = context.contentResolver.update(uri, values, null, null)
            if (rowsAffected == 0) {
                throw SmsException(ERROR_PERSISTING_MESSAGE)
            }
        } catch (e: Exception) {
            throw SmsException(ERROR_PERSISTING_MESSAGE, e)
        }

        return uri
    }

    @SuppressLint("Range")
    private fun resendSmsMessage(
        subId: Int, dest: String, text: String, timestamp: Long, threadId: Long,
        status: Int = Sms.STATUS_NONE, type: Int = Sms.MESSAGE_TYPE_OUTBOX
    ): Uri {
        val values = ContentValues().apply {
            put(Sms.ADDRESS, dest)
            put(Sms.DATE, timestamp)
            put(Sms.READ, 1)
            put(Sms.SEEN, 1)
            put(Sms.BODY, text)

            // insert subscription id only if it is a valid one.
            if (subId != Settings.DEFAULT_SUBSCRIPTION_ID) {
                put(Sms.SUBSCRIPTION_ID, subId)
            }

            if (status != Sms.STATUS_NONE) {
                put(Sms.STATUS, status)
            }
            if (type != Sms.MESSAGE_TYPE_ALL) {
                put(Sms.TYPE, type)
            }
            if (threadId != -1L) {
                put(Sms.THREAD_ID, threadId)
            }
        }

        return try {
            val selection = "${Sms.ADDRESS} = ? AND ${Sms.BODY} = ? AND ${Sms.THREAD_ID} = ?"
            val selectionArgs = arrayOf(dest, text, threadId.toString())
            val cursor = context.contentResolver.query(
                Sms.CONTENT_URI,
                arrayOf(Sms._ID),
                selection,
                selectionArgs,
                null
            )
            cursor?.use { c ->
                if (c.moveToFirst()) {
                    // Message already exists, update its status and timestamp
                    val id = c.getLong(c.getColumnIndex(Sms._ID))
                    val updateUri = Uri.withAppendedPath(Sms.CONTENT_URI, id.toString())
                    context.contentResolver.update(updateUri, values, null, null)
                    updateUri
                } else {
                    // Message doesn't exist, insert a new one
                    context.contentResolver.insert(Sms.CONTENT_URI, values) ?: throw SmsException(ERROR_PERSISTING_MESSAGE)
                }
            } ?: throw SmsException(ERROR_PERSISTING_MESSAGE)
        } catch (e: Exception) {
            throw SmsException(ERROR_PERSISTING_MESSAGE, e)
        }
    }

    private fun insertSmsMessage(
        subId: Int, dest: String, text: String, timestamp: Long, threadId: Long,
        status: Int = Sms.STATUS_NONE, type: Int = Sms.MESSAGE_TYPE_OUTBOX
    ): Uri {
        val response: Uri?
        val values = ContentValues().apply {
            put(Sms.ADDRESS, dest)
            put(Sms.DATE, timestamp)
            put(Sms.READ, 1)
            put(Sms.SEEN, 1)
            put(Sms.BODY, text)

            // insert subscription id only if it is a valid one.
            if (subId != Settings.DEFAULT_SUBSCRIPTION_ID) {
                put(Sms.SUBSCRIPTION_ID, subId)
            }

            if (status != Sms.STATUS_NONE) {
                put(Sms.STATUS, status)
            }
            if (type != Sms.MESSAGE_TYPE_ALL) {
                put(Sms.TYPE, type)
            }
            if (threadId != -1L) {
                put(Sms.THREAD_ID, threadId)
            }
        }

        try {
            response = context.contentResolver.insert(Sms.CONTENT_URI, values)
        } catch (e: Exception) {
            logD("insertSmsMessageJigar:"," error <----------> 89989 ->$e")
            throw SmsException(ERROR_PERSISTING_MESSAGE, e)
        }
        return response ?: throw SmsException(ERROR_PERSISTING_MESSAGE)
    }

    /** Send an SMS message given [text] and [addresses]. A [SmsException] is thrown in case any errors occur. */

    fun sendSmsMessage2(
        text: String, addresses: Set<String>, subId: Int, requireDeliveryReport: Boolean, messageId: Long? = null
    ) {
//        if (addresses.size > 1) {
//            // insert a dummy message for this thread if it is a group message
//            val broadCastThreadId = context.getThreadId(addresses.toSet())
//            val mergedAddresses = addresses.joinToString(ADDRESS_SEPARATOR)
//            insertSmsMessage(
//                subId = subId, dest = mergedAddresses, text = text,
//                timestamp = System.currentTimeMillis(), threadId = broadCastThreadId,
//                status = Sms.Sent.STATUS_COMPLETE, type = Sms.Sent.MESSAGE_TYPE_SENT,
//                messageId = messageId
//            )
//        }

        for (address in addresses) {
            val threadId = context.getThreadId(address)
            val messageUri = insertSmsMessage2(
                subId = subId, dest = address, text = text,
                timestamp = System.currentTimeMillis(), threadId = threadId,
                messageId = messageId
            )
            try {
                context.smsSender.sendMessage2(
                    subId = subId, destination = address, body = text, serviceCenter = null,
                    requireDeliveryReport = requireDeliveryReport, messageUri = messageUri
                )
            } catch (e: Exception) {
                updateSmsMessageSendingStatus(messageUri, Sms.Outbox.MESSAGE_TYPE_FAILED)
                throw e // propagate error to caller
            }
        }
    }

    suspend fun sendSmsMessage(
        text: String, addresses: Set<String>, subId: Int, requireDeliveryReport: Boolean, isgroupmessage: Boolean, isfaildmessage: Boolean,
        messageid: Long = 0L,
        messagetime: Long = 0L
    ) {
        Log.d("sendSMSManager", "setMessage:sendSMSManager <------------> isfaildmessage  4 ${addresses}")
        Log.d("hello", "sendMessageCompat: <------------------------> 3 ")
        for (address in addresses) {
            val threadId = context.getThreadId(address)
            Log.d("sendSMSManager", "setMessage:sendSMSManager <------------> isfaildmessage  55 ${isfaildmessage}")
            if (!isfaildmessage) {
                Log.d("sendSMSManager", "setMessage:sendSMSManager <------------> isfaildmessage  5")
                messageUri = null
                messageUri = insertSmsMessage(
                    subId = subId, dest = address, text = text,
                    timestamp = System.currentTimeMillis(), threadId = threadId
                )
                Log.d("sendSMSManager", "setMessage:sendSMSManager <------------> isfaildmessage  555  ${messageUri}")
                Log.d("sendSMSManager", "setMessage:sendSMSManager <------------> isfaildmessage  5555  ${subId}")
                Log.d("messageUri", "sendSmsMessage: messageUri <----------> ${messageUri}")

                try {
                    context.smsSender.sendMessage(
                        subId = subId, destination = address, body = text, serviceCenter = null,
                        requireDeliveryReport = requireDeliveryReport, messageUri = messageUri!!, isgroupmessage,
                        isfaildmessage = isfaildmessage,
                    )
                } catch (e: Exception) {
                    updateSmsMessageSendingStatus(messageUri, Sms.Outbox.MESSAGE_TYPE_FAILED)
                    throw e // propagate error to caller
                }

            } else {
//                Log.d("messageUri", "sendSmsMessage: messageUri 1 <----------> ${messageUri}")

//                messageUri = context.getMessageUriFromId(messageid)!!
                messageUri = null
//                messageUri = Uri.parse("content://sms/" + messageid)
                messageUri = resendSmsMessage(
                    subId = subId, dest = address, text = text,
                    timestamp = messagetime, threadId = threadId
                )
                Log.d("messageUri", "sendSmsMessage: messageUri 1 <----------> ${messageUri}")
                Log.d("sendSMSManager", "setMessage:sendSMSManager <------------> isfaildmessage  6 ${messageUri}")
                try {
                    context.smsSender.sendMessage(
                        subId = subId, destination = address, body = text, serviceCenter = null,
                        requireDeliveryReport = requireDeliveryReport, messageUri = messageUri!!, isgroupmessage,
                        messageid,
                        isfaildmessage = isfaildmessage,
                    )
                } catch (e: Exception) {
                    updateSmsMessageSendingStatus(messageUri, Sms.Outbox.MESSAGE_TYPE_FAILED)
                    throw e // propagate error to caller
                }
            }
        }
    }

    fun updateSmsMessageSendingStatus(messageUri: Uri?, type: Int) {
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(Sms.Outbox.TYPE, type)
        }

        try {
            if (messageUri != null) {
                resolver.update(messageUri, values, null, null)
            } else {
                val cursor = resolver.query(Sms.Outbox.CONTENT_URI, null, null, null, null)
                cursor?.use {
                    if (cursor.moveToFirst()) {
                        @SuppressLint("Range")
                        val id = cursor.getString(cursor.getColumnIndex(Sms.Outbox._ID))
                        val selection = "${Sms._ID} = ?"
                        val selectionArgs = arrayOf(id.toString())
                        resolver.update(Sms.Outbox.CONTENT_URI, values, selection, selectionArgs)
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("4324234", "sendMessageCompat: error <----------> 1 " + e.localizedMessage)
            context.showErrorToastMess(e)
        }
    }

    fun getSmsMessageFromDeliveryReport(intent: Intent): SmsMessage? {
        val pdu = intent.getByteArrayExtra("pdu")
        val format = intent.getStringExtra("format")
        return SmsMessage.createFromPdu(pdu, format)
    }

    @Deprecated("TODO: Move/rewrite MMS code into the app.")
    fun sendMmsMessage(text: String, addresses: List<String>, attachment: Attachment?, settings: Settings) {
        val transaction = Transaction(context, settings)
        val message = Message(text, addresses.toTypedArray())
        if (attachment != null) {
            val payload = loadAttachmentPayload(attachment) ?: return
            message.addMedia(payload.bytes, payload.mimeType, payload.name, payload.name)
        }
        val mmsSentIntent = Intent(context,MmsSentReceiver::class.java)
        transaction.setExplicitBroadcastForSentMms(mmsSentIntent)
        try {
            transaction.sendNewMessage(message)
        } catch (e: Exception) {
            context.showErrorToastMess(e)
        }
    }

    @Deprecated("TODO: Move/rewrite MMS code into the app.")
    fun sendMmsMessage2(text: String, addresses: List<String>, attachment: Attachment?, settings: Settings, messageId: Long? = null) {
        val transaction = Transaction(context, settings)
        val message = Message(text, addresses.toTypedArray())

        if (attachment != null) {
            val payload = loadAttachmentPayload(attachment) ?: return
            message.addMedia(payload.bytes, payload.mimeType, payload.name, payload.name)
        }

        val mmsSentIntent = Intent(context, MmsSentReceiver::class.java)
        mmsSentIntent.putExtra("original_message_id", messageId)
        transaction.setExplicitBroadcastForSentMms(mmsSentIntent)

        try {
            transaction.sendNewMessage(message)
        } catch (e: Exception) {
            context.showErrorToast(e)
        }
    }

    private fun loadAttachmentPayload(attachment: Attachment): AttachmentPayload? {
        val uri = attachment.getUri()
        val sizeLimit = getAttachmentSizeLimit()
        val fileSize = context.getFileSizeFromUri(uri)
        if (fileSize != FILE_SIZE_NONE && fileSize > sizeLimit) {
            showAttachmentTooLargeWarning()
            return null
        }

        var blockedByLimit = false
        val bytes = try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val initialCapacity = if (fileSize in 1 until sizeLimit && fileSize <= Int.MAX_VALUE) {
                    fileSize.toInt()
                } else {
                    DEFAULT_BUFFER_SIZE
                }
                val outputStream = ByteArrayOutputStream(initialCapacity)
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                var totalRead = 0L
                while (true) {
                    val read = inputStream.read(buffer)
                    if (read == -1) {
                        break
                    }
                    totalRead += read
                    if (totalRead > sizeLimit) {
                        blockedByLimit = true
                        break
                    }
                    outputStream.write(buffer, 0, read)
                }
                if (blockedByLimit) {
                    null
                } else {
                    outputStream.toByteArray()
                }
            }
        } catch (oom: OutOfMemoryError) {
            blockedByLimit = true
            null
        } catch (e: Exception) {
            context.showErrorToastMess(e)
            null
        }

        if (blockedByLimit) {
            showAttachmentTooLargeWarning()
            return null
        }

        if (bytes == null) {
            context.showErrorToastMess(context.getString(R.string.Something_went_wrong_new))
            return null
        }

        val mimeType = if (attachment.mimetype.isPlainTextMimeType()) {
            "application/txt"
        } else {
            attachment.mimetype
        }
        val name = attachment.filename
        return AttachmentPayload(bytes, mimeType, name)
    }

    private fun showAttachmentTooLargeWarning() {
        context.showErrorToastMess(context.getString(R.string.attachment_sized_exceeds_max_limit))
    }

    private fun getAttachmentSizeLimit(): Long {
        val configuredLimit = context.config.mmsFileSizeLimit
        val hardLimit = MAX_IN_MEMORY_ATTACHMENT_BYTES
        return if (configuredLimit != FILE_SIZE_NONE) {
            min(configuredLimit, hardLimit)
        } else {
            hardLimit
        }
    }

    private data class AttachmentPayload(val bytes: ByteArray, val mimeType: String, val name: String)

    fun maybeShowErrorToast(resultCode: Int, errorCode: Int) {
        if (resultCode != Activity.RESULT_OK) {
//            val msg = if (errorCode != SendStatusReceiver.NO_ERROR_CODE) {
//                context.resources.getString(R.string.send_special_characters)
//            } else {
//                when (resultCode) {
//                    SmsManager.RESULT_ERROR_NO_SERVICE -> context.getString(R.string.error_service_is_unavailable)
//                    SmsManager.RESULT_ERROR_RADIO_OFF -> context.getString(R.string.error_radio_turned_off)
//                    SmsManager.RESULT_NO_DEFAULT_SMS_APP -> context.getString(R.string.sim_card_not_available)
//                    else -> context.resources.getString(R.string.Something_went_wrong_new)
//                }
//            }
//            context.toastMess(msg = msg, length = Toast.LENGTH_LONG)
        } else {
            // no-op
        }
    }

    companion object {
        const val ADDRESS_SEPARATOR = "|"
        // Cap attachments we read into memory; Transaction still copies the data so keep it modest.
        private const val MAX_IN_MEMORY_ATTACHMENT_BYTES = 10L * 1024 * 1024
    }
}
