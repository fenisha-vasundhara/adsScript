package com.messenger.phone.number.text.sms.service.apps

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.InsetDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Telephony
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import com.demo.adsmanage.Commen.Constants
import com.demo.adsmanage.Commen.firebaseEventMain
import com.google.android.material.color.MaterialColors
import com.messenger.phone.number.text.sms.service.apps.CommanClass.copyToClipboard
import com.messenger.phone.number.text.sms.service.apps.CommanClass.drawableCache
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
import com.messenger.phone.number.text.sms.service.apps.CommanClass.groupnamecustom
import com.messenger.phone.number.text.sms.service.apps.CommanClass.handleDefaultSmsClick_1
import com.messenger.phone.number.text.sms.service.apps.CommanClass.insertNewSMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.shareText
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.applyFontToMenuItem
import com.messenger.phone.number.text.sms.service.apps.CustomSnackbar.CookieBar
import com.messenger.phone.number.text.sms.service.apps.Dialog.CommanDeleteBlockDialog
import com.messenger.phone.number.text.sms.service.apps.Dialog.Show_Paticular_Edit_Dialog
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
import com.messenger.phone.number.text.sms.service.apps.adapter.InMainAdapter
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityRecycleBinSendMessageBinding
import com.messenger.phone.number.text.sms.service.apps.helper.RandomDrawableProvider
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.CommanDeleteBlockDialogInterface
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MessageSelectUnselect
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversationbin
import com.simplemobiletools.commons.helpers.SimpleContactsHelper
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
import javax.inject.Inject
import java.util.Locale

