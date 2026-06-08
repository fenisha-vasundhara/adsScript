package com.messenger.phone.number.text.sms.service.apps.CommanClass

import android.graphics.Bitmap

fun Bitmap.CompressFormat.extension() = when (this) {
    Bitmap.CompressFormat.PNG -> "png"
    Bitmap.CompressFormat.WEBP -> "webp"
    else -> "jpg"
}
