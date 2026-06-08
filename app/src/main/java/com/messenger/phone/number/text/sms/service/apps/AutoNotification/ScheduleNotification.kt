package com.messenger.phone.number.text.sms.service.apps.AutoNotification

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object ScheduleNotification {

//    fun scheduleNotification(context: Context, delay: Long, data: Data) {
//
//        val notificationWork = OneTimeWorkRequest.Builder(NotificationWork::class.java)
//            .setInitialDelay(delay, TimeUnit.MILLISECONDS).setInputData(data).build()
//
//        val instanceWorkManager = WorkManager.getInstance(context)
//        instanceWorkManager.beginUniqueWork(
//            NotificationWork.NOTIFICATION_WORK,
//            ExistingWorkPolicy.REPLACE,
//            notificationWork
//        ).enqueue()
//    }
}