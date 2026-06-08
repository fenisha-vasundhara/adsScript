package com.messenger.phone.number.text.sms.service.apps.camera.implementations

interface MyPreview {

    fun isInPhotoMode(): Boolean

    fun setFlashlightState(state: Int)

    fun toggleFrontBackCamera()

    fun handleFlashlightClick()

    fun tryTakePicture()

    fun toggleRecording()

    fun initPhotoMode()

    fun initVideoMode()

    fun showChangeResolution()
}
