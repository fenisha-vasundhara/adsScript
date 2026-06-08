package com.messenger.phone.number.text.sms.service.apps.realmplan

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.modelclass.Foldermodel
import com.messenger.phone.number.text.sms.service.apps.modelClass.Category
import com.messenger.phone.number.text.sms.service.apps.modelClass.Contact
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversationbin
import com.messenger.phone.number.text.sms.service.apps.modelClass.Recentsearch
import com.messenger.phone.number.text.sms.service.apps.modelClass.Remindermodel
import com.messenger.phone.number.text.sms.service.apps.modelClass.StarNumber
import io.github.xilinjia.krdb.Realm
import io.github.xilinjia.krdb.RealmConfiguration
import io.github.xilinjia.krdb.ext.query
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for RealmMessagingRepo.
 *
 * Each test runs on a fresh in-memory Realm so tests are fully isolated.
 * Uses runBlocking to drive suspend functions — acceptable in instrumented tests
 * because they run on a real thread (not a test coroutine dispatcher).
 *
 * Run via: ./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=...RealmMessagingRepoTest
 */
@RunWith(AndroidJUnit4::class)
class RealmMessagingRepoTest {

    private lateinit var realm: Realm
    private lateinit var repo: RealmMessagingRepo

    @Before
    fun setUp() {
        val config = RealmConfiguration.Builder(
            schema = setOf(
                ConversationRealm::class,
                ConversationBinRealm::class,
                MessageAttachmentRealm::class,
                AttachmentRealm::class,
                CategoryRealm::class,
                CategoryNumberRealm::class,
                ContactRealm::class,
                RecentSearchRealm::class,
                ReminderRealm::class,
                StarNumberRealm::class,
                FolderRealm::class,
                PhotoRealm::class,
            )
        ).inMemory().name("test-${System.nanoTime()}").build()
        realm = Realm.open(config)
        repo = RealmMessagingRepo(realm)
    }

