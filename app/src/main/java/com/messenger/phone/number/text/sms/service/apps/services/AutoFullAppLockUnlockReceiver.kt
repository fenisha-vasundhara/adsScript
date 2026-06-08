package com.messenger.phone.number.text.sms.service.apps.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.demo.adsmanage.basemodule.BaseSharedPreferences
import com.messenger.phone.number.text.sms.service.apps.CommanClass.cancelAutoFullAppLockPendingIntent
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createFile
import com.messenger.phone.number.text.sms.service.apps.CommanClass.deleteAllSms
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.DI.RefreshAllData
import com.messenger.phone.number.text.sms.service.apps.LockScreenPinSetActivity
import com.messenger.phone.number.text.sms.service.apps.Notification.notificationProvider
import com.messenger.phone.number.text.sms.service.apps.sms.SmsUtils.deleteSMS
import com.simplemobiletools.commons.extensions.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class AutoFullAppLockUnlockReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationProvider: notificationProvider

    override fun onReceive(context: Context, intent: Intent) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("intervalMillis", "AutoFullAppLockRemove onFinish:<---------> 2")
            if ((BaseSharedPreferences(context).mIS_SUBSCRIBED == false)) {
                context.cancelAutoFullAppLockPendingIntent()
                context.createFile()
                context.config.Full_AppLock_Sec_Question = "Select any Security Question"

                context.config.Full_AppLock_Pin = "Not Set"
                context.config.Full_AppLock_Sec_Question_Ans = "Select any Security Question"
                notificationProvider.sendfullapplocknotification(987654)
            }
        }
    }
}
