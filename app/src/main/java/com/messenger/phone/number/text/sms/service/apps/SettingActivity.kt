package com.messenger.phone.number.text.sms.service.apps

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.demo.adsmanage.Commen.firebaseEventMain
import com.demo.adsmanage.Commen.log
import com.messenger.phone.number.text.sms.service.apps.CommanClass.baseConfig
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.fromnotificationAction
import com.messenger.phone.number.text.sms.service.apps.CommanClass.fromnotificationActionFragment
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getBottomNavigationBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperStatusBarColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivitySettingBinding
import com.messenger.phone.number.text.sms.service.apps.fragment.Conversation_swipe_motion_Fragment
import com.messenger.phone.number.text.sms.service.apps.fragment.LanguageFragment
import com.messenger.phone.number.text.sms.service.apps.fragment.PrivacyandsecurityFragment
import com.messenger.phone.number.text.sms.service.apps.fragment.SettingFragment
import com.messenger.phone.number.text.sms.service.apps.fragment.WhatNewFragment
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.AndroidEntryPoint

 var onbackbress123: Boolean = false

@AndroidEntryPoint
class SettingActivity : AppCompatActivity() {


    lateinit var binding: ActivitySettingBinding



    private var isfromnotification: Boolean = false
    private var onbackbress: Boolean = false



    var loadiswhatsnew = -1
//    override fun recreate() {
//
//        return
//
//        finish()
//        overridePendingTransition(
//            android.R.anim.fade_in, android.R.anim.fade_out
//        )
//        startActivity(intent)
//        overridePendingTransition(
//            android.R.anim.fade_in, android.R.anim.fade_out
//        )
//    }
    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeModeManager.applyThemeMode(this)
        if (ThemeModeManager.getThemeMode(this) == ThemeModeManager.MODE_SYSTEM) {
            DynamicColors.applyToActivityIfAvailable(this)
        }
        super.onCreate(savedInstanceState)
        setLocal()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)
        setBaseTheme(binding.vAnd15StatusBar)

        getCodeReffrence()
        settheme()
        Log.d("", "onStart: firsttimeusersetlang <-----> 5 ${config.SelectedLanguage}")
        loadiswhatsnew = intent.getIntExtra("loadiswhatsnew", -1)
        onbackbress = intent.getBooleanExtra("onbackbress", false)

        if (!baseConfig.firsttimeflowuse) {
            if (config.introshow) {
                loadiswhatsnew = 101
            }
        }

        if (loadiswhatsnew == 0) {
            binding.navHostFragment.gone()
            binding.fragl.visible()
            supportFragmentManager.beginTransaction().setReorderingAllowed(true)
                .add(binding.fragl.id, WhatNewFragment::class.java, null).commit()
        } else if (loadiswhatsnew == 1) {
            binding.navHostFragment.gone()
            binding.fragl.visible()
            supportFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(binding.fragl.id, LanguageFragment::class.java, null)
                .commit()
        } else if (loadiswhatsnew == 101) {
            Log.d("", "onStart: firsttimeusersetlang <-----> 6 ${config.SelectedLanguage}")
            firebaseEventMain("Language_First_Time")
            binding.navHostFragment.gone()
            binding.fragl.visible()
            val args = Bundle()
            args.putBoolean("firsttimeusersetlang", true)
            supportFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(binding.fragl.id, LanguageFragment::class.java, args)
                .commit()
        } else if (loadiswhatsnew == 99) {
            binding.navHostFragment.gone()
            binding.fragl.visible()
            supportFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(binding.fragl.id, Conversation_swipe_motion_Fragment::class.java, null)
                .commit()
        } else if (loadiswhatsnew == 9925) {
            binding.navHostFragment.gone()
            binding.fragl.visible()
            supportFragmentManager.beginTransaction().setReorderingAllowed(true)
                .add(binding.fragl.id, PrivacyandsecurityFragment::class.java, null).commit()
        } else {
            binding.navHostFragment.visible()
            binding.fragl.gone()
        }
    }

    private fun applyStatusBarInsets() {
        binding.vAnd15StatusBar.visible()
        ViewCompat.setOnApplyWindowInsetsListener(binding.vAnd15StatusBar) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            view.layoutParams = view.layoutParams.apply {
                height = statusBarHeight
            }
            binding.root.updatePadding(bottom = navBarHeight)
            insets
        }
        ViewCompat.requestApplyInsets(binding.vAnd15StatusBar)
    }

    private fun getCodeReffrence() {
        isfromnotification = intent.getBooleanExtra("isfromnotification", false)
        val firebasevalue = intent.getStringExtra("notify_vaule")


        "getCodeReffrence setting <-----------------> 1".log()

        if (isfromnotification) {
            "getCodeReffrence setting <-----------------> 2".log()
            fromnotificationAction = true
            if (firebasevalue != null) {
                "getCodeReffrence setting <-----------------> 3".log()
                fromnotificationActionFragment = firebasevalue
            } else {
                "getCodeReffrence setting <-----------------> 4".log()
            }
        } else {
            "getCodeReffrence setting <-----------------> 5".log()
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()


        "SettingActivity onBackPressed isfromnotification <-----------------> $isfromnotification".log()
        "SettingActivity onBackPressed onbackbress <-----------------> $onbackbress".log()
        "SettingActivity onBackPressed onbackbress123 <-----------------> $onbackbress123".log()
        if (isfromnotification) {
            startActivity(
                Intent(
                    this,
                        HomeABActivity::class.java

                )
            )
            finish()
        } else {
            if (onbackbress||onbackbress123) {

                    startActivity(
                        Intent(
                            this,
                                HomeABActivity::class.java

                        )
                    )

            }
            else{
//                finish()
                super.onBackPressed()
            }
        }
    }

    override fun recreate() {
        val restartIntent = Intent(intent).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        }
        finish()
        overridePendingTransition(0, 0)
        startActivity(restartIntent)
        overridePendingTransition(0, 0)
    }
    fun settheme(){
        val surfaceColor = getProperBackgroundColor()
        val statusBarColor = getProperStatusBarColor()
        val navigationBarColor = getBottomNavigationBackgroundColor()
        binding.vAnd15StatusBar.setBackgroundColor(statusBarColor)
        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        window.decorView.setBackgroundColor(surfaceColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightSystemBars = ThemeModeManager.shouldUseLightSystemBars(this@SettingActivity)
            isAppearanceLightStatusBars = useLightSystemBars
            isAppearanceLightNavigationBars = useLightSystemBars
        }
}
    override fun onStart() {
        super.onStart()
        settheme()
    }


}
