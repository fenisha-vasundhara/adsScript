package com.messenger.phone.number.text.sms.service.apps.Notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.content.LocusIdCompat
import androidx.core.content.getSystemService
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import com.demo.adsmanage.Commen.Constants
import com.messenger.phone.number.text.sms.service.apps.CommanClass.baseConfig
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.HomeABActivity
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.SendMessageActivity
import com.messenger.phone.number.text.sms.service.apps.data.SimpleContactsHelper

@RequiresApi(Build.VERSION_CODES.O)
class NootificationBubble(private val context: Context) {

    companion object {
        private const val CHANNEL_NEW_MESSAGES = "new_messages"

        private const val REQUEST_CONTENT = 1
        private const val REQUEST_BUBBLE = 2
    }

    private val notificationManager: NotificationManager =
        context.getSystemService() ?: throw IllegalStateException()


    init {
        setUpNotificationChannels()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setUpNotificationChannels() {
        if (notificationManager.getNotificationChannel(CHANNEL_NEW_MESSAGES) == null) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_NEW_MESSAGES,
                    context.getString(R.string.channel_new_messages),
                    // The importance must be IMPORTANCE_HIGH to show Bubbles.
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = context.getString(R.string.channel_new_messages_description)
                }
            )
        }



        updateShortcuts()
    }

    @WorkerThread
    fun updateShortcuts() {

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

    @RequiresApi(Build.VERSION_CODES.Q)
    fun showNotification(address: String, body: String, threadId: Long, bitmap: Bitmap?, sender: String?, alertOnlyOnce: Boolean = false, messageid: Long, fromUser: Boolean, update: Boolean = false) {

        var shortcutId = "${sender}_1"

        val shortcut =
            ShortcutInfoCompat.Builder(context, shortcutId)
                .setCategories(setOf("com.example.android.bubbles.category.TEXT_SHARE_TARGET"))
                .setIntent(Intent(Intent.ACTION_DEFAULT))
                .setLongLived(true)
                .setShortLabel(sender!!)
                .build()

        ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)

        val largeIcon = bitmap ?: if (sender != null) {
            SimpleContactsHelper(context).getContactLetterIcon(sender)
        } else {
            null
        }


        val notificationId = threadId.hashCode()

        val icon = IconCompat.createWithAdaptiveBitmap(largeIcon!!)
        val user = Person.Builder().setName(context.getString(R.string.sender_you)).build()
        val person = Person.Builder().setName(sender).setIcon(icon).build()
        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(context)

            stackBuilder.addNextIntent(Intent(context, HomeABActivity::class.java))

        Constants.isActivitychange = true
//        Constants.isAdsClicking = true

        stackBuilder.addNextIntent(
            Intent(context, if (context.config.Message_Send_Activity == "1") {
                SendMessageActivity::class.java
            } else {
                SendMessageActivity::class.java
            }).apply {
                putExtra("tredid", threadId)
                putExtra("name", sender)
                putExtra("mobileNumber", address)
            }
        )

        val pendingIntent =
            stackBuilder.getPendingIntent(notificationId, flagUpdateCurrent(mutable = true))

//        val contentUri = "https://android.example.com/chat/${chat.contact.id}".toUri()
//
//        val pendingIntent = PendingIntent.getActivity(
//            context,
//            REQUEST_BUBBLE,
//            // Launch BubbleActivity as the expanded bubble.
//            Intent(context, SendMessageActivity::class.java)
//                .setAction(Intent.ACTION_VIEW)
//                .setData(contentUri),
//            flagUpdateCurrent(mutable = true)
//        )


        // Let's add some more content to the notification in case it falls back to a normal
        // notification.
        val messagingStyle = NotificationCompat.MessagingStyle(user)
        val lastId = messageid

//        for (message in chat.messages) {
//            val m = NotificationCompat.MessagingStyle.Message(
//                message.text,
//                message.timestamp,
//                if (message.isIncoming) person else null
//            ).apply {
//                if (message.photoUri != null) {
//                    setData(message.photoMimeType, message.photoUri)
//                }
//            }
//            if (message.id < lastId) {
//                messagingStyle.addHistoricMessage(m)
//            } else {
//                messagingStyle.addMessage(m)
//            }
//        }


//        Log.d("", "showNotification: <---------> chat.contact.shortcutId ${chat.contact.shortcutId}")

        val builder = NotificationCompat.Builder(context, CHANNEL_NEW_MESSAGES)
            // A notification can be shown as a bubble by calling setBubbleMetadata()
            .setBubbleMetadata(
                NotificationCompat.BubbleMetadata.Builder(pendingIntent, icon)
                    // The height of the expanded bubble.
                    .setDesiredHeight(context.resources.getDimensionPixelSize(R.dimen.bubble_height))
                    .apply {
                        // When the bubble is explicitly opened by the user, we can show the bubble
                        // automatically in the expanded state. This works only when the app is in
                        // the foreground.
                        if (fromUser) {
                            setAutoExpandBubble(true)
                        }
                        if (fromUser || update) {
                            setSuppressNotification(true)
                        }
                    }
                    .build()
            )
            .setContentTitle(sender)
            .setCategory(Notification.CATEGORY_MESSAGE)
            .setShortcutId(shortcutId)
            .setLocusId(LocusIdCompat(shortcutId))
            .addPerson(person)
            .setShowWhen(true)
            .setContentIntent(pendingIntent)
            // Direct Reply
//            .addAction(
//                NotificationCompat.Action
//                    .Builder(
//                        IconCompat.createWithResource(context, R.drawable.ic_send_vector),
//                        context.getString(R.string.label_reply),
//                        PendingIntent.getBroadcast(
//                            context,
//                            REQUEST_CONTENT,
//                            Intent(context, ReplyReceiver::class.java).setData(contentUri),
//                            flagUpdateCurrent(mutable = true)
//                        )
//                    )
//                    .addRemoteInput(
//                        RemoteInput.Builder(ReplyReceiver.KEY_TEXT_REPLY)
//                            .setLabel(context.getString(R.string.hint_input))
//                            .build()
//                    )
//                    .setAllowGeneratedReplies(true)
//                    .build()
//            )
            // Let's add some more content to the notification in case it falls back to a normal
            // notification.
            .setStyle(messagingStyle)
            .setWhen(SystemClock.currentThreadTimeMillis())
        // Don't sound/vibrate if an update to an existing notification.
        if (update) {
            builder.setOnlyAlertOnce(true)
        }
        notificationManager.notify(notificationId, builder.build())
    }


    private fun dismissNotification(id: Long) {
        notificationManager.cancel(id.toInt())
    }


}