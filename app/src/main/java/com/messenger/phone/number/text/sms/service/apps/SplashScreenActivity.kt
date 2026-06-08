package com.messenger.phone.number.text.sms.service.apps

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.demo.adsmanage.Commen.firebaseEventMain
import com.demo.adsmanage.Commen.isValidforExperiment
import com.demo.adsmanage.Commen.log
import com.demo.adsmanage.basemodule.BaseSharedPreferences
import com.demo.adsmanage.helper.logD
import com.messenger.phone.number.text.sms.service.apps.ApplicationClass.MessagerApplication
import com.messenger.phone.number.text.sms.service.apps.AfterCallScreen.permission.PermissionAfterNewActivity
import com.messenger.phone.number.text.sms.service.apps.CommanClass.baseConfig
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isOnline
import com.messenger.phone.number.text.sms.service.apps.CommanClass.updateStatusbarColorForLockScreen
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivitySplashScreenBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashScreenBinding
    private var pendingLaunchIntent: Intent? = null
    private var launchFlowReady = false
    private var unlockRequested = false
    private var unlockCompleted = false
    private val appLockLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val app = application as? MessagerApplication
            unlockRequested = false

            if (result.resultCode == RESULT_OK) {
                unlockCompleted = true
                app?.onFullAppLockVerified()
                continueNormalLaunch()
            } else {
                app?.onFullAppLockCancelled()
                finishAffinity()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pendingLaunchIntent = savedInstanceState?.getString(KEY_PENDING_LAUNCH_INTENT)?.let {
            Intent.parseUri(it, Intent.URI_INTENT_SCHEME)
        } ?: Intent(intent)
        unlockCompleted = savedInstanceState?.getBoolean(KEY_UNLOCK_COMPLETED, false) ?: false
        unlockRequested = savedInstanceState?.getBoolean(KEY_UNLOCK_REQUESTED, false) ?: false
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen)

        logD("Bob_the_builder", "1")
        if (baseConfig.firsttimeflowuse) {
            logD("Bob_the_builder", "2")
            if (Settings.canDrawOverlays(this)) {
                if (BaseSharedPreferences(this).mIS_SUBSCRIBED || !isOnline()) {
                binding.constAdViewBottom.gone()
            return
        }
//        preloadBannerForPlacement(
//                    "splash_banner", this, ArrayList(listOfSplashBanner),
//                    config.isSplashBannerAdOn, false
//                )
                onLaunchFlowReady()
            } else {
                startActivity(
                    Intent(
                        this@SplashScreenActivity,
                        PermissionAfterNewActivity::class.java
                    )
                )
                finishAffinity()
            }

            binding.constAdViewBottom.gone()
        } else {
            logD("Bob_the_builder", "6")

            loadBannerHome()

            Handler(Looper.getMainLooper()).postDelayed({
                baseConfig.FirstTimeSplashShow = false
                if (Settings.canDrawOverlays(this)) {

                } else {

                }
                onLaunchFlowReady()
            }, 7000)
        }

    }

    private fun loadBannerHome() {
        if (BaseSharedPreferences(this).mIS_SUBSCRIBED || !isOnline()) {
                binding.constAdViewBottom.gone()
            return
        }
//        preloadBannerForPlacement(
//            "splash_banner", this, ArrayList(listOfSplashBanner),
//            config.isSplashBannerAdOn, false
//        )
//        showMultiUnitBannerAdvance(
//            this,
//            "splash_banner",
//            binding.frameAdBottom,
//            binding.bannerShimmerBottom,
//            binding.constAdViewBottom,
//            binding.shimmerContainer, config.isSplashBannerAdOn
//        )
    }

    private fun NextActivityNew() {
//        val flowcheck = "3"
        val flowcheck = baseConfig.PermissionOnboardingFlow_VS_ExperimentOnboardingFlow_MS
        "PermissionOnboardingFlow_VS_ExperimentOnboardingFlow_MS 2 <----------> ${flowcheck}".log()
        baseConfig.use_flow_count = flowcheck
        if (!baseConfig.firsttimeflowuse) {
            baseConfig.firsttimeflowuse = true
            when (flowcheck) {
                "1" -> {
                    val intent = Intent(this, LanguageActivity::class.java)
                    intent.putExtra("mainopen", true)
                    startActivity(intent)
                    finish()
                }

                "2" -> {
                    val intent = Intent(this, LanguageActivity::class.java)
                    intent.putExtra("mainopen", false)
                    intent.putExtra("flowfinish", true)
                    startActivity(intent)
                    finish()
                }

                "3" -> {
                    val intent = Intent(this, LanguageActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                else -> {


                }
            }
        } else {
            when (baseConfig.use_flow_count) {
                "1" -> {
                    val intent = Intent(this, LanguageActivity::class.java)
                    intent.putExtra("mainopen", false)
                    intent.putExtra("flowfinish", true)
                    startActivity(intent)
                    finish()
                }

                "2" -> {
                    val intent = Intent(this, LanguageActivity::class.java)
                    intent.putExtra("mainopen", false)
                    intent.putExtra("flowfinish", true)
                    startActivity(intent)
                    finish()
                }

                "3" -> {
                    val intent = Intent(this, LanguageActivity::class.java)
                    intent.putExtra("mainopen", false)
                    intent.putExtra("flowfinish", true)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun onLaunchFlowReady() {
        launchFlowReady = true
        maybeStartAppLockGate()
    }

    private fun maybeStartAppLockGate() {
        if (!launchFlowReady) {
            return
        }

        val app = application as? MessagerApplication
        if (app == null || !app.isFullAppLockEnabled() || unlockCompleted) {
            continueNormalLaunch()
            return
        }

        if (unlockRequested) {
            return
        }

        if (!app.tryBeginFullAppLockFlow()) {
            continueNormalLaunch()
            return
        }

        unlockRequested = true
        appLockLauncher.launch(
            app.createFullAppLockIntent(
                context = this,
                returnResult = true,
                appOpen = true
            )
        )
    }

    private fun continueNormalLaunch() {
        if (!launchFlowReady) {
            return
        }

        pendingLaunchIntent?.let {
            intent = it
        }
        NextActivityNew()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_PENDING_LAUNCH_INTENT, pendingLaunchIntent?.toUri(Intent.URI_INTENT_SCHEME))
        outState.putBoolean(KEY_UNLOCK_COMPLETED, unlockCompleted)
        outState.putBoolean(KEY_UNLOCK_REQUESTED, unlockRequested)
    }

    companion object {
        private const val KEY_PENDING_LAUNCH_INTENT = "pending_launch_intent"
        private const val KEY_UNLOCK_COMPLETED = "unlock_completed"
        private const val KEY_UNLOCK_REQUESTED = "unlock_requested"
    }

    override fun onStart() {
        super.onStart()
        ThemeSetup()
    }

    private fun ThemeSetup() {
        updateStatusbarColorForLockScreen()
    }

}
