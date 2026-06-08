package com.messenger.phone.number.text.sms.service.apps.AfterCallScreen.permission

import android.util.Log
import android.Manifest
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import com.demo.adsmanage.Commen.firebaseFunnel
import com.demo.adsmanage.helper.click
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.DynamicColors
import com.google.android.material.textview.MaterialTextView
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.CommanClass.baseConfig
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createOptionBackground
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getContrastColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isPackageInstalled
import com.messenger.phone.number.text.sms.service.apps.CommanClass.logOnboardingFunnelStep
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.Dialog.PerReqBottomSheet
import com.messenger.phone.number.text.sms.service.apps.HomeABActivity
import com.messenger.phone.number.text.sms.service.apps.LanguageActivity
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityPermissionOneBinding
import com.messenger.phone.number.text.sms.service.apps.setBaseTheme

class permissionOneActivity : AppCompatActivity() {

    lateinit var binding: ActivityPermissionOneBinding


    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
//
//            val allGranted = result.all { it.value }
//
////            if (allGranted ) {
//            if (hasPhonePermissions() ) {

                nextScreen()

//            } else {
//
//                // Check if user selected "Don't ask again"
//                val permanentlyDenied = result.any { (permission, granted) ->
//                    !granted && !ActivityCompat.shouldShowRequestPermissionRationale(
//                        this,
//                        permission
//                    )
//                }
//
//                if (permanentlyDenied) {
//                    showSettingsDialog()
//                } else {
//                    // Re-ask permission
//                    requestPermissions()
//                }
//            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeModeManager.applyThemeMode(this)
        if (ThemeModeManager.getThemeMode(this) == ThemeModeManager.MODE_SYSTEM) {
            DynamicColors.applyToActivityIfAvailable(this)
        }
        super.onCreate(savedInstanceState)
        setLocal()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_permission_one)
        setBaseTheme(binding.vAnd15StatusBar)

        ThemeSetup()
        logOnboardingFunnelStep("First_permissionscreen")
        if (!baseConfig.FirstPerNotification) {
            firebaseFunnel("First_notification")

            baseConfig.FirstPerNotification = true
        }
        binding.btnBeDefault.setOnClickListener {

            if (hasAllPermissions()) {
                nextScreen()
            } else {
                requestPermissions()
            }
        }

        binding.lottieAnimationView2.setOnClickListener {

            if (hasAllPermissions()) {
                nextScreen()
            } else {
                requestPermissions()
            }
        }


        binding.tvPrivacy.click {
            openUrl("https://privacypolicy.kriadl.com/messages-sms-and-private-chat/privacy-policy")
        }

        binding.tvPrivTerm.click {
            openUrl("https://privacypolicy.kriadl.com/sms-messages-and-text-messaging/terms-of-service")
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
//             if (hasPhonePermissions()){
                 nextScreen()
//             }else{
//                 showDeclarationDialog()
//             }

            }
        })

    }


    fun openUrl(url: String) {
        val builder = CustomTabsIntent.Builder()
        val params = CustomTabColorSchemeParams.Builder()
        params.setToolbarColor(ContextCompat.getColor(this, R.color.appcolor))
        builder.setDefaultColorSchemeParams(params.build())
        builder.setShowTitle(true)
        builder.setShareState(CustomTabsIntent.SHARE_STATE_ON)
        builder.setInstantAppsEnabled(true)
        val customBuilder = builder.build()
        if (isPackageInstalled("com.android.chrome")) {
            customBuilder.intent.setPackage("com.android.chrome")
            try {
                customBuilder.launchUrl(this, Uri.parse(url))
            } catch (e: Exception) {
                openlink(url)
            }
        } else {
            openlink(url)
        }
    }

    fun openlink(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            startActivity(browserIntent)
        } catch (e: ActivityNotFoundException) {
            toastMess("No web browser app found")
        }
    }


    private fun nextScreen() {

        if (hasPhonePermissions()){

            if (Settings.canDrawOverlays(this@permissionOneActivity)) {

                if (!baseConfig.secondtimeflowuse) {
                    Log.d("TAG", "language----33333  " )

                    baseConfig.secondtimeflowuse = true
                    val intent = Intent(this, LanguageActivity::class.java)
                    intent.putExtra("mainopen", true)
                    startActivity(intent)
                    finish()
                } else {
                    startActivity(Intent(this, HomeABActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    finish()
                }

            }else{
                startActivity(Intent(this@permissionOneActivity, permissionTwoActivity::class.java))
                finish()
            }
        }else{
            startActivity(Intent(this@permissionOneActivity, permissionTwoActivity::class.java))
            finish()
        }

//
//
//        if (Settings.canDrawOverlays(this)) {
//            startActivity(Intent(this@permissionOneActivity, HomeABActivity::class.java))
//            finish()
//        } else {
//            startActivity(Intent(this@permissionOneActivity, permissionTwoActivity::class.java))
//            finish()
//        }
    }

    private fun requestPermissions() {

        val permissions =
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            arrayOf(
//                Manifest.permission.POST_NOTIFICATIONS,
//                Manifest.permission.READ_PHONE_STATE
//            )
//        } else {
            arrayOf(
                Manifest.permission.READ_PHONE_STATE
            )
//        }

        permissionLauncher.launch(permissions)
    }

    private fun hasAllPermissions(): Boolean {

        val permissions =
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            arrayOf(
//                Manifest.permission.POST_NOTIFICATIONS,
//                Manifest.permission.READ_PHONE_STATE
//            )
//        } else {
            arrayOf(
                Manifest.permission.READ_PHONE_STATE
            )
//        }

        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun hasPhonePermissions(): Boolean {

        val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE)


        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private var perReqBottomSheet: PerReqBottomSheet? = null

    private var activityResultForCallState =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (hasPhonePermissions()) {
                nextScreen()
            } else {
                showSettingsDialog()
            }
        }

    private fun showSettingsDialog() {
        perReqBottomSheet?.safeDismiss()
        perReqBottomSheet = PerReqBottomSheet.create(
            activity = this,
            title = getString(R.string.permission_required_messages_title),
            subtitle = getString(R.string.permission_required_open_settings),
            step1 = getString(R.string.permission_required_step1),
            step2Template = getString(R.string.permission_required_step2_phone),
            step2Highlight = getString(R.string.permission_required_keyword_phone_call),
            step3Template = getString(R.string.permission_required_step3),
            step3Highlight = getString(R.string.permission_required_keyword_allow),
            buttonText = getString(R.string.permission_required_allow_button),
            onAllow = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                activityResultForCallState.launch(intent)
            },

            )
        perReqBottomSheet?.show()

    }
    private fun showDeclarationDialog() {
        val isDarkPopup = ThemeModeManager.isDarkThemeActive(this)
        val color = { resId: Int -> ContextCompat.getColor(this, resId) }
        val dilogbgColor = color(
            if (isDarkPopup) R.color.dilog_dark_color else R.color.dilog_light_color
        )

        val dialogView = layoutInflater.inflate(R.layout.dialog_declaration, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val root = dialogView as LinearLayout
        val tv = dialogView.findViewById<MaterialTextView>(R.id.tv_content)
        val btn = dialogView.findViewById<MaterialButton>(R.id.btnOk)
//        val icon = dialogView.findViewById<ImageView>(R.id.iv_icon)

        // Background
        root.backgroundTintList = ColorStateList.valueOf(dilogbgColor)

        // Text
//        tv.text = "The app can only function if the draw over other apps permission is granted"
        tv.setTextColor(getProperTextColor())
        // Button
        val primaryColor = ContextCompat.getColor(this, R.color.afterCallnewBlue)

        btn.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btn.setTextColor(Color.WHITE)
        btn.rippleColor = ColorStateList.valueOf(primaryColor.adjustAlpha(0.2f))

        // Icon
//        icon.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_dark))

        btn.setOnClickListener {
            dialog.dismiss()
        }
    }



    private fun ThemeSetup() {
        val isDarkPopup = ThemeModeManager.isDarkThemeActive(this)
        val color = { resId: Int -> ContextCompat.getColor(this, resId) }
        val bgforperColor = color(
            if (isDarkPopup) R.color.newPerDarkBg else R.color.newPerLightBg
        )
        val dilogbgColor = color(
            if (isDarkPopup) R.color.dilog_dark_color else R.color.dilog_light_color
        )

        val surfaceColor = bgforperColor
        val statusBarColor = bgforperColor
        val navigationBarColor = bgforperColor
        val primaryColor = color(R.color.afterCallnewBlue)
        val onPrimaryColor = primaryColor.getContrastColor()
        val buttonRippleColor = primaryColor.adjustAlpha(0.22f)
        val textcolor = getProperTextColor()
        val textdimcolor = textcolor.adjustAlpha(0.88f)

binding.lnrCtc.background =createOptionBackground(
    cornerSize = resources.getDimension(com.intuit.sdp.R.dimen._25sdp),
    fillColor = dilogbgColor,
    showRipple = false,
//    rippleColor = Color.WHITE.adjustAlpha(0.3f),
)
        binding.main.setBackgroundColor(surfaceColor)
        binding.vAnd15StatusBar.setBackgroundColor(statusBarColor)
        binding.btnBeDefault.setTextColor(onPrimaryColor)
        binding.btnBeDefault.rippleColor = ColorStateList.valueOf(buttonRippleColor)


        binding.txtTitle2.setTextColor(textcolor)
        binding.txtTitleTag.setTextColor(textdimcolor)
        binding.tvPhoneStatus.setTextColor(textcolor)
        binding.tvPhoneStatusDis.setTextColor(textdimcolor)
        binding.tvNoti.setTextColor(textcolor)
        binding.tvNotiDis.setTextColor(textdimcolor)

        binding.tvPrivacy.setTextColor(getColor(R.color.appcolor))
        binding.tvAnd.setTextColor(textdimcolor)
        binding.tvPrivTerm.setTextColor(getColor(R.color.appcolor))

        binding.tvAnd.setTextColor(textcolor)


        binding.mbPhoneStatus.iconTint = ColorStateList.valueOf(textcolor)
        binding.mbNoti.iconTint = ColorStateList.valueOf(textcolor)

        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor

        window.decorView.setBackgroundColor(surfaceColor)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }

        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightSystemBars =
                ThemeModeManager.shouldUseLightSystemBars(this@permissionOneActivity)
            isAppearanceLightStatusBars = useLightSystemBars
            isAppearanceLightNavigationBars = useLightSystemBars
        }

    }


    override fun onStart() {
        super.onStart()
        ThemeSetup()
    }


}
