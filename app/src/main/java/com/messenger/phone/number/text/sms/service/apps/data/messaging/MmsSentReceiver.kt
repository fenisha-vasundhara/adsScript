package com.messenger.phone.number.text.sms.service.apps.data.messaging

import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.net.Uri
import android.os.Handler
import android.provider.Telephony
import android.util.Log
import android.widget.Toast
import com.messenger.phone.number.text.sms.service.apps.CommanClass.showErrorToastMess
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class MmsSentReceiver : SendStatusReceiver() {

    @Inject
    lateinit var messagerDatabaseRepo: MessagerDatabaseRepo

    var messagestatus: String = "SMS delivered"
    override fun updateAndroidDatabase(context: Context, intent: Intent, receiverResultCode: Int) {
        val uri = Uri.parse(intent.getStringExtra(EXTRA_CONTENT_URI))
        val messageBox = if (receiverResultCode == Activity.RESULT_OK) {
            Telephony.Mms.MESSAGE_BOX_SENT
        } else {
            val msg = context.getString(
                R.string.unknown_error_occurred_sending_message,
                receiverResultCode
            )
            context.toastMess(msg = msg, length = Toast.LENGTH_LONG)
            Telephony.Mms.MESSAGE_BOX_FAILED
        }
        val values = ContentValues(1).apply {
            put(Telephony.Mms.MESSAGE_BOX, messageBox)
        }
        try {
            context.contentResolver.update(uri, values, null, null)
        } catch (e: SQLiteException) {
            context.showErrorToastMess(e)
        }
        val filePath = intent.getStringExtra(EXTRA_FILE_PATH)
        if (filePath != null) {
            File(filePath).delete()
        }
    }

    override fun updateAppDatabase(context: Context, intent: Intent, receiverResultCode: Int) {
        val uri = Uri.parse(intent.getStringExtra(EXTRA_CONTENT_URI))
        val messageId = getMessageIdFromUri(uri.toString())
        val handler = Handler(context.mainLooper)
        handler.postDelayed({
            if (receiverResultCode == Activity.RESULT_OK) {
                Log.d("", "updateAppDatabase: <-------------> 1 ${messageId}")
                CoroutineScope(Dispatchers.IO).launch {
                    messageId?.let {
                        messagerDatabaseRepo.updatemessagestatusfrommessageRepo(
                            "SMS delivered",
                            it
                        )
                    }
                }
            } else {
                Log.d("", "updateAppDatabase: <-------------> 2 ${messageId}")
                CoroutineScope(Dispatchers.IO).launch {
                    messageId?.let {
                        Log.d("", "updateAppDatabase: <-------------> 23 ${messageId}")
                        messagerDatabaseRepo.updatemessagestatusfrommessageRepo(
                            "Error",
                            it
                        )
                    }
                }
            }
        }, 2000)
    }

    companion object {
        private const val EXTRA_CONTENT_URI = "content_uri"
        private const val EXTRA_FILE_PATH = "file_path"
    }

    fun getMessageIdFromUri(uri: String): Long? {
        val contentUri = Uri.parse(uri)
        return contentUri.lastPathSegment?.toLongOrNull()
    }
}
