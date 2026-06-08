package com.messenger.phone.number.text.sms.service.apps.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.demo.adsmanage.Commen.Constants
import com.demo.adsmanage.Commen.log
import com.demo.adsmanage.basemodule.BaseSharedPreferences
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.messenger.phone.number.text.sms.service.apps.BlockNumberActivity
import com.messenger.phone.number.text.sms.service.apps.CommanClass.CAT_VAULE
import com.messenger.phone.number.text.sms.service.apps.CommanClass.baseConfig
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isfromnotification_New
import com.messenger.phone.number.text.sms.service.apps.CommanClass.notify_vaule_New
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.HomeABActivity
import com.messenger.phone.number.text.sms.service.apps.LockScreenSetupActivity
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.SendMessageActivity
import com.messenger.phone.number.text.sms.service.apps.SettingActivity
import com.messenger.phone.number.text.sms.service.apps.fragment.SettingFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.log

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        var notify_vaule: String? = null
        var notify_key: String? = null
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("TAG", "onNewToken: ====>$token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let {
            if (remoteMessage.data.isNotEmpty()) {
                val data = remoteMessage.data
                data.forEach { it ->
                    val key: String = it.key
                    notify_key = key
                    notify_vaule = it.value
                    CAT_VAULE = it.value
                    Log.d("TAG", "onMessageReceived:  $key Value: $notify_vaule")
                }
            } else {
                Log.d("TAG", "onMessageReceived:  2 ")
            }
            CoroutineScope(Dispatchers.IO).launch {
                sendNotification(it.title, it.body)
            }
        }
        notify_vaule_New = "setting"
        isfromnotification_New = true
    }

    suspend fun sendNotification(title: String?, messageBody: String?) {
        delay(10)
        Log.d(
            "534534543543",
            "sendNotification: <------------------> 111 notify_key ${notify_key} \n notify_vaule ${notify_vaule}"
        )
//        CoroutineScope(Dispatchers.Main).launch {
//            this@MyFirebaseMessagingService.toastMess("notify_key ${notify_key} \n notify_vaule ${notify_vaule}")
//        }
        "getCodeReffrence setting <-----------------> 111".log()

        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntent(
            Intent(
                this,  HomeABActivity::class.java
            )
        )
        Constants.isActivitychange = true

        if (notify_key == "MainActivity") {
            "getCodeReffrence setting <-----------------> 1112".log()
            stackBuilder.addNextIntent(
                Intent(
                    this,  HomeABActivity::class.java
                ).apply {
                    putExtra("isfromnotification", true)
                    putExtra("notify_vaule", notify_vaule)
                }
            )
        } else if (notify_key == "SettingActivity") {
            "getCodeReffrence setting <-----------------> 1113".log()
            stackBuilder.addNextIntent(
                Intent(this, SettingActivity::class.java).apply {
                    putExtra("isfromnotification", true)
                    putExtra("notify_vaule", notify_vaule)
                }
            )
        } else if (notify_key == "SecuritySetting") {
            "getCodeReffrence setting <-----------------> 1114".log()
            if (notify_vaule == "privatechat") {
                "getCodeReffrence setting <-----------------> 1115".log()
                Log.d("534534543543", "sendNotification: <------------------> 111")
                stackBuilder.addNextIntent(
                    Intent(this, LockScreenSetupActivity::class.java).apply {
                        putExtra("comefrom", 1)
                    }
                )
            } else if (notify_vaule == "twostepverification") {
                "getCodeReffrence setting <-----------------> 1116".log()
                stackBuilder.addNextIntent(
                    Intent(this, LockScreenSetupActivity::class.java).apply {
                        putExtra("comefrom", 2)
                    }
                )
            } else if (notify_vaule == "blockeduser") {
                "getCodeReffrence setting <-----------------> 1117".log()
                stackBuilder.addNextIntent(
                    Intent(this, BlockNumberActivity::class.java)
                )
            } else {
                "getCodeReffrence setting <-----------------> 1118".log()
            }
        }


        val contentPendingIntent =
            stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )


        val channelId = "default_channel_id"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setVibrate(
                longArrayOf(
                    1000, 1000, 1000,
                    1000, 1000
                )
            )
            .setSound(defaultSoundUri)
            .setOnlyAlertOnce(true)
            .setContentIntent(contentPendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Important Notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}