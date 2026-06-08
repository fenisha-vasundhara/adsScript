package com.messenger.phone.number.text.sms.service.apps.realmplan

import android.content.Context
import android.database.Cursor
import android.util.Log
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.modelclass.Foldermodel
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.modelclass.Photo
import com.messenger.phone.number.text.sms.service.apps.DataBase.Converters
import com.messenger.phone.number.text.sms.service.apps.DataBase.MessagerDatabase
import com.messenger.phone.number.text.sms.service.apps.data.Attachment
import com.messenger.phone.number.text.sms.service.apps.data.MessageAttachment
import com.messenger.phone.number.text.sms.service.apps.modelClass.Category
import com.messenger.phone.number.text.sms.service.apps.modelClass.CategoryNumber
import com.messenger.phone.number.text.sms.service.apps.modelClass.Contact
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversationbin
import com.messenger.phone.number.text.sms.service.apps.modelClass.Recentsearch
import com.messenger.phone.number.text.sms.service.apps.modelClass.Remindermodel
import com.messenger.phone.number.text.sms.service.apps.modelClass.StarNumber
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Full migration pipeline design without Realm wiring.
 *
 * Current behavior:
 * - Reads Room safely in batches
 * - Builds Realm model payloads
 * - Writes through RealmMigrationStore
 * - Leaves Room untouched unless a later integration explicitly validates and deletes it
 *
 * Safety rules:
 * - Migration never marks success before validation
 * - Failure never blocks Room usage
 * - Legacy Room files are not deleted here by default
 */
