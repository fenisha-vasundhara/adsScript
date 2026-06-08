package com.messenger.phone.number.text.sms.service.apps.sms

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import android.provider.Telephony
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getMMSSender
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getMmsAttachment
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getNameAndPhotoFromPhoneNumber
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isPhoneNumber
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.data.AddressBookContact
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.modelClass.Getmms
import com.messenger.phone.number.text.sms.service.apps.sms.SmsUtils.deleteSMS
import com.simplemobiletools.commons.extensions.showErrorToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val contactCache = HashMap<String, String>()

object SmsUtils {
    fun deleteSmsByContent(context: Context, phoneNumber: String, messageBody: String) {
        val contentResolver = context.contentResolver
        val uri = Telephony.Sms.CONTENT_URI
        val where = Telephony.Sms.ADDRESS + " = ? AND " + Telephony.Sms.BODY + " = ?"
        val whereArgs = arrayOf(phoneNumber, messageBody)
        val cursor = contentResolver.query(uri, null, where, whereArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val messageId = cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.Sms._ID))
                val messageUri = Uri.withAppendedPath(Telephony.Sms.CONTENT_URI, messageId.toString())
                contentResolver.delete(messageUri, null, null)
            } while (cursor.moveToNext())
            cursor.close()
        }
    }

//    fun Context.deleteSMS(id: Int, isMms: Boolean = false) {
////        try {
////            if (packageName == Telephony.Sms.getDefaultSmsPackage(this)) {
////                contentResolver.delete(
////                    Uri.parse("content://sms/$id"),
////                    null, null
////                )
////            }
////        } catch (_: Exception) {
////            Toast.makeText(this, getString(R.string.Something_Went_Wrong), Toast.LENGTH_SHORT).show()
////        }
//
//
//        try {
//            // Check if the default SMS package is your app's package
//            if (packageName == Telephony.Sms.getDefaultSmsPackage(this)) {
//                val uri = if (isMms) {
//                    Uri.parse("content://mms/$id") // MMS URI
//                } else {
//                    Uri.parse("content://sms/$id") // SMS URI
//                }
//
//                // Delete the message
//                contentResolver.delete(uri, null, null)
//            }
//        } catch (e: Exception) {
//            // Catch any errors and show a toast message
//            CoroutineScope(Dispatchers.Main).launch {
//                Toast.makeText(this@deleteSMS, getString(R.string.Something_Went_Wrong), Toast.LENGTH_SHORT).show()
//            }
//        }
//    }


    fun Context.deleteSMS(id: Int,isMms: Boolean = false): Boolean {
        return try {
            val packageName = packageName
            val telephony = Telephony.Sms.getDefaultSmsPackage(this@deleteSMS)

            // Verify your app is the default SMS app
            if (packageName != telephony) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(this@deleteSMS, "App must be default SMS app", Toast.LENGTH_SHORT).show()
                }
                return false
            }
            val uri = if (isMms) {
                Uri.parse("content://mms/$id")
            } else {
                Uri.parse("content://sms/$id")
            }
            val rowsDeleted = this@deleteSMS.contentResolver.delete(uri, null, null)
            if (rowsDeleted > 0) {
                this@deleteSMS.contentResolver.notifyChange(uri, null)
                true
            } else {
                CoroutineScope(Dispatchers.Main).launch {
//                    Toast.makeText(this@deleteSMS, "Message not found", Toast.LENGTH_SHORT).show()
                }
                false
            }
        } catch (e: SecurityException) {
            CoroutineScope(Dispatchers.Main).launch {
//                Toast.makeText(this@deleteSMS, "Permission denied", Toast.LENGTH_SHORT).show()
            }
            false
        } catch (e: Exception) {
            CoroutineScope(Dispatchers.Main).launch {
//                Toast.makeText(this@deleteSMS, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            false
        }
    }


    fun Context.deleteConversation(threadId: Long) {
        var uri = Telephony.Sms.CONTENT_URI
        val selection = "${Telephony.Sms.THREAD_ID} = ?"
        val selectionArgs = arrayOf(threadId.toString())
        try {
            contentResolver.delete(uri, selection, selectionArgs)
        } catch (e: Exception) {
            showErrorToast(e)
        }

        uri = Telephony.Mms.CONTENT_URI
        try {
            contentResolver.delete(uri, selection, selectionArgs)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("Range")
    fun Context.getmms(): ArrayList<Conversation> {
        val idlist: ArrayList<Getmms> = arrayListOf()
        val photoUriget = ""
        val conList: ArrayList<Conversation> = arrayListOf()
        val contentResolver = contentResolver
        val mmsUri = Uri.parse("content://mms")
        val projection = arrayOf("_id", "thread_id", "date", "msg_box")
        val cursor = contentResolver.query(mmsUri, projection, null, null, null)

        if (cursor != null) {
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndex("_id"))
                val threadId = cursor.getLong(cursor.getColumnIndex("thread_id"))
                val date = cursor.getLong(cursor.getColumnIndex("date"))
                val type = cursor.getInt(cursor.getColumnIndex("msg_box"))
                idlist.add(Getmms(id, threadId, date*1000, type))
            }
        }
        cursor!!.close()
        idlist.forEach { messageid ->
            val body = getMmsAttachment(messageid.id, false)
            val number = getMMSSender(messageid.id)
            val sendername = getNameAndPhotoFromPhoneNumber(number)

            number.let {
                val conversation = Conversation(
                    0, messageid.date.toString(), true, sendername.name, photoUriget, false, number, body.text, messageid.date.toString().toLong(), messageid.type,
                    true, null, messageId = messageid.id, threadId = messageid.threadId
                )
                conList.add(conversation)

            }
        }
        return conList
    }
}