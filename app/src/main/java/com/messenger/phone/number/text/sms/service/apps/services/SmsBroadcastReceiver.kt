package com.messenger.phone.number.text.sms.service.apps.services

import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsManager
import android.telephony.SubscriptionInfo
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.text.isDigitsOnly
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.findOtpInString
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getConversations
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getMessages
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getMyContactsCursor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getMyContactsCursor2
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getNameFromAddress
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getNotificationBitmap
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getThreadId
import com.messenger.phone.number.text.sms.service.apps.CommanClass.indexOfFirstOrNull
import com.messenger.phone.number.text.sms.service.apps.CommanClass.insertNewSMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isNumberBlocked
import com.messenger.phone.number.text.sms.service.apps.CommanClass.messageNotify
import com.messenger.phone.number.text.sms.service.apps.CommanClass.showReceivedMessageNotification
import com.messenger.phone.number.text.sms.service.apps.CommanClass.subscriptionManagerCompat
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.Notification.notificationProvider
import com.messenger.phone.number.text.sms.service.apps.data.Message
import com.messenger.phone.number.text.sms.service.apps.data.PhoneNumber
import com.messenger.phone.number.text.sms.service.apps.data.SIMCard
import com.messenger.phone.number.text.sms.service.apps.data.SimpleContactsHelper
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.sms.SendSMSManager
import com.simplemobiletools.commons.extensions.getMyContactsCursor
import com.simplemobiletools.commons.models.SimpleContact
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.regex.Matcher
import javax.inject.Inject


@AndroidEntryPoint
class SmsBroadcastReceiver : BroadcastReceiver() {

    private var senderNameNew: String = "Unknown"
    private var isUserNotificationShow: Boolean = true
    private var chack: Boolean = false
    private var otp: String? = ""
    private lateinit var matchResult: Matcher
    private var isarchiv: Boolean = false
    private var isPrivateChat: Boolean = false
    private var catname: String? = null
    private lateinit var c: Date
    private lateinit var shimmilarnumber: String
    private var isnewmwssage: Boolean = false
    private lateinit var conversation: Conversation
    private val availableSIMCards = ArrayList<SIMCard>()
    private var messages = ArrayList<Message>()
    private var currentSIMCardIndex = 0

    var messagetype: String = "normalmessage"
    var messageotp: String? = null

    private var participants: ArrayList<SimpleContact> = arrayListOf()

    @Inject
    lateinit var messagerDatabaseRepo: MessagerDatabaseRepo

    @Inject
    lateinit var notificationProvider: notificationProvider


