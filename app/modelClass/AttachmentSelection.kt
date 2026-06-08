package com.messenger.phone.number.text.sms.service.apps.modelClass

import android.net.Uri
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ATTACHMENT_DOCUMENT
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ATTACHMENT_MEDIA
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ATTACHMENT_VCARD
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isImageMimeType
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isVCardMimeType
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isVideoMimeType

data class AttachmentSelection(
    val id: String,
    val uri: Uri,
    val mimetype: String,
    val filename: String,
    var isPending: Boolean,
    val viewType: Int = getViewTypeForMimeType(mimetype)
) {
    companion object {
        fun getViewTypeForMimeType(mimetype: String): Int {
            return when {
                mimetype.isImageMimeType() || mimetype.isVideoMimeType() -> ATTACHMENT_MEDIA
                mimetype.isVCardMimeType() -> ATTACHMENT_VCARD
                else -> ATTACHMENT_DOCUMENT
            }
        }

        fun areItemsTheSame(first: AttachmentSelection, second: AttachmentSelection): Boolean {
            return first.id == second.id
        }

        fun areContentsTheSame(first: AttachmentSelection, second: AttachmentSelection): Boolean {
            return first.uri == second.uri && first.mimetype == second.mimetype && first.filename == second.filename
        }
    }
}
