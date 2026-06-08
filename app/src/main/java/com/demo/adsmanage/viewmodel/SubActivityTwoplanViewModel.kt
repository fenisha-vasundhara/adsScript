package com.demo.adsmanage.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.demo.adsmanage.Activity.SubActivityTwoplanActivity.Companion.plans
import com.demo.adsmanage.AdsManage.subPerchechComplete
import com.demo.adsmanage.Commen.ConnectionLiveData
import com.demo.adsmanage.Commen.Constants
import com.demo.adsmanage.Commen.Constants.BASIC_SKU
import com.demo.adsmanage.Commen.Constants.PREMIUM_LIFTIME
import com.demo.adsmanage.Commen.Constants.PREMIUM_SIX_SKU
import com.demo.adsmanage.Commen.Constants.PREMIUM_SKU
import com.demo.adsmanage.Commen.Constants.SUBButtonTextColor
import com.demo.adsmanage.Commen.Constants.SubscriptionBackground
import com.demo.adsmanage.Commen.Constants.isbilingopen
import com.demo.adsmanage.Commen.Constants.mAppIcon
import com.demo.adsmanage.Commen.Constants.mAppName
import com.demo.adsmanage.Commen.Constants.mAppNameColor
import com.demo.adsmanage.Commen.Constants.mClose_Icon
import com.demo.adsmanage.Commen.Constants.mIsRevenuCat
import com.demo.adsmanage.Commen.Constants.mPremiumScreenLine
import com.demo.adsmanage.Commen.Constants.mPremium_CardSelected_Icon
import com.demo.adsmanage.Commen.Constants.mPremium_Cardunselected_Icon
import com.demo.adsmanage.Commen.Constants.mPriceLineColor
import com.demo.adsmanage.Commen.Constants.mSmallSubLineColor
import com.demo.adsmanage.Commen.Constants.mSubLineColor
import com.demo.adsmanage.Commen.Constants.packagerenlist
import com.demo.adsmanage.Commen.Constants.testpackagerenlist
import com.demo.adsmanage.Commen.beGone
import com.demo.adsmanage.Commen.beInvisible
import com.demo.adsmanage.Commen.beVisible
import com.demo.adsmanage.Commen.configADS
import com.demo.adsmanage.Commen.openUrlInCustomTabOrFallback
import com.demo.adsmanage.Commen.subpurchese
import com.messenger.phone.number.text.sms.service.apps.R
import com.demo.adsmanage.SubscriptionBaseClass.manager.PreferencesKeys
import com.demo.adsmanage.SubscriptionBaseClass.manager.SubscriptionManager
import com.demo.adsmanage.basemodule.BaseSharedPreferences
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivitySubTwoplanBinding
import com.demo.adsmanage.helper.IconPosition
import com.demo.adsmanage.helper.click
import com.demo.adsmanage.helper.getMonthBaseYearlyDiscount
import com.demo.adsmanage.helper.getSubTrial
import com.demo.adsmanage.helper.gone
import com.demo.adsmanage.helper.isOnline
import com.demo.adsmanage.helper.logD
import com.demo.adsmanage.helper.setCloseIconPosition
import com.demo.adsmanage.helper.showToast
import com.demo.adsmanage.helper.toJson
import com.demo.adsmanage.billing.ProductPurchaseHelper.getProductInfo
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PackageType
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.interfaces.PurchaseCallback
import com.revenuecat.purchases.models.StoreTransaction
//import com.vision.aftercall.sdk.ads.advanceAds.registerAdsConfigSubscribe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt


