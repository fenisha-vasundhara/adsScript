package com.messenger.phone.number.text.sms.service.apps.AfterCallScreen.permission

import android.app.ActivityOptions
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.demo.adsmanage.Commen.firebaseFunnel
import com.demo.adsmanage.basemodule.BaseSharedPreferences
import com.google.android.material.color.DynamicColors

import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.CommanClass.baseConfig
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createOptionBackground
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getContrastColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isOnline
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.HomeABActivity
import com.messenger.phone.number.text.sms.service.apps.OverlayPermissionAnimationActivity
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityNewAftercallSceenBinding
import com.messenger.phone.number.text.sms.service.apps.setBaseTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlin.jvm.java

@AndroidEntryPoint
class PermissionAfterNewActivity : AppCompatActivity() {

    lateinit var binding: ActivityNewAftercallSceenBinding

    private lateinit var appOps: AppOpsManager
    lateinit var appOpsListener: AppOpsManager.OnOpChangedListener
    private val overlayAnimHandler by lazy { Handler(Looper.getMainLooper()) }
    private var overlayAnimRunnable: Runnable? = null
    var FromSpl: Boolean = false
    private var activityResultLauncherForOverlays =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Settings.canDrawOverlays(this@PermissionAfterNewActivity)) {

            } else {
                if (!::appOps.isInitialized) return@registerForActivityResult
                if (!::appOpsListener.isInitialized) return@registerForActivityResult

                appOps.stopWatchingMode(appOpsListener)
                gomainSCreen()
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

        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_aftercall_sceen)
        setBaseTheme(binding.vAnd15StatusBar)

        ThemeSetup()

        FromSpl = intent.getBooleanExtra("FromSpl", false)
