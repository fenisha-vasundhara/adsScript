package com.messenger.phone.number.text.sms.service.apps.ads.v2

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.demo.adsmanage.Activity.Experiment_Sub_Screen_Activity
import com.demo.adsmanage.Activity.Paywall_FourPlan_Activity
import com.demo.adsmanage.Commen.Constants
import com.demo.adsmanage.basemodule.BaseSharedPreferences
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.messenger.phone.number.text.sms.service.apps.AfterCallScreen.permission.PermissionAfterNewActivity
import com.messenger.phone.number.text.sms.service.apps.AfterCallScreen.permission.permissionOneActivity
import com.messenger.phone.number.text.sms.service.apps.AfterCallScreen.permission.permissionTwoActivity
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.firebaseConfig
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isOnline
import com.messenger.phone.number.text.sms.service.apps.GoogleMobileAdsConsentManagerChack.GoogleMobileAdsConsentManager
import com.messenger.phone.number.text.sms.service.apps.LanguageActivity
import com.messenger.phone.number.text.sms.service.apps.LockScreenActivity
import com.messenger.phone.number.text.sms.service.apps.OverlayPermissionAnimationActivity
import com.messenger.phone.number.text.sms.service.apps.PrivacyChatActivity
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.SearchActivity
import com.messenger.phone.number.text.sms.service.apps.SelectContactActivity
import com.messenger.phone.number.text.sms.service.apps.SendMessageActivity
import com.messenger.phone.number.text.sms.service.apps.WelcomeActivity
import com.messenger.phone.number.text.sms.service.apps.databinding.DialogLoadingAdBinding
import com.messenger.phone.number.text.sms.service.apps.subscription.Paywall_dynamic_Activity
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.jvm.java

class AdsOrchestrator private constructor(private val app: Application) {

    private val mainHandler = Handler(Looper.getMainLooper())
    private val prefs = app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val inFlightByPlacement = ConcurrentHashMap<AdPlacement, AtomicBoolean>()
    private val isFullScreenShowing = AtomicBoolean(false)
    private val processLaunchMarked = AtomicBoolean(false)

    private var appOpenAd: AppOpenAd? = null
    private val isAppOpenLoading = AtomicBoolean(false)
    private val appOpenPendingForForeground = AtomicBoolean(false)
    @Volatile
    private var appOpenForegroundDeadlineAt = 0L
    @Volatile
    private var resumedActivityRef: WeakReference<Activity>? = null

    fun markProcessLaunch() {
        if (!processLaunchMarked.compareAndSet(false, true)) {
            return
        }
        val count = prefs.getInt(KEY_APP_LAUNCH_COUNT, 0) + 1
        prefs.edit().putInt(KEY_APP_LAUNCH_COUNT, count).apply()
        log("launch_count=$count")
    }

    fun onAppBackgrounded() {
        appOpenPendingForForeground.set(false)
        appOpenForegroundDeadlineAt = 0L
        resumedActivityRef = null
        prefs.edit().putLong(KEY_LAST_BACKGROUND_TS, System.currentTimeMillis()).apply()
    }

    fun onAppForegrounded(activity: Activity) {
        appOpenPendingForForeground.set(true)
        appOpenForegroundDeadlineAt = System.currentTimeMillis() + APP_OPEN_FOREGROUND_WINDOW_MS
        preloadAppOpenIfEligible(activity)
    }

    fun onActivityResumed(activity: Activity) {
        resumedActivityRef = WeakReference(activity)
        tryShowPendingAppOpen("activity_resumed")
    }

    fun showHomeBanner(
        activity: Activity,
        ui: AdUiBinding,
        trigger: String,
        onNoAd: () -> Unit = {}
    ) {
        requestBanner(
            activity = activity,
            placement = AdPlacement.HOME_LARGE_BANNER,
            screenName = SCREEN_HOME,
            ui = ui,
            trigger = trigger,
            onNoAd = onNoAd
        )
    }

    fun showLanguageNative(
        activity: Activity,
        ui: AdUiBinding,
        onNoAd: () -> Unit = {}
    ) {
        requestNativeSmall(
            activity = activity,
            placement = AdPlacement.LANGUAGE_NATIVE,
            screenName = SCREEN_LANGUAGE,
            ui = ui,
            trigger = "list_visible",
            onNoAd = onNoAd
        )
    }

