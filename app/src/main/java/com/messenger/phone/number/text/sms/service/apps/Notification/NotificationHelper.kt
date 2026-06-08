package com.messenger.phone.number.text.sms.service.apps.Notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Telephony
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.LocusIdCompat
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import com.demo.adsmanage.Commen.Constants
import com.google.android.datatransport.runtime.scheduling.persistence.EventStoreModule_PackageNameFactory.packageName
import com.messenger.phone.number.text.sms.service.apps.BubbleActivity
import com.messenger.phone.number.text.sms.service.apps.CommanClass.DEFAULT_CHANNEL_ID
import com.messenger.phone.number.text.sms.service.apps.CommanClass.MARK_AS_READ
import com.messenger.phone.number.text.sms.service.apps.CommanClass.NOTIFICATION_CHANNEL
import com.messenger.phone.number.text.sms.service.apps.CommanClass.REPLY
import com.messenger.phone.number.text.sms.service.apps.CommanClass.THREAD_ID
import com.messenger.phone.number.text.sms.service.apps.CommanClass.THREAD_NUMBER
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.drawableCache
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getNotificationChannel
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isShortCodeWithLetters
import com.messenger.phone.number.text.sms.service.apps.CommanClass.notificationManager
import com.messenger.phone.number.text.sms.service.apps.HomeABActivity
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.SendMessageActivity
import com.messenger.phone.number.text.sms.service.apps.data.SimpleContactsHelper
import com.messenger.phone.number.text.sms.service.apps.helper.RandomDrawableProvider
import com.simplemobiletools.commons.helpers.isNougatPlus
import com.simplemobiletools.commons.helpers.isOreoPlus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException


class NotificationHelper(private val context: Context) {
    private var wakeLock: PowerManager.WakeLock? = null

    private val notificationManager = context.notificationManager
    private var soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    private val user = Person.Builder()
        .setName(context.getString(R.string.me))
        .build()

