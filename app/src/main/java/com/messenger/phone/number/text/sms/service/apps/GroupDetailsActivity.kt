package com.messenger.phone.number.text.sms.service.apps

import android.app.ProgressDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createOptionBackground
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getBottomNavigationBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getAddresses
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getColoredGroupIconNew
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getContrastColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperSecondaryTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperStatusBarColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getThreadParticipants
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.Dialog.RenameBrodcastDialog
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
import com.messenger.phone.number.text.sms.service.apps.adapter.GroupMemberAdapter
import com.messenger.phone.number.text.sms.service.apps.data.PhoneNumber
import com.messenger.phone.number.text.sms.service.apps.data.SimpleContact
import com.messenger.phone.number.text.sms.service.apps.data.SimpleContactsHelper
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityGroupDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class GroupDetailsActivity : AppCompatActivity() {


    lateinit var binding: ActivityGroupDetailsBinding



    private var arrayList: ArrayList<String> = arrayListOf()
    var mobilenumber: String = ""
    var senderName: String = ""
    var thredid: Long = -1L
    var name: String = ""
    var isArchive = false
    var isPrivateChat = false
    var isUserNotificationShow = true
    var isnumberBlock = false
    var isgroupmessage = false
    var contact = ArrayList<SimpleContact>()
    var selectedConListNew = ArrayList<SimpleContact>()
    private lateinit var mProgressDialog: ProgressDialog


    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    @Inject
    lateinit var groupMemberAdapter: GroupMemberAdapter

    @Inject
    lateinit var renameBrodcastDialog: RenameBrodcastDialog

    @Inject
    lateinit var messagerDatabaseRepo: MessagerDatabaseRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocal()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_group_details)
        setBaseTheme(binding.vAnd15StatusBar)
        name = intent.getStringExtra("uesrname") ?: ""
        senderName = intent.getStringExtra("sendername") ?: ""
        thredid = intent.getLongExtra("thredid", 0L)
        mobilenumber = intent.getStringExtra("mobilenumber") ?: ""
        isgroupmessage = intent.getBooleanExtra("isgroupmessage", false)

        mProgressDialog = ProgressDialog(this, R.style.Dialog_Custom)
        mProgressDialog.setCancelable(false)
        mProgressDialog.setMessage(resources.getString(R.string.Loading))

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

        Log.d("GroupDetailsActivity", "onCreate:GroupDetailsActivity <---> 1 name -> ${name}")
        Log.d(
            "GroupDetailsActivity",
            "onCreate:GroupDetailsActivity <---> 2 senderName -> ${senderName}"
        )
        Log.d("GroupDetailsActivity", "onCreate:GroupDetailsActivity <---> 3 thredid -> ${thredid}")
        Log.d(
            "GroupDetailsActivity",
            "onCreate:GroupDetailsActivity <---> 4 mobilenumber -> ${mobilenumber}"
        )
        Log.d(
            "GroupDetailsActivity",
            "onCreate:GroupDetailsActivity <---> 5 isgroupmessage -> ${isgroupmessage}"
        )

        with(binding) {
            binding.adapter = groupMemberAdapter

            CoroutineScope(Dispatchers.IO).launch {
                val list = thredid.let { messagerDatabaseRepo.getUserMessageListrepo(it) }
                val nameNew = if (list.isEmpty() == true) {
                    name
                } else {
                    list!![0].groupName
                }
                runOnUiThread { binding.gropName.text = nameNew }
            }
//            binding.contacticon.setImageDrawable(SimpleContactsHelper(this@GroupDetailsActivity).getColoredGroupIcon(name))
//            kpSmallNative()

            binding.contacticon.setImageDrawable(
                getColoredGroupIconNew(
                    name.toString(), this@GroupDetailsActivity
                )
            )


            backBtn.setOnClickListener {
                finish()
            }

            messageEdit.setOnClickListener {
                renameBrodcastDialog.catname = binding.gropName.text.toString()
                renameBrodcastDialog.tredid = thredid.toString()
                renameBrodcastDialog.show(supportFragmentManager, "renameBrodcastDialog")
            }

            addMember.setOnClickListener {
                startActivity(
                    Intent(
                        this@GroupDetailsActivity,
                        SelectContactActivity::class.java
                    ).putExtra("isgropset", true).putExtra("grouptread", thredid)
                        .putExtra("mobilenumerofxiomi", mobilenumber).putExtra("nameofxiomi", name)
                )
            }
        }
        val uiScope = CoroutineScope(Dispatchers.Main)
        SimpleContactsHelper(this@GroupDetailsActivity).getAvailableContacts(false) { contacts ->
            uiScope.launch {
                if (!canUseWindowToken()) {
                    return@launch
                }
                contact = contacts
                Log.d("contact", "onCreate: contactcontact <-----------> ${contacts}")
                setGropCall()
            }
        }

        renameBrodcastDialog.dialogdismiss = {
            binding.gropName.text = it
        }


    }

    private fun canUseWindowToken(): Boolean {
        return !isFinishing && !isDestroyed
    }

    fun setGropCall() {
        if (!canUseWindowToken()) {
            return
        }

        try {
            if (!mProgressDialog.isShowing) {
                mProgressDialog.show()
            }
        } catch (_: android.view.WindowManager.BadTokenException) {
            return
        } catch (_: IllegalStateException) {
            return
        }

        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                val participantsforgroupchat = getThreadParticipants(thredid, null)
                val addresses = participantsforgroupchat.getAddresses()
                if (addresses.isNotEmpty()) {
                    Log.d(
                        "GroupDetailsActivity",
                        "onCreate:GroupDetailsActivity <---> 6 arrayList -> ${addresses}"
                    )
                    Log.d(
                        "GroupDetailsActivity",
                        "onCreate:GroupDetailsActivity <---> 66 arrayList -> ${participantsforgroupchat}"
                    )

                    addresses.forEach { s ->
                        val selectedContact: SimpleContact? = contact.find { contact1 ->
                            contact1.phoneNumbers.any {
                                deleteCountry(removeZero(it.normalizedNumber)) == deleteCountry(
                                    removeZero(s)
                                )
                            }
                        }
                        Log.d(
                            "GroupDetailsActivity",
                            "onCreate:GroupDetailsActivity <---> 7 arrayList -> ${selectedContact}"
                        )
                        if (selectedContact != null) {
                            Log.d(
                                "GroupDetailsActivity",
                                "onCreate:GroupDetailsActivity <---> 8 arrayList -> ${selectedContact}"
                            )
                            selectedConListNew.add(selectedContact)
                        }
                    }



                    if (selectedConListNew.isEmpty()) {
                        arrayList = convertStringToArrayList(name, "forname")
                        Log.d(
                            "GroupDetailsActivity",
                            "onCreate:GroupDetailsActivity <---> 9 arrayList -> ${arrayList}"
                        )
                        arrayList.forEach { s ->
                            val selectedContact: SimpleContact? = contact.find { contact1 ->
                                contact1.name == s
                            }
                            Log.d(
                                "GroupDetailsActivity",
                                "onCreate:GroupDetailsActivity <---> 10 arrayList -> ${selectedContact}"
                            )
                            if (selectedContact != null) {
                                Log.d(
                                    "GroupDetailsActivity",
                                    "onCreate:GroupDetailsActivity <---> 11 arrayList -> ${selectedContact}"
                                )
                                selectedConListNew.add(selectedContact)
                            }
                        }
                    } else {

                    }

                    if (selectedConListNew.isEmpty()) {
                        arrayList = convertStringToArrayList(mobilenumber, "fornumber")
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
                            selectedConListNew.add(contact)
                        }
                    }

                    arrayList = convertStringToArrayList(mobilenumber, "fornumber")
                    if (arrayList.size != selectedConListNew.size) {
                        arrayList.forEachIndexed { index, s ->
                            val chack = selectedConListNew.any {
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
                                selectedConListNew.add(contact)
                            }
                        }
                        Log.d(
                            "GroupDetailsActivity",
                            "onCreate:GroupDetailsActivity <---> 20 arrayList ->"
                        )
                    }


                } else {
                    Log.d(
                        "GroupDetailsActivity",
                        "onCreate:GroupDetailsActivity <---> 12 arrayList -> ${arrayList}"
                    )
                    Log.d(
                        "GroupDetailsActivity",
                        "onCreate:GroupDetailsActivity <---> 666 arrayList -> ${contact}"
                    )
                    arrayList = convertStringToArrayList(mobilenumber, "fornumber")
                    Log.d(
                        "GroupDetailsActivity",
                        "onCreate:GroupDetailsActivity <---> 13 arrayList -> ${arrayList}"
                    )
                    arrayList.forEach { s ->
                        val selectedContact: SimpleContact? = contact.find { contact1 ->
                            contact1.phoneNumbers.any {
                                Log.d(
                                    "GroupDetailsActivity",
                                    "onCreate:GroupDetailsActivity <---> 6666 arrayList -> ${
                                        deleteCountry(removeZero(it.normalizedNumber))
                                    }"
                                )
                                Log.d(
                                    "GroupDetailsActivity",
                                    "onCreate:GroupDetailsActivity <---> 66666 arrayList -> ${
                                        deleteCountry(removeZero(s))
                                    }"
                                )
                                deleteCountry(removeZero(it.normalizedNumber)) == deleteCountry(
                                    removeZero(s)
                                )
                            }
                        }
                        Log.d(
                            "GroupDetailsActivity",
                            "onCreate:GroupDetailsActivity <---> 14 arrayList -> ${selectedContact}"
                        )
                        if (selectedContact != null) {
                            Log.d(
                                "GroupDetailsActivity",
                                "onCreate:GroupDetailsActivity <---> 15 arrayList -> ${selectedContact}"
                            )
                            selectedConListNew.add(selectedContact)
                        }
                    }



                    if (selectedConListNew.isEmpty()) {
                        arrayList = convertStringToArrayList(name, "forname")
                        Log.d(
                            "GroupDetailsActivity",
                            "onCreate:GroupDetailsActivity <---> 16 arrayList -> ${arrayList}"
                        )
                        arrayList.forEach { s ->
                            val selectedContact: SimpleContact? = contact.find { contact1 ->
                                contact1.name == s
                            }
                            Log.d(
                                "GroupDetailsActivity",
                                "onCreate:GroupDetailsActivity <---> 17 arrayList -> ${selectedContact}"
                            )
                            if (selectedContact != null) {
                                Log.d(
                                    "GroupDetailsActivity",
                                    "onCreate:GroupDetailsActivity <---> 18 arrayList -> ${selectedContact}"
                                )
                                selectedConListNew.add(selectedContact)
                            }
                        }
                    } else {

                    }

                    if (selectedConListNew.isEmpty()) {
                        arrayList = convertStringToArrayList(mobilenumber, "fornumber")
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
                            selectedConListNew.add(contact)
                        }
                    }

                    arrayList = convertStringToArrayList(mobilenumber, "fornumber")
                    if (arrayList.size != selectedConListNew.size) {
                        arrayList.forEachIndexed { index, s ->
                            val chack = selectedConListNew.any {
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
                                selectedConListNew.add(contact)
                            }
                        }
                        Log.d(
                            "GroupDetailsActivity",
                            "onCreate:GroupDetailsActivity <---> 20 arrayList ->"
                        )
                    }

                }
            }

            withContext(Dispatchers.Main) {
                if (!canUseWindowToken()) {
                    return@withContext
                }
                Log.d(
                    "GroupDetailsActivity",
                    "onCreate:GroupDetailsActivity <---> 19 arrayList -> ${selectedConListNew.size}"
                )
                binding.parCount.text =
                    "${selectedConListNew.size} " + resources.getString(R.string.People)
                groupMemberAdapter.Datlist = selectedConListNew
                if (canUseWindowToken() && mProgressDialog.isShowing) {
                    mProgressDialog.dismiss()
                }
            }
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
    }

    private fun ThemeSetup() {
        val surfaceColor = getProperBackgroundColor()
        val statusBarColor = getProperStatusBarColor()
        val navigationBarColor = getBottomNavigationBackgroundColor()
        val textColor = getProperTextColor()
        val secondaryTextColor = getProperSecondaryTextColor()
        val primaryColor = getProperPrimaryColor()

        binding.root.setBackgroundColor(surfaceColor)
        binding.vAnd15StatusBar.setBackgroundColor(statusBarColor)
        binding.defaulttoolshow.setBackgroundColor(surfaceColor)
        binding.constSmallNativeAdView.setBackgroundColor(surfaceColor)
        binding.participantsMobileNumberlist.setBackgroundColor(surfaceColor)

        binding.textView3.setTextColor(textColor)
        binding.textView21.setTextColor(secondaryTextColor)
        binding.gropName.setTextColor(textColor)
        binding.parCount.setTextColor(textColor)
        binding.backBtn.iconTint = ColorStateList.valueOf(secondaryTextColor)
        binding.messageEdit.imageTintList = ColorStateList.valueOf(primaryColor)

//        binding.participantsMobileNumber.background = createOptionBackground(
//            cornerSize = resources.getDimension(com.intuit.sdp.R.dimen._10sdp),
//            fillColor = surfaceColor,
//            strokeWidth = resources.getDimension(com.intuit.sdp.R.dimen._2sdp),
//            strokeColor = primaryColor.adjustAlpha(0.22f),
//            rippleColor = primaryColor.adjustAlpha(0.2f),
//            showRipple = false
//        )

        binding.addMember.background = createOptionBackground(
            cornerSize = resources.getDimension(com.intuit.sdp.R.dimen._10sdp),
            fillColor = primaryColor,
            strokeWidth = 0f,
            rippleColor = Color.WHITE.adjustAlpha(0.2f),
            showRipple = true
        )

        binding.addMemberIcon.imageTintList = ColorStateList.valueOf(primaryColor.getContrastColor())
        binding.addMemberLabel.setTextColor(primaryColor.getContrastColor())

        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        window.decorView.setBackgroundColor(surfaceColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightSystemBars = ThemeModeManager.shouldUseLightSystemBars(this@GroupDetailsActivity)
            isAppearanceLightStatusBars = useLightSystemBars
            isAppearanceLightNavigationBars = useLightSystemBars
        }
    }



}
