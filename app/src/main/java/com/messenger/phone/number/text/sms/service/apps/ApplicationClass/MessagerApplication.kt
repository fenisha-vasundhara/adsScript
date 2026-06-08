package com.messenger.phone.number.text.sms.service.apps.ApplicationClass

import com.messenger.phone.number.text.sms.service.apps.DataBase.MessagerDatabase
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
import com.messenger.phone.number.text.sms.service.apps.realmplan.LiveRealmMigrationStore
import com.messenger.phone.number.text.sms.service.apps.realmplan.RealmFeatureFlag
import com.messenger.phone.number.text.sms.service.apps.realmplan.RoomToRealmWorker


import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.app.NotificationManager
import android.content.ComponentCallbacks2
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.util.Log
import android.webkit.WebView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDex
import com.demo.adsmanage.Activity.Experiment_Sub_Screen_Activity
import com.demo.adsmanage.Activity.Paywall_FourPlan_Activity
import com.demo.adsmanage.Activity.PayAllSubsciptionExterimentActivity
import com.demo.adsmanage.Activity.SubActivityTwoplanActivity
import com.demo.adsmanage.AdsManage
import com.demo.adsmanage.AdsManage.onesignalUserSaveId
import com.demo.adsmanage.Commen.Constants
import com.demo.adsmanage.Commen.Constants.isAdsShowing
import com.demo.adsmanage.Commen.Constants.offerscreenshow
import com.demo.adsmanage.Commen.firebaseEventMain
import com.demo.adsmanage.Commen.firebaseFunnel
import com.demo.adsmanage.Commen.firebaseeventTXT
import com.demo.adsmanage.Commen.isValidforExperiment
import com.demo.adsmanage.Commen.isVersionAtLeast
import com.demo.adsmanage.Commen.log
import com.demo.adsmanage.Commen.purchaseEvents
import com.demo.adsmanage.InterFace.SubPerchechComplete
import com.demo.adsmanage.InterFace.SubPerchechMainComplete
import com.demo.adsmanage.basemodule.BaseSharedPreferences
import com.demo.adsmanage.helper.GlobalTimer
import com.demo.adsmanage.helper.logD
import com.demo.adsmanage.model.PackagesRen
import com.demo.adsmanage.viewmodel.AppSubscription
import com.facebook.FacebookSdk
import com.facebook.ads.AudienceNetworkActivity
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.messenger.phone.number.text.sms.service.apps.AfterCallScreen.fragment.MessageListFragment
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ZopIconget
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adshowcount
import com.messenger.phone.number.text.sms.service.apps.CommanClass.baseConfig
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.dialNumber
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gooutside
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isFLow1
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isFLow2
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isOnline
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isThirdPartyIntentCheck
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isfisttimeadopen
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ispermissiobnor14
import com.messenger.phone.number.text.sms.service.apps.CommanClass.mobileNumberLock
import com.messenger.phone.number.text.sms.service.apps.CommanClass.nameLock
import com.messenger.phone.number.text.sms.service.apps.CommanClass.parseGlobalTimeModel
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLang
import com.messenger.phone.number.text.sms.service.apps.CommanClass.systemfountstartsetting
import com.messenger.phone.number.text.sms.service.apps.CommanClass.thredidLock
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.GoogleMobileAdsConsentManagerChack.GoogleMobileAdsConsentManager
import com.messenger.phone.number.text.sms.service.apps.HomeABActivity
import com.messenger.phone.number.text.sms.service.apps.LockScreenActivity
import com.messenger.phone.number.text.sms.service.apps.Notification.notificationProvider
import com.messenger.phone.number.text.sms.service.apps.OverlayPermissionAnimationActivity
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.SettingActivity
import com.messenger.phone.number.text.sms.service.apps.SplashScreenActivity
import com.messenger.phone.number.text.sms.service.apps.WelcomeActivity
import com.messenger.phone.number.text.sms.service.apps.ads.MS_Banner_AFTERCALL_MREC
import com.messenger.phone.number.text.sms.service.apps.ads.MS_Banner_Aftercall_Low
import com.messenger.phone.number.text.sms.service.apps.ads.MS_Native_Aftercall_Low
import com.messenger.phone.number.text.sms.service.apps.ads.is_testMode
import com.messenger.phone.number.text.sms.service.apps.ads.v2.AdsOrchestrator
import com.messenger.phone.number.text.sms.service.apps.data.Attachment
import com.messenger.phone.number.text.sms.service.apps.data.GetMobileMessage
import com.messenger.phone.number.text.sms.service.apps.data.PhoneNumber
import com.messenger.phone.number.text.sms.service.apps.data.newsync.SmsSyncManager
import com.messenger.phone.number.text.sms.service.apps.helper.ForegroundNew
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
import com.messenger.phone.number.text.sms.service.apps.sms.SendSMSManager
import com.microsoft.clarity.Clarity
import com.microsoft.clarity.ClarityConfig
import com.onesignal.OneSignal
import com.onesignal.user.IUserManager
import com.onesignal.user.state.IUserStateObserver
import com.onesignal.user.state.UserChangedState
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.interfaces.LogInCallback
import com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback
import com.messenger.phone.number.text.sms.service.apps.ads.OneSignalCallBacks
import com.messenger.phone.number.text.sms.service.apps.ads.initOneSignal
import com.messenger.phone.number.text.sms.service.apps.ads.notificationHandle
import com.vungle.ads.internal.ui.VungleActivity
import dagger.hilt.android.HiltAndroidApp
//import io.appmetrica.analytics.AppMetrica
//import io.appmetrica.analytics.AppMetricaConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import dagger.Lazy
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltAndroidApp
class MessagerApplication : AppSubscription(), SubPerchechComplete, SubPerchechMainComplete,
    LifecycleObserver, Application.ActivityLifecycleCallbacks {
    val TAG = "MessagerApplication"

    private var isUserNotificationShow: Boolean = true
    private val isMobileAdsInitializeCalled = AtomicBoolean(false)
    private val isWebViewDataDirSet = AtomicBoolean(false)
    private val bannersPreloaded = AtomicBoolean(false)
    private val deferredStartupScheduled = AtomicBoolean(false)
    private val facebookSdkInitLock = Any()
    private val fullAppLockStateLock = Any()
    private val appStartupScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    @Volatile
    private var isFullAppLockShowing = false
    @Volatile
    private var hasUnlockedInCurrentForegroundSession = false


    @Volatile
    private var firstActivityClassName: String? = null
    private var matrica_api: String = "59fb43f5-e650-40e1-96f4-ae8db3d35bf4"
    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager
    private var cn: ComponentName? = null
    private lateinit var am: ActivityManager
    private var startedActivityCount = 0

    //    var openManager: AppOpenManager? = null
    var currentActivity: Activity? = null

    private var mPackagerenList = arrayListOf(
        PackagesRen(
            originalPrice = "₹610.00",
            freeTrialPeriod = null,
            title = "Messages - Monthly PRO (Messages : SMS & Private Chat)",
            price = "₹610.00",
            description = "",
            subscriptionPeriod = "P1M",
            sku = "monthly_messages_experiment",
            presentedOfferingIdentifier = "Current plan"
        ), PackagesRen(
            originalPrice = "₹3,100.00",
            freeTrialPeriod = null,
            title = "Messages - Monthly PRO (Messages : SMS & Private Chat)",
            price = "₹3,100.00",
            description = "",
            subscriptionPeriod = "P1Y",
            sku = "yearly_messages_experiment",
            presentedOfferingIdentifier = "Current plan"
        )
    )

    private lateinit var firebaseAnalytics: FirebaseAnalytics


    @Inject
    lateinit var getMobileMessage: GetMobileMessage

    @Inject
    lateinit var messagerDatabaseRepo: MessagerDatabaseRepo

    @Inject
    lateinit var notificationProvider: notificationProvider

    @Inject
    lateinit var sendSMSManager: Lazy<SendSMSManager>

    // ── Realm migration wiring ────────────────────────────────────────────────
    /** Injected by RealmModule — the process-wide Realm file handle. */
    @Inject
    lateinit var messagerDatabase: MessagerDatabase

    /** Injected by RealmModule — wraps the Realm instance for migration writes. */
    @Inject
    lateinit var realmMigrationStore: Lazy<LiveRealmMigrationStore>
    // ─────────────────────────────────────────────────────────────────────────

    private var hasAppFlow23BootstrapSyncStarted = false
    private val appSyncScope = CoroutineScope(Dispatchers.IO)
    private val appSmsSyncManager: SmsSyncManager by lazy {
        SmsSyncManager(applicationContext, messagerDatabaseRepo, appSyncScope)
    }

    companion object {

        // From your screenshot
        private const val REVENUECAT_API_KEY =
            "goog_vwffVeAqEsqRtnAPZwaXudfQnHV" // your Play Store key


        var singleton: MessagerApplication? = null
        var coldStart = true
        private const val TAG = "BalanceCheckerApplication"

        @JvmStatic
        val instance: MessagerApplication?
            get() {
                if (singleton == null) {
                    singleton = MessagerApplication()
                }
                return singleton
            }


    }

    init {
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(base)
        ensureWebViewDataDirSuffix()
    }

    fun Context.hasPermission(permId: String) = ContextCompat.checkSelfPermission(
        this, permId
    ) == PackageManager.PERMISSION_GRANTED

    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("UseCompatLoadingForDrawables")

    fun initOneSignalKey(
        app: Application,
        appId: String,
        blockActivities: List<Activity>,
        Id: (String) -> Unit,
        oneSignalCallBacks: OneSignalCallBacks
    ) {
        initOneSignal(app, appId, Id)
        notificationHandle(blockActivities, oneSignalCallBacks)
    }

    override fun onCreate() {
//        ConsentManager.restoreSavedState(this)

        super<AppSubscription>.onCreate()
        AdsOrchestrator.get(this).markProcessLaunch()
        Log.d("Startup", "coldStart=$coldStart")
        coldStart = false
        Log.d("TAG", "banner: 000 $is_testMode")



        appStartupScope.launch(Dispatchers.IO) {
            val language = config.setDefaultLanguage
            val themeMode = ThemeModeManager.getThemeMode(this@MessagerApplication)
            baseConfig.comefromebg = false
            withContext(Dispatchers.Main) {
                setLang(language)
                ThemeModeManager.applyThemeMode(themeMode)
            }
        }

//        val matria_configgg = AppMetricaConfig.newConfigBuilder(matrica_api).build()
//        AppMetrica.activate(applicationContext, matria_configgg)

        // Warm init early so ThemeModeManager sync doesn't hit uninitialized CallerModule.
//        CallerModule.warmInitialize(this)


        appStartupScope.launch(Dispatchers.IO) {
            FirebaseApp.initializeApp(this@MessagerApplication)
//            iZooto.initialize(applicationContext).setTokenReceivedListener(this@MessagerApplication)
//                .setNotificationReceiveListener(ExampleNotificationHandler()).build()
        }
        appStartupScope.launch {
            initRealm()
        }
        // New users (no Room DB on disk) go directly to Realm; skip migration worker.
        // Existing users run the worker once; it migrates data and flips the flag on success.
        val realmFlag = RealmFeatureFlag(this)
        if (!getDatabasePath("MessageDB").exists()) {
            realmFlag.useRealmReads = true
        } else {
            RoomToRealmWorker.enqueue(this)
        }





        AdsManage.singleton = this
        ZopIconget = 1

        if (config.customwallpaperselected == 2) {
            config.outmessagecolorcustomwallpaperAB = "#F2F2F2"
            config.inmessagecolorcustomwallpaperAB = "#EBEFF2"
            config.backgroundcolorcustomwallpaperab = "#FFFFFFF"
        }




        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true)

        val previousUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        val uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { thread, exception ->
            try {
                CoroutineScope(Dispatchers.Main).launch {
                    toastMess("Something Went Wrong")
                }
            } finally {
                previousUncaughtExceptionHandler?.uncaughtException(thread, exception)
            }
        }

        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler)

        googleMobileAdsConsentManager =
            GoogleMobileAdsConsentManager.getInstance(applicationContext)

        currentActivity?.let {
            try {
                googleMobileAdsConsentManager.gatherConsent(it) { consentError ->
                    if (consentError != null) {
                        Log.w(TAG, "${consentError.errorCode}. ${consentError.message}")
                    }
                    if (googleMobileAdsConsentManager.canRequestAds) {
                        initializeMobileAdsSdk()
                    }
                }
            } catch (E: Exception) {
            }
        }

        singleton = this

        ForegroundNew.init(this)
        val foregroundListener = object : ForegroundNew.Listener {
            override fun background() {
                Log.d(TAG, "setupLockScreen: <-------------> white isue 5055 5555")
                handleAppBackgroundTransition()
            }

            override fun foreground() {
                Log.d(TAG, "setupLockScreen: <-------------> white isue 5055")
                CoroutineScope(Dispatchers.IO).launch {
                    setupLockScreen()
                }
            }
        }

        ForegroundNew.addListener(foregroundListener)

        val closeIcon = try {
            resources.getDrawable(R.drawable.close_icon)
        } catch (e: Resources.NotFoundException) {
            ContextCompat.getDrawable(
                this@MessagerApplication,
                android.R.drawable.ic_menu_close_clear_cancel
            )
        }

        appStartupScope.launch(Dispatchers.IO) {
            AdsManage.ActivityBuilder().ApplicationCall(this@MessagerApplication)
                .setAdmobAdsID("", "", "", "", "").setFBAdsID("", "", "", "")
                .setIsSubscription(true).setBASIC_SKU("monthly_messages_experiment")
                .setPREMIUM_SKU("yearly_messages_experiment")
                .setPREMIUM_SIX_SKU("experiment_weekly_messages").setIsRevenuCat(true)
                .setRevenuCatPurchase_ID("goog_vwffVeAqEsqRtnAPZwaXudfQnHV")
                .setRevenuCatDefaultList(mPackagerenList)
                .setPrivacyPolicyURL("https://imagecropnwallpaperchanger.blogspot.com/2023/07/privacy-policy.html")
                .setBackgroundSubscreenLine(arrayListOf()).setSubScreenOfLine(arrayListOf())
                .setAppName(
                    this@MessagerApplication.resources.getString(R.string.app_name),
                    com.messenger.phone.number.text.sms.service.apps.R.color.black2
                )
                .setBackgroundSubscreenLineColor(R.color.black2).setPriceTextColor(R.color.black2)
                .setSubLineColor(com.messenger.phone.number.text.sms.service.apps.R.color.black2)
                .setNavigationBarColor(com.messenger.phone.number.text.sms.service.apps.R.color.white)
                .setSmallSubLineColor(com.messenger.phone.number.text.sms.service.apps.R.color.light_gray_color)
                .setSUBButtonTextColor(R.color.white)
                .setAppIcon(this@MessagerApplication.resources.getDrawable(R.drawable.appicon_sub))
                .setPremium_True_Icon(this@MessagerApplication.resources.getDrawable(R.drawable.check))
                .setBasic_Line_Icon(this@MessagerApplication.resources.getDrawable(R.drawable.line))
                .setBaseSubscriptionBackground(this@MessagerApplication.resources.getDrawable(R.drawable.bg))
                .setSubscriptionBackground(this@MessagerApplication.resources.getDrawable(R.drawable.bg))
                .setClose_Icon(closeIcon)
                .setPremium_CardSelected_Icon(this@MessagerApplication.resources.getDrawable(R.drawable.ic_pro_selected))
                .setPremium_Cardunselected_Icon(this@MessagerApplication.resources.getDrawable(R.drawable.ic_pro_selection))
                .setPremium_Button_Icon(this@MessagerApplication.resources.getDrawable(R.drawable.bg_sub_btn_))
                .Subcall(this@MessagerApplication).CallSubLister(this@MessagerApplication)
                .CallSubListerNew(this@MessagerApplication)

            firebaseAnalytics = FirebaseAnalytics.getInstance(this@MessagerApplication)
            ensureFacebookSdkInitialized()
            FacebookSdk.setAutoLogAppEventsEnabled(false)
            firebaseAnalytics.appInstanceId.addOnSuccessListener { instanceId ->
                if (instanceId != null) {
                    "firebaseAnalytics <----------------> Instance ID -> $instanceId".log()
//                    Purchases.sharedInstance.setAttributes(mapOf("\$firebaseAppInstanceId" to instanceId))
                    Purchases.sharedInstance.setFirebaseAppInstanceID(instanceId)
                } else {
                    "firebaseAnalytics <----------------> Instance ID -> NOT FOUND!".log()
                }
            }.addOnFailureListener {
                "firebaseAnalytics <----------------> addOnFailureListener".log()
            }

        }

