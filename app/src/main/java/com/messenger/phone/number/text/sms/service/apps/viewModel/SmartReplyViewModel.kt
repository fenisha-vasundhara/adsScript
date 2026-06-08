/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.messenger.phone.number.text.sms.service.apps.viewModel

import android.app.Application
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.tasks.Task
import com.google.mlkit.nl.smartreply.SmartReply
import com.google.mlkit.nl.smartreply.SmartReplySuggestion
import com.google.mlkit.nl.smartreply.SmartReplySuggestionResult
import com.google.mlkit.nl.smartreply.TextMessage
import java.util.UUID


class SmartReplyViewModel(application: Application) : AndroidViewModel(application) {

    private val remoteUserId = UUID.randomUUID().toString()
    private val suggestions = MediatorLiveData<List<SmartReplySuggestion>>()
    private val smartReply = SmartReply.getClient()
    private val messageList = MutableLiveData<MutableList<String>?>()
    private val emulatingRemoteUser = MutableLiveData<Boolean>()
    private var lastClickTime: Long = 0


    init {
        initSuggestionsGenerator()
        emulatingRemoteUser.postValue(false)
    }


    internal fun addMessage(message: String) {

        var list: MutableList<String>? = messageList.value
        if (list == null) {
            list = ArrayList()
        }
        list.add(message)
        clearSuggestions()
        messageList.postValue(list)
    }

    fun getSuggestions(): LiveData<List<SmartReplySuggestion>> {
        return suggestions
    }

    private fun clearSuggestions() {
        suggestions.postValue(ArrayList())
    }

    private fun initSuggestionsGenerator() {
        suggestions.addSource(
            emulatingRemoteUser,
            Observer { isEmulatingRemoteUser ->
                val list = messageList.value
                if (list.isNullOrEmpty()) {
                    return@Observer
                }
                val emulateRemoteUser = isEmulatingRemoteUser ?: return@Observer

                generateReplies(list, emulateRemoteUser).addOnSuccessListener { result ->
                    suggestions.postValue(result ?: emptyList())
                }
            }
        )

        suggestions.addSource(
            messageList,
            Observer { list ->
                val isEmulatingRemoteUser = emulatingRemoteUser.value
                if (isEmulatingRemoteUser == null || list.isNullOrEmpty()) {
                    return@Observer
                }

                generateReplies(list, isEmulatingRemoteUser).addOnSuccessListener { result ->
                    suggestions.postValue(result ?: emptyList())
                }
            }
        )
    }

    private fun generateReplies(
        messages: List<String>,
        isEmulatingRemoteUser: Boolean
    ): Task<List<SmartReplySuggestion>> {

        val chatHistory = ArrayList<TextMessage>()
        for (message in messages) {
            chatHistory.add(
                TextMessage.createForRemoteUser(message, System.currentTimeMillis(),remoteUserId)
            )
        }

        return smartReply
            .suggestReplies(chatHistory)
            .continueWith { task ->
                val result = task.result
                when (result.status) {
                    SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE -> {
                        Log.d("", "generateReplies: STATUS_NOT_SUPPORTED_LANGUAGE")
                    }
                    SmartReplySuggestionResult.STATUS_NO_REPLY -> { Log.d("", "generateReplies: STATUS_NO_REPLY")}
                    else -> {
                        // Do nothing.
                         Log.d("", "generateReplies: Do nothing.")
                    }
                }
                result!!.suggestions
            }
            .addOnFailureListener { e ->
                Log.e("", "Smart reply error", e)
                Toast.makeText(
                    getApplication(),
                    "Smart reply error" + "\nError: " + e.getLocalizedMessage() + "\nCause: " + e.cause,
                    Toast.LENGTH_LONG
                ).show()
            }
    }

}
