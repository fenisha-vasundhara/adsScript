package com.messenger.phone.number.text.sms.service.apps.WebUrlParser

interface OpenGraphCallback {
    fun onPostResponse(openGraphResult: OpenGraphResult)
    fun onError(error: String)
}