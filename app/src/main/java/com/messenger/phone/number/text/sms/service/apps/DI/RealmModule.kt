package com.messenger.phone.number.text.sms.service.apps.DI

import com.messenger.phone.number.text.sms.service.apps.realmplan.AppRealmConfiguration
import com.messenger.phone.number.text.sms.service.apps.realmplan.LiveRealmMigrationStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
//import io.realm.kotlin.Realm
import io.github.xilinjia.krdb.Realm
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RealmModule {

    /**
     * One Realm instance for the entire process lifetime.
     * Realm.open() with the same config is safe to call multiple times —
     * the SDK reference-counts the underlying file handle internally — but
     * keeping one singleton is cleaner and avoids extra overhead.
     */
    @Singleton
    @Provides
    fun provideRealm(): Realm = Realm.open(AppRealmConfiguration.build())

    /**
     * The live migration store is stateless (all state lives in the Realm file and
     * SharedPreferences), so a new instance per injection site is fine. We scope it
     * to Singleton anyway to avoid unnecessary allocations.
     */
    @Singleton
    @Provides
    fun provideLiveRealmMigrationStore(realm: Realm): LiveRealmMigrationStore =
        LiveRealmMigrationStore(realm)
}