    fun showSettingNative(
        activity: Activity,
        ui: AdUiBinding,
        trigger: String,
        onNoAd: () -> Unit = {}
    ) {
        requestNativeSmall(
            activity = activity,
            placement = AdPlacement.SETTING_NATIVE,
            screenName = SCREEN_SETTING,
            ui = ui,
            trigger = trigger,
            onNoAd = onNoAd
        )
    }

    fun showViewDetailsNative(
        activity: Activity,
        ui: AdUiBinding,
        trigger: String,
        onNoAd: () -> Unit = {}
    ) {
        requestNativeSmall(
            activity = activity,
            placement = AdPlacement.VIEW_DETAILS_NATIVE,
            screenName = SCREEN_VIEW_DETAILS,
            ui = ui,
            trigger = trigger,
            onNoAd = onNoAd
        )
    }

    fun showArchiveExitInterstitial(activity: Activity, onContinue: () -> Unit) {
        requestExitInterstitial(
            activity = activity,
            placement = AdPlacement.ARCHIVE_EXIT_INTERSTITIAL,
            screenName = SCREEN_ARCHIVE,
            onContinue = onContinue
        )
    }

    fun showRecycleExitInterstitial(activity: Activity, onContinue: () -> Unit) {
        requestExitInterstitial(
            activity = activity,
            placement = AdPlacement.RECYCLE_EXIT_INTERSTITIAL,
            screenName = SCREEN_RECYCLE,
            onContinue = onContinue
        )
    }

    fun showBackupRewarded(activity: Activity, onContinue: () -> Unit) {
        requestRewarded(
            activity = activity,
            placement = AdPlacement.BACKUP_REWARDED,
            screenName = SCREEN_BACKUP,
            onContinue = onContinue
        )
    }

    fun showScheduleRewarded(activity: Activity, onContinue: () -> Unit) {
        requestRewarded(
            activity = activity,
            placement = AdPlacement.SCHEDULE_REWARDED,
            screenName = SCREEN_SCHEDULE,
            onContinue = onContinue
        )
    }

    fun showTranslationRewarded(activity: Activity, onContinue: () -> Unit) {
        requestRewarded(
            activity = activity,
            placement = AdPlacement.TRANSLATION_REWARDED,
            screenName = SCREEN_TRANSLATION,
            onContinue = onContinue
        )
    }

    fun maybeShowAppOpen(activity: Activity) {
        if (!isActivityUsable(activity)) {
            log("app_open_block: unusable_activity")
            return
        }
        if (!isEligibleForPlacement(
                context = activity,
                placement = AdPlacement.APP_OPEN_RESUME,
                screenName = activity::class.java.simpleName,
                userInitiated = false
            )
        ) {
            return
        }
        if (isFullScreenShowing.get() || Constants.isAdsShowing) {
            log("app_open_block: fullscreen_showing")
            return
        }

        val ad = appOpenAd
        if (ad == null) {
            log("app_open_wait: cache_empty_preload")
            preloadAppOpenIfEligible(activity)
            return
        }

        appOpenAd = null
        isFullScreenShowing.set(true)
        Constants.isAdsShowing = true
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                isFullScreenShowing.set(false)
                Constants.isAdsShowing = false
                markShown(AdPlacement.APP_OPEN_RESUME)
                appOpenPendingForForeground.set(false)
                preloadAppOpenIfEligible(activity)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                isFullScreenShowing.set(false)
                Constants.isAdsShowing = false
                log("app_open_show_failed code=${adError.code} message=${adError.message}")
                preloadAppOpenIfEligible(activity)
            }

