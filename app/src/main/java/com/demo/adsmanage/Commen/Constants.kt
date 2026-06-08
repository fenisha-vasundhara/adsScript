package com.demo.adsmanage.Commen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import com.demo.adsmanage.helper.logD

import com.google.firebase.analytics.FirebaseAnalytics
import com.messenger.phone.number.text.sms.service.apps.BuildConfig
import com.messenger.phone.number.text.sms.service.apps.CommanClass.baseConfig
import com.revenuecat.purchases.Package
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.messenger.phone.number.text.sms.service.apps.R



var All_Ads_On = true

fun myEdgeToEdge(
    vAnd15StatusBar: View
) {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        vAnd15StatusBar.beVisible()
        ViewCompat.setOnApplyWindowInsetsListener(vAnd15StatusBar) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updateLayoutParams {
                height = statusBarHeight
            }
            insets
        }
    } else {
        vAnd15StatusBar.beGone()
    }
}


fun Activity.updateStatusbarColorMAinAppApp() {
    val window = this.window;
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    WindowCompat.getInsetsController(window, window.decorView).apply {
        isAppearanceLightStatusBars = true
    }
    window.statusBarColor = ContextCompat.getColor(this, R.color.white)


    WindowCompat.setDecorFitsSystemWindows(window, true)
    val controller = WindowInsetsControllerCompat(window, window.decorView)
//    controller.hide(WindowInsetsCompat.Type.navigationBars())
    controller.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
}

var totalRevenue = 0.0
val decimalFormat = DecimalFormat("0.00000") // 5 decimal places, no scientific notation

var isValidforExperiment = true

suspend fun Context.isVersionAtLeast(required: String): Boolean = withContext(Dispatchers.IO) {
    val context = applicationContext
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val versionName = packageInfo.versionName
    if (versionName != null) {
        val currentParts = versionName.split(".")
        val requiredParts = required.split(".")
        val maxLength = maxOf(currentParts.size, requiredParts.size)
        for (i in 0 until maxLength) {
            val cur = currentParts.getOrNull(i)?.toIntOrNull() ?: 0
            val req = requiredParts.getOrNull(i)?.toIntOrNull() ?: 0
            if (cur > req) return@withContext true
            if (cur < req) return@withContext false
        }
        return@withContext true
    } else {
        return@withContext false
    }
}


var newadsclass_vs_oldadsclass_MS_DE = "1"


var firebaseeventTXT = ""

var isadsloadfalse = false

var istestingadalod = true


//var AD_Interstitial_MessageBack_click = "ca-app-pub-2033413118114270/1418725855"

var subpurchese: (() -> Unit)? = null

fun View.beInvisible() {
    visibility = View.INVISIBLE
}

fun Context.getSharedPrefs() = getSharedPreferences("ADSPref", Context.MODE_PRIVATE)

val Context.configADS: BaseConfigADS get() = BaseConfigADS.newInstance(this)

val firebaseeventstop = true
var funnelOn = true


var onetimego = true
var onetimelogOnboardingFunnelStep = false

fun Context.firebaseFunnel(activityName: String) {
    Log.e("FFF", "firebaseEventMain .funnelOn=$funnelOn   log :--- " + activityName )

    if (funnelOn) {
        logD("Funnel Event--> firebaseFunnel-->: ", activityName)
        val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        "OnBordingFlow_AB 1 <----------> ${baseConfig.OnBordingFlow_AB}".log()
        val params = Bundle().apply {
            putString("activity_name", activityName)
            putString("button_name", activityName)
        }
//        if (BuildConfig.DEBUG) {
            firebaseAnalytics.logEvent(activityName, params)
//        }

    } else {
        if (!activityName.contains("first", ignoreCase = true)) {
            val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
            "OnBordingFlow_AB 1 <----------> ${baseConfig.OnBordingFlow_AB}".log()

            logD("firebaseFunnel--> ", activityName)
            firebaseAnalytics.logEvent(activityName, null)
        }
    }

}

