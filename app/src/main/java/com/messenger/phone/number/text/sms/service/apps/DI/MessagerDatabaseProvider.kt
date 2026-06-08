package com.messenger.phone.number.text.sms.service.apps.DI

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.messenger.phone.number.text.sms.service.apps.DataBase.MessagerDatabase
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
import com.messenger.phone.number.text.sms.service.apps.realmplan.RealmFeatureFlag
import com.messenger.phone.number.text.sms.service.apps.realmplan.RealmMessagingRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class MessagerDatabaseProvider {

    @Singleton
    @Provides
    fun provideMessagerDatabase(@ApplicationContext context: Context): MessagerDatabase {
        return Room.databaseBuilder(context, MessagerDatabase::class.java, "MessageDB")
            .addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3,
                MIGRATION_3_4,
                MIGRATION_4_5,
                MIGRATION_5_6,
                MIGRATION_6_7,
                MIGRATION_7_8,
                MIGRATION_8_9,
                MIGRATION_9_10,
                MIGRATION_10_11,
                MIGRATION_11_12,
                MIGRATION_12_13,
                MIGRATION_13_14,
                MIGRATION_14_15,
                MIGRATION_15_16,
                MIGRATION_16_17,
                MIGRATION_17_18,
                MIGRATION_18_19,
                MIGRATION_19_20,
                MIGRATION_20_21,
                MIGRATION_21_22,
                MIGRATION_22_23,
                MIGRATION_23_24,
                MIGRATION_24_25,
                MIGRATION_25_26,
                MIGRATION_26_27,
                MIGRATION_27_28,
                MIGRATION_28_29,
                MIGRATION_29_30,
                MIGRATION_30_31,
                MIGRATION_31_32,
                MIGRATION_32_33,
                MIGRATION_33_34,
                MIGRATION_34_35,
                MIGRATION_35_36,
                MIGRATION_36_37
            ).build()
    }

    @Provides
    @Singleton
    fun provideMessagerDatabaseRepo(
        database: MessagerDatabase,
        realmRepo: RealmMessagingRepo,
        flag: RealmFeatureFlag,
    ): MessagerDatabaseRepo {
        return MessagerDatabaseRepo(database, realmRepo, flag)
    }

    val MIGRATION_1_2: Migration = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE Conversation ADD COLUMN messageId INTEGER")
        }
    }

    val MIGRATION_2_3: Migration = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE Conversation ADD COLUMN threadId INTEGER")
        }
    }

    val MIGRATION_3_4: Migration = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE Conversation ADD COLUMN pinneddate INTEGER")
        }
    }

    val MIGRATION_4_5: Migration = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE Conversation ADD COLUMN ispinned INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE Conversation ADD COLUMN isarchived INTEGER NOT NULL DEFAULT 0")
        }
    }

    val MIGRATION_5_6: Migration = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE Conversation ADD COLUMN isblocknumber INTEGER NOT NULL DEFAULT 0")
        }
    }

    val MIGRATION_6_7: Migration = object : Migration(6, 7) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("CREATE TABLE IF NOT EXISTS `Recentsearch` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `recentsearch` TEXT NOT NULL)")
        }
    }

    val MIGRATION_7_8: Migration = object : Migration(7, 8) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE Conversation ADD COLUMN is_scheduled INTEGER NOT NULL DEFAULT 0")
        }
    }

    val MIGRATION_8_9: Migration = object : Migration(8, 9) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE Conversation ADD COLUMN isMessagefound INTEGER NOT NULL DEFAULT 0")
        }
    }

    val MIGRATION_9_10: Migration = object : Migration(9, 10) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE Conversation ADD COLUMN draftmessage TEXT")
        }
    }

    val MIGRATION_10_11: Migration = object : Migration(10, 11) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE Conversation ADD COLUMN isexpandmessageview INTEGER NOT NULL DEFAULT 0")
        }
    }


    val MIGRATION_11_12: Migration = object : Migration(11, 12) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE Conversation ADD COLUMN messagetype TEXT")
            db.execSQL("ALTER TABLE Conversation ADD COLUMN messageotp TEXT")
        }
    }

    val MIGRATION_12_13: Migration = object : Migration(12, 13) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE Conversation ADD COLUMN isPrivateChat INTEGER NOT NULL DEFAULT 0")
        }
    }

    val MIGRATION_13_14: Migration = object : Migration(13, 14) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE Conversation ADD COLUMN shownotification INTEGER NOT NULL DEFAULT 1")
        }
    }

    val MIGRATION_14_15: Migration = object : Migration(14, 15) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("UPDATE Conversation SET isexpandmessageview = 1 WHERE isexpandmessageview = 0")

        }
    }

    val MIGRATION_15_16: Migration = object : Migration(15, 16) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE Conversation ADD COLUMN messagetraslateshow INTEGER NOT NULL DEFAULT 0")
        }
    }

    val MIGRATION_16_17: Migration = object : Migration(16, 17) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE Conversation ADD COLUMN messagetraslationanimationshow INTEGER NOT NULL DEFAULT 0")
        }
    }


    val MIGRATION_17_18: Migration = object : Migration(17, 18) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE Conversation ADD COLUMN isgroupmessage INTEGER NOT NULL DEFAULT 0")
        }
    }

    val MIGRATION_18_19: Migration = object : Migration(18, 19) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE Conversation ADD COLUMN groupName TEXT")
        }
    }

    val MIGRATION_19_20: Migration = object : Migration(19, 20) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE category ADD COLUMN isDefaultCategory INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE category ADD COLUMN filterName TEXT")
        }
    }

    val MIGRATION_20_21: Migration = object : Migration(20, 21) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE Conversation ADD COLUMN CategoryName TEXT")
        }
    }

    val MIGRATION_21_22: Migration = object : Migration(21, 22) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE Conversation ADD COLUMN isnewmessagescroll INTEGER NOT NULL DEFAULT 0")
