package com.messenger.phone.number.text.sms.service.apps.translateAPI

import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class TranslateApi {

    suspend fun translate(
        text: String,
        sourceLang: String,
        targetLang: String,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            val url = buildTranslationUrl(text, sourceLang, targetLang)
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .build()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    parseTranslationResponse(responseBody, onSuccess)
                } else {
                    throw IOException("Unexpected response code: ${response.code}")
                }
            } catch (e: IOException) {
                Log.e("TranslateApi", "Error during translation request", e)
                onError(e)
            }
        }
    }

    private fun parseTranslationResponse(response: String?, onSuccess: (String) -> Unit) {
        response?.let {
            try {
                val jsonArray = JSONArray(response)
                val output = StringBuilder()
                for(i in 0 until jsonArray.getJSONArray(0).length()){
                    output.append(jsonArray.getJSONArray(0).getJSONArray(i).getString(0))
                }
                onSuccess(output.toString())
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }

    private fun buildTranslationUrl(text: String, sourceLang: String, targetLang: String): String {
        val encodedText = URLEncoder.encode(text, "utf-8")
        return "https://translate.googleapis.com/translate_a/single?client=gtx&sl=" +
                sourceLang + "&tl=" + targetLang + "&dt=t&q=" + encodedText
    }
}