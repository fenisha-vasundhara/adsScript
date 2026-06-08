package com.messenger.phone.number.text.sms.service.apps

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.InsetDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.annotation.MenuRes
import androidx.annotation.RequiresApi
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.demo.adsmanage.Commen.Constants
import com.demo.adsmanage.Commen.firebaseEventMain
import com.demo.adsmanage.basemodule.BaseSharedPreferences
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getBottomNavigationBackgroundColor
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
import com.messenger.phone.number.text.sms.service.apps.CommanClass.handleDefaultSmsClick_1
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isfisttimead
import com.messenger.phone.number.text.sms.service.apps.CommanClass.markThreadAsRead
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.applyFontToMenuItem
import com.messenger.phone.number.text.sms.service.apps.Dialog.CommanDeleteBlockDialog
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
import com.messenger.phone.number.text.sms.service.apps.adapter.MainMassageAdapter

import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityPrivacyChatBinding
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.CommanDeleteBlockDialogInterface
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MainMessageClick
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MessageClick
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MoreOPtionClick
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversationbin
import com.messenger.phone.number.text.sms.service.apps.sms.SmsUtils.deleteConversation
import com.messenger.phone.number.text.sms.service.apps.viewModel.GetAllConversationPrivacyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class PrivacyChatActivity : AppCompatActivity(), MessageClick, MainMessageClick, MoreOPtionClick,
    CommanDeleteBlockDialogInterface {

    lateinit var binding: ActivityPrivacyChatBinding

    @Inject
    lateinit var adapterMainMassage: MainMassageAdapter
    private var commanDeleteBlockDialog: CommanDeleteBlockDialog? = null
    lateinit var model: GetAllConversationPrivacyViewModel
    private val ICON_MARGIN = 8

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    private var convolist2: ArrayList<Conversation> = arrayListOf()

    var selecteditemmain: ArrayList<String> = arrayListOf()

    private lateinit var mProgressDialog: ProgressDialog

    @Inject
    lateinit var repo: MessagerDatabaseRepo

    var cout = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        ThemeModeManager.applyThemeMode(this)

        if (ThemeModeManager.getThemeMode(this) == ThemeModeManager.MODE_SYSTEM) {
            com.google.android.material.color.DynamicColors.applyToActivityIfAvailable(this)
        }

        super.onCreate(savedInstanceState)

        setLocal()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_privacy_chat)

        setBaseTheme(binding.vAnd15StatusBar)

        this.firebaseEventMain("Private_Chat")

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

        val textColor = getProperTextColor()

