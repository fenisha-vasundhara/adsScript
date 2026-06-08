package com.messenger.phone.number.text.sms.service.apps

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import com.demo.adsmanage.Commen.firebaseEventMain
import com.google.android.material.color.DynamicColors
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createOptionBackground
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
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getContrastColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.Dialog.PrivateChatTutorialDialog
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityLockScreenSetupBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LockScreenSetupActivity : AppCompatActivity() {

    lateinit var binding: ActivityLockScreenSetupBinding
    private val privateChatTutorialDialog by lazy { PrivateChatTutorialDialog() }
    var comefrom: Int = -1
    var pinsetupornot: String = "Not Set"
    var fullappsetupdone: Boolean = false
    var forsetting: Boolean = false

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lock_screen_setup)
        setBaseTheme(binding.vAnd15StatusBar)
        this.firebaseEventMain("Lock_Setup")

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


        comefrom = intent.getIntExtra("comefrom", -1)
        fullappsetupdone = intent.getBooleanExtra("fullappsetupdone", false)
        forsetting = intent.getBooleanExtra("forsetting", false)



        if (comefrom == 1) {
            config.isprivacychatscreenopen = true
            binding.textView3.text = resources.getString(R.string.Privacy_chat)
            binding.textView12.text = resources.getString(R.string.It_is_using_private_chat)

            if (config.privatechattutorialshow) {
//                privateChatTutorialDialog.show(supportFragmentManager, "privateChatTutorialDialog")
            }

        } else if (comefrom == 2) {
            config.isapplockchatscreenopen = true
            binding.textView3.text = resources.getString(R.string.Two_new_step_Verification)
            binding.textView12.text = resources.getString(R.string.It_is_using_two_step)
        }

        if (fullappsetupdone) {
            binding.textView12.text = resources.getString(R.string.Verification_has_been_turned_on)
            binding.turnOnPin.text = resources.getString(R.string.Done)
        }

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.turnOnPin.setOnClickListener {
            if (fullappsetupdone) {
                finish()
            } else {
                if (comefrom == 1) {
                    startActivity(Intent(this, LockScreenPinSetActivity::class.java).putExtra("pinsetfor", 1))
                    finish()
                } else if (comefrom == 2) {
                    startActivity(Intent(this, LockScreenPinSetActivity::class.java).putExtra("pinsetfor", 2))
                    finish()
                }
            }

        }

        if (comefrom == 1) {
            pinsetupornot = config.Lock_Screen_Pin
        } else if (comefrom == 2) {
            pinsetupornot = config.Full_AppLock_Pin
        }

        ThemeSetup()

        if (pinsetupornot != "Not Set") {
            when (comefrom) {
                1 -> {
                    startActivity(
                        Intent(this, LockScreenActivity::class.java)
                            .putExtra("lockype", 2)
                            .putExtra("comefrom", 1)
                    )
                    finish()
                }

                2 -> {
                    startActivity(
                        Intent(this, LockScreenSettingActivity::class.java)
                            .putExtra("comefrom", 2)
                    )
                    finish()
                }

                else -> {

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
        val primaryColor = getProperPrimaryColor()
        val buttonRippleColor = primaryColor.adjustAlpha(0.22f)
        val iconRippleColor = secondaryTextColor.adjustAlpha(0.18f)

        binding.root.setBackgroundColor(surfaceColor)
        binding.vAnd15StatusBar.setBackgroundColor(statusBarColor)
        binding.defaulttoolshow.setBackgroundColor(surfaceColor)
        binding.textView3.setTextColor(textColor)
        binding.textView12.setTextColor(secondaryTextColor)
        binding.imageView21.setImageResource(
            if (ThemeModeManager.isDarkThemeActive(this)) {
                R.drawable.tunon_bg_2
            } else {
                R.drawable.tunon_bg
            }
        )

        binding.backBtn.iconTint = ColorStateList.valueOf(textColor)
        binding.backBtn.rippleColor = ColorStateList.valueOf(iconRippleColor)

        binding.turnOnPin.background = createOptionBackground(
            cornerSize = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._50sdp).toFloat(),
            fillColor = primaryColor,
            strokeColor = primaryColor,
            strokeWidth = 0f,
            showRipple = true,
            rippleColor = buttonRippleColor
        )
        binding.turnOnPin.setTextColor(primaryColor.getContrastColor())

        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        window.decorView.setBackgroundColor(surfaceColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightSystemBars =
                ThemeModeManager.shouldUseLightSystemBars(this@LockScreenSetupActivity)
            isAppearanceLightStatusBars = useLightSystemBars
            isAppearanceLightNavigationBars = useLightSystemBars
        }

    }




}
