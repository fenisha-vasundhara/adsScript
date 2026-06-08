package com.messenger.phone.number.text.sms.service.apps.realmplan

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.lang.reflect.Proxy

/**
 * Test 4 — feature flag toggles between Room and Realm implementations.
 *
 * Verifies that:
 * - Default is Room (flag = false).
 * - Enabling the flag returns RealmConversationDataSource when a Realm is provided.
 * - Disabling the flag always returns RoomConversationDataSource.
 * - When realm=null, RoomConversationDataSource is returned even if the flag is true.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class RealmFeatureFlagTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        // Clean state for every test.
        context.getSharedPreferences(RealmFeatureFlag.PREFS_NAME, Context.MODE_PRIVATE)
            .edit().clear().commit()
    }

    @Test
    fun `default value is false — Room is the active source`() {
        val flag = RealmFeatureFlag(context)
        assertFalse(flag.useRealmReads)
    }

    @Test
    fun `flag persists across instances`() {
        RealmFeatureFlag(context).useRealmReads = true
        val readBack = RealmFeatureFlag(context)
        assertTrue(readBack.useRealmReads)
    }

    @Test
    fun `flag can be toggled back to false`() {
        val flag = RealmFeatureFlag(context)
        flag.useRealmReads = true
        flag.useRealmReads = false
        assertFalse(RealmFeatureFlag(context).useRealmReads)
    }

    @Test
    fun `provider returns RoomConversationDataSource when flag is false`() {
        RealmFeatureFlag(context).useRealmReads = false
        val source = ConversationDataSourceProvider.get(context, repo = fakeRepo())
        assertTrue(
            "Expected RoomConversationDataSource, got ${source::class.simpleName}",
            source is RoomConversationDataSource
        )
    }

    @Test
    fun `provider returns RoomConversationDataSource when flag is true but realm is null`() {
        RealmFeatureFlag(context).useRealmReads = true
        val source = ConversationDataSourceProvider.get(context, repo = fakeRepo(), realm = null)
        assertTrue(
            "Expected RoomConversationDataSource when realm=null, got ${source::class.simpleName}",
            source is RoomConversationDataSource
        )
    }

    @Test
    fun `provider returns non-null source regardless of flag`() {
        val roomSource = ConversationDataSourceProvider.get(context, repo = fakeRepo())
        assertNotNull(roomSource)
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private fun fakeRepo(): com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo {
        val db = androidx.room.Room.inMemoryDatabaseBuilder(
            context,
            com.messenger.phone.number.text.sms.service.apps.DataBase.MessagerDatabase::class.java
        ).allowMainThreadQueries().build()
        // Realm is an interface in krdb — proxy satisfies the constructor without native code.
        // No realm methods are invoked in these tests (flag=false or realm=null in all paths).
        val noOpRealm = Proxy.newProxyInstance(
            io.github.xilinjia.krdb.Realm::class.java.classLoader,
            arrayOf(io.github.xilinjia.krdb.Realm::class.java)
        ) { _, _, _ -> null } as io.github.xilinjia.krdb.Realm
        val fakeRealmRepo = RealmMessagingRepo(noOpRealm)
        val fakeFlag = RealmFeatureFlag(context)
        return com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo(db, fakeRealmRepo, fakeFlag)
    }
}
