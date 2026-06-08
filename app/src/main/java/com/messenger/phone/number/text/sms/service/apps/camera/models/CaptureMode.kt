package com.messenger.phone.number.text.sms.service.apps.camera.models

import androidx.annotation.StringRes
import com.messenger.phone.number.text.sms.service.apps.R

enum class CaptureMode(@StringRes val stringResId: Int) {
    MINIMIZE_LATENCY(R.string.minimize_latency),
    MAXIMIZE_QUALITY(R.string.maximize_quality)
}
