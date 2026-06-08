package com.messenger.phone.number.text.sms.service.apps.Repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.InvalidationTracker
import androidx.paging.PagingSource
import androidx.room.withTransaction
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.modelclass.Foldermodel
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.modelclass.Photo
import com.messenger.phone.number.text.sms.service.apps.DataBase.MessagerDatabase
import com.messenger.phone.number.text.sms.service.apps.modelClass.Category
import com.messenger.phone.number.text.sms.service.apps.modelClass.Contact
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversationbin
import com.messenger.phone.number.text.sms.service.apps.modelClass.Recentsearch
import com.messenger.phone.number.text.sms.service.apps.modelClass.Remindermodel
import com.messenger.phone.number.text.sms.service.apps.modelClass.StarNumber
import com.messenger.phone.number.text.sms.service.apps.DAOClass.ThreadInfo
import com.messenger.phone.number.text.sms.service.apps.realmplan.RealmFeatureFlag
import com.messenger.phone.number.text.sms.service.apps.realmplan.RealmMessagingRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagerDatabaseRepo @Inject constructor(
    var messagerDatabase: MessagerDatabase,
    private val realmRepo: RealmMessagingRepo,
    private val flag: RealmFeatureFlag,
) {
    private val safeQueryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private companion object {
        private const val SAFE_UNARCHIVED_CONVERSATION_QUERY =
            "SELECT " +
                "COALESCE(CAST(id AS INTEGER), 0) AS id, " +
                "COALESCE(date, '') AS date, " +
                "COALESCE(read, 0) AS read, " +
                "COALESCE(title, '') AS title, " +
                "photoUri, " +
                "COALESCE(usesCustomTitle, 0) AS usesCustomTitle, " +
                "COALESCE(phoneNumber, '') AS phoneNumber, " +
                "SUBSTR(COALESCE(snippet, ''), 1, 2000) AS snippet, " +
                "time, " +
                "type, " +
                "COALESCE(isnumaric, 0) AS isnumaric, " +
                "messageStatus, " +
                "COALESCE(isnewmessage, 0) AS isnewmessage, " +
                "COALESCE(newMessageCount, 0) AS newMessageCount, " +
                "messageId, " +
                "threadId, " +
                "COALESCE(isarchived, 0) AS isarchived, " +
                "COALESCE(ispinned, 0) AS ispinned, " +
                "pinneddate, " +
                "COALESCE(isblocknumber, 0) AS isblocknumber, " +
                "COALESCE(isexpandmessageview, 1) AS isexpandmessageview, " +
                "COALESCE(is_scheduled, 0) AS is_scheduled, " +
                "COALESCE(isMessagefound, 0) AS isMessagefound, " +
                "COALESCE(isPrivateChat, 0) AS isPrivateChat, " +
                "SUBSTR(COALESCE(draftmessage, ''), 1, 2000) AS draftmessage, " +
                "messagetype, " +
                "messageotp, " +
                "COALESCE(shownotification, 1) AS shownotification, " +
                "COALESCE(messagetraslateshow, 0) AS messagetraslateshow, " +
                "COALESCE(messagetraslationanimationshow, 0) AS messagetraslationanimationshow, " +
                "COALESCE(isgroupmessage, 0) AS isgroupmessage, " +
                "groupName, " +
                "CategoryName, " +
                "COALESCE(isnewmessagescroll, 0) AS isnewmessagescroll, " +
                "COALESCE(isonlyselectedthem, 0) AS isonlyselectedthem, " +
                "themenumber, " +
                "customtimeuri, " +
                "COALESCE(isbanneradshow, 0) AS isbanneradshow " +
                "FROM Conversation " +
                "WHERE isarchived = 0 AND isblocknumber = 0 AND isPrivateChat = 0 " +
                "AND threadId NOT IN (SELECT threadId FROM conversationbin) " +
                "ORDER BY time DESC"
    }

    private inline fun <T> runRealmReadOrFallback(
        operation: String,
        fallback: () -> T,
        realmBlock: () -> T,
    ): T {
        return try {
            realmBlock()
        } catch (t: Throwable) {
            if (t is VirtualMachineError || t is ThreadDeath) throw t
            Log.e("MessagerDatabaseRepo", "Realm read failed for $operation, falling back to Room", t)
            flag.useRealmReads = false
            fallback()
        }
    }

    //MessageRepo

    suspend fun insertmessage(message: Conversation): Long {
        if (flag.useRealmReads) return realmRepo.insertmessage(message)
        val result = messagerDatabase.getMessageDAO().insertOrUpdate(message)
        safeQueryScope.launch { runCatching { realmRepo.insertmessage(message) } }
        return result
    }

    suspend fun deleteRemainderRepo(id: Int) {
        if (flag.useRealmReads) { realmRepo.deleteRemainderRepo(id); return }
        messagerDatabase.getMessageDAO().deleteRemainder(id)
    }

    suspend fun runInTransaction(block: () -> Unit) {
        if (flag.useRealmReads) { block(); return }
        messagerDatabase.withTransaction(block)
    }

    suspend fun insertOrUpdateRemainderRepo(message: Remindermodel): Long {
        if (flag.useRealmReads) return realmRepo.insertOrUpdateRemainderRepo(message)
        return messagerDatabase.getMessageDAO().insertOrUpdateRemainder(message)
    }

    suspend fun insertOrUpdateRecycleBinRepo(message: Conversationbin): Long {
        if (flag.useRealmReads) return realmRepo.insertOrUpdateRecycleBinRepo(message)
        val result = messagerDatabase.getMessageDAO().insertOrUpdateRecycleBin(message)
        safeQueryScope.launch { runCatching { realmRepo.insertOrUpdateRecycleBinRepo(message) } }
        return result
    }

    suspend fun moveConversationsToRecycleBin(threadIds: List<Long>) {
        if (flag.useRealmReads) { realmRepo.moveConversationsToRecycleBin(threadIds); return }
        val normalizedThreadIds = threadIds.distinct()
        safeQueryScope.launch { runCatching { realmRepo.moveConversationsToRecycleBin(threadIds) } }
        if (normalizedThreadIds.isEmpty()) return
        messagerDatabase.withTransaction {
            val threadIdStrings = normalizedThreadIds.map(Long::toString)
            messagerDatabase.getMessageDAO().deleteConversationRecyclerbin(threadIdStrings)
            normalizedThreadIds.forEach { threadId ->
                val conversations = messagerDatabase.getMessageDAO().getUserMessageListChack(threadId)
                conversations.forEach { conversation ->
                    messagerDatabase.getMessageDAO().insertOrUpdateRecycleBin(conversation.toConversationBin())
                }
            }
            threadIdStrings.chunked(900).forEach { chunk ->
                messagerDatabase.getMessageDAO().deleteConversation(chunk)
            }
        }
    }

    suspend fun insertOrUpdateList(message: List<Conversation>) {
        if (flag.useRealmReads) { realmRepo.insertOrUpdateList(message); return }
        messagerDatabase.getMessageDAO().insertOrUpdateList(message)
        safeQueryScope.launch { runCatching { realmRepo.insertOrUpdateList(message) } }
    }

    suspend fun updateremaindertitleRepo(messageId: Long, titlere: String) {
        if (flag.useRealmReads) { realmRepo.updateremaindertitleRepo(messageId, titlere); return }
        return messagerDatabase.getMessageDAO().updateremaindertitle(messageId, titlere)
    }

    suspend fun gettredidfromemessageidRepo(messageId: Long): Conversation? {
        if (flag.useRealmReads) return realmRepo.gettredidfromemessageidRepo(messageId)
        return messagerDatabase.getMessageDAO().gettredidfromemessageid(messageId)
    }

    suspend fun deletedata() {
        if (flag.useRealmReads) { realmRepo.deletedata(); return }
        messagerDatabase.getMessageDAO().deleteAll()
    }

    private suspend fun <T> safeBatch(
        list: List<T>,
        chunkSize: Int = 900,
        action: suspend (List<T>) -> Unit
    ) {
        list.chunked(chunkSize).forEach { chunk ->
            action(chunk)
        }
    }

    fun getalldataconversationrepo(): LiveData<List<Conversation>> {
        if (flag.useRealmReads) return realmRepo.getalldataconversationrepo()
        return messagerDatabase.getMessageDAO().getallconversation()
    }

    fun getalldataRemainderrepo(): LiveData<List<Remindermodel>> {
        if (flag.useRealmReads) return realmRepo.getalldataRemainderrepo()
        return messagerDatabase.getMessageDAO().getallRemainder()
    }

    fun getallconversationunPrivacyOrnotListrepo(): List<Conversation> {
        if (flag.useRealmReads) return realmRepo.getallconversationunPrivacyOrnotListrepo()
        return messagerDatabase.getMessageDAO().getallconversationunPrivacyOrnotList()
    }

    fun getallconversationOnlyListrepo(): List<Conversation> {
        if (flag.useRealmReads) return realmRepo.getallconversationOnlyListrepo()
        return messagerDatabase.getMessageDAO().getallconversationOnlyList()
    }

    suspend fun getallTabOnlyListrepo(): List<Category> {
        if (flag.useRealmReads) return realmRepo.getallTabOnlyListrepo()
        return messagerDatabase.getMessageDAO().getallTabOnlyList()
    }

    fun scheduleconversationexitsrepo(messageId: Long): Boolean {
        if (flag.useRealmReads) return realmRepo.scheduleconversationexitsrepo(messageId)
        return messagerDatabase.getMessageDAO().scheduleconversationexits(messageId)
    }

    fun getallconversationunarchivrepo(): LiveData<List<Conversation>> {
        if (flag.useRealmReads) return realmRepo.getallconversationunarchivrepo()
        return object : LiveData<List<Conversation>>() {
            private val observer = object : InvalidationTracker.Observer("Conversation") {
                override fun onInvalidated(tables: Set<String>) {
                    loadConversations()
                }
            }

            override fun onActive() {
                super.onActive()
                messagerDatabase.invalidationTracker.addObserver(observer)
                loadConversations()
            }

            override fun onInactive() {
                messagerDatabase.invalidationTracker.removeObserver(observer)
                super.onInactive()
            }

            private fun loadConversations() {
                safeQueryScope.launch {
                    val conversations = runCatching { querySafeUnarchivedConversations() }
                        .onFailure {
                            Log.e(
                                "MessagerDatabaseRepo",
                                "Failed to load unarchived conversations safely",
                                it
                            )
                        }
                        .getOrDefault(emptyList())
                    postValue(conversations)
                }
            }
        }
    }

    fun getPaginatedConversations(): PagingSource<Int, Conversation> {
        if (flag.useRealmReads) return realmRepo.getPaginatedConversations()
        return messagerDatabase.getMessageDAO().getPagedConversations()
    }

    fun getallconversationunarchivforcontactrepo(): List<Conversation> {
        if (flag.useRealmReads) return realmRepo.getallconversationunarchivforcontactrepo()
        return messagerDatabase.getMessageDAO().getallconversationunarchivforcontact()
    }

    fun getallScheduleconversationurepo(): LiveData<List<Conversation>> {
        if (flag.useRealmReads) return realmRepo.getallScheduleconversationurepo()
        return messagerDatabase.getMessageDAO().getallScheduleconversationu()
    }

    fun getallconversationunarchivOrnotrepo(): LiveData<List<Conversation>> {
        if (flag.useRealmReads) return realmRepo.getallconversationunarchivOrnotrepo()
        return messagerDatabase.getMessageDAO().getallconversationunarchivOrnot()
    }

    fun getallconversationunprivacyOrnotrepo(): LiveData<List<Conversation>> {
        if (flag.useRealmReads) return realmRepo.getallconversationunprivacyOrnotrepo()
        return messagerDatabase.getMessageDAO().getallconversationunPrivacyOrnot()
    }

    fun getalldeleteconversationunrepo(): LiveData<List<Conversationbin>> {
        if (flag.useRealmReads) return realmRepo.getalldeleteconversationunrepo()
        return messagerDatabase.getMessageDAO().getalldeleteconversationun()
    }

    fun getallconversationunblockOrnotrepo(): LiveData<List<Conversation>> {
        if (flag.useRealmReads) return realmRepo.getallconversationunblockOrnotrepo()
        return messagerDatabase.getMessageDAO().getallconversationunblockOrnot()
    }

    fun getAllUserMessagerepo(tredid: Long): LiveData<List<Conversation>> {
        if (flag.useRealmReads) return realmRepo.getAllUserMessagerepo(tredid)
        return messagerDatabase.getMessageDAO().getUserMessage(tredid)
    }

    fun getUserDeleteMessagerepo(tredid: Long): LiveData<List<Conversationbin>> {
        if (flag.useRealmReads) return realmRepo.getUserDeleteMessagerepo(tredid)
        return messagerDatabase.getMessageDAO().getUserDeleteMessage(tredid)
    }

    fun getUserMessageListChackrepo(tredid: Long): List<Conversation> {
        val roomFallback = { messagerDatabase.getMessageDAO().getUserMessageListChack(tredid) }
        if (flag.useRealmReads) {
            return runRealmReadOrFallback(
                operation = "getUserMessageListChackrepo",
                fallback = roomFallback,
            ) {
                realmRepo.getUserMessageListChackrepo(tredid)
            }
        }
        return roomFallback()
    }

    fun getUserMessageRecyListChacktredrepo(tredid: Long): List<Conversationbin> {
        if (flag.useRealmReads) return realmRepo.getUserMessageRecyListChacktredrepo(tredid)
        return messagerDatabase.getMessageDAO().getUserMessageRecyListChack(tredid)
    }

    fun getUserMessageRecyListChackrepo(tredid: String): List<Conversationbin> {
        if (flag.useRealmReads) return realmRepo.getUserMessageRecyListChackrepo(tredid)
        return messagerDatabase.getMessageDAO().getUserMessageRecyListChack(tredid)
    }

    fun getUserMessageMobileListChackrepo(tredid: String): List<Conversation> {
        if (flag.useRealmReads) return realmRepo.getUserMessageMobileListChackrepo(tredid)
        return messagerDatabase.getMessageDAO().getUserMessageMobileListChack(tredid)
    }

    private fun querySafeUnarchivedConversations(): List<Conversation> {
        val cursor = messagerDatabase.openHelper.readableDatabase.query(SAFE_UNARCHIVED_CONVERSATION_QUERY)
        return cursor.use {
            val conversations = ArrayList<Conversation>(cursor.count.coerceAtLeast(0))
            val idIndex = cursor.getColumnIndexOrThrow("id")
            val dateIndex = cursor.getColumnIndexOrThrow("date")
            val readIndex = cursor.getColumnIndexOrThrow("read")
            val titleIndex = cursor.getColumnIndexOrThrow("title")
            val photoUriIndex = cursor.getColumnIndexOrThrow("photoUri")
            val usesCustomTitleIndex = cursor.getColumnIndexOrThrow("usesCustomTitle")
            val phoneNumberIndex = cursor.getColumnIndexOrThrow("phoneNumber")
            val snippetIndex = cursor.getColumnIndexOrThrow("snippet")
            val timeIndex = cursor.getColumnIndexOrThrow("time")
            val typeIndex = cursor.getColumnIndexOrThrow("type")
            val isnumaricIndex = cursor.getColumnIndexOrThrow("isnumaric")
            val messageStatusIndex = cursor.getColumnIndexOrThrow("messageStatus")
            val isnewmessageIndex = cursor.getColumnIndexOrThrow("isnewmessage")
            val newMessageCountIndex = cursor.getColumnIndexOrThrow("newMessageCount")
            val messageIdIndex = cursor.getColumnIndexOrThrow("messageId")
            val threadIdIndex = cursor.getColumnIndexOrThrow("threadId")
            val isarchivedIndex = cursor.getColumnIndexOrThrow("isarchived")
            val ispinnedIndex = cursor.getColumnIndexOrThrow("ispinned")
            val pinneddateIndex = cursor.getColumnIndexOrThrow("pinneddate")
            val isblocknumberIndex = cursor.getColumnIndexOrThrow("isblocknumber")
            val isexpandmessageviewIndex = cursor.getColumnIndexOrThrow("isexpandmessageview")
            val isScheduledIndex = cursor.getColumnIndexOrThrow("is_scheduled")
            val isMessagefoundIndex = cursor.getColumnIndexOrThrow("isMessagefound")
            val isPrivateChatIndex = cursor.getColumnIndexOrThrow("isPrivateChat")
            val draftmessageIndex = cursor.getColumnIndexOrThrow("draftmessage")
            val messagetypeIndex = cursor.getColumnIndexOrThrow("messagetype")
            val messageotpIndex = cursor.getColumnIndexOrThrow("messageotp")
            val shownotificationIndex = cursor.getColumnIndexOrThrow("shownotification")
            val messagetraslateshowIndex = cursor.getColumnIndexOrThrow("messagetraslateshow")
            val messagetraslationanimationshowIndex = cursor.getColumnIndexOrThrow("messagetraslationanimationshow")
            val isgroupmessageIndex = cursor.getColumnIndexOrThrow("isgroupmessage")
            val groupNameIndex = cursor.getColumnIndexOrThrow("groupName")
            val categoryNameIndex = cursor.getColumnIndexOrThrow("CategoryName")
            val isnewmessagescrollIndex = cursor.getColumnIndexOrThrow("isnewmessagescroll")
            val isonlyselectedthemIndex = cursor.getColumnIndexOrThrow("isonlyselectedthem")
            val themenumberIndex = cursor.getColumnIndexOrThrow("themenumber")
            val customtimeuriIndex = cursor.getColumnIndexOrThrow("customtimeuri")
            val isbanneradshowIndex = cursor.getColumnIndexOrThrow("isbanneradshow")

            while (cursor.moveToNext()) {
                conversations.add(
                    Conversation(
                        id = cursor.getIntOrDefault(idIndex),
                        date = cursor.getStringOrDefault(dateIndex),
                        read = cursor.getBooleanOrDefault(readIndex),
                        title = cursor.getStringOrDefault(titleIndex),
                        photoUri = cursor.getNullableString(photoUriIndex),
                        usesCustomTitle = cursor.getBooleanOrDefault(usesCustomTitleIndex),
                        phoneNumber = cursor.getStringOrDefault(phoneNumberIndex),
                        snippet = cursor.getStringOrDefault(snippetIndex),
                        time = cursor.getNullableLong(timeIndex),
                        type = cursor.getNullableInt(typeIndex),
                        isnumaric = cursor.getBooleanOrDefault(isnumaricIndex),
                        messageStatus = cursor.getNullableString(messageStatusIndex),
                        isnewmessage = cursor.getBooleanOrDefault(isnewmessageIndex),
                        newMessageCount = cursor.getIntOrDefault(newMessageCountIndex),
                        messageId = cursor.getNullableLong(messageIdIndex),
                        threadId = cursor.getNullableLong(threadIdIndex),
                        isarchived = cursor.getBooleanOrDefault(isarchivedIndex),
                        ispinned = cursor.getBooleanOrDefault(ispinnedIndex),
                        pinneddate = cursor.getNullableLong(pinneddateIndex),
                        isblocknumber = cursor.getBooleanOrDefault(isblocknumberIndex),
                        isexpandmessageview = cursor.getBooleanOrDefault(isexpandmessageviewIndex, true),
                        is_scheduled = cursor.getBooleanOrDefault(isScheduledIndex),
                        isMessagefound = cursor.getBooleanOrDefault(isMessagefoundIndex),
                        isPrivateChat = cursor.getBooleanOrDefault(isPrivateChatIndex),
                        draftmessage = cursor.getNullableString(draftmessageIndex),
                        messagetype = cursor.getNullableString(messagetypeIndex),
                        messageotp = cursor.getNullableString(messageotpIndex),
                        shownotification = cursor.getBooleanOrDefault(shownotificationIndex, true),
                        messagetraslateshow = cursor.getBooleanOrDefault(messagetraslateshowIndex),
                        messagetraslationanimationshow = cursor.getBooleanOrDefault(messagetraslationanimationshowIndex),
                        isgroupmessage = cursor.getBooleanOrDefault(isgroupmessageIndex),
                        groupName = cursor.getNullableString(groupNameIndex),
                        CategoryName = cursor.getNullableString(categoryNameIndex),
                        isnewmessagescroll = cursor.getBooleanOrDefault(isnewmessagescrollIndex),
                        isonlyselectedthem = cursor.getBooleanOrDefault(isonlyselectedthemIndex),
                        themenumber = cursor.getNullableInt(themenumberIndex),
                        customtimeuri = cursor.getNullableString(customtimeuriIndex),
                        isbanneradshow = cursor.getBooleanOrDefault(isbanneradshowIndex),
                        messagewithattachment = null
                    )
                )
            }
            conversations
        }
    }

    private fun android.database.Cursor.getStringOrDefault(index: Int, defaultValue: String = ""): String {
        return if (isNull(index)) defaultValue else getString(index) ?: defaultValue
    }

    private fun android.database.Cursor.getNullableString(index: Int): String? {
        return if (isNull(index)) null else getString(index)
    }

    private fun android.database.Cursor.getIntOrDefault(index: Int, defaultValue: Int = 0): Int {
        if (isNull(index)) return defaultValue
        return runCatching { getInt(index) }.getOrElse {
            getString(index)?.toIntOrNull() ?: defaultValue
        }
    }

    private fun android.database.Cursor.getNullableInt(index: Int): Int? {
        if (isNull(index)) return null
        return runCatching { getInt(index) }.getOrElse {
            getString(index)?.toIntOrNull()
        }
    }

    private fun android.database.Cursor.getNullableLong(index: Int): Long? {
        if (isNull(index)) return null
        return runCatching { getLong(index) }.getOrElse {
            getString(index)?.toLongOrNull()
        }
    }

    private fun android.database.Cursor.getBooleanOrDefault(index: Int, defaultValue: Boolean = false): Boolean {
        if (isNull(index)) return defaultValue
        return runCatching { getInt(index) != 0 }.getOrElse {
            when (getString(index)?.lowercase()) {
                "1", "true" -> true
                "0", "false" -> false
                else -> defaultValue
            }
        }
    }

    suspend fun getUserMessageListrepo(tredid: Long): List<Conversation> {
        if (flag.useRealmReads) return realmRepo.getUserMessageListrepo(tredid)
        return messagerDatabase.getMessageDAO().getUserMessageList(tredid)
    }

    suspend fun getUserOTPMessageListrepo(otp: String): List<Conversation> {
        if (flag.useRealmReads) return realmRepo.getUserOTPMessageListrepo(otp)
        return messagerDatabase.getMessageDAO().getUserOTPMessageList(otp)
    }

    fun getuserpersonalMessasgeRepo(): LiveData<List<Conversation>> {
        if (flag.useRealmReads) return realmRepo.getuserpersonalMessasgeRepo()
        return messagerDatabase.getMessageDAO().getuserpersonalMessasge()
    }

    fun getuserUnknownMessasgeRepo(): LiveData<List<Conversation>> {
        if (flag.useRealmReads) return realmRepo.getuserUnknownMessasgeRepo()
        return messagerDatabase.getMessageDAO().getuserunknownMessasge()
    }

    suspend fun insertorupdatecatgoryrep(catname: Category) {
        if (flag.useRealmReads) { realmRepo.insertorupdatecatgoryrep(catname); return }
        messagerDatabase.getMessageDAO().insertOrUpdateCategory(catname)
    }

    suspend fun insertOrUpdateCategoryUsingListrepo(catname: List<Category>) {
        if (flag.useRealmReads) { realmRepo.insertOrUpdateCategoryUsingListrepo(catname); return }
        messagerDatabase.getMessageDAO().insertOrUpdateCategoryUsingList(catname)
    }

    suspend fun deleteAllCatRepo(catList: List<Category>) {
        if (flag.useRealmReads) { realmRepo.deleteAllCatRepo(catList); return }
        messagerDatabase.getMessageDAO().deleteAllCat(catList)
    }

    fun isCatNameSelectedRepo(catName: String): Boolean {
        if (flag.useRealmReads) return realmRepo.isCatNameSelectedRepo(catName)
        return messagerDatabase.getMessageDAO().isCatNameSelected(catName)
    }

    fun isCatNumberSelectedRepo(mobilenumber: String): Boolean {
        if (flag.useRealmReads) return realmRepo.isCatNumberSelectedRepo(mobilenumber)
        return messagerDatabase.getMessageDAO().isCatNumberSelected(mobilenumber)
    }

    fun getallcatrepo(): LiveData<List<Category>> {
        if (flag.useRealmReads) return realmRepo.getallcatrepo()
        return messagerDatabase.getMessageDAO().getAllCat()
    }

    suspend fun deleteDataByCatNameRepo(packageList: List<String>) {
        if (flag.useRealmReads) { realmRepo.deleteDataByCatNameRepo(packageList); return }
        messagerDatabase.getMessageDAO().deleteDataByCatName(packageList)
    }

    suspend fun deleteDataByCatNumberRepo(packageList: List<String>) {
        if (flag.useRealmReads) { realmRepo.deleteDataByCatNumberRepo(packageList); return }
        messagerDatabase.getMessageDAO().deleteDataByCatNumber(packageList)
    }

    suspend fun deleteDataByStarNumberRepo(packageList: List<String>) {
        if (flag.useRealmReads) { realmRepo.deleteDataByStarNumberRepo(packageList); return }
        messagerDatabase.getMessageDAO().deleteDataByStarNumber(packageList)
    }

    suspend fun AddCatContactAppRepo(selectedapp: List<Contact>) {
        if (flag.useRealmReads) { realmRepo.AddCatContactAppRepo(selectedapp); return }
        messagerDatabase.getMessageDAO().addCatContactApp(selectedapp)
    }

    fun getCatMobilenumberRepo(catName: String): LiveData<List<Contact>> {
        if (flag.useRealmReads) return realmRepo.getCatMobilenumberRepo(catName)
        return messagerDatabase.getMessageDAO().getCatMobilenumber(catName)
    }

    suspend fun addNumberToStarRepo(selectedapp: StarNumber) {
        if (flag.useRealmReads) { realmRepo.addNumberToStarRepo(selectedapp); return }
        messagerDatabase.getMessageDAO().addNumberToStar(selectedapp)
    }

    fun isStarNumberSelectedRepo(mobilenumber: String): Boolean {
        if (flag.useRealmReads) return realmRepo.isStarNumberSelectedRepo(mobilenumber)
        return messagerDatabase.getMessageDAO().isStarNumberSelected(mobilenumber)
    }

    fun isMessageExitsRepo(messageId: Long): Boolean {
        if (flag.useRealmReads) return realmRepo.isMessageExitsRepo(messageId)
        return messagerDatabase.getMessageDAO().isMessageExits(messageId)
    }

    fun isNewUserMessageExitsRepo(messageId: Long): Boolean {
        if (flag.useRealmReads) return realmRepo.isNewUserMessageExitsRepo(messageId)
        return messagerDatabase.getMessageDAO().isNewUserMessageExits(messageId)
    }

    fun isCatExitsRepo(messageId: String): Boolean {
        if (flag.useRealmReads) return realmRepo.isCatExitsRepo(messageId)
        return messagerDatabase.getMessageDAO().isCatExits(messageId)
    }

    fun isBrodcastExitsRepo(messageId: String): Boolean {
        if (flag.useRealmReads) return realmRepo.isBrodcastExitsRepo(messageId)
        return messagerDatabase.getMessageDAO().isBrodcastExits(messageId)
    }

    fun isPrivacyChatExitsRepo(messageId: Long): Boolean {
        if (flag.useRealmReads) return realmRepo.isPrivacyChatExitsRepo(messageId)
        return messagerDatabase.getMessageDAO().isPrivacyChatExits(messageId)
    }

    fun isUserNotificationshowRepo(messageId: Long): Boolean {
        if (flag.useRealmReads) return realmRepo.isUserNotificationshowRepo(messageId)
        return messagerDatabase.getMessageDAO().isUserNotificationshow(messageId)
    }

    fun isUserNewMessageRepo(messageId: Long): Boolean {
        if (flag.useRealmReads) return realmRepo.isUserNewMessageRepo(messageId)
        return messagerDatabase.getMessageDAO().isUserNewMessage(messageId)
    }

    suspend fun getAllMessageIdsRepo(): Set<Long> {
        if (flag.useRealmReads) return realmRepo.getAllMessageIdsRepo()
        return messagerDatabase.getMessageDAO().getAllMessageIds().toSet()
    }

    suspend fun getThreadInfoRepo(): List<ThreadInfo> {
        if (flag.useRealmReads) return realmRepo.getThreadInfoRepo()
        return messagerDatabase.getMessageDAO().getThreadInfo()
    }

    fun getAllStarContactRepo(): LiveData<List<StarNumber>> {
        if (flag.useRealmReads) return realmRepo.getAllStarContactRepo()
        return messagerDatabase.getMessageDAO().getAllStarContact()
    }

    suspend fun updatemessagestatusRepo(messageStatus: String, id: Long) {
        if (flag.useRealmReads) { realmRepo.updatemessagestatusRepo(messageStatus, id); return }
        messagerDatabase.getMessageDAO().updatemessagestatus(messageStatus, id)
        safeQueryScope.launch { runCatching { realmRepo.updatemessagestatusRepo(messageStatus, id) } }
    }

    suspend fun updatemessagestatusfrommessageRepo(messageStatus: String, id: Long) {
        if (flag.useRealmReads) { realmRepo.updatemessagestatusfrommessageRepo(messageStatus, id); return }
        messagerDatabase.getMessageDAO().updatemessagestatusfrommessage(messageStatus, id)
        safeQueryScope.launch { runCatching { realmRepo.updatemessagestatusfrommessageRepo(messageStatus, id) } }
    }

    suspend fun updatemessageCatRepo(categoryName: String, packageList: List<String>) {
        if (flag.useRealmReads) { realmRepo.updatemessageCatRepo(categoryName, packageList); return }
        messagerDatabase.getMessageDAO().updatemessageCat(categoryName, packageList)
    }

    suspend fun updatemessageCatNameRepo(categoryName: String, packageList: List<String>) {
        if (flag.useRealmReads) { realmRepo.updatemessageCatNameRepo(categoryName, packageList); return }
        messagerDatabase.getMessageDAO().updatemessageCatName(categoryName, packageList)
    }

    suspend fun updateBroadCastNameRepo(categoryName: String, treadid: Long) {
        if (flag.useRealmReads) { realmRepo.updateBroadCastNameRepo(categoryName, treadid); return }
        messagerDatabase.getMessageDAO().updateBroadCastName(categoryName, treadid)
    }

    suspend fun updateEmptymessageCatNameRepo(categoryName: String, packageList: List<String>) {
        if (flag.useRealmReads) { realmRepo.updateEmptymessageCatNameRepo(categoryName, packageList); return }
        messagerDatabase.getMessageDAO().updateEmptymessageCatName(categoryName, packageList)
    }

    suspend fun updateCatNameRepo(categoryName: String, catname: String) {
        if (flag.useRealmReads) { realmRepo.updateCatNameRepo(categoryName, catname); return }
        messagerDatabase.getMessageDAO().updateCatName(categoryName, catname)
    }

    suspend fun setisoldmessageRepo(messageStatus: Boolean, tredid: Long) {
        if (flag.useRealmReads) { realmRepo.setisoldmessageRepo(messageStatus, tredid); return }
        messagerDatabase.getMessageDAO().setisoldmessage(messageStatus, tredid)
        safeQueryScope.launch { runCatching { realmRepo.setisoldmessageRepo(messageStatus, tredid) } }
    }

    fun getnewmessagecountRepo(mobile: String, count: Int): List<Conversation> {
        if (flag.useRealmReads) return realmRepo.getnewmessagecountRepo(mobile, count)
        return messagerDatabase.getMessageDAO().getnewmessagecount(mobile, count)
    }

    suspend fun setisoldmessageCountRepo(count: Int, tredid: Long) {
        if (flag.useRealmReads) { realmRepo.setisoldmessageCountRepo(count, tredid); return }
        messagerDatabase.getMessageDAO().setisoldmessageCount(count, tredid)
        safeQueryScope.launch { runCatching { realmRepo.setisoldmessageCountRepo(count, tredid) } }
    }

    suspend fun updatemagetitleRepo(title: String, mobile: String) {
        if (flag.useRealmReads) { realmRepo.updatemagetitleRepo(title, mobile); return }
        messagerDatabase.getMessageDAO().updatemagetitle(title, mobile)
        safeQueryScope.launch { runCatching { realmRepo.updatemagetitleRepo(title, mobile) } }
    }

    suspend fun isEmpty(): Boolean {
        if (flag.useRealmReads) return realmRepo.isEmpty()
        return messagerDatabase.getMessageDAO().isEmpty()
    }

    suspend fun removeNumberToStarRepo(selectedapp: String) {
        if (flag.useRealmReads) { realmRepo.removeNumberToStarRepo(selectedapp); return }
        return messagerDatabase.getMessageDAO().removeNumberToStar(selectedapp)
    }

    suspend fun deleteConversationRepo(packageList: List<String>) {
        if (flag.useRealmReads) { realmRepo.deleteConversationRepo(packageList); return }
        safeBatch(packageList) { messagerDatabase.getMessageDAO().deleteConversation(it) }
        safeQueryScope.launch { runCatching { realmRepo.deleteConversationRepo(packageList) } }
    }

    suspend fun deleteConversationRecyclerbinRepo(packageList: List<String>) {
        if (flag.useRealmReads) { realmRepo.deleteConversationRecyclerbinRepo(packageList); return }
        messagerDatabase.getMessageDAO().deleteConversationRecyclerbin(packageList)
        safeQueryScope.launch { runCatching { realmRepo.deleteConversationRecyclerbinRepo(packageList) } }
    }

    suspend fun archivConversationRepo(packageList: List<String>) {
        if (flag.useRealmReads) { realmRepo.archivConversationRepo(packageList); return }
        messagerDatabase.getMessageDAO().archivConversation(packageList)
        safeQueryScope.launch { runCatching { realmRepo.archivConversationRepo(packageList) } }
    }

    suspend fun PrivacyConversationRepo(packageList: List<String>) {
        if (flag.useRealmReads) { realmRepo.PrivacyConversationRepo(packageList); return }
        messagerDatabase.getMessageDAO().PrivacyConversation(packageList)
        safeQueryScope.launch { runCatching { realmRepo.PrivacyConversationRepo(packageList) } }
    }

    suspend fun removearchivConversationRepo(packageList: List<String>) {
        if (flag.useRealmReads) { realmRepo.removearchivConversationRepo(packageList); return }
        messagerDatabase.getMessageDAO().removearchivConversation(packageList)
        safeQueryScope.launch { runCatching { realmRepo.removearchivConversationRepo(packageList) } }
    }

    suspend fun addnotshownotificationRepo(packageList: List<String>) {
        if (flag.useRealmReads) { realmRepo.addnotshownotificationRepo(packageList); return }
        return messagerDatabase.getMessageDAO().addnotshownotification(packageList)
    }

    suspend fun removenotshownotificationRepo(packageList: List<String>) {
        if (flag.useRealmReads) { realmRepo.removenotshownotificationRepo(packageList); return }
        return messagerDatabase.getMessageDAO().removenotshownotification(packageList)
    }

    suspend fun removePrivacyConversationRepo(packageList: List<String>) {
        if (flag.useRealmReads) { realmRepo.removePrivacyConversationRepo(packageList); return }
        messagerDatabase.getMessageDAO().removePrivacyConversation(packageList)
        safeQueryScope.launch { runCatching { realmRepo.removePrivacyConversationRepo(packageList) } }
    }

    suspend fun updateMessageRepo(messageId: Long, snippetmess: String) {
        if (flag.useRealmReads) { realmRepo.updateMessageRepo(messageId, snippetmess); return }
        return messagerDatabase.getMessageDAO().updateMessage(messageId, snippetmess)
    }

    suspend fun deleteblocknumberRepo(packageList: List<String>) {
        if (flag.useRealmReads) { realmRepo.deleteblocknumberRepo(packageList); return }
        return messagerDatabase.getMessageDAO().deleteblocknumber(packageList)
    }

    suspend fun deleteMessagerRepo(messid: Int) {
        if (flag.useRealmReads) { realmRepo.deleteMessagerRepo(messid); return }
        messagerDatabase.getMessageDAO().deleteMessage(messid)
        safeQueryScope.launch { runCatching { realmRepo.deleteMessagerRepo(messid) } }
    }

    suspend fun deleteMessagefrommessageidRepo(messid: Long) {
        if (flag.useRealmReads) { realmRepo.deleteMessagefrommessageidRepo(messid); return }
        messagerDatabase.getMessageDAO().deleteMessagefrommessageid(messid)
        safeQueryScope.launch { runCatching { realmRepo.deleteMessagefrommessageidRepo(messid) } }
    }

    suspend fun deleteConversationRecyclerbinMessidRepo(messid: Long) {
        if (flag.useRealmReads) { realmRepo.deleteConversationRecyclerbinMessidRepo(messid); return }
        return messagerDatabase.getMessageDAO().deleteConversationRecyclerbinMessid(messid)
    }

    suspend fun deleteMessagemessidRepo(packageList: List<String>) {
        if (flag.useRealmReads) { realmRepo.deleteMessagemessidRepo(packageList); return }
        return messagerDatabase.getMessageDAO().deleteMessagemessid(packageList)
    }

    suspend fun isarchivornotRepo(messid: Long): Int {
        if (flag.useRealmReads) return realmRepo.isarchivornotRepo(messid)
        return messagerDatabase.getMessageDAO().isarchivornot(messid)
    }

    suspend fun isnumberblockornotRepo(messid: Long): Int {
        if (flag.useRealmReads) return realmRepo.isnumberblockornotRepo(messid)
        return messagerDatabase.getMessageDAO().isnumberblockornot(messid)
    }

    suspend fun addnumbertoblockRepo(thredid: Long) {
        if (flag.useRealmReads) { realmRepo.addnumbertoblockRepo(thredid); return }
        messagerDatabase.getMessageDAO().addnumbertoblock(thredid)
        safeQueryScope.launch { runCatching { realmRepo.addnumbertoblockRepo(thredid) } }
    }

    suspend fun UpdateMessageTitleRepo(title: String, threadId: Long) {
        if (flag.useRealmReads) { realmRepo.UpdateMessageTitleRepo(title, threadId); return }
        messagerDatabase.getMessageDAO().UpdateMessageTitle(title, threadId)
        safeQueryScope.launch { runCatching { realmRepo.UpdateMessageTitleRepo(title, threadId) } }
    }

    suspend fun removenumbertoblockRepo(thredid: Long) {
        if (flag.useRealmReads) { realmRepo.removenumbertoblockRepo(thredid); return }
        messagerDatabase.getMessageDAO().removenumbertoblock(thredid)
        safeQueryScope.launch { runCatching { realmRepo.removenumbertoblockRepo(thredid) } }
    }

    suspend fun addmessagefoundRepo(messageId: Long) {
        if (flag.useRealmReads) { realmRepo.addmessagefoundRepo(messageId); return }
        return messagerDatabase.getMessageDAO().addmessagefound(messageId)
    }

    suspend fun removemessagefoundRepo(messageId: Long) {
        if (flag.useRealmReads) { realmRepo.removemessagefoundRepo(messageId); return }
        return messagerDatabase.getMessageDAO().removemessagefound(messageId)
    }

    fun isSelectedMessageRepo(messageId: Long): Boolean {
        if (flag.useRealmReads) return realmRepo.isSelectedMessageRepo(messageId)
        return messagerDatabase.getMessageDAO().isSelectedMessage(messageId)
    }

    suspend fun allmessagemarkasreadRepo() {
        if (flag.useRealmReads) { realmRepo.allmessagemarkasreadRepo(); return }
        return messagerDatabase.getMessageDAO().allmessagemarkasread()
    }

    suspend fun allmessagemarkasreadNewRepo() {
        if (flag.useRealmReads) { realmRepo.allmessagemarkasreadNewRepo(); return }
        return messagerDatabase.getMessageDAO().allmessagemarkasreadNew()
    }

    suspend fun updateismassageinselectedRepo() {
        if (flag.useRealmReads) { realmRepo.updateismassageinselectedRepo(); return }
        return messagerDatabase.getMessageDAO().updateismassageinselected()
    }

    suspend fun addrecentsearchRepo(recentsearch: Recentsearch) {
        if (flag.useRealmReads) { realmRepo.addrecentsearchRepo(recentsearch); return }
        return messagerDatabase.getMessageDAO().addrecentsearch(recentsearch)
    }

    suspend fun isrecentsearchExitsRepo(recentsearch: String): Boolean {
        if (flag.useRealmReads) return realmRepo.isrecentsearchExitsRepo(recentsearch)
        return messagerDatabase.getMessageDAO().isrecentsearchExits(recentsearch)
    }

    suspend fun deleterecentsearchRepo(recentsearch: String) {
        if (flag.useRealmReads) { realmRepo.deleterecentsearchRepo(recentsearch); return }
        return messagerDatabase.getMessageDAO().deleterecentsearch(recentsearch)
    }

    suspend fun deleteAllrecentsearchRepo() {
        if (flag.useRealmReads) { realmRepo.deleteAllrecentsearchRepo(); return }
        return messagerDatabase.getMessageDAO().deleteAllrecentsearch()
    }

    suspend fun deletemessageRepo(messageId: Long) {
        if (flag.useRealmReads) { realmRepo.deletemessageRepo(messageId); return }
        return messagerDatabase.getMessageDAO().deletemessage(messageId)
    }

    suspend fun removedeletemessageRepo(messageId: Long) {
        if (flag.useRealmReads) { realmRepo.removedeletemessageRepo(messageId); return }
        return messagerDatabase.getMessageDAO().removedeletemessage(messageId)
    }

    suspend fun removeTraslatedMessagemessageRepo(threadId1: Long) {
        if (flag.useRealmReads) { realmRepo.removeTraslatedMessagemessageRepo(threadId1); return }
        return messagerDatabase.getMessageDAO().removeTraslatedMessagemessage(threadId1)
    }

    fun getScheduledMessageWithIdRepo(threadId: Long, messageId: Long): Conversation {
        if (flag.useRealmReads) return realmRepo.getScheduledMessageWithIdRepo(threadId, messageId)
        return messagerDatabase.getMessageDAO().getScheduledMessageWithId(threadId, messageId)
    }

    fun getMessageWithIdRepo(threadId: Long, messageId: Long): Conversation {
        if (flag.useRealmReads) return realmRepo.getMessageWithIdRepo(threadId, messageId)
        return messagerDatabase.getMessageDAO().getMessageWithId(threadId, messageId)
    }

    suspend fun addpinConversationRepo(threadId: Long) {
        if (flag.useRealmReads) { realmRepo.addpinConversationRepo(threadId); return }
        messagerDatabase.getMessageDAO().addpinConversation(threadId)
        safeQueryScope.launch { runCatching { realmRepo.addpinConversationRepo(threadId) } }
    }

    suspend fun removepinConversationRepo(threadId: Long) {
        if (flag.useRealmReads) { realmRepo.removepinConversationRepo(threadId); return }
        messagerDatabase.getMessageDAO().removepinConversation(threadId)
        safeQueryScope.launch { runCatching { realmRepo.removepinConversationRepo(threadId) } }
    }

    fun getRecentSearchRepo(): LiveData<List<Recentsearch>> {
        if (flag.useRealmReads) return realmRepo.getRecentSearchRepo()
        return messagerDatabase.getMessageDAO().getRecentSearch()
    }

    suspend fun isCatDataAvailableRepo(searchString: String): Boolean {
        if (flag.useRealmReads) return realmRepo.isCatDataAvailableRepo(searchString)
        return messagerDatabase.getMessageDAO().isCatDataAvailable(searchString)
    }

    suspend fun getisnewMessageRepo(threadId: Long): Conversation? {
        if (flag.useRealmReads) return realmRepo.getisnewMessageRepo(threadId)
        return messagerDatabase.getMessageDAO().getisnewMessage(threadId)
    }

    suspend fun getRemainderMessageRepo(threadId: Long): Remindermodel? {
        if (flag.useRealmReads) return realmRepo.getRemainderMessageRepo(threadId)
        return messagerDatabase.getMessageDAO().getRemainderMessage(threadId)
    }

    suspend fun setiscrolltonewmessagestartRepo(messageid: Long) {
        if (flag.useRealmReads) { realmRepo.setiscrolltonewmessagestartRepo(messageid); return }
        return messagerDatabase.getMessageDAO().setiscrolltonewmessagestart(messageid)
    }

    suspend fun setiscrolltonewmessageoffRepo(messageid: Long) {
        if (flag.useRealmReads) { realmRepo.setiscrolltonewmessageoffRepo(messageid); return }
        return messagerDatabase.getMessageDAO().setiscrolltonewmessageoff(messageid)
    }

    //GalleryRepo
    suspend fun insertOrUpdateGallerydataFolderRepo(data: Foldermodel) {
        if (flag.useRealmReads) { realmRepo.insertOrUpdateGallerydataFolderRepo(data); return }
        return messagerDatabase.getGalleryDAO().insertOrUpdateGallerydataFolder(data)
    }

    suspend fun deleteAllFolderRepo() {
        if (flag.useRealmReads) { realmRepo.deleteAllFolderRepo(); return }
        return messagerDatabase.getGalleryDAO().deleteAllFolder()
    }

    suspend fun getallTFolderListRepo(): List<Foldermodel> {
        if (flag.useRealmReads) return realmRepo.getallTFolderListRepo()
        return messagerDatabase.getGalleryDAO().getallTFolderList()
    }

    suspend fun insertOrUpdateGallerydataFolderIMageRepo(data: Photo) {
        if (flag.useRealmReads) { realmRepo.insertOrUpdateGallerydataFolderIMageRepo(data); return }
        return messagerDatabase.getGalleryDAO().insertOrUpdateGallerydataFolderIMage(data)
    }

    private fun Conversation.toConversationBin(): Conversationbin {
        return Conversationbin(
            date = date,
            draftmessage = draftmessage,
            messagetype = messagetype,
            messageotp = messageotp,
            groupName = groupName,
            CategoryName = CategoryName,
            customtimeuri = customtimeuri,
            phoneNumber = phoneNumber,
            snippet = snippet,
            title = title,
            photoUri = photoUri,
            messageStatus = messageStatus,
            isbanneradshow = isbanneradshow,
            isnewmessagescroll = isnewmessagescroll,
            isonlyselectedthem = isonlyselectedthem,
            shownotification = shownotification,
            messagetraslateshow = messagetraslateshow,
            messagetraslationanimationshow = messagetraslationanimationshow,
            isgroupmessage = isgroupmessage,
            isblocknumber = isblocknumber,
            isexpandmessageview = isexpandmessageview,
            is_scheduled = is_scheduled,
            isMessagefound = isMessagefound,
            isPrivateChat = isPrivateChat,
            isarchived = isarchived,
            ispinned = ispinned,
            isnewmessage = isnewmessage ?: false,
            isnumaric = isnumaric,
            read = read,
            usesCustomTitle = usesCustomTitle,
            messageId = messageId,
            threadId = threadId,
            time = time,
            pinneddate = pinneddate,
            type = type,
            newMessageCount = newMessageCount,
            themenumber = themenumber,
            messagewithattachment = messagewithattachment
        )
    }
}
