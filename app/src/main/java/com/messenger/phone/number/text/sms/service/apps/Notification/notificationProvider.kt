package com.messenger.phone.number.text.sms.service.apps.Notification

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.startActivity
import com.demo.adsmanage.Activity.PayAllSubsciptionExterimentActivity
import com.demo.adsmanage.Activity.SubActivityTwoplanActivity
import com.demo.adsmanage.Activity.TimerOfferActivity
import com.demo.adsmanage.Commen.Constants
import com.demo.adsmanage.basemodule.BaseSharedPreferences
import com.messenger.phone.number.text.sms.service.apps.BekupActivity
import com.messenger.phone.number.text.sms.service.apps.BlockNumberActivity
import com.messenger.phone.number.text.sms.service.apps.CommanClass.baseConfig
import com.messenger.phone.number.text.sms.service.apps.CommanClass.cancelAutoNotificationPendingIntent
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.messageNotify
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.HomeABActivity
import com.messenger.phone.number.text.sms.service.apps.LockScreenActivity
import com.messenger.phone.number.text.sms.service.apps.LockScreenSetupActivity
import com.messenger.phone.number.text.sms.service.apps.ManageBlockedKeywordsActivity
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.Schedule_Message_Show_Activity
import com.messenger.phone.number.text.sms.service.apps.SendMessageActivity
import com.messenger.phone.number.text.sms.service.apps.SettingActivity
import com.messenger.phone.number.text.sms.service.apps.data.SimpleContactsHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


class notificationProvider @Inject constructor(@ApplicationContext var context: Context) {

    val NOTIFICATION_CHANNEL_ID = "10001"
    private val default_notification_channel_id = "default"

    suspend fun postNotificaiton(
        smsaddress: String?,
        smsbody: String,
        smsnumber: String,
        threadId: Long,
        otp: String
    ) {
        delay(10)

        val expandedView = RemoteViews(context.packageName, R.layout.custom_expanded_notification)
        expandedView.setTextViewText(R.id.sender_name, smsaddress)
        expandedView.setTextViewText(R.id.sender_message, smsbody)

        val collapsedView = RemoteViews(context.packageName, R.layout.custom_collapse_notification)
        collapsedView.setTextViewText(R.id.otp_sender_name, "OTP from $smsaddress")
        collapsedView.setTextViewText(R.id.otp_show, otp)

        Constants.isActivitychange = true
        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(context)



            stackBuilder.addNextIntent(Intent(context, HomeABActivity::class.java))



        stackBuilder.addNextIntent(
            Intent(context, SendMessageActivity::class.java).putExtra("tredid", threadId)
                .putExtra("name", smsaddress).putExtra("mobileNumber", smsnumber)
        )
        val pendingIntent: PendingIntent = stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )


