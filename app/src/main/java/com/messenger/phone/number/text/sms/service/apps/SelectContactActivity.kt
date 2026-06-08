package com.messenger.phone.number.text.sms.service.apps

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.database.Cursor
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.text.Html
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.graphics.ColorUtils
import androidx.core.net.toUri
import androidx.core.text.isDigitsOnly
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import com.demo.adsmanage.Commen.Constants
import com.demo.adsmanage.Commen.firebaseEventMain
import com.demo.adsmanage.basemodule.BaseSharedPreferences

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.messenger.phone.number.text.sms.service.apps.CommanClass.Alredyclick
import com.messenger.phone.number.text.sms.service.apps.CommanClass.GENERIC_PERM_HANDLER
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperStatusBarColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getAddresses
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getContrastColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getDialogBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getThreadId
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getThreadParticipants
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getThreadTitle
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.hideKeyboard
import com.messenger.phone.number.text.sms.service.apps.CommanClass.invisible
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isShortCodeWithLetters
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isThirdPartyIntentCheck
import com.messenger.phone.number.text.sms.service.apps.CommanClass.realScreenSize
import com.messenger.phone.number.text.sms.service.apps.CommanClass.selectedConList
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.showKeyboard
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.CustomEditText.ContactChip
import com.messenger.phone.number.text.sms.service.apps.CustomEditText.model.ChipInterface
import com.messenger.phone.number.text.sms.service.apps.Dialog.RadioGroupDialog
import com.messenger.phone.number.text.sms.service.apps.adapter.SelectContactAdapter
import com.messenger.phone.number.text.sms.service.apps.data.PhoneNumber
import com.messenger.phone.number.text.sms.service.apps.data.SimpleContact
import com.messenger.phone.number.text.sms.service.apps.data.SimpleContactsHelper
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivitySelectContactBinding
import com.messenger.phone.number.text.sms.service.apps.helperClass.NetworkHelper
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.ContactNumberClickNew
//import com.messenger.phone.number.text.sms.service.apps.modelClass.ContactChip
import com.messenger.phone.number.text.sms.service.apps.modelClass.NestSimpleContact
import com.reddit.indicatorfastscroll.FastScrollItemIndicator
import com.simplemobiletools.commons.extensions.beVisibleIf
import com.simplemobiletools.commons.extensions.getPermissionString
import com.simplemobiletools.commons.extensions.getPhoneNumberTypeText
import com.simplemobiletools.commons.extensions.hasPermission
import com.simplemobiletools.commons.extensions.normalizeString
import com.simplemobiletools.commons.extensions.onTextChangeListener
import com.simplemobiletools.commons.extensions.toInt
import com.simplemobiletools.commons.extensions.toast
import com.simplemobiletools.commons.extensions.value
import com.simplemobiletools.commons.helpers.PERMISSION_READ_CONTACTS
import com.simplemobiletools.commons.models.RadioItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLDecoder
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class SelectContactActivity : AppCompatActivity(), ContactNumberClickNew {


    lateinit var binding: ActivitySelectContactBinding
    var isAskingPermissions = false
    var actionOnPermission: ((granted: Boolean) -> Unit)? = null

    private var participants: ArrayList<SimpleContact> = arrayListOf()

    private val mContactList: ArrayList<String> = arrayListOf()

    private var arrayList: ArrayList<String> = arrayListOf()

    @Inject
    lateinit var adapter: SelectContactAdapter

    lateinit var Number: String
    var mobilenumerofxiomi = ""
    var nameofxiomi = ""
    private lateinit var shimmilarnumber: String
    var isemptry: Boolean = false
    var forscheduleMessage: Boolean = false

    var contact = ArrayList<SimpleContact>()
    var contactnew = ArrayList<NestSimpleContact>()
    var numberTwo = ""
    var mobilelist: java.util.ArrayList<String> = arrayListOf()
    var message = ""
    var isforvard = false
    var isgropset = false
    var grouptread = -1L

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    private val filterScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var filterJob: Job? = null
    private var lastFilterQuery: String = ""

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocal()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_contact)

        setBaseTheme(binding.vAnd15StatusBar)

        this.firebaseEventMain("Select_Contact")

        binding.isdarktheme = ThemeModeManager.isDarkThemeActive(this)

        fontSize10 = getTextSizeForeNormal10MS()
        fontSize13 = getTextSizeForeNormal13MS()
        fontSize18 = getTextSizeForeNormal18MS()
        fontSize8 = getTextSizeForeNormal8MS()
        fontSize15 = getTextSizeHometitleMS()

        binding.textsizechagefor10 = fontSize10
        binding.textsizechagefor13 = fontSize13
        binding.textsizechagefor18 = fontSize18
        binding.textsizechagefor8 = fontSize8
        binding.textsizechagefor15 = fontSize15

        selectedConList.clear()

        message = intent.getStringExtra("message").toString()
        isforvard = intent.getBooleanExtra("isforvard", false)
        forscheduleMessage = intent.getBooleanExtra("forscheduleMessage", false)
        isgropset = intent.getBooleanExtra("isgropset", false)
        grouptread = intent.getLongExtra("grouptread", -1L)
        mobilenumerofxiomi = intent.getStringExtra("mobilenumerofxiomi").toString()
        nameofxiomi = intent.getStringExtra("nameofxiomi").toString()
        binding.txtSelected.isSelected = true
        setCustomBackground()

        Log.d(
            "GroupDetailsActivity",
            "initContact: grop <------> 19 arrayList -> ${mobilenumerofxiomi}"
        )
        Log.d("GroupDetailsActivity", "initContact: grop <------> 20 arrayList -> ${nameofxiomi}")



        binding.SearchContact.requestFocus()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        binding.SearchContact.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                true
            } else {
                false
            }
        }
