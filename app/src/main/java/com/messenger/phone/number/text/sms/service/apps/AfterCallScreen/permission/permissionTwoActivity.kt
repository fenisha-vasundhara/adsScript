package com.messenger.phone.number.text.sms.service.apps.AfterCallScreen.permission

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.AlertDialog
import android.app.AppOpsManager
import android.util.Log
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.ImageView
import android.widget.LinearLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.demo.adsmanage.Commen.firebaseFunnel
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.DynamicColors
import com.google.android.material.textview.MaterialTextView
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.CommanClass.baseConfig
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createOptionBackground
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getContrastColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.logOnboardingFunnelStep
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.Dialog.PerReqBottomSheet
import com.messenger.phone.number.text.sms.service.apps.HomeABActivity
import com.messenger.phone.number.text.sms.service.apps.LanguageActivity
import com.messenger.phone.number.text.sms.service.apps.OverlayPermissionAnimationActivity
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityPermissionTwoBinding
import com.messenger.phone.number.text.sms.service.apps.inAppUpdate.InAppUpdate
import com.messenger.phone.number.text.sms.service.apps.setBaseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class permissionTwoActivity : AppCompatActivity() {

    lateinit var binding: ActivityPermissionTwoBinding
    val inAppUpdate: InAppUpdate by lazy {
        InAppUpdate(this)
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->

            try {
                if (hasAllPerm()) {
                    nextScreen11()
                } else {
                    val permanentlyDenied = result.any { (permission, granted) ->
                        !granted && !ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            permission
                        )
                    }
                    if (permanentlyDenied) {
                        showSettingsDialog()
                    } else {
                        Handler(Looper.getMainLooper()).postDelayed({
                            requestPermissions()
                        }, 200)
                    }
                }
            } catch (_: Exception) {

            }


        }

    private lateinit var appOps: AppOpsManager
    lateinit var appOpsListener: AppOpsManager.OnOpChangedListener
    private val overlayAnimHandler by lazy { Handler(Looper.getMainLooper()) }
    private var overlayAnimRunnable: Runnable? = null
    var FromSpl: Boolean = false
    private var hintSlides: List<OverlayHintSlide> = emptyList()

    private val hintSlideslight by lazy {
        listOf(
            OverlayHintSlide(
                imageRes = R.drawable.ic_over_lay_1_light
            ),
            OverlayHintSlide(
                imageRes = R.drawable.ic_over_lay_2_light
            )
        )
    }

    private val hintSlidesDark by lazy {
        listOf(
            OverlayHintSlide(
                imageRes = R.drawable.ic_over_lay_1_dark
            ),
            OverlayHintSlide(
                imageRes = R.drawable.ic_over_lay_2_dark
            )
        )
    }

    private var activityResultLauncherForOverlays =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Settings.canDrawOverlays(this@permissionTwoActivity)) {

            } else {
                if (!::appOps.isInitialized) return@registerForActivityResult
                if (!::appOpsListener.isInitialized) return@registerForActivityResult

                appOps.stopWatchingMode(appOpsListener)
//                firebaseFunnel("Overlay_allowed")
//                firebaseFunnel("Aftercall_allow")
                nextScreen()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {

        ThemeModeManager.applyThemeMode(this)
        if (ThemeModeManager.getThemeMode(this) == ThemeModeManager.MODE_SYSTEM) {
            DynamicColors.applyToActivityIfAvailable(this)
        }
        super.onCreate(savedInstanceState)

        setLocal()

        appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager

        binding = DataBindingUtil.setContentView(this, R.layout.activity_permission_two)
        setBaseTheme(binding.vAnd15StatusBar)

        ThemeSetup()

        initphoneStatePermission()

        initOverlayPermission()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {
                if (Settings.canDrawOverlays(this@permissionTwoActivity) && hasAllPerm()) {
                    startActivity(Intent(this@permissionTwoActivity, HomeABActivity::class.java))
                    finish()
                } else {
                    try {
                        if (!hasAllPerm()) {
                            showDeclarationDialogPhoneState()
                        } else {
                            showDeclarationDialogOverlay()
                        }
                    } catch (_: Exception) {

                    }


                }
            }

        })
    }

    //    ---phonestate---
    private fun hasAllPerm(): Boolean {
        try {

            val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE)


            return permissions.all {
                ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
            }
        } catch (e: Exception) {
            return true
        }

    }

    private fun initphoneStatePermission() {
        binding.btnBeDefaultPhone.setOnClickListener {

            if (hasAllPerm()) {
                nextScreen11()
            } else {
                requestPermissions()
            }
        }
        binding.lottieAnimationView1.setOnClickListener {

            if (hasAllPerm()) {
                nextScreen11()
            } else {
                requestPermissions()
            }
        }
    }

    private fun requestPermissions() {

        val permissions =
            arrayOf(
                Manifest.permission.READ_PHONE_STATE
            )


        permissionLauncher.launch(permissions)
    }

    private var perReqBottomSheet: PerReqBottomSheet? = null

    private var activityResultForCallState =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            try {
                if (hasAllPerm()) {
                    nextScreen11()
                } else {
                    showSettingsDialog()
                }
            } catch (_: Exception) {

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
        if (!baseConfig.FirstDialogCount) {
            firebaseFunnel("First_permissiondialog")

            baseConfig.FirstDialogCount = true
        } else {
            firebaseFunnel("phonestateper_dialog")
        }
    }


    ////---overlay----///

    private fun initOverlayPermission() {


        binding.btnBeDefault.setOnClickListener {
            permissionCheck()
        }
        binding.btnFramePermissions2.setOnClickListener {
            permissionCheck()
        }
        binding.vpHintSlides.setOnClickListener {
            permissionCheck()
        }
        binding.conOverlay.setOnClickListener {
            permissionCheck()
        }

        setupHintSlider()
    }

    private fun permissionCheck() {
        if (Settings.canDrawOverlays(this@permissionTwoActivity)) {
            nextScreen()
        } else {
            takeOverlayPermission()
        }
    }

    private fun screenVisibility() {
        if (!hasAllPerm()) {
            if (!baseConfig.FirstCallStatePerm) {
                firebaseFunnel("First_callstateper")

                baseConfig.FirstCallStatePerm = true
            }
            logOnboardingFunnelStep("First_phonestatescreen")

            binding.conPhoneState.visibility = View.VISIBLE
            binding.conOverlay.visibility = View.GONE

        } else {
            logOnboardingFunnelStep("First_overlayscreen")
            binding.conPhoneState.visibility = View.GONE
            binding.conOverlay.visibility = View.VISIBLE
        }
    }

    private fun nextScreen11() {
        screenVisibility()

        if (hasAllPerm()) {

            if (Settings.canDrawOverlays(this@permissionTwoActivity)) {
                if (!baseConfig.secondtimeflowuse) {
                    Log.d("TAG", "language----22222  ")
                    baseConfig.secondtimeflowuse = true
                    val intent = Intent(this, LanguageActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    intent.putExtra("mainopen", true)
                    startActivity(intent)
                    finish()
                } else {
                    startActivity(Intent(this, HomeABActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    finish()
                }

            } else {
                permissionCheck()
            }
        }
    }


    private fun nextScreen() {
        screenVisibility()
        if (hasAllPerm()) {

            if (Settings.canDrawOverlays(this@permissionTwoActivity)) {
                if (!baseConfig.secondtimeflowuse) {
                    Log.d("TAG", "language----11111  ")
                    baseConfig.secondtimeflowuse = true
                    val intent = Intent(this, LanguageActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    intent.putExtra("mainopen", true)
                    startActivity(intent)
                    finish()
                } else {
//                    Log.d("TAG", "language----22222 ")

                    startActivity(Intent(this, HomeABActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    finish()
                }

            }
        }


    }


    override fun onStart() {
        super.onStart()
        ThemeSetup()
    }

    override fun onDestroy() {
        inAppUpdate.onDestroy()
        overlayAnimRunnable?.let { overlayAnimHandler.removeCallbacks(it) }
        super.onDestroy()
    }

    private fun showDeclarationDialogPhoneState() {
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
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )


        val window = dialog.window
        val params = window?.attributes

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels

        params?.width = (screenWidth * 0.85).toInt()

        window?.attributes = params

        val root = dialogView as LinearLayout
        val tv = dialogView.findViewById<MaterialTextView>(R.id.tv_content)
        val btn = dialogView.findViewById<MaterialButton>(R.id.btnOk)
//        val icon = dialogView.findViewById<ImageView>(R.id.iv_icon)

        // Background
        root.backgroundTintList = ColorStateList.valueOf(dilogbgColor)

        // Text
        tv.text = getString(R.string.declaration_dilog_phonestate)
        tv.setTextColor(getProperTextColor())
        // Button
        val primaryColor = ContextCompat.getColor(this, R.color.afterCallnewBlue)

        btn.backgroundTintList = ColorStateList.valueOf(primaryColor)
        btn.setTextColor(Color.WHITE)
        btn.rippleColor = ColorStateList.valueOf(primaryColor.adjustAlpha(0.2f))
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)

        // Icon
//        icon.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_dark))

        btn.setOnClickListener {
            dialog.dismiss()

            if (!hasAllPerm()) {

                binding.btnBeDefaultPhone.performClick()

            } else {
                binding.btnBeDefault.performClick()

            }
        }
    }

    private fun showDeclarationDialogOverlay() {
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

        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)

        val window = dialog.window
        val params = window?.attributes

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels

        params?.width = (screenWidth * 0.85).toInt()

        window?.attributes = params

        val root = dialogView as LinearLayout
        val tv = dialogView.findViewById<MaterialTextView>(R.id.tv_content)
        val btn = dialogView.findViewById<MaterialButton>(R.id.btnOk)
//        val icon = dialogView.findViewById<ImageView>(R.id.iv_icon)

        // Background
        root.backgroundTintList = ColorStateList.valueOf(dilogbgColor)

        // Text
        tv.text = getString(R.string.declaration_dilog_ol)
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
            if (!hasAllPerm()) {

                binding.btnBeDefaultPhone.performClick()

            } else {
                binding.btnBeDefault.performClick()

            }
        }
    }

    private fun ThemeSetup() {
        val isDarkPopup = ThemeModeManager.isDarkThemeActive(this)
        val color = { resId: Int -> ContextCompat.getColor(this, resId) }
        val bgforperColor = color(
            if (isDarkPopup) R.color.newPerDarkBg1 else R.color.newPerLightBg1
        )

        if (isDarkPopup) {
            hintSlides = hintSlidesDark
        } else {
            hintSlides = hintSlideslight
        }
        val surfaceColor = bgforperColor
        val statusBarColor = bgforperColor
        val navigationBarColor = bgforperColor
        val primaryColor = color(R.color.afterCallnewBlue)
        val textColor = getProperTextColor()
        val onPrimaryColor = primaryColor.getContrastColor()
        val buttonRippleColor = primaryColor.adjustAlpha(0.22f)
        val textdimcolor = textColor.adjustAlpha(0.88f)

        binding.main.setBackgroundColor(surfaceColor)
        binding.vAnd15StatusBar.setBackgroundColor(statusBarColor)


        ///---phonestate--//

        binding.imgVector.setImageResource(
            if (isDarkPopup) {
                R.drawable.ic_new_vec_phone_state_dark
            } else {
                R.drawable.ic_new_vec_phone_state_light
            }
        )

        binding.txtphonestate1.setTextColor(textColor)
        binding.txtphonestate2.setTextColor(textdimcolor)
        binding.txtphonestate3.setTextColor(textdimcolor)

        binding.ivPoint1.setColorFilter(textdimcolor)
        binding.ivPoint2.setColorFilter(textdimcolor)


        ///--- overlay --//


        val smartFeaturesPillFillColor = color(
            if (isDarkPopup) {
                R.color.aftercall_permission_pill_fill_dark
            } else {
                R.color.aftercall_permission_pill_fill_light
            }
        )
        val smartFeaturesPillStrokeColor = color(
            if (isDarkPopup) {
                R.color.aftercall_permission_pill_stroke_dark
            } else {
                R.color.aftercall_permission_pill_stroke_light
            }
        )
        val smartFeaturesPillStrokeWidth = resources.displayMetrics.density

        binding.smartFeaturesPill.background = createOptionBackground(
            cornerSize = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._50sdp).toFloat(),
            fillColor = smartFeaturesPillFillColor,
            strokeColor = smartFeaturesPillStrokeColor,
            strokeWidth = smartFeaturesPillStrokeWidth,
            showRipple = false,
            rippleColor = smartFeaturesPillFillColor
        )



        binding.smartFeaturesText.setTextColor(
//            if (isDarkPopup) {
//                textColor
//            } else {
            color(R.color.afterCallnewBlue)
//            }
        )
        binding.txtTitleTag.setTextColor(textColor)

        binding.btnBeDefault.setTextColor(onPrimaryColor)
        binding.btnBeDefault.rippleColor = ColorStateList.valueOf(buttonRippleColor)

        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor

        window.decorView.setBackgroundColor(surfaceColor)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }

        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightSystemBars =
                ThemeModeManager.shouldUseLightSystemBars(this@permissionTwoActivity)
            isAppearanceLightStatusBars = useLightSystemBars
            isAppearanceLightNavigationBars = useLightSystemBars
        }
        screenVisibility()
    }

    fun takeOverlayPermission() {
        try {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)

            activityResultLauncherForOverlays
                .launch(intent)
            Handler(mainLooper).postDelayed({
                val intent = Intent(this, OverlayPermissionAnimationActivity::class.java)
                val options = ActivityOptions.makeCustomAnimation(
                    this,
                    R.anim.fade_in,
                    R.anim.fade_out
                )
                startActivity(intent, options.toBundle())
            }, 100)
            startOverlayPermissionWatcher()


        } catch (_: Exception) {

        }
    }

    private fun startOverlayPermissionWatcher() {
        if (!::appOps.isInitialized) return
        appOpsListener = AppOpsManager.OnOpChangedListener { op, packageName ->
            if (packageName == this.packageName) {
                // Permission granted, move to home
                if (Settings.canDrawOverlays(this)) {
                    baseConfig.apply {
//                        if (FromSpl) {
//                            if (baseConfig.PermissionAfterCount == 1) {
//                                firebaseFunnel("Message_first_perOverlay_allow")
//                            } else if (baseConfig.PermissionAfterCount > 5) {
//                                firebaseFunnel("Message_perOverlay_allow_5")
//                            } else {
//                                firebaseFunnel("Message_perOverlay_allow_${baseConfig.PermissionAfterCount}")
//                            }

                        firebaseFunnel("Overlay_allowed")
                        firebaseFunnel("Aftercall_allow")
                        logOnboardingFunnelStep("First_overlay_allowed")
//                        }
                    }

                    appOps.stopWatchingMode(appOpsListener)
                } else {

                }
                nextScreen()
            }
        }
        // Watch for changes in overlay permission
        appOps.startWatchingMode(
            AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW, packageName, appOpsListener
        )
    }

    private fun setupHintSlider() {
        binding.vpHintSlides.apply {
            clipToPadding = false
            val sidePadding = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._18sdp)
            setPadding(sidePadding, 0, sidePadding, 0)
            pageMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
            adapter = OverlayHintPagerAdapter(hintSlides)
            offscreenPageLimit = hintSlides.size
        }
        setupIndicators(selected = 0)
        binding.vpHintSlides.addOnPageChangeListener(object :
            ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                setupIndicators(selected = position)
            }
        })
    }

    private fun setupIndicators(selected: Int) {
        binding.layHintIndicator.removeAllViews()
        val isDarkPopup = ThemeModeManager.isDarkThemeActive(this)
        val activeTextColor = ContextCompat.getColor(this, R.color.afterCallnewBlue)
        val strokeColor = activeTextColor
        val fillColor = if (isDarkPopup) {
            ContextCompat.getColor(this, R.color.newPerDarkBg)
        } else {
            ContextCompat.getColor(this, R.color.newPerLightBg)
        }

        val counterText = MaterialTextView(this).apply {
            text = String.format("%02d/%02d", selected + 1, hintSlides.size)
            setTextColor(activeTextColor)
            textSize = 10f
            includeFontPadding = false
            setPadding(
                resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp),
                resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._4sdp),
                resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp),
                resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._4sdp)
            )
            background = createOptionBackground(
                cornerSize = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._20sdp)
                    .toFloat(),
                fillColor = fillColor,
                strokeColor = strokeColor,
                strokeWidth = resources.displayMetrics.density,
                showRipple = false,
                rippleColor = fillColor

            )
        }
        binding.layHintIndicator.addView(counterText)
    }

    data class OverlayHintSlide(
        @DrawableRes val imageRes: Int
    )

    inner class OverlayHintPagerAdapter(
        private val items: List<OverlayHintSlide>
    ) : PagerAdapter() {
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = LayoutInflater.from(container.context)
                .inflate(R.layout.item_permission_overlay_slide, container, false)

            val image = view.findViewById<ImageView>(R.id.imgHint)
            val item = items[position]

            image.setImageResource(item.imageRes)
            view.setOnClickListener {
                binding.btnBeDefault.performClick()
            }
            image.setOnClickListener {
                binding.btnBeDefault.performClick()
            }

            container.addView(view)
            return view
        }

        override fun getCount(): Int = items.size

        override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }

    override fun onResume() {
        super.onResume()
        inAppUpdate.onResume()
    }

    @SuppressLint("StringFormatInvalid")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        inAppUpdate.onActivityResult(requestCode, resultCode, data)
    }

}
