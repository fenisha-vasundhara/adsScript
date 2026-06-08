package com.demo.adsmanage

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.util.Log
import com.demo.adsmanage.Commen.Constants
import com.demo.adsmanage.Commen.log
import com.demo.adsmanage.InterFace.SubPerchechComplete
import com.demo.adsmanage.InterFace.SubPerchechMainComplete
import com.demo.adsmanage.basemodule.BaseSharedPreferences
import com.demo.adsmanage.billing.ProductPurchaseHelper.setSubscriptionKey
import com.demo.adsmanage.helper.MySharedPreferences
import com.demo.adsmanage.helper.logD
import com.demo.adsmanage.helper.toJson
import com.demo.adsmanage.model.PackagesRen
import com.demo.adsmanage.subeventcheck.SubscriptionInfo
import com.demo.adsmanage.subeventcheck.SubscriptionResponse
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.revenuecat.purchases.PackageType
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.restorePurchasesWith
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object AdsManage {
    const val TAG = "AdsManageclassTAG"

    var subPerchechComplete: SubPerchechComplete? = null
    var subPerchechCompleteNew: SubPerchechMainComplete? = null
    var singleton: Application? = null
    var onesignalUserSaveId: String? = null

    class ActivityBuilder : Builder() {
        override fun Subcall(context: Context): Builder {
            if (Constants.mIsRevenuCat == true) {
                Constants.packagerenlist.clear()
                logD(TAG, "SubscriptionList Purchase_ID->${Constants.Purchase_ID}")
                setSubscriptionKey(
                    Constants.BASIC_SKU, Constants.PREMIUM_SIX_SKU, Constants.PREMIUM_SKU
                )
                Purchases.debugLogsEnabled = true
                Purchases.configure(
                    PurchasesConfiguration.Builder(context, Constants.Purchase_ID).build()
                )

                Purchases.sharedInstance.setOnesignalUserID(onesignalUserSaveId)

                val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
                firebaseAnalytics.appInstanceId.addOnSuccessListener { instanceId ->
                    if (instanceId != null) {
                        "firebaseAnalytics <----------------> Instance ID -> $instanceId".log()
                        Purchases.sharedInstance.setAttributes(mapOf("\$firebaseAppInstanceId" to instanceId))
                        Purchases.sharedInstance.setFirebaseAppInstanceID(instanceId)
                    } else {
                        "firebaseAnalytics <----------------> Instance ID -> NOT FOUND!".log()
                    }
                }.addOnFailureListener {
                    "firebaseAnalytics <----------------> addOnFailureListener".log()
                }

                Purchases.sharedInstance.collectDeviceIdentifiers()

                Purchases.sharedInstance.getOfferingsWith({ error ->
                    logD("YagnikTestingRevenucat", "error->${error.message}")
                }) { offerings ->
                    Log.wtf("fatz", "<---> ${offerings.toJson()}")
                    writeToCache(context, "offerings.json", offerings.toJson())
                    offerings.current?.availablePackages?.takeUnless { it.isEmpty() }
                        ?.let { packages ->
                            Constants.testpackagerenlist.addAll(packages)
                            packages.forEachIndexed { index, p ->
                                Log.d(
                                    "TAG",
                                    "initListener345: jigar - ${index} <--> ${p.product.period?.iso8601}"
                                )
                            }

                            for (plan in packages) {
                                when (plan.product.period?.iso8601) {
                                    "P1M" -> {
                                        val freepreode =
                                            plan.product.subscriptionOptions!!.freeTrial?.freePhase?.billingPeriod
                                        val monthModel = PackagesRen(
                                            plan.product.price.formatted,
                                            freepreode?.let {
                                                com.demo.adsmanage.model.mPeriod(
                                                    it.value, it.unit.toString(), it.iso8601
                                                )
                                            },
                                            plan.product.title,
                                            (plan.product.price.amountMicros / 1000000).toString(),
                                            plan.product.description,
                                            plan.product.period?.iso8601!!,
                                            plan.product.sku,
                                            plan.product.presentedOfferingIdentifier.toString()
                                        )
                                        logD(
                                            "YagnikTestingRevenucat",
                                            "revenucat month ${monthModel}"
                                        )
                                        Constants.packagerenlist.add(monthModel)
                                    }

                                    "P1Y" -> {
                                        val freepreode =
                                            plan.product.subscriptionOptions!!.freeTrial?.freePhase?.billingPeriod
                                        val yearModel = PackagesRen(
                                            plan.product.price.formatted,
                                            freepreode?.let {
                                                com.demo.adsmanage.model.mPeriod(
                                                    it.value, it.unit.toString(), it.iso8601
                                                )
                                            },
                                            plan.product.title,
                                            (plan.product.price.amountMicros / 1000000).toString(),
                                            plan.product.description,
                                            plan.product.period?.iso8601!!,
                                            plan.product.sku,
                                            plan.product.presentedOfferingIdentifier.toString()
                                        )
                                        logD(
                                            "YagnikTestingRevenucat", "revenucat year ${yearModel}"
                                        )
                                        Constants.packagerenlist.add(yearModel)
                                    }

                                    "P1W" -> {
                                        val freepreode =
                                            plan.product.subscriptionOptions!!.freeTrial?.freePhase?.billingPeriod
                                        val weekModel = PackagesRen(
                                            plan.product.price.formatted,
                                            freepreode?.let {
                                                com.demo.adsmanage.model.mPeriod(
                                                    it.value, it.unit.toString(), it.iso8601
                                                )
                                            },
                                            plan.product.title,
                                            (plan.product.price.amountMicros / 1000000).toString(),
                                            plan.product.description,
                                            plan.product.period?.iso8601!!,
                                            plan.product.sku,
                                            plan.product.presentedOfferingIdentifier.toString()
                                        )
                                        Constants.packagerenlist.add(weekModel)
                                    }

                                    else -> {
                                        if (plan.packageType == PackageType.LIFETIME) {
                                            val lifetimeModel = PackagesRen(
                                                plan.product.price.formatted,
                                                null,
                                                plan.product.title,
                                                (plan.product.price.amountMicros / 1000000).toString(),
                                                plan.product.description,
                                                "LIFETIME",
                                                plan.product.sku,
                                                plan.product.presentedOfferingIdentifier.toString()
                                            )
                                            Constants.packagerenlist.add(lifetimeModel)
                                        }
                                    }
                                }
                            }

                            val lifetimeExists = Constants.packagerenlist.any { packageRen ->
                                packageRen.subscriptionPeriod == "LIFETIME"
                            }
                            if (!lifetimeExists) {
                                offerings.all["Current Offering"]?.availablePackages?.takeUnless { it.isNullOrEmpty() }
                                    ?.let { packagesAll ->
                                        for (plan in packagesAll) {
                                            if (plan.packageType == PackageType.LIFETIME) {
                                                Constants.testpackagerenlist.add(plan)
                                                val lifetimeModel = PackagesRen(
                                                    plan.product.price.formatted,
                                                    null,
                                                    plan.product.title,
                                                    (plan.product.price.amountMicros / 1000000).toString(),
                                                    plan.product.description,
                                                    "LIFETIME",
                                                    plan.product.sku,
                                                    plan.product.presentedOfferingIdentifier.toString()
                                                )
                                                Constants.packagerenlist.add(lifetimeModel)
                                            }
                                        }
                                    }
                            }

                            offerings.current?.let { identifiercheck ->
                                BaseSharedPreferences(context).isCurrentPlan =
                                    identifiercheck.identifier
                            }
                        }
                }
            } else {
                setSubscriptionKey(
                    Constants.BASIC_SKU, Constants.PREMIUM_SIX_SKU, Constants.PREMIUM_SKU
                )
            }

            return this
        }
    }

    fun checkPurchases(onPurchase: ((Boolean, SubscriptionResponse?) -> Unit)? = null) {
        Purchases.sharedInstance.restorePurchasesWith(onError = {
            Log.d(TAG, "checkPurchases: sharedInstance error $it")
            onPurchase?.invoke(false, null)
        }, onSuccess = {
            val gson = Gson()
            val response = gson.fromJson(it.toJson(), SubscriptionResponse::class.java)
            if (it.entitlements.active.isEmpty()) {
                onPurchase?.invoke(false, response)
            } else {
                it.entitlements.active.forEach { _, entitlementInfo ->
                    if (entitlementInfo.isActive) {
                        onPurchase?.invoke(true, response)
                    } else {
                        onPurchase?.invoke(false, response)
                    }
                }
            }
        })
    }

    fun convertSubscriptionStatus(subscription: String): String {
        val subscriptionName = subscription.substringAfter("'").substringBefore("'")
        val status = subscription.substringAfter(":").trim()
        return "Message_Subscription_${subscriptionName.replace(" ", "_")}_$status"
    }

    fun determineSubscriptionStatus(subscription: SubscriptionInfo): String {
        val currentDate = Date()
        val expirationDate = parseDate(subscription.expirationDate)
        val unsubscribeDate = subscription.unsubscribeDetectedAt?.let { parseDate(it) }
        val originalPurchaseDate = parseDate(subscription.originalPurchaseDate)
        val latestPurchaseDate = parseDate(subscription.latestPurchaseDate)

        return when {
            subscription.isActive -> {
                if (latestPurchaseDate == originalPurchaseDate) {
                    if (subscription.isTrial) "Trial_Subscription" else "First_time_Purchase"
                } else {
                    "Renewal"
                }
            }

            unsubscribeDate != null && currentDate.after(unsubscribeDate) -> "Cancelled"
            currentDate.after(expirationDate) -> "Expired"
            else -> "Inactive"
        }
    }

    private fun parseDate(dateString: String): Date {
        val dateFormats = listOf(
            SimpleDateFormat("MMM d, yyyy h:mm:ss a", Locale.US),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        )

        for (format in dateFormats) {
            format.timeZone = TimeZone.getTimeZone("UTC")
            try {
                return format.parse(dateString) ?: Date()
            } catch (_: ParseException) {
            }
        }

        return Date()
    }

    private fun writeToCache(context: Context, filename: String, data: String) {
        val file = File(context.cacheDir, filename)
        file.writeText(data)
    }

    abstract class Builder {

        fun ApplicationCall(application: Application): Builder {
            Constants.mPreferences =
                application.getSharedPreferences("MyAdsClass", Context.MODE_PRIVATE)
            Constants.editor = Constants.mPreferences?.edit()
            return this
        }

        fun setIsRevenuCat(boolean: Boolean): Builder {
            Constants.mIsRevenuCat = boolean
            return this
        }

        fun setRevenuCatPurchase_ID(string: String): Builder {
            Constants.Purchase_ID = string
            return this
        }

        fun setPREMIUM_SIX_SKU(string: String): Builder {
            Constants.PREMIUM_SIX_SKU = string
            return this
        }

        fun setPREMIUM_SKU(string: String): Builder {
            Constants.PREMIUM_SKU = string
            return this
        }

        fun setBASIC_SKU(string: String): Builder {
            Constants.BASIC_SKU = string
            return this
        }

        fun setPrivacyPolicyURL(string: String): Builder {
            Constants.mPrivacyPolicyURL = string
            return this
        }

        fun setAppName(appName: String, color: Int): Builder {
            Constants.mAppName = appName
            Constants.mAppNameColor = color
            return this
        }

        fun setBackgroundSubscreenLineColor(color: Int): Builder {
            Constants.mMainLineColor = color
            return this
        }

        fun setSubLineColor(color: Int): Builder {
            Constants.mSubLineColor = color
            return this
        }

        fun setSmallSubLineColor(color: Int): Builder {
            Constants.mSmallSubLineColor = color
            return this
        }

        fun setSUBButtonTextColor(color: Int): Builder {
            Constants.SUBButtonTextColor = color
            return this
        }

        fun setPriceTextColor(color: Int): Builder {
            Constants.mPriceLineColor = color
            return this
        }

        fun setNavigationBarColor(color: Int): Builder {
            Constants.NavigationBarColor = color
            return this
        }

        fun setAppIcon(drawable: Drawable): Builder {
            Constants.mAppIcon = drawable
            return this
        }

        fun setPremium_True_Icon(drawable: Drawable): Builder {
            Constants.mPremium_True_Icon = drawable
            return this
        }

        fun setBasic_Line_Icon(drawable: Drawable): Builder {
            Constants.mBasic_Line_Icon = drawable
            return this
        }

        fun setBaseSubscriptionBackground(drawable: Drawable): Builder {
            Constants.BaseSubscriptionBackground = drawable
            return this
        }

        fun setSubscriptionBackground(drawable: Drawable): Builder {
            Constants.SubscriptionBackground = drawable
            return this
        }

        fun setClose_Icon(drawable: Drawable?): Builder {
            Constants.mClose_Icon = drawable
            return this
        }

        fun setPremium_Button_Icon(drawable: Drawable): Builder {
            Constants.mPremium_Button_Icon = drawable
            return this
        }

        fun setPremium_CardSelected_Icon(drawable: Drawable): Builder {
            Constants.mPremium_CardSelected_Icon = drawable
            return this
        }

        fun setPremium_Cardunselected_Icon(drawable: Drawable): Builder {
            Constants.mPremium_Cardunselected_Icon = drawable
            return this
        }

        fun setBackgroundSubscreenLine(premiumLine: ArrayList<Constants.LineWithIconModel>): Builder {
            Constants.mPremiumLine = premiumLine
            return this
        }

        fun setSubScreenOfLine(premiumLine: ArrayList<Constants.LineWithIconModel>): Builder {
            Constants.mPremiumScreenLine = premiumLine
            return this
        }

        fun setRevenuCatDefaultList(premiumLine: ArrayList<PackagesRen>): Builder {
            Constants.packagerenlist = premiumLine
            return this
        }

        fun setIsSubscription(isSubscription: Boolean): Builder {
            Constants.misSubscription = isSubscription
            return this
        }

        fun setAdmobAdsID(
            appOpenId: String? = "",
            bannerAdaptiveId: String? = "",
            interstitialId: String? = "",
            nativeAdsId: String? = "",
            rewardedId: String? = ""
        ): Builder {
            MySharedPreferences.AD_Interstitial = interstitialId
            MySharedPreferences.AD_Banner = bannerAdaptiveId
            MySharedPreferences.AD_AppOpen = appOpenId
            MySharedPreferences.AD_NativeAds = nativeAdsId
            MySharedPreferences.AD_RewardedAds = rewardedId
            return this
        }

        fun setFBAdsID(
            fbInterstitial: String? = "",
            fbBanner: String? = "",
            fbNativeAds: String? = "",
            fbRewardedAds: String? = ""
        ): Builder {
            MySharedPreferences.FB_Interstitial = fbInterstitial
            MySharedPreferences.FB_Banner = fbBanner
            MySharedPreferences.FB_NativeAds = fbNativeAds
            MySharedPreferences.FB_RewardedAds = fbRewardedAds
            return this
        }

        fun CallSubLister(subPerchechCompleteMain: SubPerchechComplete): Builder {
            subPerchechComplete = subPerchechCompleteMain
            return this
        }

        fun CallSubListerNew(subPerchechCompleteNewMain: SubPerchechMainComplete): Builder {
            subPerchechCompleteNew = subPerchechCompleteNewMain
            return this
        }

        fun setApplang(languageCode: String): Builder {
            val local = Locale(languageCode)
            Locale.setDefault(local)
            val configuration = Configuration()
            configuration.locale = local
            return this
        }

        abstract fun Subcall(context: Context): Builder
    }
}
