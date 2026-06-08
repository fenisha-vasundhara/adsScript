package com.messenger.phone.number.text.sms.service.apps.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Telephony
import android.telephony.SmsManager
import android.telephony.SubscriptionInfo
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.klinker.android.send_message.MmsReceivedReceiver
import com.messenger.phone.number.text.sms.service.apps.ApplicationClass.MessagerApplication.Companion.singleton
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.findOtpInString
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getAddresses
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getContactNamesFromNumbers
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getLatestMMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getMessages
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getThreadTitle
import com.messenger.phone.number.text.sms.service.apps.CommanClass.indexOfFirstOrNull
import com.messenger.phone.number.text.sms.service.apps.CommanClass.messageNotify
import com.messenger.phone.number.text.sms.service.apps.CommanClass.showReceivedMessageNotification
import com.messenger.phone.number.text.sms.service.apps.CommanClass.subscriptionManagerCompat
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.data.AddressBookContact
import com.messenger.phone.number.text.sms.service.apps.data.Message
import com.messenger.phone.number.text.sms.service.apps.data.SIMCard
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.simplemobiletools.commons.extensions.getBlockedNumbers
import com.simplemobiletools.commons.extensions.isNumberBlocked
import com.simplemobiletools.commons.extensions.normalizePhoneNumber
import com.simplemobiletools.commons.extensions.showErrorToast
import com.simplemobiletools.commons.models.SimpleContact
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MMSReceiver : MmsReceivedReceiver() {
    private var isnewmwssage: Boolean = false
    var messagestatus: String = "SMS delivered"
    var messagetype: String = "normalmessage"
    private var isUserNotificationShow: Boolean = true
    private var senderNameNew: String = "Unknown"
    var messageotp: String? = null
    var gropname: String? = null
    val list2 = arrayListOf<AddressBookContact>()
    var list = arrayListOf<Conversation>()
    private var isarchiv: Boolean = false
    private var isPrivateChat: Boolean = false
    private var catname: String? = null
    private var chack: Boolean = false
    private lateinit var conversation: Conversation
    private val availableSIMCards = ArrayList<SIMCard>()
    private var messages = ArrayList<Message>()
    private var currentSIMCardIndex = 0

    private var participants: ArrayList<SimpleContact> = arrayListOf()

    override fun isAddressBlocked(context: Context, address: String): Boolean {
        val normalizedAddress = address.normalizePhoneNumber()
        return context.isNumberBlocked(normalizedAddress)
    }


    override fun onMessageReceived(context: Context, uri: Uri) {
        if (context.packageName != Telephony.Sms.getDefaultSmsPackage(context)) {
            return
        }
        val message = context.getLatestMMS() ?: return
        val address = message.getSender()?.phoneNumbers?.first()?.normalizedNumber ?: ""
        val size = context.resources.getDimension(R.dimen.notification_large_icon_size).toInt()

        CoroutineScope(Dispatchers.IO).launch {
            val glideBitmap = try {
                Glide.with(context)
                    .asBitmap()
                    .load(message.attachment!!.attachments.first().getUri())
                    .centerCrop()
                    .into(size, size)
                    .get()
            } catch (e: Exception) {
                null
            }

            Handler(Looper.getMainLooper()).post {
                if (message != null) {
                    if (message.participants.isNotEmpty()) {
                        val blockedNumbers = context.getBlockedNumbers()
                        val phonenumber = message.participants.getAddresses()
                        val name = message.participants.getThreadTitle()
                        val isNewUserMessage = !message.read
                        val isBlocked =
                            context.isNumberBlocked(phonenumber.joinToString(), blockedNumbers)
                        val isNewUserMessageCont = if (isNewUserMessage) 1 else 0
                        val isgropmessage = phonenumber.size >= 2

                        CoroutineScope(Dispatchers.IO).launch {
                            isnewmwssage = messageNotify != message.threadId.toString()
                            list = ArrayList(singleton?.getMessageData(threadId = message.threadId))
                        }


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

                        senderNameNew = if (list.isEmpty()) {
                            name
                        } else {
                            list[0].title
                        }



                        if (isgropmessage) {
                            val longIdArray: LongArray =
                                phonenumber.mapNotNull { it.toLongOrNull() }.toLongArray()
                            gropname = try {
                                val contactNames =
                                    context.getContactNamesFromNumbers(longIdArray, list2)
                                contactNames.joinToString()
                            } catch (_: Exception) {
                                "No"
                            }
                        }

                        try {
                            val mess = findOtpInString(message.body)
                            if (mess.isEmpty()) {
                                messagetype = "normalmessage"
                            } else {
                                messagetype = "otp"
                                messageotp = mess
                            }
                        } catch (_: Exception) {

                        }

                        if (list.isNotEmpty()) {
                            CoroutineScope(Dispatchers.IO).launch {
                                isUserNotificationShow =
                                    singleton?.isUserNotificationshowRepoMMS(message.threadId) == true
                            }
                        }

                        when (message.status) {
                            -1 -> {
                                messagestatus = "SMS delivered"
                            }

                            0 -> {
                                messagestatus = "SMS delivered"
                            }

                            32 -> {
                                messagestatus = "Sending"
                            }

                            64 -> {
                                messagestatus = "Error"
                            }

                            else -> {
                                messagestatus = "SMS delivered"
                            }
                        }


                        conversation = Conversation(
                            0,
                            message.date.toString(),
                            true,
                            senderNameNew,
                            null,
                            false,
                            phonenumber.joinToString(),
                            message.body,
                            message.date.toLong(),
                            message.type,
                            true,
                            messagestatus,
                            messageId = message.id,
                            threadId = message.threadId,
                            isblocknumber = isBlocked,
                            messagetype = messagetype,
                            messageotp = messageotp,
                            isnewmessage = isNewUserMessage,
                            newMessageCount = isNewUserMessageCont,
                            isgroupmessage = isgropmessage,
                            groupName = gropname,
                            messagewithattachment = message.attachment,
                            isarchived = isarchiv,
                            isPrivateChat = isPrivateChat,
                            shownotification = isUserNotificationShow,
                            messagetraslateshow = true,
                            CategoryName = catname
                        )

                        CoroutineScope(Dispatchers.IO).launch {
                            singleton?.insertmessageMMS(conversation)
                        }

                    }
                }

                if (messageNotify != message.threadId.toString()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val isUserNotificationShow =
                            singleton?.isUserNotificationshowRepoMMS(message.threadId)

                        if (isUserNotificationShow == true) {
                            if (context.config.isnotificationshow) {
                                chack =
                                    singleton?.isPrivacyChatExitsRepoMMS(message.threadId!!) == true
                                if (!chack) {
                                    context.showReceivedMessageNotification(
                                        message.id,
                                        address,
                                        message.body,
                                        message.threadId,
                                        glideBitmap
                                    )
                                } else {
                                    singleton?.postNotificaitonprivatechat(
                                        senderNameNew,
                                        "New Message",
                                        address,
                                        message.threadId,
                                        glideBitmap
                                    )
                                }
                            }
                        }

                    }

                } else {
                    val myProcess = RunningAppProcessInfo()
                    ActivityManager.getMyMemoryState(myProcess)
                    val isInBackground =
                        myProcess.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    if (isInBackground) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val isUserNotificationShow =
                                singleton?.isUserNotificationshowRepoMMS(message.threadId)
                            if (isUserNotificationShow == true) {
                                if (context.config.isnotificationshow) {
                                    chack =
                                        singleton?.isPrivacyChatExitsRepoMMS(message.threadId!!) == true
                                    if (!chack) {
                                        context.showReceivedMessageNotification(
                                            message.id,
                                            address,
                                            message.body,
                                            message.threadId,
                                            glideBitmap
                                        )
                                    } else {
                                        singleton?.postNotificaitonprivatechat(
                                            senderNameNew,
                                            "New Message",
                                            address,
                                            message.threadId,
                                            glideBitmap
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (context.config.isdringmodeone) {
                    CoroutineScope(Dispatchers.IO).launch {
                        setdrivingmodeonautoreply(conversation, context)
                    }
                }

            }
        }

    }

    suspend fun setdrivingmodeonautoreply(conversation: Conversation, context: Context) {
        getavailablesimcard(conversation, context)
        val subscriptionId = availableSIMCards.getOrNull(currentSIMCardIndex)?.subscriptionId
            ?: SmsManager.getDefaultSmsSubscriptionId()
        singleton?.sendMessageCompat(
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


    override fun onError(context: Context, error: String) =
        context.showErrorToast("Couldn't download MMS")
}