//            RefreshAllData()
        }
    }

    val MIGRATION_22_23: Migration = object : Migration(22, 23) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("CREATE TABLE IF NOT EXISTS Foldermodel (id INTEGER PRIMARY KEY AUTOINCREMENT, foldername TEXT NOT NULL, folderimgcount TEXT NOT NULL, folderthumimage TEXT NOT NULL)")
        }
    }

    val MIGRATION_23_24: Migration = object : Migration(23, 24) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE Foldermodel ADD COLUMN folderpath TEXT")
        }
    }

    val MIGRATION_24_25: Migration = object : Migration(24, 25) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("CREATE TABLE IF NOT EXISTS Photo (id INTEGER PRIMARY KEY AUTOINCREMENT, path TEXT NOT NULL, position INTEGER NOT NULL DEFAULT 0,  selected INTEGER NOT NULL DEFAULT 0,lastModifieddate INTEGER NOT NULL DEFAULT 0)")
        }
    }

    val MIGRATION_25_26: Migration = object : Migration(25, 26) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE Conversation ADD COLUMN isonlyselectedthem INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE Conversation ADD COLUMN themenumber INTEGER")
            db.execSQL("ALTER TABLE Conversation ADD COLUMN customtimeuri TEXT")
        }
    }

    val MIGRATION_26_27: Migration = object : Migration(26, 27) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE Conversation ADD COLUMN isbanneradshow INTEGER NOT NULL DEFAULT 0")
        }
    }

    val MIGRATION_27_28 = object : Migration(27, 28) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("CREATE TABLE reminder (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, remindertitle TEXT, reminderstartdate TEXT, reminderenddate TEXT, selected INTEGER NOT NULL DEFAULT 0)")
        }
    }

    val MIGRATION_28_29 = object : Migration(28, 29) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE Conversation ADD COLUMN messagewithattachment TEXT")
            db.execSQL("CREATE TABLE IF NOT EXISTS `message_attachments` (`id` INTEGER PRIMARY KEY NOT NULL, `text` TEXT NOT NULL, `attachments` TEXT NOT NULL)")
            db.execSQL("CREATE TABLE IF NOT EXISTS `attachments` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `message_id` INTEGER NOT NULL, `uri_string` TEXT NOT NULL, `mimetype` TEXT NOT NULL, `width` INTEGER NOT NULL, `height` INTEGER NOT NULL, `filename` TEXT NOT NULL)")
            db.execSQL("CREATE UNIQUE INDEX `index_attachments_message_id` ON `attachments` (`message_id`)")
        }
    }

    val MIGRATION_29_30 = object : Migration(29, 30) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("CREATE TABLE Conversationbin (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
        }
    }
    val MIGRATION_30_31 = object : Migration(30, 31) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN date TEXT")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN draftmessage TEXT")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN messagetype TEXT")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN messageotp TEXT")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN groupName TEXT")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN CategoryName TEXT")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN customtimeuri TEXT")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN phoneNumber TEXT")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN snippet TEXT")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN title TEXT")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN photoUri TEXT")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN messageStatus TEXT")
        }
    }

    val MIGRATION_31_32 = object : Migration(31, 32) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN read INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN usesCustomTitle INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN isnumaric INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN isnewmessage INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN isarchived INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN ispinned INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN isblocknumber INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN isexpandmessageview INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN is_scheduled INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN isMessagefound INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN isPrivateChat INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN shownotification INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN messagetraslateshow INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN messagetraslationanimationshow INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN isgroupmessage INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN isnewmessagescroll INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN isonlyselectedthem INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN isbanneradshow INTEGER NOT NULL DEFAULT 0")
        }
    }

    val MIGRATION_32_33: Migration = object : Migration(32, 33) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN time INTEGER")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN messageId INTEGER")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN threadId INTEGER")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN pinneddate INTEGER")
        }
    }

    val MIGRATION_33_34: Migration = object : Migration(33, 34) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN type INTEGER")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN newMessageCount INTEGER")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN themenumber INTEGER")
            db.execSQL("ALTER TABLE Conversationbin ADD COLUMN messagewithattachment TEXT")
        }
    }
    val MIGRATION_34_35: Migration = object : Migration(34, 35) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Version 35 does not change the schema compared to version 34.
            // Keep the migration explicit so Room upgrades preserve existing rows and flags.
        }
    }

    val MIGRATION_35_36: Migration = object : Migration(35, 36) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Exported schemas 35 and 36 are identical in this repo.
            // Keep this explicit to make the on-device version history truthful.
        }
    }

    val MIGRATION_36_37: Migration = object : Migration(36, 37) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Exported schemas 36 and 37 are identical in this repo.
            // This is a bookkeeping migration only; no user data is rewritten here.
        }
    }

