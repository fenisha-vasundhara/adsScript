package com.messenger.phone.number.text.sms.service.apps

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.demo.adsmanage.basemodule.BaseSharedPreferences
//import com.demo.adsmanage.Commen.firebaseFunnel
import com.messenger.phone.number.text.sms.service.apps.AfterCallScreen.permission.permissionOneActivity
import com.messenger.phone.number.text.sms.service.apps.AfterCallScreen.permission.permissionTwoActivity
import com.messenger.phone.number.text.sms.service.apps.CommanClass.baseConfig
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getBottomNavigationBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperSecondaryTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperStatusBarColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getContrastColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.logOnboardingFunnelStep
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isFLow1
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isFLow1_2
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isFLow2
import com.messenger.phone.number.text.sms.service.apps.CommanClass.sendsubactivityFromWel
import com.messenger.phone.number.text.sms.service.apps.ApplicationClass.MessagerApplication
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityWelcomeBinding
import com.messenger.phone.number.text.sms.service.apps.firebase.RemoteConfigFirebase


//@AndroidEntryPoint
class WelcomeActivity : AppCompatActivity() {
    val binding: ActivityWelcomeBinding by lazy {
        ActivityWelcomeBinding.inflate(layoutInflater)
    }
    var byUser = false
    private lateinit var appOps: AppOpsManager
    private var launchFlowReady = false
    private var unlockRequested = false
    private var unlockCompleted = false
    private var pendingAppLockLaunch = false
    private var pendingLaunchIntent: Intent? = null
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
        val splashScreen = installSplashScreen() // 👈 important

        super.onCreate(savedInstanceState)
        pendingLaunchIntent = savedInstanceState?.getString(KEY_PENDING_LAUNCH_INTENT)?.let {
            Intent.parseUri(it, Intent.URI_INTENT_SCHEME)
        } ?: Intent(intent)
        unlockCompleted = savedInstanceState?.getBoolean(KEY_UNLOCK_COMPLETED, false) ?: false
        unlockRequested = savedInstanceState?.getBoolean(KEY_UNLOCK_REQUESTED, false) ?: false
        pendingAppLockLaunch = savedInstanceState?.getBoolean(KEY_PENDING_APP_LOCK_LAUNCH, false) ?: false
        setBaseTheme(binding.vAnd15StatusBar)
        setContentView(binding.root)
//        firebaseFunnel("without_metrica")
//        firebaseFunnel("with_metrica")

        onceShowDialog = false
        ThemeSetup()
        appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager

        var isLoading = true
        var isCallbackExecuted = false   // ← Important flag
        splashScreen.setKeepOnScreenCondition {
            isLoading
        }

// === Remote Config with 5 Second Timeout ===
        val handler = android.os.Handler(android.os.Looper.getMainLooper())

        val timeoutRunnable = Runnable {
            if (!isCallbackExecuted) {
                isCallbackExecuted = true
                isLoading = false
                Log.e("REMOTE_CONFIG", "Timeout reached after 5 seconds → Forcing beNext()")
                onLaunchFlowReady()
            }
        }

        // Start 5 second timer
        handler.postDelayed(timeoutRunnable, 7000)

