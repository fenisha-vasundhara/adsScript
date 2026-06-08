package com.messenger.phone.number.text.sms.service.apps

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import com.demo.adsmanage.Commen.firebaseEventMain
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
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toArrayList
import com.messenger.phone.number.text.sms.service.apps.Dialog.AddBlockedKeywordDialog
import com.messenger.phone.number.text.sms.service.apps.adapter.ManageBlockedKeywordsAdapter
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityManageBlockedKeywordsBinding
import com.simplemobiletools.commons.extensions.beVisibleIf
import com.simplemobiletools.commons.extensions.getContrastColor
import com.simplemobiletools.commons.extensions.underlineText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ManageBlockedKeywordsActivity : AppCompatActivity() {

    lateinit var binding: ActivityManageBlockedKeywordsBinding


    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    @Inject
    lateinit var manageBlockedKeywordsAdapter: ManageBlockedKeywordsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocal()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_blocked_keywords)
        this.firebaseEventMain("blocked_keyword")
        setBaseTheme(binding.vAnd15StatusBar)
        config.ismanageblocknumberscreenopen = true
        with(binding) {
            binding.manageBlockedKeywordsList.adapter = manageBlockedKeywordsAdapter

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

            updateBlockedKeywords()
            backBtn.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            manageBlockedKeywordsPlaceholder2.apply {
                underlineText()
                setOnClickListener {
                    addOrEditBlockedKeyword()
                }
            }

            addNewKeyword.setOnClickListener {
                addOrEditBlockedKeyword()
            }


            manageBlockedKeywordsAdapter.ActionChange = {
                updateBlockedKeywords()
            }

        }

    }

    private fun updateBlockedKeywords() {
        CoroutineScope(Dispatchers.IO).launch {
            val blockedKeywords = config.blockedKeywords
            runOnUiThread {
                manageBlockedKeywordsAdapter.blockedKeywords = blockedKeywords.toArrayList()
                binding.manageBlockedKeywordsPlaceholder.beVisibleIf(blockedKeywords.isEmpty())
                binding.manageBlockedKeywordsPlaceholder2.beVisibleIf(blockedKeywords.isEmpty())
            }

        }
    }

    private fun addOrEditBlockedKeyword(keyword: String? = null) {
        AddBlockedKeywordDialog(this, keyword) {
            updateBlockedKeywords()
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
        val primaryColor = getProperPrimaryColor()

        binding.main.setBackgroundColor(surfaceColor)
        binding.vAnd15StatusBar.setBackgroundColor(statusBarColor)
        binding.defaulttoolshow.setBackgroundColor(surfaceColor)
        binding.manageBlockedKeywordsWrapper.setBackgroundColor(surfaceColor)
        binding.manageBlockedKeywordsList.setBackgroundColor(surfaceColor)

        binding.textView3.setTextColor(textColor)
        binding.backBtn.iconTint = ColorStateList.valueOf(secondaryTextColor)

        binding.manageBlockedKeywordsPlaceholder.setTextColor(secondaryTextColor)
        binding.manageBlockedKeywordsPlaceholder2.setTextColor(primaryColor)

        binding.addNewKeyword.backgroundTintList = ColorStateList.valueOf(primaryColor)
        binding.addNewKeyword.imageTintList = ColorStateList.valueOf(primaryColor.getContrastColor())

        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        window.decorView.setBackgroundColor(surfaceColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightSystemBars = ThemeModeManager.shouldUseLightSystemBars(this@ManageBlockedKeywordsActivity)
            isAppearanceLightStatusBars = useLightSystemBars
            isAppearanceLightNavigationBars = useLightSystemBars
        }

        manageBlockedKeywordsAdapter.notifyDataSetChanged()
    }
}
