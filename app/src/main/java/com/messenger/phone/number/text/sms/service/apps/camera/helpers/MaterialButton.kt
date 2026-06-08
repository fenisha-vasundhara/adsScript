package com.messenger.phone.number.text.sms.service.apps.camera.helpers

import androidx.annotation.DrawableRes
import com.google.android.material.button.MaterialButton
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.camera.views.ShadowDrawable

fun MaterialButton.setShadowIcon(@DrawableRes drawableResId: Int) {
    icon = ShadowDrawable(context, drawableResId, R.style.TopIconShadow)
}