class SubActivityTwoplanViewModel(
    var binding: ActivitySubTwoplanBinding,
    var mActivity: AppCompatActivity,
    var liveDataPeriod: MutableLiveData<HashMap<String, String>>,
    var liveDataPrice: MutableLiveData<HashMap<String, String>>,
    var liveDataPriceMicro: MutableLiveData<HashMap<String, Long>>,
    var subscriptionManager: SubscriptionManager,
    var isSelecterdPlan: IsSelecterdPlan,

    ) : ViewModel() {
    @SuppressLint("AnnotateVersionCheck")
    private var connectionLiveData: ConnectionLiveData = ConnectionLiveData(mActivity)
    fun isPiePlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    val idname = arrayOf("_one", "_two", "_three", "_four", "_five", "_six", "_seven", "_eight")

    init {
        plans = PREMIUM_SIX_SKU
        connectionLiveData.observe(mActivity, androidx.lifecycle.Observer {
            if (packagerenlist.isEmpty()) {
                packagerenlist.clear()
                testpackagerenlist.clear()
                Purchases.sharedInstance.getOfferingsWith({ error ->
                })
                { offerings ->
                    offerings.current?.availablePackages?.takeUnless { it.isNullOrEmpty() }
                        ?.let { it ->
                            testpackagerenlist.addAll(it)
                            for (plan in it) {
                                when (plan.product.period?.iso8601) {
                                    "P1M" -> {
                                        val freepreode =
                                            plan.product.subscriptionOptions!!.freeTrial?.let {
                                                it?.freePhase!!.billingPeriod
                                            }
                                        val month_model =
                                            com.demo.adsmanage.model.PackagesRen(
                                                plan.product.price.formatted,
                                                freepreode?.let {
                                                    com.demo.adsmanage.model.mPeriod(
                                                        freepreode.value,
                                                        freepreode.unit.toString(),
                                                        freepreode.iso8601
                                                    )
                                                },
                                                plan.product.title,
                                                (plan.product.price.amountMicros / 1000000).toString(),
                                                plan.product.description,
                                                plan.product.period?.iso8601!!,
                                                plan.product.sku,
                                                plan.product.presentedOfferingIdentifier.toString()
                                            )
                                        packagerenlist.add(month_model)
                                    }

                                    "P1Y" -> {
                                        val freepreode =
                                            plan.product.subscriptionOptions!!.freeTrial?.let {
                                                it?.freePhase!!.billingPeriod
                                            }
                                        val month_Year =
                                            com.demo.adsmanage.model.PackagesRen(
                                                plan.product.price.formatted,
                                                freepreode?.let {
                                                    com.demo.adsmanage.model.mPeriod(
                                                        freepreode.value,
                                                        freepreode.unit.toString(),
                                                        freepreode.iso8601
                                                    )
                                                },
                                                plan.product.title,
                                                (plan.product.price.amountMicros / 1000000).toString(),
                                                plan.product.description,
                                                plan.product.period?.iso8601!!,
                                                plan.product.sku,
                                                plan.product.presentedOfferingIdentifier.toString()
                                            )
                                        packagerenlist.add(month_Year)
                                    }

                                    "P1W" -> {
                                        val freepreode =
                                            plan.product.subscriptionOptions!!.freeTrial?.let {
                                                it?.freePhase!!.billingPeriod
                                            }
                                        val week_Year =
                                            com.demo.adsmanage.model.PackagesRen(
                                                plan.product.price.formatted,
                                                freepreode?.let {
                                                    com.demo.adsmanage.model.mPeriod(
                                                        freepreode.value,
                                                        freepreode.unit.toString(),
                                                        freepreode.iso8601
                                                    )
                                                },
                                                plan.product.title,
                                                (plan.product.price.amountMicros / 1000000).toString(),
                                                plan.product.description,
                                                plan.product.period?.iso8601!!,
                                                plan.product.sku,
                                                plan.product.presentedOfferingIdentifier.toString()
                                            )
                                        packagerenlist.add(week_Year)
                                    }

                                    else -> {
                                        if (plan.packageType == PackageType.LIFETIME) {
                                            val lifetime_Year =
                                                com.demo.adsmanage.model.PackagesRen(
                                                    plan.product.price.formatted,
                                                    null,
                                                    plan.product.title,
                                                    (plan.product.price.amountMicros / 1000000).toString(),
                                                    plan.product.description,
                                                    "LIFETIME",
                                                    plan.product.sku,
                                                    plan.product.presentedOfferingIdentifier.toString()
                                                )
                                            packagerenlist.add(lifetime_Year)
                                        }
                                    }
                                }
                            }

                            val lifetimeExists = packagerenlist.any { packageRen ->
                                packageRen.subscriptionPeriod == "LIFETIME"
                            }
                            if (!lifetimeExists) {
                                offerings.all.get("Current Offering")?.availablePackages?.takeUnless { it.isNullOrEmpty() }
                                    ?.let { it ->
                                        for (plan in it) {
                                            when (plan.packageType) {
                                                PackageType.LIFETIME -> {
                                                    testpackagerenlist.add(plan)
                                                    val lifetime_Year =
                                                        com.demo.adsmanage.model.PackagesRen(
                                                            plan.product.price.formatted,
                                                            null,
                                                            plan.product.title,
                                                            (plan.product.price.amountMicros / 1000000).toString(),
                                                            plan.product.description,
                                                            "LIFETIME",
                                                            plan.product.sku,
                                                            plan.product.presentedOfferingIdentifier.toString()
                                                        )
                                                    packagerenlist.add(lifetime_Year)
                                                }

                                                else -> {

                                                }
                                            }
                                        }
                                    }
                            }

//                            BASIC_SKU = it[0].product.sku
//                            PREMIUM_SIX_SKU = it[2].product.sku
//                            PREMIUM_SKU = it[1].product.sku
                            offerings.current?.let { identifierchack ->
                                BaseSharedPreferences(mActivity).isCurrentPlan = identifierchack.identifier
                            }
                            onMain()
                        }
                }
                onMain()
            } else {
                onMain()
            }
        })
        onMain()
    }

    interface IsSelecterdPlan {
        fun monMonthPlan()
        fun monYearPlan()
        fun monWeekPlan()
        fun monBackPress()
    }

    fun onMain() {
        setUI()
        setSubScriptionUI()
        setLineView()
        initListener()
    }

    @SuppressLint("SetTextI18n")
    fun setSubScriptionUI() {
        with(binding) {
            txtUnlockKriadl.setTextColor(mActivity.resources.getColor(SUBButtonTextColor!!))
            if (mIsRevenuCat!!) {
                if (packagerenlist.isNotEmpty()) {

                    if (packagerenlist!![0].freeTrialPeriod == null) {
                        txtUnlockKriadl.text = mActivity.getString(R.string.continue_txt)
                    } else {
                        val trialMessage = mActivity.getString(
                            R.string.enjoy_month_trial,
                            mActivity.getSubTrial(packagerenlist!![0].freeTrialPeriod!!.iso8601)
                        )
                        txtUnlockKriadl.text = mActivity.getString(R.string.continue_txt)
                    }

                    packagerenlist?.get(1)?.price?.let { it1 ->
                        getMonthBaseYearlyDiscount(
                            packagerenlist?.get(0)?.price!!, it1
                        ) { yearlyDiscountPercentage, yearlyMonthBaseDiscountPrice ->

                        }
                    }

                    packagerenlist?.forEachIndexed { index, packagesRen ->
                        val price = packagesRen.originalPrice
                        val pricemic = packagesRen.price


                        if (packagesRen.sku.contains("monthly")) {

                        } else if (packagesRen.sku.contains("yearly")) {

                        } else if (packagesRen.sku.contains("lifetime")) {
                            txtlifePrice.text = price.replace(".00", "")
                        } else {
                            txtweekPrice.text = price.replace(".00", "")
                            txtlifediscountid.text = "${price.first()}${
                                pricemic.toDouble().times(100).roundToInt()
                            }"
                            txtlifediscountid.paintFlags =
                                txtlifediscountid.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                            try {
                            } catch (e: Exception) {
                            }
                        }
                    }
                    if (testpackagerenlist.isNotEmpty()) {
                        val month =
                            testpackagerenlist.find { it.packageType == PackageType.MONTHLY }
                        val year =
                            testpackagerenlist.find { it.packageType == PackageType.ANNUAL }
                        val week =
                            testpackagerenlist.find { it.packageType == PackageType.WEEKLY }
                        val lifetime =
                            testpackagerenlist.find { it.packageType == PackageType.LIFETIME }

                        val monthly =
                            week?.let { weekPackage ->
                                month?.let { monthPackage ->
                                    getDiscount(
                                        weekPackage,
                                        monthPackage
                                    )
                                }
                            }

                        val yearly = year?.let { yearPackage ->
                            week?.let { weekPackage ->
                                getDiscount(
                                    weekPackage,
                                    yearPackage
                                )
                            }
                        }

                        val lifetimeDiscount = lifetime?.let { yearPackage ->
                            week?.let { weekPackage ->
                                getDiscount(
                                    weekPackage,
                                    yearPackage
                                )
                            }
                        }
                    }
                }
            } else {
                liveDataPeriod.observe(mActivity) { trial ->
                    liveDataPrice.observe(mActivity) { price ->
                        price[PREMIUM_SIX_SKU]?.let { month ->
                            price[PREMIUM_SKU]?.let { year ->
                                getMonthBaseYearlyDiscount(
                                    month,
                                    year
                                ) { yearlyDiscountPercentage, yearlyMonthBaseDiscountPrice ->
                                    if (yearlyMonthBaseDiscountPrice.equals("₹590.00")) {

                                    } else {

                                    }
                                }
                            }
                        }
                        PREMIUM_SKU.getProductInfo?.let { year ->
                            PREMIUM_SIX_SKU.getProductInfo?.let { month ->
                                if (year.freeTrialPeriod.equals("Not Found", true)) {
                                    txtUnlockKriadl.text =
                                        mActivity.resources.getText(R.string.Continue)
                                } else {
                                    txtUnlockKriadl.text =
                                        mActivity.getString(R.string.continue_txt)
                                }
                                if (month.freeTrialPeriod.equals("Not Found", true)) {
                                    Log.d("jigar", "setSubScriptionUI: <-----------> 3 ${month}")
//                                    txtMonthBottom.gone
                                } else {
                                    Log.d("jigar", "setSubScriptionUI: <-----------> 4 ")
                                }
                            }
                        }
                    }
                }
                liveDataPriceMicro.observe(mActivity) {
                    val yearprice = ((it[Constants.PREMIUM_SKU]!! / 1000000)).toDouble()
                    val monthprice = ((it[Constants.BASIC_SKU]!! / 1000000)).toDouble()
                    val monthcount = (monthprice * 12)
                    val cal = (yearprice as Double / monthcount as Double * 100).toInt()
                    Log.d("TAG", "setSubScriptionUI: $cal--$yearprice--$monthprice--$monthcount")
                }
            }


        }
    }

    @SuppressLint("NewApi", "SetTextI18n")
    fun setUI() {
        with(binding) {
            Log.d("", "initListener: mCLUnlockLayout <-------------> 3")

//            mCLLIFELayout.setBackgroundResource(R.drawable.selection_bg)
//            mCLWeekLayout.setBackgroundResource(R.drawable.unselection_bg)

            if (mIsRevenuCat!!) {
                if (packagerenlist.isNotEmpty()) {
                    if (packagerenlist!![0].freeTrialPeriod == null) {
                        txtUnlockKriadl.text = mActivity.getString(R.string.continue_txt)
                    } else {
                        txtUnlockKriadl.text = mActivity.getString(R.string.continue_txt)
                    }
                }
            } else {
                BASIC_SKU.getProductInfo?.let { month ->
                    if (month.freeTrialPeriod.equals("Not Found", true)) {
                        txtUnlockKriadl.text = mActivity.getString(R.string.continue_txt)
                    } else {
                        txtUnlockKriadl.text = mActivity.getString(R.string.continue_txt)
                    }
                }
            }
//            plans = PREMIUM_LIFTIME

            if (packagerenlist.isNotEmpty()) {
                if (packagerenlist!![0].freeTrialPeriod == null) {
                    if (packagerenlist?.get(0)?.price != null) {
                        try {

                        } catch (e: Exception) {

                        }
                    }
                } else {
                }
            }
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun setLineView() {
        with(binding) {
        }
    }

    @SuppressLint("StringFormatInvalid", "StringFormatMatches", "SetTextI18n")
    private fun initListener() {
        with(binding) {

            mCLLIFELayout.setOnClickListener {
                if (isbilingopen) {
                    return@setOnClickListener
                }
                isbilingopen = true
                mCLLIFELayout.setBackgroundResource(R.drawable.selection_bg)
                mCLWeekLayout.setBackgroundResource(R.drawable.unselection_bg)
                if (mIsRevenuCat!!) {
                    if (packagerenlist.isNotEmpty()) {
                        try {
                            if (packagerenlist[3].freeTrialPeriod == null) {
                                txtUnlockKriadl.text = mActivity.getString(R.string.continue_txt)
                            } else {
                                txtUnlockKriadl.text = mActivity.getString(R.string.continue_txt)
                                val featureDescription = mActivity.getString(
                                    R.string.feature_description,
                                    packagerenlist?.get(2)?.price,
                                    mActivity.getSubTrial(packagerenlist!![3].freeTrialPeriod!!.iso8601)
                                )
                            }
                        } catch (e: Exception) {
                            txtUnlockKriadl.text = mActivity.getString(R.string.continue_txt)
                        }
                    }
                } else {
                    PREMIUM_SIX_SKU.getProductInfo?.let { month ->
                        if (month.freeTrialPeriod.equals("Not Found", true)) {
                            txtUnlockKriadl.text = mActivity.getString(R.string.continue_txt)
                        } else {
                            txtUnlockKriadl.text = mActivity.getString(R.string.continue_txt)
                        }
                    }
                }
                Log.d("PREMIUM_SIX_SKU", "initListener: PREMIUM_SIX_SKU <----> 1 ${plans}")
                plans = PREMIUM_LIFTIME
                Log.d("PREMIUM_SIX_SKU", "initListener: PREMIUM_SIX_SKU <----> 2 ${plans}")

                if (packagerenlist.isNotEmpty()) {
                    if (packagerenlist!![3].freeTrialPeriod == null) {

                    } else {

                    }

                    mCLUnlockLayout.performClick()
                } else {
                    isbilingopen = false
                }

            }

            mCLWeekLayout.setOnClickListener {
                if (isbilingopen) {
                    return@setOnClickListener
                }
                isbilingopen = true
                Log.d("", "initListener: mCLUnlockLayout <-------------> 2")

                mCLLIFELayout.setBackgroundResource(R.drawable.unselection_bg)
                mCLWeekLayout.setBackgroundResource(R.drawable.selection_bg)

                if (mIsRevenuCat!!) {
                    if (packagerenlist.isNotEmpty()) {
                        if (packagerenlist!![1].freeTrialPeriod == null) {
                            txtUnlockKriadl.text = mActivity.getString(R.string.continue_txt)
                        } else {
                            txtUnlockKriadl.text = mActivity.getString(R.string.continue_txt)
                            val featureDescription = mActivity.getString(
                                R.string.feature_description,
                                packagerenlist?.get(1)?.price,
                                mActivity.getSubTrial(packagerenlist!![1].freeTrialPeriod!!.iso8601)
                            )
//                        txtFeature.text = featureDescription
                        }
                    }
                } else {
                    PREMIUM_SIX_SKU.getProductInfo?.let { year ->
                        if (year.freeTrialPeriod.equals("Not Found", true)) {
                            txtUnlockKriadl.text = mActivity.getString(R.string.continue_txt)
                        } else {
                            txtUnlockKriadl.text = mActivity.getString(R.string.continue_txt)
                        }
                    }
                }
                plans = PREMIUM_SIX_SKU

                if (packagerenlist.isNotEmpty()) {
                    if (packagerenlist!![1].freeTrialPeriod == null) {
//                    textsub.text = mActivity.getString(
//                        R.string.year_price,
//                        packagerenlist?.get(1)?.price
//                    )
                        try {
//                            textsub.text = mActivity.getString(
//                                R.string.year_price,
//                                "${packagerenlist?.get(1)?.price?.first()}${
//                                    packagerenlist?.get(1)?.price?.substring(1)
//                                        ?.replace(",", "")?.toDouble()?.roundToInt()
//                                }"
//                            )
                        } catch (e: Exception) {
                        }
                    } else {
//                        try {
//                            textsub.text =
//                                "${mActivity.getString(R.string._7_days)}" +
//                                        "" + " " + "${mActivity.getString(R.string.FREE_trial_new_new)}" +
//                                        "" + " ${mActivity.getString(R.string.than_price_ex)} " + "${
//                                    packagerenlist?.get(
//                                        1
//                                    )?.price
//                                }" +
//                                        "" + "${mActivity.getString(R.string.year_static)}"
//                        } catch (E: Exception) {
//                        }
                    }
                    mCLUnlockLayout.performClick()
                } else {
                    isbilingopen = false
                }
            }


            txtPrivacy.setOnClickListener {
                val url = Constants.mPrivacyPolicyURL
                val customIntent = CustomTabsIntent.Builder()
                customIntent.setToolbarColor(ContextCompat.getColor(mActivity, R.color.white))
//                openCustomTab(mActivity, customIntent.build(), Uri.parse(url))
                it.context.openUrlInCustomTabOrFallback("https://privacypolicy.kriadl.com/sms-messages-and-text-messaging/privacy-policy")
            }
            txtTerms.setOnClickListener {
                Constants.isActivitychange = true
//                mActivity.startActivity(Intent(mActivity, TermsActivity::class.java))
                it.context.openUrlInCustomTabOrFallback("https://privacypolicy.kriadl.com/sms-messages-and-text-messaging/terms-of-service")
            }

            mCLUnlockLayout.setOnClickListener {
                Log.d("", "initListener: mCLUnlockLayout <-------------> 1")
                if (mActivity.isOnline) {
                    when (plans) {
                        BASIC_SKU -> {

                            if (mIsRevenuCat!!) {
                                Log.d("jigar", "initListener: sku <--------> 1 ")
                                if (Constants.testpackagerenlist.isNotEmpty()) {
                                    Log.d("jigar", "initListener: sku <--------> 2 ")
                                    val mlist =
                                        Constants.testpackagerenlist.filter { p: Package -> p.product.period?.iso8601 == "P1M" }
                                    Purchases.sharedInstance.purchasePackage(mActivity,
                                        mlist[0], object : PurchaseCallback {
                                            override fun onCompleted(
                                                storeTransaction: StoreTransaction,
                                                customerInfo: CustomerInfo,
                                            ) {
                                                subPerchechComplete?.onCompletedPurchase()
                                                mActivity.configADS.SubBuyADS = "month"
                                                BaseSharedPreferences(mActivity).mIS_SUBSCRIBED = true
//                                                registerAdsConfigSubscribe(true)
                                                isSelecterdPlan.monBackPress()
                                                subpurchese?.invoke()
                                            }

                                            override fun onError(
                                                error: PurchasesError,
                                                userCancelled: Boolean,
                                            ) {

                                            }
                                        })
                                } else {
                                    Log.d("jigar", "initListener: sku <--------> 6")
                                    Toast.makeText(
                                        mActivity,
                                        mActivity.getString(R.string.some_time_after_try_again),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                subPerchechComplete?.onCompletedPurchase()
                                mActivity.configADS.SubBuyADS = "month"
                                isSelecterdPlan.monMonthPlan()
                            }


//                        isSelecterdPlan.monMonthPlan()
                        }


                        PREMIUM_SIX_SKU -> {

                            if (mIsRevenuCat!!) {
                                Log.d("jigar", "initListener: sku <--------> 3 ")
                                if (Constants.testpackagerenlist.isNotEmpty()) {
                                    Log.d("jigar", "initListener: sku <--------> 4 ")
                                    val mlist =
                                        Constants.testpackagerenlist.filter { p: Package ->
                                            p.product.period?.iso8601?.lowercase() == "p1w"
                                        }

                                    testpackagerenlist.forEachIndexed { index, p ->
                                        Log.d(
                                            "TAG",
                                            "initListener345: ${mlist.size} - ${index} <--> ${p.product.period?.iso8601}"
                                        )
                                    }


                                    Purchases.sharedInstance.purchasePackage(mActivity,
                                        mlist[0], object : PurchaseCallback {
                                            override fun onCompleted(
                                                storeTransaction: StoreTransaction,
                                                customerInfo: CustomerInfo,
                                            ) {
                                                subPerchechComplete?.onCompletedPurchase()
                                                mActivity.configADS.SubBuyADS = "week"
                                                BaseSharedPreferences(mActivity).mIS_SUBSCRIBED = true
//                                                registerAdsConfigSubscribe(true)
                                                mActivity.onBackPressed()
                                                subpurchese?.invoke()
//                                        isSelecterdPlan.monBackPress()
                                            }

                                            override fun onError(
                                                error: PurchasesError,
                                                userCancelled: Boolean,
                                            ) {

                                            }
                                        })
                                } else {
                                    Log.d("jigar", "initListener: sku <--------> 5")
                                    Toast.makeText(
                                        mActivity,
                                        mActivity.getString(R.string.some_time_after_try_again),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                subPerchechComplete?.onCompletedPurchase()
                                mActivity.configADS.SubBuyADS = "week"
                                isSelecterdPlan.monWeekPlan()
                            }


//                        isSelecterdPlan.monYearPlan()
                        }

                        PREMIUM_SKU -> {

                            if (mIsRevenuCat!!) {
                                Log.d("jigar", "initListener: sku <--------> 3 ")
                                if (Constants.testpackagerenlist.isNotEmpty()) {
                                    Log.d("jigar", "initListener: sku <--------> 4 ")
                                    val mlist =
                                        Constants.testpackagerenlist.filter { p: Package ->
                                            p.product.period?.iso8601?.lowercase() == "p1y"
                                        }

                                    testpackagerenlist.forEachIndexed { index, p ->
                                        Log.d(
                                            "TAG",
                                            "initListener345: ${mlist.size} - ${index} <--> ${p.product.period?.iso8601}"
                                        )
                                    }


                                    Purchases.sharedInstance.purchasePackage(mActivity,
                                        mlist[0], object : PurchaseCallback {
                                            override fun onCompleted(
                                                storeTransaction: StoreTransaction,
                                                customerInfo: CustomerInfo,
                                            ) {
                                                subPerchechComplete?.onCompletedPurchase()
                                                mActivity.configADS.SubBuyADS = "year"
                                                BaseSharedPreferences(mActivity).mIS_SUBSCRIBED = true
//                                                registerAdsConfigSubscribe(true)
                                                mActivity.onBackPressed()
                                                subpurchese?.invoke()
//                                        isSelecterdPlan.monBackPress()
                                            }

                                            override fun onError(
                                                error: PurchasesError,
                                                userCancelled: Boolean,
                                            ) {

                                            }
                                        })
                                } else {
                                    Log.d("jigar", "initListener: sku <--------> 5")
                                    Toast.makeText(
                                        mActivity,
                                        mActivity.getString(R.string.some_time_after_try_again),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                subPerchechComplete?.onCompletedPurchase()
                                mActivity.configADS.SubBuyADS = "year"
                                isSelecterdPlan.monYearPlan()
                            }


//                        isSelecterdPlan.monYearPlan()
                        }

                        PREMIUM_LIFTIME -> {
                            if (mIsRevenuCat!!) {
                                Log.d("jigar", "initListener: sku <--------> 3 ")
                                if (Constants.testpackagerenlist.isNotEmpty()) {
                                    Log.d("jigar", "initListener: sku <--------> 4 ")
                                    val mlist =
                                        Constants.testpackagerenlist.filter { p: Package ->
                                            p.packageType == PackageType.LIFETIME
                                        }

                                    testpackagerenlist.forEachIndexed { index, p ->
                                        Log.d(
                                            "TAG",
                                            "initListener345: ${mlist.size} - ${index} <--> ${p.product.period?.iso8601}"
                                        )
                                    }


                                    Purchases.sharedInstance.purchasePackage(mActivity,
                                        mlist[0], object : PurchaseCallback {
                                            override fun onCompleted(
                                                storeTransaction: StoreTransaction,
                                                customerInfo: CustomerInfo,
                                            ) {
                                                subPerchechComplete?.onCompletedPurchase()
                                                mActivity.configADS.SubBuyADS = "life"
                                                BaseSharedPreferences(mActivity).mIS_SUBSCRIBED = true
//                                                registerAdsConfigSubscribe(true)
                                                mActivity.onBackPressed()
                                                subpurchese?.invoke()
//                                        isSelecterdPlan.monBackPress()
                                            }

                                            override fun onError(
                                                error: PurchasesError,
                                                userCancelled: Boolean,
                                            ) {

                                            }
                                        })
                                } else {
                                    Log.d("jigar", "initListener: sku <--------> 5")
                                    Toast.makeText(
                                        mActivity,
                                        mActivity.getString(R.string.some_time_after_try_again),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }

                    }
                } else {
                    mActivity.showToast(
                        mActivity.getString(R.string.please_check_connection),
                        android.widget.Toast.LENGTH_SHORT
                    )
                }
            }

//            mCLYearLayout.performClick()
        }
    }

//    fun openCustomTab(activity: Activity, customTabsIntent: CustomTabsIntent, uri: Uri?) {
//        val packageName = "com.android.chrome"
//        customTabsIntent.intent.setPackage(packageName)
//        customTabsIntent.launchUrl(activity, uri!!)
//    }

    fun openCustomTab(activity: Activity, customTabsIntent: CustomTabsIntent, uri: Uri?) {
        val packageName = "com.android.chrome"
        customTabsIntent.intent.setPackage(packageName)
        if (isChromeCustomTabsSupported(activity, customTabsIntent.intent)) {
            customTabsIntent.launchUrl(activity, uri!!)
        } else {
            activity.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }

    private fun isChromeCustomTabsSupported(context: Context, intent: Intent): Boolean {
        val activities =
            context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return activities.isNotEmpty()
    }

    fun calculateDiscountPercentage(originalPrice: Double, discountedPrice: Double): Int {
        val discountAmount = originalPrice - discountedPrice
        val discountPercentage = (discountAmount / originalPrice) * 100
        return discountPercentage.roundToInt()
    }


    fun getDiscount(weekPlan: Package, product: Package): Triple<Int, Int, String> {
        var price = weekPlan?.product?.price?.amountMicros?.div(1000000) ?: 120
        var pair = Triple(0, 0, "")
        product?.let {
            when (it.packageType) {
                PackageType.MONTHLY -> {
                    pair = getPair(4.3f, price, product, "P1M")

                }

                PackageType.SIX_MONTH -> {
                    pair = getPair(25.8f, price, product, "P6M")
                }

                PackageType.ANNUAL -> {
                    pair = getPair(52f, price, product, "P1Y")
                }

                else -> {

                }
            }

        }
        return pair
//    return pair
    }

    private fun getPair(
        time: Float,
        price: Long,
        product: Package,
        DUR: String,
    ): Triple<Int, Int, String> {
        var highPrice = (price.times(time.roundToInt()))// for round figure the duration
        var productPrice: Int = (product?.product?.price?.amountMicros?.div(1000000) ?: 120).toInt()
        var less = highPrice.minus(productPrice)
        var discount = if (highPrice != 0L) {
            (less?.times(100))?.div(highPrice) ?: 0
        } else {
            0
        }
        Log.d("TAG", "getPair235: $productPrice - $less   ")

        if (highPrice < productPrice) {
            // reValues if not give discount
            highPrice = productPrice.toLong()
            discount = 0
        }
        var perDur = ""
        if (DUR == "P1M") {
            perDur = "${(productPrice / time.roundToInt()).formatPrice()}/Week"
        } else if (DUR == "P6M") {
            perDur = "${(productPrice / time.roundToInt()).formatPrice()}/Week"
        } else if (DUR == "P1Y") {
            perDur = "${(productPrice / time.roundToInt()).formatPrice()}/Week"
        }

        return Triple(highPrice.toInt(), discount.toInt(), perDur)
//    return Pair()
    }

    private fun Int.formatPrice(): String {
        val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
        return "₹${formatter.format(this)}"
    }

    fun convertYearlyToWeekly(yearlyPrice: Double): Double {
        val weeksInYear = 52
        return yearlyPrice / weeksInYear
    }

    fun calculateDiscount(weeklyPrice: Double, yearlyPrice: Double): Double {
        val totalWeeklyCost = weeklyPrice * 52
        val discountAmount = totalWeeklyCost - yearlyPrice
        val discountPercentage = (discountAmount / totalWeeklyCost) * 100
        return discountPercentage
    }

    fun calculateMonthlyDiscountFixedWeeks(
        weeklyPrice: Double,
        monthlyPrice: Double
    ): Pair<Double, Double> {
        val weeksPerMonth = 4.0
        val weeklyCostPerMonth = weeklyPrice * weeksPerMonth
        val discountAmount = weeklyCostPerMonth - monthlyPrice
        val discountPercentage = (discountAmount / weeklyCostPerMonth) * 100
        return Pair(discountAmount, discountPercentage)
    }

}
