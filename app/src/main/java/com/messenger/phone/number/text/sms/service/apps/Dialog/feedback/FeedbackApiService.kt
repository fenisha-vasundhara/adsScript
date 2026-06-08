package com.messenger.phone.number.text.sms.service.apps.Dialog.feedback

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

class FeedbackApiService {
    private val baseUrl = "https://appsfeedback.kriadl.com/api"
    private val authToken = "VkAkVSFAIyZESEEkJV5SQA=="

    suspend fun submitFeedback(feedbackRequest: FeedbackRequest): Boolean =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val url = URL("$baseUrl/app_feedback_create")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Authorization", authToken)
                connection.doOutput = true

                val json =
                    Json.Default.encodeToString(FeedbackRequest.serializer(), feedbackRequest)
                Log.d("FeedbackAPI", "Sending feedback: $json")

                connection.outputStream.write(json.toByteArray())

                val responseCode = connection.responseCode
                val responseMessage = connection.inputStream.bufferedReader().use { it.readText() }

                Log.d("FeedbackAPI", "Response Code: $responseCode")
                Log.d("FeedbackAPI", "Response: $responseMessage")

                connection.disconnect()

                responseCode in 200..299
            } catch (e: Exception) {
                Log.e("FeedbackAPI", "Error submitting feedback", e)
                false
            }
        }
}
