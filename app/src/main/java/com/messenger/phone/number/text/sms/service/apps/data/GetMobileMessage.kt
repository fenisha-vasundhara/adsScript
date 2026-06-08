package com.messenger.phone.number.text.sms.service.apps.data

import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.Telephony
import android.util.Log
import android.util.LongSparseArray
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.text.isDigitsOnly
import com.demo.adsmanage.helper.logD
import com.messenger.phone.number.text.sms.service.apps.CommanClass.findOtpInString
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getAddresses
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getContactNamesFromNumbers
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getMMS2
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getSmsCount
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getThreadTitle
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isPhoneNumber
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isBlockedNumberPattern
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isfastcrollerishide
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ismessageloading
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setIsFistTimeMessageget
import com.messenger.phone.number.text.sms.service.apps.CommanClass.trimToComparableNumber
import com.messenger.phone.number.text.sms.service.apps.CommanClass.totalgetmessage
import com.messenger.phone.number.text.sms.service.apps.CommanClass.totalmessagecount
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.combinedListAttachment
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.getAllImagesAndVideosSortedByRecentNew
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.getRecentImage
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.imageuriGalleryFirstimage
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.loadData
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.modelclass.Foldermodel
import com.messenger.phone.number.text.sms.service.apps.DAOClass.ThreadInfo
import com.messenger.phone.number.text.sms.service.apps.modelClass.Contact
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.simplemobiletools.commons.extensions.getBlockedNumbers
import com.simplemobiletools.commons.extensions.isNumberBlocked
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.HashSet
import java.util.Locale
import javax.inject.Inject


class GetMobileMessage @Inject constructor(@ApplicationContext var context: Context) {


    private var isNewUserMessage: Boolean = false
    private var photouri: List<NamePhoto> = arrayListOf()
    private lateinit var phonrnumber: String
    val list = ArrayList<Contact>()
    private val contactCache = HashMap<String, String>()
    var onCompleted: (() -> Unit)? = null
    var onProssageStart: (() -> Unit)? = null
    var gropname: String? = null


    var albumes: HashMap<File, List<File>>? = null

    @Inject
    lateinit var messagerDatabaseRepo: MessagerDatabaseRepo

    var conList: ArrayList<Conversation> = arrayListOf()
    var photouriget: ArrayList<NamePhoto> = arrayListOf()
    var exitconList: ArrayList<Conversation> = arrayListOf()
    var photourl: ArrayList<Conversation> = arrayListOf()
    private var alredyincountact: ArrayList<Conversation> = arrayListOf()

    var messagestatus: String = "SMS delivered"
    var messagetype: String = "normalmessage"
    var messageotp: String? = null
    var folderimage: ArrayList<Foldermodel> = arrayListOf()

    private suspend fun refreshAttachmentCache() {
        val latestMedia = ArrayList(context.getAllImagesAndVideosSortedByRecentNew())
        combinedListAttachment = latestMedia
        imageuriGalleryFirstimage = context.getRecentImage()
    }

