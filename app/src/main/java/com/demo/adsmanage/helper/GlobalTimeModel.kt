package com.demo.adsmanage.helper

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class GlobalTimeModel(
    var shouldStartTimer: Boolean = true,
    var timerMinutes: Int = 9,
    var oneTime: Boolean = true
)