package com.messenger.phone.number.text.sms.service.apps.DAOClass

import android.provider.Telephony.Sms.Conversations
import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomWarnings
import com.messenger.phone.number.text.sms.service.apps.modelClass.Category
import com.messenger.phone.number.text.sms.service.apps.modelClass.CategoryNumber
import com.messenger.phone.number.text.sms.service.apps.modelClass.Contact
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversationbin
import com.messenger.phone.number.text.sms.service.apps.modelClass.Recentsearch
import com.messenger.phone.number.text.sms.service.apps.modelClass.Remindermodel
import com.messenger.phone.number.text.sms.service.apps.modelClass.StarNumber

data class ThreadInfo(
    val threadId: Long,
    val title: String?,
    val isgroupmessage: Boolean,
    val groupName: String?,
    val phoneNumber: String?
)




@Dao
@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
interface MessageDAO {

    companion object {
        private const val SAFE_ID_COLUMN = "COALESCE(CAST(id AS INTEGER), 0) AS id"
        private const val CONVERSATION_COLUMNS =
            "$SAFE_ID_COLUMN, date, read, title, photoUri, usesCustomTitle, phoneNumber, snippet, time, type, isnumaric, messageStatus, isnewmessage, newMessageCount, messageId, threadId, isarchived, ispinned, pinneddate, isblocknumber, isexpandmessageview, is_scheduled, isMessagefound, isPrivateChat, draftmessage, messagetype, messageotp, shownotification, messagetraslateshow, messagetraslationanimationshow, isgroupmessage, groupName, CategoryName, isnewmessagescroll, isonlyselectedthem, themenumber, customtimeuri, isbanneradshow, messagewithattachment"

        private const val CONVERSATION_BIN_COLUMNS =
            "$SAFE_ID_COLUMN, date, draftmessage, messagetype, messageotp, groupName, CategoryName, customtimeuri, phoneNumber, snippet, title, photoUri, messageStatus, isbanneradshow, isnewmessagescroll, isonlyselectedthem, shownotification, messagetraslateshow, messagetraslationanimationshow, isgroupmessage, isblocknumber, isexpandmessageview, is_scheduled, isMessagefound, isPrivateChat, isarchived, ispinned, isnewmessage, isnumaric, read, usesCustomTitle, messageId, threadId, time, pinneddate, type, newMessageCount, themenumber, messagewithattachment"
    }

    @Insert()
    suspend fun insertOrUpdate(message: Conversation): Long

    @Insert()
    suspend fun insertOrUpdateRemainder(message: Remindermodel): Long

    @Insert()
    suspend fun insertOrUpdateRecycleBin(message: Conversationbin): Long

    @Insert()
    suspend fun insertOrUpdateList(message: List<Conversation>)

    @Query("UPDATE reminder SET remindertitle =:titlere WHERE id =:messageId")
    suspend fun updateremaindertitle(messageId: Long,titlere:String)

    @Query("SELECT $CONVERSATION_COLUMNS FROM Conversation WHERE messageId= :messageId")
    suspend fun gettredidfromemessageid(messageId: Long) : Conversation?

    @Query("DELETE FROM conversation")
    suspend fun deleteAll()


    @Query("DELETE FROM conversation WHERE messageId= :messageId")
    suspend fun deletemessage(messageId: Long)

    @Query("UPDATE Conversation SET messagetraslateshow = 0 WHERE messageId= :messageId")
    suspend fun removedeletemessage(messageId: Long)

    @Query("UPDATE Conversation SET messagetraslateshow = 0 WHERE threadId= :threadId1")
    suspend fun removeTraslatedMessagemessage(threadId1: Long)

    @Query("SELECT $CONVERSATION_COLUMNS FROM Conversation ORDER BY time DESC")
    fun getallconversation(): LiveData<List<Conversation>>

    @Query("SELECT * FROM reminder ORDER BY id DESC")
    fun getallRemainder(): LiveData<List<Remindermodel>>

    @Query("SELECT $CONVERSATION_COLUMNS FROM Conversation ORDER BY time DESC")
    fun getallconversationOnlyList(): List<Conversation>

    @Query("SELECT $CONVERSATION_COLUMNS FROM Conversation WHERE isPrivateChat=1  AND isblocknumber = 0 AND isarchived=0 ORDER BY time DESC")
    fun getallconversationunPrivacyOrnotList(): List<Conversation>

