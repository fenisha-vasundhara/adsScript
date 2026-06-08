package com.messenger.phone.number.text.sms.service.apps.realmplan

import android.content.Context
import android.util.Log
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
import io.github.xilinjia.krdb.Realm

//import io.realm.kotlin.Realm

/**
 * Returns the correct [ConversationDataSourcePlan] implementation based on the
 * [RealmFeatureFlag].
 *
 * Room is the default and the safe fallback. Realm reads are enabled only when
 * [RealmFeatureFlag.useRealmReads] is true AND a valid [Realm] instance is supplied.
 * Callers that haven't wired Realm yet can pass null — they'll always get Room.
 */
object ConversationDataSourceProvider {

    fun get(
        context: Context,
        repo: MessagerDatabaseRepo,
        realm: Realm? = null,
    ): ConversationDataSourcePlan {
        val flag = RealmFeatureFlag(context)
        if (flag.useRealmReads && realm != null) {
            return try {
                RealmConversationDataSource(realm, repo)
            } catch (e: Exception) {
                Log.e(TAG, "Realm data source init failed, falling back to Room", e)
                flag.useRealmReads = false
                RoomConversationDataSource(repo)
            }
        }
        return RoomConversationDataSource(repo)
    }

    private const val TAG = "DataSourceProvider"
}
