package com.messenger.phone.number.text.sms.service.apps.realmplan

//import io.realm.kotlin.RealmConfiguration as KtRealmConfiguration
import io.github.xilinjia.krdb.RealmConfiguration as KtRealmConfiguration

/**
 * Factory for the app-wide Realm configuration.
 *
 * Usage in Application.onCreate():
 *   val realm = io.realm.kotlin.Realm.open(AppRealmConfiguration.build())
 */
object AppRealmConfiguration {
    const val REALM_FILE_NAME = "messages.realm"
    const val SCHEMA_VERSION = 2L

    fun build(): KtRealmConfiguration = KtRealmConfiguration.Builder(
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
    )
        .name(REALM_FILE_NAME)
        .schemaVersion(SCHEMA_VERSION)
        .migration(AppRealmMigration)
        .build()
}
