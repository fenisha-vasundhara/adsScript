package com.messenger.phone.number.text.sms.service.apps.DataBase

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.modelclass.Foldermodel
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.modelclass.Photo
import com.messenger.phone.number.text.sms.service.apps.DAOClass.GalleryDAO
import com.messenger.phone.number.text.sms.service.apps.DAOClass.MessageDAO
import com.messenger.phone.number.text.sms.service.apps.DI.RefreshAllData
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

@Database(
    entities = [Conversation::class,
        Category::class,
        CategoryNumber::class,
        StarNumber::class,
        Contact::class,
        Recentsearch::class,
        Foldermodel::class,
        Remindermodel::class,
        Photo::class, Attachment::class, MessageAttachment::class, Conversationbin::class],
    version = 37,
 /*   autoMigrations = [
        AutoMigration(from = 34, to = 35)
    ]*/
)
@TypeConverters(Converters::class)
abstract class MessagerDatabase : RoomDatabase() {
    abstract fun getMessageDAO(): MessageDAO
    abstract fun getGalleryDAO(): GalleryDAO
}