    @After
    fun tearDown() {
        realm.close()
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun conv(
        id: Int = 1,
        threadId: Long = 100L,
        messageId: Long = 200L,
        snippet: String = "Hello",
        phoneNumber: String = "+15550001234",
        title: String = "Test Contact",
    ) = Conversation(
        id = id,
        date = "1700000000000",
        read = true,
        title = title,
        photoUri = null,
        phoneNumber = phoneNumber,
        snippet = snippet,
        time = 1700000000000L,
        type = 1,
        isnumaric = true,
        messageStatus = "delivered",
        messageId = messageId,
        threadId = threadId,
    )

    private fun bin(
        id: Int = 1,
        threadId: Long = 400L,
        messageId: Long = 300L,
        phoneNumber: String = "+1999",
        snippet: String = "Deleted",
        title: String = "Gone",
    ) = Conversationbin(
        id = id,
        phoneNumber = phoneNumber,
        snippet = snippet,
        title = title,
        messageId = messageId,
        threadId = threadId,
    )

    // ── Conversation: insert / upsert ─────────────────────────────────────────

    @Test
    fun insertmessage_storesConversation() = runBlocking {
        repo.insertmessage(conv(id = 1, messageId = 20L))
        assertTrue(repo.isMessageExitsRepo(20L))
    }

    @Test
    fun insertmessage_upsert_doesNotDuplicate() = runBlocking {
        repo.insertmessage(conv(id = 5, messageId = 50L, snippet = "First"))
        repo.insertmessage(conv(id = 5, messageId = 50L, snippet = "Second"))
        val all = repo.getallconversationOnlyListrepo()
        assertEquals(1, all.size)
        assertEquals("Second", all.first().snippet)
    }

    @Test
    fun insertOrUpdateList_storesAllRows() = runBlocking {
        repo.insertOrUpdateList(listOf(
            conv(id = 1, messageId = 1L, threadId = 1L),
            conv(id = 2, messageId = 2L, threadId = 2L),
            conv(id = 3, messageId = 3L, threadId = 3L),
        ))
        assertEquals(3, repo.getallconversationOnlyListrepo().size)
    }

    @Test
    fun insertOrUpdateList_emptyList_doesNotCrash() = runBlocking {
        repo.insertOrUpdateList(emptyList())
        assertTrue(repo.isEmpty())
    }

    // ── Conversation: isEmpty / isMessageExits ────────────────────────────────

    @Test
    fun isEmpty_trueOnFreshRealm() = runBlocking {
        assertTrue(repo.isEmpty())
    }

    @Test
    fun isEmpty_falseAfterInsert() = runBlocking {
        repo.insertmessage(conv())
        assertFalse(repo.isEmpty())
    }

    @Test
    fun isMessageExitsRepo_falseWhenNotPresent() {
        assertFalse(repo.isMessageExitsRepo(9999L))
    }

    @Test
    fun isMessageExitsRepo_trueAfterInsert() = runBlocking {
        repo.insertmessage(conv(messageId = 42L))
        assertTrue(repo.isMessageExitsRepo(42L))
    }

    // ── Conversation: delete ──────────────────────────────────────────────────

    @Test
    fun deleteConversationRepo_removesRow() = runBlocking {
        repo.insertmessage(conv(id = 7, threadId = 77L, messageId = 777L))
        assertTrue(repo.isMessageExitsRepo(777L))
        repo.deleteConversationRepo(listOf("77"))
        assertFalse(repo.isMessageExitsRepo(777L))
    }

    @Test
    fun deleteConversationRepo_emptyList_doesNotCrash() = runBlocking {
        repo.insertmessage(conv())
        repo.deleteConversationRepo(emptyList())
        assertFalse(repo.isEmpty())
    }

    // ── Conversation: field updates ───────────────────────────────────────────

    @Test
    fun setisoldmessageRepo_updatesNewMessageFlag() = runBlocking {
        repo.insertmessage(conv(id = 1, threadId = 10L).apply { isnewmessage = true })
        repo.setisoldmessageRepo(false, 10L)
        val row = realm.query<ConversationRealm>("threadId == $0", 10L).first().find()
        assertNotNull(row)
        assertEquals(false, row!!.isnewmessage)
    }

    @Test
    fun setisoldmessageCountRepo_updatesCount() = runBlocking {
        repo.insertmessage(conv(id = 1, threadId = 10L).apply { newMessageCount = 5 })
        repo.setisoldmessageCountRepo(0, 10L)
        val row = realm.query<ConversationRealm>("threadId == $0", 10L).first().find()
        assertEquals(0, row!!.newMessageCount)
    }

    @Test
    fun updatemessagestatusRepo_updatesStatus() = runBlocking {
        repo.insertmessage(conv(id = 1, messageId = 99L).apply { messageStatus = "pending" })
        repo.updatemessagestatusRepo("delivered", 99L)
        val row = realm.query<ConversationRealm>("messageId == $0", 99L).first().find()
        assertEquals("delivered", row!!.messageStatus)
    }

    @Test
    fun UpdateMessageTitleRepo_updatesTitle() = runBlocking {
        repo.insertmessage(conv(id = 1, threadId = 55L, title = "Old"))
        repo.UpdateMessageTitleRepo("New", 55L)
        val row = realm.query<ConversationRealm>("threadId == $0", 55L).first().find()
        assertEquals("New", row!!.title)
    }

    // ── Conversation: archive / privacy / block / pin ─────────────────────────

    @Test
    fun archivConversationRepo_setsArchived() = runBlocking {
        repo.insertmessage(conv(id = 1, threadId = 11L))
        repo.archivConversationRepo(listOf("11"))
        val row = realm.query<ConversationRealm>("threadId == $0", 11L).first().find()
        assertTrue(row!!.isarchived)
    }

    @Test
    fun removearchivConversationRepo_clearsArchived() = runBlocking {
        repo.insertmessage(conv(id = 1, threadId = 12L).apply { isarchived = true })
        repo.removearchivConversationRepo(listOf("12"))
        val row = realm.query<ConversationRealm>("threadId == $0", 12L).first().find()
        assertFalse(row!!.isarchived)
    }

    @Test
    fun PrivacyConversationRepo_setsPrivateChat() = runBlocking {
        repo.insertmessage(conv(id = 1, threadId = 13L))
        repo.PrivacyConversationRepo(listOf("13"))
        val row = realm.query<ConversationRealm>("threadId == $0", 13L).first().find()
        assertTrue(row!!.isPrivateChat)
    }

    @Test
    fun removePrivacyConversationRepo_clearsPrivateChat() = runBlocking {
        repo.insertmessage(conv(id = 1, threadId = 13L).apply { isPrivateChat = true })
        repo.removePrivacyConversationRepo(listOf("13"))
        val row = realm.query<ConversationRealm>("threadId == $0", 13L).first().find()
        assertFalse(row!!.isPrivateChat)
    }

    @Test
    fun addnumbertoblockRepo_setsBlocked() = runBlocking {
        repo.insertmessage(conv(id = 1, threadId = 14L))
        repo.addnumbertoblockRepo(14L)
        val row = realm.query<ConversationRealm>("threadId == $0", 14L).first().find()
        assertTrue(row!!.isblocknumber)
    }

    @Test
    fun removenumbertoblockRepo_clearsBlocked() = runBlocking {
        repo.insertmessage(conv(id = 1, threadId = 15L).apply { isblocknumber = true })
        repo.removenumbertoblockRepo(15L)
        val row = realm.query<ConversationRealm>("threadId == $0", 15L).first().find()
        assertFalse(row!!.isblocknumber)
    }

    @Test
    fun addpinConversationRepo_setsPinnedAndDate() = runBlocking {
        repo.insertmessage(conv(id = 1, threadId = 16L))
        repo.addpinConversationRepo(16L)
        val row = realm.query<ConversationRealm>("threadId == $0", 16L).first().find()
        assertTrue(row!!.ispinned)
        assertNotNull(row.pinneddate)
        assertTrue((row.pinneddate ?: 0L) > 0L)
    }

    @Test
    fun removepinConversationRepo_clearsPinned() = runBlocking {
        repo.insertmessage(conv(id = 1, threadId = 17L).apply { ispinned = true; pinneddate = 999L })
        repo.removepinConversationRepo(17L)
        val row = realm.query<ConversationRealm>("threadId == $0", 17L).first().find()
        assertFalse(row!!.ispinned)
    }

    // ── Conversation: scheduled ───────────────────────────────────────────────

    @Test
    fun scheduleconversationexitsrepo_trueForScheduled() = runBlocking {
        // is_scheduled (Room) → isScheduled (Realm) via mapper
        repo.insertmessage(conv(id = 1, messageId = 42L).apply { is_scheduled = true })
        assertTrue(repo.scheduleconversationexitsrepo(42L))
    }

    @Test
    fun scheduleconversationexitsrepo_falseForNonScheduled() = runBlocking {
        repo.insertmessage(conv(id = 1, messageId = 43L))
        assertFalse(repo.scheduleconversationexitsrepo(43L))
    }

    // ── Conversation: getThreadInfoRepo ───────────────────────────────────────

    @Test
    fun getThreadInfoRepo_returnsCorrectFields() = runBlocking {
        repo.insertmessage(conv(
            id = 1,
            threadId = 999L,
            phoneNumber = "+1999",
            title = "ThreadTest",
        ).apply { isgroupmessage = false })
        val infos = repo.getThreadInfoRepo()
        assertTrue(infos.isNotEmpty())
        val info = infos.first { it.threadId == 999L }
        assertEquals(999L, info.threadId)
        assertEquals("ThreadTest", info.title)
        assertEquals("+1999", info.phoneNumber)
        assertFalse(info.isgroupmessage)
    }

    @Test
    fun getThreadInfoRepo_emptyWhenNoConversations() = runBlocking {
        assertEquals(0, repo.getThreadInfoRepo().size)
    }

    // ── Recycle bin ───────────────────────────────────────────────────────────

    @Test
    fun insertOrUpdateRecycleBinRepo_storesBinEntry() = runBlocking {
        repo.insertOrUpdateRecycleBinRepo(bin(id = 1, messageId = 300L, threadId = 400L))
        val rows = realm.query<ConversationBinRealm>().find()
        assertEquals(1, rows.size)
        assertEquals(300L, rows.first().messageId)
    }

    @Test
    fun insertOrUpdateRecycleBinRepo_upsert_doesNotDuplicate() = runBlocking {
        repo.insertOrUpdateRecycleBinRepo(bin(id = 2, messageId = 301L, snippet = "v1"))
        repo.insertOrUpdateRecycleBinRepo(bin(id = 2, messageId = 301L, snippet = "v2"))
        val rows = realm.query<ConversationBinRealm>().find()
        assertEquals(1, rows.size)
        assertEquals("v2", rows.first().snippet)
    }

    @Test
    fun moveConversationsToRecycleBin_atomicallyMovesRow() = runBlocking {
        repo.insertmessage(conv(id = 1, threadId = 20L, messageId = 201L))
        repo.moveConversationsToRecycleBin(listOf(20L))
        // Gone from main
        assertFalse(repo.isMessageExitsRepo(201L))
        // Present in bin
        val binRows = realm.query<ConversationBinRealm>("threadId == $0", 20L).find()
        assertEquals(1, binRows.size)
    }

    @Test
    fun moveConversationsToRecycleBin_emptyList_doesNotCrash() = runBlocking {
        repo.moveConversationsToRecycleBin(emptyList())
    }

    @Test
    fun removedeletemessageRepo_restoresFromBinToMain() = runBlocking {
        repo.insertOrUpdateRecycleBinRepo(bin(id = 5, messageId = 500L, threadId = 50L))
        repo.removedeletemessageRepo(500L)
        val inBin = realm.query<ConversationBinRealm>("messageId == $0", 500L).find()
        assertEquals(0, inBin.size)
        val inMain = realm.query<ConversationRealm>("messageId == $0", 500L).find()
        assertEquals(1, inMain.size)
    }

    @Test
    fun deleteConversationRecyclerbinRepo_removesEntry() = runBlocking {
        repo.insertOrUpdateRecycleBinRepo(bin(id = 6, threadId = 60L, messageId = 600L))
        repo.deleteConversationRecyclerbinRepo(listOf("60"))
        val rows = realm.query<ConversationBinRealm>("threadId == $0", 60L).find()
        assertEquals(0, rows.size)
    }

    // ── Reminder ──────────────────────────────────────────────────────────────

    @Test
    fun insertOrUpdateRemainderRepo_storesReminder() = runBlocking {
        repo.insertOrUpdateRemainderRepo(Remindermodel(id = 10, remindertitle = "Wake up", selected = true))
        val row = realm.query<ReminderRealm>("id == $0", 10).first().find()
        assertNotNull(row)
        assertEquals("Wake up", row!!.remindertitle)
        assertTrue(row.selected)
    }

    @Test
    fun getRemainderMessageRepo_returnsMatchById() = runBlocking {
        repo.insertOrUpdateRemainderRepo(Remindermodel(id = 11, remindertitle = "Doctor"))
        val result = repo.getRemainderMessageRepo(11L)
        assertNotNull(result)
        assertEquals("Doctor", result!!.remindertitle)
    }

    @Test
    fun getRemainderMessageRepo_returnsNullWhenMissing() = runBlocking {
        val result = repo.getRemainderMessageRepo(9999L)
        assertNull(result)
    }

    @Test
    fun deleteRemainderRepo_removesEntry() = runBlocking {
        repo.insertOrUpdateRemainderRepo(Remindermodel(id = 12, remindertitle = "Delete me"))
        repo.deleteRemainderRepo(12)
        assertNull(repo.getRemainderMessageRepo(12L))
    }

    @Test
    fun updateremaindertitleRepo_updatesTitle() = runBlocking {
        repo.insertOrUpdateRemainderRepo(Remindermodel(id = 13, remindertitle = "Old"))
        repo.updateremaindertitleRepo(13L, "New")
        val row = realm.query<ReminderRealm>("id == $0", 13).first().find()
        assertEquals("New", row!!.remindertitle)
    }

    @Test
    fun insertOrUpdateRemainderRepo_upsert_updatesFields() = runBlocking {
        repo.insertOrUpdateRemainderRepo(Remindermodel(id = 14, remindertitle = "v1"))
        repo.insertOrUpdateRemainderRepo(Remindermodel(id = 14, remindertitle = "v2"))
        val rows = realm.query<ReminderRealm>().find()
        assertEquals(1, rows.size)
        assertEquals("v2", rows.first().remindertitle)
    }

    // ── Category ──────────────────────────────────────────────────────────────

    @Test
    fun insertorupdatecatgoryrep_storesCategory() = runBlocking {
        repo.insertorupdatecatgoryrep(Category(id = 1, catName = "Work"))
        assertTrue(repo.isCatExitsRepo("Work"))
    }

    @Test
    fun isCatExitsRepo_falseWhenMissing() {
        assertFalse(repo.isCatExitsRepo("Nonexistent"))
    }

    @Test
    fun insertOrUpdateCategoryUsingListrepo_storesAll() = runBlocking {
        repo.insertOrUpdateCategoryUsingListrepo(listOf(
            Category(id = 1, catName = "A"),
            Category(id = 2, catName = "B"),
        ))
        assertTrue(repo.isCatExitsRepo("A"))
        assertTrue(repo.isCatExitsRepo("B"))
    }

    @Test
    fun deleteAllCatRepo_removesAll() = runBlocking {
        val cats = listOf(Category(id = 1, catName = "A"), Category(id = 2, catName = "B"))
        repo.insertOrUpdateCategoryUsingListrepo(cats)
        repo.deleteAllCatRepo(cats)
        assertFalse(repo.isCatExitsRepo("A"))
        assertFalse(repo.isCatExitsRepo("B"))
    }

    @Test
    fun deleteDataByCatNameRepo_removesNamed() = runBlocking {
        repo.insertorupdatecatgoryrep(Category(id = 1, catName = "ToDelete"))
        repo.insertorupdatecatgoryrep(Category(id = 2, catName = "Keep"))
        repo.deleteDataByCatNameRepo(listOf("ToDelete"))
        assertFalse(repo.isCatExitsRepo("ToDelete"))
        assertTrue(repo.isCatExitsRepo("Keep"))
    }

    // ── Contact ───────────────────────────────────────────────────────────────

    @Test
    fun AddCatContactAppRepo_storesContacts() = runBlocking {
        repo.AddCatContactAppRepo(listOf(
            Contact(name = "Alice", number = "+1111", contactId = 1, onlynumber = "1111", CatName = "Work", id = 1),
        ))
        assertTrue(repo.isCatNumberSelectedRepo("+1111"))
    }

    @Test
    fun isCatNumberSelectedRepo_falseWhenMissing() {
        assertFalse(repo.isCatNumberSelectedRepo("+0000"))
    }

    @Test
    fun deleteDataByCatNumberRepo_removesContact() = runBlocking {
        repo.AddCatContactAppRepo(listOf(
            Contact(name = "Bob", number = "+2222", contactId = 2, onlynumber = "2222", CatName = "Friends", id = 2),
        ))
        repo.deleteDataByCatNumberRepo(listOf("+2222"))
        assertFalse(repo.isCatNumberSelectedRepo("+2222"))
    }

    // ── Star number ───────────────────────────────────────────────────────────

    @Test
    fun addNumberToStarRepo_storesStar() = runBlocking {
        repo.addNumberToStarRepo(star(phoneNumber = "+5555"))
        assertTrue(repo.isStarNumberSelectedRepo("+5555"))
    }

    @Test
    fun isStarNumberSelectedRepo_falseWhenMissing() {
        assertFalse(repo.isStarNumberSelectedRepo("+0000"))
    }

    @Test
    fun removeNumberToStarRepo_removesStar() = runBlocking {
        repo.addNumberToStarRepo(star(phoneNumber = "+6666"))
        repo.removeNumberToStarRepo("+6666")
        assertFalse(repo.isStarNumberSelectedRepo("+6666"))
    }

    private fun star(phoneNumber: String, id: Int = 1) = StarNumber(
        id = id,
        date = "2024-01-01",
        read = true,
        title = "VIP",
        photoUri = null,
        usesCustomTitle = false,
        phoneNumber = phoneNumber,
        snippet = "",
        time = 0L,
        type = null,
        isnumaric = true,
    )

    // ── Recent search ─────────────────────────────────────────────────────────

    @Test
    fun addrecentsearchRepo_storesSearch() = runBlocking {
        repo.addrecentsearchRepo(Recentsearch(id = 1, recentsearch = "hello"))
        assertTrue(repo.isrecentsearchExitsRepo("hello"))
    }

    @Test
    fun isrecentsearchExitsRepo_falseWhenMissing() = runBlocking {
        assertFalse(repo.isrecentsearchExitsRepo("missing"))
    }

    @Test
    fun deleterecentsearchRepo_removesEntry() = runBlocking {
        repo.addrecentsearchRepo(Recentsearch(id = 2, recentsearch = "bye"))
        repo.deleterecentsearchRepo("bye")
        assertFalse(repo.isrecentsearchExitsRepo("bye"))
    }

    @Test
    fun deleteAllrecentsearchRepo_removesAll() = runBlocking {
        repo.addrecentsearchRepo(Recentsearch(id = 1, recentsearch = "a"))
        repo.addrecentsearchRepo(Recentsearch(id = 2, recentsearch = "b"))
        repo.deleteAllrecentsearchRepo()
        assertFalse(repo.isrecentsearchExitsRepo("a"))
        assertFalse(repo.isrecentsearchExitsRepo("b"))
    }

    // ── Gallery ───────────────────────────────────────────────────────────────

    @Test
    fun insertOrUpdateGallerydataFolderRepo_storesFolder() = runBlocking {
        repo.insertOrUpdateGallerydataFolderRepo(
            Foldermodel(id = 1, foldername = "Camera", folderimgcount = "10",
                folderthumimage = "/img.jpg", folderpath = "/sdcard/DCIM")
        )
        val list = repo.getallTFolderListRepo()
        assertEquals(1, list.size)
        assertEquals("Camera", list.first().foldername)
    }

    @Test
    fun deleteAllFolderRepo_removesAll() = runBlocking {
        repo.insertOrUpdateGallerydataFolderRepo(
            Foldermodel(id = 1, foldername = "A", folderimgcount = "1",
                folderthumimage = "", folderpath = "/a")
        )
        repo.deleteAllFolderRepo()
        assertEquals(0, repo.getallTFolderListRepo().size)
    }

    @Test
    fun insertOrUpdateGallerydataFolderRepo_upsert_updatesFields() = runBlocking {
        repo.insertOrUpdateGallerydataFolderRepo(
            Foldermodel(id = 1, foldername = "Old", folderimgcount = "1", folderthumimage = "", folderpath = "/x")
        )
        repo.insertOrUpdateGallerydataFolderRepo(
            Foldermodel(id = 1, foldername = "New", folderimgcount = "99", folderthumimage = "", folderpath = "/x")
        )
        val list = repo.getallTFolderListRepo()
        assertEquals(1, list.size)
        assertEquals("New", list.first().foldername)
    }
}