    @SuppressLint("Recycle", "Range")
    suspend fun refreshSmsInbox1() {
        logD("Kpdfbdfb", "1")
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        logD("Kpdfbdfb", "2")
        if (checkPermissions()) {
            context.loadData()
            refreshAttachmentCache()
        }
        logD("Kpdfbdfb", "3")
        totalmessagecount = getSmsCount(context.contentResolver, context)
        conList.clear()
        exitconList.clear()
        alredyincountact.clear()
        ismessageloading = true
        setIsFistTimeMessageget(true, context)
        val list = arrayListOf<AddressBookContact>()
        val array: LongSparseArray<AddressBookContact> = LongSparseArray<AddressBookContact>()
        val projection2 = arrayOf(
            ContactsContract.Data.MIMETYPE,
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Contactables.DATA,
            ContactsContract.CommonDataKinds.Contactables.TYPE
        )
        val selection = ContactsContract.Data.MIMETYPE + " in (?, ?)"
        val selectionArgs = arrayOf(
            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
        )
        val sortOrder2 = ContactsContract.Contacts.SORT_KEY_ALTERNATIVE
        val uri = ContactsContract.CommonDataKinds.Contactables.CONTENT_URI
        val cursor =
            context.contentResolver.query(uri, projection2, selection, selectionArgs, sortOrder2)
        val idIdx = cursor?.getColumnIndex(ContactsContract.Data.CONTACT_ID) ?: 0
        val nameIdx = cursor?.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME) ?: 0
        val dataIdx =
            cursor?.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.DATA) ?: 0
        val typeIdx =
            cursor?.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.TYPE) ?: 0
        logD("Kpdfbdfb", "4-")
        while (cursor?.moveToNext() == true) {
            val id = cursor!!.getLong(idIdx)
            var addressBookContact: AddressBookContact? = array.get(id)
            if (addressBookContact == null) {
                addressBookContact =
                    AddressBookContact(id, cursor.getString(nameIdx), context.resources)
                array.put(id, addressBookContact)
                list.add(addressBookContact)

            }
            val type = cursor.getInt(typeIdx)
            val data = cursor.getString(dataIdx)
            addressBookContact.addPhone(type, data)
        }
        var lastUpdate = 0L
        logD("Kpdfbdfb", "44-")

        cursor?.close()
        val contentResolver: ContentResolver = context.contentResolver
        val projection = arrayOf(
            Telephony.Sms.DATE,
            Telephony.Sms.ADDRESS,
            Telephony.Sms.BODY,
            Telephony.Sms.TYPE,
            Telephony.Sms._ID,
            Telephony.Sms.THREAD_ID,
            Telephony.Sms.STATUS,
            Telephony.Sms.READ
        )
        try {
            logD("Kpdfbdfb", "5")
            val sortOrder = "${Telephony.Sms.DATE} DESC"
            contentResolver.query(Uri.parse("content://sms/"), projection, null, null, sortOrder)
                ?.apply {
                    val blockedNumbers = context.getBlockedNumbers()

                    logD("Kpdfbdfb", "7")
                    withContext(Dispatchers.IO) {
                        while (moveToNext()) {
                            val now = System.currentTimeMillis()
                            if (now - lastUpdate > 300) {
                                lastUpdate = now
//                            withContext(Dispatchers.Main) {
                                onProssageStart?.invoke()
//                            }
                            }
                            val smsaddress = getString(getColumnIndex(Telephony.Sms.ADDRESS)) ?: ""
                            val smsbody = getString(getColumnIndexOrThrow(Telephony.Sms.BODY)) ?: ""
                            val smsdate = getString(getColumnIndexOrThrow(Telephony.Sms.DATE)) ?: ""
                            val smsmessageid =
                                getString(getColumnIndexOrThrow(Telephony.Sms._ID)) ?: ""
                            val typeID = getString(getColumnIndexOrThrow(Telephony.Sms.TYPE)) ?: ""
                            val thread = getLong(getColumnIndexOrThrow(Telephony.Sms.THREAD_ID))
                            val status = getInt(getColumnIndexOrThrow(Telephony.Sms.STATUS))
                            val read = getInt(getColumnIndexOrThrow(Telephony.Sms.READ)) == 1

                            when (status) {
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
                            if (typeID.toInt() != 3) {
                                phonrnumber = smsaddress ?: ""
                                val number = phonrnumber
                                val numbersArray: LongArray = if (number.contains("|")) {
                                    try {
                                        number.split("|").map { it.toLong() }.toLongArray()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        longArrayOf() // Empty LongArray in case of error
                                    }
                                } else {
                                    try {
                                        longArrayOf(number.toLong())
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        longArrayOf()
                                    }
                                }
                                val isgropmessage = numbersArray.size >= 2
                                if (isgropmessage) {
                                    gropname = try {
                                        val contactNames =
                                            context.getContactNamesFromNumbers(numbersArray, list)
                                        contactNames.joinToString()
                                    } catch (_: Exception) {
                                        ""
                                    }
                                }
                                val valued = list.filter { it1 -> it1.phones == number }
                                val isBlocked = context.isNumberBlocked(number, blockedNumbers)
                                isNewUserMessage = !read
                                val isNewUserMessageCont = if (isNewUserMessage) 1 else 0
                                try {
                                    val mess = findOtpInString(smsbody)
                                    if (mess.isEmpty()) {
                                        messagetype = "normalmessage"
                                    } else {
                                        messagetype = "otp"
                                        messageotp = mess
                                    }
                                } catch (_: Exception) {
                                }
                                if (valued.isNotEmpty()) {
                                    smsaddress?.let {
                                        val conversation = Conversation(
                                            0,
                                            smsdate,
                                            true,
                                            valued[0].name,
                                            null,
                                            false,
                                            number,
                                            smsbody,
                                            smsdate.toLong(),
                                            typeID.toInt(),
                                            true,
                                            messagestatus,
                                            messageId = smsmessageid.toLong(),
                                            threadId = thread,
                                            isblocknumber = isBlocked,
                                            messagetype = messagetype,
                                            messageotp = messageotp,
                                            isnewmessage = isNewUserMessage,
                                            newMessageCount = isNewUserMessageCont,
                                            isgroupmessage = isgropmessage,
                                            groupName = gropname
                                        )

                                        conList.add(conversation)
                                    }
                                } else if (number.isPhoneNumber()) {
                                    if (number.length <= 7) {
                                        smsaddress?.let {
                                            val conversation = Conversation(
                                                0,
                                                smsdate,
                                                true,
                                                number,
                                                null,
                                                false,
                                                phonrnumber,
                                                smsbody,
                                                smsdate.toLong(),
                                                typeID.toInt(),
                                                true,
                                                messagestatus,
                                                messageId = smsmessageid.toLong(),
                                                threadId = thread,
                                                isblocknumber = isBlocked,
                                                messagetype = messagetype,
                                                messageotp = messageotp,
                                                isnewmessage = isNewUserMessage,
                                                newMessageCount = isNewUserMessageCont,
                                                isgroupmessage = isgropmessage,
                                                groupName = gropname
                                            )
                                            conList.add(conversation)
                                        }
                                    } else {
                                        val isalredyincontact =
                                            alredyincountact.filter { it.phoneNumber == phonrnumber }
                                        if (isalredyincontact.isEmpty()) {
                                            smsaddress?.let {
                                                val conversation = Conversation(
                                                    0,
                                                    smsdate,
                                                    true,
                                                    getContactName(number, context)!!,
                                                    null,
                                                    false,
                                                    phonrnumber,
                                                    smsbody,
                                                    smsdate.toLong(),
                                                    typeID.toInt(),
                                                    true,
                                                    messagestatus,
                                                    messageId = smsmessageid.toLong(),
                                                    threadId = thread,
                                                    isblocknumber = isBlocked,
                                                    messagetype = messagetype,
                                                    messageotp = messageotp,
                                                    isnewmessage = isNewUserMessage,
                                                    newMessageCount = isNewUserMessageCont,
                                                    isgroupmessage = isgropmessage,
                                                    groupName = gropname
                                                )
                                                Log.d("TAG", "refreshSmsInbox: <------> 3 <----->")
                                                alredyincountact.add(conversation)
                                                conList.add(conversation)
                                            }
                                        } else {
                                            smsaddress?.let {
                                                val conversation = Conversation(
                                                    0,
                                                    smsdate,
                                                    true,
                                                    isalredyincontact[0].title,
                                                    null,
                                                    false,
                                                    phonrnumber,
                                                    smsbody,
                                                    smsdate.toLong(),
                                                    typeID.toInt(),
                                                    true,
                                                    messagestatus,
                                                    messageId = smsmessageid.toLong(),
                                                    threadId = thread,
                                                    isblocknumber = isBlocked,
                                                    messagetype = messagetype,
                                                    messageotp = messageotp,
                                                    isnewmessage = isNewUserMessage,
                                                    newMessageCount = isNewUserMessageCont,
                                                    isgroupmessage = isgropmessage,
                                                    groupName = gropname
                                                )
                                                conList.add(conversation)
                                            }
                                        }
                                    }
                                } else {
                                    smsaddress?.let {
                                        val conversation = Conversation(
                                            0,
                                            smsdate,
                                            true,
                                            number,
                                            null,
                                            false,
                                            phonrnumber,
                                            smsbody,
                                            smsdate.toLong(),
                                            typeID.toInt(),
                                            true,
                                            messagestatus,
                                            messageId = smsmessageid.toLong(),
                                            threadId = thread,
                                            isblocknumber = isBlocked,
                                            messagetype = messagetype,
                                            messageotp = messageotp,
                                            isnewmessage = isNewUserMessage,
                                            newMessageCount = isNewUserMessageCont,
                                            isgroupmessage = isgropmessage,
                                            groupName = gropname
                                        )
                                        conList.add(conversation)
                                    }
                                }
                            }
                            totalgetmessage = conList.size
                        }
                        onProssageStart?.invoke()
                        logD("Kpdfbdfb", "7-1")
                        val allmms = context.getMMS2()
                        allmms.forEachIndexed { index, message ->
                            val phonenumber = message.participants.getAddresses()
                            val name = message.participants.getThreadTitle()
                            isNewUserMessage = !message.read
                            val isBlocked =
                                context.isNumberBlocked(phonenumber.joinToString(), blockedNumbers)
                            val isNewUserMessageCont = if (isNewUserMessage) 1 else 0


                            val isgropmessage = phonenumber.size >= 2
                            if (isgropmessage) {
                                val longIdArray: LongArray =
                                    phonenumber.mapNotNull { it.toLongOrNull() }.toLongArray()
                                gropname = try {
                                    val contactNames =
                                        context.getContactNamesFromNumbers(longIdArray, list)
                                    contactNames.joinToString()
                                } catch (_: Exception) {
                                    ""
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
                            val conversation = Conversation(
                                0,
                                message.date.toString(),
                                true,
                                name,
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
                                messagewithattachment = message.attachment
                            )
                            conList.add(conversation)
                        }
                        logD("Kpdfbdfb", "7-2")
                    }
                    conList.toList().forEachIndexed { index, conversation ->
                        conversation.messageId?.let {
                            if (!messagerDatabaseRepo.isMessageExitsRepo(it)) {
                                val data = conversation.threadId?.let { it1 ->
                                    messagerDatabaseRepo.getUserMessageListChackrepo(it1)
                                }
                                if (conversation.snippet.isNotEmpty() || conversation.messagewithattachment?.attachments?.isNotEmpty() == true) {
                                    if (data?.isNotEmpty() == true) {
                                        val isgropmessage = data[0].isgroupmessage
                                        if (isgropmessage) {
                                            val conversation = Conversation(
                                                0,
                                                conversation.date,
                                                true,
                                                data[0].title,
                                                null,
                                                false,
                                                data[0].phoneNumber,
                                                conversation.snippet,
                                                conversation.date.toLong(),
                                                conversation.type,
                                                true,
                                                conversation.messageStatus,
                                                messageId = conversation.messageId,
                                                threadId = conversation.threadId,
                                                isblocknumber = conversation.isblocknumber,
                                                messagetype = messagetype,
                                                messageotp = messageotp,
                                                isnewmessage = isNewUserMessage,
                                                newMessageCount = conversation.newMessageCount,
                                                isgroupmessage = isgropmessage,
                                                groupName = data[0].groupName,
                                                messagewithattachment = conversation.messagewithattachment
                                            )
                                            exitconList.add(conversation)
                                        } else {
                                            val conversationNew = Conversation(
                                                0,
                                                conversation.date,
                                                true,
                                                data[0].title,
                                                null,
                                                false,
                                                conversation.phoneNumber,
                                                conversation.snippet,
                                                conversation.date.toLong(),
                                                conversation.type,
                                                conversation.isnumaric,
                                                conversation.messageStatus,
                                                messageId = conversation.messageId,
                                                threadId = conversation.threadId,
                                                isblocknumber = conversation.isblocknumber,
                                                messagetype = conversation.messagetype,
                                                messageotp = conversation.messageotp,
                                                isnewmessage = conversation.isnewmessage,
                                                newMessageCount = conversation.newMessageCount,
                                                isgroupmessage = isgropmessage,
                                                groupName = conversation.groupName,
                                                messagewithattachment = conversation.messagewithattachment
                                            )
                                            exitconList.add(conversationNew)
                                        }
                                    } else {
                                        exitconList.add(conversation)
                                    }
                                }
                            }
                        }
                    }
                    logD("Kpdfbdfb", "8 exitconList:${exitconList.size}")
                    messagerDatabaseRepo.insertOrUpdateList(exitconList)
                    ismessageloading = false
                    isfastcrollerishide = true
                    withContext(Dispatchers.Main) {
                        onProssageStart?.invoke()
                        onCompleted?.invoke()
                        setIsFistTimeMessageget(false, context)
                    }
                }
        } catch (e: Exception) {
            logD("Kpdfbdfb", "6 $e")
        }
    }

    @SuppressLint("Range")
    suspend fun refreshSmsInbox() = withContext(Dispatchers.IO) {
        logD("Kpdfbdfb", "1")

        exitconList.clear()
        // --- Permissions -----------------------------------------------------------------
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            logD("Kpdfbdfb", "no READ_CONTACTS permission, abort")
            return@withContext
        }

        logD("Kpdfbdfb", "2")

        // --- Initial state / gallery data (heavy: keep on IO) ----------------------------
        if (checkPermissions()) {
            context.loadData()
            refreshAttachmentCache()
        }
        logD("Kpdfbdfb", "3")

        totalmessagecount = getSmsCount(context.contentResolver, context)
        conList.clear()
        alredyincountact.clear()
        ismessageloading = true
//        setIsFistTimeMessageget(true, context)

        // --- Load contacts once, on IO ---------------------------------------------------
        val contacts = ArrayList<AddressBookContact>()
        val contactsById = LongSparseArray<AddressBookContact>()

        val projectionContacts = arrayOf(
            ContactsContract.Data.MIMETYPE,
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Contactables.DATA,
            ContactsContract.CommonDataKinds.Contactables.TYPE
        )

        val selection = ContactsContract.Data.MIMETYPE + " in (?, ?)"
        val selectionArgs = arrayOf(
            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
        )

        val sortOrderContacts = ContactsContract.Contacts.SORT_KEY_ALTERNATIVE
        val contactsUri = ContactsContract.CommonDataKinds.Contactables.CONTENT_URI

        val contactsCursor = context.contentResolver.query(
            contactsUri,
            projectionContacts,
            selection,
            selectionArgs,
            sortOrderContacts
        )

        val idIdx = contactsCursor?.getColumnIndex(ContactsContract.Data.CONTACT_ID) ?: -1
        val nameIdx = contactsCursor?.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME) ?: -1
        val dataIdx = contactsCursor?.getColumnIndex(
            ContactsContract.CommonDataKinds.Contactables.DATA
        ) ?: -1
        val typeIdx = contactsCursor?.getColumnIndex(
            ContactsContract.CommonDataKinds.Contactables.TYPE
        ) ?: -1

        logD("Kpdfbdfb", "4-")

        if (contactsCursor != null && idIdx != -1 && nameIdx != -1 &&
            dataIdx != -1 && typeIdx != -1
        ) {
            while (contactsCursor.moveToNext()) {
                val id = contactsCursor.getLong(idIdx)
                var addressBookContact = contactsById.get(id)

                if (addressBookContact == null) {
                    val rawName = contactsCursor.getString(nameIdx)
                    val safeName = rawName ?: "Unknown"

                    addressBookContact =
                        AddressBookContact(id, safeName, context.resources)
                    contactsById.put(id, addressBookContact)
                    contacts.add(addressBookContact)
                }

                val type = contactsCursor.getInt(typeIdx)
                val data = contactsCursor.getString(dataIdx)
                addressBookContact.addPhone(type, data)
            }
        }

        contactsCursor?.close()

        // Build a fast lookup map: phone number -> contact name
        val phoneToName = HashMap<String, String>(contacts.size * 2)
        contacts.forEach { contact ->
            val phone = contact.phones
            if (!phone.isNullOrEmpty()) {
                phoneToName[phone] = contact.name
            }
        }

        logD("Kpdfbdfb", "44-")

        // --- SMS query -------------------------------------------------------------------
        val contentResolver: ContentResolver = context.contentResolver
        val projectionSms = arrayOf(
            Telephony.Sms.DATE,
            Telephony.Sms.ADDRESS,
            Telephony.Sms.BODY,
            Telephony.Sms.TYPE,
            Telephony.Sms._ID,
            Telephony.Sms.THREAD_ID,
            Telephony.Sms.STATUS,
            Telephony.Sms.READ
        )

        var lastUpdate = 0L

        try {
            logD("Kpdfbdfb", "5")

            val sortOrderSms = "${Telephony.Sms.DATE} DESC"
            val blockedNumbers = context.getBlockedNumbers()
            val blockedDirect = HashSet<String>(blockedNumbers.size * 2).apply {
                blockedNumbers.forEach {
                    add(it.numberToCompare)
                    add(it.number)
                }
            }
            val blockedPatterns = blockedNumbers.filter { it.number.isBlockedNumberPattern() }

            fun isBlockedFast(number: String): Boolean {
                val comparable = number.trimToComparableNumber()
                if (blockedDirect.contains(comparable) || blockedDirect.contains(number)) {
                    return true
                }
                return blockedPatterns.any { blocked ->
                    val num = blocked.number
                    val pattern = num.replace("+", "\\+").replace("*", ".*")
                    number.matches(pattern.toRegex())
                }
            }

            contentResolver.query(
                Uri.parse("content://sms/"),
                projectionSms,
                null,
                null,
                sortOrderSms
            )?.use { cursor ->

                logD("Kpdfbdfb", "7")



                // Already on IO dispatcher (because of withContext(IO))
                while (cursor.moveToNext()) {

                    // Throttled progress callback (optional)
                    val now = System.currentTimeMillis()
                    if (now - lastUpdate > 300) {
                        lastUpdate = now
                        onProssageStart?.invoke()
                    }

                    val smsAddress = cursor.getString(
                        cursor.getColumnIndex(Telephony.Sms.ADDRESS)
                    ) ?: ""

                    // promotional hide


                    if (smsAddress.contains("@BOT.RCS.GOOGLE.COM") ||
                        smsAddress.contains("@RCS.GOOGLE.COM") ||
                        smsAddress.contains("@bot.rcs.google.com") ||
                        smsAddress.endsWith(".RCS.GOOGLE.COM") ||
                        smsAddress.matches(Regex(".+@.+\\.GOOGLE\\.COM$"))) {
                        continue
                    }

                    val smsBody = cursor.getString(
                        cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)
                    ) ?: ""

                    val smsDate = cursor.getString(
                        cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)
                    ) ?: ""

                    val smsMessageId = cursor.getString(
                        cursor.getColumnIndexOrThrow(Telephony.Sms._ID)
                    ) ?: ""

                    val typeIdStr = cursor.getString(
                        cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE)
                    ) ?: "0"

                    val threadId = cursor.getLong(
                        cursor.getColumnIndexOrThrow(Telephony.Sms.THREAD_ID)
                    )

                    val status = cursor.getInt(
                        cursor.getColumnIndexOrThrow(Telephony.Sms.STATUS)
                    )

                    val read = cursor.getInt(
                        cursor.getColumnIndexOrThrow(Telephony.Sms.READ)
                    ) == 1

                    val typeId = runCatching { typeIdStr.toInt() }.getOrDefault(0)

                    // Ignore drafts like before
                    if (typeId == 3) continue

                    messagestatus = when (status) {
                        -1, 0 -> "SMS delivered"
                        32 -> "Sending"
                        64 -> "Error"
                        else -> "SMS delivered"
                    }

                    phonrnumber = smsAddress
                    val number = phonrnumber

                    // Group detection only when address contains multiple numbers
                    var isGroupMessage = false
                    if (number.contains("|")) {
                        val numbersArray: LongArray = runCatching {
                            number.split("|").mapNotNull { it.toLongOrNull() }.toLongArray()
                        }.getOrDefault(longArrayOf())
                        isGroupMessage = numbersArray.size >= 2
                        if (isGroupMessage) {
                            gropname = runCatching {
                                val contactNames =
                                    context.getContactNamesFromNumbers(numbersArray, contacts)
                                contactNames.joinToString()
                            }.getOrDefault("")
                        }
                    }

                    // Fast contact lookup
                    val contactName = phoneToName[number]

                    val isBlocked = isBlockedFast(number)
                    isNewUserMessage = !read
                    val isNewUserMessageCount = if (isNewUserMessage) 1 else 0

                    // OTP detection (kept)
                    try {
                        val mess = findOtpInString(smsBody)
                        if (mess.isEmpty()) {
                            messagetype = "normalmessage"
                        } else {
                            messagetype = "otp"
                            messageotp = mess
                        }
                    } catch (_: Exception) {
                        messagetype = "normalmessage"
                    }

                    fun buildConversation(
                        title: String,
                        phoneNumber: String
                    ): Conversation {
                        return Conversation(
                            0,
                            smsDate,
                            true,
                            title,
                            null,
                            false,
                            phoneNumber,
                            smsBody,
                            smsDate.toLong(),
                            typeId,
                            true,
                            messagestatus,
                            messageId = smsMessageId.toLong(),
                            threadId = threadId,
                            isblocknumber = isBlocked,
                            messagetype = messagetype,
                            messageotp = messageotp,
                            isnewmessage = isNewUserMessage,
                            newMessageCount = isNewUserMessageCount,
                            isgroupmessage = isGroupMessage,
                            groupName = gropname
                        )
                    }

                    if (!contactName.isNullOrEmpty()) {
                        conList.add(buildConversation(contactName, number))
                    }
                    else if (number.isPhoneNumber()) {
                        if (number.length <= 7) {
                            conList.add(buildConversation(number, phonrnumber))
                        } else {
                            val alreadyInContact =
                                alredyincountact.filter { it.phoneNumber == phonrnumber }
                            if (alreadyInContact.isEmpty()) {
                                val title = getContactName(number, context) ?: number
                                val conversation = buildConversation(title, phonrnumber)
                                Log.d("TAG", "refreshSmsInbox: <------> 3 <----->")
                                alredyincountact.add(conversation)
                                conList.add(conversation)
                            } else {
                                val conversation = buildConversation(
                                    alreadyInContact[0].title,
                                    phonrnumber
                                )
                                conList.add(conversation)
                            }
                        }
                    } else {
                        conList.add(buildConversation(number, phonrnumber))
                    }

                    totalgetmessage = conList.size
                } // end while SMS
            } // end use cursor

            // --- MMS ----------------------------------------------------------------------
            logD("Kpdfbdfb", "7-1")
            val allMms = context.getMMS2()
            allMms.forEach { message ->
                val phoneNumbers = message.participants.getAddresses()
                val name = message.participants.getThreadTitle()

                isNewUserMessage = !message.read
                val isBlocked =
                    context.isNumberBlocked(phoneNumbers.joinToString(), blockedNumbers)
                val isNewUserMessageCount = if (isNewUserMessage) 1 else 0

                val isGroupMessage = phoneNumbers.size >= 2
                if (isGroupMessage) {
                    val longIdArray: LongArray =
                        phoneNumbers.mapNotNull { it.toLongOrNull() }.toLongArray()
                    gropname = try {
                        val contactNames =
                            context.getContactNamesFromNumbers(longIdArray, contacts)
                        contactNames.joinToString()
                    } catch (_: Exception) {
                        ""
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
                    messagetype = "normalmessage"
                }

                messagestatus = when (message.status) {
                    -1, 0 -> "SMS delivered"
                    32 -> "Sending"
                    64 -> "Error"
                    else -> "SMS delivered"
                }

                val conversation = Conversation(
                    0,
                    message.date.toString(),
                    true,
                    name,
                    null,
                    false,
                    phoneNumbers.joinToString(),
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
                    newMessageCount = isNewUserMessageCount,
                    isgroupmessage = isGroupMessage,
                    groupName = gropname,
                    messagewithattachment = message.attachment
                )
                conList.add(conversation)
            }

            logD("Kpdfbdfb", "7-2")

            // --- Post-process & DB sync ---------------------------------------------------
            val existingMessageIds = HashSet<Long>(messagerDatabaseRepo.getAllMessageIdsRepo())
            val threadInfoMap: Map<Long, ThreadInfo> =
                messagerDatabaseRepo.getThreadInfoRepo().associateBy { it.threadId }

            conList.toList().forEach { conversation ->
                conversation.messageId?.let { msgId ->
                    if (!existingMessageIds.contains(msgId)) {
                        // Reuse thread metadata if we already have a thread row, but keep original flags safe
                        val threadMeta = conversation.threadId?.let { threadInfoMap[it] }
                        val convToInsert = threadMeta?.let { meta ->
                            val safeIsGroup = meta.isgroupmessage || conversation.isgroupmessage
                            val safeTitle = meta.title ?: conversation.title
                            val safePhone = meta.phoneNumber ?: conversation.phoneNumber
                            val safeGroupName = meta.groupName ?: conversation.groupName
                            conversation.copy(
                                title = safeTitle,
                                phoneNumber = safePhone,
                                isgroupmessage = safeIsGroup,
                                groupName = safeGroupName
                            )
                        } ?: conversation

                        if (convToInsert.snippet.isNotEmpty() ||
                            convToInsert.messagewithattachment?.attachments?.isNotEmpty() == true
                        ) {
                            exitconList.add(convToInsert)
                            existingMessageIds.add(msgId)
                        }
                    }
                }
            }

            logD("Kpdfbdfb", "8 exitconList:${exitconList.size}")

            messagerDatabaseRepo.insertOrUpdateList(exitconList)
            ismessageloading = false
            isfastcrollerishide = true

            // --- Final UI callbacks on Main ----------------------------------------------
            withContext(Dispatchers.Main) {
                onProssageStart?.invoke()
                onCompleted?.invoke()
                setIsFistTimeMessageget(false, context)
            }
        } catch (e: Exception) {
            logD("Kpdfbdfb", "6 $e")
        }
    }

    /**
     * Lightweight background sync without UI callbacks.
     * Mirrors latest SMS rows into our DB (no UI progress, safe if not default SMS).
     */
    @SuppressLint("Range")
    suspend fun realtimesynced(limit: Int = 200) = withContext(Dispatchers.IO) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) return@withContext
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED
        ) return@withContext

        val projectionSms = arrayOf(
            Telephony.Sms.DATE,
            Telephony.Sms.ADDRESS,
            Telephony.Sms.BODY,
            Telephony.Sms.TYPE,
            Telephony.Sms._ID,
            Telephony.Sms.THREAD_ID,
            Telephony.Sms.STATUS,
            Telephony.Sms.READ
        )

        val sortOrder = "${Telephony.Sms.DATE} DESC LIMIT $limit"
        val blockedNumbers = context.getBlockedNumbers()
        val newConversations = mutableListOf<Conversation>()

        fun statusToLabel(status: Int): String = when (status) {
            -1, 0 -> "SMS delivered"
            32 -> "Sending"
            64 -> "Error"
            else -> "SMS delivered"
        }

        try {
            context.contentResolver.query(
                Uri.parse("content://sms/"),
                projectionSms,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    val smsMessageId =
                        cursor.getString(cursor.getColumnIndex(Telephony.Sms._ID)) ?: continue
                    val messageIdLong = smsMessageId.toLongOrNull() ?: continue
                    if (messagerDatabaseRepo.isMessageExitsRepo(messageIdLong)) continue

                    val smsAddress =
                        cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS)) ?: ""
                    val smsBody =
                        cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)) ?: ""
                    val smsDate =
                        cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)) ?: "0"
                    val typeIdStr =
                        cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE)) ?: "0"
                    val threadId =
                        cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms.THREAD_ID))
                    val status =
                        cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.Sms.STATUS))
                    val read =
                        cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.Sms.READ)) == 1


                    if (typeIdStr.toIntOrNull() == 3) continue // skip drafts

                    val existingByThread =
                        messagerDatabaseRepo.getUserMessageListChackrepo(threadId)
                    val existingByNumber =
                        messagerDatabaseRepo.getUserMessageMobileListChackrepo(smsAddress)
                    val existing = existingByThread.firstOrNull() ?: existingByNumber.firstOrNull()

                    val isBlocked = context.isNumberBlocked(smsAddress, blockedNumbers)
                    val displayName =
                        existing?.title ?: getContactName(smsAddress, context) ?: smsAddress
                    val isGroup = smsAddress.contains("|")
                    val typeId = typeIdStr.toIntOrNull() ?: 0
                    val messageStatus = statusToLabel(status)
                    val otp = findOtpInString(smsBody)
                    val msgType = if (otp.isEmpty()) "normalmessage" else "otp"

                    val conv = Conversation(
                        id = 0,
                        date = smsDate,
                        read = read,
                        title = displayName,
                        photoUri = null,
                        usesCustomTitle = false,
                        phoneNumber = smsAddress,
                        snippet = smsBody,
                        time = smsDate.toLongOrNull(),
                        type = typeId,
                        isnumaric = smsAddress.isDigitsOnly(),
                        messageStatus = messageStatus,
                        isnewmessage = !read,
                        newMessageCount = if (!read) 1 else 0,
                        messageId = messageIdLong,
                        threadId = threadId,
                        isarchived = existing?.isarchived ?: false,
                        ispinned = existing?.ispinned ?: false,
                        pinneddate = existing?.pinneddate ?: 0L,
                        isblocknumber = isBlocked,
                        isexpandmessageview = true,
                        is_scheduled = false,
                        isMessagefound = false,
                        isPrivateChat = existing?.isPrivateChat ?: false,
                        draftmessage = null,
                        messagetype = msgType,
                        messageotp = otp,
                        shownotification = existing?.shownotification ?: !read,
                        messagetraslateshow = false,
                        messagetraslationanimationshow = false,
                        isgroupmessage = isGroup,
                        groupName = existing?.groupName ?: if (isGroup) smsAddress else null,
                        CategoryName = existing?.CategoryName,
                        isnewmessagescroll = false,
                        isonlyselectedthem = false,
                        themenumber = existing?.themenumber ?: 0,
                        customtimeuri = existing?.customtimeuri,
                        isbanneradshow = false,
                        messagewithattachment = null
                    )

                    newConversations.add(conv)
                }
            }

            // --- MMS (lightweight) -----------------------------------------------------------
            try {
                context.getMMS2().take(limit).forEach { message ->
                    val msgId = message.id ?: return@forEach
                    if (messagerDatabaseRepo.isMessageExitsRepo(msgId)) return@forEach

                    val phoneNumbers = message.participants.getAddresses()
                    val addressStr = phoneNumbers.joinToString()
                    val isGroup = phoneNumbers.size >= 2
                    val isBlocked = context.isNumberBlocked(addressStr, blockedNumbers)
                    val existingByThread = message.threadId?.let {
                        messagerDatabaseRepo.getUserMessageListChackrepo(it)
                    } ?: emptyList()
                    val existingByNumber =
                        messagerDatabaseRepo.getUserMessageMobileListChackrepo(addressStr)
                    val existing = existingByThread.firstOrNull() ?: existingByNumber.firstOrNull()

                    val displayName = existing?.title
                        ?: message.participants.getThreadTitle()
                        ?: addressStr

                    val otp = runCatching { findOtpInString(message.body) }.getOrDefault("")
                    val msgType = if (otp.isEmpty()) "normalmessage" else "otp"

                    val statusLabel = when (message.status) {
                        -1, 0 -> "SMS delivered"
                        32 -> "Sending"
                        64 -> "Error"
                        else -> "SMS delivered"
                    }

                    val conv = Conversation(
                        id = 0,
                        date = message.date.toString(),
                        read = message.read,
                        title = displayName,
                        photoUri = null,
                        usesCustomTitle = false,
                        phoneNumber = addressStr,
                        snippet = message.body,
                        time = message.date?.toLong(),
                        type = message.type,
                        isnumaric = addressStr.isDigitsOnly(),
                        messageStatus = statusLabel,
                        isnewmessage = !message.read,
                        newMessageCount = if (!message.read) 1 else 0,
                        messageId = msgId,
                        threadId = message.threadId,
                        isarchived = existing?.isarchived ?: false,
                        ispinned = existing?.ispinned ?: false,
                        pinneddate = existing?.pinneddate ?: 0L,
                        isblocknumber = isBlocked,
                        isexpandmessageview = true,
                        is_scheduled = false,
                        isMessagefound = false,
                        isPrivateChat = existing?.isPrivateChat ?: false,
                        draftmessage = null,
                        messagetype = msgType,
                        messageotp = otp,
                        shownotification = existing?.shownotification ?: !message.read,
                        messagetraslateshow = false,
                        messagetraslationanimationshow = false,
                        isgroupmessage = isGroup,
                        groupName = existing?.groupName ?: if (isGroup) message.participants.getThreadTitle() else null,
                        CategoryName = existing?.CategoryName,
                        isnewmessagescroll = false,
                        isonlyselectedthem = false,
                        themenumber = existing?.themenumber ?: 0,
                        customtimeuri = existing?.customtimeuri,
                        isbanneradshow = false,
                        messagewithattachment = message.attachment
                    )
                    newConversations.add(conv)
                }
            } catch (_: Exception) {
            }

            if (newConversations.isNotEmpty()) {
                messagerDatabaseRepo.insertOrUpdateList(newConversations)
            }
        } catch (_: Exception) {
        }
    }

    suspend fun getContactName(phoneNumber: String?, context: Context): String? {
        if (phoneNumber.isNullOrEmpty()) {
            return null
        }
        val cachedName = contactCache[phoneNumber]
        if (cachedName != null) {
            return cachedName
        }

        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)

        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val contactName = cursor.getString(0)
                // Cache the contact name for future use
                contactCache[phoneNumber] = contactName
                return contactName
            }
        }

        return phoneNumber
    }

    fun checkPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= 33) {
            PermissionChecker.checkSelfPermission(
                context, Manifest.permission.READ_MEDIA_IMAGES
            ) == PermissionChecker.PERMISSION_GRANTED
        } else {
            PermissionChecker.checkSelfPermission(
                context, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PermissionChecker.PERMISSION_GRANTED && PermissionChecker.checkSelfPermission(
                context, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PermissionChecker.PERMISSION_GRANTED
        }
    }
}