    @SuppressLint("NewApi")
    fun showMessageNotification(
        address: String,
        body: String,
        threadId: Long,
        bitmap: Bitmap?,
        sender: String?,
        alertOnlyOnce: Boolean = false,
        messageid: Long,
    ) {

//        NOTIFICATION_CHANNEL = "${NOTIFICATION_CHANNEL}" + System.currentTimeMillis()

        if(Telephony.Sms.getDefaultSmsPackage(context) != context.packageName){
            return
        }


        if (context.config.iswakescreen) {
            onScreen()
        }

        val onlythisprofiledata = context.config.getProfileNotification(threadId.toString())

//        if (onlythisprofiledata != null) {
//            if (File(onlythisprofiledata.notificationUri).exists()) {
//                soundUri = onlythisprofiledata.notificationUri.toUri()
//            } else {
//                soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//            }
//        } else {
//            if (context.config.ringtoanselectforallcontact) {
//                soundUri = context.config.ringtoanselectforallcontactPath.toUri()
//            } else {
//                soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//            }
//        }

//        soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


        soundUri = context.config.ringtone(threadId)
            .takeIf { it.isNotEmpty() }
            ?.let(Uri::parse)
            ?.also { uri ->
                context.grantUriPermission(
                    "com.android.systemui",
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }

//        deleteNotificationChannel(context, NOTIFICATION_CHANNEL)
//        NOTIFICATION_CHANNEL = "${NOTIFICATION_CHANNEL}" + address
        NOTIFICATION_CHANNEL = getChannelIdForNotification(threadId)
        if (NOTIFICATION_CHANNEL == "notifications_default") {
            maybeCreateChannel(name = address)
        }
        Log.d(
            "NOTIFICATION_CHANNEL",
            "showMessageNotification: <------------> ${NOTIFICATION_CHANNEL}"
        )

//        maybeCreateChannel(name = address)

//        updateNotificationChannelSound(context, soundUri)

        var largeIcon: Bitmap? = null
        if (bitmap != null) {
            largeIcon = bitmap
        } else {
            try {
                val drawable = drawableCache[threadId.toString()]
                if (drawable != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        largeIcon = drawableToBitmap(drawable)
                    }

                } else {
                    val randomDrawable = RandomDrawableProvider(context).getRandomDrawable(threadId)
                    CoroutineScope(Dispatchers.Main).launch {
                        largeIcon = drawableToBitmap(randomDrawable)
                    }
                    drawableCache[threadId.toString()] = randomDrawable
                }
            } catch (E: Exception) {

                largeIcon = if (sender != null) {
                    SimpleContactsHelper(context).getContactLetterIcon(sender)
                } else {
                    null
                }
            }
        }

//        val icon = IconCompat.createWithAdaptiveBitmap(largeIcon!!)
//        val person = Person.Builder().setName(sender).setIcon(icon).build()


//        val shortcut =
//            ShortcutInfoCompat.Builder(context, shortcutId)
//                .setLocusId(LocusIdCompat(shortcutId))
//                .setActivity(ComponentName(context, SendMessageActivity::class.java))
//                .setIcon(icon)
//                .setCategories(setOf("com.messenger.phone.number.text.sms.service.apps.bubbles.category.TEXT_SHARE_TARGET"))
//                .setIntent(
//                    Intent(context, SendMessageActivity::class.java).setAction(Intent.ACTION_VIEW).apply {
//                        putExtra("tredid", threadId)
//                        putExtra("name", sender)
//                        putExtra("mobileNumber", address)
//                    }
//                )
//                .setLongLived(true)
//                .setShortLabel(sender!!)
//                .setPerson(
//                    Person.Builder()
//                        .setName(sender)
//                        .setIcon(icon)
//                        .build()
//                )
//                .build()
//
//        ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)

        val notificationId = threadId.hashCode()

        Constants.isActivitychange = true

        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(context)

            stackBuilder.addNextIntent(Intent(context, HomeABActivity::class.java))

        Constants.isActivitychange = true
//        Constants.isAdsClicking = true

        stackBuilder.addNextIntent(
            Intent(
                context, if (context.config.Message_Send_Activity == "1") {
                    SendMessageActivity::class.java
                } else {
                    SendMessageActivity::class.java
                }
            ).apply {
                putExtra("tredid", threadId)
                putExtra("name", sender)
                putExtra("mobileNumber", address)
                putExtra("fromnotification2", true)
            }
        )

        val contentPendingIntent =
            stackBuilder.getPendingIntent(
                notificationId,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

//        val contentPendingIntentforbubble =
//            PendingIntent.getActivity(
//                context,
//                notificationId,
//                // Launch BubbleActivity as the expanded bubble.
//                Intent(context, SendMessageActivity::class.java).apply {
//                    putExtra("tredid", threadId)
//                    putExtra("name", sender)
//                    putExtra("mobileNumber", address)
//                },flagUpdateCurrent(true)
//
//            )

        val markAsReadIntent = Intent(context, MarkAsReadReceiver::class.java).apply {
            action = MARK_AS_READ
            putExtra(THREAD_ID, threadId)
            putExtra("mobileNumber", address)
            putExtra("messageid", messageid)
        }

        val markAsReadPendingIntent =
            PendingIntent.getBroadcast(
                context,
                notificationId,
                markAsReadIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )


        val markAsReadIntentforfirstbutton =
            Intent(context, MarkAsReadForFirstReceiver::class.java).apply {
                action = MARK_AS_READ
                putExtra(THREAD_ID, threadId)
                putExtra("mobileNumber", address)
                putExtra("messageid", messageid)
            }

        val markAsReadPendingIntentforfirstbutton =
            PendingIntent.getBroadcast(
                context,
                notificationId,
                markAsReadIntentforfirstbutton,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )


        val markAsReadIntentforThirdbutton =
            Intent(context, MarkAsReadForThridReceiver::class.java).apply {
                action = MARK_AS_READ
                putExtra(THREAD_ID, threadId)
                putExtra("mobileNumber", address)
                putExtra("messageid", messageid)
            }

        val markAsReadPendingIntentforThirdbutton =
            PendingIntent.getBroadcast(
                context,
                notificationId,
                markAsReadIntentforThirdbutton,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )



        val callintent =
            Intent(
                context,
                HomeABActivity::class.java
            )


        callintent.action = "ACTION_DIAL_NUMBER"
        callintent.putExtra("PHONE_NUMBER", address)
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        val CallPendingIntent = PendingIntent.getActivity(context, 0, callintent, flags)


        var replyAction: NotificationCompat.Action? = null
        if (isNougatPlus() && !isShortCodeWithLetters(address)) {
            val replyLabel = context.getString(R.string.reply_message)
            val remoteInput = RemoteInput.Builder(REPLY)
                .setLabel(replyLabel)
                .build()

            val replyIntent = Intent(context, DirectReplyReceiver::class.java).apply {
                putExtra(THREAD_ID, threadId)
                putExtra(THREAD_NUMBER, address)
                putExtra("messageid", messageid)
            }

            val replyPendingIntent =
                PendingIntent.getBroadcast(
                    context.applicationContext,
                    notificationId,
                    replyIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )
            replyAction = NotificationCompat.Action.Builder(
                R.drawable.ic_send_vector,
                replyLabel,
                replyPendingIntent
            )
                .addRemoteInput(remoteInput)
                .setAllowGeneratedReplies(true)
                .build()
        }


        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL).apply {
            setLargeIcon(largeIcon)
            when (context.config.lastselectedpreviews) {
                "Show name and message" -> {
//                    setStyle(NotificationCompat.MessagingStyle(user))
                    setStyle(getMessagesStyle(address, body, notificationId, sender))
                }

                "Show name" -> {
//                    setStyle(NotificationCompat.MessagingStyle(user))
                    setStyle(getMessagesStyle(address, "New message", notificationId, sender))
                }

                else -> {
//                    setStyle(NotificationCompat.MessagingStyle(user))
                    setStyle(
                        getMessagesStyle(
                            address,
                            "New message",
                            notificationId,
                            "New message"
                        )
                    )


                }
            }

            Log.d("", "showMessageNotification: soundUri 4 <--------> ${soundUri}")
            setSmallIcon(R.drawable.ic_message_notification_icon)
            setContentIntent(contentPendingIntent)
            priority = NotificationCompat.PRIORITY_MAX
            setDefaults(Notification.DEFAULT_LIGHTS)
            setCategory(Notification.CATEGORY_MESSAGE)
            setAutoCancel(true)
//            setBubbleMetadata(
//                NotificationCompat.BubbleMetadata.Builder(contentPendingIntentforbubble, icon)
//                    // The height of the expanded bubble.
//                    .setDesiredHeight(context.resources.getDimensionPixelSize(R.dimen.bubble_height))
//                    .build()
//            )
            setOnlyAlertOnce(false)
//            setShortcutId(shortcutId)
//            setLocusId(LocusIdCompat(shortcutId))
//            addPerson(person)
            setShowWhen(true)
            setSound(soundUri, AudioManager.STREAM_NOTIFICATION)
        }

//        builder.addAction(replyAction)

        when (context.config.NotiButton1) {
            "Mark as read" -> {
                builder.addAction(
                    R.drawable.check,
                    context.getString(R.string.Mark_as_read),
                    markAsReadPendingIntentforfirstbutton
                )
                    .setChannelId(NOTIFICATION_CHANNEL)
            }

            "Reply" -> {
                builder.addAction(replyAction)
            }

            "Call" -> {
                builder.addAction(R.drawable.check, "Call", CallPendingIntent)
                    .setChannelId(NOTIFICATION_CHANNEL)

            }

            "Delete" -> {
                builder.addAction(R.drawable.check, "Delete", markAsReadPendingIntentforfirstbutton)
                    .setChannelId(NOTIFICATION_CHANNEL)
            }
        }


        when (context.config.NotiButton2) {
            "Mark as read" -> {
                builder.addAction(
                    R.drawable.check,
                    context.getString(R.string.Mark_as_read),
                    markAsReadPendingIntent
                )
                    .setChannelId(NOTIFICATION_CHANNEL)
            }

            "Reply" -> {
                builder.addAction(replyAction)
            }

            "Call" -> {
                builder.addAction(R.drawable.check, "Call", CallPendingIntent)
                    .setChannelId(NOTIFICATION_CHANNEL)

            }

            "Delete" -> {
                builder.addAction(R.drawable.check, "Delete", markAsReadPendingIntent)
                    .setChannelId(NOTIFICATION_CHANNEL)
            }
        }

        when (context.config.NotiButton3) {
            "Mark as read" -> {
                builder.addAction(
                    R.drawable.check,
                    context.getString(R.string.Mark_as_read),
                    markAsReadPendingIntentforThirdbutton
                )
                    .setChannelId(NOTIFICATION_CHANNEL)
            }

            "Reply" -> {
                builder.addAction(replyAction)
            }

            "Call" -> {
                builder.addAction(R.drawable.check, "Call", CallPendingIntent)
                    .setChannelId(NOTIFICATION_CHANNEL)

            }

            "Delete" -> {
                builder.addAction(R.drawable.check, "Delete", markAsReadPendingIntentforThirdbutton)
                    .setChannelId(NOTIFICATION_CHANNEL)
            }
        }
        try {
            notificationManager.notify(notificationId, builder.build())
        } catch (e: Exception) {

        }
//        playCustomSound(context,soundUri)
    }

