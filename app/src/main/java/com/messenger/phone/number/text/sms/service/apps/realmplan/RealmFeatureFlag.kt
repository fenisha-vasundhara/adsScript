package com.messenger.phone.number.text.sms.service.apps.realmplan

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Controls whether reads/writes are served from Realm or Room.
 *
 * Default is false (Room). Flip to true after migration completes or for new users.
 * The flag persists across process restarts via SharedPreferences.
 */
@Singleton
class RealmFeatureFlag @Inject constructor(@ApplicationContext context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var useRealmReads: Boolean
        get() = prefs.getBoolean(KEY_USE_REALM_READS, false)
        set(value) = prefs.edit { putBoolean(KEY_USE_REALM_READS, value) }

    companion object {
        const val PREFS_NAME = "realm_feature_flags"
        const val KEY_USE_REALM_READS = "use_realm_reads"
    }
}