fun Context.firebaseAdsEvent(activityName: String) {
    Log.e("FFF", "firebaseAdsEvent .   log :--- " + activityName)
    if (funnelOn) {
        logD("Funnel Event--> firebaseAdsEvent-->: ", activityName)

        val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        "OnBordingFlow_AB 1 <----------> ${baseConfig.OnBordingFlow_AB}".log()

        val params = Bundle().apply {
            putString("activity_name", activityName)
            putString("button_name", activityName)
        }
        firebaseAnalytics.logEvent(activityName, params)
    } else {
        if (!activityName.contains("first", ignoreCase = true)) {
            val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
            "OnBordingFlow_AB 1 <----------> ${baseConfig.OnBordingFlow_AB}".log()

            logD("firebaseAdsEvent--> ", activityName)
            firebaseAnalytics.logEvent(activityName, null)
        }
    }
}

fun Context.firebaseEventMain(activityName: String) {
    Log.e("FFF", "firebaseEventMain .   log :--- " + activityName)
    if (firebaseeventstop) {
        val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        "OnBordingFlow_AB 1 <----------> ${baseConfig.OnBordingFlow_AB}".log()

        val params = Bundle().apply {
            putString("activity_name", activityName)
            putString("button_name", activityName)
        }
        firebaseAnalytics.logEvent(activityName, params)
//        AppMetrica.reportEvent("$activityName")
    }
}

fun View.beVisible() {
    visibility = View.VISIBLE
}

fun View.beGone() {
    visibility = View.GONE
}

fun String.log() {
    Log.d("log", this)
}

var oldclass_vs_newclass_SUB = "1"


var Message_Interstial_Ad_Conversation_Click_Ex = "1"
fun Date.format(pattern: String): String {
    return SimpleDateFormat(pattern).format(this)
}

fun Date.addDays(days: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.DAY_OF_MONTH, days)
    return calendar.time
}

//object Constants {
object Constants {
    internal var PREMIUM_SIX_SKU = ""
    internal var BASIC_SKU = ""
    internal var PREMIUM_SKU = ""
    internal var PREMIUM_LIFTIME = "subscribe_lifetime_message"
    internal var Purchase_ID = ""
    var isbilingopen = false
    var offerscreenshow = true

    internal var mInterstitialAdlist: Any? = null
    internal var mNativeAdlist: Any? = null
    internal var mRewardedAds: Any? = null
    internal var mAppOpenAds: Any? = null
    internal var mAppOpenAds_LANDSCAPE: Any? = null

    internal var isInter_RequestSend: Boolean = false
    internal var isRewarde_RequestSend: Boolean = false
    internal var isAppOpen_RequestSend: Boolean = false
    internal var isAppOpen_RequestSend_LANDSCAPE: Boolean = false

    var isAdsShowing: Boolean = false
    internal var isTestMode: Boolean = false
    var Applang = "en"
    internal var isDebugMode: Boolean = true
    var isAdsClicking: Boolean = false
    var isActivitychange: Boolean? = false
    var IsOutAppPermission: Boolean = false
    internal var isShowAdmobAds: Boolean = true
    internal var isHomeNativeShow: Boolean = true
    internal var isSettingNativeShow: Boolean = true
    internal var isCreationNativeShow: Boolean = true
    internal var is_ProgressShow: Boolean = false
    var is_BackAdsShow: Boolean = false
    internal var mIsRevenuCat: Boolean? = false
    internal var misSubscription: Boolean? = false

    var is_ABTest: Int = 0

    internal var mPreferences: SharedPreferences? = null
    internal var editor: SharedPreferences.Editor? = null

    val APP_OPEN_AD_ORIENTATION_PORTRAIT = 1
    val APP_OPEN_AD_ORIENTATION_LANDSCAPE = 2

    internal var mInterstitialAds_clickCount = 0
    internal var mSplashDelayTime: Long = 0

    internal var mHEIGHT: Int? = 0
    internal var mWIDTH: Int? = 0

    internal var Noads = "NoAds"
    internal var mPrivacyPolicyURL: String? = ""


