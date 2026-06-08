package com.messenger.phone.number.text.sms.service.apps.AfterCallScreen.fragment

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.messenger.phone.number.text.sms.service.apps.CommanClass.CONTACT_INSERT_EDIT_REQUEST_CODE
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.incomingCallNumber
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.FragmentAfterCallMoreNewBinding
import com.simplemobiletools.commons.helpers.KEY_PHONE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AfterCallMoreNewFragment : Fragment() {

    lateinit var binding: FragmentAfterCallMoreNewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().setLocal()
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_after_call_more_new,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {

            if (requireActivity().config.activeThemeSelection==1){
                card.setCardBackgroundColor(Color.parseColor("#F2F2F2"))
            }else{
                card.setCardBackgroundColor(Color.parseColor("#232323"))
            }

            EditContact.setOnClickListener {
                editContactByPhoneNumber(incomingCallNumber!!)
            }
            Messages.setOnClickListener {
                openMessageApp(incomingCallNumber!!, "")
            }
            SendMail.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    sendEmail("","","")
                }
            }
            Calendar.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    openGoogleCalendar()
                }
            }
            WebAftercall.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    performGoogleSearch(incomingCallNumber!!)
                }
            }
        }
    }

    suspend fun performGoogleSearch(query: String) {
        val intent = Intent(Intent.ACTION_WEB_SEARCH)
        val term = query
        intent.putExtra(SearchManager.QUERY, term)
        startActivity(intent)
    }

    suspend fun  openGoogleCalendar() {

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("content://com.android.calendar/time") // This opens the calendar view
             val dateInMillis = System.currentTimeMillis()
             data = Uri.parse("content://com.android.calendar/time/$dateInMillis")
        }

        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            CoroutineScope(Dispatchers.Main).launch {   requireActivity().toastMess("something went wrong") }
        }

    }


    suspend fun sendEmail(email: String, subject: String, body: String) {
        val emailsend: String = email
        val emailsubject: String = subject
        val emailbody: String = body
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailsend))
        intent.putExtra(Intent.EXTRA_SUBJECT, emailsubject)
        intent.putExtra(Intent.EXTRA_TEXT, emailbody)
        intent.setType("message/rfc822")
        startActivity(Intent.createChooser(intent, "Choose an Email client :"))
    }

    private fun openMessageApp(phoneNumber: String, message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val uri = Uri.parse("smsto:$phoneNumber")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                putExtra("sms_body", message)
            }
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    requireActivity().toastMess("something went wrong")
                }
            }
        }
    }

     fun editContactByPhoneNumber(phoneNumber1: String) {

         val phoneNumber = if (phoneNumber1.isBlank()) {
             "000"
         } else {
             phoneNumber1
         }
        Intent().apply {
            action = Intent.ACTION_INSERT_OR_EDIT
            type = "vnd.android.cursor.item/contact"
            putExtra(KEY_PHONE, phoneNumber)
            putExtra(ContactsContract.Intents.Insert.NAME, phoneNumber)
            try {
                startActivityForResult(this, CONTACT_INSERT_EDIT_REQUEST_CODE)
            } catch (e: ActivityNotFoundException) {
                requireActivity().toastMess("Contacts app not found")
            }

        }
    }

    @SuppressLint("Range")
    suspend fun getEmailFromPhoneNumber(context: Context, phoneNumber: String): String? {
        val contentResolver: ContentResolver = context.contentResolver
        val cursor: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.NUMBER),
            ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
            arrayOf(phoneNumber),
            null
        )
        var email: String? = null

        cursor?.let {
            if (it.moveToFirst()) {
                val contactId = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID))
                val emailCursor: Cursor? = contentResolver.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS),
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                    arrayOf(contactId),
                    null
                )
                emailCursor?.let { emailCursor ->
                    if (emailCursor.moveToFirst()) {
                        email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS))
                    }
                    emailCursor.close()
                }
            }
            cursor.close()
        }
        return email
    }
}