class RoomToRealmMigrator(
    private val appContext: Context,
    private val roomDatabase: MessagerDatabase,
    private val migrationStore: RealmMigrationStore = NoOpRealmMigrationStore()
) {
    private val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val mutex = Mutex()
    private val converters = Converters()

    suspend fun migrateIfNeeded(): RoomToRealmMigrationResult {
        if (prefs.getBoolean(KEY_MIGRATION_DONE, false)) {
            return RoomToRealmMigrationResult.AlreadyMigrated
        }

        return mutex.withLock {
            if (prefs.getBoolean(KEY_MIGRATION_DONE, false)) {
                return@withLock RoomToRealmMigrationResult.AlreadyMigrated
            }

            runCatching {
                markMigrationStarted()
                migrationStore.beginMigration()
                migrationStore.clearAll()

                val summary = RoomToRealmSummary(
                    conversationCount = migrateConversations(),
                    conversationBinCount = migrateConversationBins(),
                    categoryCount = migrateCategories(),
                    categoryNumberCount = migrateCategoryNumbers(),
                    contactCount = migrateContacts(),
                    recentSearchCount = migrateRecentSearches(),
                    reminderCount = migrateReminders(),
                    starNumberCount = migrateStarNumbers(),
                    folderCount = migrateFolders(),
                    photoCount = migratePhotos(),
                    messageAttachmentCount = migrateMessageAttachments(),
                    standaloneAttachmentCount = migrateStandaloneAttachments()
                )

                val validated = migrationStore.validateMigration(summary)
                if (!validated) {
                    error("Realm validation failed. Room remains the source of truth.")
                }

                migrationStore.finalizeMigration()
                markMigrationSuccess(summary)
                RoomToRealmMigrationResult.Success
            }.getOrElse { throwable ->
                Log.e(TAG, "Room to Realm migration failed", throwable)
                runCatching { migrationStore.abortMigration(throwable) }
                markMigrationFailed(throwable)
                RoomToRealmMigrationResult.Failed(throwable)
            }
        }
    }

    fun isMigrationDone(): Boolean = prefs.getBoolean(KEY_MIGRATION_DONE, false)

    fun isMigrationRunning(): Boolean = prefs.getBoolean(KEY_MIGRATION_RUNNING, false)

    fun lastFailure(): String? = prefs.getString(KEY_LAST_ERROR, null)

    fun clearMigrationFlagsForDebugOnly() {
        prefs.edit().clear().apply()
    }

    private suspend fun migrateConversations(): Int {
        return migrateCursorTable(
            tableName = "Conversation",
            orderBy = "id ASC",
            batchSize = CHAT_BATCH_SIZE
        ) { cursor ->
            RoomToRealmBatch(
                conversations = listOf(cursor.toConversation().toRealmModel())
            )
        }
    }

    private suspend fun migrateConversationBins(): Int {
        return migrateCursorTable(
            tableName = "Conversationbin",
            orderBy = "id ASC",
            batchSize = CHAT_BATCH_SIZE
        ) { cursor ->
            RoomToRealmBatch(
                conversationBins = listOf(cursor.toConversationBin().toRealmModel())
            )
        }
    }

    private suspend fun migrateCategories(): Int {
        val rows = roomDatabase.getMessageDAO().getallTabOnlyList()
        return writeSmallTable(rows.map { it.toRealmModel() }) { items ->
            RoomToRealmBatch(categories = items)
        }
    }

    private suspend fun migrateCategoryNumbers(): Int {
        return migrateCursorTable(
            tableName = "CategoryNumber",
            orderBy = "id ASC",
            batchSize = DEFAULT_BATCH_SIZE
        ) { cursor ->
            RoomToRealmBatch(
                categoryNumbers = listOf(cursor.toCategoryNumber().toRealmModel())
            )
        }
    }

    private suspend fun migrateContacts(): Int {
        return migrateCursorTable(
            tableName = "contact",
            orderBy = "id ASC",
            batchSize = DEFAULT_BATCH_SIZE
        ) { cursor ->
            RoomToRealmBatch(
                contacts = listOf(cursor.toContact().toRealmModel())
            )
        }
    }

    private suspend fun migrateRecentSearches(): Int {
        return migrateCursorTable(
            tableName = "Recentsearch",
            orderBy = "id ASC",
            batchSize = DEFAULT_BATCH_SIZE
        ) { cursor ->
            RoomToRealmBatch(
                recentSearches = listOf(cursor.toRecentSearch().toRealmModel())
            )
        }
    }

    private suspend fun migrateReminders(): Int {
        return migrateCursorTable(
            tableName = "reminder",
            orderBy = "id ASC",
            batchSize = DEFAULT_BATCH_SIZE
        ) { cursor ->
            RoomToRealmBatch(
                reminders = listOf(cursor.toReminder().toRealmModel())
            )
        }
    }

    private suspend fun migrateStarNumbers(): Int {
        return migrateCursorTable(
            tableName = "starnumber",
            orderBy = "id ASC",
            batchSize = DEFAULT_BATCH_SIZE
        ) { cursor ->
            RoomToRealmBatch(
                starNumbers = listOf(cursor.toStarNumber().toRealmModel())
            )
        }
    }

    private suspend fun migrateFolders(): Int {
        val rows = roomDatabase.getGalleryDAO().getallTFolderList()
        return writeSmallTable(rows.map { it.toRealmModel() }) { items ->
            RoomToRealmBatch(folders = items)
        }
    }

    private suspend fun migratePhotos(): Int {
        return migrateCursorTable(
            tableName = "Photo",
            orderBy = "id ASC",
            batchSize = DEFAULT_BATCH_SIZE
        ) { cursor ->
            RoomToRealmBatch(
                photos = listOf(cursor.toPhoto().toRealmModel())
            )
        }
    }

    private suspend fun migrateMessageAttachments(): Int {
        return migrateCursorTable(
            tableName = "message_attachments",
            orderBy = "id ASC",
            batchSize = DEFAULT_BATCH_SIZE
        ) { cursor ->
            RoomToRealmBatch(
                messageAttachments = listOf(cursor.toMessageAttachment().toRealmModel())
            )
        }
    }

    private suspend fun migrateStandaloneAttachments(): Int {
        return migrateCursorTable(
            tableName = "attachments",
            orderBy = "id ASC",
            batchSize = DEFAULT_BATCH_SIZE
        ) { cursor ->
            RoomToRealmBatch(
                standaloneAttachments = listOf(cursor.toAttachment().toRealmModel())
            )
        }
    }

    private suspend fun <T> writeSmallTable(
        rows: List<T>,
        batchFactory: (List<T>) -> RoomToRealmBatch
    ): Int {
        if (rows.isEmpty()) return 0

        rows.chunked(DEFAULT_BATCH_SIZE).forEach { batch ->
            migrationStore.writeBatch(batchFactory(batch))
        }
        return rows.size
    }

    private suspend fun migrateCursorTable(
        tableName: String,
        orderBy: String,
        batchSize: Int,
        mapRow: (Cursor) -> RoomToRealmBatch
    ): Int {
        val db = roomDatabase.openHelper.readableDatabase
        var offset = 0
        var total = 0

        while (true) {
            val sql = "SELECT * FROM $tableName ORDER BY $orderBy LIMIT $batchSize OFFSET $offset"
            val batch = db.query(sql).use { cursor ->
                buildList {
                    while (cursor.moveToNext()) {
                        add(mapRow(cursor))
                    }
                }
            }

            if (batch.isEmpty()) break

            // Merge the per-row batches into one page-level write transaction.
            migrationStore.writeBatch(batch.merge())
            total += batch.size
            offset += batch.size
        }

        return total
    }

    private fun List<RoomToRealmBatch>.merge() = RoomToRealmBatch(
        conversations         = flatMap { it.conversations },
        conversationBins      = flatMap { it.conversationBins },
        categories            = flatMap { it.categories },
        categoryNumbers       = flatMap { it.categoryNumbers },
        contacts              = flatMap { it.contacts },
        recentSearches        = flatMap { it.recentSearches },
        reminders             = flatMap { it.reminders },
        starNumbers           = flatMap { it.starNumbers },
        folders               = flatMap { it.folders },
        photos                = flatMap { it.photos },
        messageAttachments    = flatMap { it.messageAttachments },
        standaloneAttachments = flatMap { it.standaloneAttachments }
    )

    private fun markMigrationStarted() {
        prefs.edit()
            .putBoolean(KEY_MIGRATION_RUNNING, true)
            .remove(KEY_LAST_ERROR)
            .apply()
    }

    private fun markMigrationSuccess(summary: RoomToRealmSummary) {
        prefs.edit()
            .putBoolean(KEY_MIGRATION_RUNNING, false)
            .putBoolean(KEY_MIGRATION_DONE, true)
            .putString(KEY_LAST_SUMMARY, summary.toDebugString())
            .apply()
    }

    private fun markMigrationFailed(throwable: Throwable) {
        prefs.edit()
            .putBoolean(KEY_MIGRATION_RUNNING, false)
            .putString(KEY_LAST_ERROR, throwable.stackTraceToString())
            .apply()
    }

    private fun RoomToRealmSummary.toDebugString(): String {
        return "conversation=$conversationCount, bin=$conversationBinCount, category=$categoryCount, " +
            "categoryNumber=$categoryNumberCount, contact=$contactCount, recentSearch=$recentSearchCount, " +
            "reminder=$reminderCount, star=$starNumberCount, folder=$folderCount, photo=$photoCount, " +
            "messageAttachment=$messageAttachmentCount, attachment=$standaloneAttachmentCount"
    }

    private fun Cursor.toConversation(): Conversation {
        return Conversation(
            id = getIntOrDefault("id"),
            date = getStringOrDefault("date"),
            read = getBooleanOrDefault("read"),
            title = getStringOrDefault("title"),
            photoUri = getNullableString("photoUri"),
            usesCustomTitle = getBooleanOrDefault("usesCustomTitle"),
            phoneNumber = getStringOrDefault("phoneNumber"),
            snippet = getStringOrDefault("snippet"),
            time = getNullableLong("time"),
            type = getNullableInt("type"),
            isnumaric = getBooleanOrDefault("isnumaric"),
            messageStatus = getNullableString("messageStatus"),
            isnewmessage = getNullableBoolean("isnewmessage"),
            newMessageCount = getNullableInt("newMessageCount"),
            messageId = getNullableLong("messageId"),
            threadId = getNullableLong("threadId"),
            isarchived = getBooleanOrDefault("isarchived"),
            ispinned = getBooleanOrDefault("ispinned"),
            pinneddate = getNullableLong("pinneddate"),
            isblocknumber = getBooleanOrDefault("isblocknumber"),
            isexpandmessageview = getBooleanOrDefault("isexpandmessageview", true),
            is_scheduled = getBooleanOrDefault("is_scheduled"),
            isMessagefound = getBooleanOrDefault("isMessagefound"),
            isPrivateChat = getBooleanOrDefault("isPrivateChat"),
            draftmessage = getNullableString("draftmessage"),
            messagetype = getNullableString("messagetype"),
            messageotp = getNullableString("messageotp"),
            shownotification = getBooleanOrDefault("shownotification", true),
            messagetraslateshow = getBooleanOrDefault("messagetraslateshow"),
            messagetraslationanimationshow = getBooleanOrDefault("messagetraslationanimationshow"),
            isgroupmessage = getBooleanOrDefault("isgroupmessage"),
            groupName = getNullableString("groupName"),
            CategoryName = getNullableString("CategoryName"),
            isnewmessagescroll = getBooleanOrDefault("isnewmessagescroll"),
            isonlyselectedthem = getBooleanOrDefault("isonlyselectedthem"),
            themenumber = getNullableInt("themenumber"),
            customtimeuri = getNullableString("customtimeuri"),
            isbanneradshow = getBooleanOrDefault("isbanneradshow"),
            messagewithattachment = getNullableString("messagewithattachment")
                ?.let(converters::jsonToMessageAttachment)
        )
    }

    private fun Cursor.toConversationBin(): Conversationbin {
        return Conversationbin(
            id = getIntOrDefault("id"),
            date = getNullableString("date"),
            draftmessage = getNullableString("draftmessage"),
            messagetype = getNullableString("messagetype"),
            messageotp = getNullableString("messageotp"),
            groupName = getNullableString("groupName"),
            CategoryName = getNullableString("CategoryName"),
            customtimeuri = getNullableString("customtimeuri"),
            phoneNumber = getNullableString("phoneNumber"),
            snippet = getNullableString("snippet"),
            title = getNullableString("title"),
            photoUri = getNullableString("photoUri"),
            messageStatus = getNullableString("messageStatus"),
            isbanneradshow = getBooleanOrDefault("isbanneradshow"),
            isnewmessagescroll = getBooleanOrDefault("isnewmessagescroll"),
            isonlyselectedthem = getBooleanOrDefault("isonlyselectedthem"),
            shownotification = getBooleanOrDefault("shownotification", true),
            messagetraslateshow = getBooleanOrDefault("messagetraslateshow"),
            messagetraslationanimationshow = getBooleanOrDefault("messagetraslationanimationshow"),
            isgroupmessage = getBooleanOrDefault("isgroupmessage"),
            isblocknumber = getBooleanOrDefault("isblocknumber"),
            isexpandmessageview = getBooleanOrDefault("isexpandmessageview", true),
            is_scheduled = getBooleanOrDefault("is_scheduled"),
            isMessagefound = getBooleanOrDefault("isMessagefound"),
            isPrivateChat = getBooleanOrDefault("isPrivateChat"),
            isarchived = getBooleanOrDefault("isarchived"),
            ispinned = getBooleanOrDefault("ispinned"),
            isnewmessage = getBooleanOrDefault("isnewmessage"),
            isnumaric = getBooleanOrDefault("isnumaric"),
            read = getBooleanOrDefault("read"),
            usesCustomTitle = getBooleanOrDefault("usesCustomTitle"),
            messageId = getNullableLong("messageId"),
            threadId = getNullableLong("threadId"),
            time = getNullableLong("time"),
            pinneddate = getNullableLong("pinneddate"),
            type = getNullableInt("type"),
            newMessageCount = getNullableInt("newMessageCount"),
            themenumber = getNullableInt("themenumber"),
            messagewithattachment = getNullableString("messagewithattachment")
                ?.let(converters::jsonToMessageAttachment)
        )
    }

    private fun Cursor.toCategoryNumber(): CategoryNumber {
        return CategoryNumber(
            id = getIntOrDefault("id"),
            catName = getStringOrDefault("catName"),
            date = getStringOrDefault("date"),
            read = getBooleanOrDefault("read"),
            title = getStringOrDefault("title"),
            photoUri = getNullableString("photoUri"),
            usesCustomTitle = getBooleanOrDefault("usesCustomTitle"),
            phoneNumber = getStringOrDefault("phoneNumber"),
            snippet = getStringOrDefault("snippet"),
            time = getNullableLong("time"),
            type = getNullableInt("type"),
            isnumaric = getBooleanOrDefault("isnumaric")
        )
    }

    private fun Cursor.toContact(): Contact {
        return Contact(
            name = getStringOrDefault("name"),
            number = getStringOrDefault("number"),
            contactId = getIntOrDefault("contactId"),
            onlynumber = getStringOrDefault("onlynumber"),
            CatName = getStringOrDefault("CatName", "No"),
            id = getIntOrDefault("id")
        )
    }

    private fun Cursor.toRecentSearch(): Recentsearch {
        return Recentsearch(
            id = getIntOrDefault("id"),
            recentsearch = getStringOrDefault("recentsearch")
        )
    }

    private fun Cursor.toReminder(): Remindermodel {
        return Remindermodel(
            id = getIntOrDefault("id"),
            remindertitle = getNullableString("remindertitle"),
            reminderstartdate = getNullableString("reminderstartdate"),
            reminderenddate = getNullableString("reminderenddate"),
            selected = getBooleanOrDefault("selected")
        )
    }

    private fun Cursor.toStarNumber(): StarNumber {
        return StarNumber(
            id = getIntOrDefault("id"),
            date = getStringOrDefault("date"),
            read = getBooleanOrDefault("read"),
            title = getStringOrDefault("title"),
            photoUri = getNullableString("photoUri"),
            usesCustomTitle = getBooleanOrDefault("usesCustomTitle"),
            phoneNumber = getStringOrDefault("phoneNumber"),
            snippet = getStringOrDefault("snippet"),
            time = getNullableLong("time"),
            type = getNullableInt("type"),
            isnumaric = getBooleanOrDefault("isnumaric")
        )
    }

    private fun Cursor.toPhoto(): Photo {
        return Photo(
            id = getIntOrDefault("id"),
            path = getStringOrDefault("path"),
            position = getIntOrDefault("position"),
            selected = getBooleanOrDefault("selected"),
            lastModifieddate = getLongOrDefault("lastModifieddate")
        )
    }

    private fun Cursor.toMessageAttachment(): MessageAttachment {
        return MessageAttachment(
            id = getLongOrDefault("id"),
            text = getStringOrDefault("text"),
            attachments = converters.jsonToAttachmentList(getNullableString("attachments")) ?: arrayListOf()
        )
    }

    private fun Cursor.toAttachment(): Attachment {
        return Attachment(
            id = getNullableLong("id"),
            messageId = getLongOrDefault("message_id"),
            uriString = getStringOrDefault("uri_string"),
            mimetype = getStringOrDefault("mimetype"),
            width = getIntOrDefault("width"),
            height = getIntOrDefault("height"),
            filename = getStringOrDefault("filename")
        )
    }

    private fun Category.toRealmModel(): CategoryRealm {
        return CategoryRealm().apply {
            id = this@toRealmModel.id
            catName = this@toRealmModel.catName
            filterName = this@toRealmModel.filterName
            isDefaultCategory = this@toRealmModel.isDefaultCategory
        }
    }

    private fun CategoryNumber.toRealmModel(): CategoryNumberRealm {
        return CategoryNumberRealm().apply {
            id = this@toRealmModel.id
            catName = this@toRealmModel.catName
            date = this@toRealmModel.date
            read = this@toRealmModel.read
            title = this@toRealmModel.title
            photoUri = this@toRealmModel.photoUri
            usesCustomTitle = this@toRealmModel.usesCustomTitle
            phoneNumber = this@toRealmModel.phoneNumber
            snippet = this@toRealmModel.snippet
            time = this@toRealmModel.time
            type = this@toRealmModel.type
            isnumaric = this@toRealmModel.isnumaric
        }
    }

    private fun Contact.toRealmModel(): ContactRealm {
        return ContactRealm().apply {
            id = this@toRealmModel.id
            name = this@toRealmModel.name
            number = this@toRealmModel.number
            contactId = this@toRealmModel.contactId
            onlynumber = this@toRealmModel.onlynumber
            catName = this@toRealmModel.CatName
        }
    }

    private fun Recentsearch.toRealmModel(): RecentSearchRealm {
        return RecentSearchRealm().apply {
            id = this@toRealmModel.id
            recentsearch = this@toRealmModel.recentsearch
        }
    }

    private fun Remindermodel.toRealmModel(): ReminderRealm {
        return ReminderRealm().apply {
            id = this@toRealmModel.id
            remindertitle = this@toRealmModel.remindertitle
            reminderstartdate = this@toRealmModel.reminderstartdate
            reminderenddate = this@toRealmModel.reminderenddate
            selected = this@toRealmModel.selected
        }
    }

    private fun StarNumber.toRealmModel(): StarNumberRealm {
        return StarNumberRealm().apply {
            id = this@toRealmModel.id
            date = this@toRealmModel.date
            read = this@toRealmModel.read
            title = this@toRealmModel.title
            photoUri = this@toRealmModel.photoUri
            usesCustomTitle = this@toRealmModel.usesCustomTitle
            phoneNumber = this@toRealmModel.phoneNumber
            snippet = this@toRealmModel.snippet
            time = this@toRealmModel.time
            type = this@toRealmModel.type
            isnumaric = this@toRealmModel.isnumaric
        }
    }

    private fun Foldermodel.toRealmModel(): FolderRealm {
        return FolderRealm().apply {
            id = this@toRealmModel.id ?: 0
            foldername = this@toRealmModel.foldername
            folderimgcount = this@toRealmModel.folderimgcount
            folderthumimage = this@toRealmModel.folderthumimage
            folderpath = this@toRealmModel.folderpath
        }
    }

    private fun Photo.toRealmModel(): PhotoRealm {
        return PhotoRealm().apply {
            id = this@toRealmModel.id ?: 0
            path = this@toRealmModel.path
            position = this@toRealmModel.position
            selected = this@toRealmModel.selected
            lastModifieddate = this@toRealmModel.lastModifieddate
        }
    }

    private fun Attachment.toRealmModel(): AttachmentRealm {
        return AttachmentRealm().apply {
            roomId = this@toRealmModel.id
            messageId = this@toRealmModel.messageId
            uriString = this@toRealmModel.uriString
            mimetype = this@toRealmModel.mimetype
            width = this@toRealmModel.width
            height = this@toRealmModel.height
            filename = this@toRealmModel.filename
            primaryKey = buildAttachmentPrimaryKey(this@toRealmModel)
        }
    }

    private fun MessageAttachment.toRealmModel(): MessageAttachmentRealm {
        return MessageAttachmentRealm().apply {
            id = this@toRealmModel.id
            text = this@toRealmModel.text
            attachments.addAll(this@toRealmModel.attachments.map { it.toRealmModel() })
        }
    }

    private fun Conversation.toRealmModel(): ConversationRealm {
        return ConversationRealm().apply {
            id = this@toRealmModel.id
            date = this@toRealmModel.date
            read = this@toRealmModel.read
            title = this@toRealmModel.title
            photoUri = this@toRealmModel.photoUri
            usesCustomTitle = this@toRealmModel.usesCustomTitle
            phoneNumber = this@toRealmModel.phoneNumber
            snippet = this@toRealmModel.snippet
            time = this@toRealmModel.time
            type = this@toRealmModel.type
            isnumaric = this@toRealmModel.isnumaric
            messageStatus = this@toRealmModel.messageStatus
            isnewmessage = this@toRealmModel.isnewmessage
            newMessageCount = this@toRealmModel.newMessageCount
            messageId = this@toRealmModel.messageId
            threadId = this@toRealmModel.threadId
            isarchived = this@toRealmModel.isarchived
            ispinned = this@toRealmModel.ispinned
            pinneddate = this@toRealmModel.pinneddate
            isblocknumber = this@toRealmModel.isblocknumber
            isexpandmessageview = this@toRealmModel.isexpandmessageview
            isScheduled = this@toRealmModel.is_scheduled
            isMessagefound = this@toRealmModel.isMessagefound
            isPrivateChat = this@toRealmModel.isPrivateChat
            draftmessage = this@toRealmModel.draftmessage
            messagetype = this@toRealmModel.messagetype
            messageotp = this@toRealmModel.messageotp
            shownotification = this@toRealmModel.shownotification
            messagetraslateshow = this@toRealmModel.messagetraslateshow
            messagetraslationanimationshow = this@toRealmModel.messagetraslationanimationshow
            isgroupmessage = this@toRealmModel.isgroupmessage
            groupName = this@toRealmModel.groupName
            categoryName = this@toRealmModel.CategoryName
            isnewmessagescroll = this@toRealmModel.isnewmessagescroll
            isonlyselectedthem = this@toRealmModel.isonlyselectedthem
            themenumber = this@toRealmModel.themenumber
            customtimeuri = this@toRealmModel.customtimeuri
            isbanneradshow = this@toRealmModel.isbanneradshow
            messagewithattachment = this@toRealmModel.messagewithattachment?.toRealmModel()
        }
    }

    private fun Conversationbin.toRealmModel(): ConversationBinRealm {
        return ConversationBinRealm().apply {
            id = this@toRealmModel.id
            date = this@toRealmModel.date
            draftmessage = this@toRealmModel.draftmessage
            messagetype = this@toRealmModel.messagetype
            messageotp = this@toRealmModel.messageotp
            groupName = this@toRealmModel.groupName
            categoryName = this@toRealmModel.CategoryName
            customtimeuri = this@toRealmModel.customtimeuri
            phoneNumber = this@toRealmModel.phoneNumber
            snippet = this@toRealmModel.snippet
            title = this@toRealmModel.title
            photoUri = this@toRealmModel.photoUri
            messageStatus = this@toRealmModel.messageStatus
            isbanneradshow = this@toRealmModel.isbanneradshow
            isnewmessagescroll = this@toRealmModel.isnewmessagescroll
            isonlyselectedthem = this@toRealmModel.isonlyselectedthem
            shownotification = this@toRealmModel.shownotification
            messagetraslateshow = this@toRealmModel.messagetraslateshow
            messagetraslationanimationshow = this@toRealmModel.messagetraslationanimationshow
            isgroupmessage = this@toRealmModel.isgroupmessage
            isblocknumber = this@toRealmModel.isblocknumber
            isexpandmessageview = this@toRealmModel.isexpandmessageview
            isScheduled = this@toRealmModel.is_scheduled
            isMessagefound = this@toRealmModel.isMessagefound
            isPrivateChat = this@toRealmModel.isPrivateChat
            isarchived = this@toRealmModel.isarchived
            ispinned = this@toRealmModel.ispinned
            isnewmessage = this@toRealmModel.isnewmessage
            isnumaric = this@toRealmModel.isnumaric
            read = this@toRealmModel.read
            usesCustomTitle = this@toRealmModel.usesCustomTitle
            messageId = this@toRealmModel.messageId
            threadId = this@toRealmModel.threadId
            time = this@toRealmModel.time
            pinneddate = this@toRealmModel.pinneddate
            type = this@toRealmModel.type
            newMessageCount = this@toRealmModel.newMessageCount
            themenumber = this@toRealmModel.themenumber
            messagewithattachment = this@toRealmModel.messagewithattachment?.toRealmModel()
        }
    }

    private fun buildAttachmentPrimaryKey(attachment: Attachment): String {
        val roomIdPart = attachment.id?.toString() ?: "no-room-id"
        return listOf(
            roomIdPart,
            attachment.messageId.toString(),
            attachment.uriString,
            attachment.filename
        ).joinToString("|")
    }

    private fun Cursor.getColumnIndexOrNull(columnName: String): Int? {
        val index = getColumnIndex(columnName)
        return if (index >= 0) index else null
    }

    private fun Cursor.getStringOrDefault(columnName: String, defaultValue: String = ""): String {
        val index = getColumnIndexOrNull(columnName) ?: return defaultValue
        if (isNull(index)) return defaultValue
        return getString(index) ?: defaultValue
    }

    private fun Cursor.getNullableString(columnName: String): String? {
        val index = getColumnIndexOrNull(columnName) ?: return null
        return if (isNull(index)) null else getString(index)
    }

    private fun Cursor.getIntOrDefault(columnName: String, defaultValue: Int = 0): Int {
        val index = getColumnIndexOrNull(columnName) ?: return defaultValue
        if (isNull(index)) return defaultValue
        return runCatching { getInt(index) }.getOrElse {
            getString(index)?.toIntOrNull() ?: defaultValue
        }
    }

    private fun Cursor.getNullableInt(columnName: String): Int? {
        val index = getColumnIndexOrNull(columnName) ?: return null
        if (isNull(index)) return null
        return runCatching { getInt(index) }.getOrElse {
            getString(index)?.toIntOrNull()
        }
    }

    private fun Cursor.getLongOrDefault(columnName: String, defaultValue: Long = 0L): Long {
        val index = getColumnIndexOrNull(columnName) ?: return defaultValue
        if (isNull(index)) return defaultValue
        return runCatching { getLong(index) }.getOrElse {
            getString(index)?.toLongOrNull() ?: defaultValue
        }
    }

    private fun Cursor.getNullableLong(columnName: String): Long? {
        val index = getColumnIndexOrNull(columnName) ?: return null
        if (isNull(index)) return null
        return runCatching { getLong(index) }.getOrElse {
            getString(index)?.toLongOrNull()
        }
    }

    private fun Cursor.getBooleanOrDefault(
        columnName: String,
        defaultValue: Boolean = false
    ): Boolean {
        val index = getColumnIndexOrNull(columnName) ?: return defaultValue
        if (isNull(index)) return defaultValue
        return runCatching { getInt(index) != 0 }.getOrElse {
            when (getString(index)?.lowercase()) {
                "1", "true" -> true
                "0", "false" -> false
                else -> defaultValue
            }
        }
    }

    private fun Cursor.getNullableBoolean(columnName: String): Boolean? {
        val index = getColumnIndexOrNull(columnName) ?: return null
        if (isNull(index)) return null
        return runCatching { getInt(index) != 0 }.getOrElse {
            when (getString(index)?.lowercase()) {
                "1", "true" -> true
                "0", "false" -> false
                else -> null
            }
        }
    }

    companion object {
        private const val TAG = "RoomToRealmMigrator"
        private const val PREFS_NAME = "room_to_realm_migration"
        private const val KEY_MIGRATION_DONE = "migration_done"
        private const val KEY_MIGRATION_RUNNING = "migration_running"
        private const val KEY_LAST_ERROR = "last_error"
        private const val KEY_LAST_SUMMARY = "last_summary"
        private const val CHAT_BATCH_SIZE = 500
        private const val DEFAULT_BATCH_SIZE = 300
    }
}
