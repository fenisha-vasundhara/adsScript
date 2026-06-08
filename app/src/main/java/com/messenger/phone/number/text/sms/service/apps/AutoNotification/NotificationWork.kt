//package com.messenger.phone.number.text.sms.service.apps.AutoNotification
//
//import android.annotation.SuppressLint
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.NotificationManager.IMPORTANCE_HIGH
//import android.app.PendingIntent
//import android.app.PendingIntent.getActivity
//import android.content.Context
//import android.content.Context.NOTIFICATION_SERVICE
//import android.content.Intent
//import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
//import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
//import android.graphics.Bitmap
//import android.graphics.Canvas
//import android.graphics.Color.RED
//import android.media.AudioAttributes
//import android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION
//import android.media.AudioAttributes.USAGE_NOTIFICATION_RINGTONE
//import android.media.RingtoneManager.TYPE_NOTIFICATION
//import android.media.RingtoneManager.getDefaultUri
//import android.os.Build.VERSION.SDK_INT
//import android.os.Build.VERSION_CODES.O
//import android.os.Build.VERSION_CODES.S
//import androidx.core.app.NotificationCompat
//import androidx.core.app.NotificationCompat.DEFAULT_ALL
//import androidx.core.content.ContextCompat
//import androidx.work.Data
//import androidx.work.ListenableWorker.Result.success
//import androidx.work.Worker
//import androidx.work.WorkerParameters
//import com.messenger.phone.number.text.sms.service.apps.R
//
//class NotificationWork(context: Context, params: WorkerParameters) : Worker(context, params) {
//
//    override fun doWork(): Result {
//        val id = inputData.getLong(NOTIFICATION_ID, 0).toInt()
//        sendNotification(id)
//        return success()
//    }
//
//    @SuppressLint("UnspecifiedImmutableFlag")
//    private fun sendNotification(id: Int) {
//
//        val bitmap = applicationContext.vectorToBitmap(R.drawable.appicon_sub)
//        val titleNotification = applicationContext.getString(R.string.notification_title)
//        var subtitleNotification = applicationContext.getString(R.string.notification_subtitle)
//
//        val data = if (!MySharedPref(applicationContext).isDubbleVideoUsed()) {
//            subtitleNotification = "Set dubble wallpapers"
//            "VideoActivity"
//        } else if (!MySharedPref(applicationContext).isCategoryActivityUsed()) {
//            subtitleNotification = "Get more wallpapers by category"
//            "CategoryActivity"
//        } else if (!MySharedPref(applicationContext).isBatteryActivityUsed()) {
//            subtitleNotification = "Get more battery animations"
//            "BatteryActivity"
//        } else if (!MySharedPref(applicationContext).isNotificationActivityUsed()) {
//            subtitleNotification = "Get more notification animations"
//            "NotificationActivity"
//        } else if (!MySharedPref(applicationContext).isFavoriteActivityUsed()) {
//            subtitleNotification = "You can save your favorite wallpapers"
//            "FavoriteActivity"
//        } else if (!MySharedPref(applicationContext).isAddEventActivityUsed()) {
//            subtitleNotification = "You can change wallpapers by date"
//            "AddEventActivity"
//        } else if (!MySharedPref(applicationContext).isManualChangeActivityUsed()) {
//            subtitleNotification = "You can change wallpaper by your actions"
//            "ManualChangeActivity"
//        } else if (!MySharedPref(applicationContext).isDownloadActivityUsed()) {
//            subtitleNotification = "See your downloaded wallpapers"
//            "DownloadActivity"
//        } else {
//            ""
//        }
//
//        if (data == "") {
//            return
//        } else {
//            val currentTime = System.currentTimeMillis()
//            val notificationData = Data.Builder().putInt(NOTIFICATION_ID, 0).build()
//            val delay = (currentTime + 900000) - currentTime
//            scheduleNotification(applicationContext, delay, notificationData)
//        }
//
//        val intent = Intent(applicationContext, SplashScreenActivity::class.java)
//        intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
//        intent.putExtra(NOTIFICATION_ID, id)
//        intent.putExtra("activity_name", data)
//
//        val notificationManager =
//            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//
//
//        val pendingIntent = if (SDK_INT >= S) {
//            getActivity(applicationContext, 0, intent, PendingIntent.FLAG_MUTABLE)
//        } else {
//            getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//        }
//
//        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL)
//            .setLargeIcon(bitmap)
//            .setSmallIcon(R.drawable.splash_app_icon)
//            .setContentTitle(titleNotification)
//            .setContentText(subtitleNotification)
//            .setDefaults(DEFAULT_ALL)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//
//        notification.priority = IMPORTANCE_HIGH
//
//        if (SDK_INT >= O) {
//            notification.setChannelId(NOTIFICATION_CHANNEL)
//
//            val ringtoneManager = getDefaultUri(TYPE_NOTIFICATION)
//            val audioAttributes = AudioAttributes.Builder().setUsage(USAGE_NOTIFICATION_RINGTONE)
//                .setContentType(CONTENT_TYPE_SONIFICATION).build()
//
//            val channel =
//                NotificationChannel(NOTIFICATION_CHANNEL, NOTIFICATION_NAME, IMPORTANCE_HIGH)
//
//            channel.enableLights(true)
//            channel.lightColor = RED
//            channel.enableVibration(true)
//            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
//            channel.setSound(ringtoneManager, audioAttributes)
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        notificationManager.notify(id, notification.build())
//    }
//
//    companion object {
//        const val NOTIFICATION_ID = "Message_Notification_ID"
//        const val NOTIFICATION_NAME = "Message"
//        const val NOTIFICATION_CHANNEL = "MWC"
//        const val NOTIFICATION_WORK = "Message_Notification_Work"
//    }
//
//    private fun Context.vectorToBitmap(drawableId: Int): Bitmap? {
//        val drawable = ContextCompat.getDrawable(this, drawableId) ?: return null
//        val bitmap = Bitmap.createBitmap(
//            drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
//        ) ?: return null
//        val canvas = Canvas(bitmap)
//        drawable.setBounds(0, 0, canvas.width, canvas.height)
//        drawable.draw(canvas)
//        return bitmap
//    }
//
//}