    @Query("SELECT * FROM CATEGORY")
    suspend fun getallTabOnlyList(): List<Category>

    @Query("SELECT EXISTS (SELECT 1 FROM conversation WHERE messageId = :messageId AND is_scheduled = 1)")
    fun scheduleconversationexits(messageId: Long): Boolean

    //mainscreen <-------->

    @Query("SELECT $CONVERSATION_COLUMNS FROM Conversation WHERE isarchived=0 AND isblocknumber = 0 AND isPrivateChat = 0 AND threadId NOT IN (SELECT threadId FROM conversationbin) ORDER BY time DESC")
    fun getallconversationunarchiv(): LiveData<List<Conversation>>

//    @Query("SELECT c.*\n" +
//            "FROM Conversation c\n" +
//            "JOIN (\n" +
//            "    SELECT threadId, MAX(id) AS max_id\n" +
//            "    FROM Conversation\n" +
//            "    WHERE isarchived = 0 AND isblocknumber = 0 AND isPrivateChat = 0\n" +
//            "    GROUP BY threadId\n" +
//            ") max_ids ON c.threadId = max_ids.threadId AND c.id = max_ids.max_id\n" +
//            "ORDER BY c.time DESC")
//    fun getallconversationunarchiv(): LiveData<List<Conversation>>


    @Query("SELECT $CONVERSATION_COLUMNS FROM Conversation WHERE isarchived=0 AND isblocknumber = 0 AND isPrivateChat = 0 AND is_scheduled = 0 AND threadId NOT IN (SELECT threadId FROM conversationbin) ORDER BY time DESC")
    fun getPagedConversations(): PagingSource<Int, Conversation>

    @Query("SELECT $CONVERSATION_COLUMNS FROM Conversation WHERE isarchived=0 AND isblocknumber = 0 AND isPrivateChat = 0 AND is_scheduled = 0 AND threadId NOT IN (SELECT threadId FROM conversationbin) ORDER BY time DESC")
    fun getallconversationunarchivforcontact(): List<Conversation>

    @Query("SELECT $CONVERSATION_COLUMNS FROM Conversation WHERE isblocknumber = 0 AND is_scheduled = 1 ORDER BY id DESC")
    fun getallScheduleconversationu(): LiveData<List<Conversation>>

    @Query("SELECT $CONVERSATION_COLUMNS FROM Conversation WHERE isarchived=1  AND isblocknumber = 0 ORDER BY time DESC")
    fun getallconversationunarchivOrnot(): LiveData<List<Conversation>>

    @Query("SELECT $CONVERSATION_COLUMNS FROM Conversation WHERE isPrivateChat=1  AND isblocknumber = 0 AND isarchived=0 ORDER BY time DESC")
    fun getallconversationunPrivacyOrnot(): LiveData<List<Conversation>>

    @Query("SELECT $CONVERSATION_BIN_COLUMNS FROM Conversationbin ORDER BY time DESC")
    fun getalldeleteconversationun(): LiveData<List<Conversationbin>>

    @Query("SELECT $CONVERSATION_COLUMNS FROM Conversation WHERE isblocknumber=1 ORDER BY time DESC")
    fun getallconversationunblockOrnot(): LiveData<List<Conversation>>

    @Query("SELECT $CONVERSATION_COLUMNS FROM Conversation WHERE threadId =:tredid ORDER BY time ASC")
    fun getUserMessage(tredid: Long): LiveData<List<Conversation>>

    @Query("SELECT $CONVERSATION_BIN_COLUMNS FROM Conversationbin WHERE threadId =:tredid ORDER BY time ASC")
    fun getUserDeleteMessage(tredid: Long): LiveData<List<Conversationbin>>

//    @Query("SELECT * FROM conversation WHERE threadId =:tredid AND is_scheduled = 0 ORDER BY time ASC")
//    fun getUserMessage(tredid: Long): LiveData<List<Conversation>>

    @Query("SELECT $CONVERSATION_COLUMNS FROM Conversation WHERE threadId =:tredid AND is_scheduled = 0 ORDER BY time ASC")
    fun getUserMessageListChack(tredid: Long): List<Conversation>