        val mNotificationManager =
            context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(context, default_notification_channel_id)
        mBuilder.setContentTitle(smsaddress)
        mBuilder.setTicker(smsbody)
        mBuilder.setSmallIcon(R.drawable.baseline_message_24_new)
        mBuilder.setLargeIcon(
            BitmapFactory.decodeResource(
                context.resources,
                R.drawable.contacticon
            )
        )
        mBuilder.setContentIntent(pendingIntent)
        mBuilder.setCustomContentView(collapsedView)
        mBuilder.setCustomBigContentView(expandedView)
        mBuilder.setAutoCancel(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "NOTIFICATION_CHANNEL_NAME",
                importance
            )
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
        mNotificationManager.notify(System.currentTimeMillis().toInt(), mBuilder.build())

    }

    suspend fun postNotificaitonprivatechat(
        smsaddress: String?,
        smsbody: String,
        smsnumber: String,
        threadId: Long,
        bitmap: Bitmap?
    ) {
        delay(10)

        Log.d("fdfdf", "postNotificaiton: <----------> $smsaddress")
        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addNextIntent(Intent(context, HomeABActivity::class.java))

        stackBuilder.addNextIntent(
            Intent(context, LockScreenActivity::class.java).putExtra("tredid", threadId)
                .putExtra("name", smsaddress)
                .putExtra("mobileNumber", smsnumber)
                .putExtra("lockype", 1)
                .putExtra("comefrom", 1)
                .putExtra("fromnotification", true)
        )
        val pendingIntent: PendingIntent = stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )


        val mNotificationManager =
            context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(context, default_notification_channel_id)
        mBuilder.setContentTitle(smsaddress)
        mBuilder.setContentText(smsbody)
        mBuilder.setTicker(smsbody)
        mBuilder.setSmallIcon(R.drawable.ic_message_notification_icon)
        mBuilder.setLargeIcon(
            BitmapFactory.decodeResource(
                context.resources,
                R.drawable.contacticon
            )
        )
        mBuilder.setContentIntent(pendingIntent)
        mBuilder.setAutoCancel(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "NOTIFICATION_CHANNEL_NAME",
                importance
            )
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
        mNotificationManager.notify(System.currentTimeMillis().toInt(), mBuilder.build())

    }

    suspend fun sendnotificationforfailedmessage(
        smsaddress: String?,
        smsbody: String,
        smsnumber: String,
        threadId: Long,
        bitmap: Bitmap?
    ) {
        delay(10)
//        if (messageNotify == threadId.toString()) {
//            return
//        }
        val largeIcon =
            bitmap ?: smsaddress?.let { SimpleContactsHelper(context).getContactLetterIcon(it) }
        val notificationId = threadId.hashCode()
        Constants.isActivitychange = true
        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addNextIntent(Intent(context, HomeABActivity::class.java))

        Constants.isActivitychange = true

        stackBuilder.addNextIntent(
            Intent(context, SendMessageActivity::class.java).apply {
                putExtra("tredid", threadId)
                putExtra("name", smsaddress)
                putExtra("mobileNumber", smsnumber)
            }
        )
        val contentPendingIntent =
            stackBuilder.getPendingIntent(
                notificationId,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

        val mNotificationManager =
            context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(context, default_notification_channel_id)
        mBuilder.setContentTitle("Can't send a message")
        mBuilder.setContentText("message to ${smsaddress} has not been sent")
        mBuilder.setTicker(smsbody)
        mBuilder.setSmallIcon(R.drawable.ic_message_notification_icon)
        mBuilder.setLargeIcon(
            largeIcon
        )
        mBuilder.setContentIntent(contentPendingIntent)
        mBuilder.setAutoCancel(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "NOTIFICATION_CHANNEL_NAME",
                importance
            )
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
        mNotificationManager.notify(System.currentTimeMillis().toInt(), mBuilder.build())
    }


    suspend fun sendnotificationforschedulemessage(
        smsaddress: String?,
        smsbody: String,
        smsnumber: String,
        threadId: Long,
        bitmap: Bitmap?
    ) {
        delay(10)
        val largeIcon =
            bitmap ?: smsaddress?.let { SimpleContactsHelper(context).getContactLetterIcon(it) }
        val notificationId = threadId.hashCode()
        Constants.isActivitychange = true
        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addNextIntent(Intent(context, HomeABActivity::class.java))

        Constants.isActivitychange = true

        stackBuilder.addNextIntent(
            Intent(context, SendMessageActivity::class.java).apply {
                putExtra("tredid", threadId)
                putExtra("name", smsaddress)
                putExtra("mobileNumber", smsnumber)
            }
        )
        val contentPendingIntent =
            stackBuilder.getPendingIntent(
                notificationId,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

        val mNotificationManager =
            context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(context, default_notification_channel_id)
        mBuilder.setContentTitle(context.resources.getString(R.string.Successfully_send_scheduled_message_to) + " " + smsaddress)
        mBuilder.setContentText(smsbody)
        mBuilder.setTicker(smsbody)
        mBuilder.setSmallIcon(R.drawable.ic_message_notification_icon)
        mBuilder.setLargeIcon(
            largeIcon
        )
        mBuilder.setContentIntent(contentPendingIntent)
        mBuilder.setAutoCancel(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "NOTIFICATION_CHANNEL_NAME",
                importance
            )
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
        mNotificationManager.notify(System.currentTimeMillis().toInt(), mBuilder.build())
    }

    suspend fun sendAutonotification(intro: Int, requestCode: Int) {
        delay(10)
        Constants.isActivitychange = true
        var notificationtital = ""
        var ContentText = ""

        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(context)

            stackBuilder.addNextIntent(Intent(context, HomeABActivity::class.java))


        if (!context.config.isprivacychatscreenopen) {

            notificationtital = "Privacy Mode Activated"
            ContentText =
                "You've activated private chat mode. Your conversations are now secure with end-to-end encryption \uD83D\uDD10\uD83D\uDCAC"
            stackBuilder.addNextIntent(
                Intent(context, LockScreenSetupActivity::class.java).apply {
                    putExtra("comefrom", 1)
                }
            )
        } else if (!context.config.iscolorthemescreenopen) {

            notificationtital = "App Color Mode Switched"
            ContentText =
                "You've changed the app's theme color. Experience the app in a new visual style! ☀\uFE0F\uD83C\uDF19 "

            stackBuilder.addNextIntent(
                Intent(context, SettingActivity::class.java).apply {
                    putExtra("isfromnotification", true)
                    putExtra("notify_vaule", "colortheme")
                }
            )

        } else if (!context.config.iscustomwallopaperscreenopen) {

            notificationtital = "Custom Background Set"
            ContentText =
                "Plan ahead for a fresh look! Your app now allows you to schedule future wallpaper changes. Set the mood for each moment! \uD83D\uDDBC\uFE0F\uD83C\uDFA8"

            stackBuilder.addNextIntent(
                Intent(
                    context,  HomeABActivity::class.java
                ).apply {
                    putExtra("isfromnotificationauto", true)
                    putExtra("notify_vaule", "iscustomwallopaperscreenopen")
                }
            )

        } else if (!context.config.ismessagetextsizescreenopen) {

            notificationtital = "Text Size Controls Available"
            ContentText =
                "Take control of your reading experience! Use text size controls to adjust the font size and enjoy comfortable reading! \uD83D\uDD20\uD83D\uDD8A\uFE0F"

            stackBuilder.addNextIntent(
                Intent(
                    context,  HomeABActivity::class.java
                ).apply {
                    putExtra("isfromnotificationauto", true)
                    putExtra("notify_vaule", "ismessagetextsizescreenopen")
                }
            )

        } else if (!context.config.ismessagecornerscreenopen) {

            notificationtital = "Message Corner Setting Apply"
            ContentText =
                "You can now adjust the message corner to your liking. Customize your chat experience with different corner styles! \uD83D\uDD32\uD83D\uDD27"

            stackBuilder.addNextIntent(
                Intent(
                    context,  HomeABActivity::class.java
                ).apply {
                    putExtra("isfromnotificationauto", true)
                    putExtra("notify_vaule", "ismessagecornerscreenopen")
                }
            )

        } else if (!context.config.istraslatescreenopen) {

            notificationtital = "Translate your Messages"
            ContentText =
                "Communicate with anyone, anywhere. Our new built-in translator lets you understand and be understood in real-time! ✅ "

            stackBuilder.addNextIntent(
                Intent(
                    context, HomeABActivity::class.java
                ).apply {
                    putExtra("isfromnotificationauto", true)
                    putExtra("notify_vaule", "istraslatescreenopen")
                }
            )

        } else if (!context.config.isapplockchatscreenopen) {

            notificationtital = "Make Your App Safe"
            ContentText =
                "You've accessed your app with 2 step authantication, Unlock Message app with your PIN! \uD83D\uDD13\uD83D\uDD11"

            stackBuilder.addNextIntent(
                Intent(context, LockScreenSetupActivity::class.java).apply {
                    putExtra("comefrom", 2)
                }
            )

        } else if (!context.config.isapplangscreenopen) {

            notificationtital = "Language Settings Saved"
            ContentText =
                "Your language preference has been saved. From now on, the app will speak your language. Enjoy using it in a way that feels most comfortable for you! \uD83C\uDF10"
            stackBuilder.addNextIntent(
                Intent(context, SettingActivity::class.java).apply {
                    putExtra("isfromnotification", true)
                    putExtra("notify_vaule", "language")
                }
            )


        } else if (!context.config.isnotificationscreenopen) {

            notificationtital = "Instant Notification Access"
            ContentText =
                "Get instant access! Peek at notifications without unlocking your device! \uD83D\uDC40\uD83D\uDD14"

            stackBuilder.addNextIntent(
                Intent(context, SettingActivity::class.java).apply {
                    putExtra("isfromnotification", true)
                    putExtra("notify_vaule", "notificationandsound")
                }
            )


        } else if (!context.config.isprivacyandsecscreenopen) {

            notificationtital = "Privacy Check-Up Reminder"
            ContentText =
                "Keep your information safe. Receive reminders to review and update your privacy settings regularly! \uD83D\uDD10\uD83D\uDEE1\uFE0F"

            stackBuilder.addNextIntent(
                Intent(context, SettingActivity::class.java).apply {
                    putExtra("isfromnotification", true)
                    putExtra("notify_vaule", "privacyandsecurity")
                }
            )


        } else if (!context.config.isblocknumberscreenopen) {

            notificationtital = "Blocked User Activity Alert"
            ContentText =
                "Stay informed about blocked user activity. Receive alerts if a blocked user attempts to interact with you! \uD83D\uDEAB"
            stackBuilder.addNextIntent(
                Intent(context, BlockNumberActivity::class.java)
            )

        } else if (!context.config.ismanageblocknumberscreenopen) {

            notificationtital = "Blocked Keyword Detected"
            ContentText =
                "Stay protected. Get notified when content containing blocked keywords is detected! \uD83D\uDD0D\uD83D\uDCDD"
            stackBuilder.addNextIntent(
                Intent(context, ManageBlockedKeywordsActivity::class.java)
            )

        } else if (!context.config.isdriwingmodescreenopen) {

            notificationtital = "Driving Mode Enabled"
            ContentText =
                "Keep your eyes on the road. Distraction-free driving mode is now active! \uD83D\uDE98\uD83D\uDCF5"
            stackBuilder.addNextIntent(
                Intent(context, SettingActivity::class.java).apply {
                    putExtra("isfromnotification", true)
                    putExtra("notify_vaule", "drivingmode")
                }
            )

        } else if (!context.config.isbackupandrestorescreenopen) {

            notificationtital = "Back up & Restored"
            ContentText =
                "Remember to back up your data manually to avoid loss! \uD83D\uDD04\uD83D\uDCE4"
            stackBuilder.addNextIntent(
                Intent(context, BekupActivity::class.java)
            )

        } else if (!context.config.isschedulemessagescreenopen) {
            notificationtital = "Scheduled Message Reminder"
            ContentText =
                "Just a friendly reminder: You have a message scheduled for your time! ⏰\uD83D\uDCE9"
            stackBuilder.addNextIntent(
                Intent(context, Schedule_Message_Show_Activity::class.java)
            )

        } else if (!context.config.isswipemotionscreenopen) {
            notificationtital = "Swipe Action Prompt "
            ContentText =
                "Unlock hidden powers! Swipe gestures let you take control of your messages in seconds! \uD83D\uDC49\uD83D\uDC48"
            stackBuilder.addNextIntent(
                Intent(context, SettingActivity::class.java).apply {
                    putExtra("isfromnotification", true)
                    putExtra("notify_vaule", "swipemotion")
                }
            )

        } else {
            notificationtital = ""
            ContentText = ""
            context.cancelAutoNotificationPendingIntent()
        }
        if (notificationtital == "") {
            return
        }


        val contentPendingIntent =
            stackBuilder.getPendingIntent(
                requestCode,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

        val mNotificationManager =
            context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(context, default_notification_channel_id)
        mBuilder.setContentTitle(notificationtital)
        mBuilder.setContentText(ContentText)
        mBuilder.setTicker(ContentText)
        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
        mBuilder.setContentIntent(contentPendingIntent)
        mBuilder.setAutoCancel(true)
        val bigTextStyle = NotificationCompat.BigTextStyle()
        bigTextStyle.bigText(ContentText)
        mBuilder.setStyle(bigTextStyle)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "NOTIFICATION_CHANNEL_NAME",
                importance
            )
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
        mNotificationManager.notify(System.currentTimeMillis().toInt(), mBuilder.build())
    }


    suspend fun SendRemainderNotification(notificationtitle: String, requestCode: Int) {
        delay(10)
        Constants.isActivitychange = true
        var notificationtital = ""
        var ContentText = ""
        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addNextIntent(
            Intent(
                context,  HomeABActivity::class.java
            )
        )
        notificationtital = "Remainder"
        ContentText = notificationtitle
        val contentPendingIntent =
            stackBuilder.getPendingIntent(
                requestCode,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

        val mNotificationManager =
            context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(context, default_notification_channel_id)
        mBuilder.setContentTitle(notificationtital)
        mBuilder.setContentText(ContentText)
        mBuilder.setTicker(ContentText)
        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
        mBuilder.setContentIntent(contentPendingIntent)
        mBuilder.setAutoCancel(true)
        val bigTextStyle = NotificationCompat.BigTextStyle()
        bigTextStyle.bigText(ContentText)
        mBuilder.setStyle(bigTextStyle)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "NOTIFICATION_Remainder",
                importance
            )
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
        mNotificationManager.notify(System.currentTimeMillis().toInt(), mBuilder.build())
    }

    suspend fun sendfullapplocknotification(requestCode: Int) {
        delay(10)
        Constants.isActivitychange = true
        var notificationtital = ""
        var ContentText = ""

        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addNextIntent(
            Intent(
                context,  HomeABActivity::class.java
            )
        )

        notificationtital = "Two step verification free try finish"
        ContentText =
            "start two step verification click notification and buy premium"
        val target = if (Constants.packagerenlist.isNotEmpty() &&
            Constants.packagerenlist!![0].freeTrialPeriod != null
        ) {
            TimerOfferActivity::class.java
        } else {
            when (BaseSharedPreferences(context).isCurrentPlan) {
                "Experiment Offering" -> SubActivityTwoplanActivity::class.java
                else -> PayAllSubsciptionExterimentActivity::class.java
            }
        }
        stackBuilder.addNextIntent(
            Intent(context, target).putExtra(
                "AppOpen", "SettingsActivity"
            ).putExtra("isfirsttime", false)
        )
        val contentPendingIntent =
            stackBuilder.getPendingIntent(
                requestCode,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

        val mNotificationManager =
            context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(context, default_notification_channel_id)
        mBuilder.setContentTitle(notificationtital)
        mBuilder.setContentText(ContentText)
        mBuilder.setTicker(ContentText)
        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
        mBuilder.setContentIntent(contentPendingIntent)
        mBuilder.setAutoCancel(true)
        val bigTextStyle = NotificationCompat.BigTextStyle()
        bigTextStyle.bigText(ContentText)
        mBuilder.setStyle(bigTextStyle)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "NOTIFICATION_CHANNEL_NAME",
                importance
            )
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
        mNotificationManager.notify(System.currentTimeMillis().toInt(), mBuilder.build())
    }

}
