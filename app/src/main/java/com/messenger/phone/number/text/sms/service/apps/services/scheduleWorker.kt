package com.messenger.phone.number.text.sms.service.apps.services

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

fun scheduleWorker(context: Context) {
    val myWork = PeriodicWorkRequest.Builder(
        SendMessageWorker::class.java,
        5,
        TimeUnit.DAYS
    ).build()
    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(
            "SendMessageWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            myWork
        )
}