    @Query("SELECT $CONVERSATION_BIN_COLUMNS FROM Conversationbin WHERE threadId =:tredid ORDER BY time ASC")
    fun getUserMessageRecyListChack(tredid: Long): List<Conversationbin>

    @Query("SELECT $CONVERSATION_BIN_COLUMNS FROM Conversationbin WHERE  phoneNumber=:phoneNumber ORDER BY time ASC")
    fun getUserMessageRecyListChack(phoneNumber: String): List<Conversationbin>

    @Query("SELECT $CONVERSATION_COLUMNS FROM Conversation WHERE  phoneNumber=:phoneNumber ORDER BY time ASC")
    fun getUserMessageMobileListChack(phoneNumber: String): List<Conversation>

    @Query("SELECT $CONVERSATION_COLUMNS FROM Conversation WHERE threadId =:tredid")
    suspend fun getUserMessageList(tredid: Long): List<Conversation>

    @Query("SELECT $CONVERSATION_COLUMNS FROM Conversation WHERE messagetype =:otp")
    suspend fun getUserOTPMessageList(otp: String): List<Conversation>

    @Query("SELECT messageId FROM Conversation")
    suspend fun getAllMessageIds(): List<Long>

    @Query("SELECT threadId, title, isgroupmessage, groupName, phoneNumber FROM Conversation")
    suspend fun getThreadInfo(): List<ThreadInfo>

    @Query("SELECT $CONVERSATION_COLUMNS FROM Conversation WHERE isnumaric=0 ORDER BY time DESC ")
    fun getuserpersonalMessasge(): LiveData<List<Conversation>>

    @Query("SELECT $CONVERSATION_COLUMNS FROM Conversation WHERE isnumaric=1 ORDER BY time DESC ")
    fun getuserunknownMessasge(): LiveData<List<Conversation>>

    @Insert
    suspend fun insertOrUpdateCategory(catName: Category)

    @Insert
    suspend fun insertOrUpdateCategoryUsingList(catName: List<Category>)

    @Query("SELECT EXISTS (SELECT 1 FROM category WHERE catName = :catName)")
    fun isCatNameSelected(catName: String): Boolean

    @Query("SELECT * FROM category")
    fun getAllCat(): LiveData<List<Category>>

    @Query("SELECT * FROM recentsearch ORDER BY id DESC")
    fun getRecentSearch(): LiveData<List<Recentsearch>>

    @Query("delete from CATEGORY where catName in (:packageList)")
    suspend fun deleteDataByCatName(packageList: List<String>)

