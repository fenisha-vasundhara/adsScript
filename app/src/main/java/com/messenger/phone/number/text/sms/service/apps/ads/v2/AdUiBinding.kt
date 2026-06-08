package com.messenger.phone.number.text.sms.service.apps.ads.v2

import android.view.ViewGroup
import com.facebook.shimmer.ShimmerFrameLayout

data class AdUiBinding(
    val rootContainer: ViewGroup,
    val adFrame: ViewGroup,
    val shimmer: ShimmerFrameLayout? = null
)
