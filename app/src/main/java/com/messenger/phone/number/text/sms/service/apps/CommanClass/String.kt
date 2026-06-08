package com.messenger.phone.number.text.sms.service.apps.CommanClass

import android.util.Patterns
import java.text.Normalizer

fun String.getExtensionFromMimeType(): String {
    return when (lowercase()) {
        "image/png" -> ".png"
        "image/apng" -> ".apng"
        "image/webp" -> ".webp"
        "image/svg+xml" -> ".svg"
        "image/gif" -> ".gif"
        else -> ".jpg"
    }
}

// remove diacritics, for example č -> c
fun String.normalizeString() = Normalizer.normalize(this, Normalizer.Form.NFD).replace(com.simplemobiletools.commons.helpers.normalizeRegex, "")

fun String.isImageMimeType(): Boolean {
    return lowercase().startsWith("image")
}

fun String.isGifMimeType(): Boolean {
    return lowercase().endsWith("gif")
}

fun String.isVideoMimeType(): Boolean {
    return lowercase().startsWith("video")
}

fun String.isVCardMimeType(): Boolean {
    val lowercase = lowercase()
    return lowercase.endsWith("x-vcard") || lowercase.endsWith("vcard")
}

fun String.isAudioMimeType(): Boolean {
    return lowercase().startsWith("audio")
}

fun String.isCalendarMimeType(): Boolean {
    return lowercase().endsWith("calendar")
}

fun String.isPdfMimeType(): Boolean {
    return lowercase().endsWith("pdf")
}

fun String.isZipMimeType(): Boolean {
    return lowercase().endsWith("zip")
}

fun String.isPlainTextMimeType(): Boolean {
    return lowercase() == "text/plain"
}

fun String.isValidUrl(): Boolean = Patterns.WEB_URL.matcher(this).matches()

fun String.checkSenderIsValid(): Boolean {
    val body = this
    return if (body.contains("Mandate.kindly maintain sufficient Balance", true)
        || body.contains("sufficient Balance", true)
        || body.contains("sufficient", true)
        || body.contains("for upcoming mandate", true)
        || body.contains("loan account", true)
        || body.contains("loan amount", true)
    ) {
        false
    } else body.contains("received", true)
            || body.contains("withdrawn", true)
            || body.contains("Deposits", true)
            || body.contains("credited", true)
            || body.contains("Credited", true)
            || body.contains("debited", true)
            || body.contains("cash deposited", true)
            || body.contains("money transfer", true)
            || body.contains("transferred", true)
            || body.contains("below minimum limit", true)
            || body.contains("available balance", true)
}

fun String.isOfferMessage(): Boolean {
    val keywords = listOf("offer", "promo", "discount", "sale", "deal", "coupon", "exclusive")
    return keywords.any { keyword ->
        this.contains(keyword, ignoreCase = true)
    }
}

