package com.messenger.phone.number.text.sms.service.apps.realmplan

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.paging.PagingSource
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.modelclass.Foldermodel
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.modelclass.Photo
import com.messenger.phone.number.text.sms.service.apps.modelClass.Category
import com.messenger.phone.number.text.sms.service.apps.modelClass.Contact
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversationbin
import com.messenger.phone.number.text.sms.service.apps.modelClass.Recentsearch
import com.messenger.phone.number.text.sms.service.apps.modelClass.Remindermodel
import com.messenger.phone.number.text.sms.service.apps.modelClass.StarNumber
import com.messenger.phone.number.text.sms.service.apps.DAOClass.ThreadInfo
import io.github.xilinjia.krdb.Realm
import io.github.xilinjia.krdb.UpdatePolicy
import io.github.xilinjia.krdb.ext.query
import io.github.xilinjia.krdb.query.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOn
import dagger.Lazy
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealmMessagingRepo @Inject constructor(private val realmProvider: Lazy<Realm>) {

    private val realm: Realm
        get() = realmProvider.get()

    private fun genId(roomId: Int, messageId: Long?): Int = when {
        roomId > 0 -> roomId
        else -> messageId?.toInt()?.takeIf { it > 0 }
            ?: (System.currentTimeMillis() and 0x7FFF_FFFFL).toInt()
    }

    private fun tsId(): Int = (System.currentTimeMillis() and 0x7FFF_FFFFL).toInt()

    private fun conversationExists(query: String, vararg args: Any?): Boolean {
        return realm.query<ConversationRealm>(query, *args).count().find() > 0L
    }

    // ── Conversation inserts / upserts ────────────────────────────────────────

    suspend fun insertmessage(message: Conversation): Long {
        val obj = message.toConversationRealm(genId(message.id, message.messageId))
        realm.write { copyToRealm(obj, UpdatePolicy.ALL) }
        return obj.id.toLong()
    }

    suspend fun insertOrUpdateList(messages: List<Conversation>) {
        realm.write {
            messages.forEach { msg ->
                copyToRealm(msg.toConversationRealm(genId(msg.id, msg.messageId)), UpdatePolicy.ALL)
            }
        }
    }

    suspend fun insertOrUpdateRecycleBinRepo(message: Conversationbin): Long {
        val obj = message.toConversationBinRealm(genId(message.id, message.messageId))
        realm.write { copyToRealm(obj, UpdatePolicy.ALL) }
        return obj.id.toLong()
    }

    suspend fun moveConversationsToRecycleBin(threadIds: List<Long>) {
        if (threadIds.isEmpty()) return
        realm.write {
            threadIds.forEach { tid ->
                val convs = query<ConversationRealm>("threadId == $0", tid).find()
                val binEntries = convs.map { it.toBinRealm() }
                delete(convs)
                binEntries.forEach { copyToRealm(it, UpdatePolicy.ALL) }
            }
        }
    }

    // ── Reminder ──────────────────────────────────────────────────────────────

    suspend fun insertOrUpdateRemainderRepo(message: Remindermodel): Long {
        val obj = ReminderRealm().also {
            it.id = if (message.id > 0) message.id else tsId()
            it.remindertitle = message.remindertitle
            it.reminderstartdate = message.reminderstartdate
            it.reminderenddate = message.reminderenddate
            it.selected = message.selected
        }
        realm.write { copyToRealm(obj, UpdatePolicy.ALL) }
        return obj.id.toLong()
    }

    suspend fun deleteRemainderRepo(id: Int) {
        realm.write {
            query<ReminderRealm>("id == $0", id).find().let { delete(it) }
        }
    }

    suspend fun updateremaindertitleRepo(messageId: Long, titlere: String) {
        realm.write {
            query<ReminderRealm>("id == $0", messageId.toInt()).first().find()
                ?.remindertitle = titlere
        }
    }

    fun getalldataRemainderrepo(): LiveData<List<Remindermodel>> =
        realm.query<ReminderRealm>().asFlow().map { change ->
            change.list.map {
                Remindermodel(it.id, it.remindertitle, it.reminderstartdate, it.reminderenddate, it.selected)
            }
        }.asLiveData()

    // ── Conversation live queries ──────────────────────────────────────────────

    fun getallconversationunarchivrepo(): LiveData<List<Conversation>> =
        realm.query<ConversationRealm>(
            "isarchived == false AND isblocknumber == false AND isPrivateChat == false AND isScheduled == false"
        ).sort("time", Sort.DESCENDING).asFlow()
            .map { it.list.map { c -> c.toConversationSummary() } }
            .flowOn(Dispatchers.Default)
            .asLiveData(context = Dispatchers.Default)

    fun getalldataconversationrepo(): LiveData<List<Conversation>> =
        realm.query<ConversationRealm>().asFlow()
            .map { it.list.map { c -> c.toConversationSummary() } }
            .flowOn(Dispatchers.Default)
            .asLiveData(context = Dispatchers.Default)

    fun getallScheduleconversationurepo(): LiveData<List<Conversation>> =
        realm.query<ConversationRealm>("isScheduled == true").asFlow()
            .map { it.list.map { c -> c.toConversation() } }.asLiveData()

    fun getallconversationunarchivOrnotrepo(): LiveData<List<Conversation>> =
        realm.query<ConversationRealm>("isarchived == true").asFlow()
            .map { it.list.map { c -> c.toConversationSummary() } }
            .flowOn(Dispatchers.Default)
            .asLiveData(context = Dispatchers.Default)

    fun getallconversationunprivacyOrnotrepo(): LiveData<List<Conversation>> =
        realm.query<ConversationRealm>("isPrivateChat == true").asFlow()
            .map { it.list.map { c -> c.toConversationSummary() } }
            .flowOn(Dispatchers.Default)
            .asLiveData(context = Dispatchers.Default)

    fun getallconversationunblockOrnotrepo(): LiveData<List<Conversation>> =
        realm.query<ConversationRealm>("isblocknumber == true").asFlow()
            .map { it.list.map { c -> c.toConversationSummary() } }
            .flowOn(Dispatchers.Default)
            .asLiveData(context = Dispatchers.Default)

    fun getAllUserMessagerepo(tredid: Long): LiveData<List<Conversation>> =
        realm.query<ConversationRealm>()
            .sort("time", Sort.ASCENDING).asFlow()
            .map { change ->
                change.list
                    .filter { it.threadId == tredid }
                    .map { c -> c.toConversation() }
            }.asLiveData()

    fun getalldeleteconversationunrepo(): LiveData<List<Conversationbin>> =
        realm.query<ConversationBinRealm>().asFlow()
            .map { it.list.map { c -> c.toConversationBin() } }.asLiveData()

    fun getUserDeleteMessagerepo(tredid: Long): LiveData<List<Conversationbin>> =
        realm.query<ConversationBinRealm>()
            .sort("time", Sort.ASCENDING).asFlow()
            .map { change ->
                change.list
                    .filter { it.threadId == tredid }
                    .map { c -> c.toConversationBin() }
            }.asLiveData()

    fun getuserpersonalMessasgeRepo(): LiveData<List<Conversation>> =
        realm.query<ConversationRealm>("isnumaric == false")
            .sort("time", Sort.DESCENDING).asFlow()
            .map { it.list.map { c -> c.toConversationSummary() } }
            .flowOn(Dispatchers.Default)
            .asLiveData(context = Dispatchers.Default)

    fun getuserUnknownMessasgeRepo(): LiveData<List<Conversation>> =
        realm.query<ConversationRealm>("isnumaric == true")
            .sort("time", Sort.DESCENDING).asFlow()
            .map { it.list.map { c -> c.toConversationSummary() } }
            .flowOn(Dispatchers.Default)
            .asLiveData(context = Dispatchers.Default)

    // ── Paged / sync list queries ─────────────────────────────────────────────

    fun getPaginatedConversations(): PagingSource<Int, Conversation> =
        RealmConversationPagingSource(realm)

    fun getallconversationunarchivforcontactrepo(): List<Conversation> =
        realm.query<ConversationRealm>("isarchived == false").find()
            .map { it.toConversationSummary() }

    fun getallconversationunPrivacyOrnotListrepo(): List<Conversation> =
        realm.query<ConversationRealm>("isPrivateChat == false").find()
            .map { it.toConversationSummary() }

    fun getallconversationOnlyListrepo(): List<Conversation> =
        realm.query<ConversationRealm>().find().map { it.toConversationSummary() }

    fun getUserMessageListChackrepo(tredid: Long): List<Conversation> =
        realm.query<ConversationRealm>()
            .sort("time", Sort.ASCENDING)
            .find()
            .filter { it.threadId == tredid && !it.isScheduled }
            .map { it.toConversation() }

    fun getUserMessageRecyListChacktredrepo(tredid: Long): List<Conversationbin> =
        realm.query<ConversationBinRealm>()
            .sort("time", Sort.ASCENDING)
            .find()
            .filter { it.threadId == tredid }
            .map { it.toConversationBin() }

    fun getUserMessageRecyListChackrepo(tredid: String): List<Conversationbin> =
        realm.query<ConversationBinRealm>("phoneNumber == $0", tredid)
            .sort("time", Sort.ASCENDING).find().map { it.toConversationBin() }

    fun getUserMessageMobileListChackrepo(tredid: String): List<Conversation> =
        realm.query<ConversationRealm>("phoneNumber == $0", tredid)
            .find().map { it.toConversation() }

    suspend fun getUserMessageListrepo(tredid: Long): List<Conversation> =
        realm.query<ConversationRealm>().find()
            .filter { it.threadId == tredid }
            .map { it.toConversation() }

    suspend fun getUserOTPMessageListrepo(otp: String): List<Conversation> =
        realm.query<ConversationRealm>("messagetype == $0", otp).find()
            .map { it.toConversation() }

    suspend fun getallTabOnlyListrepo(): List<Category> =
        realm.query<CategoryRealm>().find().map {
            Category(it.id, it.catName, it.filterName, it.isDefaultCategory)
        }

    fun getnewmessagecountRepo(mobile: String, count: Int): List<Conversation> =
        realm.query<ConversationRealm>("phoneNumber == $0 AND newMessageCount == $1", mobile, count)
            .find().map { it.toConversation() }

    suspend fun getAllMessageIdsRepo(): Set<Long> =
        realm.query<ConversationRealm>().find().mapNotNull { it.messageId }.toSet()

    fun getScheduledMessageWithIdRepo(threadId: Long, messageId: Long): Conversation =
        realm.query<ConversationRealm>(
            "threadId == $0 AND messageId == $1 AND isScheduled == true", threadId, messageId
        ).first().find()?.toConversation()
            ?: Conversation(date = "", read = false, title = "", photoUri = null, phoneNumber = "", snippet = "", time = null, type = null, isnumaric = false, messageStatus = null)

    fun getMessageWithIdRepo(threadId: Long, messageId: Long): Conversation =
        realm.query<ConversationRealm>("threadId == $0 AND messageId == $1", threadId, messageId)
            .first().find()?.toConversation()
            ?: Conversation(date = "", read = false, title = "", photoUri = null, phoneNumber = "", snippet = "", time = null, type = null, isnumaric = false, messageStatus = null)

    suspend fun getisnewMessageRepo(threadId: Long): Conversation? =
        realm.query<ConversationRealm>("threadId == $0 AND isnewmessage == $1", threadId, true)
            .first().find()?.toConversation()

    suspend fun gettredidfromemessageidRepo(messageId: Long): Conversation? =
        realm.query<ConversationRealm>("messageId == $0", messageId).first().find()
            ?.toConversation()

    // ── Boolean checks ────────────────────────────────────────────────────────

    fun scheduleconversationexitsrepo(messageId: Long): Boolean =
        conversationExists("messageId == $0 AND isScheduled == $1", messageId, true)

    fun isMessageExitsRepo(messageId: Long): Boolean =
        conversationExists("messageId == $0", messageId)

    fun isNewUserMessageExitsRepo(threadId: Long): Boolean =
        conversationExists("threadId == $0", threadId)

    fun isPrivacyChatExitsRepo(threadId: Long): Boolean =
        conversationExists("threadId == $0 AND isPrivateChat == $1", threadId, true)

    fun isUserNotificationshowRepo(threadId: Long): Boolean =
        conversationExists("threadId == $0 AND shownotification == $1", threadId, true)

    fun isUserNewMessageRepo(threadId: Long): Boolean =
        conversationExists("threadId == $0 AND isnewmessage == $1", threadId, true)

    fun isSelectedMessageRepo(messageId: Long): Boolean =
        conversationExists("messageId == $0 AND isMessagefound == $1", messageId, true)

    fun isCatNameSelectedRepo(catName: String): Boolean =
        realm.query<CategoryRealm>("catName == $0", catName).count().find() > 0L

    fun isCatNumberSelectedRepo(mobilenumber: String): Boolean =
        realm.query<ContactRealm>("number == $0", mobilenumber).count().find() > 0L

    fun isStarNumberSelectedRepo(mobilenumber: String): Boolean =
        realm.query<StarNumberRealm>("phoneNumber == $0", mobilenumber).count().find() > 0L

    suspend fun isEmpty(): Boolean =
        realm.query<ConversationRealm>().count().find() == 0L

    fun isCatExitsRepo(cateName: String): Boolean =
        realm.query<CategoryRealm>("catName == $0", cateName).count().find() > 0L

    fun isBrodcastExitsRepo(cateName: String): Boolean =
        realm.query<ConversationRealm>("groupName == $0", cateName).count().find() > 0L

    suspend fun isarchivornotRepo(messid: Long): Int =
        if (conversationExists("threadId == $0 AND isarchived == $1", messid, true)) 1 else 0

    suspend fun isnumberblockornotRepo(messid: Long): Int =
        if (conversationExists("threadId == $0 AND isblocknumber == $1", messid, true)) 1 else 0

    suspend fun isCatDataAvailableRepo(searchString: String): Boolean =
        realm.query<ConversationRealm>("categoryName == $0", searchString).count().find() > 0L

    suspend fun isrecentsearchExitsRepo(recentsearch: String): Boolean =
        realm.query<RecentSearchRealm>("recentsearch == $0", recentsearch).count().find() > 0L

    // ── Status / field updates ────────────────────────────────────────────────

    suspend fun setisoldmessageRepo(messageStatus: Boolean, tredid: Long) {
        realm.write {
            query<ConversationRealm>("threadId == $0", tredid).find()
                .forEach { it.isnewmessage = messageStatus }
        }
    }

    suspend fun setisoldmessageCountRepo(count: Int, tredid: Long) {
        realm.write {
            query<ConversationRealm>("threadId == $0", tredid).find()
                .forEach { it.newMessageCount = count }
        }
    }

    suspend fun updatemessagestatusRepo(messageStatus: String, id: Long) {
        realm.write {
            query<ConversationRealm>("messageId == $0", id).find()
                .forEach { it.messageStatus = messageStatus }
        }
    }

    suspend fun updatemessagestatusfrommessageRepo(messageStatus: String, id: Long) {
        realm.write {
            query<ConversationRealm>("messageId == $0", id).find()
                .forEach { it.messageStatus = messageStatus }
        }
    }

    suspend fun updateMessageRepo(messageId: Long, snippetmess: String) {
        realm.write {
            query<ConversationRealm>("messageId == $0", messageId).find()
                .forEach { it.snippet = snippetmess }
        }
    }

    suspend fun UpdateMessageTitleRepo(title: String, threadId: Long) {
        realm.write {
            query<ConversationRealm>("threadId == $0", threadId).find()
                .forEach { it.title = title }
        }
    }

    suspend fun updatemagetitleRepo(title: String, mobile: String) {
        realm.write {
            query<ConversationRealm>("phoneNumber == $0", mobile).find()
                .forEach { it.title = title }
        }
    }

    suspend fun updatemessageCatRepo(categoryName: String, packageList: List<String>) {
        if (packageList.isEmpty()) return
        realm.write {
            packageList.forEach { tid ->
                val tl = tid.toLongOrNull() ?: return@forEach
                query<ConversationRealm>("threadId == $0", tl).find()
                    .forEach { it.categoryName = categoryName }
            }
        }
    }

    suspend fun updatemessageCatNameRepo(categoryName: String, packageList: List<String>) {
        if (packageList.isEmpty()) return
        realm.write {
            packageList.forEach { catName ->
                query<ConversationRealm>("categoryName == $0", catName).find()
                    .forEach { it.categoryName = categoryName }
            }
        }
    }

    suspend fun updateBroadCastNameRepo(categoryName: String, treadid: Long) {
        realm.write {
            query<ConversationRealm>("threadId == $0", treadid).find()
                .forEach { it.groupName = categoryName }
        }
    }

    suspend fun updateEmptymessageCatNameRepo(categoryName: String, packageList: List<String>) {
        if (packageList.isEmpty()) return
        realm.write {
            packageList.forEach { tid ->
                val tl = tid.toLongOrNull() ?: return@forEach
                query<ConversationRealm>("threadId == $0", tl).find()
                    .forEach { it.categoryName = categoryName }
            }
        }
    }

    suspend fun updateCatNameRepo(categoryName: String, catname: String) {
        realm.write {
            query<CategoryRealm>("catName == $0", catname).find()
                .forEach { it.catName = categoryName }
            query<ConversationRealm>("categoryName == $0", catname).find()
                .forEach { it.categoryName = categoryName }
        }
    }

    suspend fun allmessagemarkasreadRepo() {
        realm.write {
            query<ConversationRealm>().find()
                .forEach { it.isnewmessage = false; it.newMessageCount = 0 }
        }
    }

    suspend fun allmessagemarkasreadNewRepo() {
        realm.write {
            query<ConversationRealm>().find().forEach { it.newMessageCount = 0 }
        }
    }

    suspend fun updateismassageinselectedRepo() {
        realm.write {
            query<ConversationRealm>().find().forEach { it.isMessagefound = false }
        }
    }

    suspend fun addmessagefoundRepo(messageId: Long) {
        realm.write {
            query<ConversationRealm>("messageId == $0", messageId).find()
                .forEach { it.isMessagefound = true }
        }
    }

    suspend fun removemessagefoundRepo(messageId: Long) {
        realm.write {
            query<ConversationRealm>("messageId == $0", messageId).find()
                .forEach { it.isMessagefound = false }
        }
    }

    suspend fun removeTraslatedMessagemessageRepo(threadId1: Long) {
        realm.write {
            query<ConversationRealm>("threadId == $0", threadId1).find()
                .forEach { it.messagetraslateshow = false }
        }
    }

    suspend fun setiscrolltonewmessagestartRepo(messageid: Long) {
        realm.write {
            query<ConversationRealm>("messageId == $0", messageid).find()
                .forEach { it.isnewmessagescroll = true }
        }
    }

    suspend fun setiscrolltonewmessageoffRepo(messageid: Long) {
        realm.write {
            query<ConversationRealm>("messageId == $0", messageid).find()
                .forEach { it.isnewmessagescroll = false }
        }
    }

    // ── Archive / block / privacy / notification ──────────────────────────────

    suspend fun archivConversationRepo(packageList: List<String>) {
        if (packageList.isEmpty()) return
        realm.write {
            packageList.forEach { tid ->
                val tl = tid.toLongOrNull() ?: return@forEach
                query<ConversationRealm>("threadId == $0", tl).find()
                    .forEach { it.isarchived = true }
            }
        }
    }

    suspend fun removearchivConversationRepo(packageList: List<String>) {
        if (packageList.isEmpty()) return
        realm.write {
            packageList.forEach { tid ->
                val tl = tid.toLongOrNull() ?: return@forEach
                query<ConversationRealm>("threadId == $0", tl).find()
                    .forEach { it.isarchived = false }
            }
        }
    }

    suspend fun PrivacyConversationRepo(packageList: List<String>) {
        if (packageList.isEmpty()) return
        realm.write {
            packageList.forEach { tid ->
                val tl = tid.toLongOrNull() ?: return@forEach
                query<ConversationRealm>("threadId == $0", tl).find()
                    .forEach { it.isPrivateChat = true }
            }
        }
    }

    suspend fun removePrivacyConversationRepo(packageList: List<String>) {
        if (packageList.isEmpty()) return
        realm.write {
            packageList.forEach { tid ->
                val tl = tid.toLongOrNull() ?: return@forEach
                query<ConversationRealm>("threadId == $0", tl).find()
                    .forEach { it.isPrivateChat = false }
            }
        }
    }

    suspend fun addnumbertoblockRepo(thredid: Long) {
        realm.write {
            query<ConversationRealm>("threadId == $0", thredid).find()
                .forEach { it.isblocknumber = true }
        }
    }

    suspend fun removenumbertoblockRepo(thredid: Long) {
        realm.write {
            query<ConversationRealm>("threadId == $0", thredid).find()
                .forEach { it.isblocknumber = false }
        }
    }

    suspend fun addnotshownotificationRepo(packageList: List<String>) {
        if (packageList.isEmpty()) return
        realm.write {
            packageList.forEach { tid ->
                val tl = tid.toLongOrNull() ?: return@forEach
                query<ConversationRealm>("threadId == $0", tl).find()
                    .forEach { it.shownotification = false }
            }
        }
    }

    suspend fun removenotshownotificationRepo(packageList: List<String>) {
        if (packageList.isEmpty()) return
        realm.write {
            packageList.forEach { tid ->
                val tl = tid.toLongOrNull() ?: return@forEach
                query<ConversationRealm>("threadId == $0", tl).find()
                    .forEach { it.shownotification = true }
            }
        }
    }

    suspend fun addpinConversationRepo(threadId: Long) {
        realm.write {
            query<ConversationRealm>("threadId == $0", threadId).find()
                .forEach { it.ispinned = true; it.pinneddate = System.currentTimeMillis() }
        }
    }

    suspend fun removepinConversationRepo(threadId: Long) {
        realm.write {
            query<ConversationRealm>("threadId == $0", threadId).find()
                .forEach { it.ispinned = false; it.pinneddate = null }
        }
    }

    // ── Delete operations ─────────────────────────────────────────────────────

    suspend fun deletedata() {
        realm.write { delete(query<ConversationRealm>().find()) }
    }

    suspend fun deleteConversationRepo(packageList: List<String>) {
        if (packageList.isEmpty()) return
        realm.write {
            packageList.forEach { tid ->
                val tl = tid.toLongOrNull() ?: return@forEach
                delete(query<ConversationRealm>("threadId == $0", tl).find())
            }
        }
    }

    suspend fun deleteConversationRecyclerbinRepo(packageList: List<String>) {
        if (packageList.isEmpty()) return
        realm.write {
            packageList.forEach { tid ->
                val tl = tid.toLongOrNull() ?: return@forEach
                delete(query<ConversationBinRealm>("threadId == $0", tl).find())
            }
        }
    }

    suspend fun deleteblocknumberRepo(packageList: List<String>) {
        if (packageList.isEmpty()) return
        realm.write {
            packageList.forEach { tid ->
                val tl = tid.toLongOrNull() ?: return@forEach
                delete(query<ConversationRealm>("threadId == $0", tl).find())
            }
        }
    }

    suspend fun deleteMessagerRepo(messid: Int) {
        realm.write {
            delete(query<ConversationRealm>("id == $0", messid).find())
        }
    }

    suspend fun deleteMessagefrommessageidRepo(messid: Long) {
        realm.write {
            delete(query<ConversationRealm>("messageId == $0", messid).find())
        }
    }

    suspend fun deleteConversationRecyclerbinMessidRepo(messid: Long) {
        realm.write {
            delete(query<ConversationBinRealm>("messageId == $0", messid).find())
        }
    }

    suspend fun deleteMessagemessidRepo(packageList: List<String>) {
        if (packageList.isEmpty()) return
        realm.write {
            packageList.forEach { mid ->
                val ml = mid.toLongOrNull() ?: return@forEach
                delete(query<ConversationRealm>("messageId == $0", ml).find())
            }
        }
    }

    suspend fun deletemessageRepo(messageId: Long) {
        realm.write {
            delete(query<ConversationRealm>("messageId == $0", messageId).find())
        }
    }

    suspend fun removedeletemessageRepo(messageId: Long) {
        val binEntry = realm.query<ConversationBinRealm>("messageId == $0", messageId).first().find()
            ?: return
        realm.write {
            val restored = ConversationRealm().apply {
                id = binEntry.id
                date = binEntry.date ?: ""
                draftmessage = binEntry.draftmessage
                messagetype = binEntry.messagetype
                messageotp = binEntry.messageotp
                groupName = binEntry.groupName
                categoryName = binEntry.categoryName
                customtimeuri = binEntry.customtimeuri
                phoneNumber = binEntry.phoneNumber ?: ""
                snippet = binEntry.snippet ?: ""
                title = binEntry.title ?: ""
                photoUri = binEntry.photoUri
                messageStatus = binEntry.messageStatus
                isbanneradshow = binEntry.isbanneradshow
                isnewmessagescroll = binEntry.isnewmessagescroll
                isonlyselectedthem = binEntry.isonlyselectedthem
                shownotification = binEntry.shownotification
                messagetraslateshow = binEntry.messagetraslateshow
                messagetraslationanimationshow = binEntry.messagetraslationanimationshow
                isgroupmessage = binEntry.isgroupmessage
                isblocknumber = binEntry.isblocknumber
                isexpandmessageview = binEntry.isexpandmessageview
                isScheduled = binEntry.isScheduled
                isMessagefound = binEntry.isMessagefound
                isPrivateChat = binEntry.isPrivateChat
                isarchived = binEntry.isarchived
                ispinned = binEntry.ispinned
                isnewmessage = binEntry.isnewmessage
                isnumaric = binEntry.isnumaric
                read = binEntry.read
                usesCustomTitle = binEntry.usesCustomTitle
                this.messageId = binEntry.messageId
                threadId = binEntry.threadId
                time = binEntry.time
                pinneddate = binEntry.pinneddate
                type = binEntry.type
                newMessageCount = binEntry.newMessageCount
                themenumber = binEntry.themenumber
            }
            copyToRealm(restored, UpdatePolicy.ALL)
            delete(query<ConversationBinRealm>("messageId == $0", messageId).find())
        }
    }

    // ── Category operations ───────────────────────────────────────────────────

    suspend fun insertorupdatecatgoryrep(catname: Category) {
        val obj = CategoryRealm().also {
            it.id = if (catname.id > 0) catname.id else tsId()
            it.catName = catname.catName
            it.filterName = catname.filterName
            it.isDefaultCategory = catname.isDefaultCategory
        }
        realm.write { copyToRealm(obj, UpdatePolicy.ALL) }
    }

    suspend fun insertOrUpdateCategoryUsingListrepo(catname: List<Category>) {
        realm.write {
            catname.forEach { cat ->
                val obj = CategoryRealm().also {
                    it.id = if (cat.id > 0) cat.id else tsId()
                    it.catName = cat.catName
                    it.filterName = cat.filterName
                    it.isDefaultCategory = cat.isDefaultCategory
                }
                copyToRealm(obj, UpdatePolicy.ALL)
            }
        }
    }

    suspend fun deleteAllCatRepo(catList: List<Category>) {
        realm.write {
            catList.forEach { cat ->
                delete(query<CategoryRealm>("catName == $0", cat.catName).find())
            }
        }
    }

    fun getallcatrepo(): LiveData<List<Category>> =
        realm.query<CategoryRealm>().asFlow().map { change ->
            change.list.map { Category(it.id, it.catName, it.filterName, it.isDefaultCategory) }
        }.asLiveData()

    suspend fun deleteDataByCatNameRepo(packageList: List<String>) {
        if (packageList.isEmpty()) return
        realm.write {
            packageList.forEach { catName ->
                delete(query<CategoryRealm>("catName == $0", catName).find())
            }
        }
    }

    suspend fun deleteDataByCatNumberRepo(packageList: List<String>) {
        if (packageList.isEmpty()) return
        realm.write {
            packageList.forEach { number ->
                delete(query<ContactRealm>("number == $0", number).find())
            }
        }
    }

    suspend fun deleteDataByStarNumberRepo(packageList: List<String>) {
        if (packageList.isEmpty()) return
        realm.write {
            packageList.forEach { number ->
                delete(query<StarNumberRealm>("phoneNumber == $0", number).find())
            }
        }
    }

    suspend fun AddCatContactAppRepo(selectedapp: List<Contact>) {
        realm.write {
            selectedapp.forEach { contact ->
                val obj = ContactRealm().also {
                    it.id = if (contact.id > 0) contact.id else tsId()
                    it.name = contact.name
                    it.number = contact.number
                    it.contactId = contact.contactId
                    it.onlynumber = contact.onlynumber
                    it.catName = contact.CatName
                }
                copyToRealm(obj, UpdatePolicy.ALL)
            }
        }
    }

    fun getCatMobilenumberRepo(catName: String): LiveData<List<Contact>> =
        realm.query<ContactRealm>("catName == $0", catName).asFlow().map { change ->
            change.list.map { Contact(it.name, it.number, it.contactId, it.onlynumber, it.catName, it.id) }
        }.asLiveData()

    // ── Star numbers ──────────────────────────────────────────────────────────

    suspend fun addNumberToStarRepo(selectedapp: StarNumber) {
        val obj = StarNumberRealm().also {
            it.id = if (selectedapp.id > 0) selectedapp.id else tsId()
            it.date = selectedapp.date
            it.read = selectedapp.read
            it.title = selectedapp.title
            it.photoUri = selectedapp.photoUri
            it.usesCustomTitle = selectedapp.usesCustomTitle
            it.phoneNumber = selectedapp.phoneNumber
            it.snippet = selectedapp.snippet
            it.time = selectedapp.time
            it.type = selectedapp.type
            it.isnumaric = selectedapp.isnumaric
        }
        realm.write { copyToRealm(obj, UpdatePolicy.ALL) }
    }

    suspend fun removeNumberToStarRepo(selectedapp: String) {
        realm.write {
            delete(query<StarNumberRealm>("phoneNumber == $0", selectedapp).find())
        }
    }

    fun getAllStarContactRepo(): LiveData<List<StarNumber>> =
        realm.query<StarNumberRealm>().asFlow().map { change ->
            change.list.map {
                StarNumber(it.id, it.date, it.read, it.title, it.photoUri, it.usesCustomTitle,
                    it.phoneNumber, it.snippet, it.time, it.type, it.isnumaric)
            }
        }.asLiveData()

    // ── Recent search ─────────────────────────────────────────────────────────

    suspend fun addrecentsearchRepo(recentsearch: Recentsearch) {
        val obj = RecentSearchRealm().also {
            it.id = if (recentsearch.id > 0) recentsearch.id else tsId()
            it.recentsearch = recentsearch.recentsearch
        }
        realm.write { copyToRealm(obj, UpdatePolicy.ALL) }
    }

    suspend fun deleterecentsearchRepo(recentsearch: String) {
        realm.write {
            delete(query<RecentSearchRealm>("recentsearch == $0", recentsearch).find())
        }
    }

    suspend fun deleteAllrecentsearchRepo() {
        realm.write { delete(query<RecentSearchRealm>().find()) }
    }

    fun getRecentSearchRepo(): LiveData<List<Recentsearch>> =
        realm.query<RecentSearchRealm>().asFlow().map { change ->
            change.list.map { Recentsearch(it.id, it.recentsearch) }
        }.asLiveData()

    // ── Reminder by id ───────────────────────────────────────────────────────

    suspend fun getRemainderMessageRepo(id: Long): Remindermodel? =
        realm.query<ReminderRealm>("id == $0", id.toInt()).first().find()?.let {
            Remindermodel(it.id, it.remindertitle, it.reminderstartdate, it.reminderenddate, it.selected)
        }

    // ── Thread info projection ────────────────────────────────────────────────

    suspend fun getThreadInfoRepo(): List<ThreadInfo> =
        realm.query<ConversationRealm>().find().map {
            ThreadInfo(
                threadId = it.threadId ?: 0L,
                title = it.title,
                isgroupmessage = it.isgroupmessage,
                groupName = it.groupName,
                phoneNumber = it.phoneNumber
            )
        }

    // ── Gallery ───────────────────────────────────────────────────────────────

    suspend fun insertOrUpdateGallerydataFolderRepo(data: Foldermodel) {
        val obj = FolderRealm().also {
            it.id = data.id?.takeIf { id -> id > 0 } ?: tsId()
            it.foldername = data.foldername
            it.folderimgcount = data.folderimgcount
            it.folderthumimage = data.folderthumimage
            it.folderpath = data.folderpath
        }
        realm.write { copyToRealm(obj, UpdatePolicy.ALL) }
    }

    suspend fun deleteAllFolderRepo() {
        realm.write { delete(query<FolderRealm>().find()) }
    }

    suspend fun getallTFolderListRepo(): List<Foldermodel> =
        realm.query<FolderRealm>().find().map {
            Foldermodel(it.id, it.foldername, it.folderimgcount, it.folderthumimage, it.folderpath)
        }

    suspend fun insertOrUpdateGallerydataFolderIMageRepo(data: Photo) {
        val obj = PhotoRealm().also {
            it.id = data.id?.takeIf { id -> id > 0 } ?: tsId()
            it.path = data.path
            it.position = data.position
            it.selected = data.selected
            it.lastModifieddate = data.lastModifieddate
        }
        realm.write { copyToRealm(obj, UpdatePolicy.ALL) }
    }
}
