package com.messenger.phone.number.text.sms.service.apps.data.newsync

import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.provider.Telephony
import android.util.Log
import androidx.collection.LongSparseArray
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.messenger.phone.number.text.sms.service.apps.CommanClass.findOtpInString
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getAddresses
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getMMS2
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getNameAndPhotoFromPhoneNumber
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getSmsCount
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getThreadTitle
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isPhoneNumber
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.combinedListAttachment
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.getAllImagesAndVideosSortedByRecentNew
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.getRecentImage
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.imageuriGalleryFirstimage
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.loadData
import com.messenger.phone.number.text.sms.service.apps.DAOClass.ThreadInfo
import com.messenger.phone.number.text.sms.service.apps.data.Message
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.simplemobiletools.commons.extensions.getBlockedNumbers
import com.simplemobiletools.commons.extensions.isNumberBlocked
import com.simplemobiletools.commons.models.BlockedNumber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Replace old refreshSmsInbox() entry with:
 *    smsSyncManager.onAppBecameDefault()
 *
 * This class gives:
 * - initial full sync in background
 * - realtime SMS/MMS observation
 * - gradual DB insert
 * - UI progress/live state through LiveData
 *
 * IMPORTANT:
 * - Adapt helper methods marked TODO to your project methods
 * - Adapt Conversation / ThreadInfo / AddressBookContact / repo names if different
 */
