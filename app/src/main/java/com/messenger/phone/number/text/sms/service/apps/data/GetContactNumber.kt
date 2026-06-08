package com.messenger.phone.number.text.sms.service.apps.data

import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import android.util.Log
import android.util.LongSparseArray
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.MutableLiveData
import com.messenger.phone.number.text.sms.service.apps.CommanClass.fetchContactIdFromPhoneNumber
import com.messenger.phone.number.text.sms.service.apps.CommanClass.phoneNumber
import com.messenger.phone.number.text.sms.service.apps.modelClass.Contact
import dagger.hilt.android.qualifiers.ApplicationContext
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.HashSet
import java.util.Locale
import javax.inject.Inject

class GetContactNumber @Inject constructor(@ApplicationContext var mContext: Context) {

    private val phoneUtil: PhoneNumberUtil = PhoneNumberUtil.createInstance(mContext)
    private val tm = mContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val list2 = ArrayList<Contact>()

    @Inject
    lateinit var messagerDatabaseRepo: MessagerDatabaseRepo

    @SuppressLint("Range")
    suspend fun getmobilenumber(): List<Contact> {
        delay(50)

        if (ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_DENIED
            && PermissionChecker.checkSelfPermission(
                mContext,
                Manifest.permission.READ_SMS
            ) == PermissionChecker.PERMISSION_GRANTED
            && PermissionChecker.checkSelfPermission(
                mContext,
                Manifest.permission.SEND_SMS
            ) == PermissionChecker.PERMISSION_GRANTED
        ) {

            val list = arrayListOf<AddressBookContact>()
            val array: LongSparseArray<AddressBookContact> = LongSparseArray<AddressBookContact>()
            val start = System.currentTimeMillis()

            val projection2 = arrayOf(
                ContactsContract.Data.MIMETYPE,
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Contactables.DATA,
                ContactsContract.CommonDataKinds.Contactables.TYPE
            )
            val selection = ContactsContract.Data.MIMETYPE + " in (?, ?)"
            val selectionArgs = arrayOf(
                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
            )
            val sortOrder2 = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} COLLATE NOCASE"

            val uri = ContactsContract.CommonDataKinds.Contactables.CONTENT_URI
            val cursor = mContext.contentResolver.query(uri, projection2, selection, selectionArgs, sortOrder2)

            val mimeTypeIdx = cursor!!.getColumnIndex(ContactsContract.Data.MIMETYPE)
            val idIdx = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)
            val nameIdx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val dataIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.DATA)
            val typeIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.TYPE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIdx)
                var addressBookContact: AddressBookContact? = array.get(id)
                if (addressBookContact == null) {
                    addressBookContact = AddressBookContact(id, cursor.getString(nameIdx), mContext.resources)
                    array.put(id, addressBookContact)
                    list.add(addressBookContact)
                }
                val type = cursor.getInt(typeIdx)
                val data = cursor.getString(dataIdx) ?: ""
                val phonrnumber = data.trim().replace(" ", "")
                val phonrnumber1 = phonrnumber.trim().replace("-", "")
                addressBookContact.addPhone(type, phonrnumber1)
                var number = ""

                if (phonrnumber.isNotEmpty()) {
                    if (phonrnumber.startsWith("+")) {
                        number = removeNonPlusAndDigits(phonrnumber)
                    } else if (phonrnumber[0].isDigit()) {
                        if (phonrnumber.length == 12) {
                            if (phonrnumber.startsWith("91")) {
                                val num = phonrnumber1.removePrefix("91")
                                number = removeNonPlusAndDigits("+91$num")
                            }
                        } else {
                            number = removeNonPlusAndDigits("+91$phonrnumber")
                        }
                    } else {
                        number = phonrnumber
                    }
                } else {
                    number = phonrnumber
                }


//                if (phonrnumber1[0].isLetter()) {
//                    list2.add(Contact(cursor.getString(nameIdx), number, id.toInt(), number))
//                } else {
//                    list2.add(Contact(cursor.getString(nameIdx), number, id.toInt(), number))
//                }
                try {
                    if (phonrnumber1.isNotEmpty() && phonrnumber1[0].isLetter()) {
                        list2.add(Contact(cursor.getString(nameIdx), number, id.toInt(), number))
                    } else {
                        list2.add(Contact(cursor.getString(nameIdx), number, id.toInt(), number))
                    }
                } catch (e: Exception) {
                }
            }
            cursor.close()

        }

        messagerDatabaseRepo.getallconversationunarchivforcontactrepo().distinctBy { it.threadId }.forEachIndexed { index, conversation ->
            if (!conversation.isgroupmessage) {
                list2.add(Contact(conversation.title, conversation.phoneNumber, conversation.id, conversation.phoneNumber))
            }
        }


        return list2.distinctBy { it.number }.distinctBy { it.name.lowercase() }
    }

    fun getClean(number: String): String {
        return try {
            val countryCodeValue = tm.networkCountryIso
            val numberProto: Phonenumber.PhoneNumber = phoneUtil.parse(
                number,
                countryCodeValue.uppercase(Locale.ROOT)
            )
            if (numberProto.nationalNumber.toString().length < 7) {
                numberProto.nationalNumber.toString()
            } else "+${numberProto.countryCode} ${numberProto.nationalNumber}"
        } catch (e: NumberParseException) {
            number.filter {
                it.isLetterOrDigit()
            }
        }
    }

    fun removeNonPlusAndDigits(input: String): String {
        return input.replace(Regex("[^+\\d]"), "")
    }

}