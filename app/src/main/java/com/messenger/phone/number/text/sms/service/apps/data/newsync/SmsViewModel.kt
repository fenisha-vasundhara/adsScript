package com.messenger.phone.number.text.sms.service.apps.data.newsync

import androidx.lifecycle.ViewModel

class SmsViewModel(
    private val smsSyncManager: SmsSyncManager
) : ViewModel() {

    val isLoading = smsSyncManager.isMessageLoading
    val syncStatus = smsSyncManager.syncStatusText
    val insertedCount = smsSyncManager.lastInsertedCount

    fun onDefaultSmsAppEnabled() {
        smsSyncManager.onAppBecameDefault()
    }

    override fun onCleared() {
        super.onCleared()
        smsSyncManager.stopRealtimeObservers()
    }
}