@AndroidEntryPoint
class RecycleBinSendMessageActivity : AppCompatActivity(), MessageSelectUnselect,
    CommanDeleteBlockDialogInterface {
    private var Allconvolist2: ArrayList<Conversationbin> = arrayListOf()
    private var datanetonoff: ArrayList<Conversationbin> = arrayListOf()
    private var datanetonoffadapter: ArrayList<Conversation> = arrayListOf()
    lateinit var binding: ActivityRecycleBinSendMessageBinding
    var tredid: Long? = null
    private var mobileNumber: String? = null
    private var name: String? = ""
    private val ICON_MARGIN = 8
    private var isgroupmessage: Boolean = false
    var selectedMessageList2: ArrayList<Conversation> = arrayListOf()
    private var commanDeleteBlockDialog: CommanDeleteBlockDialog? = null
    private lateinit var mProgressDialog: ProgressDialog
    var selecteditemmain: ArrayList<String> = arrayListOf()

    @Inject
    lateinit var inMainAdapter: InMainAdapter

    @Inject
    lateinit var messagerDatabaseRepo: MessagerDatabaseRepo

    @Inject
    lateinit var realm: Lazy<Realm>

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recycle_bin_send_message)
        setBaseTheme(binding.vAnd15StatusBar)
        tredid = intent.getLongExtra("tredid", 0L)
        this.firebaseEventMain("Recycle_Bin_Send_Message")
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

        mobileNumber = if (intent.getStringExtra("mobileNumber") == null) {
            ""
        } else {
            intent.getStringExtra("mobileNumber")
        }
        name = if (intent.getStringExtra("name") == null) {
            mobileNumber
        } else {
            intent.getStringExtra("name")
        }
        isgroupmessage = intent.getBooleanExtra("isgroupmessage", false)
        try {
            binding.isLetterorNot = name?.get(0)?.isLetter()
        } catch (_: Exception) {
            binding.isLetterorNot = false
        }
        binding.backBtn.setOnClickListener {
            Constants.isActivitychange = true
            onBackPressedDispatcher.onBackPressed()
        }
        binding.threadMessagesFastscroller.updateColors(resources.getColor(R.color.procolor, theme))
        setupcontacticon()
        setupmessageadapter()
        mProgressDialog = ProgressDialog(this, R.style.Dialog_Custom)
        mProgressDialog.setCancelable(false)
        mProgressDialog.setMessage("Loading...")
        val realmInstance = if (RealmFeatureFlag(this).useRealmReads) {
            runCatching { realm.get() }.getOrNull()
        } else {
            null
        }
        ConversationDataSourceProvider.get(this, messagerDatabaseRepo, realmInstance).observeDeletedMessages(tredid!!).observe(this) { it ->
            Allconvolist2.clear()
            Allconvolist2.addAll(it)
            datanetonoff.clear()
            datanetonoffadapter.clear()
            datanetonoff = ArrayList(it.distinctBy { it.messageId })
            datanetonoff =
                ArrayList(datanetonoff.sortedWith(compareBy<Conversationbin> { it.date }.thenBy { it.id }))
            datanetonoff.forEachIndexed { index, conversation ->
                datanetonoffadapter.add(
                    Conversation(
                        date = conversation.date!!,
                        draftmessage = conversation.draftmessage,
                        messagetype = conversation.messagetype,
                        messageotp = conversation.messageotp,
                        groupName = conversation.groupName,
                        CategoryName = conversation.CategoryName,
                        customtimeuri = conversation.customtimeuri,
                        phoneNumber = conversation.phoneNumber!!,
                        snippet = conversation.snippet!!,
                        title = conversation.title!!,
                        photoUri = conversation.photoUri,
                        messageStatus = conversation.messageStatus,
                        isbanneradshow = conversation.isbanneradshow,
                        isnewmessagescroll = conversation.isnewmessagescroll,
                        isonlyselectedthem = conversation.isonlyselectedthem,
                        shownotification = conversation.shownotification,
                        messagetraslateshow = conversation.messagetraslateshow,
                        messagetraslationanimationshow = conversation.messagetraslationanimationshow,
                        isgroupmessage = conversation.isgroupmessage,
                        isblocknumber = conversation.isblocknumber,
                        isexpandmessageview = conversation.isexpandmessageview,
                        is_scheduled = conversation.is_scheduled,
                        isMessagefound = conversation.isMessagefound,
                        isPrivateChat = conversation.isPrivateChat,
                        isarchived = conversation.isarchived,
                        ispinned = conversation.ispinned,
                        isnewmessage = conversation.isnewmessage!!,
                        isnumaric = conversation.isnumaric,
                        read = conversation.read,
                        usesCustomTitle = conversation.usesCustomTitle,
                        messageId = conversation.messageId,
                        threadId = conversation.threadId,
                        time = conversation.time,
                        pinneddate = conversation.pinneddate,
                        type = conversation.type,
                        newMessageCount = conversation.newMessageCount,
                        themenumber = conversation.themenumber,
                        messagewithattachment = conversation.messagewithattachment
                    )
                )
            }
            inMainAdapter.list = datanetonoffadapter
        }


        binding.deleteBtn.setOnClickListener {

            if (packageName == Telephony.Sms.getDefaultSmsPackage(this@RecycleBinSendMessageActivity)) {

                if (selectedMessageList2.isEmpty()) {
                showcommandialog(
                    dialogtital = resources.getString(R.string.Delete_this_conversation),
                    dialogmessage = resources.getString(R.string.This_is_permanent),
                    positivebutton = resources.getString(R.string.Delete),
                    negativebutton = resources.getString(R.string.cancel),
                    "alldelete"
                )
            } else {
                showcommandialog(
                    dialogtital = resources.getString(R.string.Delete_this_conversation),
                    dialogmessage = resources.getString(R.string.This_is_permanent),
                    positivebutton = resources.getString(R.string.Delete),
                    negativebutton = resources.getString(R.string.cancel),
                    "delete"
                )
            }
            }else{
                handleDefaultSmsClick_1(this@RecycleBinSendMessageActivity)
            }
        }
        binding.copyBtn.setOnClickListener {
            copyToClipboard(selectedMessageList2[0].snippet.replace(Regex("^\\s+|\\s+$"), ""))
            showcustomSnackbar(selectedMessageList2[0].snippet.replace(Regex("^\\s+|\\s+$"), ""))
        }
        binding.moreBtnForEditing.setOnClickListener {
            if (selectedMessageList2.isEmpty()) {
                showMenu(it, R.menu.recylerbin_default_menu)
            } else {
                showMenuselected(it, R.menu.recylerbin_seleted_menu)
            }
        }
    }

    private fun showcustomSnackbar(copytextnew: String) {
        CookieBar.build(this).setCustomView(R.layout.copy_text_custom_leyout)
            .setCustomViewInitializer { view ->
                val copytext = view.findViewById <com.google.android.material.textview.MaterialTextView>(R.id.copytext)
                val textcopy = view.findViewById<ConstraintLayout>(R.id.textcopy)
                val textshare = view.findViewById<ConstraintLayout>(R.id.textshare)
                copytext.text = copytextnew
                textcopy.setOnClickListener {
                    CookieBar.dismiss(this)
                    showPaticulareditpopup(copytextnew)
                }
                textshare.setOnClickListener {
                    CookieBar.dismiss(this)
                    shareText(copytextnew)
                }
            }.setAction("Close") { CookieBar.dismiss(this) }
            .setTitle(R.string.two_Step_Verification_new)
            .setMessage(R.string.two_Step_Verification_new).setEnableAutoDismiss(true)
            .setDuration(7000).setSwipeToDismiss(true).setCookiePosition(Gravity.BOTTOM).show()
    }

    private fun showPaticulareditpopup(text: String) {
        val showPaticularEditDialog by lazy {
            Show_Paticular_Edit_Dialog()
        }
        showPaticularEditDialog.edittxt = text
        showPaticularEditDialog.show(supportFragmentManager, "showPaticularEditDialog")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("RestrictedApi", "ResourceType")
    private fun showMenuselected(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(this, v)
        popup.menuInflater.inflate(menuRes, popup.menu)
        if (popup.menu is MenuBuilder) {
            val menuBuilder = popup.menu as MenuBuilder
            menuBuilder.setOptionalIconsVisible(true)
            for (item in menuBuilder.visibleItems) {
                val iconMarginPx = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, ICON_MARGIN.toFloat(), resources.displayMetrics
                ).toInt()
                if (item.icon != null) {
                    item.icon = InsetDrawable(item.icon, iconMarginPx, 0, iconMarginPx, 0)
                }
            }
        }
        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.restore_message_selected -> {
                    restoreallmessageselected()
                }
            }
            true
        }
        val fontRes = R.font.lato_semibold
        for (i in 0 until popup.menu.size()) {
            val item = popup.menu.getItem(i)
            applyFontToMenuItem(this, item, fontRes)
        }
        popup.show()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("RestrictedApi", "ResourceType")
    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(this, v)
        popup.menuInflater.inflate(menuRes, popup.menu)
        if (popup.menu is MenuBuilder) {
            val menuBuilder = popup.menu as MenuBuilder
            menuBuilder.setOptionalIconsVisible(true)
            for (item in menuBuilder.visibleItems) {
                val iconMarginPx = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, ICON_MARGIN.toFloat(), resources.displayMetrics
                ).toInt()
                if (item.icon != null) {
                    item.icon = InsetDrawable(item.icon, iconMarginPx, 0, iconMarginPx, 0)
                }
            }
        }
        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.restore_message -> {
                    restoreallmessage()
                }
            }
            true
        }
        val fontRes = R.font.lato_semibold
        for (i in 0 until popup.menu.size()) {
            val item = popup.menu.getItem(i)
            applyFontToMenuItem(this, item, fontRes)
        }
        popup.show()
    }

    private fun restoreallmessageselected() {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                runOnUiThread {
                    mProgressDialog.show()
                }
                selectedMessageList2.forEachIndexed { index, conversation ->
                    if (conversation.messagewithattachment?.attachments?.isEmpty() == true) {
                        val newMessageId = insertNewSMS(
                            conversation.phoneNumber!!,
                            "",
                            conversation.snippet!!,
                            conversation.date?.toLong()!!,
                            if (conversation.read) 1 else 0,
                            conversation.threadId!!,
                            conversation.type!!,
                            1
                        )
                        messagerDatabaseRepo.insertmessage(
                            Conversation(
                                date = conversation.date!!,
                                draftmessage = conversation.draftmessage,
                                messagetype = conversation.messagetype,
                                messageotp = conversation.messageotp,
                                groupName = conversation.groupName,
                                CategoryName = conversation.CategoryName,
                                customtimeuri = conversation.customtimeuri,
                                phoneNumber = conversation.phoneNumber!!,
                                snippet = conversation.snippet!!,
                                title = conversation.title!!,
                                photoUri = conversation.photoUri,
                                messageStatus = conversation.messageStatus,
                                isbanneradshow = conversation.isbanneradshow,
                                isnewmessagescroll = conversation.isnewmessagescroll,
                                isonlyselectedthem = conversation.isonlyselectedthem,
                                shownotification = conversation.shownotification,
                                messagetraslateshow = conversation.messagetraslateshow,
                                messagetraslationanimationshow = conversation.messagetraslationanimationshow,
                                isgroupmessage = conversation.isgroupmessage,
                                isblocknumber = conversation.isblocknumber,
                                isexpandmessageview = conversation.isexpandmessageview,
                                is_scheduled = conversation.is_scheduled,
                                isMessagefound = conversation.isMessagefound,
                                isPrivateChat = conversation.isPrivateChat,
                                isarchived = conversation.isarchived,
                                ispinned = conversation.ispinned,
                                isnewmessage = conversation.isnewmessage!!,
                                isnumaric = conversation.isnumaric,
                                read = conversation.read,
                                usesCustomTitle = conversation.usesCustomTitle,
                                messageId = newMessageId,
                                threadId = conversation.threadId,
                                time = conversation.time,
                                pinneddate = conversation.pinneddate,
                                type = conversation.type,
                                newMessageCount = conversation.newMessageCount,
                                themenumber = conversation.themenumber,
                                messagewithattachment = conversation.messagewithattachment
                            )
                        )
                    } else {
                        messagerDatabaseRepo.insertmessage(
                            Conversation(
                                date = conversation.date!!,
                                draftmessage = conversation.draftmessage,
                                messagetype = conversation.messagetype,
                                messageotp = conversation.messageotp,
                                groupName = conversation.groupName,
                                CategoryName = conversation.CategoryName,
                                customtimeuri = conversation.customtimeuri,
                                phoneNumber = conversation.phoneNumber!!,
                                snippet = conversation.snippet!!,
                                title = conversation.title!!,
                                photoUri = conversation.photoUri,
                                messageStatus = conversation.messageStatus,
                                isbanneradshow = conversation.isbanneradshow,
                                isnewmessagescroll = conversation.isnewmessagescroll,
                                isonlyselectedthem = conversation.isonlyselectedthem,
                                shownotification = conversation.shownotification,
                                messagetraslateshow = conversation.messagetraslateshow,
                                messagetraslationanimationshow = conversation.messagetraslationanimationshow,
                                isgroupmessage = conversation.isgroupmessage,
                                isblocknumber = conversation.isblocknumber,
                                isexpandmessageview = conversation.isexpandmessageview,
                                is_scheduled = conversation.is_scheduled,
                                isMessagefound = conversation.isMessagefound,
                                isPrivateChat = conversation.isPrivateChat,
                                isarchived = conversation.isarchived,
                                ispinned = conversation.ispinned,
                                isnewmessage = conversation.isnewmessage!!,
                                isnumaric = conversation.isnumaric,
                                read = conversation.read,
                                usesCustomTitle = conversation.usesCustomTitle,
                                messageId = conversation.messageId,
                                threadId = conversation.threadId,
                                time = conversation.time,
                                pinneddate = conversation.pinneddate,
                                type = conversation.type,
                                newMessageCount = conversation.newMessageCount,
                                themenumber = conversation.themenumber,
                                messagewithattachment = conversation.messagewithattachment
                            )
                        )
                    }
                }
                selectedMessageList2.forEachIndexed { index, conversation ->
                    conversation.messageId?.toInt()
                        ?.let { messagerDatabaseRepo.deleteConversationRecyclerbinMessidRepo(it.toLong()) }
                }
            }
            withContext(Dispatchers.Main) {
                mProgressDialog.dismiss()
                selectionRemove()
            }
        }
    }


    private fun restoreallmessage() {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                runOnUiThread {
                    mProgressDialog.show()
                }
                Allconvolist2.forEachIndexed { index, conversation ->
                    if (conversation.messagewithattachment?.attachments?.isEmpty() == true) {
                        val newMessageId = insertNewSMS(
                            conversation.phoneNumber!!,
                            "",
                            conversation.snippet!!,
                            conversation.date?.toLong()!!,
                            if (conversation.read) 1 else 0,
                            conversation.threadId!!,
                            conversation.type!!,
                            1
                        )
                        messagerDatabaseRepo.insertmessage(
                            Conversation(
                                date = conversation.date!!,
                                draftmessage = conversation.draftmessage,
                                messagetype = conversation.messagetype,
                                messageotp = conversation.messageotp,
                                groupName = conversation.groupName,
                                CategoryName = conversation.CategoryName,
                                customtimeuri = conversation.customtimeuri,
                                phoneNumber = conversation.phoneNumber!!,
                                snippet = conversation.snippet!!,
                                title = conversation.title!!,
                                photoUri = conversation.photoUri,
                                messageStatus = conversation.messageStatus,
                                isbanneradshow = conversation.isbanneradshow,
                                isnewmessagescroll = conversation.isnewmessagescroll,
                                isonlyselectedthem = conversation.isonlyselectedthem,
                                shownotification = conversation.shownotification,
                                messagetraslateshow = conversation.messagetraslateshow,
                                messagetraslationanimationshow = conversation.messagetraslationanimationshow,
                                isgroupmessage = conversation.isgroupmessage,
                                isblocknumber = conversation.isblocknumber,
                                isexpandmessageview = conversation.isexpandmessageview,
                                is_scheduled = conversation.is_scheduled,
                                isMessagefound = conversation.isMessagefound,
                                isPrivateChat = conversation.isPrivateChat,
                                isarchived = conversation.isarchived,
                                ispinned = conversation.ispinned,
                                isnewmessage = conversation.isnewmessage!!,
                                isnumaric = conversation.isnumaric,
                                read = conversation.read,
                                usesCustomTitle = conversation.usesCustomTitle,
                                messageId = newMessageId,
                                threadId = conversation.threadId,
                                time = conversation.time,
                                pinneddate = conversation.pinneddate,
                                type = conversation.type,
                                newMessageCount = conversation.newMessageCount,
                                themenumber = conversation.themenumber,
                                messagewithattachment = conversation.messagewithattachment
                            )
                        )
                    } else {
                        messagerDatabaseRepo.insertmessage(
                            Conversation(
                                date = conversation.date!!,
                                draftmessage = conversation.draftmessage,
                                messagetype = conversation.messagetype,
                                messageotp = conversation.messageotp,
                                groupName = conversation.groupName,
                                CategoryName = conversation.CategoryName,
                                customtimeuri = conversation.customtimeuri,
                                phoneNumber = conversation.phoneNumber!!,
                                snippet = conversation.snippet!!,
                                title = conversation.title!!,
                                photoUri = conversation.photoUri,
                                messageStatus = conversation.messageStatus,
                                isbanneradshow = conversation.isbanneradshow,
                                isnewmessagescroll = conversation.isnewmessagescroll,
                                isonlyselectedthem = conversation.isonlyselectedthem,
                                shownotification = conversation.shownotification,
                                messagetraslateshow = conversation.messagetraslateshow,
                                messagetraslationanimationshow = conversation.messagetraslationanimationshow,
                                isgroupmessage = conversation.isgroupmessage,
                                isblocknumber = conversation.isblocknumber,
                                isexpandmessageview = conversation.isexpandmessageview,
                                is_scheduled = conversation.is_scheduled,
                                isMessagefound = conversation.isMessagefound,
                                isPrivateChat = conversation.isPrivateChat,
                                isarchived = conversation.isarchived,
                                ispinned = conversation.ispinned,
                                isnewmessage = conversation.isnewmessage!!,
                                isnumaric = conversation.isnumaric,
                                read = conversation.read,
                                usesCustomTitle = conversation.usesCustomTitle,
                                messageId = conversation.messageId,
                                threadId = conversation.threadId,
                                time = conversation.time,
                                pinneddate = conversation.pinneddate,
                                type = conversation.type,
                                newMessageCount = conversation.newMessageCount,
                                themenumber = conversation.themenumber,
                                messagewithattachment = conversation.messagewithattachment
                            )
                        )
                    }
                }
                this@RecycleBinSendMessageActivity.selecteditemmain.clear()
                Allconvolist2.forEachIndexed { index, conversation ->
                    this@RecycleBinSendMessageActivity.selecteditemmain.add(conversation.threadId.toString())
                }
                messagerDatabaseRepo.deleteConversationRecyclerbinRepo(this@RecycleBinSendMessageActivity.selecteditemmain)
            }
            withContext(Dispatchers.Main) {
                mProgressDialog.dismiss()
                selectionRemove()
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

    private fun setupmessageadapter() {
        inMainAdapter.setHasStableIds(true)
        binding.adapter = inMainAdapter
        inMainAdapter.setInterface(this)
    }

    private fun setupcontacticon() {
        if (isgroupmessage) {
            binding.contacticon.setImageDrawable(SimpleContactsHelper(this).getColoredGroupIcon(name.toString()))
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                val profile = mobileNumber?.let { getNameAndPhotoFromPhoneNumber(it) }
                if (profile?.photoUri?.isNotEmpty() == true) {
                    CoroutineScope(Dispatchers.Main).launch {
                        if (profile.photoUri != null && !isDestroyed && !isFinishing) {
                            SimpleContactsHelper(this@RecycleBinSendMessageActivity).loadContactImage(
                                profile.photoUri,
                                binding.contacticon,
                                name!!,
                                null
                            )
                        }
                    }
                } else {
                    val drawable = drawableCache[tredid.toString()]
                    if (drawable != null) {
                        CoroutineScope(Dispatchers.Main).launch {
                            binding.contacticon.setImageDrawable(drawable)
                        }

                    } else {
                        val randomDrawable =
                            RandomDrawableProvider(binding.contacticon.context).getRandomDrawable(tredid!!)
                        CoroutineScope(Dispatchers.Main).launch {
                            binding.contacticon.setImageDrawable(randomDrawable)
                        }
                        drawableCache[tredid.toString()] = randomDrawable
                    }
                }
            }
        }
    }

    override fun onMessageSelect(
        pos: Int,
        snippet: String,
        selectedMessageList: ArrayList<Conversation>,
    ) {
        selectmessage(selectedMessageList, snippet)
    }

    fun selectmessage(selectedMessageList: ArrayList<Conversation>, snippet: String) {
        binding.messagecount.text = "" + selectedMessageList.size
        selectedMessageList2.clear()
        selectedMessageList2.addAll(selectedMessageList)
        this.selecteditemmain.clear()
        Log.d(
            "runOnUiThread",
            "deleteMessage:runOnUiThread <--------> 2 ${selectedMessageList2.size}"
        )
        Log.d(
            "runOnUiThread",
            "deleteMessage:runOnUiThread <--------> 3 ${selectedMessageList.size}"
        )
        selectedMessageList2.forEach {
            this.selecteditemmain.add(it.messageId.toString())
        }
        if (selectedMessageList.isEmpty()) {
            binding.copyBtn.visibility = View.GONE
            binding.messagecount.visibility = View.GONE
            binding.userid.visibility = View.VISIBLE
            binding.maincanternar.visibility = View.VISIBLE
        } else if (selectedMessageList.size == 1) {
            val hasFilledSnippet = selectedMessageList.any { it.snippet.isNotEmpty() }
            if (hasFilledSnippet) {
                binding.copyBtn.visibility = View.VISIBLE
            } else {
                binding.copyBtn.visibility = View.GONE
            }
            binding.messagecount.visibility = View.VISIBLE
            binding.userid.visibility = View.GONE
            binding.maincanternar.visibility = View.GONE
        } else {
            binding.copyBtn.visibility = View.GONE
            binding.messagecount.visibility = View.VISIBLE
            binding.userid.visibility = View.GONE
            binding.maincanternar.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch {
            val list = tredid?.let { messagerDatabaseRepo.getUserMessageListrepo(it) }
            val nameNew = if (list?.isEmpty() == true) {
                if (isgroupmessage) {
                    if (groupnamecustom != null) {
                        groupnamecustom
                    } else {
                        name
                    }
                } else {
                    name
                }

            } else {
                if (isgroupmessage) {
                    if (groupnamecustom != null) {
                        groupnamecustom
                    } else {
                        list!![0].groupName
                    }
                } else {
                    list!![0].title
                }

            }
            runOnUiThread { binding.userid.text = nameNew }

        }

    }

    private fun deleteMessage() {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                runOnUiThread { mProgressDialog.show() }
                Log.d(
                    "runOnUiThread",
                    "deleteMessage:runOnUiThread <--------> 1 ${this@RecycleBinSendMessageActivity.selecteditemmain.size}"
                )
                this@RecycleBinSendMessageActivity.selecteditemmain.forEachIndexed { index, s ->
                    try {
                        messagerDatabaseRepo.deleteConversationRecyclerbinMessidRepo(s.toLong())
                    } catch (e: Exception) {

                    }
                }
            }
            withContext(Dispatchers.Main) {
                mProgressDialog.dismiss()
                selectionRemove()
            }
        }
    }

    private fun selectionRemove() {
        binding.copyBtn.visibility = View.GONE
        binding.messagecount.visibility = View.GONE
        binding.userid.visibility = View.VISIBLE
        binding.maincanternar.visibility = View.VISIBLE
        binding.deleteBtn.visibility = View.VISIBLE
        binding.moreBtnForEditing.visibility = View.VISIBLE
        selectedMessageList2.clear()
        inMainAdapter.selectedMessageList.clear()
        selecteditemmain.clear()
        inMainAdapter.notifyDataSetChanged()
        Handler(Looper.myLooper()!!).postDelayed({
            binding.showusermessage.adapter?.let {

                Log.e("FFFF", "selectionRemove: ${it.itemCount}", )
                if (it.itemCount <= 0){
                    onBackPressedDispatcher.onBackPressed()

                }
            }

        }, 1000)


    }

    override fun onpositive(whatfordialog: String) {
        commanDeleteBlockDialog?.dismiss()
        when (whatfordialog) {
            "delete" -> {
                deleteMessage()
            }

            "alldelete" -> {
                GlobalScope.launch {
                    withContext(Dispatchers.IO) {
                        runOnUiThread { mProgressDialog.show() }
                        messagerDatabaseRepo.deleteConversationRecyclerbinRepo(arrayListOf(tredid.toString()))
                    }
                    withContext(Dispatchers.Main) {
                        mProgressDialog.dismiss()
                    }
                }
            }

            else -> {
                commanDeleteBlockDialog?.dismiss()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        ThemeSetup()
    }

    private fun ThemeSetup() {
        applyMaterialChatTheme()
        applyMaterialSystemBarColors()
    }

    private fun isLightChatTheme(): Boolean {
        return ThemeModeManager.shouldUseLightSystemBars(this)
    }

    private fun resolveChatSurfaceColor(): Int {
        return getProperBackgroundColor()
    }

    private fun resolveChatOnSurfaceColor(): Int {
        return getProperTextColor()
    }

    private fun resolveChatOnSurfaceVariantColor(): Int {
        return getProperSecondaryTextColor()
    }

    private fun resolveChatPrimaryColor(): Int {
        return getProperPrimaryColor()
    }

    private fun resolveChatOutlineVariantColor(surfaceColor: Int, onSurfaceColor: Int): Int {
        return MaterialColors.layer(surfaceColor, onSurfaceColor, 0.12f)
    }

    private fun formatColor(color: Int): String = String.format(Locale.US, "#%08X", color)

    private fun resolveDefaultIncomingBubbleColor(): Int {
        val defaultColor = ColorUtils.blendARGB(
            getProperBackgroundColor(), getProperPrimaryColor(), if (isLightChatTheme()) 0.4f else 0.25f
        )
        return defaultColor
    }

    private fun resolveDefaultOutgoingBubbleColor(): Int {
        val defaultColor = ColorUtils.blendARGB(
            getProperBackgroundColor(), getProperTextColor(), 0.05f
        )
        return defaultColor
    }

    private fun applyAdapterBubblePalette() {
        inMainAdapter.inmessagecolorcustomwallpaper =
            formatColor(resolveDefaultIncomingBubbleColor())
        inMainAdapter.outmessagecolorcustomwallpaper =
            formatColor(resolveDefaultOutgoingBubbleColor())
    }

    private fun applyMaterialChatTheme() {
        val surfaceColor = resolveChatSurfaceColor()
        val statusBarColor = getProperStatusBarColor()
        val onSurfaceColor = resolveChatOnSurfaceColor()
        val onSurfaceVariantColor = resolveChatOnSurfaceVariantColor()
        val outlineVariantColor = resolveChatOutlineVariantColor(surfaceColor, onSurfaceColor)

        applyAdapterBubblePalette()

        binding.mainbackground.setBackgroundColor(surfaceColor)
        binding.constraintLayout.setBackgroundColor(surfaceColor)
        binding.vAnd15StatusBar.setBackgroundColor(statusBarColor)
        binding.view.setBackgroundColor(outlineVariantColor)

        binding.userid.setTextColor(onSurfaceColor)
        binding.messagecount.setTextColor(onSurfaceColor)

        binding.backBtn.setIconResource(R.drawable.recycle_bin_material_ic_arrow_back_rounded)
        binding.copyBtn.setIconResource(R.drawable.message_copy_baseline_content_copy_24)
        binding.deleteBtn.setIconResource(R.drawable.baseline_delete_24_black)
        binding.moreBtnForEditing.setIconResource(R.drawable.ic_m3_more_vert_24)

        val toolbarIconTint = ColorStateList.valueOf(onSurfaceVariantColor)
        listOf(
            binding.copyBtn,
            binding.deleteBtn,
            binding.moreBtnForEditing,
        ).forEach { icon ->
            icon.iconTint = toolbarIconTint
        }

        binding.backBtn.iconTint = ColorStateList.valueOf(onSurfaceColor)
        binding.backBtn.rippleColor = ColorStateList.valueOf(onSurfaceVariantColor)

        applyToolbarIconRipple(surfaceColor, onSurfaceColor)
    }

    private fun applyToolbarIconRipple(surfaceColor: Int, onSurfaceColor: Int) {
        val rippleAlpha = if (isLightChatTheme()) 0.12f else 0.28f
        val rippleColor = MaterialColors.layer(surfaceColor, onSurfaceColor, rippleAlpha)
        val rippleState = ColorStateList.valueOf(rippleColor)
        val mask = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.WHITE)
        }
        val baseRipple = RippleDrawable(ColorStateList.valueOf(rippleColor), null, mask)

        binding.backBtn.rippleColor = rippleState
        listOf(
            binding.copyBtn,
            binding.deleteBtn,
            binding.moreBtnForEditing,
        ).forEach { button ->
            button.rippleColor = rippleState
            button.iconTint = ColorStateList.valueOf(getProperTextColor())
        }

        binding.maincanternar.background = baseRipple.constantState?.newDrawable()?.mutate()
    }

    private fun applyMaterialSystemBarColors() {
        val surfaceColor = resolveChatSurfaceColor()
        val statusBarColor = getProperStatusBarColor()
        val navigationBarColor = getBottomNavigationBackgroundColor()
        val window = this.window

        binding.vAnd15StatusBar.setBackgroundColor(statusBarColor)
        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        window.decorView.setBackgroundColor(surfaceColor)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightSystemBars =
                ThemeModeManager.shouldUseLightSystemBars(this@RecycleBinSendMessageActivity)
            isAppearanceLightStatusBars = useLightSystemBars
            isAppearanceLightNavigationBars = useLightSystemBars
        }
    }

    override fun onnegative(whatfordialog: String) {
        commanDeleteBlockDialog?.dismiss()
    }
}
