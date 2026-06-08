package com.messenger.phone.number.text.sms.service.apps.Dialog

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.view.View
import com.google.android.material.color.MaterialColors
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getDialogBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperSecondaryTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor

data class DialogColors(
    val surface: Int,
    val onSurface: Int,
    val onSurfaceVariant: Int,
    val primary: Int,
    val outlineVariant: Int,
    val optionFill: Int
)

fun Context.resolveDialogColors(): DialogColors {
    val surface = getDialogBackgroundColor()
    val onSurface = getProperTextColor()
    val onSurfaceVariant = getProperSecondaryTextColor()
    val primary = getProperPrimaryColor()
    val outlineVariant = MaterialColors.layer(surface, onSurface, 0.12f)
    val optionFill = MaterialColors.layer(surface, onSurface, 0.08f)
    return DialogColors(surface, onSurface, onSurfaceVariant, primary, outlineVariant, optionFill)
}

fun View.applyDialogRipple(colors: DialogColors, alpha: Float = 0.12f) {
    val rippleColor = MaterialColors.layer(colors.surface, colors.onSurface, alpha)
    val content = background
    background = RippleDrawable(ColorStateList.valueOf(rippleColor), content, null)
}
