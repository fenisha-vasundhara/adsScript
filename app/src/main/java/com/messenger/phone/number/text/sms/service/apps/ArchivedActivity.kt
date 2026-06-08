package com.messenger.phone.number.text.sms.service.apps

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.res.ColorStateList
import android.os.Build
import android.provider.Telephony
import android.util.Log
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors
import com.demo.adsmanage.Commen.Constants
import com.demo.adsmanage.Commen.firebaseEventMain
import com.demo.adsmanage.basemodule.BaseSharedPreferences
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
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
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.handleDefaultSmsClick_1
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isfisttimead
import com.messenger.phone.number.text.sms.service.apps.CommanClass.markThreadAsRead
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.Dialog.CommanDeleteBlockDialog
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
import com.messenger.phone.number.text.sms.service.apps.adapter.MainMassageAdapter
import com.messenger.phone.number.text.sms.service.apps.ads.v2.AdsOrchestrator


import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityArchivedBinding
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.CommanDeleteBlockDialogInterface
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MainMessageClick
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MessageClick
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MoreOPtionClick
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversationbin
import com.messenger.phone.number.text.sms.service.apps.sms.SmsUtils.deleteConversation
import com.messenger.phone.number.text.sms.service.apps.viewModel.GetAllConversationArchivedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ArchivedActivity : AppCompatActivity(), MessageClick, MainMessageClick, MoreOPtionClick,
    CommanDeleteBlockDialogInterface {

    lateinit var binding: ActivityArchivedBinding

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    @Inject
    lateinit var adapterMainMassage: MainMassageAdapter

    lateinit var model: GetAllConversationArchivedViewModel

    private var convolist2: ArrayList<Conversation> = arrayListOf()

    var selecteditemmain: ArrayList<String> = arrayListOf()

    private lateinit var mProgressDialog: ProgressDialog
    private var commanDeleteBlockDialog: CommanDeleteBlockDialog? = null

    @Inject
    lateinit var repo: MessagerDatabaseRepo

    var cout = 0
    var selecteditemmainpin: ArrayList<Conversation> = arrayListOf()
    private val adsOrchestrator by lazy { AdsOrchestrator.get(application) }


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocal()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_archived)
        setBaseTheme(binding.vAnd15StatusBar)
        this.firebaseEventMain("Archive")
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

        binding.conversationsFastscroller.updateColors(getProperPrimaryColor())
        binding.adapter = adapterMainMassage
        adapterMainMassage.setHasStableIds(true)
        adapterMainMassage.isselectedAdapter = false
        adapterMainMassage.ismoreoption = true
        adapterMainMassage.setInterface(this@ArchivedActivity)
        adapterMainMassage.setInterfaceMoreClick(this@ArchivedActivity)
        adapterMainMassage.setInterfaceMainClick(this@ArchivedActivity)
        setImageNotFound()

        mProgressDialog = ProgressDialog(this, R.style.Dialog_Custom)
        mProgressDialog.setCancelable(false)
        mProgressDialog.setMessage("Loading...")


        model = ViewModelProvider(this)[GetAllConversationArchivedViewModel::class.java]

        model.GetAllConversationlivelist.observe(this, Observer {
            try {
                convolist2.clear()
                convolist2 = ArrayList(it.distinctBy { it.threadId } as ArrayList<Conversation>)
                adapterMainMassage.updateList(convolist2)
                if (convolist2.isEmpty()) {
                    binding.selectAllMessage.gone()
                } else {
                    binding.selectAllMessage.gone()
                }
                removepin()
                binding.nomessagefoundchack = it.isEmpty()
            } catch (_: Exception) {
            }
        })

        binding.messageArchive.setOnClickListener {
            Snackbar.make(
                binding.root,
                "${selecteditemmain.size} " + resources.getString(R.string.conversation_unarchived),
                Snackbar.LENGTH_LONG
            )
                .setAction("Undo") {
                    Log.d("TAG", "Try to undo ticket")
                    addarchiveContact()

                }
                .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onDismissed(
                        transientBottomBar: Snackbar?,
                        event: Int,
                    ) {
                        super.onDismissed(transientBottomBar, event)
                        if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                            selectionRemove()
                        }
                    }
                })
                .show()
            try {
                removearchiveContact()
            } catch (_: Exception) {
            }
        }
        binding.messageDelete.setOnClickListener {

            if (packageName == Telephony.Sms.getDefaultSmsPackage(this@ArchivedActivity)) {
            showcommandialog(
                dialogtital = resources.getString(R.string.Delete_this_conversation),
                dialogmessage = resources.getString(R.string.This_is_permanent),
                positivebutton = resources.getString(R.string.Delete),
                negativebutton = resources.getString(R.string.cancel),
                "delete"
            )
            }else{
                handleDefaultSmsClick_1(this@ArchivedActivity)
            }
        }
        binding.messageSelectionClose.setOnClickListener {
            selectionRemove()
        }

        binding.backBtn.setOnClickListener {
            handleExitWithInterstitial()
        }

        binding.selectAllMessage.setOnClickListener { selectAllArchivedMessages() }
        binding.messageSelectAll.setOnClickListener { selectAllArchivedMessages() }
    }

    override fun onBackPressed() {
        handleExitWithInterstitial()
    }

    private fun handleExitWithInterstitial() {
        adsOrchestrator.showArchiveExitInterstitial(this) {
            finish()
        }
    }

    private fun selectAllArchivedMessages() {
        CoroutineScope(Dispatchers.IO).launch {
            this@ArchivedActivity.selecteditemmainpin.clear()
            selecteditemmainpin.addAll(convolist2)
            adapterMainMassage.selecteditem.clear()
            adapterMainMassage.selecteditem.addAll(convolist2)

            this@ArchivedActivity.selecteditemmain.clear()
            adapterMainMassage.selecteditem.forEach {
                this@ArchivedActivity.selecteditemmain.add(it.threadId.toString())
            }
            runOnUiThread {
                binding.isselection = adapterMainMassage.selecteditem.isNotEmpty()
                ("${adapterMainMassage.selecteditem.size} " + resources.getString(R.string.selected)).also {
                    binding.selectioncount.text = it
                }
            }
            runOnUiThread {
                adapterMainMassage.notifyDataSetChanged()
            }
        }
    }

    private fun setImageNotFound() {
        val imageRes = if (ThemeModeManager.isDarkThemeActive(this)) {
            R.drawable.archivedmessage_bg_three
        } else {
            R.drawable.archivedmessage_bg
        }
        binding.imageView15.setImageResource(imageRes)
    }

    private fun removepin() {
        CoroutineScope(Dispatchers.IO).launch {
            config.removePinnedConversations(convolist2)
        }
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
                try{
                repo.deleteConversationRepo(selecteditemmain)

            }catch (e: Exception){ }


            CoroutineScope(Dispatchers.Main).launch {
                    mProgressDialog.dismiss()
                    selectionRemove()
                }
            }
        }
    }

    private fun removearchiveContact() {
        if (selecteditemmain.isEmpty()) {
            selectionRemove()
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                repo.removearchivConversationRepo(selecteditemmain)
//                selecteditemmain.forEachIndexed { index, conversation ->
//                    conversation.let {
//
//                    }
//                }
                CoroutineScope(Dispatchers.Main).launch {
                    binding.isselection = false
                    adapterMainMassage.notifyDataSetChanged()
                }
            }
        }
    }

    private fun addarchiveContact() {
        if (selecteditemmain.isEmpty()) {
            selectionRemove()
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                repo.archivConversationRepo(selecteditemmain)
//                selecteditemmain.forEachIndexed { index, conversation ->
//                    conversation.let {
//                        repo.archivConversationRepo(it.toLong())
//                    }
//                }
                CoroutineScope(Dispatchers.Main).launch {
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
        sendMessagerActivity(tredid, title, phoneNumber, pos, list)

    }

    private fun sendMessagerActivity(
        tredid: Long?,
        title: String,
        phoneNumber: String,
        position: Int,
        list: ArrayList<Conversation>,
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
                            this@ArchivedActivity, if (config.Message_Send_Activity == "1") {
                                SendMessageActivity::class.java
                            } else {
                                SendMessageActivity::class.java
                            }
                        )
                            .putExtra("tredid", tredid)
                            .putExtra("name", title)
                            .putExtra("mobileNumber", phoneNumber)
                            .putExtra("isgroupmessage", list[position].isgroupmessage)
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
                )
                    .putExtra("tredid", tredid)
                    .putExtra("name", title)
                    .putExtra("mobileNumber", phoneNumber)
                    .putExtra("isgroupmessage", list[position].isgroupmessage)
            )

        }
        Constants.isActivitychange = true


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
        binding.defaulttoolshow.setBackgroundColor(surfaceColor)
        binding.selectioncontenar.setBackgroundColor(surfaceColor)
        binding.conversationsFastscroller.setBackgroundColor(surfaceColor)
        binding.allmassagerecycler.setBackgroundColor(surfaceColor)
        binding.textView3.setTextColor(textColor)
        binding.selectioncount.setTextColor(textColor)
        binding.nomessagefound.setTextColor(secondaryTextColor)

        binding.backBtn.setIconResource(R.drawable.recycle_bin_material_ic_arrow_back_rounded)
        binding.selectAllMessage.setIconResource(R.drawable.home_material_ic_select_all_rounded)
        binding.messageSelectAll.setIconResource(R.drawable.home_material_ic_select_all_rounded)
        binding.messageArchive.setIconResource(R.drawable.baseline_unarchive_24)
        binding.messageDelete.setIconResource(R.drawable.home_material_ic_delete_rounded)
        binding.messageSelectionClose.setIconResource(R.drawable.home_material_ic_close_rounded)

        val backIconTint = ColorStateList.valueOf(textColor)
        val actionIconTint = ColorStateList.valueOf(secondaryTextColor)
        val rippleAlpha = if (ThemeModeManager.isDarkThemeActive(this)) 0.24f else 0.12f
        val rippleColor = ColorStateList.valueOf(MaterialColors.layer(surfaceColor, textColor, rippleAlpha))

        binding.backBtn.iconTint = backIconTint
        binding.backBtn.rippleColor = rippleColor
        applyToolbarActionButtons(actionIconTint, rippleColor, listOf(
            binding.selectAllMessage,
            binding.messageSelectAll,
            binding.messageArchive,
            binding.messageDelete,
            binding.messageSelectionClose
        ))

        binding.conversationsFastscroller.updateColors(primaryColor)

        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        window.decorView.setBackgroundColor(surfaceColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightSystemBars = ThemeModeManager.shouldUseLightSystemBars(this@ArchivedActivity)
            isAppearanceLightStatusBars = useLightSystemBars
            isAppearanceLightNavigationBars = useLightSystemBars
        }

        setImageNotFound()
//        Banner.loadBannerArchive(this, binding.bannerAds) {
//            if (it) {
//                binding.bannerAds.visible()
//            } else {
//                binding.bannerAds.gone()
//            }
//        }
    }

    private fun applyToolbarActionButtons(
        iconTint: ColorStateList,
        rippleColor: ColorStateList,
        buttons: List<MaterialButton>,
    ) {
        buttons.forEach { button ->
            button.iconTint = iconTint
            button.rippleColor = rippleColor
            button.backgroundTintList = ColorStateList.valueOf(android.graphics.Color.TRANSPARENT)
        }
    }

}
