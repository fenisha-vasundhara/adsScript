package com.messenger.phone.number.text.sms.service.apps

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.provider.Telephony
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.demo.adsmanage.Commen.Constants
import com.demo.adsmanage.Commen.firebaseEventMain
import com.demo.adsmanage.Commen.log
import com.demo.adsmanage.basemodule.BaseSharedPreferences
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.MaterialColors
import com.messenger.phone.number.text.sms.service.apps.CommanClass.CONTACT_INSERT_EDIT_REQUEST_CODE
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.CommanClass.buildNotificationChannelId
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.containsAlphabets
import com.messenger.phone.number.text.sms.service.apps.CommanClass.copyToClipboard
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createNotificationChannel
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createOptionBackground
import com.messenger.phone.number.text.sms.service.apps.CommanClass.dialNumber
import com.messenger.phone.number.text.sms.service.apps.CommanClass.drawableCache
import com.messenger.phone.number.text.sms.service.apps.CommanClass.fetchContactIdFromPhoneNumber
import com.messenger.phone.number.text.sms.service.apps.CommanClass.generateRandomId
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getBottomNavigationBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getNameAndPhotoFromPhoneNumber
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
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.handleDefaultSmsClick_1
import com.messenger.phone.number.text.sms.service.apps.CommanClass.retrieveContactName
import com.messenger.phone.number.text.sms.service.apps.CommanClass.sendsubactivity
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.Dialog.CommanDeleteBlockDialog
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
import com.messenger.phone.number.text.sms.service.apps.ShowCase.DismissType
import com.messenger.phone.number.text.sms.service.apps.ShowCase.GuideView
import com.messenger.phone.number.text.sms.service.apps.ShowCase.PointerType
import com.messenger.phone.number.text.sms.service.apps.adapter.ParticipantsmobileAdaper
import com.messenger.phone.number.text.sms.service.apps.ads.v2.AdUiBinding
import com.messenger.phone.number.text.sms.service.apps.ads.v2.AdsOrchestrator
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityViewDetailsBinding
import com.messenger.phone.number.text.sms.service.apps.helper.RandomDrawableProvider
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.CommanDeleteBlockDialogInterface
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.modelClass.Participantsmobilemodel
import com.messenger.phone.number.text.sms.service.apps.sms.SmsUtils.deleteConversation
import com.simplemobiletools.commons.extensions.addBlockedNumber
import com.simplemobiletools.commons.extensions.deleteBlockedNumber
import com.simplemobiletools.commons.extensions.toast
import com.simplemobiletools.commons.helpers.KEY_PHONE
import com.simplemobiletools.commons.helpers.SimpleContactsHelper
import com.simplemobiletools.commons.helpers.ensureBackgroundThread
import com.simplemobiletools.commons.models.PhoneNumber
import com.simplemobiletools.commons.models.SimpleContact
import com.messenger.phone.number.text.sms.service.apps.realmplan.ConversationDataSourceProvider
import com.messenger.phone.number.text.sms.service.apps.realmplan.RealmFeatureFlag
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import io.github.xilinjia.krdb.Realm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale.getDefault
import javax.inject.Inject


@AndroidEntryPoint
class ViewDetailsActivity : AppCompatActivity(), CommanDeleteBlockDialogInterface {

    lateinit var binding: ActivityViewDetailsBinding


    private var chacksaveandedit: Boolean = false

//    private val viewModelBanner by viewModels<ViewModelBanner>()

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    var mobilenumber: String = ""
    var senderName: String = ""
    var thredid: Long = -1L
    var name: String = ""
    var isArchive = false
    var isPrivateChat = false
    var isUserNotificationShow = true
    var isnumberBlock = false
    var isgroupmessage = false
    private var builder: GuideView.Builder? = null
    private var mGuideView: GuideView? = null
    private var viewDetailsAdRequested = false
    private val adsOrchestrator by lazy { AdsOrchestrator.get(application) }

    var list: ArrayList<Conversation> = arrayListOf()
    private var commanDeleteBlockDialog: CommanDeleteBlockDialog? = null

    private var participants: ArrayList<SimpleContact> = arrayListOf()

    @Inject
    lateinit var repo: MessagerDatabaseRepo

    @Inject
    lateinit var realm: Lazy<Realm>

    @Inject
    lateinit var participantsmobileAdaper: ParticipantsmobileAdaper

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeModeManager.applyThemeMode(this)
        if (ThemeModeManager.getThemeMode(this) == ThemeModeManager.MODE_SYSTEM) {
            DynamicColors.applyToActivityIfAvailable(this)
        }
        super.onCreate(savedInstanceState)
        setLocal()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_details)
        this.firebaseEventMain("View_Details")
        setBaseTheme(binding.vAnd15StatusBar)
//        applyStatusBarInsets()

        binding.username.isSelected = true
        binding.mobilenumbertxt.isSelected = true
        binding.txtnumberblockornot.isSelected = true
        binding.txtButtonArchive.isSelected = true

        fontSize10 = getTextSizeForeNormal10MS()
        fontSize13 = getTextSizeForeNormal13MS()
        fontSize18 = getTextSizeForeNormal18MS()
        fontSize8 = getTextSizeForeNormal8MS()
        fontSize15 = getTextSizeHometitleMS()

        binding.textsizechagefor10 = fontSize10
        binding.textsizechagefor13 = fontSize13 * 1.2f
        binding.textsizechagefor18 = fontSize18
        binding.textsizechagefor8 = fontSize8
        binding.textsizechagefor15 = fontSize15