//        binding.backBtn.iconTint = ColorStateList.valueOf(textColor)

        binding.conversationsFastscroller.updateColors(resources.getColor(R.color.procolor, theme))
        binding.adapter = adapterMainMassage
        adapterMainMassage.setHasStableIds(true)
        adapterMainMassage.isselectedAdapter = false
        adapterMainMassage.ismoreoption = true
        adapterMainMassage.setInterface(this@PrivacyChatActivity)
        adapterMainMassage.setInterfaceMoreClick(this@PrivacyChatActivity)
        adapterMainMassage.setInterfaceMainClick(this@PrivacyChatActivity)

        mProgressDialog = ProgressDialog(this, R.style.Dialog_Custom)
        mProgressDialog.setCancelable(false)
        mProgressDialog.setMessage("Loading...")

        model = ViewModelProvider(this)[GetAllConversationPrivacyViewModel::class.java]

        model.GetAllConversationlivelist.observe(this, Observer {
            try {
                convolist2.clear()
                convolist2 = ArrayList(it.distinctBy { it.threadId } as ArrayList<Conversation>)
                adapterMainMassage.updateList(convolist2)
                removepin()
                binding.nomessagefoundchack = it.isEmpty()
            } catch (_: Exception) {
            }
        })

        binding.messageDelete.setOnClickListener {
            if (packageName == Telephony.Sms.getDefaultSmsPackage(this@PrivacyChatActivity)) {
                showcommandialog(
                    dialogtital = resources.getString(R.string.Delete_this_conversation),
                    dialogmessage = resources.getString(R.string.This_is_permanent),
                    positivebutton = resources.getString(R.string.Delete),
                    negativebutton = resources.getString(R.string.cancel),
                    "delete"
                )
            } else {
                handleDefaultSmsClick_1(this@PrivacyChatActivity)
            }
        }

        binding.moreBtn.setOnClickListener {
            showMenu(it, R.menu.privacychatmenu)
        }

        binding.messageArchive.setOnClickListener {
            try {
                removePrivacyChatContact()
            } catch (_: Exception) {
            }
        }

        binding.messageSelectionClose.setOnClickListener {
            selectionRemove()
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        setImageNotFound()
        ThemeSetup()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("RestrictedApi", "ResourceType")
    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val isDarkPopup = ThemeModeManager.isDarkThemeActive(this)
        val baseThemeRes = if (isDarkPopup) {
            R.style.mainScreenDark
        } else {
            R.style.mainScreen
        }
        val overlayRes = if (isDarkPopup) {
            R.style.App_PopupOverlay_Message_Dark
        } else {
            R.style.App_PopupOverlay_Message_Light
        }
        val baseThemedContext = ContextThemeWrapper(this, baseThemeRes)
        val themedContext = ContextThemeWrapper(baseThemedContext, overlayRes)

        val popup = PopupMenu(themedContext, v)
        popup.gravity = Gravity.END
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
                R.id.privacy_setting -> {
                    startActivity(
                        Intent(
                            this,
                            LockScreenSettingActivity::class.java
                        ).putExtra("comefrom", 1)
                    )
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

    private fun deleteMessage() {
        if (selecteditemmain.isEmpty()) {
            selectionRemove()
        } else {
            mProgressDialog.show()
            CoroutineScope(Dispatchers.IO).launch {
                selecteditemmain.forEachIndexed { index, conversation ->
                    conversation.let {
                        val data = repo.getUserMessageListChackrepo(it.toLong())
                        data.forEachIndexed { index, conversation ->
                            repo.insertOrUpdateRecycleBinRepo(
                                Conversationbin(
                                    date = conversation.date,
                                    draftmessage = conversation.draftmessage,
                                    messagetype = conversation.messagetype,
                                    messageotp = conversation.messageotp,
                                    groupName = conversation.groupName,
                                    CategoryName = conversation.CategoryName,
                                    customtimeuri = conversation.customtimeuri,
                                    phoneNumber = conversation.phoneNumber,
                                    snippet = conversation.snippet,
                                    title = conversation.title,
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
                        deleteConversation(it.toLong())
                    }
                }

            try{    repo.deleteConversationRepo(selecteditemmain)

            }catch (e: Exception){ }

            CoroutineScope(Dispatchers.Main).launch {
                    if (!isFinishing && !isDestroyed && mProgressDialog.isShowing) {
                        mProgressDialog.dismiss()
                    }
                    selectionRemove()
                }
            }
        }
    }

    private fun removePrivacyChatContact() {
        if (selecteditemmain.isEmpty()) {
            selectionRemove()
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                repo.removePrivacyConversationRepo(selecteditemmain)
                CoroutineScope(Dispatchers.Main).launch {
                    binding.isselection = false
                    adapterMainMassage.notifyDataSetChanged()
                }
            }
        }
    }

    private fun removepin() {
        CoroutineScope(Dispatchers.IO).launch {
            config.removePinnedConversations(convolist2)
        }
    }

    private fun selectionRemove() {
        binding.isselection = false
        selecteditemmain.clear()
        adapterMainMassage.selecteditem.clear()
        adapterMainMassage.notifyDataSetChanged()
    }

    override fun onMainClick(
        position: Int,
        list: ArrayList<Conversation>,
        selecteditem: ArrayList<Conversation>
    ) {
        this.selecteditemmain.clear()
        selecteditem.forEach {
            this.selecteditemmain.add(it.threadId.toString())
        }
        binding.isselection = selecteditem.isNotEmpty()
        ("${selecteditem.size} " + resources.getString(R.string.selected)).also {
            binding.selectioncount.text = it
        }

    }

    override fun onClick(
        tredid: Long?,
        pos: Int,
        title: String,
        phoneNumber: String,
        holder: MainMassageAdapter.MainMassageAdapterViewHolder,
        list: ArrayList<Conversation>,
        position: Int
    ) {
        Constants.isActivitychange = true
        sendMessagerActivity(tredid, title, phoneNumber, list[pos].isgroupmessage)

    }

    private fun sendMessagerActivity(
        tredid: Long?,
        title: String,
        phoneNumber: String,
        isgroupmessage: Boolean
    ) {


        CoroutineScope(Dispatchers.IO).launch {
            repo.setisoldmessageRepo(false, tredid!!)
            repo.setisoldmessageCountRepo(0, tredid)
            markThreadAsRead(tredid)
        }

        if (cout >= 2 || !isfisttimead) {
            val isSubscribed = BaseSharedPreferences(this).mIS_SUBSCRIBED!!

            isfisttimead = true
            cout++


            com.messenger.phone.number.text.sms.service.apps.CommanClass.cout = 0
            startActivity(
                Intent(
                    this@PrivacyChatActivity, if (config.Message_Send_Activity == "1") {
                        SendMessageActivity::class.java
                    } else {
                        SendMessageActivity::class.java
                    }
                ).putExtra("tredid", tredid).putExtra("name", title)
                    .putExtra("mobileNumber", phoneNumber).putExtra(
                    "isgroupmessage",
                    isgroupmessage
                )
            )


        } else {
            cout++
            startActivity(
                Intent(
                    this, if (config.Message_Send_Activity == "1") {
                        SendMessageActivity::class.java
                    } else {
                        SendMessageActivity::class.java
                    }
                ).putExtra("tredid", tredid).putExtra("name", title)
                    .putExtra("mobileNumber", phoneNumber).putExtra(
                    "isgroupmessage",
                    isgroupmessage
                )
            )

        }
        Constants.isActivitychange = true


    }

    override fun onClickMenu(
        position: Int,
        list: ArrayList<Conversation>,
        holder: MainMassageAdapter.MainMassageAdapterViewHolder
    ) {

    }

    override fun onpositive(whatfordialog: String) {
        when (whatfordialog) {
            "delete" -> {
                commanDeleteBlockDialog?.dismiss()
                deleteMessage()
            }

            else -> {
                commanDeleteBlockDialog?.dismiss()
            }
        }
    }

    override fun onnegative(whatfordialog: String) {
        when (whatfordialog) {
            "delete" -> {
                commanDeleteBlockDialog?.dismiss()
                selectionRemove()
            }

            else -> {
                commanDeleteBlockDialog?.dismiss()
            }
        }
    }

    private fun showcommandialog(
        dialogtital: String,
        dialogmessage: String,
        positivebutton: String,
        negativebutton: String,
        whatfordialog: String
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

    override fun onStart() {
        super.onStart()
        setImageNotFound()
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
        binding.mainMenuAr.setBackgroundColor(surfaceColor)
        binding.toolBarMain.setBackgroundColor(surfaceColor)
        binding.defaulttoolshow.setBackgroundColor(surfaceColor)
        binding.selectioncontenar.setBackgroundColor(surfaceColor)
        binding.conversationsFastscroller.setBackgroundColor(surfaceColor)
        binding.allmassagerecycler.setBackgroundColor(surfaceColor)
        binding.textView3.setTextColor(textColor)
        binding.selectioncount.setTextColor(textColor)
        binding.nomessagefound.setTextColor(secondaryTextColor)

//        binding.backBtn.iconTint = ColorStateList.valueOf(textColor)
        val iconTint = ColorStateList.valueOf(secondaryTextColor)
        listOf(
            binding.backBtn,
            binding.moreBtn,
            binding.messageArchive,
            binding.messageDelete,
            binding.messageSelectionClose,
            binding.messageSelectAll
        ).forEach { icon ->
            icon.imageTintList = iconTint
        }

        binding.conversationsFastscroller.updateColors(primaryColor)

        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        window.decorView.setBackgroundColor(surfaceColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightSystemBars = ThemeModeManager.shouldUseLightSystemBars(this@PrivacyChatActivity)
            isAppearanceLightStatusBars = useLightSystemBars
            isAppearanceLightNavigationBars = useLightSystemBars
        }
    }

    private fun setImageNotFound() {
        val imageRes = if (ThemeModeManager.isDarkThemeActive(this)) {
            R.drawable.no_private_chat_found_three
        } else {
            R.drawable.no_private_chat_found
        }
        binding.imageView15.setImageResource(imageRes)
    }
}
