//package com.messenger.phone.number.text.sms.service.apps.ApplicationClass
//
//import android.annotation.SuppressLint
//import android.app.Activity
//import android.app.ActivityManager
//import android.app.Application
//import android.app.NotificationManager
//import android.content.ComponentName
//import android.content.Context
//import android.content.Intent
//import android.graphics.Bitmap
//import android.os.Build
//import android.os.Bundle
//import android.os.Handler
//import android.os.Looper
//import android.util.Log
//import android.webkit.WebView
//import androidx.annotation.RequiresApi
//import androidx.core.content.PermissionChecker
//import androidx.core.os.bundleOf
//import androidx.lifecycle.LifecycleObserver
//import androidx.lifecycle.LifecycleOwner
//import androidx.lifecycle.ProcessLifecycleOwner
//import androidx.multidex.MultiDex
//import com.demo.adsmanage.Activity.PayAllSubsciptionExterimentActivity
//import com.demo.adsmanage.Activity.SubActivityTwoplanActivity
//import com.demo.adsmanage.AdsManage
//import com.demo.adsmanage.AdsManage.appOpenAdManager
//import com.demo.adsmanage.Commen.AdsPaidGet
//import com.demo.adsmanage.Commen.Constants
//import com.demo.adsmanage.Commen.Constants.isAdsShowing
//import com.demo.adsmanage.Commen.Constants.offerscreenshow
//import com.demo.adsmanage.Commen.firebaseEventMain
//import com.demo.adsmanage.Commen.firebaseeventTXT
//import com.demo.adsmanage.Commen.isValidforExperiment
//import com.demo.adsmanage.Commen.isVersionAtLeast
//import com.demo.adsmanage.Commen.log
//import com.demo.adsmanage.InterFace.SubPerchechComplete
//import com.demo.adsmanage.InterFace.SubPerchechMainComplete
//import com.demo.adsmanage.NewAdsSDK.AdsManager
//import com.demo.adsmanage.helper.GlobalTimer
//import com.demo.adsmanage.helper.logD
//import com.demo.adsmanage.model.PackagesRen
//import com.demo.adsmanage.viewmodel.AppSubscription
//import com.facebook.FacebookSdk
//import com.facebook.ads.AudienceNetworkActivity
//import com.facebook.appevents.AppEventsConstants
//import com.facebook.appevents.AppEventsLogger
//import com.google.android.gms.ads.AdActivity
//import com.google.android.gms.ads.AdValue
//import com.google.android.gms.ads.MobileAds
//import com.google.firebase.FirebaseApp
//import com.google.firebase.analytics.FirebaseAnalytics
//import com.izooto.TokenReceivedListener
//import com.izooto.iZooto
//import com.messenger.phone.number.text.sms.service.apps.AfterCallScreen.AfterCallActivity
//import com.messenger.phone.number.text.sms.service.apps.AfterCallScreen.fragment.AfterCallMoreFragment
//import com.messenger.phone.number.text.sms.service.apps.CommanClass.ZopIconget
//import com.messenger.phone.number.text.sms.service.apps.CommanClass.adshowcount
//import com.messenger.phone.number.text.sms.service.apps.CommanClass.baseConfig
//import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
//import com.messenger.phone.number.text.sms.service.apps.CommanClass.decimalFormat
//import com.messenger.phone.number.text.sms.service.apps.CommanClass.dialNumber
//import com.messenger.phone.number.text.sms.service.apps.CommanClass.gooutside
//import com.messenger.phone.number.text.sms.service.apps.CommanClass.impressionCount
//import com.messenger.phone.number.text.sms.service.apps.CommanClass.isDarkMode
//import com.messenger.phone.number.text.sms.service.apps.CommanClass.isThirdPartyIntentCheck
//import com.messenger.phone.number.text.sms.service.apps.CommanClass.isfisttimeadopen
//import com.messenger.phone.number.text.sms.service.apps.CommanClass.ispermissiobnor14
//import com.messenger.phone.number.text.sms.service.apps.CommanClass.mobileNumberLock
//import com.messenger.phone.number.text.sms.service.apps.CommanClass.nameLock
//import com.messenger.phone.number.text.sms.service.apps.CommanClass.parseGlobalTimeModel
//import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
//import com.messenger.phone.number.text.sms.service.apps.CommanClass.systemfountstartsetting
//import com.messenger.phone.number.text.sms.service.apps.CommanClass.thredidLock
//import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
//import com.messenger.phone.number.text.sms.service.apps.CommanClass.totalRevenue
//import com.messenger.phone.number.text.sms.service.apps.GoogleMobileAdsConsentManagerChack.GoogleMobileAdsConsentManager
//import com.messenger.phone.number.text.sms.service.apps.HomeABActivity
//import com.messenger.phone.number.text.sms.service.apps.LockScreenActivity
//import com.messenger.phone.number.text.sms.service.apps.MainActivity
//import com.messenger.phone.number.text.sms.service.apps.Notification.notificationProvider
//import com.messenger.phone.number.text.sms.service.apps.OverlayPermissionAnimationActivity
//import com.messenger.phone.number.text.sms.service.apps.R
//import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo
//import com.messenger.phone.number.text.sms.service.apps.SettingActivity
//import com.messenger.phone.number.text.sms.service.apps.ads.afterCall_banner_3
//import com.messenger.phone.number.text.sms.service.apps.ads.listOfAfterCallBanner
//import com.messenger.phone.number.text.sms.service.apps.ads.listOfAppOpen
//import com.messenger.phone.number.text.sms.service.apps.data.Attachment
//import com.messenger.phone.number.text.sms.service.apps.data.GetMobileMessage
//import com.messenger.phone.number.text.sms.service.apps.data.PhoneNumber
//import com.messenger.phone.number.text.sms.service.apps.firebase.AdsManageRemoteConfig
//import com.messenger.phone.number.text.sms.service.apps.firebase.ExampleNotificationHandler
//import com.messenger.phone.number.text.sms.service.apps.helper.ForegroundNew
//import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation
//import com.messenger.phone.number.text.sms.service.apps.sms.SendSMSManager
//import com.microsoft.clarity.Clarity
//import com.microsoft.clarity.ClarityConfig
//import com.revenuecat.purchases.Purchases
//import com.vision.aftercall.sdk.ads.CallerAdMode
//import com.vision.aftercall.sdk.ads.advanceAds.isTakingPermission
//import com.vision.aftercall.sdk.ads.advanceAds.loadAndShowAppOpenAd
//import com.vision.aftercall.sdk.ads.advanceAds.registerNoAppOpenAdActivities
//import com.vision.aftercall.sdk.ads.advanceAds.resumeAppPlace
//import com.vision.aftercall.sdk.core.AfterCallThemeMode
//import com.vision.aftercall.sdk.core.AppearanceLightStatusBars
//import com.vision.aftercall.sdk.core.CallEventListener
//import com.vision.aftercall.sdk.core.CallEventStore
//import com.vision.aftercall.sdk.core.CallEventing
//import com.vision.aftercall.sdk.core.CallerModule
//import com.vision.aftercall.sdk.core.DefaultAfterCallUiConfig
//import com.vision.aftercall.sdk.core.onesignal.OneSignalCallBacks
//import com.vision.aftercall.sdk.ui.DefaultAfterCallActivity
//import com.vision.aftercall.sdk.ui.ResumeMonitoringActivity
//import com.vungle.ads.internal.ui.VungleActivity
//import dagger.hilt.android.HiltAndroidApp
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import java.util.concurrent.atomic.AtomicBoolean
//import javax.inject.Inject
//import kotlin.jvm.java
//
//  for 2 step varification old code by krupalsir > jigarsir
//
//@HiltAndroidApp
//class MessagerApplication : AppSubscription(), SubPerchechComplete, SubPerchechMainComplete,
//    LifecycleObserver, Application.ActivityLifecycleCallbacks,
//    TokenReceivedListener {
//    val TAG = "MessagerApplication"
//    private val moduleListener = CallEventListener { event ->
//        CallEventStore.publish(event)
//    }
//    private var isUserNotificationShow: Boolean = true
//    private val isMobileAdsInitializeCalled = AtomicBoolean(false)
//    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager
//    private var cn: ComponentName? = null
//    private lateinit var am: ActivityManager
//
//    //    var openManager: AppOpenManager? = null
//    var currentActivity: Activity? = null
//
//
//    private var mPackagerenList = arrayListOf(
//        PackagesRen(
//            originalPrice = "₹610.00",
//            freeTrialPeriod = null,
//            title = "Messages - Monthly PRO (Messages : SMS & Private Chat)",
//            price = "₹610.00",
//            description = "",
//            subscriptionPeriod = "P1M",
//            sku = "monthly_messages_experiment",
//            presentedOfferingIdentifier = "Current plan"
//        ), PackagesRen(
//            originalPrice = "₹3,100.00",
//            freeTrialPeriod = null,
//            title = "Messages - Monthly PRO (Messages : SMS & Private Chat)",
//            price = "₹3,100.00",
//            description = "",
//            subscriptionPeriod = "P1Y",
//            sku = "yearly_messages_experiment",
//            presentedOfferingIdentifier = "Current plan"
//        )
//    )
//
//    private lateinit var firebaseAnalytics: FirebaseAnalytics
//
//
//    @Inject
//    lateinit var getMobileMessage: GetMobileMessage
//
//    @Inject
//    lateinit var messagerDatabaseRepo: MessagerDatabaseRepo
//
//    @Inject
//    lateinit var notificationProvider: notificationProvider
//
//    @Inject
//    lateinit var sendSMSManager: SendSMSManager
//
//    companion object {
//        var singleton: MessagerApplication? = null
//        private const val TAG = "BalanceCheckerApplication"
//
//        @JvmStatic
//        val instance: MessagerApplication?
//            get() {
//                if (singleton == null) {
//                    singleton = MessagerApplication()
//                }
//                return singleton
//            }
//    }
//
//    init {
//    }
//
//
//    @RequiresApi(Build.VERSION_CODES.P)
//    @SuppressLint("UseCompatLoadingForDrawables")
//    override fun onCreate() {
//        super<AppSubscription>.onCreate()
//        setLocal()
//        CoroutineScope(Dispatchers.IO).launch {
//            FirebaseApp.initializeApp(this@MessagerApplication)
//            iZooto.initialize(applicationContext).setTokenReceivedListener(this@MessagerApplication)
//                .setNotificationReceiveListener(ExampleNotificationHandler()).build()
//        }
//        initRealm()
//        AdsManage.singleton = this
//        appOpenAdManager = AdsManager(this@MessagerApplication).ManageAds(
//            isTestAds = true, AppOpenAdsShow = true, BannerAdsShow = false
//        )
//        baseConfig.comefromebg = false
//
//        ZopIconget = 1
//
//        try {
//            CallerModule.apply {
//                initialize(
//                    this@MessagerApplication,
//                    autoStartMonitoring = true,
//                    restartOnTaskRemoval = false,
//                    callerIdDialog = false,
//                    callerIdScreen = true,
//                    floatingLayoutRes = 0,
//                    adLayout = com.vision.aftercall.sdk.R.layout.caller_module_native_after_call
//                )
//                initOneSignalKey(
//                    this@MessagerApplication,
//                    "beaaedec-07fd-4ff1-b108-3d5afacf9dcf",
//                    arrayListOf(),
//                    object : OneSignalCallBacks {
//                        override fun oneSignalClick(keyword: String) {
//
//                        }
//                    })
//
//                configureAds(
//                    CallerAdMode.BANNER,
//                    java.util.ArrayList(arrayListOf(afterCall_banner_3)),
//                    arrayListOf(),
//                )
//
//                //Switches
//                registerCallEventing(object : CallEventing {
//                    override fun noPermission() {
////                        showCallerIdDisabledNotification(
////                            this@AppClass.applicationContext,
////                            SplashActivity::class.java, R.mipmap.ic_app_icon, R.color.call_theme
////                        )
//                    }
//                })
//                setCallerIsMultiIdsSwitch(true)
//                setCallerIdScreenEnabled(true)
//                setCallerIdDialogEnabled(false)
//                addListener(moduleListener)
//                setCallerScreenOpenTime(500L, 700L)
//                setCallObservingFlowEnabled(true)
//                val defaultAfterCallUiConfig = DefaultAfterCallUiConfig.builder()
//                    .setPackageName(packageName)
//                    .setThemeColor(com.vision.aftercall.sdk.R.color.caller_module_toolbar)
////                    .setStatusBarColor(R.color.toolbar_color)
//                    .setAppIconResId(R.mipmap.ic_launcher_round)
//                    .setPersonImageResId(R.drawable.contacticon)
//                    .setAppName(getString(R.string.app_name))
//                    .setStatusBarAppearance(AppearanceLightStatusBars.BY_COLOR)
//                    .setShareText("Try our caller ID app!")
//                    .setQuickMessagesRes(
//                        listOf(
//                            R.string.sorry_i_can_t_talk_right_now,
//                            R.string.can_i_call_you_later,
//                            R.string.i_m_on_my_way
//                        )
//                    )
//                    .setShowAds(true) // false to hide CallerModule-managed ads
//                    .setFragmentClass(
//                        AfterCallMoreFragment::class.java,
//                        bundleOf("extra" to "value")
//                    )
//                    .build()
//                if (baseConfig.chat_theme_selection == 3) {
//                    updateDefaultAfterCallTheme(AfterCallThemeMode.DARK)
//                    updateStatusBarColor(R.color.toolbar_color_aftercall_dark)
//                } else {
//                    updateDefaultAfterCallTheme(AfterCallThemeMode.LIGHT)
//                    updateStatusBarColor(R.color.toolbar_color_aftercall)
//                }
//
//                configureDefaultAfterCallUi(defaultAfterCallUiConfig)
////                registerAfterCallActivity(AfterCallActivity::class.java)
//            }
//        } catch (se: SecurityException) {
//            se.printStackTrace()
//        } catch (e: Throwable) {
//            Log.d(TAG, "onCreate: --> CallerModule Throwable ${e.message}")
//        }
//
//
//        if (config.chat_theme_selection == 0) {
//            config.chat_theme_System = true
//            if (!isDarkMode(applicationContext)) {
//                config.chat_theme_selection = 1
//            } else {
//                config.chat_theme_selection = 3
//            }
//        }
//
//
//        if (config.customwallpaperselected == 2) {
//            config.outmessagecolorcustomwallpaperAB = "#F2F2F2"
//            config.inmessagecolorcustomwallpaperAB = "#EBEFF2"
//            config.backgroundcolorcustomwallpaperab = "#FFFFFFF"
//        }
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            val currentProcess = Application.getProcessName() ?: packageName
//            if (packageName != currentProcess) {
//                WebView.setDataDirectorySuffix(currentProcess)
//            }
//        }
//        val backgroundScope = CoroutineScope(Dispatchers.IO)
//        backgroundScope.launch {
//            FacebookSdk.setAutoInitEnabled(true)
//            FacebookSdk.fullyInitialize()
//            // Initialize the Google Mobile Ads SDK on a background thread.
//            MobileAds.initialize(this@MessagerApplication) {}
//
//            val configmic = ClarityConfig(projectId = "n6qw4auo5u")
//            Clarity.initialize(this@MessagerApplication, configmic)
//        }
//
//
//        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true)
//
//        val previousUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
//        val uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { thread, exception ->
//            try {
//                CoroutineScope(Dispatchers.Main).launch {
//                    toastMess("Something Went Wrong")
//                }
//            } finally {
//                previousUncaughtExceptionHandler?.uncaughtException(thread, exception)
//            }
//        }
//        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler)
//
////        if (config.chat_theme_System) {
////            if (!isDarkMode(applicationContext)) {
////                config.chat_theme_selection = 1
////            } else {
////                config.chat_theme_selection = 3
////            }
////        }
//
//        MultiDex.install(this)
//        AdsManageRemoteConfig(applicationContext)
//
//
//        googleMobileAdsConsentManager =
//            GoogleMobileAdsConsentManager.getInstance(applicationContext)
//        currentActivity?.let {
//            try {
//                googleMobileAdsConsentManager.gatherConsent(it) { consentError ->
//                    if (consentError != null) {
//                        Log.w(TAG, "${consentError.errorCode}. ${consentError.message}")
//                    }
//                    if (googleMobileAdsConsentManager.canRequestAds) {
//                        initializeMobileAdsSdk()
//                    }
//                }
//            } catch (E: Exception) {
//            }
//        }
//        singleton = this
//
//        ForegroundNew.init(this)
//        val foregroundListener = object : ForegroundNew.Listener {
//            override fun background() {
//                Log.d(TAG, "setupLockScreen: <-------------> white isue 5055 5555")
//            }
//
//            override fun foreground() {
//                Log.d(TAG, "setupLockScreen: <-------------> white isue 5055")
//                CoroutineScope(Dispatchers.IO).launch {
//                    setupLockScreen()
//                }
//            }
//        }
//        ForegroundNew.addListener(foregroundListener)
//
//        CoroutineScope(Dispatchers.IO).launch {
////            val country = getCountryName()
////            "getUserCountry <------------------> Tier ${country.first} <-> country ${country.second}".log()
////
////            val mBasePremiumLine = arrayListOf(
////                Constants.LineWithIconModel(
////                    applicationContext.resources.getString(R.string.Backup_and_Restore),
////                    false,
////                    this@MessagerApplication.resources.getDrawable(R.drawable.ic_backup),
////                    R.color.sub_text
////                ), Constants.LineWithIconModel(
////                    applicationContext.resources.getString(R.string.Secure_Message),
////                    true,
////                    this@MessagerApplication.resources.getDrawable(R.drawable.ic_secure),
////                    R.color.sub_text
////                ), Constants.LineWithIconModel(
////                    applicationContext.resources.getString(R.string.Remove_Ads),
////                    true,
////                    this@MessagerApplication.resources.getDrawable(R.drawable.ic_ads),
////                    R.color.sub_text
////                ), Constants.LineWithIconModel(
////                    applicationContext.resources.getString(R.string.Schedule_Message),
////                    true,
////                    this@MessagerApplication.resources.getDrawable(R.drawable.ic_media),
////                    R.color.sub_text
////                ), Constants.LineWithIconModel(
////                    applicationContext.resources.getString(R.string.User_Support),
////                    true,
////                    this@MessagerApplication.resources.getDrawable(R.drawable.ic_support),
////                    R.color.sub_text
////                )
////            )
////
////            val mMainPremiumLine = arrayListOf(
////                Constants.LineWithIconModel(
////                    applicationContext.resources.getString(R.string.Backup_and_Restore),
////                    false,
////                    this@MessagerApplication.resources.getDrawable(R.drawable.ic_backup),
////                    R.color.sub_text
////                ), Constants.LineWithIconModel(
////                    applicationContext.resources.getString(R.string.Secure_Message),
////                    true,
////                    this@MessagerApplication.resources.getDrawable(R.drawable.ic_secure),
////                    R.color.sub_text
////                ), Constants.LineWithIconModel(
////                    applicationContext.resources.getString(R.string.Remove_Ads),
////                    true,
////                    this@MessagerApplication.resources.getDrawable(R.drawable.ic_ads),
////                    R.color.sub_text
////                ), Constants.LineWithIconModel(
////                    applicationContext.resources.getString(R.string.Schedule_Message),
////                    true,
////                    this@MessagerApplication.resources.getDrawable(R.drawable.ic_media),
////                    R.color.sub_text
////                ), Constants.LineWithIconModel(
////                    applicationContext.resources.getString(R.string.User_Support),
////                    true,
////                    this@MessagerApplication.resources.getDrawable(R.drawable.ic_support),
////                    R.color.sub_text
////                )
////            )
//
//            AdsManage.ActivityBuilder().ApplicationCall(this@MessagerApplication)
//                .setAdmobAdsID("", "", "", "", "").setFBAdsID("", "", "", "")
//                .setIsSubscription(true).setBASIC_SKU("monthly_messages_experiment")
//                .setPREMIUM_SKU("yearly_messages_experiment")
//                .setPREMIUM_SIX_SKU("experiment_weekly_messages").setIsRevenuCat(true)
//                .setRevenuCatPurchase_ID("goog_vwffVeAqEsqRtnAPZwaXudfQnHV")
//                .setRevenuCatDefaultList(mPackagerenList)
//                .setPrivacyPolicyURL("https://imagecropnwallpaperchanger.blogspot.com/2023/07/privacy-policy.html")
//                .setBackgroundSubscreenLine(arrayListOf()).setSubScreenOfLine(arrayListOf())
//                .setAppName(
//                    this@MessagerApplication.resources.getString(R.string.app_name),
//                    com.demo.adsmanage.R.color.black2
//                ).setBackgroundSubscreenLineColor(R.color.black2).setPriceTextColor(R.color.black2)
//                .setSubLineColor(com.demo.adsmanage.R.color.black2)
//                .setNavigationBarColor(com.demo.adsmanage.R.color.white)
//                .setSmallSubLineColor(com.demo.adsmanage.R.color.light_gray_color)
//                .setSUBButtonTextColor(R.color.white)
//                .setAppIcon(this@MessagerApplication.resources.getDrawable(R.drawable.appicon_sub))
//                .setPremium_True_Icon(this@MessagerApplication.resources.getDrawable(R.drawable.check))
//                .setBasic_Line_Icon(this@MessagerApplication.resources.getDrawable(R.drawable.line))
//                .setBaseSubscriptionBackground(this@MessagerApplication.resources.getDrawable(R.drawable.bg))
//                .setSubscriptionBackground(this@MessagerApplication.resources.getDrawable(R.drawable.bg))
//                .setClose_Icon(this@MessagerApplication.resources.getDrawable(R.drawable.close_icon))
//                .setPremium_CardSelected_Icon(this@MessagerApplication.resources.getDrawable(R.drawable.ic_pro_selected))
//                .setPremium_Cardunselected_Icon(this@MessagerApplication.resources.getDrawable(R.drawable.ic_pro_selection))
//                .setPremium_Button_Icon(this@MessagerApplication.resources.getDrawable(R.drawable.bg_sub_btn_))
//                .Subcall(this@MessagerApplication).CallSubLister(this@MessagerApplication)
//                .CallSubListerNew(this@MessagerApplication)
//
//            firebaseAnalytics = FirebaseAnalytics.getInstance(this@MessagerApplication)
//            FacebookSdk.setAutoLogAppEventsEnabled(false)
//            AppEventsLogger.activateApp(this@MessagerApplication)
//
//            firebaseAnalytics.appInstanceId.addOnSuccessListener { instanceId ->
//                if (instanceId != null) {
//                    "firebaseAnalytics <----------------> Instance ID -> $instanceId".log()
////                    Purchases.sharedInstance.setAttributes(mapOf("\$firebaseAppInstanceId" to instanceId))
//                    Purchases.sharedInstance.setFirebaseAppInstanceID(instanceId)
//                } else {
//                    "firebaseAnalytics <----------------> Instance ID -> NOT FOUND!".log()
//                }
//            }.addOnFailureListener {
//                "firebaseAnalytics <----------------> addOnFailureListener".log()
//            }
//        }
//
////        openManager = AppOpenManager(this)
//        registerActivityLifecycleCallbacks(this)
////        moduleRegisterActivityLifecycleCallbacks()
//        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
//
//        try {
////            subpurchese = {
////                firebaseEventMain("Message_Subscription_Purchase_Successfully")
////            }
//        } catch (e: Exception) {
//        }
//
//        when (config.Home_Banner_VS_Native_VS_Random) {
//            "1" -> {
//                adshowcount = 1
//            }
//
//            "2" -> {
//                adshowcount = 2
//            }
//
//            "3" -> {
//                adshowcount = (1..2).random()
//            }
//        }
//        adshowcount = 2
//        registerNoAppOpenAdActivities(
//            AdActivity::class.java,
//            VungleActivity::class.java,
//            AfterCallActivity::class.java,
//            AudienceNetworkActivity::class.java,
//            OverlayPermissionAnimationActivity::class.java,
//            ResumeMonitoringActivity::class.java,
//            SubActivityTwoplanActivity::class.java,
//            PayAllSubsciptionExterimentActivity::class.java
//        )
//    }
//
//
//    suspend fun setupLockScreen() {
//
//        if (systemfountstartsetting) {
//            systemfountstartsetting = false
//            return
//        }
//
//        Log.d(TAG, "setupLockScreen: <-------------> white isue 2 ${!Constants.isAdsClicking}")
//        Log.d(
//            TAG,
//            "setupLockScreen: <-------------> white isue 25 ${!com.messenger.phone.number.text.sms.service.apps.adsnew.Constants.isAdsClicking}"
//        )
//        Log.d(
//            TAG, "setupLockScreen: <-------------> white isue 26 ${!Constants.IsOutAppPermission}"
//        )
//        Log.d(TAG, "setupLockScreen: <-------------> white isue 27 ${!Constants.isAdsShowing} ")
////        Log.d(
////            TAG,
////            "setupLockScreen: <-------------> white isue 28 ${
////                currentActivity?.intent?.let {
////                    isThirdPartyIntent(it)
////                }
////            }"
////        )
////        Log.d(TAG, "setupLockScreen: <-------------> white isue 29")
//        if (currentActivity?.intent?.let { isThirdPartyIntent(it) } == false && !Constants.isAdsClicking && !com.messenger.phone.number.text.sms.service.apps.adsnew.Constants.isAdsClicking && !Constants.isAdsShowing) {
//            try {
//                am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//                cn = am.getRunningTasks(1)[0].topActivity
//            } catch (_: Exception) {
//            }
//            Log.d(TAG, "setupLockScreen: <-------------> white isue 1")
//            if (!Constants.isAdsClicking && (cn?.className != "com.messenger.phone.number.text.sms.service.apps.LockScreenActivity") && (cn?.className != "com.google.android.gms.ads.AdActivity")) {
//                Log.d(TAG, "setupLockScreen: <-------------> white isue 3")
//                if (!config.introshow) {
//                    Log.d(TAG, "setupLockScreen: <-------------> white isue 4")
//                    if (config.Full_AppLock_Pin != "Not Set") {
//                        Log.d(TAG, "setupLockScreen: <-------------> white isue 5")
//                        if ((cn?.className == "com.messenger.phone.number.text.sms.service.apps.IntroActivity")) {
//                            Log.d(TAG, "setupLockScreen: <-------------> white isue 6")
//                            startActivity(
//                                Intent(
//                                    currentActivity, LockScreenActivity::class.java
//                                ).putExtra("comefrom", 2).putExtra("appopen", true)
//                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                            )
//                        } else {
//                            Log.d(TAG, "setupLockScreen: <-------------> white isue 7")
//                            if (config.Lock_Screen_Pin != "Not Set") {
//                                Log.d(TAG, "setupLockScreen: <-------------> white isue 8")
//                                if ((cn?.className == "com.messenger.phone.number.text.sms.service.apps.PrivacyChatActivity")) {
//                                    Log.d(TAG, "setupLockScreen: <-------------> white isue 9")
//                                    currentActivity?.finish()
//                                    startActivity(
//                                        Intent(
//                                            currentActivity,
//                                            LockScreenActivity::class.java
//                                        ).putExtra("lockype", 2).putExtra("comefrom", 2)
//                                            .putExtra("openprivatechat", true)
//                                            .putExtra("gotingprivatechat", true)
//                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                    )
//                                } else if ((if (applicationContext.config.Message_Send_Activity == "1") {
//                                        cn?.className == "com.messenger.phone.number.text.sms.service.apps.SendmessageAB.SendMessageActivityAB"
//                                    } else {
//                                        cn?.className == "com.messenger.phone.number.text.sms.service.apps.SendMessageActivity"
//                                    })
//                                ) {
//                                    Log.d(TAG, "setupLockScreen: <-------------> white isue 10")
//                                    CoroutineScope(Dispatchers.IO).launch {
//                                        val list =
//                                            messagerDatabaseRepo.getUserMessageListrepo(thredidLock)
//                                        val isPrivateChat = if (list.isEmpty()) {
//                                            false
//                                        } else {
//                                            list[0].isPrivateChat
//                                        }
//                                        if (isPrivateChat) {
//                                            Log.d(
//                                                TAG,
//                                                "setupLockScreen: <-------------> white isue 11"
//                                            )
//                                            currentActivity?.finish()
////                                            startActivity(
////                                                Intent(currentActivity, LockScreenActivity::class.java).putExtra("tredid", thredidLock)
////                                                    .putExtra("name", nameLock).putExtra("mobileNumber", mobileNumberLock).putExtra("lockype", 1).putExtra("comefrom", 1).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
////                                            )
//                                            startActivity(
//                                                Intent(
//                                                    currentActivity, LockScreenActivity::class.java
//                                                ).putExtra("comefrom", 2).putExtra("appopen", false)
//                                                    .putExtra("openprivatechat", true)
//                                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                            )
//                                        } else {
//                                            if (config.Full_AppLock_Pin != "Not Set") {
////                                                startActivity(
////                                                    Intent(currentActivity, LockScreenActivity::class.java).putExtra("tredid", thredidLock)
////                                                        .putExtra("name", nameLock).putExtra("mobileNumber", mobileNumberLock).putExtra("lockype", 1).putExtra("comefrom", 1).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
////                                                )
////                                                currentActivity?.finish()
////                                                startActivity(
////                                                    Intent(currentActivity, LockScreenActivity::class.java)
////                                                        .putExtra("comefrom", 2)
////                                                        .putExtra("appopen", false)
////                                                        .putExtra("openprivatechat", true)
////                                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
////                                                )
//                                                startActivity(
//                                                    Intent(
//                                                        currentActivity,
//                                                        LockScreenActivity::class.java
//                                                    ).putExtra("comefrom", 2)
//                                                        .putExtra("appopen", false)
//                                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                                )
//                                                Log.d(
//                                                    TAG,
//                                                    "setupLockScreen: <-------------> white isue 1111111"
//                                                )
//                                            }
//                                        }
//                                    }
//                                } else {
//                                    Log.d(TAG, "setupLockScreen: <-------------> white isue 12")
//                                    startActivity(
//                                        Intent(
//                                            currentActivity, LockScreenActivity::class.java
//                                        ).putExtra("comefrom", 2).putExtra("appopen", false)
//                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                    )
//                                }
//                            } else {
//                                Log.d(TAG, "setupLockScreen: <-------------> white isue 13")
//                                startActivity(
//                                    Intent(
//                                        currentActivity, LockScreenActivity::class.java
//                                    ).putExtra("comefrom", 2).putExtra("appopen", false)
//                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                )
//                            }
//                        }
//                    } else if (config.Lock_Screen_Pin != "Not Set") {
//                        Log.d(TAG, "setupLockScreen: <-------------> white isue 14")
//                        if ((cn?.className == "com.messenger.phone.number.text.sms.service.apps.PrivacyChatActivity")) {
//                            Log.d(TAG, "setupLockScreen: <-------------> white isue 15")
//                            currentActivity?.finish()
//                            startActivity(
//                                Intent(
//                                    currentActivity, LockScreenActivity::class.java
//                                ).putExtra("lockype", 2).putExtra("comefrom", 1)
//                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                            )
//                        } else if (if (applicationContext.config.Message_Send_Activity == "1") {
//                                cn?.className == "com.messenger.phone.number.text.sms.service.apps.SendmessageAB.SendMessageActivityAB"
//                            } else {
//                                cn?.className == "com.messenger.phone.number.text.sms.service.apps.SendMessageActivity"
//                            }
//                        ) {
//                            Log.d(TAG, "setupLockScreen: <-------------> white isue 16")
//                            CoroutineScope(Dispatchers.IO).launch {
//                                val list = messagerDatabaseRepo.getUserMessageListrepo(thredidLock)
//                                val isPrivateChat = if (list.isEmpty()) {
//                                    false
//                                } else {
//                                    list[0].isPrivateChat
//                                }
//                                if (isPrivateChat) {
//                                    Log.d(TAG, "setupLockScreen: <-------------> white isue 17")
//                                    currentActivity?.finish()
//                                    startActivity(
//                                        Intent(
//                                            currentActivity, LockScreenActivity::class.java
//                                        ).putExtra("tredid", thredidLock).putExtra("name", nameLock)
//                                            .putExtra("mobileNumber", mobileNumberLock)
//                                            .putExtra("lockype", 1).putExtra("comefrom", 1)
//                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                    )
//                                }
//                            }
//                        } else {
//                            Log.d(TAG, "setupLockScreen: <-------------> white isue 18")
//                            if ((cn?.className == "com.messenger.phone.number.text.sms.service.apps.IntroActivity")) {
//                                Log.d(TAG, "setupLockScreen: <-------------> white isue 19")
////                                startActivity(Intent(currentActivity, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
//                                if (!config.introshow) {
//                                    if (!config.firsttimeusersetlang) {
//                                        if (config.SelectedLanguage == "en") {
//                                            startActivity(
//                                                Intent(
//                                                    currentActivity,
//                                                    SettingActivity::class.java
//                                                ).putExtra("loadiswhatsnew", 101)
//                                                    .putExtra("onbackbress", true)
//                                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                            )
//                                        } else {
////                                        config.firsttimeusersetlang = true
//                                            if (this.baseConfig.setABHomeActivityPref == "1") {
//                                                startActivity(
//                                                    Intent(
//                                                        currentActivity, MainActivity::class.java
//                                                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                                )
//                                            } else {
//                                                startActivity(
//                                                    Intent(
//                                                        currentActivity, HomeABActivity::class.java
//                                                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                                )
//                                            }
//
//                                        }
//                                    } else {
//                                        if (!config.introshow) {
//                                            if (this.baseConfig.setABHomeActivityPref == "1") {
//                                                startActivity(
//                                                    Intent(
//                                                        currentActivity, MainActivity::class.java
//                                                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                                )
//                                            } else {
//                                                startActivity(
//                                                    Intent(
//                                                        currentActivity, HomeABActivity::class.java
//                                                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                                )
//                                            }
//                                        }
//                                    }
//                                } else {
//                                    if (!config.firsttimeusersetlang) {
//                                        if (config.SelectedLanguage == "en") {
//                                            startActivity(
//                                                Intent(
//                                                    currentActivity,
//                                                    SettingActivity::class.java
//                                                ).putExtra("loadiswhatsnew", 101)
//                                                    .putExtra("onbackbress", true)
//                                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                            )
//                                        } else {
////                                        config.firsttimeusersetlang = true
//                                            if (this.baseConfig.setABHomeActivityPref == "1") {
//                                                startActivity(
//                                                    Intent(
//                                                        currentActivity, MainActivity::class.java
//                                                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                                )
//                                            } else {
//                                                startActivity(
//                                                    Intent(
//                                                        currentActivity, HomeABActivity::class.java
//                                                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                                )
//                                            }
//                                        }
//                                    } else {
//                                        if (this.baseConfig.setABHomeActivityPref == "1") {
//                                            startActivity(
//                                                Intent(
//                                                    currentActivity, MainActivity::class.java
//                                                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                            )
//                                        } else {
//                                            startActivity(
//                                                Intent(
//                                                    currentActivity, HomeABActivity::class.java
//                                                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                            )
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    } else {
//                        Log.d(TAG, "setupLockScreen: <-------------> white isue 20")
//                        if ((cn?.className == "com.messenger.phone.number.text.sms.service.apps.IntroActivity")) {
//                            Log.d(TAG, "setupLockScreen: <-------------> white isue 21")
//                            if (!config.introshow) {
//                                if (!config.firsttimeusersetlang) {
//                                    if (config.SelectedLanguage == "en") {
//                                        startActivity(
//                                            Intent(
//                                                currentActivity,
//                                                SettingActivity::class.java
//                                            ).putExtra("loadiswhatsnew", 101)
//                                                .putExtra("onbackbress", true)
//                                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                        )
//                                    } else {
////                                        config.firsttimeusersetlang = true
//                                        if (this.baseConfig.setABHomeActivityPref == "1") {
//                                            startActivity(
//                                                Intent(
//                                                    currentActivity, MainActivity::class.java
//                                                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                            )
//                                        } else {
//                                            startActivity(
//                                                Intent(
//                                                    currentActivity, HomeABActivity::class.java
//                                                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                            )
//                                        }
//                                    }
//                                } else {
//                                    if (!config.introshow) {
//                                        if (this.baseConfig.setABHomeActivityPref == "1") {
//                                            startActivity(
//                                                Intent(
//                                                    currentActivity, MainActivity::class.java
//                                                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                            )
//                                        } else {
//                                            startActivity(
//                                                Intent(
//                                                    currentActivity, HomeABActivity::class.java
//                                                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                            )
//                                        }
//                                    }
//                                }
//                            } else {
//                                if (!config.firsttimeusersetlang) {
//                                    if (config.SelectedLanguage == "en") {
//                                        startActivity(
//                                            Intent(
//                                                currentActivity,
//                                                SettingActivity::class.java
//                                            ).putExtra("loadiswhatsnew", 101)
//                                                .putExtra("onbackbress", true)
//                                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                        )
//                                    } else {
////                                        config.firsttimeusersetlang = true
//                                        if (this.baseConfig.setABHomeActivityPref == "1") {
//                                            startActivity(
//                                                Intent(
//                                                    currentActivity, MainActivity::class.java
//                                                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                            )
//                                        } else {
//                                            startActivity(
//                                                Intent(
//                                                    currentActivity, HomeABActivity::class.java
//                                                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                            )
//                                        }
//                                    }
//                                } else {
//                                    if (this.baseConfig.setABHomeActivityPref == "1") {
//                                        startActivity(
//                                            Intent(
//                                                currentActivity, MainActivity::class.java
//                                            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                        )
//                                    } else {
//                                        startActivity(
//                                            Intent(
//                                                currentActivity, HomeABActivity::class.java
//                                            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
//                } else {
//                    if (!config.firsttimeusersetlang) {
//                        if (config.SelectedLanguage == "en") {
////                            startActivity(
////                                Intent(this, SettingActivity::class.java)
////                                    .putExtra("loadiswhatsnew", 101)
////                                    .putExtra("onbackbress", true)
////                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
////                            )
//                        } else {
//                            config.firsttimeusersetlang = true
//                        }
//                    }
//                }
//            } else {
//                Log.d(TAG, "setupLockScreen: <-------------> white isue 22")
//                if (!Constants.isAdsClicking && (cn?.className != "com.messenger.phone.number.text.sms.service.apps.LockScreenActivity") && (cn?.className != "com.google.android.gms.ads.AdActivity")) {
//                    Log.d(TAG, "setupLockScreen: <-------------> white isue 23")
//                    startActivity(
//                        Intent(
//                            currentActivity, LockScreenActivity::class.java
//                        ).putExtra("comefrom", 2).putExtra("appopen", false)
//                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    )
//                }
//            }
//        } else {
//            Log.d(TAG, "setupLockScreen: currentActivity <-----> 500")
//            if (isThirdPartyIntentCheck == false) {
//                Log.d(TAG, "setupLockScreen: currentActivity <-----> 5001")
//                if (!ispermissiobnor14) {
//                    Log.d(TAG, "setupLockScreen: currentActivity <-----> 502")
//                    ispermissiobnor14 = false
//                    if (this.baseConfig.setABHomeActivityPref == "1") {
//                        Log.d(TAG, "setupLockScreen: currentActivity <-----> 503")
//                        startActivity(
//                            Intent(
//                                currentActivity, MainActivity::class.java
//                            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        )
//                    } else {
//                        Log.d(TAG, "setupLockScreen: currentActivity <-----> 504")
//                        if (Constants.isAdsClicking) {
//                            startActivity(
//                                Intent(
//                                    currentActivity, HomeABActivity::class.java
//                                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                            )
//                        }
//
//                    }
//                }
//            } else {
//                isThirdPartyIntentCheck = false
//            }
////
//        }
//    }
//
//
//    suspend fun chackDataBase() {
//        CoroutineScope(Dispatchers.IO).launch {
//            if (chackPermission()) {
//                logD("fdf", "dfdhgsh")
//                messagerDatabaseRepo.deletedata()
//                getMobileMessage.refreshSmsInbox()
//            }
//        }
//
//    }
//
//    suspend fun dialnumber(mobileNumber: String) {
//
//        CoroutineScope(Dispatchers.IO).launch {
//            currentActivity?.let {
//                it.dialNumber(mobileNumber)
//            }
//        }
//
//    }
//
//
//    suspend fun updateMessageStatus(s: String, l: Long) {
//        messagerDatabaseRepo.updatemessagestatusRepo(s, l)
//    }
//
//    suspend fun updatemessagestatusfrommessageRepoApp(s: String, l: Long) {
//        messagerDatabaseRepo.updatemessagestatusfrommessageRepo(s, l)
//    }
//
//    suspend fun sendMessageCompat(
//        drivingmodemessage: String,
//        listOf: List<String>,
//        subscriptionId: Int,
//        arrayListOf: ArrayList<Attachment>,
//        isgroupmessage: Boolean,
//    ) {
//        sendSMSManager.sendMessageCompat(
//            drivingmodemessage, listOf, subscriptionId, arrayListOf, isgroupmessage
//        )
//    }
//
//    suspend fun insertMessageToDatabase(
//        i: Int,
//        toString: String,
//        b: Boolean,
//        fetchContactIdFromPhoneNumber: String,
//        nothing: Nothing?,
//        b1: Boolean,
//        dest: String,
//        joinToString: String,
//        time: Long,
//        i1: Int,
//        digitsOnly: Boolean,
//        insertedId: Long,
//        tredid2: Long,
//    ): Long {
//        var tredid = tredid2
//
//        val data = messagerDatabaseRepo.getUserMessageListChackrepo(tredid)
//        if (data.isEmpty()) {
//            val phoneNumber = PhoneNumber(dest, 0, "", dest)
//            val datausingmobile =
//                messagerDatabaseRepo.getUserMessageMobileListChackrepo(phoneNumber.normalizedNumber)
//            if (datausingmobile.isEmpty()) {
//                val redata =
//                    messagerDatabaseRepo.getUserMessageRecyListChackrepo(phoneNumber.normalizedNumber)
//                tredid = if (redata.isEmpty()) {
//                    tredid2
//                } else {
//                    if (redata[0].threadId != null) {
//                        redata[0].threadId!!
//                    } else {
//                        tredid2
//                    }
//                }
//            } else {
//                tredid = if (datausingmobile[0].threadId != null) {
//                    datausingmobile[0].threadId!!
//                } else {
//                    tredid2
//                }
//            }
//        } else {
//            tredid = if (data[0].threadId != null) {
//                data[0].threadId!!
//            } else {
//                tredid2
//            }
//        }
//
//
//        isUserNotificationShow = if (messagerDatabaseRepo.isNewUserMessageExitsRepo(tredid)) {
//            messagerDatabaseRepo.isUserNotificationshowRepo(tredid)
//        } else {
//            true
//        }
//
//        val list = messagerDatabaseRepo.getUserMessageListrepo(tredid)
//        val isarchiv = if (list.isEmpty()) {
//            false
//        } else {
//            list[0].isarchived
//        }
//
//        val isPrivateChat = if (list.isEmpty()) {
//            false
//        } else {
//            list[0].isPrivateChat
//        }
//
//
//        val conversation = Conversation(
//            i,
//            toString,
//            b,
//            fetchContactIdFromPhoneNumber,
//            null,
//            b1,
//            dest,
//            joinToString,
//            time,
//            i1,
//            digitsOnly,
//            null,
//            messageId = insertedId,
//            threadId = tredid,
//            isarchived = isarchiv,
//            isPrivateChat = isPrivateChat,
//            shownotification = isUserNotificationShow
//        )
//
//        return messagerDatabaseRepo.insertmessage(conversation)
//    }
//
//    suspend fun insertmessageMMS(conversation: Conversation) {
//        messagerDatabaseRepo.insertmessage(conversation)
//    }
//
//
//    suspend fun getMessageData(threadId: Long) =
//        messagerDatabaseRepo.getUserMessageListrepo(threadId)
//
//
//    suspend fun isUserNotificationshowRepoMMS(threadId: Long) =
//        messagerDatabaseRepo.isUserNotificationshowRepo(threadId!!)
//
//    suspend fun isPrivacyChatExitsRepoMMS(threadId: Long) =
//        messagerDatabaseRepo.isPrivacyChatExitsRepo(threadId)
//
//    suspend fun postNotificaitonprivatechat(
//        senderNameNew: String,
//        s: String,
//        address: String,
//        threadId: Long,
//        glideBitmap: Bitmap?,
//    ) {
//        notificationProvider.postNotificaitonprivatechat(
//            senderNameNew, "New Message", address, threadId, glideBitmap
//        )
//    }
//
//    override fun onStart(owner: LifecycleOwner) {
////        super.onStart(owner)
//        "className <--------------> Activity ${currentActivity == null}".log()
//        currentActivity?.let {
//            "className <--------------> Activity ${currentActivity?.javaClass?.simpleName}".log()
//            if (!isfisttimeadopen) {
//                isfisttimeadopen = true
//                "className <--------------> isfisttimeadopen".log()
//                return
//            }
//            "className <--------------> $gooutside $isTakingPermission  ${config.All_Ads_On}".log()
//            if (!gooutside && !isTakingPermission || config.All_Ads_On) {
//                CoroutineScope(Dispatchers.IO).launch {
//                    "className <--------------> CoroutineScope".log()
//                    loadAndShowAppOpenAd(it, resumeAppPlace, ArrayList(listOfAppOpen))
//                }
//            }
//        }
//        Handler(Looper.myLooper()!!).postDelayed(
//            {
//                gooutside = false
//            }, 800
//        )
//    }
//
//    fun chackPermission(): Boolean {
//        return PermissionChecker.checkSelfPermission(
//            this, android.Manifest.permission.SEND_SMS
//        ) == PermissionChecker.PERMISSION_GRANTED && PermissionChecker.checkSelfPermission(
//            this, android.Manifest.permission.READ_CONTACTS
//        ) == PermissionChecker.PERMISSION_GRANTED && PermissionChecker.checkSelfPermission(
//            this, android.Manifest.permission.WRITE_CONTACTS
//        ) == PermissionChecker.PERMISSION_GRANTED
//    }
//
//    override fun onOpenAdShowSurface(v: Activity?) {
//        "className <--------------> Activity ${currentActivity == null}    ${v == null}".log()
////        currentActivity?.let {
////            "className <--------------> Activity ${currentActivity?.javaClass?.simpleName}".log()
////            if (!isfisttimeadopen) {
////                isfisttimeadopen = true
////                "className <--------------> isfisttimeadopen".log()
////                return
////            }
////            "className <--------------> $gooutside $isTakingPermission  ${config.All_Ads_On}".log()
////            if (!gooutside && !isTakingPermission || config.All_Ads_On) {
////                CoroutineScope(Dispatchers.IO).launch {
////                    "className <--------------> CoroutineScope".log()
////                    loadAndShowAppOpenAd(it, resumeAppPlace, ArrayList(listOfAppOpen))
////                }
////            }
////        }
////        Handler(Looper.myLooper()!!).postDelayed(
////            {
////                gooutside = false
////            }, 800
////        )
//
//    }
//
//    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
//
//        if (!isAdsShowing) {
////            Log.e(TAG, "onEnteredBackground Started->$activity==$currentActivity==$openManager")
//            currentActivity = activity
//        }
//
//    }
//
//    override fun onActivityStarted(activity: Activity) {
//
//    }
//
//    override fun onActivityResumed(activity: Activity) {
//        "className <--------------> onActivityResumed ${activity.javaClass}".log()
//        currentActivity = activity
//        if (activity is DefaultAfterCallActivity) {
//            return
//        }
//        clearAllNotifications()
//    }
//
//    override fun onActivityPaused(activity: Activity) {
//        currentActivity = activity
//    }
//
//    override fun onActivityStopped(activity: Activity) {
//
//    }
//
//    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
//
//    }
//
//    override fun onActivityDestroyed(activity: Activity) {
//
//    }
//
//    private fun clearAllNotifications() {
//        val notificationManager =
//            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.cancelAll()
//    }
//
//    interface OnShowAdCompleteListener {
//        fun onShowAdComplete()
//    }
//
//
//    private fun isThirdPartyIntent(intent: Intent): Boolean {
//        if ((intent.action == Intent.ACTION_SENDTO || intent.action == Intent.ACTION_SEND || intent.action == Intent.ACTION_VIEW) && intent.dataString != null) {
//            val number =
//                intent.dataString!!.removePrefix("sms:").removePrefix("smsto:").removePrefix("mms")
//                    .removePrefix("mmsto:").replace("+", "%2b").trim()
//            return true
//        }
//        return false
//    }
//
//    private fun initializeMobileAdsSdk() {
//        if (isMobileAdsInitializeCalled.getAndSet(true)) {
//            return
//        }
//    }
//
//    fun getAllMessageOnlyList(): List<Conversation> {
//        return messagerDatabaseRepo.getallconversationOnlyListrepo().distinctBy { it.threadId }
//    }
//
//    override fun onCompletedPurchase() {
////        this.toastMess("Message_Subscription_Purchase_Successfully")
////        firebaseEventMain("Message_Subscription_Purchase_Successfully")
//    }
//
//    override fun onCompletedPurchaseNew(key: String) {
//
//        config.totalpucheshcount += 1
//        val total = config.totalpucheshcount
//        firebaseEventMain("Message_Total_Purchase")
//        logPurchaseEvent("Message_Total_Purchase")
//
//        if (firebaseeventTXT != "") {
//            firebaseEventMain(firebaseeventTXT)
//            logPurchaseEvent(firebaseeventTXT)
//        }
//
//        when (key) {
//            "subscribe_lifetime_message" -> {
//                firebaseEventMain("Message_Lifetime_Purchase_Successfully")
//                logPurchaseEvent("Message_Lifetime_Purchase_Successfully")
//            }
//
//            "subscribe_yearly_messagess" -> {
//                firebaseEventMain("Message_Year_Purchase_Successfully")
//                logPurchaseEvent("Message_Year_Purchase_Successfully")
//            }
//
//            "subscribe_monthly_messages" -> {
//                firebaseEventMain("Message_Month_Purchase_Successfully")
//                logPurchaseEvent("Message_Month_Purchase_Successfully")
//            }
//
//            "subscribe_weekly_messages" -> {
//                firebaseEventMain("Message_Week_Purchase_Successfully")
//                logPurchaseEvent("Message_Week_Purchase_Successfully")
//            }
//
//            "experiment_lifetime_message" -> {
//                firebaseEventMain("Message_Lifetime_Purchase_Successfully")
//                logPurchaseEvent("Message_Lifetime_Purchase_Successfully")
//            }
//
//            "yearly_messages_experiment" -> {
//                firebaseEventMain("Message_Year_Purchase_Successfully")
//                logPurchaseEvent("Message_Year_Purchase_Successfully")
//            }
//
//            "monthly_messages_experiment" -> {
//                firebaseEventMain("Message_Month_Purchase_Successfully")
//                logPurchaseEvent("Message_Month_Purchase_Successfully")
//            }
//
//            "experiment_weekly_messages" -> {
//                firebaseEventMain("Message_Week_Purchase_Successfully")
//                logPurchaseEvent("Message_Week_Purchase_Successfully")
//            }
//
//            else -> {
//            }
//        }
//    }
//
//    fun logPurchaseEvent(key: String) {
//        val logger = AppEventsLogger.newLogger(this)
//        logger.logEvent(key)
//    }
//
//    fun initRealm() {
//        VesionCheck()
//        AdsPaidGet = { adValue, adtype ->
//            impressionCount++
//            val revenueMicros = adValue.valueMicros
//            val revenue = revenueMicros / 1_000_000.0
//            val currency = adValue.currencyCode
//            val precision = when (adValue.precisionType) {
//                AdValue.PrecisionType.ESTIMATED -> "Estimated"
//                AdValue.PrecisionType.PUBLISHER_PROVIDED -> "Publisher Provided"
//                AdValue.PrecisionType.PRECISE -> "Precise"
//                else -> "Unknown"
//            }
//            totalRevenue += revenue
//            val formattedRevenue = decimalFormat.format(revenue)
//            val formattedTotalRevenue = decimalFormat.format(totalRevenue)
//            "loadBanner info <------------------> ${adtype} ${"Ad Value: $formattedRevenue"} ${"Total Revenue: $formattedTotalRevenue"}".log()
//            sendFacebookAdImpressionEvent(
//                revenue, totalRevenue, currency, impressionCount, precision
//            )
//        }
//
//        GlobalTimer.init(this)
////        val shouldStartTimer = true
////        val timerMinutes = 99
//        val globaldata = parseGlobalTimeModel(config.Get_Offer_Config_MS)
//        "globaldata <------------------> 1 ${globaldata}".log()
//        "globaldata <------------------> 2 ${config.Get_Offer_Config_MS}".log()
//        GlobalTimer.startOrResume(
//            shouldStart = globaldata.shouldStartTimer,
//            minutes = globaldata.timerMinutes,
//            oneTime = globaldata.oneTime
//        )
//        GlobalTimer.onFinish {
//            offerscreenshow = false
//        }
//        if (GlobalTimer.hasFinished()) {
//            offerscreenshow = false
//        }
//    }
//
//
//    private fun sendFacebookAdImpressionEvent(
//        adValue: Double, totalRevenue: Double, currency: String, count: Int, precision: String
//    ) {
//        val fbLogger = AppEventsLogger.newLogger(this)
//        val params = Bundle().apply {
//            putDouble("ad_value", adValue)
//            putDouble("total_revenue", totalRevenue)
//            putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currency)
//            putString("precision", precision)
//            putInt("impression_count", count)
//        }
//        fbLogger.logEvent(
//            "Message_Ads_Revenue", adValue, params
//        )
//    }
//
//
//    private fun VesionCheck() {
//        CoroutineScope(Dispatchers.IO).launch {
//            isValidforExperiment = isVersionAtLeast("1.7.8")
//            "isValidforExperiment <----------------> ${isValidforExperiment}".log()
//        }
//    }
//
//    override fun onTokenReceived(p0: String?) {
//        "onTokenReceived <---------------> ${p0}".log()
//    }
//
//
//    override fun getListTestDeviceIdSub(): List<String> {
//        return emptyList()
//    }
//
//    override fun showTestIdAlertSub(): Boolean {
//        return false
//    }
//
//    override fun AutosetTestDeviceIdSub(): Boolean {
//        return true
//    }
//
//    override fun enableopenAdsSub(): Boolean {
//        return false
//    }
//
//    override fun disablebackgroundAdsSub(): Boolean {
//        return false
//    }
//
//    override fun getOpenAppAdIdSub(): String? {
//        return null
//    }
//
//    private val AFTERCALL_BANNER_ID = "ca-app-pub-3940256099942544/9214589741"
//    val listOfAfterCallBannerTest =
//        listOf(AFTERCALL_BANNER_ID, AFTERCALL_BANNER_ID, AFTERCALL_BANNER_ID)
//
//}
