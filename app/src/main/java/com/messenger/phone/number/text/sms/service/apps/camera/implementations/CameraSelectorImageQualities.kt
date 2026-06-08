package com.simplemobiletools.camera.models

import androidx.camera.core.CameraSelector
import com.messenger.phone.number.text.sms.service.apps.camera.models.MySize

data class CameraSelectorImageQualities(
    val camSelector: CameraSelector,
    val qualities: List<MySize>,
)