            override fun onAdShowedFullScreenContent() {
                markShown(AdPlacement.APP_OPEN_RESUME)
                appOpenPendingForForeground.set(false)
            }
        }
        ad.show(activity)
    }

    fun preloadAppOpenIfEligible(context: Context) {
        if (!isEligibleForPlacement(
                context = context,
                placement = AdPlacement.APP_OPEN_RESUME,
                screenName = context::class.java.simpleName,
                userInitiated = false,
                skipCooldownCheck = true,
                forPreload = true
            )
        ) {
            log("app_open_preload_blocked screen=${context::class.java.simpleName}")
            return
        }
        if (appOpenAd != null || !isAppOpenLoading.compareAndSet(false, true)) {
            return
        }

        val ids = AdsUnitCatalog.waterfall(context, AdPlacement.APP_OPEN_RESUME)
        if (ids.isEmpty()) {
            isAppOpenLoading.set(false)
            return
        }

        loadAppOpenSequential(context.applicationContext, ids, 0)
    }

    private fun requestBanner(
        activity: Activity,
        placement: AdPlacement,
        screenName: String,
        ui: AdUiBinding,
        trigger: String,
        onNoAd: () -> Unit
    ) {
        if (!isEligibleForPlacement(activity, placement, screenName, userInitiated = false)) {
            hideAdUi(ui)
            onNoAd()
            return
        }

        if (!tryAcquireInFlight(placement)) {
            return
        }

        showLoading(ui)

        val ids = AdsUnitCatalog.waterfall(activity, placement)
        if (ids.isEmpty()) {
            releaseInFlight(placement)
            hideAdUi(ui)
            onNoAd()
            return
        }

        val adWidthDp =
            (activity.resources.displayMetrics.widthPixels / activity.resources.displayMetrics.density)
                .toInt()
                .coerceAtLeast(320)
        val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidthDp)

        loadBannerSequential(
            activity = activity,
            ids = ids,
            index = 0,
            adSize = adSize,
            onLoaded = { adView ->
                runOnMain {
                    if (!isActivityUsable(activity)) {
                        adView.destroy()
                        releaseInFlight(placement)
                        onNoAd()
                        return@runOnMain
                    }
                    renderBanner(ui, adView)
                    markShown(placement)
                    releaseInFlight(placement)
                    log("banner_shown placement=${placement.name} trigger=$trigger")
                }
            },
            onFailed = {
                runOnMain {
                    hideAdUi(ui)
                    releaseInFlight(placement)
                    onNoAd()
                }
            }
        )
    }

    private fun requestNativeSmall(
        activity: Activity,
        placement: AdPlacement,
        screenName: String,
        ui: AdUiBinding,
        trigger: String,
        onNoAd: () -> Unit
    ) {
        if (!isEligibleForPlacement(activity, placement, screenName, userInitiated = false)) {
            hideAdUi(ui)
            onNoAd()
            return
        }

        if (!tryAcquireInFlight(placement)) {
            return
        }

        showLoading(ui)

        val ids = AdsUnitCatalog.waterfall(activity, placement)
        if (ids.isEmpty()) {
            releaseInFlight(placement)
            hideAdUi(ui)
            onNoAd()
            return
        }

        loadNativeSequential(
            activity = activity,
            ids = ids,
            index = 0,
            onLoaded = { nativeAd ->
                runOnMain {
                    if (!isActivityUsable(activity)) {
                        nativeAd.destroy()
                        releaseInFlight(placement)
                        onNoAd()
                        return@runOnMain
                    }
                    renderNativeSmall(ui, activity, nativeAd)
                    markShown(placement)
                    releaseInFlight(placement)
                    log("native_shown placement=${placement.name} trigger=$trigger")
                }
            },
            onFailed = {
                runOnMain {
                    hideAdUi(ui)
                    releaseInFlight(placement)
                    onNoAd()
                }
            }
        )
    }

    private fun requestExitInterstitial(
        activity: Activity,
        placement: AdPlacement,
        screenName: String,
        onContinue: () -> Unit
    ) {
        if (!isEligibleForPlacement(activity, placement, screenName, userInitiated = false)) {
            onContinue()
            return
        }

        if (isFullScreenShowing.get() || Constants.isAdsShowing || !tryAcquireInFlight(placement)) {
            onContinue()
            return
        }

        val ids = AdsUnitCatalog.waterfall(activity, placement)
        if (ids.isEmpty()) {
            releaseInFlight(placement)
            onContinue()
            return
        }

        val loadingDialog = activity.loadingDialog()

        runOnMain {
            if (isActivityUsable(activity)) {
                loadingDialog.show()
            }
        }

        loadInterstitialSequential(
            activity = activity,
            ids = ids,
            index = 0,
            onLoaded = { interstitial ->
                runOnMain { loadingDialog.dismiss() }
                if (!isActivityUsable(activity)) {
                    releaseInFlight(placement)
                    onContinue()
                    return@loadInterstitialSequential
                }

                isFullScreenShowing.set(true)
                Constants.isAdsShowing = true
                interstitial.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        isFullScreenShowing.set(false)
                        Constants.isAdsShowing = false
                        markShown(placement)
                        releaseInFlight(placement)
                        onContinue()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        isFullScreenShowing.set(false)
                        Constants.isAdsShowing = false
                        releaseInFlight(placement)
                        onContinue()
                    }

                    override fun onAdShowedFullScreenContent() {
                        markShown(placement)
                    }
                }
                interstitial.show(activity)
            },
            onFailed = {
                runOnMain { loadingDialog.dismiss() }
                releaseInFlight(placement)
                onContinue()
            }
        )
    }

    private fun requestRewarded(
        activity: Activity,
        placement: AdPlacement,
        screenName: String,
        onContinue: () -> Unit
    ) {
        if (!isEligibleForPlacement(activity, placement, screenName, userInitiated = true)) {
            onContinue()
            return
        }

        if (isFullScreenShowing.get() || Constants.isAdsShowing || !tryAcquireInFlight(placement)) {
            onContinue()
            return
        }

        val ids = AdsUnitCatalog.waterfall(activity, placement)
        if (ids.isEmpty()) {
            releaseInFlight(placement)
            onContinue()
            return
        }

        val loadingDialog = activity.loadingDialog()

        runOnMain {
            if (isActivityUsable(activity)) {
                loadingDialog.show()
            }
        }

        loadRewardedSequential(
            activity = activity,
            ids = ids,
            index = 0,
            onLoaded = { rewardedAd ->
                runOnMain { loadingDialog.dismiss() }
                if (!isActivityUsable(activity)) {
                    releaseInFlight(placement)
                    onContinue()
                    return@loadRewardedSequential
                }
                isFullScreenShowing.set(true)
                Constants.isAdsShowing = true
                rewardedAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        isFullScreenShowing.set(false)
                        Constants.isAdsShowing = false
                        markShown(placement)
                        releaseInFlight(placement)
                        onContinue()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        isFullScreenShowing.set(false)
                        Constants.isAdsShowing = false
                        releaseInFlight(placement)
                        onContinue()
                    }

                    override fun onAdShowedFullScreenContent() {
                        markShown(placement)
                    }
                }
                rewardedAd.show(activity) { _ ->
                    // No gating logic based on reward amount. Action always continues on dismiss.
                }
            },
            onFailed = {
                runOnMain { loadingDialog.dismiss() }
                releaseInFlight(placement)
                onContinue()
            }
        )
    }

    private fun loadAppOpenSequential(context: Context, ids: List<String>, index: Int) {
        if (index >= ids.size) {
            isAppOpenLoading.set(false)
            return
        }
        val adUnit = ids[index]
        AppOpenAd.load(
            context,
            adUnit,
            AdRequest.Builder().build(),
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(loadedAd: AppOpenAd) {
                    appOpenAd = loadedAd
                    isAppOpenLoading.set(false)
                    log("app_open_loaded")
                    tryShowPendingAppOpen("load_completed")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    log("app_open_load_failed code=${loadAdError.code} message=${loadAdError.message} idx=$index")
                    loadAppOpenSequential(context, ids, index + 1)
                }
            }
        )
    }

    private fun tryShowPendingAppOpen(trigger: String) {
        if (!appOpenPendingForForeground.get()) {
            return
        }
        val now = System.currentTimeMillis()
        if (now > appOpenForegroundDeadlineAt) {
            appOpenPendingForForeground.set(false)
            log("app_open_pending_expired trigger=$trigger")
            return
        }
        val activity = resumedActivityRef?.get() ?: return
        if (!isActivityUsable(activity)) {
            return
        }
        maybeShowAppOpen(activity)
    }

    private fun loadBannerSequential(
        activity: Activity,
        ids: List<String>,
        index: Int,
        adSize: AdSize,
        onLoaded: (AdView) -> Unit,
        onFailed: () -> Unit
    ) {
        if (index >= ids.size) {
            onFailed()
            return
        }

        val adView = AdView(activity)
        adView.setAdSize(adSize)
        adView.adUnitId = ids[index]
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                onLoaded(adView)
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                adView.destroy()
                loadBannerSequential(activity, ids, index + 1, adSize, onLoaded, onFailed)
            }
        }
        adView.loadAd(AdRequest.Builder().build())
    }

    private fun loadNativeSequential(
        activity: Activity,
        ids: List<String>,
        index: Int,
        onLoaded: (NativeAd) -> Unit,
        onFailed: () -> Unit
    ) {
        if (index >= ids.size) {
            onFailed()
            return
        }

        val adLoader = AdLoader.Builder(activity, ids[index])
            .forNativeAd { nativeAd ->
                onLoaded(nativeAd)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {


                    Log.d(TAG, "onNativeAds: ERROR - $index - ${error.message}")
                    loadNativeSequential(activity, ids, index + 1, onLoaded, onFailed)
                }
            })
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun loadInterstitialSequential(
        activity: Activity,
        ids: List<String>,
        index: Int,
        onLoaded: (InterstitialAd) -> Unit,
        onFailed: () -> Unit
    ) {
        if (index >= ids.size) {
            onFailed()
            return
        }
        InterstitialAd.load(
            activity,
            ids[index],
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    onLoaded(interstitialAd)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    loadInterstitialSequential(activity, ids, index + 1, onLoaded, onFailed)
                }
            }
        )
    }

    private fun loadRewardedSequential(
        activity: Activity,
        ids: List<String>,
        index: Int,
        onLoaded: (RewardedAd) -> Unit,
        onFailed: () -> Unit
    ) {
        if (index >= ids.size) {
            onFailed()
            return
        }
        RewardedAd.load(
            activity,
            ids[index],
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    onLoaded(rewardedAd)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    loadRewardedSequential(activity, ids, index + 1, onLoaded, onFailed)
                }
            }
        )
    }

    private fun renderBanner(ui: AdUiBinding, adView: AdView) {
        hideLoading(ui.shimmer)
        ui.adFrame.removeAllViews()
        (adView.parent as? ViewGroup)?.removeView(adView)
        ui.adFrame.addView(
            adView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        ui.adFrame.visibility = View.VISIBLE
        ui.rootContainer.visibility = View.VISIBLE
    }

    private fun renderNativeSmall(ui: AdUiBinding, activity: Activity, nativeAd: NativeAd) {
        hideLoading(ui.shimmer)
        ui.adFrame.removeAllViews()

        val adView = activity.layoutInflater.inflate(
            R.layout.native_banner_ad_bb,
            ui.adFrame,
            false
        ) as NativeAdView

        val headline = adView.findViewById<TextView>(R.id.ad_headline)
        val body = adView.findViewById<TextView>(R.id.ad_body)
        val cta = adView.findViewById<TextView>(R.id.ad_call_to_action)
        val icon = adView.findViewById<ImageView>(R.id.ad_app_icon)

        headline?.text = nativeAd.headline
        body?.text = nativeAd.body
        cta?.text = nativeAd.callToAction
        cta?.isEnabled = !nativeAd.callToAction.isNullOrBlank()

        if (nativeAd.icon?.drawable != null) {
            icon?.setImageDrawable(nativeAd.icon?.drawable)
            icon?.visibility = View.VISIBLE
        } else {
            icon?.visibility = View.GONE
        }

        adView.headlineView = headline
        adView.bodyView = body
        adView.callToActionView = cta
        adView.iconView = icon
        adView.setNativeAd(nativeAd)

        ui.adFrame.addView(adView)
        ui.adFrame.visibility = View.VISIBLE
        ui.rootContainer.visibility = View.VISIBLE
    }

    private fun showLoading(ui: AdUiBinding) {
        runOnMain {
            // Keep ad area fully collapsed while loading to avoid blank/white placeholders.
            hideLoading(ui.shimmer)
            ui.adFrame.removeAllViews()
            ui.adFrame.visibility = View.GONE
            ui.rootContainer.visibility = View.GONE
        }
    }

    private fun hideLoading(shimmer: ShimmerFrameLayout?) {
        runOnMain {
            shimmer?.let {
                it.stopShimmer()
                it.visibility = View.GONE
            }
        }
    }

    private fun hideAdUi(ui: AdUiBinding) {
        runOnMain {
            hideLoading(ui.shimmer)
            ui.adFrame.removeAllViews()
            ui.adFrame.visibility = View.GONE
            ui.rootContainer.visibility = View.GONE
        }
    }

    private fun isEligibleForPlacement(
        context: Context,
        placement: AdPlacement,
        screenName: String,
        userInitiated: Boolean,
        skipCooldownCheck: Boolean = false,
        forPreload: Boolean = false
    ): Boolean {
        if (!context.config.All_Ads_On) {
            if (placement == AdPlacement.APP_OPEN_RESUME) log("app_open_block: all_ads_off")
            return false
        }
        if (BaseSharedPreferences(context).mIS_SUBSCRIBED) {
            if (placement == AdPlacement.APP_OPEN_RESUME) log("app_open_block: subscribed")
            return false
        }
        if (!context.isOnline()) {
            if (placement == AdPlacement.APP_OPEN_RESUME) log("app_open_block: offline")
            return false
        }
        if (!GoogleMobileAdsConsentManager.getInstance(context).canRequestAds) {
            if (placement == AdPlacement.APP_OPEN_RESUME) log("app_open_block: consent_denied")
            return false
        }
        if (!isPlacementEnabledByConfig(context, placement)) {
            if (placement == AdPlacement.APP_OPEN_RESUME) log("app_open_block: remote_flag_off")
            return false
        }
        if (!placement.allowInFirstSession && getLaunchCount() <= 1 && !forPreload) {
            if (placement == AdPlacement.APP_OPEN_RESUME) log("app_open_block: first_session")
            return false
        }
        if (!skipCooldownCheck && !passesCooldown(placement)) {
            if (placement == AdPlacement.APP_OPEN_RESUME) log("app_open_block: cooldown")
            return false
        }

        if (placement == AdPlacement.APP_OPEN_RESUME) {
            if (!forPreload) {
                if (screenName in appOpenSensitiveScreens) {
                    log("app_open_block: sensitive_screen=$screenName")
                    return false
                }
                if (getLaunchCount() < APP_OPEN_MIN_LAUNCH_COUNT) {
                    log("app_open_block: min_launch_count current=${getLaunchCount()} required=$APP_OPEN_MIN_LAUNCH_COUNT")
                    return false
                }
                val lastBackgroundAt = prefs.getLong(KEY_LAST_BACKGROUND_TS, 0L)
                val backgroundGap = System.currentTimeMillis() - lastBackgroundAt
                if (lastBackgroundAt <= 0L || backgroundGap < APP_OPEN_MIN_BACKGROUND_GAP_MS) {
                    log("app_open_block: bg_gap current=${backgroundGap}ms required=${APP_OPEN_MIN_BACKGROUND_GAP_MS}ms")
                    return false
                }
            }
        }

        if (!userInitiated && placement.format == AdFormat.REWARDED) {
            return false
        }

        return true
    }

    private fun isPlacementEnabledByConfig(context: Context, placement: AdPlacement): Boolean {
        return when (placement) {
            AdPlacement.HOME_LARGE_BANNER -> context.config.isHomeBannerAdOn
            AdPlacement.LANGUAGE_NATIVE -> context.config.isLanguageNativeAdOn
            AdPlacement.SETTING_NATIVE,
            AdPlacement.VIEW_DETAILS_NATIVE -> context.config.isProfileNativeAdOn

            AdPlacement.APP_OPEN_RESUME -> firebaseConfig.Message_show_open_Ad
            AdPlacement.ARCHIVE_EXIT_INTERSTITIAL,
            AdPlacement.RECYCLE_EXIT_INTERSTITIAL -> firebaseConfig.message_show_Interstitial_Ad

            AdPlacement.BACKUP_REWARDED,
            AdPlacement.SCHEDULE_REWARDED,
            AdPlacement.TRANSLATION_REWARDED -> firebaseConfig.Message_show_Rewarded_Ad
        }
    }

    private fun passesCooldown(placement: AdPlacement): Boolean {
        val lastShown = prefs.getLong(lastShownKey(placement), 0L)
        if (lastShown <= 0L) return true
        return System.currentTimeMillis() - lastShown >= placement.cooldownMs
    }

    private fun markShown(placement: AdPlacement) {
        prefs.edit().putLong(lastShownKey(placement), System.currentTimeMillis()).apply()
    }

    private fun tryAcquireInFlight(placement: AdPlacement): Boolean {
        val flag = inFlightByPlacement.getOrPut(placement) { AtomicBoolean(false) }
        return flag.compareAndSet(false, true)
    }

    private fun releaseInFlight(placement: AdPlacement) {
        inFlightByPlacement[placement]?.set(false)
    }

    private fun getLaunchCount(): Int = prefs.getInt(KEY_APP_LAUNCH_COUNT, 0)

    private fun runOnMain(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            block()
        } else {
            mainHandler.post(block)
        }
    }

    private fun isActivityUsable(activity: Activity): Boolean {
        if (activity.isFinishing) return false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed) return false
        return true
    }

    private fun lastShownKey(placement: AdPlacement): String =
        "${KEY_LAST_SHOWN_PREFIX}${placement.name}"

    private fun log(message: String) {
        Log.d(TAG, message)
    }


    private fun Activity.loadingDialog (): AlertDialog {
        val binding = DialogLoadingAdBinding.bind(LayoutInflater.from(this).inflate(R.layout.dialog_loading_ad, null, false))

        binding.loadingText.setTextColor(this.getProperTextColor())
        binding.progressIndicator.setIndicatorColor(this.getProperPrimaryColor())
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(binding.root)
            .setCancelable(false)
            .create()
        return dialog
    }

    companion object {
        private const val TAG = "AdsOrchestratorV2"
        private const val PREFS_NAME = "ads_v2_state"
        private const val KEY_APP_LAUNCH_COUNT = "key_app_launch_count"
        private const val KEY_LAST_BACKGROUND_TS = "key_last_background_ts"
        private const val KEY_LAST_SHOWN_PREFIX = "key_last_shown_"
        private const val APP_OPEN_MIN_LAUNCH_COUNT = 2
        private const val APP_OPEN_MIN_BACKGROUND_GAP_MS = 10_000L
        private const val APP_OPEN_FOREGROUND_WINDOW_MS = 8_000L

        private const val SCREEN_HOME = "HomeABActivity"
        private const val SCREEN_LANGUAGE = "LanguageActivity"
        private const val SCREEN_SETTING = "SettingActivity"
        private const val SCREEN_VIEW_DETAILS = "ViewDetailsActivity"
        private const val SCREEN_ARCHIVE = "ArchivedActivity"
        private const val SCREEN_RECYCLE = "RecycleBinActivity"
        private const val SCREEN_BACKUP = "BekupActivity"
        private const val SCREEN_SCHEDULE = "Schedule_Message_Show_Activity"
        private const val SCREEN_TRANSLATION = "Message_Translation_Activity"

        private val appOpenSensitiveScreens = setOf(
            WelcomeActivity::class.java.simpleName,
            LanguageActivity::class.java.simpleName,
            permissionOneActivity::class.java.simpleName,
            permissionTwoActivity::class.java.simpleName,
            PermissionAfterNewActivity::class.java.simpleName,
            OverlayPermissionAnimationActivity::class.java.simpleName,
            Experiment_Sub_Screen_Activity::class.java.simpleName,
            Paywall_FourPlan_Activity::class.java.simpleName,
            Paywall_dynamic_Activity::class.java.simpleName,
            LockScreenActivity::class.java.simpleName,
            SendMessageActivity::class.java.simpleName,
            SelectContactActivity::class.java.simpleName,
            SearchActivity::class.java.simpleName,
            PrivacyChatActivity::class.java.simpleName,
        )

        @Volatile
        private var instance: AdsOrchestrator? = null

        fun get(application: Application): AdsOrchestrator {
            return instance ?: synchronized(this) {
                instance ?: AdsOrchestrator(application).also { instance = it }
            }
        }

        fun get(context: Context): AdsOrchestrator {
            val app = context.applicationContext as Application
            return get(app)
        }
    }
}
