package com.messenger.phone.number.text.sms.service.apps.CommanClass

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.TypefaceSpan
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnStart
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible

fun View.showWithAnimation(duration: Long = 250L) {
    if (!isVisible) {
        ObjectAnimator.ofFloat(
            this, "alpha", 0f, 1f
        ).apply {
            this.duration = duration
            doOnStart { visibility = View.VISIBLE }
        }.start()
    }
}
fun View.startPulseAnimation(
    scaleFrom: Float = 1f,
    scaleTo: Float = 1.1f,
    duration: Long = 1400
) {
    val scaleX = ObjectAnimator.ofFloat(this, View.SCALE_X, scaleFrom, scaleTo)
    val scaleY = ObjectAnimator.ofFloat(this, View.SCALE_Y, scaleFrom, scaleTo)

    scaleX.repeatCount = ValueAnimator.INFINITE
    scaleY.repeatCount = ValueAnimator.INFINITE

    scaleX.repeatMode = ValueAnimator.REVERSE
    scaleY.repeatMode = ValueAnimator.REVERSE

    scaleX.duration = duration
    scaleY.duration = duration

    val interpolator = AccelerateDecelerateInterpolator()
    scaleX.interpolator = interpolator
    scaleY.interpolator = interpolator

    scaleX.start()
    scaleY.start()
}

fun applyFontToMenuItem(context: Context, menuItem: MenuItem, fontRes: Int) {
    val typeface = ResourcesCompat.getFont(context, fontRes)
    val spannable = SpannableString(menuItem.title)

    spannable.setSpan(
        object : TypefaceSpan("") {
            override fun updateDrawState(ds: TextPaint) {
                ds.typeface = typeface
            }

            override fun updateMeasureState(paint: TextPaint) {
                paint.typeface = typeface
            }
        },
        0,
        spannable.length,
        Spannable.SPAN_INCLUSIVE_INCLUSIVE
    )

    menuItem.title = spannable
}