//        kpBannerAd()

        /*  binding.conatctAppDownload.setOnClickListener {
              config.contact_app_open = true
              binding.conatctAppDownload.gone()
              val appPackageName: String = "com.contacts.phone.number.dialer.sms.service"
              try {
                  startActivity(
                      Intent(
                          Intent.ACTION_VIEW,
                          Uri.parse("market://details?id=$appPackageName")
                      )
                  )
              } catch (anfe: ActivityNotFoundException) {
                  startActivity(
                      Intent(
                          Intent.ACTION_VIEW,
                          Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                      )
                  )
              }
          }

          binding.contactInstall.setOnClickListener {
              binding.conatctAppDownload.performClick()
          }*/

        handlePermission(PERMISSION_READ_CONTACTS) {
            initContact()
        }

        binding.noContactsPlaceholder2.setOnClickListener {
            handlePermission(PERMISSION_READ_CONTACTS) {
                initContact()
            }
        }

        binding.keybordopen.setOnClickListener {
            val isNumberKeyboard =
                (binding.chipsInput.mChipsAdapter.mEditText.inputType and InputType.TYPE_CLASS_NUMBER) == InputType.TYPE_CLASS_NUMBER
            binding.keybordopen.setImageResource(R.drawable.open_keybord_number)
            if (isNumberKeyboard) {
                binding.chipsInput.mChipsAdapter.mEditText.inputType = InputType.TYPE_CLASS_TEXT
                binding.keybordopen.setImageResource(R.drawable.open_keybord_number)
            } else {
                binding.chipsInput.mChipsAdapter.mEditText.inputType = InputType.TYPE_CLASS_NUMBER
                binding.keybordopen.setImageResource(R.drawable.open_keybord_number_not)
            }
            binding.chipsInput.mChipsAdapter.mEditText.requestFocus()
            this.showKeyboard(binding.chipsInput.mChipsAdapter.mEditText)
        }

        binding.createGroup.setOnClickListener {
            if (adapter.iscredgrop) {
                adapter.iscredgrop = false
                adapter.notifyDataSetChanged()
                binding.createGroup.visible()
//                binding.threadAddContacts.gone()
                binding.confirmManageContacts.gone()
//                binding.searchBarCon.visible()

            } else {
                binding.addContactOrNumber.requestFocus()
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                adapter.iscredgrop = true
                binding.customornot = false
                adapter.notifyDataSetChanged()
                binding.createGroup.gone()
//                binding.threadAddContacts.visible()
                binding.confirmManageContacts.visible()
//                binding.searchBarCon.gone()
            }
        }

        binding.confirmManageContacts.setOnClickListener {


            if (config.All_Ads_On) {
                if (NetworkHelper.isOnline(this)) {
                    if (Alredyclick) {
                        return@setOnClickListener
                    }
                    Alredyclick = true

                    if (!BaseSharedPreferences(this@SelectContactActivity).mIS_SUBSCRIBED!!) {

                        /* val AdsListInter: ArrayList<AdConfigmodel> = arrayListOf(
                             AdConfigmodel(
                                 no = 1,
                                 Adsid = getString(R.string.mInterstitialCommon)
                             )
                         )*/

                        sendMessageAc()
                    } else {
                        sendMessageAc()
                    }
                } else {
                    sendMessageAc()
                }
            } else {
                sendMessageAc()
            }


        }

    }

    private fun sendMessageAc() {
        Alredyclick = false
        CoroutineScope(Dispatchers.IO).launch {
            hideKeyboard()
            val numbers = HashSet<String>()
            participants.forEach { contact ->
                contact.phoneNumbers.forEach {
                    numbers.add(it.normalizedNumber)
                }
            }
            val newThreadId = getThreadId(numbers)
            val participantsforgroupchat = getThreadParticipants(newThreadId!!, null)
            val addresses = participantsforgroupchat.getAddresses()
            val result = addresses.joinToString(separator = "|")
            if (addresses.isEmpty()) {
                val titlename: ArrayList<String> = arrayListOf()
                val numbername: ArrayList<String> = arrayListOf()
                var messagename = ""
                var messagenumber = ""
                participants.forEach {
                    titlename.add(it.name)
                    if (it.phoneNumbers.isNotEmpty()) {
                        numbername.add(it.phoneNumbers[0].normalizedNumber)
                    }

                }
                messagename = titlename.joinToString()
                messagenumber = numbername.joinToString("|")
                if (participants.size == 1) {
                    launchThreadActivity(messagenumber, messagename)
                } else {
                    Log.d(
                        "addresses",
                        "onCreate: addresses chack is gropmessage 7 <---> ${messagename}"
                    )
                    Log.d(
                        "addresses",
                        "onCreate: addresses chack is gropmessage 8 <---> ${messagenumber}"
                    )
                    hideKeyboard()
                    launchThreadForBrodcastActivity(newThreadId, messagename, messagenumber)
                }
            } else {
                if (addresses.size == 1) {
                    launchThreadActivity(result, participantsforgroupchat.getThreadTitle())
                } else {
                    hideKeyboard()
                    launchThreadForBrodcastActivity(
                        newThreadId,
                        participantsforgroupchat.getThreadTitle(),
                        result
                    )
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun initContact() {

        if (isThirdPartyIntent()) {
            return
        }

        with(binding) {

            if (!adapter.hasObservers()) {
                adapter.setHasStableIds(true)
            }
            contactadapter = adapter
            serchCleasr.setOnClickListener {
                SearchContact.text.clear()
            }

            imageView2.setOnClickListener {
                Constants.isActivitychange = true
                finish()

            }

            binding.SearchContact.onTextChangeListener { searchString ->
                scheduleFilter(searchString)
            }


            // chips listener
            binding.chipsInput.addChipsListener(object : ChipsInput.ChipsListener {
                override fun onChipAdded(chip: ChipInterface?, newSize: Int) {

                }

                override fun onChipRemoved(chip: ChipInterface?, newSize: Int) {
                    chip?.id?.toInt()?.let { removeSelectedContact(it) }
                }

                override fun onTextChanged(text: CharSequence) {
                    scheduleFilter(text.toString())
                }
            })

            binding.addContactOrNumber.onTextChangeListener { searchString ->
                binding.confirmInsertedNumber.beVisibleIf(searchString.length > 2)
                scheduleFilter(searchString)
            }


            contactsLetterFastscrollerThumb.setupWithFastScroller(contactsLetterFastscroller)
            contactsLetterFastscrollerThumb.textColor = getColor(R.color.white)
            contactsLetterFastscrollerThumb.thumbColor =
                ColorStateList.valueOf(getColor(R.color.appcolor))
            itemclick.setOnClickListener {
                val number = binding.lastmessageshow.text.toString()
                if (isShortCodeWithLetters(number)) {
                    toast(R.string.invalid_short_code, length = Toast.LENGTH_LONG)
                    return@setOnClickListener
                }
                val phoneNumber = PhoneNumber(number, 0, "", number)
                val contact = SimpleContact(
                    number.hashCode(),
                    number.hashCode(),
                    number,
                    "",
                    arrayListOf(phoneNumber),
                    ArrayList(),
                    ArrayList()
                )
                addSelectedContact(contact)
                addEditChip(
                    contact.contactId.toString(),
                    contact.photoUri.toUri(),
                    contact.name,
                    contact.phoneNumbers[0].normalizedNumber
                )
                selectedConList.add(contact)
                if (selectedConList.isEmpty()) {
                    binding.confirmManageContacts.gone()
                } else {
                    binding.confirmManageContacts.visible()
                }
            }

        }

        adapter.setContactclick(this)

        val uiScope = CoroutineScope(Dispatchers.Main)
        showContactsLoading()

        SimpleContactsHelper(this@SelectContactActivity).getAvailableContacts(false) { contacts ->
            uiScope.launch {
                contact = contacts
                hideContactsLoading()
                setAdapter(contacts)
                Handler(Looper.myLooper()!!).postDelayed({
                    setGropCall()
                }, 1000)
                if (contacts.isEmpty()) {
                    binding.contactsLetterFastscroller.invisible()
                } else {
                    binding.contactsLetterFastscroller.visible()
                }
            }
        }



        binding.confirmInsertedNumber.setOnClickListener {
            val number = binding.addContactOrNumber.value
            val phoneNumber = PhoneNumber(number, 0, "", number)
            val contact = SimpleContact(
                number.hashCode(),
                number.hashCode(),
                number,
                "",
                arrayListOf(phoneNumber),
                ArrayList(),
                ArrayList()
            )
            addSelectedContact(contact)
        }


    }

    fun setGropCall() {
        if (isgropset) {
            if (grouptread != -1L) {
                GlobalScope.launch(Dispatchers.IO) {
                    withContext(Dispatchers.IO) {
                        val participantsforgroupchat = getThreadParticipants(grouptread, null)
                        val addresses = participantsforgroupchat.getAddresses()
                        Log.d("", "initContact: grop <------> ${addresses}")
                        if (addresses.isNotEmpty()) {
                            addresses.forEach { s ->
                                val selectedContact: SimpleContact? = contact.find { contact1 ->
                                    contact1.phoneNumbers.any {
                                        deleteCountry(removeZero(it.normalizedNumber)) == deleteCountry(
                                            removeZero(s)
                                        )
                                    }
                                }
                                if (selectedContact != null) {
                                    selectedConList.add(selectedContact)
                                    runOnUiThread {
                                        addSelectedContact(selectedContact)
                                        addEditChip(
                                            selectedContact.contactId.toString(),
                                            selectedContact.photoUri.toUri(),
                                            selectedContact.name,
                                            selectedContact.phoneNumbers[0].normalizedNumber
                                        )
                                    }
                                }
                            }
                            Log.d("", "initContact: grop <------> 2 ${selectedConList}")

                            arrayList = convertStringToArrayList(mobilenumerofxiomi, "fornumber")
                            if (arrayList.size != selectedConList.size) {
                                arrayList.forEachIndexed { index, s ->
                                    val chack = selectedConList.any {
                                        it.phoneNumbers.any {
                                            deleteCountry(removeZero(it.normalizedNumber)) == deleteCountry(
                                                removeZero(s)
                                            )
                                        }
                                    }
                                    if (!chack) {
                                        val phoneNumber = PhoneNumber(s, 0, "", s)
                                        val contact = SimpleContact(
                                            s.hashCode(),
                                            s.hashCode(),
                                            s,
                                            "",
                                            arrayListOf(phoneNumber),
                                            ArrayList(),
                                            ArrayList()
                                        )
                                        selectedConList.add(contact)
                                        runOnUiThread {
                                            try {
                                                addSelectedContact(contact)
                                                addEditChip(
                                                    contact.contactId.toString(),
                                                    contact.photoUri.toUri(),
                                                    contact.name,
                                                    contact.phoneNumbers[0].normalizedNumber
                                                )
                                            } catch (_: Exception) {
                                            }
                                        }
                                    }
                                }
                                Log.d(
                                    "GroupDetailsActivity",
                                    "onCreate:GroupDetailsActivity <---> 20 arrayList ->"
                                )
                            }

                            if (selectedConList.isEmpty()) {
                                arrayList = convertStringToArrayList(nameofxiomi, "forname")
                                Log.d(
                                    "GroupDetailsActivity",
                                    "initContact: grop <------> 9 arrayList -> ${arrayList}"
                                )
                                arrayList.forEach { s ->
                                    val selectedContact: SimpleContact? = contact.find { contact1 ->
                                        contact1.name == s
                                    }
                                    Log.d(
                                        "GroupDetailsActivity",
                                        "initContact: grop <------> 10 arrayList -> ${selectedContact}"
                                    )
                                    if (selectedContact != null) {
                                        Log.d(
                                            "GroupDetailsActivity",
                                            "initContact: grop <------> 11 arrayList -> ${selectedContact}"
                                        )
                                        selectedConList.add(selectedContact)
                                        runOnUiThread {
                                            addSelectedContact(selectedContact)
                                            addEditChip(
                                                selectedContact.contactId.toString(),
                                                selectedContact.photoUri.toUri(),
                                                selectedContact.name,
                                                selectedContact.phoneNumbers[0].normalizedNumber
                                            )
                                        }
                                    }
                                }
                            } else {

                            }

                            if (selectedConList.isEmpty()) {
                                arrayList =
                                    convertStringToArrayList(mobilenumerofxiomi, "fornumber")
                                Log.d(
                                    "GroupDetailsActivity",
                                    "onCreate:GroupDetailsActivity <---> 13 arrayList -> ${arrayList}"
                                )
                                arrayList.forEach { s ->

                                    val phoneNumber = PhoneNumber(s, 0, "", s)
                                    val contact = SimpleContact(
                                        s.hashCode(),
                                        s.hashCode(),
                                        s,
                                        "",
                                        arrayListOf(phoneNumber),
                                        ArrayList(),
                                        ArrayList()
                                    )

                                    selectedConList.add(contact)
                                    runOnUiThread {
                                        addSelectedContact(contact)
                                        addEditChip(
                                            contact.contactId.toString(),
                                            contact.photoUri.toUri(),
                                            contact.name,
                                            contact.phoneNumbers[0].normalizedNumber
                                        )
                                    }
                                }
                            }

                        } else {
                            Log.d(
                                "GroupDetailsActivity",
                                "initContact: grop <------> 12 arrayList -> ${arrayList}"
                            )
                            arrayList = convertStringToArrayList(mobilenumerofxiomi, "fornumber")
                            Log.d(
                                "GroupDetailsActivity",
                                "initContact: grop <------> 13 arrayList -> ${arrayList}"
                            )

                            Log.d(
                                "GroupDetailsActivity",
                                "setGropCall: jigar 2 <---->  ${arrayList}"
                            )
                            Log.d("GroupDetailsActivity", "setGropCall: jigar 1 <---->  ${contact}")

                            arrayList.forEach { s ->
                                val selectedContact: SimpleContact? = contact.find { contact1 ->
                                    contact1.phoneNumbers.any {
                                        deleteCountry(removeZero(it.normalizedNumber)) == deleteCountry(
                                            removeZero(s)
                                        )
                                    }
                                }
                                Log.d(
                                    "GroupDetailsActivity",
                                    "setGropCall: jigar 3 <---->  ${selectedContact}"
                                )
                                if (selectedContact != null) {
                                    Log.d(
                                        "GroupDetailsActivity",
                                        "setGropCall: jigar 4 <---->  ${selectedContact}"
                                    )
                                    selectedConList.add(selectedContact)
                                    runOnUiThread {
                                        addSelectedContact(selectedContact)
                                        addEditChip(
                                            selectedContact.contactId.toString(),
                                            selectedContact.photoUri.toUri(),
                                            selectedContact.name,
                                            selectedContact.phoneNumbers[0].normalizedNumber
                                        )
                                    }
                                }
                            }



                            Log.d(
                                "GroupDetailsActivity",
                                "setGropCall: jigar 8 <---->  ${selectedConList}"
                            )
                            if (selectedConList.isEmpty()) {
                                arrayList = convertStringToArrayList(nameofxiomi, "forname")
                                Log.d(
                                    "GroupDetailsActivity",
                                    "setGropCall: jigar 9 <---->  ${arrayList}"
                                )
                                Log.d(
                                    "GroupDetailsActivity",
                                    "initContact: grop <------> 16 arrayList -> ${arrayList}"
                                )
                                arrayList.forEach { s ->
                                    val selectedContact: SimpleContact? = contact.find { contact1 ->
                                        contact1.name == s
                                    }
                                    Log.d(
                                        "GroupDetailsActivity",
                                        "setGropCall: jigar 10 <---->  ${selectedContact}"
                                    )
                                    if (selectedContact != null) {
                                        Log.d(
                                            "GroupDetailsActivity",
                                            "setGropCall: jigar 11 <---->  ${selectedContact}"
                                        )
                                        selectedConList.add(selectedContact)
                                        runOnUiThread {
                                            addSelectedContact(selectedContact)
                                            addEditChip(
                                                selectedContact.contactId.toString(),
                                                selectedContact.photoUri.toUri(),
                                                selectedContact.name,
                                                selectedContact.phoneNumbers[0].normalizedNumber
                                            )
                                        }
                                    }
                                }
                            } else {
                                Log.d("GroupDetailsActivity", "setGropCall: jigar 12 <---->")
                            }

                            if (selectedConList.isEmpty()) {
                                arrayList =
                                    convertStringToArrayList(mobilenumerofxiomi, "fornumber")
                                Log.d(
                                    "GroupDetailsActivity",
                                    "setGropCall: jigar 13 <---->  ${arrayList}"
                                )
                                arrayList.forEach { s ->

                                    val phoneNumber = PhoneNumber(s, 0, "", s)
                                    val contact = SimpleContact(
                                        s.hashCode(),
                                        s.hashCode(),
                                        s,
                                        "",
                                        arrayListOf(phoneNumber),
                                        ArrayList(),
                                        ArrayList()
                                    )

                                    selectedConList.add(contact)
                                    Log.d(
                                        "GroupDetailsActivity",
                                        "setGropCall: jigar 14 <---->  ${selectedConList}"
                                    )
                                    runOnUiThread {
                                        addSelectedContact(contact)
                                        addEditChip(
                                            contact.contactId.toString(),
                                            contact.photoUri.toUri(),
                                            contact.name,
                                            contact.phoneNumbers[0].normalizedNumber
                                        )
                                    }
                                }
                            }

                            arrayList = convertStringToArrayList(mobilenumerofxiomi, "fornumber")
                            Log.d(
                                "GroupDetailsActivity",
                                "setGropCall: jigar 5 <---->  ${arrayList}"
                            )
                            if (arrayList.size != selectedConList.size) {
                                Log.d(
                                    "GroupDetailsActivity",
                                    "setGropCall: jigar 6 <---->  ${arrayList}"
                                )
                                arrayList.forEachIndexed { index, s ->
                                    val chack = selectedConList.any {
                                        it.phoneNumbers.any {
                                            deleteCountry(removeZero(it.normalizedNumber)) == deleteCountry(
                                                removeZero(s)
                                            )
                                        }
                                    }
                                    if (!chack) {
                                        val phoneNumber = PhoneNumber(s, 0, "", s)
                                        val contact = SimpleContact(
                                            s.hashCode(),
                                            s.hashCode(),
                                            s,
                                            "",
                                            arrayListOf(phoneNumber),
                                            ArrayList(),
                                            ArrayList()
                                        )
                                        selectedConList.add(contact)
                                        Log.d(
                                            "GroupDetailsActivity",
                                            "setGropCall: jigar 7 <---->  ${selectedConList}"
                                        )
                                        runOnUiThread {
                                            try {
                                                addSelectedContact(contact)
                                                addEditChip(
                                                    contact.contactId.toString(),
                                                    contact.photoUri.toUri(),
                                                    contact.name,
                                                    contact.phoneNumbers[0].normalizedNumber
                                                )
                                            } catch (_: Exception) {

                                            }
                                        }
                                    }
                                }

                            }

                        }
                    }
                }
            }
        }
    }

    private fun showContactsLoading() {
        binding.progressbarcontact.visible()
        binding.recyclerView.gone()
        binding.noContactsPlaceholder.gone()
        binding.noContactsPlaceholder2.gone()
        binding.contactsLetterFastscroller.invisible()
    }

    private fun hideContactsLoading() {
        binding.progressbarcontact.gone()
    }

    private fun addSelectedContact(contact: SimpleContact) {
        Log.d("", "addSelectedContact:   sendMessage:<----> addresses <---> ${contact}")
        binding.addContactOrNumber.setText("")
        if (participants.map { it.rawId }.contains(contact.rawId)) {
            return
        }
        participants.add(contact)
//        showSelectedContacts()
    }



    private fun setAdapter(contacts: ArrayList<SimpleContact>) {
        val hasContacts = contacts.isNotEmpty()
        binding.apply {
            recyclerView.beVisibleIf(hasContacts)
            noContactsPlaceholder.beVisibleIf(!hasContacts)
            noContactsPlaceholder2.beVisibleIf(
                !hasContacts && !hasPermission(
                    PERMISSION_READ_CONTACTS
                )
            )

            if (!hasContacts) {
                val placeholderText =
                    if (hasPermission(PERMISSION_READ_CONTACTS)) R.string.no_contacts_found else R.string.no_access_to_contacts
                noContactsPlaceholder.text = getString(placeholderText)
            }

            adapter.list = contacts
        }
        setupLetterFastscroller(contacts)
    }

    @SuppressLint("SetTextI18n")
    private fun scheduleFilter(query: String) {
        lastFilterQuery = query
        filterJob?.cancel()
        filterJob = filterScope.launch {
            delay(150)
            val snapshotQuery = lastFilterQuery
            val contactsSnapshot = contact.toList()
            val filtered = withContext(Dispatchers.Default) {
                computeFilteredContacts(contactsSnapshot, snapshotQuery)
            }
            if (!isFinishing && !isDestroyed && snapshotQuery == lastFilterQuery) {
                applyFilterResult(snapshotQuery, filtered)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun applyFilterResult(query: String, filteredContacts: ArrayList<SimpleContact>) {
        if (query.isDigitsOnly() || query.startsWith("+", true)) {
            binding.customornot = query.isNotEmpty()
            val name: String =
                getColoredSpanned(
                    resources.getString(R.string.Send_to) + " (",
                    toHtmlColor(getProperTextColor())
                )
            val surName: String = getColoredSpanned(query, toHtmlColor(getProperPrimaryColor()))
            binding.number.setText(Html.fromHtml(name + "" + surName + ")"))
            binding.lastmessageshow.text = query
        }
        Number = query
        adapter.list = filteredContacts
        if (filteredContacts.isEmpty()) {
            binding.contactsLetterFastscroller.invisible()
        } else {
            binding.contactsLetterFastscroller.visible()
        }
        setupLetterFastscroller(filteredContacts)
    }

    private fun computeFilteredContacts(
        contacts: List<SimpleContact>,
        query: String
    ): ArrayList<SimpleContact> {
        if (query.isBlank()) {
            return ArrayList(contacts)
        }
        val trimmedQuery = query.trim().replace("\\s".toRegex(), "")
        val normalizedQuery = trimmedQuery.normalizeString()
        val filteredContacts = ArrayList<SimpleContact>()
        contacts.forEach { contact ->
            val nameCompact = contact.name.trim().replace("\\s".toRegex(), "")
            val nameNormalized = nameCompact.normalizeString()
            val match = contact.phoneNumbers.any {
                it.normalizedNumber.trim().replace("\\s".toRegex(), "")
                    .contains(trimmedQuery, true)
            } ||
                nameCompact.contains(trimmedQuery, true) ||
                nameCompact.contains(normalizedQuery, true) ||
                nameNormalized.contains(trimmedQuery, true)

            if (match) {
                filteredContacts.add(contact)
            }
        }
        filteredContacts.sortWith(compareBy { !it.name.startsWith(query, true) })
        return filteredContacts
    }

    override fun onClick(mobilenumber: ArrayList<SimpleContact>, pos: Int, name: String) {
//        val contactChip = ContactChip(mobilenumber[pos].contactId.toString(), mobilenumber[pos].photoUri.toUri(), name, mobilenumber[pos].)
//        binding.chipsInput.addChip()
        Log.e("Contact", "onClick :init", )

        if (true) {
            Log.e("Contact", "onClick :true ", )

//            managePeople()
//            if (chackPermission()) {
//
//            }


            val contact = mobilenumber[pos]
            val phoneNumbers = contact.phoneNumbers
            Log.e("Contact", "onClick :phoneNumbers.size  ${phoneNumbers.size}    $phoneNumbers", )
            if (phoneNumbers.size > 1) {
                val primaryNumber = contact.phoneNumbers.find { it.isPrimary }
                Log.e("Contact", "onClick :primaryNumber  ${primaryNumber}  "  )

//                if (primaryNumber != null) {
//                    launchThreadActivity(primaryNumber.value, contact.name)
//                } else {
                    val items = java.util.ArrayList<RadioItem>()
                    phoneNumbers.forEachIndexed { index, phoneNumber ->
                        val type = getPhoneNumberTypeText(phoneNumber.type, phoneNumber.label)
                        items.add(
                            RadioItem(
                                index,
                                "${phoneNumber.normalizedNumber} ($type)",
                                phoneNumber.normalizedNumber
                            )
                        )
                    }
                    RadioGroupDialog(this, items) { di ->
                        val selectedContact: SimpleContact? = mobilenumber.find { contact ->
                            contact.phoneNumbers.any { it.normalizedNumber == di as String }
                        }
                        if (selectedContact != null) {
                            addEditChip(
                                selectedContact.contactId.toString(),
                                selectedContact.photoUri.toUri(),
                                selectedContact.name,
                                di as String
                            )
                            addSelectedContact(selectedContact)
                            if (selectedConList.contains(mobilenumber[pos])) {
                                selectedConList.remove(mobilenumber[pos])
                                //1
                                removeSelectedContact(selectedContact.contactId)
                                adapter.notifyDataSetChanged()
                            } else {
                                mobilenumber[pos].let {
                                    selectedConList.add(it)
                                    adapter.notifyDataSetChanged()
                                }
                            }

                            if (selectedConList.isEmpty()) {
                                binding.confirmManageContacts.gone()
                            } else {
                                binding.confirmManageContacts.visible()
                            }

                        }
                    }
//                }
            }
            else {
                val selectedContact = mobilenumber[pos]

                addSelectedContact(selectedContact)
                Log.d("", "onClick:addSelectedContact <-----> 1 ${mobilenumber[pos]}")
                if (selectedConList.contains(mobilenumber[pos])) {
                    selectedConList.remove(mobilenumber[pos])
                    removeSelectedContact(selectedContact.contactId)
                    Log.d("", "onClick:addSelectedContact <-----> 2 ${mobilenumber[pos]}")
                    adapter.notifyDataSetChanged()
                } else {
                    Log.d("", "onClick:addSelectedContact <-----> 3 ${mobilenumber[pos]}")
                    addEditChip(
                        selectedContact.contactId.toString(),
                        selectedContact.photoUri.toUri(),
                        selectedContact.name,
                        selectedContact.phoneNumbers[0].normalizedNumber
                    )
                    mobilenumber[pos].let {
                        selectedConList.add(it)
                        adapter.notifyDataSetChanged()
                    }
                }

                if (selectedConList.isEmpty()) {
                    binding.confirmManageContacts.gone()
                } else {
                    binding.confirmManageContacts.visible()
                }
            }


        } else {
            Log.e("Contact", "onClick :false ", )

            Constants.isActivitychange = true
            if (chackPermission()) {
                val contact = mobilenumber[pos]
                val phoneNumbers = contact.phoneNumbers
                if (phoneNumbers.size > 1) {
                    val primaryNumber = contact.phoneNumbers.find { it.isPrimary }
                    if (primaryNumber != null) {
                        launchThreadActivity(primaryNumber.value, contact.name)
                    } else {
                        val items = java.util.ArrayList<RadioItem>()
                        phoneNumbers.forEachIndexed { index, phoneNumber ->
                            val type = getPhoneNumberTypeText(phoneNumber.type, phoneNumber.label)
                            items.add(
                                RadioItem(
                                    index,
                                    "${phoneNumber.normalizedNumber} ($type)",
                                    phoneNumber.normalizedNumber
                                )
                            )
                        }
                        RadioGroupDialog(this, items) {
                            launchThreadActivity(it as String, contact.name)
                        }
                    }
                } else {
                    launchThreadActivity(phoneNumbers.first().normalizedNumber, contact.name)
                }

            }
        }
    }

    private fun managePeople() {
        showSelectedContacts()
//        binding.threadAddContacts.beVisible()
        binding.addContactOrNumber.requestFocus()
//        showKeyboard(binding.addContactOrNumber)
    }

    private fun launchThreadActivity(mobilenumber: String, name: String) {

        val text =
            intent.getStringExtra(Intent.EXTRA_TEXT) ?: intent.getStringExtra("sms_body") ?: ""

        if (!forscheduleMessage) {
            Log.d("", "launchThreadActivity: for sendmessagte to exit 6 ")
            if (isforvard) {
                Log.d("", "launchThreadActivity: for sendmessagte to exit 7 ")
                Log.d(
                    "",
                    "getsetIntent: mobile number 1111 1:----> ${message} <-------> ${isforvard} <-------> ${name}"
                )
                val intent = Intent(
                    this@SelectContactActivity, if (config.Message_Send_Activity == "1") {
                        SendMessageActivity::class.java
                    } else {
                        SendMessageActivity::class.java
                    }
                )
                    .putExtra("tredid", getThreadId(mobilenumber))
                    .putExtra("message", message)
                    .putExtra("name", name)
                    .putExtra("isforvard", isforvard)
                    .putExtra("mobileNumber", mobilenumber)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                safeStartActivity(intent, finishDelayMs = 100)
                Log.d("", "launchThreadActivity: for sendmessagte to exit 2 ")
            } else if (text.isNotEmpty()) {
                Log.d("", "launchThreadActivity: for sendmessagte to exit 3 ")
                val intent = Intent(
                    this@SelectContactActivity, if (config.Message_Send_Activity == "1") {
                        SendMessageActivity::class.java
                    } else {
                        SendMessageActivity::class.java
                    }
                )
                    .putExtra("tredid", getThreadId(mobilenumber))
                    .putExtra("message", text)
                    .putExtra(
                        "name", if (name.isEmpty()) {
                            mobilenumber
                        } else {
                            name
                        }
                    )
                    .putExtra("isforvard", true)
                    .putExtra("mobileNumber", mobilenumber)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                safeStartActivity(intent, finishDelayMs = 100)
            } else {
                isThirdPartyIntentCheck = isThirdPartyIntent(intent)
                Log.d(
                    "",
                    "launchThreadActivity: for sendmessagte to exit 333333 ${isThirdPartyIntentCheck}"
                )
                val name2 = getContactNameFromNumber(this@SelectContactActivity, mobilenumber)
                val intent = Intent(
                    this@SelectContactActivity, if (config.Message_Send_Activity == "1") {
                        SendMessageActivity::class.java
                    } else {
                        SendMessageActivity::class.java
                    }
                )
                    .putExtra("tredid", getThreadId(mobilenumber))
                    .putExtra(
                        "name", if (name2.isEmpty()) {
                            mobilenumber
                        } else {
                            name2
                        }
                    )
                    .putExtra("mobileNumber", mobilenumber)
                safeStartActivity(intent)
                Log.d("", "launchThreadActivity: for sendmessagte to exit 4 ")
            }
        } else {
            Log.d("", "launchThreadActivity: for sendmessagte to exit 1 ")
            val name2 = getContactNameFromNumber(this@SelectContactActivity, mobilenumber)
            val intent = Intent(
                this@SelectContactActivity, if (config.Message_Send_Activity == "1") {
                    SendMessageActivity::class.java
                } else {
                    SendMessageActivity::class.java
                }
            )
                .putExtra("tredid", getThreadId(mobilenumber))
                .putExtra(
                    "name", if (name2.isEmpty()) {
                        mobilenumber
                    } else {
                        name2
                    }
                )
                .putExtra("mobileNumber", mobilenumber)
                .putExtra("forscheduleMessage", true)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            safeStartActivity(intent, finishDelayMs = 100)
        }
    }

    private fun launchThreadForBrodcastActivity(
        newThreadId: Long,
        threadTitle: String,
        result: String,
    ) {

        val text =
            intent.getStringExtra(Intent.EXTRA_TEXT) ?: intent.getStringExtra("sms_body") ?: ""

        if (!forscheduleMessage) {
            if (isforvard) {
                val intent = if (config.Message_Send_Activity == "1") {
                    Intent(this, SendMessageActivity::class.java)
                } else {
                    Intent(this, SendMessageActivity::class.java)
                }

                intent.apply {
                    putExtra("tredid", newThreadId)
                    putExtra("message", message)
                    putExtra("isgroupmessage", true)
                    putExtra("isforvard", isforvard)
                    threadTitle?.let { putExtra("name", it) } // Add threadTitle if not null
                    result?.let { putExtra("mobileNumber", it) } // Add result if not null
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }

                safeStartActivity(intent, finishNow = true)
            } else if (text.isNotEmpty()) {

                val intent = Intent(
                    this, if (config.Message_Send_Activity == "1") {
                        SendMessageActivity::class.java
                    } else {
                        SendMessageActivity::class.java
                    }
                ).apply {
                    putExtra("tredid", newThreadId)
                    putExtra("message", text)
                    putExtra("isgroupmessage", true)
                    putExtra("name", threadTitle)
                    putExtra("mobileNumber", result)
                    putExtra("isforvard", true)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
                safeStartActivity(intent, finishNow = true)

            } else {
                val intent = Intent(
                    this, if (config.Message_Send_Activity == "1") {
                        SendMessageActivity::class.java
                    } else {
                        SendMessageActivity::class.java
                    }
                ).apply {
                    putExtra("tredid", newThreadId)
                    putExtra("isgroupmessage", true)
                    putExtra("name", threadTitle)
                    putExtra("mobileNumber", result)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
                safeStartActivity(intent, finishNow = true)
            }
        } else {
            val intent = Intent(
                this, if (config.Message_Send_Activity == "1") {
                    SendMessageActivity::class.java
                } else {
                    SendMessageActivity::class.java
                }
            ).apply {
                putExtra("tredid", newThreadId)
                putExtra("isgroupmessage", true)
                putExtra("name", threadTitle)
                putExtra("mobileNumber", result)
                putExtra("forscheduleMessage", true)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            safeStartActivity(intent, finishNow = true)
        }
    }

    private fun safeStartActivity(
        intent: Intent,
        finishDelayMs: Long? = null,
        finishNow: Boolean = false
    ) {
        if (isFinishing || isDestroyed) return
        val action = action@{
            if (isFinishing || isDestroyed) return@action
            startActivity(intent)
            when {
                finishNow -> finish()
                finishDelayMs != null -> Handler(Looper.getMainLooper()).postDelayed({
                    if (!isFinishing && !isDestroyed) finish()
                }, finishDelayMs)
            }
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action()
        } else {
            runOnUiThread { action() }
        }
    }

    override fun OnLongClick() {

    }


    fun chackPermission(): Boolean {
        return PermissionChecker.checkSelfPermission(
            this,
            android.Manifest.permission.SEND_SMS
        ) == PermissionChecker.PERMISSION_GRANTED
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Constants.isActivitychange = true
    }


    private fun setupLetterFastscroller(contacts: ArrayList<SimpleContact>) {
        binding.contactsLetterFastscroller.setupWithRecyclerView(binding.recyclerView, { position ->
            try {
                val name = contacts[position].name
                val character = if (name.isNotEmpty()) name.substring(0, 1) else ""
                FastScrollItemIndicator.Text(
                    character.uppercase(Locale.getDefault()).normalizeString()
                )
            } catch (e: Exception) {
                FastScrollItemIndicator.Text("")
            }
        })

    }

    private fun isThirdPartyIntent(): Boolean {
        if ((intent.action == Intent.ACTION_SENDTO || intent.action == Intent.ACTION_SEND || intent.action == Intent.ACTION_VIEW) && intent.dataString != null) {
            val number =
                intent.dataString!!.removePrefix("sms:").removePrefix("smsto:").removePrefix("mms")
                    .removePrefix("mmsto:").replace("+", "%2b").trim()
            launchThreadActivity(URLDecoder.decode(number), "")
            finish()
            return true
        }
        return false
    }

    fun handlePermission(permissionId: Int, callback: (granted: Boolean) -> Unit) {
        actionOnPermission = null
        if (hasPermission(permissionId)) {
            callback(true)
        } else {
            isAskingPermissions = true
            actionOnPermission = callback
            ActivityCompat.requestPermissions(
                this,
                arrayOf(getPermissionString(permissionId)),
                GENERIC_PERM_HANDLER
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        isAskingPermissions = false
        if (requestCode == GENERIC_PERM_HANDLER && grantResults.isNotEmpty()) {
            actionOnPermission?.invoke(grantResults[0] == 0)
        }
    }

    private fun getColoredSpanned(text: String, color: String): String {
        return "<font color=$color>$text</font>"
    }

    @SuppressLint("Range")
    fun getContactNameFromNumber(context: Context, phoneNumber: String): String {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_DENIED
            && PermissionChecker.checkSelfPermission(
                context,
                Manifest.permission.WRITE_CONTACTS
            ) == PermissionChecker.PERMISSION_GRANTED

        ) {
            var contactName = ""
            try {
                val uri: Uri = Uri.withAppendedPath(
                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                    Uri.encode(phoneNumber)
                )
                val contentResolver: ContentResolver = context.contentResolver
                val cursor: Cursor? = contentResolver.query(
                    uri,
                    arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME),
                    null,
                    null,
                    null
                )

                cursor?.use {
                    if (it.moveToFirst()) {
                        contactName =
                            it.getString(it.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME))
                    }
                }

                cursor?.close()
            } catch (E: Exception) {

            }
            return contactName.ifEmpty {
                phoneNumber
            }
        } else {
            return ""
        }

    }

    private fun showSelectedContact(views: ArrayList<View>) {
        binding.selectedContacts.removeAllViews()
        var newLinearLayout = LinearLayout(this)
        newLinearLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        newLinearLayout.orientation = LinearLayout.HORIZONTAL

        val sideMargin =
            (binding.selectedContacts.layoutParams as RelativeLayout.LayoutParams).leftMargin
        val mediumMargin = resources.getDimension(R.dimen.medium_margin).toInt()
        val parentWidth = realScreenSize.x - sideMargin * 2
        val firstRowWidth =
            parentWidth - resources.getDimension(R.dimen.normal_icon_size).toInt() + sideMargin / 2
        var widthSoFar = 0
        var isFirstRow = true

        for (i in views.indices) {
            val layout = LinearLayout(this)
            layout.orientation = LinearLayout.HORIZONTAL
            layout.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
            layout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            views[i].measure(0, 0)

            var params = LinearLayout.LayoutParams(
                views[i].measuredWidth,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, mediumMargin, 0)
            layout.addView(views[i], params)
            layout.measure(0, 0)
            widthSoFar += views[i].measuredWidth + mediumMargin

            val checkWidth = if (isFirstRow) firstRowWidth else parentWidth
            if (widthSoFar >= checkWidth) {
                isFirstRow = false
                binding.selectedContacts.addView(newLinearLayout)
                newLinearLayout = LinearLayout(this)
                newLinearLayout.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                newLinearLayout.orientation = LinearLayout.HORIZONTAL
                params = LinearLayout.LayoutParams(layout.measuredWidth, layout.measuredHeight)
                params.topMargin = mediumMargin
                newLinearLayout.addView(layout, params)
                widthSoFar = layout.measuredWidth
            } else {
                if (!isFirstRow) {
                    (layout.layoutParams as LinearLayout.LayoutParams).topMargin = mediumMargin
                }
                newLinearLayout.addView(layout)
            }
        }
        binding.selectedContacts.addView(newLinearLayout)
    }



    @SuppressLint("UseCompatLoadingForDrawables", "InflateParams")
    private fun showSelectedContacts() {
        val properPrimaryColor = resources.getColor(R.color.appcolor, theme)
        val views = ArrayList<View>()
        binding.selectedContactsSc.post {
            binding.selectedContactsSc.fullScroll(ScrollView.FOCUS_DOWN)
        }
        if (participants.isEmpty()) {
            val scrollView = binding.selectedContactsSc
            val maxScrollViewHeight = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._1sdp)

            val layoutParams = scrollView.layoutParams
            layoutParams.height = maxScrollViewHeight
            scrollView.layoutParams = layoutParams
        } else {
            val scrollView = binding.selectedContactsSc
            val maxScrollViewHeight = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._50sdp)

            val layoutParams = scrollView.layoutParams
            layoutParams.height = maxScrollViewHeight
            scrollView.layoutParams = layoutParams
        }
        participants.forEach { contact ->
            val view = layoutInflater.inflate(R.layout.item_selected_contact, null)
            val selected_contact_holder =
                view.findViewById<RelativeLayout>(R.id.selected_contact_holder)
            val selected_contact_name = view.findViewById <com.google.android.material.textview.MaterialTextView>(R.id.selected_contact_name)
            val selected_contact_remove = view.findViewById<ImageView>(R.id.selected_contact_remove)
            val selectedContactBg =
                resources.getDrawable(R.drawable.item_selected_contact_background)
            (selectedContactBg as LayerDrawable).findDrawableByLayerId(R.id.selected_contact_bg)
            selected_contact_holder.background = selectedContactBg

            selected_contact_name.text = contact.name
//            selected_contact_name.setTextColor(properPrimaryColor.getContrastColor())
//            selected_contact_remove.applyColorFilter(properPrimaryColor.getContrastColor())

            selected_contact_remove.setOnClickListener {
                val selectedContact: SimpleContact? = selectedConList.find { contact1 ->
                    contact1.rawId == contact.rawId
                }
                if (selectedContact != null) {
                    if (selectedConList.contains(selectedContact)) {
                        selectedConList.remove(selectedContact)
                        adapter.notifyDataSetChanged()
                    }
                }
                removeSelectedContact(contact.contactId)
            }
            views.add(view)

        }
        showSelectedContact(views)
    }

    private fun removeSelectedContact(id: Int) {
        Log.d("", "removeSelectedContact: <----> ${id}")
        Log.d("", "onClick:addSelectedContact <-----> 13 ${id}")
        Log.d("", "onClick:addSelectedContact <-----> 13333333 ${participants}")
        Log.d("", "onClick:addSelectedContact <-----> 13333333455555555 ${selectedConList}")
        participants.toList().forEach { contact ->
            Log.d("", "onClick:addSelectedContact <-----> 4 ${contact}")
            val selectedContact: SimpleContact? = selectedConList.find { contact1 ->
                contact1.contactId == id
            }
            Log.d("", "onClick:addSelectedContact <-----> 1333 ${selectedContact}")
            if (selectedContact != null) {
                Log.d("", "onClick:addSelectedContact <-----> 5 ${selectedContact}")
                if (selectedConList.contains(selectedContact)) {
                    Log.d("", "onClick:addSelectedContact <-----> 6 ${selectedContact}")
                    selectedConList.remove(selectedContact)
                    adapter.notifyDataSetChanged()
                }
            }
        }
        Log.d("", "onClick:addSelectedContact <-----> 11 ${participants}")
        participants = participants.toList().filter { it.contactId != id }
            .toMutableList() as ArrayList<SimpleContact>
        val chipdata = binding.chipsInput.selectedChipList
        Log.d("", "onClick:addSelectedContact <-----> 7 ${chipdata.toList().toString()}")
        Log.d("", "onClick:addSelectedContact <-----> 10 ${participants}")
        chipdata.toList().forEachIndexed { index, chipInterface ->
            Log.d(
                "",
                "onClick:addSelectedContact <-----> 12 ${chipInterface.info} ${chipInterface.id} ${chipInterface.avatarUri} ${chipInterface.label}"
            )
            Log.d(
                "",
                "onClick:addSelectedContact <-----> 8 ${
                    chipInterface.id.toInt().equals(id)
                } <--> chipInterface.id = ${chipInterface.id} <--> id = ${id}"
            )
            Log.d(
                "",
                "removeSelectedContact: <----> 3 ${
                    chipInterface.id.toInt().equals(id)
                } <--> chipInterface.id = ${chipInterface.id} <--> id = ${id}"
            )
            if (chipInterface.id.toInt() == id) {
                Log.d("", "onClick:addSelectedContact <-----> 9 ${chipInterface}")
                Log.d("", "removeSelectedContact: <----> 2 ${chipInterface}")
                binding.chipsInput.removeChipByPos(index)
            }
        }

        if (selectedConList.isEmpty()) {
            binding.confirmManageContacts.gone()
        } else {
            binding.confirmManageContacts.visible()
        }

//        showSelectedContacts()
//        if (participants.isEmpty()) {
//            adapter.iscredgrop = false
//            adapter.notifyDataSetChanged()
//            binding.createGroup.visible()
//            binding.threadAddContacts.gone()
//            binding.confirmManageContacts.gone()
////            binding.searchBarCon.visible()
//
//            val scrollView = binding.selectedContactsSc
//            val maxScrollViewHeight = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._1sdp)
//
//            val layoutParams = scrollView.layoutParams
//            layoutParams.height = maxScrollViewHeight
//            scrollView.layoutParams = layoutParams
//
//
//        } else {
//            adapter.iscredgrop = true
//            adapter.notifyDataSetChanged()
//            binding.createGroup.gone()
////            binding.threadAddContacts.visible()
//            binding.confirmManageContacts.visible()
////            binding.searchBarCon.gone()
//
//            val scrollView = binding.selectedContactsSc
//            val maxScrollViewHeight = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._50sdp)
//
//            val layoutParams = scrollView.layoutParams
//            layoutParams.height = maxScrollViewHeight
//            scrollView.layoutParams = layoutParams
//        }
    }


    fun addEditChip(contactid: String, iconuri: Uri, name: String, s2: String) {
        Log.d("contactid", "addEditChip: contactid <----------> 222")
        val contactChip = ContactChip(contactid, iconuri, name, s2)
        mContactList.add(name)
        CoroutineScope(Dispatchers.Main).launch {
            binding.chipsInput.addChip(contactChip)
        }

    }


    fun convertStringToArrayList(inputString: String, s: String): ArrayList<String> {
        if (s == "forname") {
            val stringArray = inputString.split(",").toTypedArray()
            val arrayList = ArrayList<String>()

            for (str in stringArray) {
                arrayList.add(str.trim())
            }

            return arrayList
        } else {
            val stringArray = inputString.split("|").toTypedArray()
            val arrayList = ArrayList<String>()

            for (str in stringArray) {
                arrayList.add(str.trim())
            }

            return arrayList
        }

    }

    fun removeZero(number: String): String {
        return if (number.startsWith('0')) {
            number.substring(1)
        } else {
            number
        }
    }

    fun deleteCountry(phone: String): String {
        val phoneInstance = PhoneNumberUtil.getInstance()
        try {
            val phoneNumber = phoneInstance.parse(phone, null)
            return phoneNumber?.nationalNumber?.toString() ?: phone
        } catch (_: Exception) {
        }
        return phone
    }

    override fun onStart() {
        super.onStart()
        ThemeSetup()

        binding.confirmManageContacts.backgroundTintList = ColorStateList.valueOf(
            getProperPrimaryColor()
        )
    }

    override fun onDestroy() {
        filterJob?.cancel()
        filterScope.cancel()
        super.onDestroy()
    }

    private fun ThemeSetup() {
        setCustomBackground()
        applyMaterialSystemBarColors()
    }

    private fun setCustomBackground() {
        val primaryColor = getProperPrimaryColor()
        val backgroundColor = getProperBackgroundColor()
        val statusBarColor = getProperStatusBarColor()
        val textColor = getProperTextColor()
        val dilogColor = getDialogBackgroundColor()
        val iconColor = withAlpha(textColor, 0.84f)
        val hintColor = withAlpha(textColor, 0.60f)

        val topContainerFill = blendWithBaseColor(primaryColor, backgroundColor, 0.14f)
        val mediumContainerFill = blendWithBaseColor(primaryColor, backgroundColor, 0.10f)
        val lowContainerFill = blendWithBaseColor(primaryColor, backgroundColor, 0.06f)
        val mediumContainerStroke = blendWithBaseColor(primaryColor, backgroundColor, 0.42f)
        val chipBackground = primaryColor.adjustAlpha(0.55f)
        val chipDetailedBackground = blendWithBaseColor(primaryColor, backgroundColor, 0.20f)
        val chipTextColor = textColor
        val chipDeleteColor = withAlpha(textColor, 0.80f)

        binding.vAnd15StatusBar.setBackgroundColor(statusBarColor)


        binding.clMain.setBackgroundColor(backgroundColor)
        binding.mainlayout.setBackgroundColor(backgroundColor)
        binding.recyclerView.setBackgroundColor(backgroundColor)


        binding.searchBarCon.background = createRoundedBackground(
            fillColor = topContainerFill,
            strokeColor = topContainerFill,
            cornerRadiusResId = com.intuit.sdp.R.dimen._30sdp
        )
        binding.threadAddContacts.background = createRoundedBackground(
            fillColor = topContainerFill,
            strokeColor = mediumContainerStroke,
            cornerRadiusResId = com.intuit.sdp.R.dimen._30sdp
        )
        binding.contactsLetterFastscroller.background = createRoundedBackground(
            fillColor = mediumContainerFill,
            strokeColor = mediumContainerFill,
            cornerRadiusResId = com.intuit.sdp.R.dimen._20sdp
        )
        binding.createGroup.background = createRoundedBackground(
            fillColor = mediumContainerFill,
            strokeColor = mediumContainerStroke,
            cornerRadiusResId = com.intuit.sdp.R.dimen._35sdp
        )

        binding.imageView20.backgroundTintList = ColorStateList.valueOf(chipDetailedBackground)
        binding.noContactsPlaceholder2.backgroundTintList = ColorStateList.valueOf(mediumContainerFill)
        binding.confirmManageContacts.backgroundTintList = ColorStateList.valueOf(primaryColor)

        binding.SearchContact.setTextColor(textColor)
        binding.SearchContact.setHintTextColor(hintColor)
        binding.addContactOrNumber.setTextColor(textColor)
        binding.addContactOrNumber.setHintTextColor(hintColor)
        binding.number.setTextColor(textColor)
        binding.lastmessageshow.setTextColor(withAlpha(textColor, 0.80f))
        binding.txtSelected.setTextColor(textColor)
        binding.CreateGroup.setTextColor(textColor)
        binding.noContactsPlaceholder.setTextColor(withAlpha(textColor, 0.72f))
        binding.nomessagefound.setTextColor(withAlpha(textColor, 0.58f))

        binding.chipsInput.mChipsAdapter.mEditText.setTextColor(textColor)
        binding.chipsInput.mChipsAdapter.mEditText.setHintTextColor(hintColor)
        binding.chipsInput.setChipLabelColor(ColorStateList.valueOf(chipTextColor))
        binding.chipsInput.setChipBackgroundColor(ColorStateList.valueOf(chipBackground))
        binding.chipsInput.setChipDeleteIconColor(ColorStateList.valueOf(chipDeleteColor))
        binding.chipsInput.setChipDetailedTextColor(ColorStateList.valueOf(chipTextColor))
        binding.chipsInput.setChipDetailedBackgroundColor(ColorStateList.valueOf(chipDetailedBackground))
        binding.chipsInput.setChipDetailedDeleteIconColor(ColorStateList.valueOf(chipDeleteColor))
        binding.chipsInput.mChipsAdapter.notifyDataSetChanged()

        binding.contactsLetterFastscroller.textColor = ColorStateList.valueOf(withAlpha(textColor, 0.70f))
        binding.contactsLetterFastscrollerThumb.textColor = primaryColor.getContrastColor()
        binding.contactsLetterFastscrollerThumb.thumbColor = ColorStateList.valueOf(primaryColor)

        binding.imageView2.setColorFilter(iconColor)
        binding.serchCleasr.setColorFilter(iconColor)
        binding.keybordopen.setColorFilter(iconColor)
        binding.confirmInsertedNumber.setColorFilter(iconColor)
        binding.createGroupVector.setColorFilter(iconColor)
    }

    private fun applyMaterialSystemBarColors() {
        val surfaceColor = getProperBackgroundColor()

        window.statusBarColor = surfaceColor
        window.navigationBarColor = surfaceColor
        window.decorView.setBackgroundColor(surfaceColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }

        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightSystemBars = ThemeModeManager.shouldUseLightSystemBars(this@SelectContactActivity)
            isAppearanceLightStatusBars = useLightSystemBars
            isAppearanceLightNavigationBars = useLightSystemBars
        }
    }

    private fun createRoundedBackground(
        fillColor: Int,
        strokeColor: Int,
        cornerRadiusResId: Int,
    ): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = resources.getDimension(cornerRadiusResId)
            setColor(fillColor)
            setStroke(resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._1sdp), strokeColor)
        }
    }

    private fun blendWithBaseColor(primaryColor: Int, baseColor: Int, ratio: Float): Int {
        return ColorUtils.blendARGB(baseColor, primaryColor, ratio.coerceIn(0f, 1f))
    }

    private fun withAlpha(color: Int, alpha: Float): Int {
        return ColorUtils.setAlphaComponent(color, (255 * alpha.coerceIn(0f, 1f)).toInt())
    }

    private fun toHtmlColor(color: Int): String {
        return String.format(Locale.US, "#%06X", 0xFFFFFF and color)
    }

    fun isThirdPartyIntent(intent: Intent): Boolean {
        if ((intent.action == Intent.ACTION_SENDTO || intent.action == Intent.ACTION_SEND || intent.action == Intent.ACTION_VIEW) && intent.dataString != null) {
            val number =
                intent.dataString!!.removePrefix("sms:").removePrefix("smsto:").removePrefix("mms")
                    .removePrefix("mmsto:").replace("+", "%2b").trim()
            return true
        }
        return false
    }




/*
    private fun kpBannerAd() {
        if (config.All_Ads_On) {
            if (BaseSharedPreferences(this).mIS_SUBSCRIBED || !isOnline()) {
                binding.constAdViewBottom.gone()
            return
        }
        preloadBannerForPlacement(
                "contact_banner", this, ArrayList(listOfContactBanner),
                config.isSearchBannerAdOn, false
            )
            showMultiUnitBannerAdvance(
                this,
                "contact_banner",
                binding.frameAdBottom,
                binding.bannerShimmerBottom,
                binding.constAdViewBottom,
                binding.shimmerContainer, config.isSearchBannerAdOn
            )
        } else {
            binding.constAdViewBottom.gone()
        }
    }*/
}