//        binding.txtTitleTag.setTextColor(Color.BLACK)
        if (FromSpl) {
            if (baseConfig.PermissionAfterCount == 1) {
                firebaseFunnel("First_perOverlay")

            } else if (baseConfig.PermissionAfterCount > 5) {
                firebaseFunnel("Message_perOverlay_5")

            } else {
                firebaseFunnel("Message_perOverlay_${baseConfig.PermissionAfterCount}")

            }


        }




        initPermission()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                gomainSCreenBack()

            }
        })

    }


    private fun initPermission() {
        loadBannerAd()
        binding.btnFramePermissions.setOnClickListener {
            permissionCheck()
        }


        binding.btnBeDefault.setOnClickListener {
            permissionCheck()
        }


        binding.imgVector.setOnClickListener {
            permissionCheck()
        }
    }

    private fun permissionCheck() {
        if (Settings.canDrawOverlays(this@PermissionAfterNewActivity)) {
            gomainSCreen()
        } else {
            takeOverlayPermission()
        }
    }

    private fun gomainSCreen() {
//        if (baseConfig.firsttimeflowuse) {
//            if (!baseConfig.secondtimeflowuse) {
//                baseConfig.secondtimeflowuse = true
//                val intent = Intent(this, LanguageActivity::class.java).apply {
//                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                }
//                intent.putExtra("mainopen", true)
//                startActivity(intent)
//                finish()
//            } else {
        startActivity(Intent(this, HomeABActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
//            }
//        } else {
//            val intent = Intent(this, LanguageActivity::class.java).apply {
//                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            }
//            intent.putExtra("mainopen", true)
//            startActivity(intent)
//            finish()
//        }
    }


    private fun gomainSCreenBack() {
        if (!Settings.canDrawOverlays(this@PermissionAfterNewActivity)) {
            baseConfig.apply {
                if (FromSpl) {
                    if (baseConfig.PermissionAfterCount == 1) {
                        firebaseFunnel("Message_first_perOverlay_disallow")

                    } else if (baseConfig.PermissionAfterCount > 5) {
                        firebaseFunnel("Message_perOverlay_disallow_5")

                    } else {
                        firebaseFunnel("Message_perOverlay_disallow_${baseConfig.PermissionAfterCount}")

                    }


                }


            }
        }
        startActivity(Intent(this, HomeABActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()

    }


    override fun onStart() {
        super.onStart()
        ThemeSetup()
    }

    override fun onDestroy() {
        overlayAnimRunnable?.let { overlayAnimHandler.removeCallbacks(it) }
        super.onDestroy()
    }

    private fun ThemeSetup() {
        val isDarkPopup = ThemeModeManager.isDarkThemeActive(this)
        val color = { resId: Int -> ContextCompat.getColor(this, resId) }
        val bgforperColor = color(
            if (isDarkPopup) R.color.newPerDarkBg else R.color.newPerLightBg
        )
        val surfaceColor = bgforperColor
        val statusBarColor = bgforperColor
        val navigationBarColor = bgforperColor
        val primaryColor = color(R.color.afterCallnewBlue)
        val textColor = getProperTextColor()
        val onPrimaryColor = primaryColor.getContrastColor()
        val buttonRippleColor = primaryColor.adjustAlpha(0.22f)
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
        val cardBackgroundColor = color(
            if (isDarkPopup) {
                R.color.aftercall_permission_card_fill_dark
            } else {
                R.color.aftercall_permission_card_fill_light
            }
        )
        val cardStrokeColor = color(
            if (isDarkPopup) {
                R.color.aftercall_permission_card_stroke_dark
            } else {
                R.color.aftercall_permission_card_stroke_light
            }
        )

        binding.main.setBackgroundColor(surfaceColor)
        binding.vAnd15StatusBar.setBackgroundColor(statusBarColor)
        binding.smartFeaturesPill.background = createOptionBackground(
            cornerSize = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._50sdp).toFloat(),
            fillColor = smartFeaturesPillFillColor,
            strokeColor = smartFeaturesPillStrokeColor,
            strokeWidth = smartFeaturesPillStrokeWidth,
            showRipple = false,
            rippleColor = smartFeaturesPillFillColor
        )
        binding.smartFeaturesText.setTextColor(
            if (isDarkPopup) {
                textColor
            } else {
                color(R.color.afterCallnewBlue)
            }
        )
        binding.txtTitle2.setTextColor(textColor)
        binding.includedFeaturesTitle.setTextColor(textColor)
        binding.featureText1.setTextColor(textColor)
        binding.featureText2.setTextColor(textColor)
        binding.featureText3.setTextColor(textColor)

        binding.txtTitleTag.setTextColor(
            if (isDarkPopup) {
                Color.parseColor("#D6D6D6")
            } else {
                Color.parseColor("#575757")
            }
        )
        binding.imgVector.setImageResource(
            if (isDarkPopup) {
                R.drawable.ic_vec_new_per_aftercall_dark
            } else {
                R.drawable.ic_vec_new_per_aftercall_light
            }
        )
        binding.line1.setImageResource(
            if (isDarkPopup) {
                R.drawable.ic_line_2
            } else {
                R.drawable.ic_line73
            }
        )

        binding.line2.setImageResource(
            if (isDarkPopup) {
                R.drawable.ic_line_2
            } else {
                R.drawable.ic_line73
            }
        )
//        binding.cardDialogBackground.setCardBackgroundColor(cardBackgroundColor)
        binding.cardDialogBackground.strokeColor = cardStrokeColor

        binding.btnBeDefault.setTextColor(onPrimaryColor)
        binding.btnBeDefault.rippleColor = ColorStateList.valueOf(buttonRippleColor)
//        ((binding.btnFramePermissions.getChildAt(0) as? com.facebook.shimmer.ShimmerFrameLayout)
//            ?.getChildAt(0) as? com.google.android.material.card.MaterialCardView)
//            ?.setCardBackgroundColor(primaryColor)

        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor

        window.decorView.setBackgroundColor(surfaceColor)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }

        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightSystemBars =
                ThemeModeManager.shouldUseLightSystemBars(this@PermissionAfterNewActivity)
            isAppearanceLightStatusBars = useLightSystemBars
            isAppearanceLightNavigationBars = useLightSystemBars
        }

    }

    fun loadBannerAd() {

        Log.d(
            "AdsGenNew__",
            "loadBanner_init_permissionAfter"
        )
        if (BaseSharedPreferences(this).mIS_SUBSCRIBED || !isOnline()) {
            binding.constAdViewBottom.gone()
            return
        }
        binding.constAdViewBottom.gone()

        /*   BannerCache.peekAnyCachedEntry(excludeKey = "perAfter")?.let { cached ->
               Log.d(
                   "AdsGenNew__",
                   "perAfter cached key=${cached.key} slot=${cached.slotLabel} adUnitId=${cached.adUnitId}"
               )


           }

           if (BannerCache.showCachedIfAny(
                   shimmer = binding.bannerShimmerBottom,
                   container = binding.frameAdBottom,
                   activity = this,
                   displayKey = "perAfter",
                   excludeKey = "fff",
                   useCachedKeyForDisplay = true
               )
           ) {

               return
           }
           else {

               binding.bannerShimmerBottom.visibility = View.VISIBLE
               binding.bannerShimmerBottom.startShimmer()

               BannerCache.waitForAnyLoad(
                   scope = lifecycleScope,
                   excludeKey = "fff",
                   timeoutMs = 5_000L
               ) {
                   if (BannerCache.showCachedIfAny(
                           shimmer = binding.bannerShimmerBottom,
                           container = binding.frameAdBottom,
                           activity = this,
                           displayKey = "perAfter",
                           excludeKey = "fff",
                           useCachedKeyForDisplay = true
                       )
                   ) {
                       return@waitForAnyLoad
                   }

                   BannerCache.checkAndShowBanner(
                       scope = lifecycleScope,
                       shimmer = binding.bannerShimmerBottom,
                       container = binding.frameAdBottom,
                       activity = this,
                       key = "perAfter",
                       adUnitIds = listOfPerBanner,
                       *//* nativeFallbackAdUnitId = per_native_1,*//*
                    onNoAdAvailable = { binding.constAdViewBottom.visibility = View.GONE }
                )
            }

//            BannerCache.checkAndShowBanner(
//                scope = lifecycleScope,
//                shimmer = binding.bannerShimmerBottom,
//                container = binding.frameAdBottom,
//                activity = this,
//                key = "perAfter",
//                adUnitIds = listOfPerBanner,
//                nativeFallbackAdUnitId = per_native_1,
//                onNoAdAvailable = { binding.constAdViewBottom.visibility = View.GONE }
//            )
        }
*/

    }

    fun takeOverlayPermission() {
        try {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")
            )
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

//        val intent = Intent(
//            Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")
//        )
//        activityResultLauncherForOverlays
//            .launch(intent)
//        overlayAnimRunnable?.let { overlayAnimHandler.removeCallbacks(it) }
//        overlayAnimRunnable = Runnable {
//            if (!isFinishing && !isDestroyed && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) && hasWindowFocus()) {
//                val animIntent = Intent(this, OverlayPermissionAnimationActivity::class.java)
//                val options = ActivityOptions.makeCustomAnimation(
//                    this,
//                    R.anim.fade_in,
//                    R.anim.fade_out
//                )
//                startActivity(animIntent, options.toBundle())
//            }
//        }
//        overlayAnimHandler.postDelayed(overlayAnimRunnable!!, 700)
//        startOverlayPermissionWatcher()


    }

    private fun startOverlayPermissionWatcher() {
        if (!::appOps.isInitialized) return
        appOpsListener = AppOpsManager.OnOpChangedListener { op, packageName ->
            if (packageName == this.packageName) {
                // Permission granted, move to home
                if (Settings.canDrawOverlays(this)) {
                    baseConfig.apply {
                        if (FromSpl) {
                            if (baseConfig.PermissionAfterCount == 1) {
                                firebaseFunnel("Message_first_perOverlay_allow")
                            } else if (baseConfig.PermissionAfterCount > 5) {
                                firebaseFunnel("Message_perOverlay_allow_5")
                            } else {
                                firebaseFunnel("Message_perOverlay_allow_${baseConfig.PermissionAfterCount}")
                            }

                            firebaseFunnel("Aftercall_allow")
                        }
                    }

                    appOps.stopWatchingMode(appOpsListener)
                } else {

                }
                gomainSCreen()
            }
        }
        // Watch for changes in overlay permission
        appOps.startWatchingMode(
            AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW, packageName, appOpsListener
        )
    }


}
