package com.messenger.phone.number.text.sms.service.apps.data

import android.content.Context
import com.messenger.phone.number.text.sms.service.apps.CommanClass.CotegorySetPref
import com.messenger.phone.number.text.sms.service.apps.CommanClass.PINNED_CONVERSATIONS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.USE_SIM_ID_PREFIX
import com.messenger.phone.number.text.sms.service.apps.modelClass.Category
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation

class Config(context: Context) : BaseConfig(context) {
    companion object {
        fun newInstance(context: Context) = Config(context)
    }

    fun getUseSIMIdAtNumber(number: String) = prefs.getInt(USE_SIM_ID_PREFIX + number, 0)

    fun saveUseSIMIdAtNumber(number: String, SIMId: Int) {
        prefs.edit().putInt(USE_SIM_ID_PREFIX + number, SIMId).apply()
    }

    var forfirsttimeuser: Boolean
        get() = prefs.getBoolean("forfirsttimeuserpref", true)!!
        set(introshow) = prefs.edit().putBoolean("forfirsttimeuserpref", introshow).apply()

    var pinnedConversations: Set<String>
        get() = prefs.getStringSet(PINNED_CONVERSATIONS, HashSet<String>())!!
        set(pinnedConversations) = prefs.edit().putStringSet(PINNED_CONVERSATIONS, pinnedConversations).apply()



    fun addPinnedConversations(conversations: List<Conversation>) {
        pinnedConversations = pinnedConversations.plus(
            conversations.mapNotNull { it.threadId?.toString() }
        )
    }
    fun removePinnedConversations(conversations: List<Conversation>) {
        pinnedConversations = pinnedConversations.minus(
            conversations.mapNotNull { it.threadId?.toString() }
        )
    }



    var CotegorySet: Set<String>
        get() = prefs.getStringSet(CotegorySetPref, HashSet<String>())!!
        set(pinnedConversations) = prefs.edit().putStringSet(CotegorySetPref, pinnedConversations).apply()

    fun addCotegorySet(conversations: List<Category>) {
        CotegorySet = CotegorySet.plus(conversations.map { it.catName})
    }
    fun removeCotegorySet(conversations: List<Category>) {
        CotegorySet = CotegorySet.minus(conversations.map { it.catName})
    }

}