//        loadNative()
        listAllNotificationChannels(this)

        name = intent.getStringExtra("uesrname") ?: ""
        senderName = intent.getStringExtra("sendername") ?: ""
        thredid = intent.getLongExtra("thredid", 0L)
        mobilenumber = intent.getStringExtra("mobilenumber") ?: ""

//        if (config.SelectedLanguage == "ar") {
////            textView9.gravity = Gravity.END
//            binding.txtButtonAdd.gravity = Gravity.END
//
//        } else {
////            textView9.gravity = Gravity.START
//            binding.txtButtonAdd.gravity = Gravity.START
//        }

        binding.customNotification.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (thredid != 0L) {
                    createNotificationChannel(thredid, mobilenumber)
                }
                val channelId = buildNotificationChannelId(thredid)
                val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_CHANNEL_ID, channelId)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                startActivity(intent)
            }


//            var i = Intent();
//            i.setType("image/*");
//            i.setAction(Intent.ACTION_GET_CONTENT);
//
//            // pass the constant to compare it
//            // with the returned requestCode
//            startActivityForResult(Intent.createChooser(i, "Select Picture"), 2);


//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                if (thredid != 0L) {
//                    createNotificationChannel(thredid)
//                }
//
//                val channelId = buildNotificationChannelId(thredid)
//                val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
//                    .putExtra(Settings.EXTRA_CHANNEL_ID, channelId)
//                    .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
//                startActivity(intent)
//            }


//            startActivity(
//                Intent(this, Custom_Notification_Activity::class.java)
//                    .putExtra("thredid", thredid)
//            )
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                var NOTIFICATION_NAME = mobilenumber
//                NOTIFICATION_CHANNEL = "messenger_sms_own" + mobilenumber
//                val notificationManager =
//                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//                val existingChannel =
//                    notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL)
//                if (existingChannel == null) {
//                    val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//                    val audioAttributes = AudioAttributes.Builder()
//                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
//                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                        .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
//                        .build()
//
//                    val id = NOTIFICATION_CHANNEL
//                    val importance = IMPORTANCE_HIGH
//                    NotificationChannel(id, NOTIFICATION_NAME, importance).apply {
//                        setBypassDnd(false)
//                        enableLights(true)
//                        setSound(soundUri, audioAttributes)
//                        enableVibration(true)
//                        try {
//                            notificationManager.createNotificationChannel(this)
//                        } catch (e: Exception) {}
//                    }
//
//                    val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
//                        putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
//                        putExtra(Settings.EXTRA_CHANNEL_ID, NOTIFICATION_CHANNEL)
//                    }
//                    startActivity(intent)
//
//                } else {
//                    val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
//                        putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
//                        putExtra(Settings.EXTRA_CHANNEL_ID, NOTIFICATION_CHANNEL)
//                    }
//                    startActivity(intent)
//                }
//
//            }


        }



        CoroutineScope(Dispatchers.IO).launch {
            chacksaveandedit = containsAlphabets(name)
            Log.d(
                "",
                "onCreate:chacksaveandedit <-----------> ${chacksaveandedit} <---> ${mobilenumber}"
            )
            runOnUiThread {
                if (chacksaveandedit) {
                    binding.txtButtonAdd.text = resources.getString(R.string.Edit)
                    binding.imageView6.setImageResource(R.drawable.baseline_edit_24)
                } else {
                    binding.txtButtonAdd.text = resources.getString(R.string.Add_to_contact_new_ui)
                    binding.imageView6.setImageResource(R.drawable.baseline_person_add_alt_24)
                }


                if (binding.txtButtonAdd.text.equals(resources.getString(R.string.Add_to_contact_new_ui))){
                    binding.mobilenumbertxt.text = binding.username.text

                }else{
                    binding. mobilenumbertxt.text = mobilenumber

                }

            }
        }

        isgroupmessage = intent.getBooleanExtra("isgroupmessage", false)

        participantsmobileAdaper.setHasStableIds(true)
        binding.adapter = participantsmobileAdaper


        val phoneNumber = mobilenumber.let { PhoneNumber(it, 0, "", it) }
        val contact = name.let {
            SimpleContact(
                0,
                0,
                it,
                "",
                arrayListOf(phoneNumber!!),
                ArrayList(),
                ArrayList()
            )
        }
        if (contact.phoneNumbers.isNotEmpty()) {
            if (contact.phoneNumbers[0].value.isNotEmpty()) {
                if (isgroupmessage) {
                    val number = contact.phoneNumbers[0].value
                    val numbersArray: LongArray = if (number.contains("|")) {
                        try {
                            number.split("|").map { it.toLong() }.toLongArray()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            longArrayOf() // Empty LongArray in case of error
                        }
                    } else {
                        try {
                            longArrayOf(number.toLong())
                        } catch (e: Exception) {
                            e.printStackTrace()
                            longArrayOf() // Empty LongArray in case of error
                        }
                    }

                    if (numbersArray.isNotEmpty()) {
                        for (numberget in numbersArray) {
                            val phoneNumbergrop =
                                numberget.toString().let { PhoneNumber(it, 0, "", it) }
                            val contactgrop = fetchContactIdFromPhoneNumber(
                                phoneNumbergrop.normalizedNumber,
                                this
                            ).let {
                                SimpleContact(
                                    0,
                                    0,
                                    it,
                                    "",
                                    arrayListOf(phoneNumbergrop),
                                    ArrayList(),
                                    ArrayList()
                                )
                            }
                            participants.add(contactgrop)
                        }
                    }

                } else {
                    participants.add(contact)
                }
            }
        }

        Log.d("participants", "onCreate: participants <------------> ${participants}")

        val realmInstance = if (RealmFeatureFlag(this).useRealmReads) {
            runCatching { realm.get() }.getOrNull()
        } else {
            null
        }
        ConversationDataSourceProvider.get(this, repo, realmInstance).observeMessages(thredid!!).observe(this, Observer {
            list = ArrayList(it.distinctBy { it.threadId })
        })