    @Inject
    lateinit var sendSMSManager: SendSMSManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
//        CoroutineScope(Dispatchers.IO).launch {
//            sentdemomessage(context)
//        }
        CoroutineScope(Dispatchers.IO).launch {

            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            var address = ""
            var body = ""
            var subject = ""
            var date = 0L
            var threadId = 0L
            var status = Telephony.Sms.STATUS_NONE
            val type = Telephony.Sms.MESSAGE_TYPE_INBOX
            val read = 0
            val subscriptionId = intent.getIntExtra("subscription", -1)

            messages.forEach {
                address = it.originatingAddress ?: ""
                subject = it.pseudoSubject
                Log.d("", "onReceive: pseudoSubject <----------> ${subject}")
                Log.d("", "onReceive: pseudoSubject subscriptionId <----------> ${subscriptionId}")
                status = it.status
                body += it.messageBody
                date = System.currentTimeMillis()
                c = Calendar.getInstance().time
                threadId = context.getThreadId(address)

                val data = messagerDatabaseRepo.getUserMessageListChackrepo(threadId)
                if (data.isEmpty()) {
                    val phoneNumber = PhoneNumber(address, 0, "", address)
                    val datausingmobile =
                        messagerDatabaseRepo.getUserMessageMobileListChackrepo(phoneNumber.normalizedNumber)
                    if (datausingmobile.isEmpty()) {
                        val redata =
                            messagerDatabaseRepo.getUserMessageRecyListChackrepo(phoneNumber.normalizedNumber)
                        threadId = if (redata.isEmpty()) {
                            context.getThreadId(address)
                        } else {
                            if (redata[0].threadId != null) {
                                redata[0].threadId!!
                            } else {
                                context.getThreadId(address)
                            }
                        }
                    } else {
                        threadId = if (datausingmobile[0].threadId != null) {
                            datausingmobile[0].threadId!!
                        } else {
                            context.getThreadId(address)
                        }
                    }
                } else {
                    threadId = if (data[0].threadId != null) {
                        data[0].threadId!!
                    } else {
                        context.getThreadId(address)
                    }
                }
            }

            handleMessage(
                context,
                address,
                subject,
                body,
                c.time,
                read,
                threadId,
                type,
                subscriptionId,
                status
            )
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun sentdemomessage(context: Context) {
        CoroutineScope(Dispatchers.Main).launch { context.toastMess("sentdemomessage") }
        (0..10000).forEachIndexed { index, i ->
            var number = "${"(966476699)"}" + (0..100).random()
//            val number = "9664766997"
            handleMessage(
                context,
                number,
                "demomessage",
                "Demo System Message", // Use the asterisks in the message
                System.currentTimeMillis(),
                1,
                ((100..150).random()).toLong(),
                1, -1, 1
            )
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun handleMessage(
        context: Context,
        address: String,
        subject: String,
        body: String,
        date: Long,
        read: Int,
        threadId: Long,
        type: Int,
        subscriptionId: Int,
        status: Int,
    ) {
        Log.d("", "onReceive: originatingAddress <----------> 23 ${threadId}")
        if (isMessageFilteredOut(context, body)) {
            return
        }
        Log.d("", "onReceive: originatingAddress <----------> 24 ${threadId}")
        val photoUri = SimpleContactsHelper(context).getPhotoUriFromPhoneNumber(address)
        val bitmap = context.getNotificationBitmap(photoUri)
        if (!context.isNumberBlocked(address)) {
            Log.d("", "onReceive: originatingAddress <----------> 25 ${threadId}")
            val privateCursor =
                context.getMyContactsCursor2(favoritesOnly = false, withPhoneNumbersOnly = true)
            val newMessageId = context.insertNewSMS(
                address,
                subject,
                body,
                date,
                read,
                threadId,
                type,
                subscriptionId
            )
            val phoneNumber = PhoneNumber(address, 0, "", address)
            val conversationGEt = context.getConversations(threadId).firstOrNull()
//             senderNameNew = conversationGEt.title
            Log.d("", "onReceive: originatingAddress <----------> 26 ${threadId}")
            isnewmwssage = messageNotify != threadId.toString()
//            isnewmwssage = messageNotify != phoneNumber.normalizedNumber

            val list = messagerDatabaseRepo.getUserMessageListrepo(threadId)

            isarchiv = if (list.isEmpty()) {
                false
            } else {
                list[0].isarchived
            }

            isPrivateChat = if (list.isEmpty()) {
                false
            } else {
                list[0].isPrivateChat
            }

            catname = if (list.isEmpty()) {
                null
            } else {
                list[0].CategoryName
            }

            try {
                senderNameNew = if (list.isEmpty()) {
                    if (conversationGEt?.title != null) {
                        Log.d("", "handleMessage: senderNameNew <---------> 1")
                        conversationGEt.title
                    } else {
                        val redata =
                            messagerDatabaseRepo.getUserMessageRecyListChackrepo(phoneNumber.normalizedNumber)
                        if (redata.isEmpty()) {
                            val privateCursor = context.getMyContactsCursor2(
                                favoritesOnly = false,
                                withPhoneNumbersOnly = true
                            )
                            context.getNameFromAddress(address, privateCursor)
                                ?: phoneNumber.normalizedNumber
                        } else {
                            if (redata[0].title != null) {
                                redata[0].title!!
                            } else {
                                val privateCursor = context.getMyContactsCursor2(
                                    favoritesOnly = false,
                                    withPhoneNumbersOnly = true
                                )
                                context.getNameFromAddress(address, privateCursor)
                                    ?: phoneNumber.normalizedNumber
                            }
                        }
                    }
                } else {
                    Log.d("", "handleMessage: senderNameNew <---------> 3")
                    list[0].title
                }
            } catch (e: Exception) {
                senderNameNew = phoneNumber.normalizedNumber
            }

            Log.d("", "handleMessage: senderNameNew <---------> 4 ${senderNameNew}")


            try {
                val mess = findOtpInString(body)
                if (mess.isEmpty()) {
                    messagetype = "normalmessage"
                } else {
                    messagetype = "otp"
                    messageotp = mess
                }
            } catch (_: Exception) {

            }


            if (list.isNotEmpty()) {
                isUserNotificationShow = messagerDatabaseRepo.isUserNotificationshowRepo(threadId!!)
            }

            this.conversation = Conversation(
                0,
                date.toString(),
                false,
                senderNameNew,
                null,
                false,
                phoneNumber.normalizedNumber,
                body.toString(),
                date.toLong(),
                type,
                senderNameNew.isDigitsOnly(),
                null,
                isnewmwssage,
                if (isnewmwssage) {
                    1
                } else {
                    0
                },
                messageId = newMessageId,
                threadId = threadId,
                isarchived = isarchiv,
                messagetype = messagetype,
                messageotp = messageotp,
                isPrivateChat = isPrivateChat,
                shownotification = isUserNotificationShow,
                messagetraslateshow = true,
                CategoryName = catname
            )
            Log.d("", "onReceive: originatingAddress <----------> 26 ${threadId}")
            val messid = messagerDatabaseRepo.insertmessage(this.conversation)
//            context.messagetraslateset(this.conversation)

            if (messageNotify != threadId.toString()) {
                Log.d("", "handleMessage: messageNotify <-------------> 1")
                Log.d(
                    "",
                    "handleMessage: messageNotify <-------------> 11 <----------> ${messageNotify}"
                )
                Log.d(
                    "",
                    "handleMessage: messageNotify <-------------> 111 <-----------> ${phoneNumber.normalizedNumber}"
                )
//                context.broadcastUpdateWidgetState()
//                val otp = findOtpInString(body)
//                if (otp.isEmpty()) {
//                    context.showReceivedMessageNotification(phoneNumber.normalizedNumber, body, threadId, bitmap)
//                } else {

//                }
                val isUserNotificationShow =
                    messagerDatabaseRepo.isUserNotificationshowRepo(threadId!!)

                if (isUserNotificationShow) {
                    if (context.config.isnotificationshow) {
                        chack = messagerDatabaseRepo.isPrivacyChatExitsRepo(threadId)
                        if (!chack) {
                            context.showReceivedMessageNotification(
                                phoneNumber.normalizedNumber,
                                body,
                                threadId,
                                bitmap,
                                messid
                            )
                        } else {
                            notificationProvider.postNotificaitonprivatechat(
                                senderNameNew,
                                "New Message",
                                phoneNumber.normalizedNumber,
                                threadId,
                                bitmap
                            )
                        }
                    }
                }
            } else {
                Log.d("", "handleMessage: messageNotify <-------------> 2")
                val myProcess = RunningAppProcessInfo()
                ActivityManager.getMyMemoryState(myProcess)
                val isInBackground =
                    myProcess.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                if (isInBackground) {
//                    context.broadcastUpdateWidgetState()
                    val isUserNotificationShow =
                        messagerDatabaseRepo.isUserNotificationshowRepo(threadId!!)
                    if (isUserNotificationShow) {
                        if (context.config.isnotificationshow) {
                            chack = messagerDatabaseRepo.isPrivacyChatExitsRepo(threadId)
                            if (!chack) {
                                context.showReceivedMessageNotification(
                                    phoneNumber.normalizedNumber,
                                    body,
                                    threadId,
                                    bitmap,
                                    messid
                                )
                            } else {
                                notificationProvider.postNotificaitonprivatechat(
                                    senderNameNew,
                                    "New Message",
                                    phoneNumber.normalizedNumber,
                                    threadId,
                                    bitmap
                                )
                            }
                        }
                    }
                }
            }
            if (context.config.isdringmodeone) {
//                delay(1000)
                setdrivingmodeonautoreply(this.conversation, context)
            }

        }
    }

    suspend fun setdrivingmodeonautoreply(conversation: Conversation, context: Context) {
        getavailablesimcard(conversation, context)
        val subscriptionId = availableSIMCards.getOrNull(currentSIMCardIndex)?.subscriptionId
            ?: SmsManager.getDefaultSmsSubscriptionId()
        sendSMSManager.sendMessageCompat(
            context.config.drivingmodemessage,
            listOf(conversation.phoneNumber),
            subscriptionId,
            arrayListOf(),
            isgroupmessage = conversation.isgroupmessage
        )
    }


    suspend fun getavailablesimcard(conversation: Conversation, context: Context) {
        val phoneNumber = conversation.phoneNumber?.let {
            com.simplemobiletools.commons.models.PhoneNumber(
                it,
                0,
                "",
                it
            )
        }
        val contact = conversation.title?.let {
            SimpleContact(
                0,
                0,
                it,
                "",
                arrayListOf(phoneNumber!!),
                ArrayList(),
                ArrayList()
            )
        }
        if (contact != null) {
            participants.add(contact)
        }
        setupSIMSelector(context)
    }

    @SuppressLint("MissingPermission")
    suspend fun setupSIMSelector(context: Context) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val availableSIMs = context.subscriptionManagerCompat().activeSubscriptionInfoList ?: return
        if (availableSIMs.size > 1) {
            availableSIMs.forEachIndexed { index, subscriptionInfo ->
                var label = subscriptionInfo.displayName?.toString() ?: ""
                if (subscriptionInfo.number?.isNotEmpty() == true) {
                    label += " (${subscriptionInfo.number})"
                }
                val simCard = SIMCard(index + 1, subscriptionInfo.subscriptionId, label)
                availableSIMCards.add(simCard)
            }
            val numbers = ArrayList<String>()
            participants.forEach { contact ->
                contact.phoneNumbers.forEach {
                    numbers.add(it.normalizedNumber)
                }
            }
            currentSIMCardIndex = getProperSimIndex(availableSIMs, numbers, context, conversation)
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getProperSimIndex(
        availableSIMs: MutableList<SubscriptionInfo>,
        numbers: List<String>,
        context: Context,
        conversation: Conversation,
    ): Int {
        delay(10)
        val userPreferredSimId = context.config.getUseSIMIdAtNumber(numbers.first())
        val userPreferredSimIdx =
            availableSIMs.indexOfFirstOrNull { it.subscriptionId == userPreferredSimId }

        messages = context.getMessages(conversation.threadId!!, false)
        val lastMessage = messages.lastOrNull()
        val senderPreferredSimIdx = if (lastMessage?.type == 1) {
            availableSIMs.indexOfFirstOrNull { it.subscriptionId == lastMessage.subscriptionId }
        } else {
            null
        }

        val defaultSmsSubscriptionId = SmsManager.getDefaultSmsSubscriptionId()
        val systemPreferredSimIdx = if (defaultSmsSubscriptionId >= 0) {
            availableSIMs.indexOfFirstOrNull { it.subscriptionId == defaultSmsSubscriptionId }
        } else {
            null
        }
        return userPreferredSimIdx ?: senderPreferredSimIdx ?: systemPreferredSimIdx ?: 0
    }

    private fun isMessageFilteredOut(context: Context, body: String): Boolean {
        for (blockedKeyword in context.config.blockedKeywords) {
            if (body.contains(blockedKeyword, ignoreCase = true)) {
                return true
            }
        }

        return false
    }

}