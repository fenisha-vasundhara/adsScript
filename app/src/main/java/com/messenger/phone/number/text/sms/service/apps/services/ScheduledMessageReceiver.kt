package com.messenger.phone.number.text.sms.service.apps.services

import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.Telephony
import android.telephony.SmsManager
import android.util.Log
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ADDRESS_SEPARATOR
import com.messenger.phone.number.text.sms.service.apps.CommanClass.SCHEDULED_MESSAGE_ID
import com.messenger.phone.number.text.sms.service.apps.CommanClass.THREAD_ID
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getAddresses
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getNotificationBitmap
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getSendMessageSettings
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getThreadId
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getThreadParticipants
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getThreadTitle
import com.messenger.phone.number.text.sms.service.apps.CommanClass.insertSmsMessageForGroup
import com.messenger.phone.number.text.sms.service.apps.Notification.notificationProvider
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.data.SimpleContactsHelper
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.sms.SendSMSManager
import com.simplemobiletools.commons.extensions.showErrorToast
import com.simplemobiletools.commons.extensions.toast
import com.simplemobiletools.commons.helpers.ensureBackgroundThread
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class ScheduledMessageReceiver : BroadcastReceiver() {


    private var message: Conversation? = null

    @Inject
    lateinit var repo: MessagerDatabaseRepo

    @Inject
    lateinit var sendSMSManager: SendSMSManager


    @Inject
    lateinit var notificationProvider: notificationProvider


    private var isarchiv: Boolean = false
    private var isPrivateChat: Boolean = false

    var list: ArrayList<Conversation> = arrayListOf()


    override fun onReceive(context: Context, intent: Intent) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "txt.messenger:scheduled.message.receiver")
        wakelock.acquire(3000)


        ensureBackgroundThread {
            handleIntent(context, intent)
        }
    }

    private fun handleIntent(context: Context, intent: Intent) {
        val threadId = intent.getLongExtra(THREAD_ID, 0L)
        val messageId = intent.getLongExtra(SCHEDULED_MESSAGE_ID, 0L)
        message = repo.getScheduledMessageWithIdRepo(threadId, messageId)

        try {
            val subscriptionId = SmsManager.getDefaultSmsSubscriptionId()
            Handler(Looper.getMainLooper()).post {
                message?.let { messagebrod ->
                    val addresses = convertStringToArrayList(messagebrod.phoneNumber, "fornumber")
                    val settings = context.getSendMessageSettings()
                    if (addresses.size > 1) {

                        CoroutineScope(Dispatchers.IO).launch {
                            sendSMSManager.sendMessageCompat(messagebrod.snippet, addresses, subscriptionId, arrayListOf(), isgroupmessage = true)
                        }


                        val broadCastThreadId = context.getThreadId(addresses.toSet())
                        val mergedAddresses = addresses.joinToString(ADDRESS_SEPARATOR)

                        val messageuri = context.insertSmsMessageForGroup(
                            subId = settings.subscriptionId, dest = mergedAddresses, text = messagebrod.snippet, timestamp = System.currentTimeMillis(), threadId = broadCastThreadId, status = Telephony.Sms.Sent.STATUS_COMPLETE, type = Telephony.Sms.Sent.MESSAGE_TYPE_SENT
                        )
                        val insertedId = messageuri.lastPathSegment!!.toLong()
                        CoroutineScope(Dispatchers.IO).launch {
                            list = ArrayList(repo.getUserMessageListrepo(threadId))
                        }


                        isarchiv = if (list?.isEmpty() == true) {
                            false
                        } else {
                            list!![0].isarchived
                        }

                        isPrivateChat = if (list?.isEmpty() == true) {
                            false
                        } else {
                            list!![0].isPrivateChat
                        }

                        CoroutineScope(Dispatchers.IO).launch {
                            if (!repo.isMessageExitsRepo(insertedId)) {
                                val c: Date = Calendar.getInstance().time
                                val data = repo.getUserMessageListChackrepo(threadId!!)
                                if (data.isNotEmpty()) {
                                    val isgropmessage = data[0].isgroupmessage
                                    if (isgropmessage) {
                                        val conversation = Conversation(
                                            0,
                                            c.time.toString(),
                                            true,
                                            data[0].title,
                                            null,
                                            false,
                                            data[0].phoneNumber,
                                            messagebrod.snippet,
                                            c.time,
                                            2,
                                            true,
                                            null,
                                            messageId = insertedId,
                                            threadId = threadId,
                                            isgroupmessage = true,
                                            groupName = data[0].groupName,
                                            isarchived = isarchiv,
                                            isPrivateChat = isPrivateChat
                                        )
                                        repo.insertmessage(conversation)

                                        val photoUri = SimpleContactsHelper(context).getPhotoUriFromPhoneNumber(data[0].phoneNumber)
                                        val bitmap = context.getNotificationBitmap(photoUri)

                                        notificationProvider.sendnotificationforschedulemessage(
                                            threadId = threadId,
                                            smsaddress = data[0].title,
                                            smsnumber = data[0].phoneNumber,
                                            smsbody = messagebrod.snippet,
                                            bitmap = bitmap
                                        )
                                    }
                                } else {

                                    val participantsforgroupchat = context.getThreadParticipants(threadId!!, null)
                                    val addresses = participantsforgroupchat.getAddresses()
                                    val result = addresses.joinToString(separator = "|")

                                    val conversation = Conversation(
                                        0,
                                        c.time.toString(),
                                        true,
                                        participantsforgroupchat.getThreadTitle(),
                                        null,
                                        false,
                                        result,
                                        messagebrod.snippet,
                                        c.time,
                                        2,
                                        true,
                                        null,
                                        messageId = insertedId,
                                        threadId = threadId,
                                        isgroupmessage = true,
                                        groupName = participantsforgroupchat.getThreadTitle(),
                                        isarchived = isarchiv,
                                        isPrivateChat = isPrivateChat
                                    )
                                    repo.insertmessage(conversation)

                                    val photoUri = SimpleContactsHelper(context).getPhotoUriFromPhoneNumber(result)
                                    val bitmap = context.getNotificationBitmap(photoUri)

                                    notificationProvider.sendnotificationforschedulemessage(
                                        threadId = threadId,
                                        smsaddress = participantsforgroupchat.getThreadTitle(),
                                        smsnumber = result,
                                        smsbody = messagebrod.snippet,
                                        bitmap = bitmap
                                    )
                                }
                            }


                        }

                    } else {


                        CoroutineScope(Dispatchers.IO).launch {
                            sendSMSManager.sendMessageCompat(messagebrod.snippet, listOf(messagebrod.phoneNumber), subscriptionId, arrayListOf(), isgroupmessage = false)
                            val photoUri = SimpleContactsHelper(context).getPhotoUriFromPhoneNumber(messagebrod.phoneNumber)
                            val bitmap = context.getNotificationBitmap(photoUri)

                            notificationProvider.sendnotificationforschedulemessage(
                                threadId = threadId,
                                smsaddress = messagebrod.title,
                                smsnumber = messagebrod.phoneNumber,
                                smsbody = messagebrod.snippet,
                                bitmap = bitmap
                            )
                        }
                    }

                }

            }
            CoroutineScope(Dispatchers.IO).launch {
                repo.deletemessageRepo(messageId)
            }
        } catch (e: Exception) {
            context.showErrorToast(e)
        } catch (e: Error) {
            context.showErrorToast(e.localizedMessage ?: context.getString(R.string.unknown_error_occurred))
        }
    }

    fun convertStringToArrayList(inputString: String, s: String): ArrayList<String> {
        if (s == "forname") {
            val stringArray = inputString.split(",").toTypedArray()
            val arrayList = ArrayList<String>()

            for (str in stringArray) {
                arrayList.add(str.trim())
            }

            return arrayList
        } else {
            val stringArray = inputString.split("|").toTypedArray()
            val arrayList = ArrayList<String>()

            for (str in stringArray) {
                arrayList.add(str.trim())
            }

            return arrayList
        }

    }
}