//    val MIGRATION_34_35: Migration = object : Migration(34, 35) {
//        override fun migrate(db: SupportSQLiteDatabase) {
//            db.execSQL(
//                """
//                CREATE TABLE IF NOT EXISTS `Conversation_new` (
//                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
//                    `date` TEXT NOT NULL,
//                    `read` INTEGER NOT NULL,
//                    `title` TEXT NOT NULL,
//                    `photoUri` TEXT,
//                    `usesCustomTitle` INTEGER NOT NULL,
//                    `phoneNumber` TEXT NOT NULL,
//                    `snippet` TEXT NOT NULL,
//                    `time` INTEGER,
//                    `type` INTEGER,
//                    `isnumaric` INTEGER NOT NULL,
//                    `messageStatus` TEXT,
//                    `isnewmessage` INTEGER,
//                    `newMessageCount` INTEGER,
//                    `messageId` INTEGER,
//                    `threadId` INTEGER,
//                    `isarchived` INTEGER NOT NULL,
//                    `ispinned` INTEGER NOT NULL,
//                    `pinneddate` INTEGER,
//                    `isblocknumber` INTEGER NOT NULL,
//                    `isexpandmessageview` INTEGER NOT NULL,
//                    `is_scheduled` INTEGER NOT NULL,
//                    `isMessagefound` INTEGER NOT NULL,
//                    `isPrivateChat` INTEGER NOT NULL,
//                    `draftmessage` TEXT,
//                    `messagetype` TEXT,
//                    `messageotp` TEXT,
//                    `shownotification` INTEGER NOT NULL,
//                    `messagetraslateshow` INTEGER NOT NULL,
//                    `messagetraslationanimationshow` INTEGER NOT NULL,
//                    `isgroupmessage` INTEGER NOT NULL,
//                    `groupName` TEXT,
//                    `CategoryName` TEXT,
//                    `isnewmessagescroll` INTEGER NOT NULL,
//                    `isonlyselectedthem` INTEGER NOT NULL,
//                    `themenumber` INTEGER,
//                    `customtimeuri` TEXT,
//                    `isbanneradshow` INTEGER NOT NULL,
//                    `messagewithattachment` TEXT
//                )
//                """.trimIndent()
//            )
//
//            db.execSQL(
//                """
//                INSERT INTO `Conversation_new` (
//                    `id`, `date`, `read`, `title`, `photoUri`, `usesCustomTitle`, `phoneNumber`, `snippet`,
//                    `time`, `type`, `isnumaric`, `messageStatus`, `isnewmessage`, `newMessageCount`,
//                    `messageId`, `threadId`, `isarchived`, `ispinned`, `pinneddate`, `isblocknumber`,
//                    `isexpandmessageview`, `is_scheduled`, `isMessagefound`, `isPrivateChat`, `draftmessage`,
//                    `messagetype`, `messageotp`, `shownotification`, `messagetraslateshow`,
//                    `messagetraslationanimationshow`, `isgroupmessage`, `groupName`, `CategoryName`,
//                    `isnewmessagescroll`, `isonlyselectedthem`, `themenumber`, `customtimeuri`,
//                    `isbanneradshow`, `messagewithattachment`
//                )
//                SELECT
//                    `id`,
//                    COALESCE(`date`, ''),
//                    COALESCE(`read`, 0),
//                    COALESCE(`title`, ''),
//                    `photoUri`,
//                    COALESCE(`usesCustomTitle`, 0),
//                    COALESCE(`phoneNumber`, ''),
//                    COALESCE(`snippet`, ''),
//                    `time`,
//                    `type`,
//                    COALESCE(`isnumaric`, 0),
//                    `messageStatus`,
//                    COALESCE(`isnewmessage`, 0),
//                    COALESCE(`newMessageCount`, 0),
//                    `messageId`,
//                    `threadId`,
//                    COALESCE(`isarchived`, 0),
//                    COALESCE(`ispinned`, 0),
//                    `pinneddate`,
//                    COALESCE(`isblocknumber`, 0),
//                    COALESCE(`isexpandmessageview`, 1),
//                    COALESCE(`is_scheduled`, 0),
//                    COALESCE(`isMessagefound`, 0),
//                    COALESCE(`isPrivateChat`, 0),
//                    `draftmessage`,
//                    `messagetype`,
//                    `messageotp`,
//                    COALESCE(`shownotification`, 1),
//                    COALESCE(`messagetraslateshow`, 0),
//                    COALESCE(`messagetraslationanimationshow`, 0),
//                    COALESCE(`isgroupmessage`, 0),
//                    `groupName`,
//                    `CategoryName`,
//                    COALESCE(`isnewmessagescroll`, 0),
//                    COALESCE(`isonlyselectedthem`, 0),
//                    `themenumber`,
//                    `customtimeuri`,
//                    COALESCE(`isbanneradshow`, 0),
//                    CASE
//                        WHEN `messagewithattachment` IS NULL THEN NULL
//                        WHEN LENGTH(`messagewithattachment`) > 1000000 THEN NULL
//                        ELSE `messagewithattachment`
//                    END
//                FROM `Conversation`
//                WHERE `messageId` IS NULL OR `messageId` <= 0
//                   OR `id` IN (
//                        SELECT MIN(`id`) FROM `Conversation`
//                        WHERE `messageId` > 0
//                        GROUP BY `messageId`
//                    )
//                """.trimIndent()
//            )
//
//            db.execSQL("DROP TABLE `Conversation`")
//            db.execSQL("ALTER TABLE `Conversation_new` RENAME TO `Conversation`")
//
//        }
//    }