//        openManager = AppOpenManager(this)
        registerActivityLifecycleCallbacks(this)
//        moduleRegisterActivityLifecycleCallbacks()
        Handler(Looper.getMainLooper()).post {
            ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        }

        try {
//            subpurchese = {
//                firebaseEventMain("Subscription_Purchase_Successfully")
//            }
        } catch (e: Exception) {
        }

        when (config.Home_Banner_VS_Native_VS_Random) {
            "1" -> {
                adshowcount = 1
            }

            "2" -> {
                adshowcount = 2
            }

            "3" -> {
                adshowcount = (1..2).random()
            }
        }
        adshowcount = 2
    }


    suspend fun setupLockScreen() {
        val activity = currentActivity ?: return
        Log.d(TAG, "setupLockScreen: <-------------> white isue 0")

        if (systemfountstartsetting) {
            systemfountstartsetting = false
            Log.d(TAG, "setupLockScreen: <-------------> white isue 00")

            return
        }

        if (!isFullAppLockEnabled()) {
            onFullAppLockCancelled()
            Log.d(TAG, "setupLockScreen: <-------------> white isue 000")

            return
        }

        if (shouldSkipForegroundFullAppLock(activity)) {
            Log.d(TAG, "setupLockScreen: <-------------> white isue 0000")

            return
        }
        Log.d(TAG, "setupLockScreen: <-------------> white isue 00000")

        if (activity.intent?.let { isThirdPartyIntent(it) } == false /*&& !Constants.isAdsClicking*/ && !com.messenger.phone.number.text.sms.service.apps.adsnew.Constants.isAdsClicking && !Constants.isAdsShowing) {
            try {
                am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                cn = am.getRunningTasks(1)[0].topActivity
            } catch (_: Exception) {
            }
            Log.d(TAG, "setupLockScreen: <-------------> white isue 1")
            if (/*!Constants.isAdsClicking &&*/ (cn?.className != "com.messenger.phone.number.text.sms.service.apps.LockScreenActivity") && (cn?.className != "com.google.android.gms.ads.AdActivity")) {
                Log.d(TAG, "setupLockScreen: <-------------> white isue 3")
//                if (!config.introshow) {
                    Log.d(TAG, "setupLockScreen: <-------------> white isue 4")

                    if (config.Full_AppLock_Pin != "Not Set") {
                        Log.d(TAG, "setupLockScreen: <-------------> white isue 5")

                        if ((cn?.className == "com.messenger.phone.number.text.sms.service.apps.WelcomeActivity")) {
                            Log.d(TAG, "setupLockScreen: <-------------> white isue 6")
                        } else {
                            Log.d(TAG, "setupLockScreen: <-------------> white isue 115 . ${config.Lock_Screen_Pin}")
                            Log.d(TAG, "setupLockScreen: <-------------> white isue 115 . ${cn?.className}")

                            if (config.Lock_Screen_Pin != "Not Set") {
                                if ((cn?.className == "com.messenger.phone.number.text.sms.service.apps.PrivacyChatActivity")) {
                                    currentActivity?.finish()
                                    launchFullAppLock(
                                        Intent(
                                            currentActivity, LockScreenActivity::class.java
                                        ).putExtra("lockype", 2).putExtra("comefrom", 2)
                                            .putExtra("openprivatechat", true)
                                            .putExtra("gotingprivatechat", true)
                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    )
                                } else if ((cn?.className == "com.messenger.phone.number.text.sms.service.apps.SendMessageActivity")) {
                                    Log.d(TAG, "setupLockScreen: <-------------> white isue 10")
                                    CoroutineScope(Dispatchers.IO).launch {
                                        val list = messagerDatabaseRepo.getUserMessageListrepo(
                                            thredidLock
                                        )
                                        val isPrivateChat = if (list.isEmpty()) {
                                            false
                                        } else {
                                            list[0].isPrivateChat
                                        }
                                        if (isPrivateChat) {
                                            currentActivity?.finish()
//                                            startActivity(
//                                                Intent(currentActivity, LockScreenActivity::class.java).putExtra("tredid", thredidLock)
//                                                    .putExtra("name", nameLock).putExtra("mobileNumber", mobileNumberLock).putExtra("lockype", 1).putExtra("comefrom", 1).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                            )
                                            startActivity(
                                                Intent(
                                                    currentActivity, LockScreenActivity::class.java
                                                ).putExtra("comefrom", 2).putExtra("appopen", false)
                                                    .putExtra("openprivatechat", true)
                                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            )
                                        } else {
                                            if (config.Full_AppLock_Pin != "Not Set") {
//                                                startActivity(
//                                                    Intent(currentActivity, LockScreenActivity::class.java).putExtra("tredid", thredidLock)
//                                                        .putExtra("name", nameLock).putExtra("mobileNumber", mobileNumberLock).putExtra("lockype", 1).putExtra("comefrom", 1).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                                )
//                                                currentActivity?.finish()
//                                                startActivity(
//                                                    Intent(currentActivity, LockScreenActivity::class.java)
//                                                        .putExtra("comefrom", 2)
//                                                        .putExtra("appopen", false)
//                                                        .putExtra("openprivatechat", true)
//                                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                                )
                                                launchFullAppLock(
                                                    Intent(
                                                        currentActivity,
                                                        LockScreenActivity::class.java
                                                    ).putExtra("comefrom", 2)
                                                        .putExtra("appopen", false)
                                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    launchFullAppLock(
                                        Intent(
                                            currentActivity, LockScreenActivity::class.java
                                        ).putExtra("comefrom", 2).putExtra("appopen", false)
                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    )
                                }
                            } else {
                                launchFullAppLock(
                                    Intent(
                                        currentActivity, LockScreenActivity::class.java
                                    ).putExtra("comefrom", 2).putExtra("appopen", false)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                )
                            }
                        }
                    }
                    else if (config.Lock_Screen_Pin != "Not Set") {
                        if ((cn?.className == "com.messenger.phone.number.text.sms.service.apps.PrivacyChatActivity")) {
                            currentActivity?.finish()
                            startActivity(
                                Intent(
                                    currentActivity, LockScreenActivity::class.java
                                ).putExtra("lockype", 2).putExtra("comefrom", 1)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                        } else if (if (applicationContext.config.Message_Send_Activity == "1") {
                                cn?.className == "com.messenger.phone.number.text.sms.service.apps.SendmessageAB.SendMessageActivityAB"
                            } else {
                                cn?.className == "com.messenger.phone.number.text.sms.service.apps.SendMessageActivity"
                            }
                        ) {
                            CoroutineScope(Dispatchers.IO).launch {
                                val list = messagerDatabaseRepo.getUserMessageListrepo(thredidLock)
                                val isPrivateChat = if (list.isEmpty()) {
                                    false
                                } else {
                                    list[0].isPrivateChat
                                }
                                if (isPrivateChat) {
                                    currentActivity?.finish()
                                    startActivity(
                                        Intent(
                                            currentActivity, LockScreenActivity::class.java
                                        ).putExtra("tredid", thredidLock).putExtra("name", nameLock)
                                            .putExtra("mobileNumber", mobileNumberLock)
                                            .putExtra("lockype", 1).putExtra("comefrom", 1)
                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    )
                                }
                            }
                        } else {
                            Log.d(TAG, "setupLockScreen: <-------------> white isue 18")
                            if ((cn?.className == "com.messenger.phone.number.text.sms.service.apps.WelcomeActivity")) {
                                Log.d(TAG, "setupLockScreen: <-------------> white isue 19")
                                if (!config.introshow) {
                                    if (!config.firsttimeusersetlang) {
                                        if (config.SelectedLanguage == "en") {
                                            startActivity(
                                                Intent(
                                                    currentActivity, SettingActivity::class.java
                                                ).putExtra("loadiswhatsnew", 101)
                                                    .putExtra("onbackbress", true)
                                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            )
                                        } else {
//                                        config.firsttimeusersetlang = true

                                            startActivity(
                                                Intent(
                                                    currentActivity, HomeABActivity::class.java
                                                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            )


                                        }
                                    } else {
                                        if (!config.introshow) {

                                            startActivity(
                                                Intent(
                                                    currentActivity, HomeABActivity::class.java
                                                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            )

                                        }
                                    }
                                } else {
                                    if (!config.firsttimeusersetlang) {
                                        if (config.SelectedLanguage == "en") {
                                            startActivity(
                                                Intent(
                                                    currentActivity, SettingActivity::class.java
                                                ).putExtra("loadiswhatsnew", 101)
                                                    .putExtra("onbackbress", true)
                                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            )
                                        } else {
//                                        config.firsttimeusersetlang = true

                                            startActivity(
                                                Intent(
                                                    currentActivity, HomeABActivity::class.java
                                                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            )

                                        }
                                    } else {

                                        startActivity(
                                            Intent(
                                                currentActivity, HomeABActivity::class.java
                                            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        )

                                    }
                                }
                            }
                        }
                    }
                    else {
                        if ((cn?.className == "com.messenger.phone.number.text.sms.service.apps.WelcomeActivity")) {
                            if (!config.introshow) {
                                if (!config.firsttimeusersetlang) {
                                    if (config.SelectedLanguage == "en") {
                                        startActivity(
                                            Intent(
                                                currentActivity, SettingActivity::class.java
                                            ).putExtra("loadiswhatsnew", 101)
                                                .putExtra("onbackbress", true)
                                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        )
                                    } else {
//                                        config.firsttimeusersetlang = true

                                        startActivity(
                                            Intent(
                                                currentActivity, HomeABActivity::class.java
                                            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        )

                                    }
                                } else {
                                    if (!config.introshow) {

                                        startActivity(
                                            Intent(
                                                currentActivity, HomeABActivity::class.java
                                            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        )

                                    }
                                }
                            } else {
                                if (!config.firsttimeusersetlang) {
                                    if (config.SelectedLanguage == "en") {
                                        startActivity(
                                            Intent(
                                                currentActivity, SettingActivity::class.java
                                            ).putExtra("loadiswhatsnew", 101)
                                                .putExtra("onbackbress", true)
                                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        )
                                    } else {
//                                        config.firsttimeusersetlang = true

                                        startActivity(
                                            Intent(
                                                currentActivity, HomeABActivity::class.java
                                            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        )

                                    }
                                } else {

                                    startActivity(
                                        Intent(
                                            currentActivity, HomeABActivity::class.java
                                        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    )
                                }

                            }
                        }
                    }
//                }
               /* else {
                    if (!config.firsttimeusersetlang) {
                        if (config.SelectedLanguage == "en") {
//                            startActivity(
//                                Intent(this, SettingActivity::class.java)
//                                    .putExtra("loadiswhatsnew", 101)
//                                    .putExtra("onbackbress", true)
//                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                            )
                        } else {
                            config.firsttimeusersetlang = true
                        }
                    }
                   } */
            } else {
                if (/*!Constants.isAdsClicking &&*/ (cn?.className != "com.messenger.phone.number.text.sms.service.apps.LockScreenActivity") && (cn?.className != "com.google.android.gms.ads.AdActivity")) {
                    launchFullAppLock(
                        Intent(
                            currentActivity, LockScreenActivity::class.java
                        ).putExtra("comefrom", 2).putExtra("appopen", false)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }
            }
        } else {
            if (isThirdPartyIntentCheck == false) {
                if (!ispermissiobnor14) {
                    ispermissiobnor14 = false

//                    if (Constants.isAdsClicking) {
                        startActivity(
                            Intent(
                                currentActivity, HomeABActivity::class.java
                            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
//                    }


                }
            } else {
                isThirdPartyIntentCheck = false
            }
//
        }
    }

    fun isFullAppLockEnabled(): Boolean {
        return config.Full_AppLock_Pin != "Not Set"
    }

    fun createFullAppLockIntent(context: Context, returnResult: Boolean, appOpen: Boolean): Intent {
        return Intent(context, LockScreenActivity::class.java)
            .putExtra("comefrom", 2)
            .putExtra("appopen", appOpen)
            .putExtra(LockScreenActivity.EXTRA_RETURN_RESULT, returnResult)
    }

    fun tryBeginFullAppLockFlow(): Boolean {
        synchronized(fullAppLockStateLock) {


            if (!isFullAppLockEnabled()) {
                isFullAppLockShowing = false
                hasUnlockedInCurrentForegroundSession = false
                return false

                logD("setupLockScreen","launchFullAppLock---init ----01")

            }

            if (isFullAppLockShowing || hasUnlockedInCurrentForegroundSession) {
                logD("setupLockScreen","launchFullAppLock----init---011")

                return false
            }
            logD("setupLockScreen","launchFullAppLock---init----0111")

            isFullAppLockShowing = true
            return true
        }
    }

    fun onFullAppLockVerified() {
        synchronized(fullAppLockStateLock) {
            isFullAppLockShowing = false
            hasUnlockedInCurrentForegroundSession = true
        }
    }

    fun onFullAppLockCancelled() {
        synchronized(fullAppLockStateLock) {
            isFullAppLockShowing = false
        }
    }

    private fun resetFullAppLockStateForBackground() {
        synchronized(fullAppLockStateLock) {
            isFullAppLockShowing = false
            hasUnlockedInCurrentForegroundSession = false
        }
    }

    private fun handleAppBackgroundTransition() {
        resetFullAppLockStateForBackground()
    }

    private fun shouldSkipForegroundFullAppLock(activity: Activity): Boolean {
        return activity is LockScreenActivity ||
            activity is WelcomeActivity ||
            activity is SplashScreenActivity
    }

    private fun launchFullAppLock(intent: Intent) {
        if (!tryBeginFullAppLockFlow()) {
            logD("setupLockScreen","launchFullAppLock-------11")
            return
        }

        startActivity(
            intent.putExtra(LockScreenActivity.EXTRA_RETURN_RESULT, false)
        )
    }


    suspend fun chackDataBase() {
        CoroutineScope(Dispatchers.IO).launch {
            if (chackPermission()) {
                logD("fdf", "dfdhgsh")
                if (isFLow1 || isFLow2) {
                    if (!hasAppFlow23BootstrapSyncStarted) {
                        hasAppFlow23BootstrapSyncStarted = true
                        appSmsSyncManager.onAppBecameDefault()
                    } else {
                        appSmsSyncManager.startRealtimeOnly()
                    }
                } else {
                    messagerDatabaseRepo.deletedata()
                    getMobileMessage.refreshSmsInbox()
                }
            }
        }

    }

    suspend fun dialnumber(mobileNumber: String) {
        CoroutineScope(Dispatchers.IO).launch {
            currentActivity?.let {
                it.dialNumber(mobileNumber)
            }
        }
    }


    suspend fun updateMessageStatus(s: String, l: Long) {
        messagerDatabaseRepo.updatemessagestatusRepo(s, l)
    }

    suspend fun updatemessagestatusfrommessageRepoApp(s: String, l: Long) {
        messagerDatabaseRepo.updatemessagestatusfrommessageRepo(s, l)
    }

    suspend fun sendMessageCompat(
        drivingmodemessage: String,
        listOf: List<String>,
        subscriptionId: Int,
        arrayListOf: ArrayList<Attachment>,
        isgroupmessage: Boolean,
    ) {
        sendSMSManager.get().sendMessageCompat(
            drivingmodemessage, listOf, subscriptionId, arrayListOf, isgroupmessage
        )
    }

    suspend fun insertMessageToDatabase(
        i: Int,
        toString: String,
        b: Boolean,
        fetchContactIdFromPhoneNumber: String,
        nothing: Nothing?,
        b1: Boolean,
        dest: String,
        joinToString: String,
        time: Long,
        i1: Int,
        digitsOnly: Boolean,
        insertedId: Long,
        tredid2: Long,
    ): Long {
        var tredid = tredid2

        val data = messagerDatabaseRepo.getUserMessageListChackrepo(tredid)
        if (data.isEmpty()) {
            val phoneNumber = PhoneNumber(dest, 0, "", dest)
            val datausingmobile =
                messagerDatabaseRepo.getUserMessageMobileListChackrepo(phoneNumber.normalizedNumber)
            if (datausingmobile.isEmpty()) {

                val redata =
                    messagerDatabaseRepo.getUserMessageRecyListChackrepo(phoneNumber.normalizedNumber)
                tredid = if (redata.isEmpty()) {
                    tredid2
                } else {
                    if (redata[0].threadId != null) {
                        redata[0].threadId!!
                    } else {
                        tredid2
                    }
                }
            } else {
                tredid = if (datausingmobile[0].threadId != null) {
                    datausingmobile[0].threadId!!
                } else {
                    tredid2
                }
            }
        } else {
            tredid = if (data[0].threadId != null) {
                data[0].threadId!!
            } else {
                tredid2
            }
        }


        isUserNotificationShow = if (messagerDatabaseRepo.isNewUserMessageExitsRepo(tredid)) {
            messagerDatabaseRepo.isUserNotificationshowRepo(tredid)
        } else {
            true
        }

        val list = messagerDatabaseRepo.getUserMessageListrepo(tredid)
        val isarchiv = if (list.isEmpty()) {
            false
        } else {
            list[0].isarchived
        }

        val isPrivateChat = if (list.isEmpty()) {
            false
        } else {
            list[0].isPrivateChat
        }


        val conversation = Conversation(
            i,
            toString,
            b,
            fetchContactIdFromPhoneNumber,
            null,
            b1,
            dest,
            joinToString,
            time,
            i1,
            digitsOnly,
            null,
            messageId = insertedId,
            threadId = tredid,
            isarchived = isarchiv,
            isPrivateChat = isPrivateChat,
            shownotification = isUserNotificationShow
        )

        return messagerDatabaseRepo.insertmessage(conversation)
    }

    suspend fun insertmessageMMS(conversation: Conversation) {
        messagerDatabaseRepo.insertmessage(conversation)
    }


    suspend fun getMessageData(threadId: Long) =
        messagerDatabaseRepo.getUserMessageListrepo(threadId)


    suspend fun isUserNotificationshowRepoMMS(threadId: Long) =
        messagerDatabaseRepo.isUserNotificationshowRepo(threadId!!)

    suspend fun isPrivacyChatExitsRepoMMS(threadId: Long) =
        messagerDatabaseRepo.isPrivacyChatExitsRepo(threadId)


    suspend fun postNotificaitonprivatechat(
        senderNameNew: String,
        s: String,
        address: String,
        threadId: Long,
        glideBitmap: Bitmap?,
    ) {
        notificationProvider.postNotificaitonprivatechat(
            senderNameNew, "New Message", address, threadId, glideBitmap
        )


    }

    fun chackPermission(): Boolean {
        return PermissionChecker.checkSelfPermission(
            this, android.Manifest.permission.SEND_SMS
        ) == PermissionChecker.PERMISSION_GRANTED && PermissionChecker.checkSelfPermission(
            this, android.Manifest.permission.READ_CONTACTS
        ) == PermissionChecker.PERMISSION_GRANTED && PermissionChecker.checkSelfPermission(
            this, android.Manifest.permission.WRITE_CONTACTS
        ) == PermissionChecker.PERMISSION_GRANTED
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

        if (!isAdsShowing) {
//            Log.e(TAG, "onEnteredBackground Started->$activity==$currentActivity==$openManager")
            currentActivity = activity
        }
        markFirstActivity(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        startedActivityCount += 1
        if (startedActivityCount == 1) {
            AdsOrchestrator.get(this).onAppForegrounded(activity)
        }
    }

    override fun onActivityResumed(activity: Activity) {

        currentActivity = activity
        markFirstActivity(activity)
        clearAllNotifications()
        AdsOrchestrator.get(this).onActivityResumed(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityStopped(activity: Activity) {
        startedActivityCount = (startedActivityCount - 1).coerceAtLeast(0)
        if (startedActivityCount == 0) {
            AdsOrchestrator.get(this).onAppBackgrounded()
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            AdsOrchestrator.get(this).onAppBackgrounded()
        }
    }

    private fun clearAllNotifications() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    interface OnShowAdCompleteListener {
        fun onShowAdComplete()
    }


    private fun isThirdPartyIntent(intent: Intent): Boolean {
        if ((intent.action == Intent.ACTION_SENDTO || intent.action == Intent.ACTION_SEND || intent.action == Intent.ACTION_VIEW) && intent.dataString != null) {
            val number =
                intent.dataString!!.removePrefix("sms:").removePrefix("smsto:").removePrefix("mms")
                    .removePrefix("mmsto:").replace("+", "%2b").trim()
            return true
        }
        return false
    }

    private fun markFirstActivity(activity: Activity) {
        if (firstActivityClassName == null) {
            firstActivityClassName = activity.javaClass.name
            Log.e(TAG, "onCreate:maybePreloadUserLaunchBanners 000000")

            scheduleDeferredStartup()
            maybePreloadUserLaunchBanners()
        }
    }

    private fun isUserLaunch(): Boolean {
        return firstActivityClassName != null
    }

    fun Context.firebaseECpmTracker(payload: Bundle) {
        val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseAnalytics.logEvent("AdmobEvent", payload)
    }

    private fun maybePreloadUserLaunchBanners() {

        if (::googleMobileAdsConsentManager.isInitialized.not()) return
        if (!googleMobileAdsConsentManager.canRequestAds) return
        if (!isUserLaunch()) return
        if (BaseSharedPreferences(applicationContext).mIS_SUBSCRIBED || !isOnline()) return
        if (!bannersPreloaded.compareAndSet(false, true)) return


    }

    private fun scheduleDeferredStartup() {
        if (!deferredStartupScheduled.compareAndSet(false, true)) {
            return
        }

        appStartupScope.launch(Dispatchers.IO) {
            try {
                ensureFacebookSdkInitialized()
                initializeMobileAdsSdk()
                val configmic = ClarityConfig(projectId = "n6qw4auo5u")
                Clarity.initialize(this@MessagerApplication, configmic)
            } catch (t: Throwable) {
                Log.w(TAG, "Deferred background startup failed", t)
            }
        }

    }

    private fun ensureFacebookSdkInitialized(): Boolean {
        if (FacebookSdk.isInitialized()) {
            return true
        }

        synchronized(facebookSdkInitLock) {
            if (FacebookSdk.isInitialized()) {
                return true
            }

            return try {
                FacebookSdk.setAutoInitEnabled(true)
                FacebookSdk.fullyInitialize()
                FacebookSdk.isInitialized()
            } catch (t: Throwable) {
                Log.w(TAG, "Facebook SDK initialization failed", t)
                false
            }
        }
    }

    private fun initializeMobileAdsSdk() {
        if (!isMobileAdsInitializeCalled.compareAndSet(false, true)) {
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                MobileAds.initialize(applicationContext) {
                    Log.d(TAG, "MobileAds.initialize completed")
                }
            } catch (t: Throwable) {
                isMobileAdsInitializeCalled.set(false)
            }
        }
    }

    fun getAllMessageOnlyList(): List<Conversation> {
        return messagerDatabaseRepo.getallconversationOnlyListrepo().distinctBy { it.threadId }
    }

    override fun onCompletedPurchase() {
//        this.toastMess("Message_Subscription_Purchase_Successfully")
//        firebaseEventMain("Subscription_Purchase_Successfully")
    }

    override fun onCompletedPurchaseNew(key: String, from: String) {
        purchaseEvents(from)
        config.totalpucheshcount += 1
        val total = config.totalpucheshcount
        firebaseEventMain("Total_Purchase")
        logPurchaseEvent("Message_Total_Purchase")

        if (firebaseeventTXT != "") {
            firebaseEventMain(firebaseeventTXT)
            logPurchaseEvent(firebaseeventTXT)
        }

        when (key) {
            "subscribe_lifetime_message" -> {
                firebaseEventMain("Lifetime_Purchase_Successfully")
                logPurchaseEvent("Message_Lifetime_Purchase_Successfully")
            }

            "subscribe_yearly_messagess" -> {
                firebaseEventMain("Year_Purchase_Successfully")
                logPurchaseEvent("Message_Year_Purchase_Successfully")
            }

            "subscribe_monthly_messages" -> {
                firebaseEventMain("Month_Purchase_Successfully")
                logPurchaseEvent("Message_Month_Purchase_Successfully")
            }

            "subscribe_weekly_messages" -> {
                firebaseEventMain("Week_Purchase_Successfully")
                logPurchaseEvent("Message_Week_Purchase_Successfully")
            }

            "experiment_lifetime_message" -> {
                firebaseEventMain("Lifetime_Purchase_Successfully")
                logPurchaseEvent("Message_Lifetime_Purchase_Successfully")
            }

            "yearly_messages_experiment" -> {
                firebaseEventMain("Year_Purchase_Successfully")
                logPurchaseEvent("Message_Year_Purchase_Successfully")
            }

            "monthly_messages_experiment" -> {
                firebaseEventMain("Month_Purchase_Successfully")
                logPurchaseEvent("Message_Month_Purchase_Successfully")
            }

            "experiment_weekly_messages" -> {
                firebaseEventMain("Week_Purchase_Successfully")
                logPurchaseEvent("Message_Week_Purchase_Successfully")
            }

            else -> {
            }
        }
    }


    private fun wireOneSignalToRevenueCat() {
        // (a) If OneSignal already has a user ID, send it to RevenueCat now
        val existingId = OneSignal.User.onesignalId
        if (!existingId.isNullOrEmpty()) {
            Purchases.sharedInstance.setOnesignalUserID(existingId)
        }

        // (b) Watch for changes — OneSignal IDs can change after login/logout
        OneSignal.User.addObserver(object : IUserStateObserver {
            override fun onUserStateChange(state: UserChangedState) {
                val newId = state.current.onesignalId
                if (!newId.isNullOrEmpty()) {
                    Purchases.sharedInstance.setOnesignalUserID(newId)
                }
            }
        })

        onUserLoggedIn(existingId)


    }
    fun onUserLoggedIn(myAppUserId: String) {
        // Tell OneSignal who this user is
        OneSignal.login(myAppUserId)

        Purchases.sharedInstance.logIn(
            myAppUserId,
            object : LogInCallback {

                override fun onReceived(
                    customerInfo: CustomerInfo,
                    created: Boolean
                ) {
                    Log.d("RC", "RevenueCat user identified")
                }

                override fun onError(error: PurchasesError) {
                    Log.e("RC", "logIn failed: $error")
                }
            }
        )
    }

    fun onUserLoggedOut() {
        OneSignal.logout()

        Purchases.sharedInstance.logOut(
            object :ReceiveCustomerInfoCallback{
                override fun onReceived(
                    customerInfo: CustomerInfo
                ) {
                    Log.d("RC", "Logged out")
                }

                override fun onError(error: PurchasesError) {
                    Log.e("RC", "logOut failed: $error")
                }
            }
        )

    }
    fun logPurchaseEvent(key: String) {
        if (!ensureFacebookSdkInitialized()) {
            Log.w(TAG, "Skipping Facebook purchase event because SDK is not initialized: $key")
            return
        }

        try {
            AppEventsLogger.newLogger(this).logEvent(key)
        } catch (t: Throwable) {
            Log.w(TAG, "Failed to log Facebook purchase event: $key", t)
        }
    }

    fun initRealm() {
        VesionCheck()
        CoroutineScope(Dispatchers.IO).launch {
            GlobalTimer.init(this@MessagerApplication)
            val offerConfig = config.Get_Offer_Config_MS
            val globaldata = parseGlobalTimeModel(offerConfig)
            val hasFinished = GlobalTimer.hasFinished()
            "globaldata <------------------> 1 ${globaldata}".log()
            "globaldata <------------------> 2 ${offerConfig}".log()

            withContext(Dispatchers.Main) {

                GlobalTimer.startOrResume(
                    shouldStart = globaldata.shouldStartTimer,
                    minutes = globaldata.timerMinutes,
                    oneTime = globaldata.oneTime
                )

                GlobalTimer.onFinish {
                    offerscreenshow = false
                }

                if (hasFinished) {
                    offerscreenshow = false
                }

            }
        }
    }


    private fun VesionCheck() {
        CoroutineScope(Dispatchers.IO).launch {
            isValidforExperiment = isVersionAtLeast("1.7.8")
            "isValidforExperiment <----------------> ${isValidforExperiment}".log()
        }
    }

    private fun getProcessNameCompat(): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return Application.getProcessName()
        }
        val pid = Process.myPid()
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager ?: return null
        val processes = manager.runningAppProcesses ?: return null
        return processes.firstOrNull { it.pid == pid }?.processName
    }

    private fun ensureWebViewDataDirSuffix() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return
        }

        if (!isWebViewDataDirSet.compareAndSet(false, true)) {
            return
        }

        val currentProcess = getProcessNameCompat() ?: packageName

        if (packageName != currentProcess) {
            WebView.setDataDirectorySuffix(currentProcess)
        }

    }


    override fun getListTestDeviceIdSub(): List<String> {
        return emptyList()
    }

    override fun showTestIdAlertSub(): Boolean {
        return false
    }

    override fun AutosetTestDeviceIdSub(): Boolean {
        return true
    }

    override fun enableopenAdsSub(): Boolean {
        return false
    }

    override fun disablebackgroundAdsSub(): Boolean {
        return false
    }

    override fun getOpenAppAdIdSub(): String? {
        return null
    }


}