    internal var mAppIcon: Drawable? = null
    internal var mPremium_True_Icon: Drawable? = null
    internal var mBasic_Line_Icon: Drawable? = null
    internal var BaseSubscriptionBackground: Drawable? = null
    internal var SubscriptionBackground: Drawable? = null
    internal var mPriceBackground: Drawable? = null
    internal var mClose_Icon: Drawable? = null
    internal var mPremium_Button_Icon: Drawable? = null
    internal var mPremium_CardSelected_Icon: Drawable? = null
    internal var mPremium_Cardunselected_Icon: Drawable? = null


    internal var mAppName: String? = null
    internal var mAppNameColor: Int? = null
    internal var mMainLineColor: Int? = null
    internal var mSubLineColor: Int? = null
    internal var mSmallSubLineColor: Int? = null
    internal var mPriceLineColor: Int? = null
    internal var SUBButtonTextColor: Int? = null
    internal var NavigationBarColor: Int? = null

    internal var mPremiumLine: ArrayList<LineWithIconModel>? = null
    internal var mPremiumScreenLine: ArrayList<LineWithIconModel>? = null

    //    internal var packagerenlist: ArrayList<PackagesRen>? = null
    var packagerenlist: ArrayList<com.demo.adsmanage.model.PackagesRen> = arrayListOf()

    //    var testpackagerenlist = listOf<Package>()
//    var testpackagerenlist = listOf<Package>()
    var testpackagerenlist: ArrayList<Package> = arrayListOf()

    data class LineWithIconModel(
        val mLine: String,
        val mLineRight: Boolean,
        val mIconLine: Drawable,
        val mLineColor: Int,
    )

    data class PackagesRen(
        val originalPrice: String,
        val freeTrialPeriod: String,
        val title: String,
        val price: String,
        val description: String,
        val subscriptionPeriod: String,
        val sku: String,
    ) {
        override fun toString(): String {
            return "PackagesRen(originalPrice='$originalPrice', freeTrialPeriod='$freeTrialPeriod', title='$title', price='$price', description='$description', subscriptionPeriod='$subscriptionPeriod', sku='$sku')"
        }
    }

    fun Context.setLocal() {
        setLang(Applang)
    }

    fun Context.setLang(languageCode: String?) {
        val local = languageCode?.let { Locale(it) }
        if (local != null) {
            Locale.setDefault(local)
        }
        val configuration = Configuration()
        configuration.locale = local
        this.resources.updateConfiguration(configuration, this.resources.displayMetrics)
        if (languageCode != null) {
            Applang = languageCode
        }
    }

}

fun Context.openUrlInCustomTabOrFallback(url: String) {
    val packageManager = packageManager
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    val resolvedActivityList =
        packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)

    if (resolvedActivityList.isNotEmpty()) {
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ContextCompat.getColor(this, android.R.color.black))
        val customTabsIntent = builder.build()

        // Find a package that supports Custom Tabs
        val customTabsPackages = listOf(
            "com.android.chrome", "org.mozilla.firefox", "com.microsoft.emmx", "com.brave.browser"
        )
        var customTabsPackage: String? = null

        try {
            for (packageName in customTabsPackages) {
                if (isPackageInstalled(packageManager, packageName)) {
                    customTabsPackage = packageName
                    break
                }
            }
            if (customTabsPackage != null) {
                customTabsIntent.intent.setPackage(customTabsPackage)
                customTabsIntent.launchUrl(this, Uri.parse(url))
            } else {
                // Fallback to default browser
                startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    } else {
        // No browser available
        Toast.makeText(
            this,
            "No browser found",
            Toast.LENGTH_SHORT
        ).show()
    }
}

fun isPackageInstalled(packageManager: PackageManager, packageName: String): Boolean {
    return try {
        packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}
fun Context.purchaseEvents(from: String) {
    // Create a purchase event name with prefix based on build type (debug or release)
    val pEvent = if (BuildConfig.DEBUG) "Test_Purchase_$from" else "Purchase_$from"
    // Log the purchase event to debug log with "Purchase_" prefix
    logD("firebase purchaseEvents--> ", pEvent)
    if (from.isEmpty()) return
    // Get Firebase Analytics instance for this context
    val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
    firebaseAnalytics.logEvent(pEvent, null)
}