        // Call Remote Config
        RemoteConfigFirebase(this, {
            if (!isCallbackExecuted) {
                isCallbackExecuted = true
                handler.removeCallbacks(timeoutRunnable)  // Cancel timeout
                isLoading = false
                onLaunchFlowReady()
            }
        })

    }

    override fun onStart() {
        super.onStart()
        ThemeSetup()
        maybeLaunchPendingAppLock()
    }

    private fun ThemeSetup() {

        val surfaceColor = getProperBackgroundColor()
        val statusBarColor = getProperStatusBarColor()
        val navigationBarColor = getBottomNavigationBackgroundColor()
        val textColor = getProperTextColor()
        val secondaryTextColor = getProperSecondaryTextColor()
        val primaryColor = getProperPrimaryColor()
        val onPrimaryColor = primaryColor.getContrastColor()

        binding.main.setBackgroundColor(surfaceColor)
        binding.vAnd15StatusBar.setBackgroundColor(statusBarColor)
        binding.txtAppName.setTextColor(textColor)

        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        window.decorView.setBackgroundColor(surfaceColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightSystemBars = ThemeModeManager.shouldUseLightSystemBars(this@WelcomeActivity)
            isAppearanceLightStatusBars = useLightSystemBars
            isAppearanceLightNavigationBars = useLightSystemBars
        }
    }


    fun Context.hasPermission(permId: String) = ContextCompat.checkSelfPermission(
        this, permId
    ) == PackageManager.PERMISSION_GRANTED



    private fun hasPhonePermissions(): Boolean {
        val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE)

        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun beNext() {
        if (!baseConfig.firsttimeflowuse) {
            Log.d("TAG", "beNext: ---Wall: $isFLow2 -- $isFLow1")
        }

        logOnboardingFunnelStep("First_splash")

        if (isFLow2) {
            if (!baseConfig.firsttimeflowuse&& !BaseSharedPreferences(this).mIS_SUBSCRIBED) {
                baseConfig.firsttimeflowuse = true
//                startActivity(Intent(this@WelcomeActivity, HomeABActivity::class.java))
//                finish()
                sendsubactivityFromWel(true)
                finish()
            } else {

                baseConfig.flow2_3_firstcomeDone = true
                startActivity(Intent(this@WelcomeActivity, HomeABActivity::class.java))
                finish()
            }
        } else if (isFLow1) {
            if (!baseConfig.firsttimeflowuse) {
                baseConfig.firsttimeflowuse = true
                startActivity(Intent(this@WelcomeActivity, HomeABActivity::class.java))
                finish()
            } else {
                baseConfig.flow2_3_firstcomeDone = true
//                if (!baseConfig.secondtimeflowuse) {
//                    Log.d("TAG", "language----22222  ")
//                    baseConfig.secondtimeflowuse = true
//                    val intent = Intent(this, LanguageActivity::class.java)
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    intent.putExtra("mainopen", true)
//                    startActivity(intent)
//                    finish()
//                } else {
                    startActivity(Intent(this@WelcomeActivity, HomeABActivity::class.java))
                    finish()
//                }
            }

        } else {

            if (!hasPhonePermissions() && !baseConfig.firsttimeflowuse) {
                baseConfig.firsttimeflowuse = true
                startActivity(Intent(this@WelcomeActivity, permissionOneActivity::class.java))
                finish()
            } else {
                if (Settings.canDrawOverlays(this) && hasPhonePermissions()) {
                    startActivity(Intent(this@WelcomeActivity, HomeABActivity::class.java))
                    finish()
                } else {
                    startActivity(
                        Intent(
                            this@WelcomeActivity,
                            permissionTwoActivity::class.java
                        )
                    )
                    finish()
                }
            }

        }


    }

    private fun onLaunchFlowReady() {
        launchFlowReady = true
        pendingAppLockLaunch = true
        maybeStartAppLockGate()
    }

    private fun maybeStartAppLockGate() {
        if (!launchFlowReady) {
            return
        }

        if (!pendingAppLockLaunch) {
            return
        }

        if (isFinishing || isDestroyed) {
            return
        }

        val app = application as? MessagerApplication
        if (app == null || !app.isFullAppLockEnabled() || unlockCompleted) {
            pendingAppLockLaunch = false
            continueNormalLaunch()
            return
        }

        if (unlockRequested) {
            return
        }

        if (!app.tryBeginFullAppLockFlow()) {
            pendingAppLockLaunch = false
            continueNormalLaunch()
            return
        }

        pendingAppLockLaunch = false
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

        pendingAppLockLaunch = false
        pendingLaunchIntent?.let {
            intent = it
        }
        beNext()
    }

    private fun maybeLaunchPendingAppLock() {
        maybeStartAppLockGate()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_PENDING_LAUNCH_INTENT, pendingLaunchIntent?.toUri(Intent.URI_INTENT_SCHEME))
        outState.putBoolean(KEY_UNLOCK_COMPLETED, unlockCompleted)
        outState.putBoolean(KEY_UNLOCK_REQUESTED, unlockRequested)
        outState.putBoolean(KEY_PENDING_APP_LOCK_LAUNCH, pendingAppLockLaunch)
    }

    companion object {
        private const val KEY_PENDING_LAUNCH_INTENT = "pending_launch_intent"
        private const val KEY_UNLOCK_COMPLETED = "unlock_completed"
        private const val KEY_UNLOCK_REQUESTED = "unlock_requested"
        private const val KEY_PENDING_APP_LOCK_LAUNCH = "pending_app_lock_launch"
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStop() {
        super.onStop()
    }
}