    private fun playCustomSound(context: Context, soundUri: Uri?) {
        if (soundUri == null) return // Exit if the Uri is null
        try {
            val mediaPlayer = MediaPlayer().apply {
                setDataSource(context, soundUri)
                prepare()  // Prepare the MediaPlayer asynchronously
                start()    // Start playback
                setOnCompletionListener {
                    release()  // Release resources once playback is complete
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()  // Handle IOException
        }
    }


    fun updateNotificationChannelSound(context: Context, soundUri: Uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            val channel = notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL) ?: return
            // Update sound setting
            channel.setSound(soundUri, null)
            // Apply the updated settings
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun maybeCreateChannel(name: String) {
        Log.d("", "showMessageNotification: soundUri 3 <--------> ${soundUri}")
        if (isOreoPlus()) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                .build()

            val id = NOTIFICATION_CHANNEL
            val importance = IMPORTANCE_HIGH
            NotificationChannel(id, name, importance).apply {
                setBypassDnd(false)
                enableLights(true)
                setSound(soundUri, audioAttributes)
                enableVibration(true)
                try {
                    notificationManager.createNotificationChannel(this)
                } catch (e: Exception) {

                }
            }
        }
    }

    private fun getMessagesStyle(
        address: String,
        body: String,
        notificationId: Int,
        name: String?,
    ): NotificationCompat.MessagingStyle {
        val sender = if (name != null) {
            Person.Builder()
                .setName(name)
                .setKey(address)
                .build()
        } else {
            null
        }

        return NotificationCompat.MessagingStyle(user).also { style ->
            getOldMessages(notificationId).forEach {
                style.addMessage(it)
            }
            val newMessage =
                NotificationCompat.MessagingStyle.Message(body, System.currentTimeMillis(), sender)
            style.addMessage(newMessage)
        }
    }

    private fun getOldMessages(notificationId: Int): List<NotificationCompat.MessagingStyle.Message> {
        if (!isNougatPlus()) {
            return emptyList()
        }
        val currentNotification =
            notificationManager.activeNotifications.find { it.id == notificationId }
        return if (currentNotification != null) {
            val activeStyle =
                NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(
                    currentNotification.notification
                )
            activeStyle?.messages.orEmpty()
        } else {
            emptyList()
        }
    }

//    fun onScreen() {
//        try {
//            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
//            val isScreenOn = pm.isInteractive
//            if (!isScreenOn) {
//                val wl = pm.newWakeLock(
//                    PowerManager.SCREEN_DIM_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
//                    "messengerMy:notificationLock"
//                )
//                wl.acquire(3000)
//            }
//        } catch (_: Exception) {
//        }
//    }

    fun onScreen() {
        try {
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val isScreenOn = pm.isInteractive

            if (!isScreenOn) {
                if (wakeLock == null) {
                    wakeLock = pm.newWakeLock(
                        PowerManager.SCREEN_DIM_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                        "messengerMy:notificationLock"
                    )
                }

                if (!wakeLock!!.isHeld) {
                    wakeLock!!.acquire(3000) // 3 seconds
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            releaseWakeLock()
        }
    }

    fun releaseWakeLock() {
        try {
            if (wakeLock != null && wakeLock!!.isHeld) {
                wakeLock!!.release()
                wakeLock = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun flagUpdateCurrent(mutable: Boolean): Int {
        return if (mutable) {
            if (Build.VERSION.SDK_INT >= 31) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        }
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun deleteNotificationChannel(context: Context, channelId: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Use reflection to safely call getNotificationChannel
        val existingChannel = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.javaClass.getMethod(
                    "getNotificationChannel",
                    String::class.java
                )
                    .invoke(notificationManager, channelId) as NotificationChannel?
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("Notification", "Reflection error: ${e.message}")
            null
        }

        if (existingChannel != null) {
//            Log.d("Notification", "Channel exists before deletion: ${existingChannel.id}")

            // Delete the channel
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationManager.deleteNotificationChannel(channelId)
                    Log.d("Notification", "Attempted to delete channel: $channelId")
                }
            } catch (e: Exception) {
                Log.e("Notification", "Error deleting channel: ${e.message}")
            }

            // Verify deletion
            val deletedChannel = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationManager.javaClass.getMethod(
                        "getNotificationChannel",
                        String::class.java
                    )
                        .invoke(notificationManager, channelId) as NotificationChannel?
                } else {
                    null
                }
            } catch (e: Exception) {
                Log.e("Notification", "Reflection error: ${e.message}")
                null
            }

            if (deletedChannel == null) {
                Log.d("Notification", "Channel successfully deleted: $channelId")
            } else {
//                Log.d(
//                    "Notification",
//                    "Channel still exists after deletion attempt: ${deletedChannel.id}"
//                )
            }
        } else {
            Log.d("Notification", "Channel does not exist before deletion attempt: $channelId")
        }
    }

    fun getChannelIdForNotification(threadId: Long): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return context.getNotificationChannel(threadId)?.id ?: DEFAULT_CHANNEL_ID
        }

        return DEFAULT_CHANNEL_ID
    }
}