//    val MIGRATION_35_36: Migration = object : Migration(35, 36) {
//        override fun migrate(db: SupportSQLiteDatabase) {
//            db.execSQL("DROP TABLE IF EXISTS `Conversation_new`")
//            db.execSQL(
//                """
//                CREATE TABLE IF NOT EXISTS `Conversation_new` (
//                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
//                    `date` TEXT NOT NULL,
//                    `read` INTEGER NOT NULL,
//                    `title` TEXT NOT NULL,
//                    `photoUri` TEXT,
//                    `usesCustomTitle` INTEGER NOT NULL,
//                    `phoneNumber` TEXT NOT NULL,
//                    `snippet` TEXT NOT NULL,
//                    `time` INTEGER,
//                    `type` INTEGER,
//                    `isnumaric` INTEGER NOT NULL,
//                    `messageStatus` TEXT,
//                    `isnewmessage` INTEGER,
//                    `newMessageCount` INTEGER,
//                    `messageId` INTEGER,
//                    `threadId` INTEGER,
//                    `isarchived` INTEGER NOT NULL,
//                    `ispinned` INTEGER NOT NULL,
//                    `pinneddate` INTEGER,
//                    `isblocknumber` INTEGER NOT NULL,
//                    `isexpandmessageview` INTEGER NOT NULL,
//                    `is_scheduled` INTEGER NOT NULL,
//                    `isMessagefound` INTEGER NOT NULL,
//                    `isPrivateChat` INTEGER NOT NULL,
//                    `draftmessage` TEXT,
//                    `messagetype` TEXT,
//                    `messageotp` TEXT,
//                    `shownotification` INTEGER NOT NULL,
//                    `messagetraslateshow` INTEGER NOT NULL,
//                    `messagetraslationanimationshow` INTEGER NOT NULL,
//                    `isgroupmessage` INTEGER NOT NULL,
//                    `groupName` TEXT,
//                    `CategoryName` TEXT,
//                    `isnewmessagescroll` INTEGER NOT NULL,
//                    `isonlyselectedthem` INTEGER NOT NULL,
//                    `themenumber` INTEGER,
//                    `customtimeuri` TEXT,
//                    `isbanneradshow` INTEGER NOT NULL,
//                    `messagewithattachment` TEXT
//                )
//                """.trimIndent()
//            )
//
//            val select = buildConversationSelectForMigration(db)
//            val whereClause = buildConversationWhereClauseForMigration(db)
//
//            db.execSQL(
//                """
//                INSERT INTO `Conversation_new` (
//                    `id`, `date`, `read`, `title`, `photoUri`, `usesCustomTitle`, `phoneNumber`, `snippet`,
//                    `time`, `type`, `isnumaric`, `messageStatus`, `isnewmessage`, `newMessageCount`,
//                    `messageId`, `threadId`, `isarchived`, `ispinned`, `pinneddate`, `isblocknumber`,
//                    `isexpandmessageview`, `is_scheduled`, `isMessagefound`, `isPrivateChat`, `draftmessage`,
//                    `messagetype`, `messageotp`, `shownotification`, `messagetraslateshow`,
//                    `messagetraslationanimationshow`, `isgroupmessage`, `groupName`, `CategoryName`,
//                    `isnewmessagescroll`, `isonlyselectedthem`, `themenumber`, `customtimeuri`,
//                    `isbanneradshow`, `messagewithattachment`
//                )
//                SELECT $select FROM `Conversation` $whereClause
//                """.trimIndent()
//            )
//
//            db.execSQL("DROP TABLE `Conversation`")
//            db.execSQL("ALTER TABLE `Conversation_new` RENAME TO `Conversation`")
//
//        }
//    }

    private fun buildConversationSelectForMigration(db: SupportSQLiteDatabase): String {
        val hasColumn = { name: String -> hasColumn(db, "Conversation", name) }

        val columns = ArrayList<String>(40)
        columns += if (hasColumn("id")) "`id`" else "NULL"
        columns += if (hasColumn("date")) "COALESCE(`date`, '')" else "''"
        columns += if (hasColumn("read")) "COALESCE(`read`, 0)" else "0"
        columns += if (hasColumn("title")) "COALESCE(`title`, '')" else "''"
        columns += if (hasColumn("photoUri")) "`photoUri`" else "NULL"
        columns += if (hasColumn("usesCustomTitle")) "COALESCE(`usesCustomTitle`, 0)" else "0"
        columns += if (hasColumn("phoneNumber")) "COALESCE(`phoneNumber`, '')" else "''"
        columns += if (hasColumn("snippet")) "COALESCE(`snippet`, '')" else "''"
        columns += if (hasColumn("time")) "`time`" else "NULL"
        columns += if (hasColumn("type")) "`type`" else "NULL"
        columns += if (hasColumn("isnumaric")) "COALESCE(`isnumaric`, 0)" else "0"
        columns += if (hasColumn("messageStatus")) "`messageStatus`" else "NULL"
        columns += if (hasColumn("isnewmessage")) "COALESCE(`isnewmessage`, 0)" else "0"
        columns += if (hasColumn("newMessageCount")) "COALESCE(`newMessageCount`, 0)" else "0"
        columns += if (hasColumn("messageId")) "`messageId`" else "NULL"
        columns += if (hasColumn("threadId")) "`threadId`" else "NULL"
        columns += if (hasColumn("isarchived")) "COALESCE(`isarchived`, 0)" else "0"
        columns += if (hasColumn("ispinned")) "COALESCE(`ispinned`, 0)" else "0"
        columns += if (hasColumn("pinneddate")) "`pinneddate`" else "NULL"
        columns += if (hasColumn("isblocknumber")) "COALESCE(`isblocknumber`, 0)" else "0"
        columns += if (hasColumn("isexpandmessageview")) "COALESCE(`isexpandmessageview`, 1)" else "1"
        columns += if (hasColumn("is_scheduled")) "COALESCE(`is_scheduled`, 0)" else "0"
        columns += if (hasColumn("isMessagefound")) "COALESCE(`isMessagefound`, 0)" else "0"
        columns += if (hasColumn("isPrivateChat")) "COALESCE(`isPrivateChat`, 0)" else "0"
        columns += if (hasColumn("draftmessage")) "`draftmessage`" else "NULL"
        columns += if (hasColumn("messagetype")) "`messagetype`" else "NULL"
        columns += if (hasColumn("messageotp")) "`messageotp`" else "NULL"
        columns += if (hasColumn("shownotification")) "COALESCE(`shownotification`, 1)" else "1"
        columns += if (hasColumn("messagetraslateshow")) "COALESCE(`messagetraslateshow`, 0)" else "0"
        columns += if (hasColumn("messagetraslationanimationshow")) "COALESCE(`messagetraslationanimationshow`, 0)" else "0"
        columns += if (hasColumn("isgroupmessage")) "COALESCE(`isgroupmessage`, 0)" else "0"
        columns += if (hasColumn("groupName")) "`groupName`" else "NULL"
        columns += if (hasColumn("CategoryName")) "`CategoryName`" else "NULL"
        columns += if (hasColumn("isnewmessagescroll")) "COALESCE(`isnewmessagescroll`, 0)" else "0"
        columns += if (hasColumn("isonlyselectedthem")) "COALESCE(`isonlyselectedthem`, 0)" else "0"
        columns += if (hasColumn("themenumber")) "`themenumber`" else "NULL"
        columns += if (hasColumn("customtimeuri")) "`customtimeuri`" else "NULL"
        columns += if (hasColumn("isbanneradshow")) "COALESCE(`isbanneradshow`, 0)" else "0"
        columns += if (hasColumn("messagewithattachment")) {
            "CASE " +
                "WHEN `messagewithattachment` IS NULL THEN NULL " +
                "WHEN LENGTH(`messagewithattachment`) > 1000000 THEN NULL " +
                "ELSE `messagewithattachment` END"
        } else {
            "NULL"
        }

        return columns.joinToString(", ")
    }

    private fun buildConversationWhereClauseForMigration(db: SupportSQLiteDatabase): String {
        val hasMessageId = hasColumn(db, "Conversation", "messageId")
        val hasId = hasColumn(db, "Conversation", "id")
        if (!hasMessageId || !hasId) return ""

        return """
            WHERE `messageId` IS NULL OR `messageId` <= 0
               OR `id` IN (
                    SELECT MIN(`id`) FROM `Conversation`
                    WHERE `messageId` > 0
                    GROUP BY `messageId`
                )
        """.trimIndent()
    }

    private fun hasColumn(db: SupportSQLiteDatabase, tableName: String, columnName: String): Boolean {
        db.query("PRAGMA table_info(`$tableName`)").use { cursor ->
            val nameIndex = cursor.getColumnIndex("name")
            if (nameIndex == -1) return false
            while (cursor.moveToNext()) {
                if (cursor.getString(nameIndex).equals(columnName, ignoreCase = true)) {
                    return true
                }
            }
        }
        return false
    }

}
