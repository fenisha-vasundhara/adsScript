package com.messenger.phone.number.text.sms.service.apps

import android.app.ProgressDialog
import android.app.role.RoleManager
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.WindowCompat
import android.provider.BlockedNumberContract
import android.provider.Telephony
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.demo.adsmanage.Commen.Constants
import com.demo.adsmanage.Commen.firebaseEventMain
import com.demo.adsmanage.basemodule.BaseSharedPreferences
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
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isfisttimead
import com.messenger.phone.number.text.sms.service.apps.CommanClass.markThreadAsRead
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
import com.messenger.phone.number.text.sms.service.apps.adapter.MainMassageAdapter

import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityBlockNumberBinding
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MainMessageClick
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MessageClick
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MoreOPtionClick
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.viewModel.GetAllConversationBlockViewModel
import com.simplemobiletools.commons.extensions.deleteBlockedNumber
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BlockNumberActivity : AppCompatActivity(), MessageClick, MainMessageClick, MoreOPtionClick {

    lateinit var binding: ActivityBlockNumberBinding

    private lateinit var mProgressDialog: ProgressDialog

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    var cout = 0

    var selecteditemmain: ArrayList<String> = arrayListOf()
    var selectedThreadIds: ArrayList<Long> = arrayListOf()

    @Inject
    lateinit var adapterMainMassage: MainMassageAdapter

    @Inject
    lateinit var repo: MessagerDatabaseRepo

    lateinit var model: GetAllConversationBlockViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocal()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_block_number)
        setBaseTheme(binding.vAnd15StatusBar)
        this.firebaseEventMain("Block_Number")
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



        config.isblocknumberscreenopen = true
        binding.conversationsFastscroller.updateColors(getProperPrimaryColor())
        binding.adapter = adapterMainMassage
        adapterMainMassage.setHasStableIds(true)
        adapterMainMassage.isselectedAdapter = false
        adapterMainMassage.ismoreoption = true
        adapterMainMassage.setInterface(this@BlockNumberActivity)
        adapterMainMassage.setInterfaceMoreClick(this@BlockNumberActivity)
        adapterMainMassage.setInterfaceMainClick(this@BlockNumberActivity)

        mProgressDialog = ProgressDialog(this, R.style.Dialog_Custom)
        mProgressDialog.setCancelable(false)
        mProgressDialog.setMessage(resources.getString(R.string.Loading))

        model = ViewModelProvider(this)[GetAllConversationBlockViewModel::class.java]

        model.GetAllConversationlivelist.observe(this, Observer {
            try {
                adapterMainMassage.updateList(ArrayList(it.distinctBy { it.threadId } as ArrayList<Conversation>))
                binding.nomessagefoundchack = it.isEmpty()
            } catch (_: Exception) {
            }
        })

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.messageSelectionClose.setOnClickListener {
            selectionRemove()
        }

        binding.messageUnblock.setOnClickListener {
            unBlockMessage()
        }

    }

    private fun unBlockMessage() {

        if (selecteditemmain.isEmpty()) {
            selectionRemove()
        } else {
            if (!canModifySystemBlockedNumbers()) {

                    selectionRemove()


                return
            }


            mProgressDialog.show()
            CoroutineScope(Dispatchers.IO).launch {

                    selecteditemmain.forEach { phoneNumber ->
                        deleteBlockedNumber(phoneNumber)
                    }
                    selectedThreadIds.forEach { threadId ->
                        repo.removenumbertoblockRepo(threadId)
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        mProgressDialog.dismiss()
                        selectionRemove()
                    }


            }
        }

    }
    private fun canModifySystemBlockedNumbers(): Boolean {
        return BlockedNumberContract.canCurrentUserBlockNumbers(this) &&
                (isDefaultSmsApp() )
    }

    private fun isDefaultSmsApp(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(RoleManager::class.java) ?: return false
            return roleManager.isRoleHeld(RoleManager.ROLE_SMS)
        }

        return  Telephony.Sms.getDefaultSmsPackage(this) == packageName
    }
    override fun onMainClick(position: Int, list: ArrayList<Conversation>, selecteditem: ArrayList<Conversation>) {
        this.selecteditemmain.clear()
        this.selectedThreadIds.clear()
        selecteditem.forEach {
            this.selecteditemmain.add(it.phoneNumber)
            this.selectedThreadIds.add(it.threadId!!)
        }
        binding.isselection = selecteditem.isNotEmpty()
        ("${selecteditem.size} " + resources.getString(R.string.selected)).also { binding.selectioncount.text = it }
    }

    override fun onClick(tredid: Long?, pos: Int, title: String, phoneNumber: String, holder: MainMassageAdapter.MainMassageAdapterViewHolder, list: ArrayList<Conversation>, position: Int) {

        Constants.isActivitychange = true
//        if (cout >= 2 || !isfisttimead) {
//            isfisttimead = true
//            cout++
//            AdsManage.ActivityBuilder().Load_InterstitialAd_Image_Click(this, BaseSharedPreferences(this).mIS_SUBSCRIBED!!, object : OnInterAdsLoadAds {
//                override fun OnLoadAds() {
//                    AdsManage.ActivityBuilder().Show_InterstitialAds_Image_Click(this@BlockNumberActivity, BaseSharedPreferences(this@BlockNumberActivity).mIS_SUBSCRIBED!!, object : OnInterstitialAdsapp {
//                        override fun OnDismissAds()  {
//                            cout = 0
//                            sendMessagerActivity(tredid, title, phoneNumber)
//                        }
//
//                        override fun OnError() {
//                            sendMessagerActivity(tredid, title, phoneNumber)
//                        }
//                    })
//                }
//
//                override fun OnError() {
//                    sendMessagerActivity(tredid, title, phoneNumber)
//                }
//            })
//        } else {
//            cout++
//            sendMessagerActivity(tredid, title, phoneNumber)
//        }
        sendMessagerActivity(tredid, title, phoneNumber)
    }

    private fun sendMessagerActivity(tredid: Long?, title: String, phoneNumber: String) {


        CoroutineScope(Dispatchers.IO).launch {
            repo.setisoldmessageRepo(false, tredid!!)
            repo.setisoldmessageCountRepo(0, tredid)
            markThreadAsRead(tredid)
        }

        if (cout >= 2 || !isfisttimead) {
            val isSubscribed = BaseSharedPreferences(this).mIS_SUBSCRIBED!!

            isfisttimead = true
            cout++

                    cout = 0
                    startActivity(
                        Intent(this@BlockNumberActivity, if (config.Message_Send_Activity == "1") {
                            SendMessageActivity::class.java
                        } else {
                            SendMessageActivity::class.java
                        }).putExtra("tredid", tredid).putExtra("name", title).putExtra("mobileNumber", phoneNumber)
                    )

        } else {
            cout++
            startActivity(
                Intent(this@BlockNumberActivity, if (config.Message_Send_Activity == "1") {
                    SendMessageActivity::class.java
                } else {
                    SendMessageActivity::class.java
                }).putExtra("tredid", tredid).putExtra("name", title).putExtra("mobileNumber", phoneNumber)
            )
        }
        Constants.isActivitychange = true


    }

    override fun onClickMenu(position: Int, list: ArrayList<Conversation>, holder: MainMassageAdapter.MainMassageAdapterViewHolder) {

    }

    private fun selectionRemove() {
        binding.isselection = false
        selecteditemmain.clear()
        selectedThreadIds.clear()
        adapterMainMassage.selecteditem.clear()
        adapterMainMassage.notifyDataSetChanged()
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
        binding.defaulttoolshow.setBackgroundColor(surfaceColor)
        binding.selectioncontenar.setBackgroundColor(surfaceColor)
        binding.conversationsFastscroller.setBackgroundColor(surfaceColor)
        binding.allmassagerecycler.setBackgroundColor(surfaceColor)
        binding.textView3.setTextColor(textColor)
        binding.selectioncount.setTextColor(textColor)
        binding.nomessagefound.setTextColor(secondaryTextColor)
        binding.backBtn.iconTint = ColorStateList.valueOf(secondaryTextColor)
        binding.messageUnblock.iconTint = ColorStateList.valueOf(secondaryTextColor)
        binding.messageSelectionClose.iconTint = ColorStateList.valueOf(secondaryTextColor)
        binding.conversationsFastscroller.updateColors(primaryColor)

        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        window.decorView.setBackgroundColor(surfaceColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightSystemBars = ThemeModeManager.shouldUseLightSystemBars(this@BlockNumberActivity)
            isAppearanceLightStatusBars = useLightSystemBars
            isAppearanceLightNavigationBars = useLightSystemBars
        }
    }



}