class SmsSyncManager(
    private val context: Context,
    private val messagerDatabaseRepo: MessagerDatabaseRepo,
    private val externalScope: CoroutineScope
) {

    companion object {
        private const val TAG = "SmsSyncManager"
        private const val INSERT_BATCH_SIZE = 25
        private const val OBSERVER_DEBOUNCE_MS = 350L
    }

    // -------------------------------------------------------------------------------------
    // Public LiveData / global-style state
    // -------------------------------------------------------------------------------------

    private val _isMessageLoading = MutableLiveData(false)
    val isMessageLoading: LiveData<Boolean> = _isMessageLoading

    private val _isInitialSyncDone = MutableLiveData(false)
    val isInitialSyncDone: LiveData<Boolean> = _isInitialSyncDone

    private val _syncStatusText = MutableLiveData("Idle")
    val syncStatusText: LiveData<String> = _syncStatusText

    private val _totalMessageCount = MutableLiveData(0)
    val totalMessageCount: LiveData<Int> = _totalMessageCount

    private val _processedMessageCount = MutableLiveData(0)
    val processedMessageCount: LiveData<Int> = _processedMessageCount

    private val _lastInsertedCount = MutableLiveData(0)
    val lastInsertedCount: LiveData<Int> = _lastInsertedCount

    private val _lastError = MutableLiveData<String?>(null)
    val lastError: LiveData<String?> = _lastError

    /**
     * Optional if UI still depends on in-memory list.
     * Prefer observing DB from Room instead.
     */
    private val _latestInsertedConversations = MutableLiveData<List<Conversation>>(emptyList())
    val latestInsertedConversations: LiveData<List<Conversation>> = _latestInsertedConversations

    // -------------------------------------------------------------------------------------
    // Internal mutable state
    // -------------------------------------------------------------------------------------

    private var smsObserver: ContentObserver? = null
    private var mmsObserver: ContentObserver? = null

    private var smsObserverJob: Job? = null
    private var mmsObserverJob: Job? = null

    private val syncMutex = Mutex()

    private var contactsCache: ContactsCache? = null

    // Keep your global-style vars if some other old logic still depends on them
    var isfastcrollerishide: Boolean = false
    var isNewUserMessage: Boolean = false
    var messagestatus: String = "SMS delivered"
    var messagetype: String = "normalmessage"
    var messageotp: String = ""
    var gropname: String = ""
    var phonrnumber: String = ""

    // -------------------------------------------------------------------------------------
    // Public entry
    // -------------------------------------------------------------------------------------

    /**
     * Call this when app becomes default SMS app or on main messaging startup.
     * This replaces direct use of refreshSmsInbox().
     */
    fun onAppBecameDefault() {
        startRealtimeObservers()

        externalScope.launch(Dispatchers.IO) {
            bootstrapFullSync()
        }
    }

    /**
     * If app opens later and you only need realtime continuation.
     */
    fun startRealtimeOnly() {
        startRealtimeObservers()
    }

    fun stopRealtimeObservers() {
        try {
            smsObserver?.let {
                context.contentResolver.unregisterContentObserver(it)
            }
        } catch (_: Exception) {
        }

        try {
            mmsObserver?.let {
                context.contentResolver.unregisterContentObserver(it)
            }
        } catch (_: Exception) {
        }

        smsObserver = null
        mmsObserver = null
    }

    // -------------------------------------------------------------------------------------
    // Initial sync
    // -------------------------------------------------------------------------------------

    @SuppressLint("Range")
    suspend fun bootstrapFullSync() = syncMutex.withLock {
        withContext(Dispatchers.IO) {
            try {
                _isMessageLoading.postValue(true)
                _isInitialSyncDone.postValue(false)
                _syncStatusText.postValue("Preparing initial sync")
                _lastError.postValue(null)
                _processedMessageCount.postValue(0)
                _lastInsertedCount.postValue(0)

                if (!hasRequiredPermissions()) {
                    _syncStatusText.postValue("Missing permissions")
                    _isMessageLoading.postValue(false)
                    return@withContext
                }

                // If you need these from your old code, keep them here.
                if (checkPermissions()) {
                    context.loadData()
                    refreshAttachmentCache()
                }

                val total = getSmsCount(context.contentResolver, context)
                _totalMessageCount.postValue(total)

                _syncStatusText.postValue("Loading contacts")
                val cache = loadContactsCache()
                contactsCache = cache

                _syncStatusText.postValue("Syncing SMS")
                bootstrapSms(cache)

                _syncStatusText.postValue("Syncing MMS")
                bootstrapMms(cache)

                _syncStatusText.postValue("Initial sync completed")
                _isInitialSyncDone.postValue(true)
                isfastcrollerishide = true
            } catch (e: Exception) {
                Log.e(TAG, "bootstrapFullSync error", e)
                _lastError.postValue(e.message)
                _syncStatusText.postValue("Sync failed")
            } finally {
                _isMessageLoading.postValue(false)
            }
        }
    }

    @SuppressLint("Range")
    private suspend fun bootstrapSms(cache: ContactsCache) {
        val contentResolver = context.contentResolver
        val blockedNumbers = context.getBlockedNumbers()

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

        val existingMessageIds = HashSet<Long>(messagerDatabaseRepo.getAllMessageIdsRepo())
        val threadInfoMap = messagerDatabaseRepo.getThreadInfoRepo().associateBy { it.threadId }

        val batch = mutableListOf<Conversation>()
        var processed = 0
        var inserted = 0

        contentResolver.query(
            Uri.parse("content://sms/"),
            projectionSms,
            null,
            null,
            "${Telephony.Sms.DATE} DESC"
        )?.use { cursor ->

            while (cursor.moveToNext()) {
                val conversation = mapSmsCursorToConversation(cursor, cache, blockedNumbers) ?: continue
                processed++

                val messageId = conversation.messageId ?: continue
                if (existingMessageIds.contains(messageId)) {
                    _processedMessageCount.postValue(processed)
                    continue
                }

                val convToInsert = mergeWithThreadMeta(conversation, threadInfoMap)
                if (shouldInsertConversation(convToInsert)) {
                    batch.add(convToInsert)
                    existingMessageIds.add(messageId)
                }

                if (batch.size >= INSERT_BATCH_SIZE) {
                    messagerDatabaseRepo.insertOrUpdateList(batch.toList())
                    inserted += batch.size
                    _lastInsertedCount.postValue(inserted)
                    _latestInsertedConversations.postValue(batch.toList())
                    batch.clear()
                }

                _processedMessageCount.postValue(processed)
            }
        }

        if (batch.isNotEmpty()) {
            messagerDatabaseRepo.insertOrUpdateList(batch.toList())
            inserted += batch.size
            _lastInsertedCount.postValue(inserted)
            _latestInsertedConversations.postValue(batch.toList())
            batch.clear()
        }
    }

    private suspend fun bootstrapMms(cache: ContactsCache) {
        val existingMessageIds = HashSet<Long>(messagerDatabaseRepo.getAllMessageIdsRepo())
        val threadInfoMap = messagerDatabaseRepo.getThreadInfoRepo().associateBy { it.threadId }
        val blockedNumbers = context.getBlockedNumbers()

        val allMms = context.getMMS2()
        val batch = mutableListOf<Conversation>()
        var inserted = _lastInsertedCount.value ?: 0
        var processed = _processedMessageCount.value ?: 0

        allMms.forEach { message ->
            val conversation = mapMmsToConversation(message, cache, blockedNumbers) ?: return@forEach
            processed++

            val messageId = conversation.messageId ?: return@forEach
            if (existingMessageIds.contains(messageId)) {
                _processedMessageCount.postValue(processed)
                return@forEach
            }

            val convToInsert = mergeWithThreadMeta(conversation, threadInfoMap)
            if (shouldInsertConversation(convToInsert)) {
                batch.add(convToInsert)
                existingMessageIds.add(messageId)
            }

            if (batch.size >= INSERT_BATCH_SIZE) {
                messagerDatabaseRepo.insertOrUpdateList(batch.toList())
                inserted += batch.size
                _lastInsertedCount.postValue(inserted)
                _latestInsertedConversations.postValue(batch.toList())
                batch.clear()
            }

            _processedMessageCount.postValue(processed)
        }

        if (batch.isNotEmpty()) {
            messagerDatabaseRepo.insertOrUpdateList(batch.toList())
            inserted += batch.size
            _lastInsertedCount.postValue(inserted)
            _latestInsertedConversations.postValue(batch.toList())
            batch.clear()
        }
    }

    // -------------------------------------------------------------------------------------
    // Realtime observers
    // -------------------------------------------------------------------------------------

    private fun startRealtimeObservers() {
        if (smsObserver != null || mmsObserver != null) return

        smsObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                debounceSmsSync()
            }

            override fun onChange(selfChange: Boolean, uri: Uri?) {
                debounceSmsSync()
            }
        }

        mmsObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                debounceMmsSync()
            }

            override fun onChange(selfChange: Boolean, uri: Uri?) {
                debounceMmsSync()
            }
        }

        context.contentResolver.registerContentObserver(
            Telephony.Sms.CONTENT_URI,
            true,
            smsObserver!!
        )

        context.contentResolver.registerContentObserver(
            Uri.parse("content://mms"),
            true,
            mmsObserver!!
        )
    }

    private fun debounceSmsSync() {
        smsObserverJob?.cancel()
        smsObserverJob = externalScope.launch(Dispatchers.IO) {
            delay(OBSERVER_DEBOUNCE_MS)
            syncLatestSms(limit = 15)
        }
    }

    private fun debounceMmsSync() {
        mmsObserverJob?.cancel()
        mmsObserverJob = externalScope.launch(Dispatchers.IO) {
            delay(OBSERVER_DEBOUNCE_MS)
            syncLatestMms(limit = 10)
        }
    }

    // -------------------------------------------------------------------------------------
    // Incremental realtime sync
    // -------------------------------------------------------------------------------------

    @SuppressLint("Range")
    suspend fun syncLatestSms(limit: Int = 10) {
        withContext(Dispatchers.IO) {
            try {
                if (!hasRequiredPermissions()) return@withContext

                val cache = contactsCache ?: loadContactsCache().also { contactsCache = it }
                val existingMessageIds = HashSet<Long>(messagerDatabaseRepo.getAllMessageIdsRepo())
                val threadInfoMap = messagerDatabaseRepo.getThreadInfoRepo().associateBy { it.threadId }

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

                val list = mutableListOf<Conversation>()
                val blockedNumbers = context.getBlockedNumbers()

                context.contentResolver.query(
                    Uri.parse("content://sms/"),
                    projectionSms,
                    null,
                    null,
                    "${Telephony.Sms.DATE} DESC LIMIT $limit"
                )?.use { cursor ->
                    while (cursor.moveToNext()) {
                        val conversation = mapSmsCursorToConversation(cursor, cache, blockedNumbers) ?: continue
                        val msgId = conversation.messageId ?: continue
                        if (existingMessageIds.contains(msgId)) continue

                        val convToInsert = mergeWithThreadMeta(conversation, threadInfoMap)
                        if (shouldInsertConversation(convToInsert)) {
                            list.add(convToInsert)
                            existingMessageIds.add(msgId)
                        }
                    }
                }

                if (list.isNotEmpty()) {
                    messagerDatabaseRepo.insertOrUpdateList(list)
                    _latestInsertedConversations.postValue(list)
                    _lastInsertedCount.postValue((_lastInsertedCount.value ?: 0) + list.size)
                }
            } catch (e: Exception) {
                Log.e(TAG, "syncLatestSms error", e)
                _lastError.postValue(e.message)
            }
        }
    }

    suspend fun syncLatestMms(limit: Int = 10) {
        withContext(Dispatchers.IO) {
            try {
                if (!hasRequiredPermissions()) return@withContext

                val cache = contactsCache ?: loadContactsCache().also { contactsCache = it }
                val existingMessageIds = HashSet<Long>(messagerDatabaseRepo.getAllMessageIdsRepo())
                val threadInfoMap = messagerDatabaseRepo.getThreadInfoRepo().associateBy { it.threadId }
                val blockedNumbers = context.getBlockedNumbers()

                val list = mutableListOf<Conversation>()
                val allMms = context.getMMS2().sortedByDescending { it.date }.take(limit)

                allMms.forEach { message ->
                    val conversation = mapMmsToConversation(message, cache, blockedNumbers) ?: return@forEach
                    val msgId = conversation.messageId ?: return@forEach
                    if (existingMessageIds.contains(msgId)) return@forEach

                    val convToInsert = mergeWithThreadMeta(conversation, threadInfoMap)
                    if (shouldInsertConversation(convToInsert)) {
                        list.add(convToInsert)
                        existingMessageIds.add(msgId)
                    }
                }

                if (list.isNotEmpty()) {
                    messagerDatabaseRepo.insertOrUpdateList(list)
                    _latestInsertedConversations.postValue(list)
                    _lastInsertedCount.postValue((_lastInsertedCount.value ?: 0) + list.size)
                }
            } catch (e: Exception) {
                Log.e(TAG, "syncLatestMms error", e)
                _lastError.postValue(e.message)
            }
        }
    }

    /**
     * Optional targeted call if you later decide to sync one thread only.
     */
    suspend fun syncThread(threadId: Long) {
        withContext(Dispatchers.IO) {
            try {
                if (!hasRequiredPermissions()) return@withContext
                val cache = contactsCache ?: loadContactsCache().also { contactsCache = it }
                val existingMessageIds = HashSet<Long>(messagerDatabaseRepo.getAllMessageIdsRepo())
                val threadInfoMap = messagerDatabaseRepo.getThreadInfoRepo().associateBy { it.threadId }

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

                val list = mutableListOf<Conversation>()
                val blockedNumbers = context.getBlockedNumbers()

                context.contentResolver.query(
                    Uri.parse("content://sms/"),
                    projectionSms,
                    "${Telephony.Sms.THREAD_ID}=?",
                    arrayOf(threadId.toString()),
                    "${Telephony.Sms.DATE} DESC"
                )?.use { cursor ->
                    while (cursor.moveToNext()) {
                        val conversation = mapSmsCursorToConversation(cursor, cache, blockedNumbers) ?: continue
                        val msgId = conversation.messageId ?: continue
                        if (existingMessageIds.contains(msgId)) continue

                        val convToInsert = mergeWithThreadMeta(conversation, threadInfoMap)
                        if (shouldInsertConversation(convToInsert)) {
                            list.add(convToInsert)
                            existingMessageIds.add(msgId)
                        }
                    }
                }

                val threadMms = context.getMMS2(threadId = threadId)
                threadMms.forEach { message ->
                    val conversation = mapMmsToConversation(message, cache, blockedNumbers) ?: return@forEach
                    val msgId = conversation.messageId ?: return@forEach
                    if (existingMessageIds.contains(msgId)) return@forEach

                    val convToInsert = mergeWithThreadMeta(conversation, threadInfoMap)
                    if (shouldInsertConversation(convToInsert)) {
                        list.add(convToInsert)
                        existingMessageIds.add(msgId)
                    }
                }

                if (list.isNotEmpty()) {
                    messagerDatabaseRepo.insertOrUpdateList(list)
                    _latestInsertedConversations.postValue(list)
                    _lastInsertedCount.postValue((_lastInsertedCount.value ?: 0) + list.size)
                }
            } catch (e: Exception) {
                Log.e(TAG, "syncThread error", e)
                _lastError.postValue(e.message)
            }
        }
    }

    // -------------------------------------------------------------------------------------
    // Contacts cache
    // -------------------------------------------------------------------------------------

    @SuppressLint("Range")
    private suspend fun loadContactsCache(): ContactsCache = withContext(Dispatchers.IO) {
        val contacts = ArrayList<AddressBookContact>()
        val contactsById = LongSparseArray<AddressBookContact>()

        val projectionContacts = arrayOf(
            ContactsContract.Data.MIMETYPE,
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Contactables.DATA,
            ContactsContract.CommonDataKinds.Contactables.TYPE
        )

        val selection = "${ContactsContract.Data.MIMETYPE} in (?, ?)"
        val selectionArgs = arrayOf(
            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
        )

        val sortOrderContacts = ContactsContract.Contacts.SORT_KEY_ALTERNATIVE
        val contactsUri = ContactsContract.CommonDataKinds.Contactables.CONTENT_URI

        context.contentResolver.query(
            contactsUri,
            projectionContacts,
            selection,
            selectionArgs,
            sortOrderContacts
        )?.use { contactsCursor ->

            val idIdx = contactsCursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)
            val nameIdx = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val dataIdx = contactsCursor.getColumnIndex(
                ContactsContract.CommonDataKinds.Contactables.DATA
            )
            val typeIdx = contactsCursor.getColumnIndex(
                ContactsContract.CommonDataKinds.Contactables.TYPE
            )

            if (idIdx != -1 && nameIdx != -1 && dataIdx != -1 && typeIdx != -1) {
                while (contactsCursor.moveToNext()) {
                    val id = contactsCursor.getLong(idIdx)
                    var addressBookContact = contactsById.get(id)

                    if (addressBookContact == null) {
                        val safeName = contactsCursor.getString(nameIdx) ?: "Unknown"
                        addressBookContact = AddressBookContact(id, safeName, context.resources)
                        contactsById.put(id, addressBookContact)
                        contacts.add(addressBookContact)
                    }

                    val type = contactsCursor.getInt(typeIdx)
                    val data = contactsCursor.getString(dataIdx)
                    addressBookContact.addPhone(type, data)
                }
            }
        }

        val phoneToName = HashMap<String, String>(contacts.size * 2)
        contacts.forEach { contact ->
            val phone = contact.phones
            if (!phone.isNullOrEmpty()) {
                phoneToName[phone] = contact.name
            }
        }

        ContactsCache(
            contacts = contacts,
            phoneToName = phoneToName
        )
    }

    // -------------------------------------------------------------------------------------
    // Mapping
    // -------------------------------------------------------------------------------------

    @SuppressLint("Range")
    private fun mapSmsCursorToConversation(
        cursor: Cursor,
        cache: ContactsCache,
        blockedNumbers: ArrayList<BlockedNumber>
    ): Conversation? {
        val smsAddress = cursor.getString(
            cursor.getColumnIndex(Telephony.Sms.ADDRESS)
        ) ?: ""

        val smsBody = cursor.getString(
            cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)
        ) ?: ""

        val smsDate = cursor.getString(
            cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)
        ) ?: ""

        val smsMessageId = cursor.getString(
            cursor.getColumnIndexOrThrow(Telephony.Sms._ID)
        ) ?: return null

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

        // Ignore drafts
        if (typeId == 3) return null

        messagestatus = when (status) {
            -1, 0 -> "SMS delivered"
            32 -> "Sending"
            64 -> "Error"
            else -> "SMS delivered"
        }

        phonrnumber = smsAddress
        val number = phonrnumber

        var isGroupMessage = false
        gropname = ""

        if (number.contains("|")) {
            val numbers = number.split("|").map { it.trim() }.filter { it.isNotEmpty() }
            isGroupMessage = numbers.size >= 2
            if (isGroupMessage) {
                gropname = numbers.joinToString { part ->
                    cache.phoneToName[part].orEmpty().ifBlank { part }
                }
            }
        }

        val isBlocked = context.isNumberBlocked(number, blockedNumbers)

        isNewUserMessage = !read
        val isNewUserMessageCount = if (isNewUserMessage) 1 else 0

        try {
            val otp = findOtpInString(smsBody)
            if (otp.isEmpty()) {
                messagetype = "normalmessage"
                messageotp = ""
            } else {
                messagetype = "otp"
                messageotp = otp
            }
        } catch (_: Exception) {
            messagetype = "normalmessage"
            messageotp = ""
        }

        val contactName = cache.phoneToName[number]

        val title = when {
            !contactName.isNullOrEmpty() -> contactName
            number.isPhoneNumber() && number.length <= 7 -> number
            number.isPhoneNumber() -> resolveContactName(number)
            else -> number
        }

        return Conversation(
            id = 0,
            date = smsDate,
            read = true,
            title = title,
            photoUri = null,
            usesCustomTitle = false,
            phoneNumber = number,
            snippet = smsBody,
            time = smsDate.toLongOrNull() ?: 0L,
            type = typeId,
            isnumaric = true,
            messageStatus = messagestatus,
            messageId = smsMessageId.toLongOrNull() ?: return null,
            threadId = threadId,
            isblocknumber = isBlocked,
            messagetype = messagetype,
            messageotp = messageotp,
            isnewmessage = isNewUserMessage,
            newMessageCount = isNewUserMessageCount,
            isgroupmessage = isGroupMessage,
            groupName = if (isGroupMessage) gropname else null
        )
    }

    private fun mapMmsToConversation(
        message: Message,
        cache: ContactsCache,
        blockedNumbers: ArrayList<BlockedNumber>
    ): Conversation? {
        val phoneNumbers = message.participants.getAddresses()
        val name = message.participants.getThreadTitle()

        isNewUserMessage = !message.read
        val isBlocked = context.isNumberBlocked(phoneNumbers.joinToString(), blockedNumbers)
        val isNewUserMessageCount = if (isNewUserMessage) 1 else 0

        val isGroupMessage = phoneNumbers.size >= 2
        gropname = if (isGroupMessage) name else ""

        try {
            val otp = findOtpInString(message.body)
            if (otp.isEmpty()) {
                messagetype = "normalmessage"
                messageotp = ""
            } else {
                messagetype = "otp"
                messageotp = otp
            }
        } catch (_: Exception) {
            messagetype = "normalmessage"
            messageotp = ""
        }

        messagestatus = when (message.status) {
            -1, 0 -> "SMS delivered"
            32 -> "Sending"
            64 -> "Error"
            else -> "SMS delivered"
        }

        return Conversation(
            id = 0,
            date = message.date.toString(),
            read = true,
            title = name,
            photoUri = null,
            usesCustomTitle = false,
            phoneNumber = phoneNumbers.joinToString(),
            snippet = message.body,
            time = message.date.toLong(),
            type = message.type,
            isnumaric = true,
            messageStatus = messagestatus,
            messageId = message.id,
            threadId = message.threadId,
            isblocknumber = isBlocked,
            messagetype = messagetype,
            messageotp = messageotp,
            isnewmessage = isNewUserMessage,
            newMessageCount = isNewUserMessageCount,
            isgroupmessage = isGroupMessage,
            groupName = if (isGroupMessage) gropname else null,
            messagewithattachment = message.attachment
        )
    }

    // -------------------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------------------

    private fun mergeWithThreadMeta(
        conversation: Conversation,
        threadInfoMap: Map<Long, ThreadInfo>
    ): Conversation {
        val meta = conversation.threadId?.let { threadInfoMap[it] } ?: return conversation

        val safeIsGroup = meta.isgroupmessage || conversation.isgroupmessage
        val safeTitle = meta.title ?: conversation.title
        val safePhone = meta.phoneNumber ?: conversation.phoneNumber
        val safeGroupName = meta.groupName ?: conversation.groupName

        return conversation.copy(
            title = safeTitle,
            phoneNumber = safePhone,
            isgroupmessage = safeIsGroup,
            groupName = safeGroupName
        )
    }

    private fun shouldInsertConversation(conversation: Conversation): Boolean {
        return conversation.snippet.isNotEmpty() ||
                conversation.messagewithattachment?.attachments?.isNotEmpty() == true
    }

    private fun hasRequiredPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= 33) {
            PermissionChecker.checkSelfPermission(
                context, Manifest.permission.READ_MEDIA_IMAGES
            ) == PermissionChecker.PERMISSION_GRANTED
        } else {
            PermissionChecker.checkSelfPermission(
                context, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PermissionChecker.PERMISSION_GRANTED &&
                    PermissionChecker.checkSelfPermission(
                        context, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PermissionChecker.PERMISSION_GRANTED
        }
    }

    private suspend fun refreshAttachmentCache() {
        val latestMedia = ArrayList(context.getAllImagesAndVideosSortedByRecentNew())
        combinedListAttachment = latestMedia
        imageuriGalleryFirstimage = context.getRecentImage()
    }

    private fun resolveContactName(number: String): String {
        return context.getNameAndPhotoFromPhoneNumber(number).name.ifBlank { number }
    }
}

// -----------------------------------------------------------------------------------------
// Supporting models
// Replace with your actual project models if already present.
// -----------------------------------------------------------------------------------------

data class ContactsCache(
    val contacts: List<AddressBookContact>,
    val phoneToName: Map<String, String>
)

class AddressBookContact(
    val id: Long,
    val name: String,
    resources: android.content.res.Resources
) {
    var phones: String? = null

    fun addPhone(type: Int, data: String?) {
        if (!data.isNullOrBlank()) {
            phones = data
        }
    }
}
