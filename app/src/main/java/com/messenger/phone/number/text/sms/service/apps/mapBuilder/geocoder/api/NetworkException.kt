package com.messenger.phone.number.text.sms.service.apps.mapBuilder.geocoder.api

class NetworkException : RuntimeException {
    constructor()

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)

    constructor(cause: Throwable) : super(cause)
}
