package com.messenger.phone.number.text.sms.service.apps

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.InsetDrawable
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.demo.adsmanage.Commen.Constants
import com.demo.adsmanage.Commen.firebaseEventMain
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.applyFontToMenuItem
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
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.handleDefaultSmsClick_1
import com.messenger.phone.number.text.sms.service.apps.CommanClass.insertNewSMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.Dialog.CommanDeleteBlockDialog
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
import com.messenger.phone.number.text.sms.service.apps.adapter.MainMassageAdapter
import com.messenger.phone.number.text.sms.service.apps.ads.v2.AdsOrchestrator
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityRecycleBinBinding
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.CommanDeleteBlockDialogInterface
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MainMessageClick
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MessageClick
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MoreOPtionClick
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversationbin
import com.messenger.phone.number.text.sms.service.apps.viewModel.GetAllConversationDeleteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class RecycleBinActivity : AppCompatActivity(), MessageClick, MainMessageClick, MoreOPtionClick,
    CommanDeleteBlockDialogInterface {
    lateinit var binding: ActivityRecycleBinBinding

    @Inject
    lateinit var adapterMainMassage: MainMassageAdapter
    private var commanDeleteBlockDialog: CommanDeleteBlockDialog? = null
    lateinit var model: GetAllConversationDeleteViewModel
    private val ICON_MARGIN = 8

    private var convolist2: ArrayList<Conversation> = arrayListOf()
    private var Allconvolist2: ArrayList<Conversationbin> = arrayListOf()

    var selecteditemmain: ArrayList<String> = arrayListOf()

    private lateinit var mProgressDialog: ProgressDialog

    @Inject
    lateinit var repo: MessagerDatabaseRepo

    var cout = 0

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f
    private val adsOrchestrator by lazy { AdsOrchestrator.get(application) }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocal()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recycle_bin)
        setBaseTheme(binding.vAnd15StatusBar)
        adapterMainMassage.newmessagecountshow = false
        this.firebaseEventMain("Recycle_Bin")


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

        binding.conversationsFastscroller.updateColors(resources.getColor(R.color.procolor, theme))
        binding.adapter = adapterMainMassage
        adapterMainMassage.setHasStableIds(true)
        adapterMainMassage.isselectedAdapter = false
        adapterMainMassage.ismoreoption = true
        adapterMainMassage.setInterface(this@RecycleBinActivity)
        adapterMainMassage.setInterfaceMoreClick(this@RecycleBinActivity)
        adapterMainMassage.setInterfaceMainClick(this@RecycleBinActivity)

        mProgressDialog = ProgressDialog(this, R.style.Dialog_Custom)
        mProgressDialog.setCancelable(false)
        mProgressDialog.setMessage("Loading...")

        model = ViewModelProvider(this)[GetAllConversationDeleteViewModel::class.java]

        model.GetAllConversationlivelist.observe(this, Observer {
            try {
                convolist2.clear()
                Allconvolist2.clear()
                Allconvolist2.addAll(it)
                ArrayList(it.distinctBy { it.threadId } as ArrayList<Conversationbin>).forEachIndexed { index, conversation ->
                    convolist2.add(
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
                adapterMainMassage.updateList(convolist2)
                binding.nomessagefoundchack = it.isEmpty()
                if (it.isEmpty()) {
                    binding.moreBtn.gone()
                    binding.moreBtn2.gone()
                } else {
                    binding.moreBtn.visible()
                    binding.moreBtn2.gone()
                }
            } catch (_: Exception) {
            }
        })

        binding.selectedMoreBtn.setOnClickListener {
            if (packageName == Telephony.Sms.getDefaultSmsPackage(this@RecycleBinActivity)) {

            showcommandialog(
                dialogtital = resources.getString(R.string.Delete_this_conversation),
                dialogmessage = resources.getString(R.string.This_is_permanent),
                positivebutton = resources.getString(R.string.Delete),
                negativebutton = resources.getString(R.string.cancel),
                "delete"
            )}else{
                handleDefaultSmsClick_1(this@RecycleBinActivity)
            }
        }

        binding.moreBtn.setOnClickListener {
            if (packageName == Telephony.Sms.getDefaultSmsPackage(this@RecycleBinActivity)) {  showcommandialog(
                dialogtital = resources.getString(R.string.Delete),
                dialogmessage = resources.getString(R.string.This_is_permanent_re_all_delete),
                positivebutton = resources.getString(R.string.Yes),
                negativebutton = resources.getString(R.string.No),
                "alldelete"
            )}else{
                handleDefaultSmsClick_1(this@RecycleBinActivity)
            }
        }

        binding.moreBtn2.setOnClickListener {
            showMenu(it, R.menu.recylerbin_default_menu, false)
        }

        binding.selectedMoreBtn2.setOnClickListener {
            showMenu(it, R.menu.recylerbin_default_menu, true)
        }

        binding.messageSelectionClose.setOnClickListener {
            selectionRemove()
        }

        binding.backBtn.setOnClickListener {
            handleExitWithInterstitial()
        }

    }

    override fun onBackPressed() {
        handleExitWithInterstitial()
    }

    private fun handleExitWithInterstitial() {
        adsOrchestrator.showRecycleExitInterstitial(this) {
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("RestrictedApi", "ResourceType")
    private fun showMenu(v: View, @MenuRes menuRes: Int, forselcted: Boolean) {
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
                    restoreallmessage(forselcted)
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

    private fun restoreallmessage(forselcted: Boolean) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                runOnUiThread {
                    mProgressDialog.show()
                }
                if (forselcted) {
                    Allconvolist2.clear()
                    this@RecycleBinActivity.selecteditemmain.forEachIndexed { index, s ->
                        val recyclerbindat = repo.getUserMessageRecyListChacktredrepo(s.toLong())
                        Allconvolist2.addAll(recyclerbindat)
                    }

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
                        repo.insertmessage(
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
                        repo.insertmessage(
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
                this@RecycleBinActivity.selecteditemmain.clear()
                Allconvolist2.forEachIndexed { index, conversation ->
                    this@RecycleBinActivity.selecteditemmain.add(conversation.threadId.toString())
                }
                repo.deleteConversationRecyclerbinRepo(this@RecycleBinActivity.selecteditemmain)
            }
            withContext(Dispatchers.Main) {
                mProgressDialog.dismiss()
                if (forselcted) {
                    selectionRemove()
                }
            }
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
        selecteditem: ArrayList<Conversation>,
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
        position: Int,
    ) {
        Constants.isActivitychange = true
        CoroutineScope(Dispatchers.IO).launch {
            sendMessagerActivity(
                tredid,
                title,
                phoneNumber,
                holder.itemView,
                list,
                position
            )
        }

    }

    private fun sendMessagerActivity(
        tredid: Long?,
        title: String,
        phoneNumber: String,
        itemView: View,
        list: ArrayList<Conversation>,
        position: Int,
    ) {
        startActivity(
            Intent(
                this@RecycleBinActivity, RecycleBinSendMessageActivity::class.java
            ).putExtra("tredid", tredid).putExtra("name", title)
                .putExtra("mobileNumber", phoneNumber)
                .putExtra("isgroupmessage", list[position].isgroupmessage)
        )
    }

    override fun onClickMenu(
        position: Int,
        list: ArrayList<Conversation>,
        holder: MainMassageAdapter.MainMassageAdapterViewHolder,
    ) {

    }

    override fun onpositive(whatfordialog: String) {
        when (whatfordialog) {
            "delete" -> {
                commanDeleteBlockDialog?.dismiss()
                deleteMessage()
            }

            "alldelete" -> {
                commanDeleteBlockDialog?.dismiss()
                GlobalScope.launch {
                    withContext(Dispatchers.IO) {
                        this@RecycleBinActivity.selecteditemmain.clear()
                        runOnUiThread { mProgressDialog.show() }
                        adapterMainMassage.list.forEachIndexed { index, conversation ->
                            this@RecycleBinActivity.selecteditemmain.add(conversation.threadId.toString())
                        }
                        repo.deleteConversationRecyclerbinRepo(this@RecycleBinActivity.selecteditemmain)
                    }
                    withContext(Dispatchers.Main) {
                        mProgressDialog.dismiss()
                        this@RecycleBinActivity.selecteditemmain.clear()
                    }
                }
            }

            else -> {
                commanDeleteBlockDialog?.dismiss()
            }
        }
    }

    private fun deleteMessage() {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                runOnUiThread { mProgressDialog.show() }
                repo.deleteConversationRecyclerbinRepo(this@RecycleBinActivity.selecteditemmain)
            }
            withContext(Dispatchers.Main) {
                mProgressDialog.dismiss()
                selectionRemove()
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

        binding.main.setBackgroundColor(surfaceColor)
        binding.vAnd15StatusBar.setBackgroundColor(statusBarColor)
        binding.mainMenuAr.setBackgroundColor(surfaceColor)
        binding.toolBarMain.setBackgroundColor(surfaceColor)
        binding.conversationsFastscroller.setBackgroundColor(surfaceColor)
        binding.allmassagerecycler.setBackgroundColor(surfaceColor)
        binding.defaulttoolshow.setBackgroundColor(surfaceColor)
        binding.selectioncontenar.setBackgroundColor(surfaceColor)
        binding.nomessagefound.setTextColor(secondaryTextColor)
        binding.textView3.setTextColor(textColor)
        binding.selectioncount.setTextColor(textColor)
        binding.backBtn.iconTint = android.content.res.ColorStateList.valueOf(secondaryTextColor)
        binding.moreBtn.iconTint = android.content.res.ColorStateList.valueOf(secondaryTextColor)
        binding.moreBtn2.iconTint = android.content.res.ColorStateList.valueOf(secondaryTextColor)
        binding.messageSelectionClose.iconTint = android.content.res.ColorStateList.valueOf(secondaryTextColor)
        binding.selectedMoreBtn.iconTint = android.content.res.ColorStateList.valueOf(secondaryTextColor)
        binding.selectedMoreBtn2.iconTint = android.content.res.ColorStateList.valueOf(secondaryTextColor)
        binding.conversationsFastscroller.updateColors(primaryColor)

        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        window.decorView.setBackgroundColor(surfaceColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightSystemBars = ThemeModeManager.shouldUseLightSystemBars(this@RecycleBinActivity)
            isAppearanceLightStatusBars = useLightSystemBars
            isAppearanceLightNavigationBars = useLightSystemBars
        }
    }
}
