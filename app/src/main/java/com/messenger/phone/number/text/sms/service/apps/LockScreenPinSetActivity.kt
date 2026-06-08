package com.messenger.phone.number.text.sms.service.apps

import android.content.res.ColorStateList
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Build
import android.os.Bundle
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import com.demo.adsmanage.Commen.firebaseEventMain
import com.demo.adsmanage.basemodule.BaseSharedPreferences
import com.messenger.phone.number.text.sms.service.apps.CommanClass.checkIfFileExists
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createChatBottomSheetDialog
import com.messenger.phone.number.text.sms.service.apps.CommanClass.filePathForLock
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getBottomNavigationBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperStatusBarColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createOptionBackground
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityLockScreenPinSetBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.RecoveryMethodDialogBinding
import com.simplemobiletools.commons.extensions.adjustAlpha
import com.simplemobiletools.commons.extensions.getContrastColor
import com.simplemobiletools.commons.extensions.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LockScreenPinSetActivity : AppCompatActivity() {


    lateinit var binding: ActivityLockScreenPinSetBinding




    var comefrom: Int = -1

    var forpasswordforgot = false

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocal()
        comefrom = intent.getIntExtra("pinsetfor", -1)
        forpasswordforgot = intent.getBooleanExtra("forpasswordforgot", false)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lock_screen_pin_set)
        setBaseTheme(binding.vAnd15StatusBar)
        this.firebaseEventMain("Lock_Pin_Set")

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

        with(binding) {
            backBtn.setOnClickListener {
                finish()
            }

            if (!forpasswordforgot &&
                BaseSharedPreferences(this@LockScreenPinSetActivity).mIS_SUBSCRIBED == false &&
                !checkIfFileExists(filePathForLock)
            ) {
                if (comefrom == 2) {
                    textView13.visible()
                } else {
                    textView13.gone()
                }
            } else {
                textView13.gone()
            }


            binding.savebtn.setOnClickListener {
                val pass = binding.newpasswordtxt.text.toString()
                val passconfirm = binding.confermnewpasswordtxt.text.toString()

                if (forpasswordforgot) {
                    if (pass.isNotEmpty() && pass.length == 6 && passconfirm.isNotEmpty() && passconfirm.length == 6) {
                        if (pass == passconfirm) {
                            if (comefrom == 1) {
                                config.Lock_Screen_Pin = passconfirm
                                finish()
                            } else if (comefrom == 2) {
                                config.Full_AppLock_Pin = passconfirm
                                finish()
                            }
                        } else {
                            toast(resources.getString(R.string.Password_Not_mach))
                        }
                    } else {
                        toast(resources.getString(R.string.Password_Not_mach))
                    }

                } else {
                    if (pass.isNotEmpty() && pass.length == 6 && passconfirm.isNotEmpty() && passconfirm.length == 6) {
                        if (pass == passconfirm) {
                            showCustomDialog(pass, passconfirm)
                        } else {
                            toast(resources.getString(R.string.Password_Not_mach))
                        }
                    } else {
                        if (passconfirm.isNotEmpty()) {
                            if (pass.isNotEmpty()) {
                                toast(resources.getString(R.string.Password_Not_mach))
                            } else {
                                toast(resources.getString(R.string.Please_Enter_password))
                            }
                        } else {
                            toast(resources.getString(R.string.Please_Enter_Con_password))
                        }
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
        val secondaryTextColor = textColor.adjustAlpha(0.72f)
        val primaryColor = getProperPrimaryColor()
        val inputFillColor = primaryColor.adjustAlpha(0.1f)
        val inputHintColor = textColor.adjustAlpha(0.54f)

        binding.root.setBackgroundColor(surfaceColor)
        binding.defaulttoolshow.setBackgroundColor(surfaceColor)
        binding.vAnd15StatusBar.setBackgroundColor(statusBarColor)
        binding.textView3.setTextColor(textColor)
        binding.backBtn.iconTint = ColorStateList.valueOf(textColor)

        binding.textView13.setTextColor(secondaryTextColor)
        binding.enterNewpasstxt.setTextColor(textColor)
        binding.confirmenterNewpasstxt.setTextColor(textColor)
        binding.enterNewpasstxt2.setTextColor(secondaryTextColor)
        binding.enterNewpasstxt3.setTextColor(secondaryTextColor)

        binding.cardView3.setCardBackgroundColor(inputFillColor)
        binding.cardView4.setCardBackgroundColor(inputFillColor)
        binding.newpasswordtxt.setBackgroundColor(Color.TRANSPARENT)
        binding.confermnewpasswordtxt.setBackgroundColor(Color.TRANSPARENT)
        binding.newpasswordtxt.setTextColor(textColor)
        binding.confermnewpasswordtxt.setTextColor(textColor)
        binding.newpasswordtxt.setHintTextColor(inputHintColor)
        binding.confermnewpasswordtxt.setHintTextColor(inputHintColor)

        binding.savebtn.background = createOptionBackground(
            cornerSize = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._50sdp).toFloat(),
            fillColor = primaryColor,
            strokeColor = primaryColor,
            strokeWidth = 0f,
            showRipple = true,
            rippleColor = Color.WHITE.adjustAlpha(0.3f)
        )

        binding.savebtn.setTextColor(primaryColor.getContrastColor())


        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        window.decorView.setBackgroundColor(surfaceColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightSystemBars = ThemeModeManager.shouldUseLightSystemBars(this@LockScreenPinSetActivity)
            isAppearanceLightStatusBars = useLightSystemBars
            isAppearanceLightNavigationBars = useLightSystemBars
        }
    }

    private fun showCustomDialog(pass: String, passconfirm: String) {
        val binding = RecoveryMethodDialogBinding.inflate(layoutInflater)
        when (config.activeThemeSelection) {
            1 -> {
                binding.isdarktheme = false
            }

            2 -> {
                binding.isdarktheme = true
            }

            3 -> {
                binding.isdarktheme = true
            }

            4 -> {
                binding.isdarktheme = false
            }
        }
        val dialogView = binding.root
        val dialogBuilder =createChatBottomSheetDialog()
        dialogBuilder.setContentView(dialogView)
        val alertDialog = dialogBuilder
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        with(binding) {
            closeDialog.setOnClickListener {
                alertDialog.dismiss()
            }
            patternLock.setOnClickListener {
                startActivity(
                    Intent(
                        this@LockScreenPinSetActivity,
                        PattenActivity::class.java
                    )
                        .putExtra("comefrom", comefrom)
                        .putExtra("password", passconfirm)
                        .putExtra("fornewpattenset", true)
                )
                finish()
            }
            tvSecurity.setOnClickListener {
                startActivity(
                    Intent(
                        this@LockScreenPinSetActivity,
                        LockSceenSecQuestionActivity::class.java
                    )
                        .putExtra("comefrom", comefrom)
                        .putExtra("password", passconfirm)
                )
                finish()
            }
        }
        alertDialog.show()
    }
}
