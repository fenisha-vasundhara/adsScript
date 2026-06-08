package com.messenger.phone.number.text.sms.service.apps

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.res.ColorStateList
import android.os.Build
import android.util.Log
import androidx.core.view.WindowCompat
import androidx.core.content.PermissionChecker
import androidx.databinding.DataBindingUtil
import com.demo.adsmanage.Commen.Constants
import com.demo.adsmanage.Commen.firebaseEventMain
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.allresultlist
import com.messenger.phone.number.text.sms.service.apps.CommanClass.allresultlistcontact
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.deletedmessage
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getBottomNavigationBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperSecondaryTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperStatusBarColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getThreadId
import com.messenger.phone.number.text.sms.service.apps.CommanClass.higlitetetxt
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
import com.messenger.phone.number.text.sms.service.apps.adapter.ContactSearchAdapter
import com.messenger.phone.number.text.sms.service.apps.adapter.MessageSearchAdapter
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivitySearchAllResultBinding
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.ContactNumberClick
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.MessageSearchAdapterClickInterface
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.modelClass.Recentsearch
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class SearchAllResultActivity : AppCompatActivity(), MessageSearchAdapterClickInterface,
    ContactNumberClick {

    lateinit var binding: ActivitySearchAllResultBinding


    private var iscon: Boolean = true

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    @Inject
    lateinit var contactSearchAdapter: ContactSearchAdapter

    @Inject
    lateinit var adapter: MessageSearchAdapter

    @Inject
    lateinit var repo: MessagerDatabaseRepo

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocal()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_all_result)
        this.firebaseEventMain("Search_All_Message_View_More")

        setBaseTheme(binding.vAnd15StatusBar)

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

        iscon = intent.getBooleanExtra("iscon", true)

        binding.backBtn.setOnClickListener {
            Constants.isActivitychange = true
            finish()
        }


        if (iscon) {
            binding.serchallresult.adapter = contactSearchAdapter
            contactSearchAdapter.isThreeItem = false
            contactSearchAdapter.list = allresultlistcontact
            contactSearchAdapter.highlighttext = higlitetetxt
            contactSearchAdapter.setContactclick(this)
        } else {
            binding.serchallresult.adapter = adapter
            adapter.isThreeItem = false
            adapter.list = allresultlist
            adapter.highlighttext = higlitetetxt
            adapter.setInterface(this)
        }

        if (iscon) {
            binding.messagecount.text = resources.getString(R.string.Contact)
            binding.messagecount2.text = "(" + allresultlistcontact.size.toString() + ")"
        } else {
            binding.messagecount.text = resources.getString(R.string.Message)
            binding.messagecount2.text = "(" + allresultlist.size.toString() + ")"
        }

    }

    override fun MessageSearchAdapterOnClick(position: Int, list: ArrayList<Conversation>) {
        if (position < 0 || position >= list.size) {
            return
        }

        Constants.isActivitychange = true
        addrecentsearch()
        CoroutineScope(Dispatchers.IO).launch {
            list[position].messageId?.toLong()?.let { repo.addmessagefoundRepo(it) }
        }
        startActivity(
            Intent(
                this, if (config.Message_Send_Activity == "1") {
                    SendMessageActivity::class.java
                } else {
                    SendMessageActivity::class.java
                }
            ).putExtra("tredid", list[position].threadId).putExtra("name", list[position].title)
                .putExtra("isSearchFound", true)
                .putExtra("isSearchFoundmessage", list[position].snippet)
                .putExtra("isSearchFoundMessageId", list[position].messageId ?: -1L)
                .putExtra("mobileNumber", list[position].phoneNumber)
        )
    }

    override fun onClick(mobilenumber: String, pos: Int, name: String) {
        Constants.isActivitychange = true
        if (chackPermission()) {
            addrecentsearch()
            startActivity(
                Intent(
                    this@SearchAllResultActivity, if (config.Message_Send_Activity == "1") {
                        SendMessageActivity::class.java
                    } else {
                        SendMessageActivity::class.java
                    }
                ).putExtra("tredid", getThreadId(mobilenumber)).putExtra("name", name)
                    .putExtra("mobileNumber", mobilenumber)
            )
        }
    }

    override fun OnLongClick() {

    }

    fun chackPermission(): Boolean {
        return PermissionChecker.checkSelfPermission(
            this, android.Manifest.permission.SEND_SMS
        ) == PermissionChecker.PERMISSION_GRANTED
    }

    fun addrecentsearch() {
        CoroutineScope(Dispatchers.IO).launch {
            if (!repo.isrecentsearchExitsRepo(higlitetetxt)) {
                repo.addrecentsearchRepo(Recentsearch(recentsearch = higlitetetxt))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!iscon) {
            if (deletedmessage.isNotEmpty()) {
                GlobalScope.launch {
                    withContext(Dispatchers.IO) {
                        deletedmessage.forEachIndexed { index, conversation ->
                            Log.d(
                                "deletedmessage",
                                "onResume: deletedmessage <--------> 1 <--> ${allresultlist.size}"
                            )
                            val data =
                                allresultlist.filter { it.messageId == conversation.messageId }
                            allresultlist.removeAll(data)
                        }
                    }
                    withContext(Dispatchers.Main) {
                        Log.d(
                            "deletedmessage",
                            "onResume: deletedmessage <--------> 2 <--> ${allresultlist.size}"
                        )
                        adapter.list = allresultlist
                    }
                }
            }
        }
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

        binding.main.setBackgroundColor(surfaceColor)
        binding.vAnd15StatusBar.setBackgroundColor(statusBarColor)
        binding.toolbar.setBackgroundColor(surfaceColor)
        binding.serchallresult.setBackgroundColor(surfaceColor)
        binding.backBtn.iconTint = ColorStateList.valueOf(secondaryTextColor)
        binding.messagecount.setTextColor(textColor)
        binding.messagecount2.setTextColor(secondaryTextColor)

        val window = window
        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        window.decorView.setBackgroundColor(surfaceColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightSystemBars =
                ThemeModeManager.shouldUseLightSystemBars(this@SearchAllResultActivity)
            isAppearanceLightStatusBars = useLightSystemBars
            isAppearanceLightNavigationBars = useLightSystemBars
        }
    }

}