//        if (name?.isNotEmpty() == true) {
//            if (name?.first()?.isDigit() == true) {
//                binding.contacticon.setImageBitmap(mobilenumber?.let { binding.contacticon.context.getContactLetterIcon(name, it) })
//            } else {
//                val drawable = drawableCache[thredid.toString()]
//                if (drawable != null) {
//                    binding.contacticon.setImageDrawable(drawable)
//                } else {
//                    val randomDrawable = RandomDrawableProvider(binding.contacticon.context).getRandomDrawable()
//                    binding.contacticon.setImageDrawable(randomDrawable)
//                    drawableCache[thredid.toString()] = randomDrawable
//                }
//            }
//        }

        CoroutineScope(Dispatchers.IO).launch {
            val profile = mobilenumber?.let { getNameAndPhotoFromPhoneNumber(it) }
            if (profile?.photoUri?.isNotEmpty() == true) {
                CoroutineScope(Dispatchers.Main).launch {
                    SimpleContactsHelper(this@ViewDetailsActivity)
                        .loadContactImage(profile.photoUri, binding.contacticon, name!!, null)
                }
            } else {
                val drawable = drawableCache[thredid.toString()]
                if (drawable != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.contacticon.setImageDrawable(drawable)
                    }

                } else {
                    val randomDrawable =
                        RandomDrawableProvider(binding.contacticon.context).getRandomDrawable(
                            thredid!!
                        )
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.contacticon.setImageDrawable(randomDrawable)
                    }
                    drawableCache[thredid.toString()] = randomDrawable
                }
            }
        }

        GlobalScope.launch {

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val chack = repo.isarchivornotRepo(thredid)
                    val block = repo.isnumberblockornotRepo(thredid)
                    isPrivateChat = repo.isPrivacyChatExitsRepo(thredid)
                    isUserNotificationShow = if (repo.isNewUserMessageExitsRepo(thredid)) {
                        repo.isUserNotificationshowRepo(thredid)
                    } else {
                        true
                    }
                    if (block == 0) {
                        isnumberBlock = false
                    } else if (block == 1) {
                        isnumberBlock = true
                    }

                    if (chack == 0) {
                        isArchive = false
                    } else if (chack == 1) {
                        isArchive = true
                    }
                } catch (e: Exception) {
                }
            }
            CoroutineScope(Dispatchers.Main).launch {
                binding.notificationGetOrNot.isChecked = isUserNotificationShow
                if (isArchive) {
//                    binding.archiveornot.text = resources.getString(R.string.UnArchive)
                    binding.txtButtonArchive.text = resources.getString(R.string.UnArchive)
                } else {
//                    binding.archiveornot.text = resources.getString(R.string.Archive)
                    binding.txtButtonArchive.text = resources.getString(R.string.Archive)
                }

                if (isnumberBlock) {
                    binding.txtnumberblockornot.text =
                        resources.getString(R.string.UnBlock) + " ${binding.username.text.toString()}"
//                    binding.txtButtonArchive.text =
//                        resources.getString(R.string.UnBlock)
                } else {
                    binding.txtnumberblockornot.text =
                        resources.getString(R.string.Block) + " ${binding.username.text.toString()}"
//                    binding.txtButtonArchive.text =
//                        resources.getString(R.string.Block)
                }

                if (!isPrivateChat) {
                    binding.archiveornotPrivateChat.text =
                        resources.getString(R.string.Move_to_Private)
                } else {
                    binding.archiveornotPrivateChat.text =
                        resources.getString(R.string.Remove_to_Private)
                }
            }

        }

        with(binding) {

            username.text = name
            if (txtButtonAdd.text.equals(resources.getString(R.string.Add_to_contact_new_ui))){
                mobilenumbertxt.text = username.text

            }else{
                mobilenumbertxt.text = mobilenumber

            }
            if (name.length >= 2) {
                numberfisttxt.text = firstTwo(name)
            } else {
                numberfisttxt.text = "JI"
            }
            notreplychack = if (mobilenumber == "") {
                false
            } else {
                mobilenumber.first().isLetter()
            }

            isLetterorNot = if (name.isNotEmpty()) {
                name[0].isLetter()
            } else {
                false
            }

            numberCopyBtn.setOnClickListener {
                if (config.usershowviewdetailsshowcase) {
                    return@setOnClickListener
                }
                copyToClipboard(mobilenumber)
                toast(resources.getString(R.string.Copied_to_Clipboard))
            }

            callBtn.setOnClickListener {
//                if (config.usershowviewdetailsshowcase) {
//                    return@setOnClickListener
//                }
                dialNumber()
            }

            smsBtn.setOnClickListener {
//                if (config.usershowviewdetailsshowcase) {
//                    return@setOnClickListener
//                }
//                Constants.isActivitychange = true
//                finish()


                CoroutineScope(Dispatchers.IO).launch {
                    if (isArchive) {
                        isArchive = false
                        config.removePinnedConversations(list)
                        repo.removearchivConversationRepo(arrayListOf(thredid.toString()))
                        runOnUiThread {
//                            binding.archiveornot.text = resources.getString(R.string.Archive)
                            binding.txtButtonArchive.text = resources.getString(R.string.Archive)
                        }
                    } else {
                        isArchive = true
                        repo.archivConversationRepo(arrayListOf(thredid.toString()))
                        runOnUiThread {
//                            binding.archiveornot.text = resources.getString(R.string.UnArchive)
                            binding.txtButtonArchive.text = resources.getString(R.string.UnArchive)
                        }
                        repo.removePrivacyConversationRepo(arrayListOf(thredid.toString()))
                        runOnUiThread {
                            binding.archiveornotPrivateChat.text =
                                resources.getString(R.string.Move_to_Private)
                        }
                    }
                }

//                if (Telephony.Sms.getDefaultSmsPackage(this@ViewDetailsActivity) == packageName) {
//                    if (isnumberBlock) {
//                        removeblocknumber()
//                    } else {
//                        blockNumber()
//                    }
//                } else {
//                    toast(resources.getString(R.string.permission_required))
//                }
                ThemeSetup()

            }
            contacticon.setOnClickListener {
                numberCopyBtn.performClick()
            }
            username.setOnClickListener {
                numberCopyBtn.performClick()
            }
            backBtn.setOnClickListener {
                Constants.isActivitychange = true
                finish()
            }
            wpBtn1.setOnClickListener {


                openWhatsApp(this@ViewDetailsActivity,mobilenumber)
            }
            showNotification.setOnClickListener {
//                val contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, mobilenumber)
//                val intent = Intent(Intent.ACTION_VIEW, contactUri)
//                startActivity(intent)
            }

            notificationGetOrNot.setOnCheckedChangeListener { _, isChecked ->

//                if (config.usershowviewdetailsshowcase) {
//                    binding.notificationGetOrNot.isChecked = isUserNotificationShow
//                    return@setOnCheckedChangeListener
//                }

                CoroutineScope(Dispatchers.IO).launch {
                    if (isChecked) {
                        repo.removenotshownotificationRepo(arrayListOf(thredid.toString()))
                    } else {
                        repo.addnotshownotificationRepo(arrayListOf(thredid.toString()))
                    }
                }
            }

            deleteBtn.setOnClickListener {


                if (chacksaveandedit) {
                    CoroutineScope(Dispatchers.IO).launch {
                        editContactByPhoneNumber(mobilenumber)
                    }
                } else {
                    addtocontact()
                }

            }

            contactArchive.setOnClickListener {
                if (packageName == Telephony.Sms.getDefaultSmsPackage(this@ViewDetailsActivity)) {

                showcommandialog(
                    dialogtital = resources.getString(R.string.Delete_this_conversation),
                    dialogmessage = getString(R.string.do_you_really_want_to_clear_this_conversation),
                    positivebutton = getString(R.string.clear_con),
                    negativebutton = resources.getString(R.string.cancel),
                    "clear conversation"
                )

                }else{
                    handleDefaultSmsClick_1(this@ViewDetailsActivity)
                }
            }

            contactPrivateChat.setOnClickListener {


                if (config.Lock_Screen_Pin == "Not Set") {
                    startActivity(
                        Intent(
                            this@ViewDetailsActivity,
                            LockScreenSetupActivity::class.java
                        ).putExtra("comefrom", 1)
                    )
                    toastMess(resources.getString(R.string.Please_set_first_private))
                } else {
                    GlobalScope.launch {
                        withContext(Dispatchers.IO) {
                            isPrivateChat = if (!isPrivateChat) {


                                if (!BaseSharedPreferences(this@ViewDetailsActivity).mIS_SUBSCRIBED!!) {
                                    if (repo.getallconversationunPrivacyOrnotListrepo()
                                            .distinctBy { it.threadId }.size >= 3
                                    ) {
                                        sendsubactivity()
                                        false
                                    } else {
                                        config.removePinnedConversations(list)
                                        repo.PrivacyConversationRepo(arrayListOf(thredid.toString()))
                                        repo.removearchivConversationRepo(arrayListOf(thredid.toString()))
                                        runOnUiThread {
//                                            binding.archiveornot.text =
//                                                resources.getString(R.string.Archive)
                                            binding.txtButtonArchive.text =
                                                resources.getString(R.string.Archive)
                                        }
                                        true
                                    }
                                } else {
                                    config.removePinnedConversations(list)
                                    repo.PrivacyConversationRepo(arrayListOf(thredid.toString()))
                                    repo.removearchivConversationRepo(arrayListOf(thredid.toString()))
                                    runOnUiThread {
//                                        binding.archiveornot.text =
//                                            resources.getString(R.string.Archive)
                                        binding.txtButtonArchive.text =
                                            resources.getString(R.string.Archive)
                                    }
                                    true
                                }
                            } else {
                                repo.removePrivacyConversationRepo(arrayListOf(thredid.toString()))
                                false
                            }

                        }
                        withContext(Dispatchers.Main) {
                            if (!isPrivateChat) {
                                binding.archiveornotPrivateChat.text =
                                    resources.getString(R.string.Move_to_Private)
                            } else {
                                binding.archiveornotPrivateChat.text =
                                    resources.getString(R.string.Remove_to_Private)
                            }
                        }
                    }
                }
                ThemeSetup()
            }

            blockNumberBtn.setOnClickListener {


//                if (config.usershowviewdetailsshowcase) {
//                    return@setOnClickListener
//                }

                if (Telephony.Sms.getDefaultSmsPackage(this@ViewDetailsActivity) == packageName) {
                    if (isnumberBlock) {
                        removeblocknumber()
                    } else {
                        blockNumber()
                    }
                } else {
                    toast(resources.getString(R.string.permission_required))
                }
            }

        }

        if (chackPermission()) {
            val contactName = getContactNameByPhoneNumber(this, mobilenumber)
            val phoneNumbers = contactName?.let { getContactPhoneNumbersByName(this, it) }
            val list: ArrayList<String> = arrayListOf()

            GlobalScope.launch {

                withContext(Dispatchers.IO) {
                    if (phoneNumbers != null) {
                        for (phoneNumber in phoneNumbers) {
                            list.add(phoneNumber.rawPhoneNumber)
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    participantsmobileAdaper.listdata = list
                    if (list.isEmpty()) {
                        binding.participantsMobileNumber.gone()
                    } else {
                        binding.participantsMobileNumber.visible()
                    }

                }

            }
        } else {
            binding.participantsMobileNumber.gone()
        }

        if (config.usershowviewdetailsshowcase) {
//            showCaseStart()
        }

    }
    fun isAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun openWhatsApp(context: Context, number: String) {
        val url = "https://wa.me/$number"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

        when {

            isAppInstalled(context, "com.whatsapp") -> {
                intent.setPackage("com.whatsapp")
            }
            isAppInstalled(context, "com.whatsapp.w4b") -> {
                intent.setPackage("com.whatsapp.w4b")
            }
            else -> {
                Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
                return
            }
        }

        context.startActivity(intent)
    }
    private fun applyStatusBarInsets() {
        binding.vAnd15StatusBar.visible()
        ViewCompat.setOnApplyWindowInsetsListener(binding.vAnd15StatusBar) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            view.layoutParams = view.layoutParams.apply {
                height = statusBarHeight
            }
            binding.mainBg.updatePadding(bottom = navBarHeight)
            insets
        }
        ViewCompat.requestApplyInsets(binding.vAnd15StatusBar)
    }


    private fun removeblocknumber() {
        deleteBlockedNumber(mobilenumber)
        CoroutineScope(Dispatchers.IO).launch {
            repo.removenumbertoblockRepo(thredid)
        }
        isnumberBlock = false
        binding.txtnumberblockornot.text =
            resources.getString(R.string.Block) + " ${binding.username.text.toString()}"
//        binding.txtButtonArchive.text =
//            resources.getString(R.string.Block)
    }

    private fun deleteMessage() {
        CoroutineScope(Dispatchers.IO).launch {
            deleteConversation(thredid)
          try{  repo.deleteConversationRepo(arrayListOf(thredid.toString()))

        }catch (e: Exception){ }
            startActivity(
                Intent(
                    this@ViewDetailsActivity, HomeABActivity::class.java
                )
            )
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Constants.isActivitychange = true
        finish()
    }

    private fun dialNumber() {
        dialNumber(mobilenumber)
    }

    fun firstTwo(str: String): String? {
        return if (str.length < 2) str else str.substring(0, 2).uppercase(getDefault())
    }

    private fun blockNumber() {
        val numbers = mobilenumber
        val question = String.format(resources.getString(R.string.block_confirmation), numbers)

        if (packageName == Telephony.Sms.getDefaultSmsPackage(this@ViewDetailsActivity)) {

        showcommandialog(
            dialogtital = resources.getString(R.string.Delete_this_conversation),
            dialogmessage = question,
            positivebutton = resources.getString(R.string.Block),
            negativebutton = resources.getString(R.string.cancel),
            "Block"
        )

        isnumberBlock = true
        }else{
            handleDefaultSmsClick_1(this@ViewDetailsActivity)
        }
    }

    fun chackPermission(): Boolean {
        return PermissionChecker.checkSelfPermission(
            this,
            android.Manifest.permission.SEND_SMS
        ) == PermissionChecker.PERMISSION_GRANTED &&
                PermissionChecker.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_CONTACTS
                ) == PermissionChecker.PERMISSION_GRANTED &&
                PermissionChecker.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_CONTACTS
                ) == PermissionChecker.PERMISSION_GRANTED
    }

    @SuppressLint("Range")
    fun getContactPhoneNumbersByName(
        context: Context,
        contactName: String,
    ): List<Participantsmobilemodel> {
        val contactInfo = mutableListOf<Participantsmobilemodel>()
//        val phoneNumbers = mutableListOf<String>()
        val contentResolver: ContentResolver = context.contentResolver

        // Define the columns you want to retrieve from the Contacts database
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        // Specify the selection criteria to get data for the contact with the specified name
        val selection = "${ContactsContract.CommonDataKinds.Phone.NUMBER} = ?"
        val selectionArgs = arrayOf(contactName)

        val cursor: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )

        cursor?.use {
            while (cursor.moveToNext()) {
                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY))
                val rawPhoneNumber =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val phoneNumber = removeCountryCode(rawPhoneNumber)
                contactInfo.add(Participantsmobilemodel(name, phoneNumber))
            }
        }

        return contactInfo
    }

    private fun removeCountryCode(rawPhoneNumber: String): String {
        // Regex pattern to match the country code prefix, assuming it starts with a '+' followed by one or more digits
        val countryCodePattern = Regex("^\\+\\d+")

        // Remove the country code prefix from the raw phone number
        return rawPhoneNumber.replace(countryCodePattern, "")
    }

    @SuppressLint("Range")
    fun getContactNameByPhoneNumber(context: Context, phoneNumber: String): String? {
        val contentResolver: ContentResolver = context.contentResolver
        var contactName: String? = null

        // Define the columns you want to retrieve from the Contacts database
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)

        // Specify the selection criteria to get data for the contact with the specified phone number
        val selection = "${ContactsContract.CommonDataKinds.Phone.NUMBER} = ?"
        val selectionArgs = arrayOf(phoneNumber)

        val cursor: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )

        cursor?.use {
            if (cursor.moveToFirst()) {
                contactName =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            }
        }

        return contactName
    }

    override fun onpositive(whatfordialog: String) {
        when (whatfordialog) {
            "delete" -> {
                commanDeleteBlockDialog?.dismiss()
                deleteMessage()
            }

            "Block" -> {
                commanDeleteBlockDialog?.dismiss()
                ensureBackgroundThread {
                    addBlockedNumber(mobilenumber)
                    runOnUiThread {
                        binding.txtnumberblockornot.text =
                            resources.getString(R.string.UnBlock) + " ${binding.username.text.toString()}"
//                        binding.txtButtonArchive.text =
//                            resources.getString(R.string.UnBlock)
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        repo.addnumbertoblockRepo(thredid)
                    }
                }
            }

            "clear conversation" -> {
                commanDeleteBlockDialog?.dismiss()
                GlobalScope.launch {
                    withContext(Dispatchers.IO) {

                        try {
                        repo.deleteConversationRepo(arrayListOf(thredid.toString()))


                        }catch (e: Exception){

                        }
                        deleteConversation(thredid)
                        val conversation = Conversation(
                            0,
                            System.currentTimeMillis().toString(),
                            true,
                            binding.username.text.toString(),
                            null,
                            false,
                            binding.mobilenumbertxt.text.toString(),
                            "converremoved_12345",
                            System.currentTimeMillis(),
                            2,
                            true,
                            null,
                            messageId = generateRandomId(),
                            threadId = thredid,
                            isgroupmessage = false,
                            groupName = "",
                            isarchived = false,
                            isPrivateChat = false
                        )
                        repo.insertmessage(conversation)
                    }
                }

            }

            else -> {
                commanDeleteBlockDialog?.dismiss()
            }
        }
    }

    override fun onnegative(whatfordialog: String) {

        when (whatfordialog) {
            "delete" -> {
                commanDeleteBlockDialog?.dismissAllowingStateLoss()
            }

            "Block" -> {
                commanDeleteBlockDialog?.dismissAllowingStateLoss()
            }

            "clear conversation" -> {
                commanDeleteBlockDialog?.dismissAllowingStateLoss()
            }

            else -> {
                commanDeleteBlockDialog?.dismissAllowingStateLoss()
            }
        }
    }

    private fun showcommandialog(
        dialogtital: String,
        dialogmessage: String,
        positivebutton: String,
        negativebutton: String,
        whatfordialog: String,
    ) {
        commanDeleteBlockDialog = CommanDeleteBlockDialog.newInstance(
            dialogtital,
            dialogmessage,
            positivebutton,
            negativebutton,
            whatfordialog
        )
        commanDeleteBlockDialog?.setInterface(this)
        commanDeleteBlockDialog?.show(supportFragmentManager, "delete")

    }

    private fun addtocontact() {
        if (mobilenumber.isEmpty()) {
            return
        }
        Intent().apply {
            action = Intent.ACTION_INSERT_OR_EDIT
            type = "vnd.android.cursor.item/contact"
            putExtra(KEY_PHONE, mobilenumber)
            try {
                startActivityForResult(this, CONTACT_INSERT_EDIT_REQUEST_CODE)
            } catch (e: ActivityNotFoundException) {
                toastMess("Contacts app not found")
            }

        }
    }

    suspend fun editContactByPhoneNumber(phoneNumber: String) {

        if (mobilenumber.isEmpty()) {
            return
        }
        Intent().apply {
            action = Intent.ACTION_INSERT_OR_EDIT
            type = "vnd.android.cursor.item/contact"
            putExtra(KEY_PHONE, mobilenumber)
            putExtra(ContactsContract.Intents.Insert.NAME, name)
            try {
//                gooutside = true
                startActivityForResult(this, CONTACT_INSERT_EDIT_REQUEST_CODE)
            } catch (e: ActivityNotFoundException) {
                toastMess("Contacts app not found")
            }

        }

//        val (contactId, displayName) = retrieveContactDetailsByPhoneNumber(phoneNumber)
//        if (contactId == null) {
//            toastMess("Contact not found for phone number: $phoneNumber")
//            return
//        }
////        val intent = Intent(Intent.ACTION_EDIT)
////        intent.data = Uri.parse("content://contacts/people/$contactId/edit")
////        intent.type = ContactsContract.Contacts.CONTENT_ITEM_TYPE
////        intent.putExtra(ContactsContract.Intents.Insert.NAME, displayName)
//        val intent = Intent(Intent.ACTION_EDIT, ContactsContract.Data.CONTENT_URI)
//        intent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber)
//        try {
//            startActivityForResult(intent, CONTACT_INSERT_EDIT_REQUEST_CODE)
//        } catch (e: ActivityNotFoundException) {
//            Log.d(
//                "",
//                "editContactByPhoneNumber: ActivityNotFoundException <---------> ${e.localizedMessage}"
//            )
//            toastMess("Contacts app not found")
//        }
    }

    @SuppressLint("Range")
    suspend fun retrieveContactDetailsByPhoneNumber(phoneNumber: String): Pair<Long?, String?> =
        withContext(Dispatchers.IO) {
            var contactId: Long? = null
            var displayName: String? = null
            val cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                ),
                "${ContactsContract.CommonDataKinds.Phone.NUMBER} = ?",
                arrayOf(phoneNumber),
                null
            )
            cursor?.use {
                if (it.moveToFirst()) {
                    contactId =
                        it.getLong(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                    displayName =
                        it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                }
            }
            return@withContext Pair(contactId, displayName)
        }

    @SuppressLint("StringFormatInvalid")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CONTACT_INSERT_EDIT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                GlobalScope.launch {
                    withContext(Dispatchers.IO) {
                        val contactData: Uri? = data?.data // Get the URI of the saved contact
                        "deleteCurrentContact <---------------------> message ${contactData}".log()
                        val contactName = retrieveContactName(contactData)
                        if (contactName != null) {
                            thredid.let { repo.UpdateMessageTitleRepo(contactName, it) }
                            runOnUiThread {
                                binding.username.text = contactName



                                name = contactName
                                chacksaveandedit = containsAlphabets(name)
                                "deleteCurrentContact <---------------------> chacksaveandedit ${chacksaveandedit}".log()
                                if (chacksaveandedit) {
                                    binding.txtButtonAdd.text = resources.getString(R.string.Edit)
                                    binding.imageView6.setImageResource(R.drawable.baseline_edit_24)
                                } else {
                                    binding.txtButtonAdd.text = resources.getString(R.string.Add_to_contact_new_ui)
                                    binding.imageView6.setImageResource(R.drawable.baseline_person_add_alt_24)
                                }

                                if (binding.txtButtonAdd.text.equals(resources.getString(R.string.Add_to_contact_new_ui))){
                                    binding.mobilenumbertxt.text = binding.username.text
                                }else{
                                    binding. mobilenumbertxt.text = mobilenumber
                                }

                                if (isnumberBlock) {
                                    binding.txtnumberblockornot.text = resources.getString(R.string.UnBlock) + " ${binding.username.text.toString()}"
                                } else {
                                    binding.txtnumberblockornot.text = resources.getString(R.string.Block) + " ${binding.username.text.toString()}"
                                }



                            }


                        }
                    }
                }


                CoroutineScope(Dispatchers.IO).launch {

                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
//                Toast.makeText(this, "no", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onStart() {
        super.onStart()
        ThemeSetup()
        if (BaseSharedPreferences(this@ViewDetailsActivity).mIS_SUBSCRIBED!!) {
            binding.constSmallNativeAdView.gone()
        } else {
            maybeShowViewDetailsNative()
        }
    }

    private fun maybeShowViewDetailsNative() {
        if (viewDetailsAdRequested) {
            return
        }
        viewDetailsAdRequested = true
        adsOrchestrator.showViewDetailsNative(
            activity = this,
            ui = AdUiBinding(
                rootContainer = binding.constSmallNativeAdView,
                adFrame = binding.frameSmallNativeAdBottom,
                shimmer = binding.smallNativeAdShimmerBottom
            ),
            trigger = "content_ready",
            onNoAd = { binding.constSmallNativeAdView.gone() }
        )
    }

    private fun ThemeSetup() {
        applyMaterialViewDetailColors()
        applyMaterialSwitchColors()
        applyMaterialSystemBarColors()
    }

    private fun applyMaterialViewDetailColors() {
        val surfaceColor = getProperBackgroundColor()
        val statusBarColor = getProperStatusBarColor()
        val onSurfaceColor = getProperTextColor()
        val onSurfaceVariantColor = getProperSecondaryTextColor()
        val errorColor =
            MaterialColors.getColor(binding.mainBg, R.attr.colorError)
        val optionFillColor = getProperPrimaryColor().adjustAlpha(0.1f)
        val optionStrokeColor = getProperPrimaryColor().adjustAlpha(0.05f)

        binding.mainBg.setBackgroundColor(surfaceColor)
        binding.vAnd15StatusBar.setBackgroundColor(statusBarColor)
        binding.defaulttoolshow.setBackgroundColor(surfaceColor)
        binding.nest.setBackgroundColor(surfaceColor)

        binding.constraintLayout4.background = createOptionBackground(
            cornerSize = resources.getDimension(com.intuit.sdp.R.dimen._12sdp),
            fillColor = optionFillColor,
            strokeColor = optionStrokeColor,
            strokeWidth = resources.getDimension(com.intuit.sdp.R.dimen._1sdp),
            showRipple = true,
            rippleColor = getProperPrimaryColor().adjustAlpha(0.5f)
        )


//        binding.constraintLayout6.background = createOptionBackground(
//            cornerSize = resources.getDimension(com.intuit.sdp.R.dimen._12sdp),
//            fillColor = optionFillColor,
//            strokeColor = optionStrokeColor,
//            strokeWidth = resources.getDimension(com.intuit.sdp.R.dimen._1sdp),
//            showRipple = false,
//        )

        listOf(
            binding.btnDivider1,
            binding.btnDivider11,
            binding.btnDivider2,
        ).forEachIndexed { index, view ->

            view.setBackgroundColor(onSurfaceColor.adjustAlpha(0.05f))
        }

        listOf(
            binding.line1,
            binding.customNotification,
            binding.contactPrivateChat,
            binding.contactArchive,
            binding.blockNumberBtn,
        ).forEachIndexed { index, layout ->

            layout.background = createOptionBackground(
                cornerSize = resources.getDimension(com.intuit.sdp.R.dimen._5sdp),
                fillColor = optionFillColor,
                strokeWidth = resources.getDimension(com.intuit.sdp.R.dimen._1sdp),
                strokeColor = surfaceColor,
                showRipple = true,
                rippleColor = getProperPrimaryColor().adjustAlpha(0.3f),
                isTop = index == 0,
                isBottom = index == 4,
            )
        }





        binding.participantsMobileNumber.background = createOptionBackground(
            cornerSize = resources.getDimension(com.intuit.sdp.R.dimen._12sdp),
            fillColor = optionFillColor,
            strokeColor = optionStrokeColor,
            strokeWidth = resources.getDimension(com.intuit.sdp.R.dimen._1sdp),
            showRipple = false,
        )



        binding.view3New.setBackgroundColor(getProperPrimaryColor().adjustAlpha(0.5f))

        binding.textView3.setTextColor(onSurfaceColor)
        binding.username.setTextColor(onSurfaceColor)
        binding.mobilenumbertxt.setTextColor(onSurfaceColor)
        binding.textView6.setTextColor(onSurfaceColor)
        binding.textView8.setTextColor(onSurfaceColor)
        binding.customNotificationTxt.setTextColor(onSurfaceColor)
        binding.archiveornotPrivateChat.setTextColor(onSurfaceColor)
        binding.archiveornot.setTextColor(onSurfaceColor)
        binding.textView6New.setTextColor(onSurfaceColor)
        binding.txtnumberblockornot.setTextColor(errorColor)

        binding.backBtn.iconTint = ColorStateList.valueOf(onSurfaceVariantColor)



        listOf(
            binding.imageView4,
            binding.imageView5,
            binding.imageView51,
            binding.imageView6,
        ).forEach { view ->
            view.setColorFilter(getProperPrimaryColor())
        }

        listOf(
            binding.txtButtonCall,
            binding.txtButtonArchive,
            binding.txtButtonWp,
            binding.txtButtonAdd,
        ).forEach { view ->
            view.setTextColor(onSurfaceColor)
        }


        listOf(
            binding.callBtn,
            binding.smsBtn,
            binding.deleteBtn,
        ).forEachIndexed { index, view ->

            view.background = createOptionBackground(
                fillColor = Color.TRANSPARENT,
                rippleColor = getProperPrimaryColor().adjustAlpha(0.3f),
                showRipple = true,
                useCustomCorners = index == 0 || index == 2,
                topLeftCornerSize = if (index == 0) resources.getDimension(com.intuit.sdp.R.dimen._12sdp) else 0f,
                bottomLeftCornerSize = if (index == 0) resources.getDimension(com.intuit.sdp.R.dimen._12sdp) else 0f,
                topRightCornerSize = if (index == 2) resources.getDimension(com.intuit.sdp.R.dimen._12sdp) else 0f,
                bottomRightCornerSize = if (index == 2) resources.getDimension(com.intuit.sdp.R.dimen._12sdp) else 0f,
            )

        }





        binding.imageView7.setColorFilter(onSurfaceVariantColor)
        binding.numberCopyBtn.setColorFilter(onSurfaceVariantColor)
        binding.notificationChat.setColorFilter(onSurfaceVariantColor)
        binding.customNotificationImage.setColorFilter(onSurfaceVariantColor)
        binding.imageView8PrivateChat.setColorFilter(onSurfaceVariantColor)
        binding.imageView8.setColorFilter(onSurfaceVariantColor)
        binding.imageView9.setColorFilter(errorColor)
    }


    private fun applyMaterialSwitchColors() {
        val onSurfaceVariantColor = getProperSecondaryTextColor()
        val primaryColor = getProperPrimaryColor()

        binding.notificationGetOrNot.thumbTintList = ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
            intArrayOf(primaryColor, onSurfaceVariantColor)
        )
        binding.notificationGetOrNot.trackTintList = ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
            intArrayOf(
                primaryColor.adjustAlpha(0.35f),
                onSurfaceVariantColor.adjustAlpha(0.25f)
            )
        )
    }

    private fun applyMaterialSystemBarColors() {
        val surfaceColor = getProperBackgroundColor()
        val statusBarColor = getProperStatusBarColor()
        val navigationBarColor = getBottomNavigationBackgroundColor()
        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        window.decorView.setBackgroundColor(surfaceColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightSystemBars =
                ThemeModeManager.shouldUseLightSystemBars(this@ViewDetailsActivity)
            isAppearanceLightStatusBars = useLightSystemBars
            isAppearanceLightNavigationBars = useLightSystemBars
        }
    }

//    private fun createOptionBackground(fillColor: Int, strokeColor: Int): GradientDrawable {
//        return GradientDrawable().apply {
//            shape = GradientDrawable.RECTANGLE
//            cornerRadius = resources.getDimension(com.intuit.sdp.R.dimen._12sdp)
//            setColor(fillColor)
//            setStroke(resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._1sdp), fillColor)
////            setStroke(0, strokeColor)
//        }
//    }

    fun listAllNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channels = notificationManager.notificationChannels
            for (channel in channels) {
                Log.d(
                    "",
                    "listAllNotificationChannels: <----> Channel ID: ${channel.id}, Name: ${channel.name}"
                )
            }
        } else {
            println("Notification channels are not supported on this device.")
        }
    }


}
