package com.messenger.phone.number.text.sms.service.apps

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import com.demo.adsmanage.Commen.firebaseEventMain
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getBottomNavigationBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getDialogBackgroundColor
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
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setchatthemecolor
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityLockScreenSettingBinding

class LockScreenSettingActivity : AppCompatActivity() {

    lateinit var binding: ActivityLockScreenSettingBinding

    var comefrom: Int = -1

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeModeManager.applyThemeMode(this)
        if (ThemeModeManager.getThemeMode(this) == ThemeModeManager.MODE_SYSTEM) {
            DynamicColors.applyToActivityIfAvailable(this)
        }
        super.onCreate(savedInstanceState)
        setLocal()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lock_screen_setting)
        this.firebaseEventMain("Lock_Setting")

        fontSize10 = getTextSizeForeNormal10MS()
        fontSize13 = getTextSizeForeNormal13MS()
        fontSize18 = getTextSizeForeNormal18MS()
        fontSize8 = getTextSizeForeNormal8MS()
        fontSize15 = getTextSizeHometitleMS()
        setBaseTheme(binding.vAnd15StatusBar)
        binding.vAnd15StatusBar.setchatthemecolor(0)
        binding.textsizechagefor10 = fontSize10
        binding.textsizechagefor13 = fontSize13
        binding.textsizechagefor18 = fontSize18
        binding.textsizechagefor8 = fontSize8
        binding.textsizechagefor15 = fontSize15
        ThemeSetup()

        comefrom = intent.getIntExtra("comefrom", -1)
        if (comefrom == 1) {
            binding.textView12.text = resources.getString(R.string.When_you_private_chat)
        } else if (comefrom == 2) {
            binding.textView12.text = resources.getString(R.string.When_you_two_step)
        }
        with(binding) {

            line1.setOnClickListener {
                turnOfLock()
            }

            blockNumberBtn.setOnClickListener {

                when (comefrom) {
                    1 -> {
                        if (config.isprivatelocktype == 2) {
                            startActivity(
                                Intent(this@LockScreenSettingActivity, PattenActivity::class.java)
                                    .putExtra("comefrom", comefrom)
                                    .putExtra("forpasswordforgot", true)
                                    .putExtra("appopen", false)
                            )
                        } else {
                            startActivity(
                                Intent(
                                    this@LockScreenSettingActivity,
                                    LockSceenSecQuestionActivity::class.java
                                )
                                    .putExtra("comefrom", comefrom)
                                    .putExtra("forpasswordforgot", true)
                                    .putExtra("appopen", false)
                            )
                        }
                    }

                    2 -> {
                        if (config.is2steplocktype == 2) {
                            startActivity(
                                Intent(this@LockScreenSettingActivity, PattenActivity::class.java)
                                    .putExtra("comefrom", comefrom)
                                    .putExtra("forpasswordforgot", true)
                                    .putExtra("appopen", false)
                            )
                        } else {
                            startActivity(
                                Intent(
                                    this@LockScreenSettingActivity,
                                    LockSceenSecQuestionActivity::class.java
                                )
                                    .putExtra("comefrom", comefrom)
                                    .putExtra("forpasswordforgot", true)
                                    .putExtra("appopen", false)
                            )
                        }
                    }
                }
            }
            archiveornot.setOnClickListener {
                startActivity(
                    Intent(
                        this@LockScreenSettingActivity,
                        LockScreenActivity::class.java
                    )
                        .putExtra("ispasswordchange", true)
                        .putExtra("comefrom", comefrom)
                )
            }

            backBtn.setOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun turnOfLock() {

        val builder = MaterialAlertDialogBuilder(this)
            .setCancelable(false)
            .setMessage(resources.getString(R.string.Turn_off_verification))
            .setPositiveButton(resources.getString(R.string.Turn_off)) { dialog, which ->
                dialog.dismiss()
                if (comefrom == 1) {
                    config.Lock_Screen_Sec_Question = "Select any Security Question"
                    config.Lock_Screen_Pin = "Not Set"
                    config.Lock_Screen_Sec_Question_Ans = "Select any Security Question"
                    finish()
                } else if (comefrom == 2) {
                    config.Full_AppLock_Sec_Question = "Select any Security Question"
                    config.Full_AppLock_Pin = "Not Set"
                    config.Full_AppLock_Sec_Question_Ans = "Select any Security Question"
                    finish()
                }
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }
        builder.create()
        builder.show()

    }

    override fun onStart() {
        super.onStart()
        ThemeSetup()
    }

    override fun onBackPressed() {
//        super.onBackPressed()

        Log.d("", "onBackPressed: comefrom <------LockScreenSettingActivity-> 1 ${config.Full_AppLock_Pin}")

        finish()
    }
    private fun ThemeSetup() {
        val surfaceColor = getProperBackgroundColor()
        val statusBarColor = getProperStatusBarColor()
        val navigationBarColor = getBottomNavigationBackgroundColor()
        val textColor = getProperTextColor()
        val secondaryTextColor = getProperSecondaryTextColor()
        val primaryColor = getProperPrimaryColor()
        val cardColor = getDialogBackgroundColor()

        binding.root.setBackgroundColor(surfaceColor)
        binding.defaulttoolshow.setBackgroundColor(surfaceColor)
        binding.vAnd15StatusBar.setBackgroundColor(statusBarColor)
        binding.constraintLayout6.backgroundTintList = ColorStateList.valueOf(cardColor)

        binding.textView3.setTextColor(textColor)
        binding.textView12.setTextColor(secondaryTextColor)
        binding.mobilenumbertxt.setTextColor(textColor)
        binding.archiveornot.setTextColor(textColor)
        binding.txtnumberblockornot.setTextColor(textColor)

        binding.backBtn.iconTint = ColorStateList.valueOf(textColor)
        listOf(binding.imageView7, binding.imageView8, binding.imageView9).forEach { icon ->
            icon.imageTintList = ColorStateList.valueOf(primaryColor)
        }

        val useLightBars = ThemeModeManager.shouldUseLightSystemBars(this)
        binding.imageView21.setImageResource(
            if (useLightBars) R.drawable.tunon_bg else R.drawable.tunon_bg_2
        )

        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        window.decorView.setBackgroundColor(surfaceColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            isAppearanceLightStatusBars = useLightBars
            isAppearanceLightNavigationBars = useLightBars
        }
    }
}
