package com.messenger.phone.number.text.sms.service.apps.realmplan

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
//import io.realm.kotlin.Realm
//import io.realm.kotlin.ext.query
//import io.realm.kotlin.query.Sort
import io.github.xilinjia.krdb.Realm
import io.github.xilinjia.krdb.ext.query
import io.github.xilinjia.krdb.query.Sort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

/**
 * Offset-based PagingSource that reads [ConversationRealm] objects from Realm
 * and maps them to [Conversation] for the existing UI layer.
 *
 * Key = Int (page offset). The source is invalidated whenever the underlying
 * Realm results change, via a coroutine that collects [realmQuery.asFlow()].
 */
class RealmConversationPagingSource(
    private val realm: Realm,
) : PagingSource<Int, Conversation>() {

    private val realmQuery =
        realm.query<ConversationRealm>(
            "isarchived == false AND isblocknumber == false " +
                "AND isPrivateChat == false AND isScheduled == false"
        ).sort("time", Sort.DESCENDING)

    // Coroutine scope that observes Realm changes and invalidates this PagingSource.
    // Cancelled via registerInvalidatedCallback so it stops as soon as Paging discards us.
    private val observerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        registerInvalidatedCallback { observerScope.cancel() }
        observerScope.launch {
            realmQuery.asFlow()
                .drop(1) // skip the initial emission — data already loaded via load()
                .collect { invalidate() }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Conversation>): Int? =
        state.anchorPosition?.let { anchor ->
            (state.closestPageToPosition(anchor)?.prevKey?.plus(1))
                ?: (state.closestPageToPosition(anchor)?.nextKey?.minus(1))
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Conversation> {
        val offset = params.key ?: 0
        return runCatching {
            val all = realmQuery.find()
            val page = all.drop(offset).take(params.loadSize).map { it.toConversation() }
            LoadResult.Page(
                data    = page,
                prevKey = if (offset == 0) null else (offset - params.loadSize).coerceAtLeast(0),
                nextKey = if (page.size < params.loadSize) null else offset + page.size,
            )
        }.getOrElse { LoadResult.Error(it) }
    }
}
