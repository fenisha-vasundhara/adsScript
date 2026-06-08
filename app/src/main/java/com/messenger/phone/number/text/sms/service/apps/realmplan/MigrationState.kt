package com.messenger.phone.number.text.sms.service.apps.realmplan

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Singleton observable for Realm migration progress.
 *
 * Produced by RoomToRealmWorker, consumed by any UI that wants to show a banner
 * ("Migrating your data…") or block on completion before switching reads to Realm.
 *
 * Room stays the source of truth regardless of state — a failed migration never
 * affects the user's ability to read/write messages.
 */
object MigrationStateHolder {

    private val _state = MutableStateFlow<MigrationState>(MigrationState.Idle)
    val state: StateFlow<MigrationState> = _state.asStateFlow()

    internal fun emit(s: MigrationState) { _state.value = s }
}

sealed interface MigrationState {
    /** Migration hasn't started yet (checked SharedPreferences, nothing scheduled). */
    data object Idle : MigrationState

    /** Worker is actively reading Room and writing Realm. */
    data class Running(val table: String = "", val rowsWritten: Int = 0) : MigrationState

    /** All rows validated and written. Realm is ready to be used as a read source. */
    data class Success(val summary: RoomToRealmSummary) : MigrationState

    /** Migration failed on this attempt. Worker will retry (see WorkManager policy). */
    data class Failed(
        val attempt: Int,
        val cause: String,
        val willRetry: Boolean,
    ) : MigrationState

    /** Worker exhausted all retries. Room remains source of truth indefinitely. */
    data object PermanentlyFailed : MigrationState
}
