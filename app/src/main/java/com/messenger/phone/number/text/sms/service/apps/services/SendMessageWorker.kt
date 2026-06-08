package com.messenger.phone.number.text.sms.service.apps.services

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isTaskExecuted
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess

class SendMessageWorker(var context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        if (!isTaskExecuted) {
//            context.toastMess("Task executed!")
            context.config.issubbannershow = true
            isTaskExecuted = true
        }
        return Result.success()
    }
}