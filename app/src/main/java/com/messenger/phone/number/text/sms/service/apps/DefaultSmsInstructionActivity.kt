package com.messenger.phone.number.text.sms.service.apps

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.MaterialColors
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getContrastColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isDynamicTheme
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isSystemInDarkMode
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setTextSizeFullApp
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityDefaultSmsInstructionBinding

class DefaultSmsInstructionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDefaultSmsInstructionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeModeManager.applyThemeMode(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ThemeModeManager.getThemeMode(this) == ThemeModeManager.MODE_SYSTEM
        ) {
            DynamicColors.applyToActivityIfAvailable(this)
        }
        super.onCreate(savedInstanceState)
        setLocal()

        binding = ActivityDefaultSmsInstructionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val size=getTextSizeForeNormal18MS()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.textView3.setTextSizeFullApp(size)
        }
        setBaseTheme(binding.vAnd15StatusBar)
        applyCtaThemeColors()
        applyHintImagesForTheme()

        binding.txtStep3.text = getString(
            R.string.default_sms_instruction_step_3,
            getString(R.string.app_name_)
        )

        binding.backBtn.setOnClickListener { finish() }
        binding.btnOpenSettings.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
    }

    private fun applyCtaThemeColors() {
        val primaryColor: Int
        val onPrimaryColor: Int

        if (isDynamicTheme()) {
            primaryColor = MaterialColors.getColor(binding.btnOpenSettings, R.attr.colorPrimary)
            onPrimaryColor = MaterialColors.getColor(
                binding.btnOpenSettings,
                com.google.android.material.R.attr.colorOnPrimary
            )
        } else {
            primaryColor = getProperPrimaryColor()
            onPrimaryColor = primaryColor.getContrastColor()
        }

        binding.btnOpenSettings.backgroundTintList = ColorStateList.valueOf(primaryColor)
        binding.btnOpenSettings.setTextColor(onPrimaryColor)
    }

    private fun applyHintImagesForTheme() {
        val useNightHints = if (isDynamicTheme()) {
            isSystemInDarkMode()
        } else {
            ThemeModeManager.isDarkThemeActive(this)
        }
        binding.imgStep1.setImageResource(if (useNightHints) R.drawable.hint_1_night else R.drawable.hint_1)
        binding.imgStep2.setImageResource(if (useNightHints) R.drawable.hint_2_night else R.drawable.hint_2)
    }
}
