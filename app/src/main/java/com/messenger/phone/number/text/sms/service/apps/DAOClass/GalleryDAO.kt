package com.messenger.phone.number.text.sms.service.apps.DAOClass

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.modelclass.Foldermodel
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.modelclass.Photo
import com.messenger.phone.number.text.sms.service.apps.modelClass.Category
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation

@Dao
interface GalleryDAO {

    @Insert()
    suspend fun insertOrUpdateGallerydataFolder(data: Foldermodel)

    @Query("DELETE FROM Foldermodel")
    suspend fun deleteAllFolder()

    @Query("SELECT * FROM Foldermodel")
    suspend fun getallTFolderList(): List<Foldermodel>

    @Insert()
    suspend fun insertOrUpdateGallerydataFolderIMage(data: Photo)

}