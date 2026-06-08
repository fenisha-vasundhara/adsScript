package com.messenger.phone.number.text.sms.service.apps.camera.helpers

import androidx.camera.video.Quality
import com.messenger.phone.number.text.sms.service.apps.camera.models.VideoQuality

fun Quality.toVideoQuality(): VideoQuality {
    return when (this) {
        Quality.UHD -> VideoQuality.UHD
        Quality.FHD -> VideoQuality.FHD
        Quality.HD -> VideoQuality.HD
        Quality.SD -> VideoQuality.SD
        else -> throw IllegalArgumentException("Unsupported quality: $this")
    }
}

fun VideoQuality.toCameraXQuality(): Quality {
    return when (this) {
        VideoQuality.UHD -> Quality.UHD
        VideoQuality.FHD -> Quality.FHD
        VideoQuality.HD -> Quality.HD
        VideoQuality.SD -> Quality.SD
    }
}