    @Delete
    suspend fun deleteAllCat(catList: List<Category>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCatContactApp(selectedapp: List<Contact>)

    @Query("SELECT EXISTS (SELECT 1 FROM contact WHERE onlynumber = :mobilenumber)")
    fun isCatNumberSelected(mobilenumber: String): Boolean

    @Query("SELECT * FROM CONTACT WHERE catName =:catName ")
    fun getCatMobilenumber(catName: String): LiveData<List<Contact>>

    @Query("SELECT isarchived FROM conversation WHERE threadId =:threadId  ")
    suspend fun isarchivornot(threadId: Long): Int

    @Query("SELECT isblocknumber FROM conversation WHERE threadId =:threadId  ")
    suspend fun isnumberblockornot(threadId: Long): Int

    @Query("delete from CONTACT where number in (:packageList)")
    suspend fun deleteDataByCatNumber(packageList: List<String>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNumberToStar(selectedapp: StarNumber)

    @Query("delete from starnumber where phoneNumber =:selectedapp")
    suspend fun removeNumberToStar(selectedapp: String)


    @Query("delete from reminder where id =:idin")
    suspend fun deleteRemainder(idin: Int)


    @Query("UPDATE Conversation SET isblocknumber = 0 WHERE phoneNumber in (:packageList)")
    suspend fun deleteblocknumber(packageList: List<String>)

    @Query("UPDATE Conversation SET isarchived = 1 WHERE threadId in (:packageList)")
    suspend fun archivConversation(packageList: List<String>)

    @Query("UPDATE Conversation SET isPrivateChat = 1 WHERE threadId in (:packageList)")
    suspend fun PrivacyConversation(packageList: List<String>)


    @Query("UPDATE Conversation SET title =:titlenew WHERE threadId =:threadId")
    suspend fun UpdateMessageTitle(titlenew: String, threadId: Long)

    @Query("UPDATE Conversation SET isblocknumber = 1 WHERE threadId =:threadId")
    suspend fun addnumbertoblock(threadId: Long)


    @Query("UPDATE Conversation SET isMessagefound = 1 WHERE messageId =:messageId")
    suspend fun addmessagefound(messageId: Long)

    @Query("UPDATE Conversation SET isMessagefound = 0 WHERE messageId =:messageId")
    suspend fun removemessagefound(messageId: Long)

    @Query("SELECT isMessagefound FROM conversation WHERE messageId=:messageId")
    fun isSelectedMessage(messageId: Long): Boolean

    @Query("UPDATE Conversation SET isblocknumber = 0 WHERE threadId =:threadId")
    suspend fun removenumbertoblock(threadId: Long)

    @Query("UPDATE Conversation SET isarchived = 0 WHERE threadId in (:packageList)")
    suspend fun removearchivConversation(packageList: List<String>)

    @Query("UPDATE Conversation SET shownotification = 0 WHERE threadId in (:packageList)")
    suspend fun addnotshownotification(packageList: List<String>)

    @Query("UPDATE Conversation SET shownotification = 1 WHERE threadId in (:packageList)")
    suspend fun removenotshownotification(packageList: List<String>)

    @Query("UPDATE Conversation SET isPrivateChat = 0 WHERE threadId in (:packageList)")
    suspend fun removePrivacyConversation(packageList: List<String>)

    @Query("UPDATE Conversation SET snippet = :snippetmess WHERE messageId = :messageId")
    suspend fun updateMessage(messageId: Long, snippetmess: String)

    @Query("SELECT EXISTS (SELECT 1 FROM starnumber WHERE phoneNumber = :mobilenumber)")
    fun isStarNumberSelected(mobilenumber: String): Boolean

    @Query("SELECT EXISTS (SELECT 1 FROM conversation WHERE messageId = :messageId)")
    fun isMessageExits(messageId: Long): Boolean

    @Query("SELECT EXISTS (SELECT 1 FROM conversation WHERE threadId = :messageId)")
    fun isNewUserMessageExits(messageId: Long): Boolean

    @Query("SELECT EXISTS (SELECT 1 FROM CATEGORY WHERE catName = :catename)")
    fun isCatExits(catename: String): Boolean

    @Query("SELECT EXISTS (SELECT 1 FROM Conversation WHERE groupName = :catename)")
    fun isBrodcastExits(catename: String): Boolean

    @Query("SELECT EXISTS (SELECT 1 FROM conversation WHERE threadId = :messageId AND isPrivateChat  = 1)")
    fun isPrivacyChatExits(messageId: Long): Boolean


    @Query("SELECT EXISTS (SELECT 1 FROM conversation WHERE threadId = :messageId AND shownotification  = 1)")
    fun isUserNotificationshow(messageId: Long): Boolean

    @Query("SELECT EXISTS (SELECT 1 FROM conversation WHERE threadId = :messageId AND isnewmessage  = 1)")
    fun isUserNewMessage(messageId: Long): Boolean

    @Query("SELECT * FROM starnumber")
    fun getAllStarContact(): LiveData<List<StarNumber>>

    @Query("delete from starnumber where phoneNumber in (:packageList)")
    suspend fun deleteDataByStarNumber(packageList: List<String>)

    @Query("UPDATE Conversation SET messageStatus = :messageStatus WHERE id =:id")
    suspend fun updatemessagestatus(messageStatus: String, id: Long)

    @Query("UPDATE Conversation SET messageStatus = :messageStatus WHERE messageId =:id")
    suspend fun updatemessagestatusfrommessage(messageStatus: String, id: Long)


    @Query("UPDATE Conversation SET CategoryName = :categoryName WHERE threadId in (:packageList)")
    suspend fun updatemessageCat(categoryName: String, packageList: List<String>)

    @Query("UPDATE Conversation SET CategoryName = :categoryName WHERE CategoryName in (:packageList)")
    suspend fun updatemessageCatName(categoryName: String, packageList: List<String>)

    @Query("UPDATE Conversation SET groupName = :categoryName WHERE threadId=:tredid")
    suspend fun updateBroadCastName(categoryName: String, tredid: Long)

    @Query("UPDATE Conversation SET CategoryName = :categoryName WHERE threadId in (:packageList)")
    suspend fun updateEmptymessageCatName(categoryName: String, packageList: List<String>)

    @Query("UPDATE category SET catName = :categoryName WHERE catName =:catname")
    suspend fun updateCatName(categoryName: String, catname: String)

    @Query("SELECT (SELECT COUNT(*) FROM Conversation) == 0")
    suspend fun isEmpty(): Boolean

    @Query("UPDATE Conversation SET isnewmessage = :messageStatus WHERE threadId =:tredid")
    suspend fun setisoldmessage(messageStatus: Boolean, tredid: Long)

    @Query("SELECT $CONVERSATION_COLUMNS FROM Conversation WHERE phoneNumber =:phoneNumber AND newMessageCount=:count")
    fun getnewmessagecount(phoneNumber: String, count: Int): List<Conversation>

    @Query("UPDATE Conversation SET newMessageCount = :count WHERE threadId =:tredid")
    suspend fun setisoldmessageCount(count: Int, tredid: Long)

    @Query("UPDATE Conversation SET title = :title WHERE phoneNumber =:mobile")
    suspend fun updatemagetitle(title: String, mobile: String)

    @Query("delete from conversation where id = :messid")
    suspend fun deleteMessage(messid: Int)

    @Query("delete from conversation where messageId = :messid")
    suspend fun deleteMessagefrommessageid(messid: Long)

    @Query("delete from conversation where threadId in (:packageList)")
    suspend fun deleteConversation(packageList: List<String>)

    @Query("delete from conversationbin where threadId in (:packageList)")
    suspend fun deleteConversationRecyclerbin(packageList: List<String>)

    @Query("delete from conversationbin where messageId = :messid")
    suspend fun deleteConversationRecyclerbinMessid(messid: Long)

    @Query("delete from conversation where messageId in (:packageList)")
    suspend fun deleteMessagemessid(packageList: List<String>)

    @Query("UPDATE Conversation SET isnewmessage = 0 AND newMessageCount = 0")
    suspend fun allmessagemarkasread()

    @Query("UPDATE Conversation SET newMessageCount = 0")
    suspend fun allmessagemarkasreadNew()

    @Query("UPDATE Conversation SET isMessagefound = 0")
    suspend fun updateismassageinselected()

    @Insert
    suspend fun addrecentsearch(recentsearch: Recentsearch)

    @Query("SELECT EXISTS (SELECT 1 FROM recentsearch WHERE recentsearch = :recentsearch)")
    suspend fun isrecentsearchExits(recentsearch: String): Boolean

    @Query("DELETE FROM recentsearch")
    suspend fun deleteAllrecentsearch()

    @Query("delete from recentsearch where recentsearch = :recentsearch")
    suspend fun deleterecentsearch(recentsearch: String)

    @Query("SELECT $CONVERSATION_COLUMNS FROM Conversation WHERE threadId = :threadId AND messageId = :messageId AND is_scheduled = 1")
    fun getScheduledMessageWithId(threadId: Long, messageId: Long): Conversation

    @Query("SELECT $CONVERSATION_COLUMNS FROM Conversation WHERE threadId = :threadId AND messageId = :messageId")
    fun getMessageWithId(threadId: Long, messageId: Long): Conversation

    @Query("UPDATE Conversation SET ispinned = 1 WHERE threadId =:threadId")
    suspend fun addpinConversation(threadId: Long)

    @Query("UPDATE Conversation SET ispinned = 1 WHERE threadId =:threadId")
    suspend fun removepinConversation(threadId: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM Conversation WHERE  CategoryName= :searchString)")
    suspend fun isCatDataAvailable(searchString: String): Boolean

    @Query("SELECT $CONVERSATION_COLUMNS FROM Conversation WHERE threadId = :threadId AND isnewmessage = 1 LIMIT 1")
    suspend fun getisnewMessage(threadId: Long): Conversation?

    @Query("SELECT * FROM reminder WHERE id = :id")
    suspend fun getRemainderMessage(id: Long): Remindermodel?

    @Query("UPDATE Conversation SET isnewmessagescroll = 1 WHERE messageId =:messageid")
    suspend fun setiscrolltonewmessagestart(messageid: Long)

    @Query("UPDATE Conversation SET isnewmessagescroll = 0 WHERE threadId =:threadId")
    suspend fun setiscrolltonewmessageoff(threadId: Long)

}
