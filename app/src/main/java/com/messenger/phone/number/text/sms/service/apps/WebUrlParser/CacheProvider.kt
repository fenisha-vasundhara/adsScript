package com.messenger.phone.number.text.sms.service.apps.WebUrlParser

interface CacheProvider {
    suspend fun getOpenGraphResult(url: String): OpenGraphResult?
    suspend fun setOpenGraphResult(openGraphResult: OpenGraphResult, url: String)
}
