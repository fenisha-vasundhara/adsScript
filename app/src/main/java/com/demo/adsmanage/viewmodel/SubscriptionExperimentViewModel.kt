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
import com.demo.adsmanage.Activity.TermsActivity
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

import com.demo.adsmanage.SubscriptionBaseClass.manager.PreferencesKeys
import com.demo.adsmanage.SubscriptionBaseClass.manager.SubscriptionManager
import com.demo.adsmanage.basemodule.BaseSharedPreferences
import com.demo.adsmanage.billing.ProductPurchaseHelper.getProductInfo

import com.demo.adsmanage.helper.IconPosition
import com.demo.adsmanage.helper.click
import com.demo.adsmanage.helper.getMonthBaseYearlyDiscount
import com.demo.adsmanage.helper.getSubTrial
import com.demo.adsmanage.helper.gone
import com.demo.adsmanage.helper.isOnline
import com.demo.adsmanage.helper.logD
import com.demo.adsmanage.helper.setCloseIconPosition
import com.demo.adsmanage.helper.showToast
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityExperimentSubScreenBinding
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PackageType
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.interfaces.PurchaseCallback
import com.revenuecat.purchases.models.StoreTransaction

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt


class SubscriptionExperimentViewModel(
    var binding: ActivityExperimentSubScreenBinding,
    var mActivity: AppCompatActivity,
    var liveDataPeriod: MutableLiveData<HashMap<String, String>>,
    var liveDataPrice: MutableLiveData<HashMap<String, String>>,
    var liveDataPriceMicro: MutableLiveData<HashMap<String, Long>>,
    var subscriptionManager: SubscriptionManager,
    var isSelecterdPlan: IsSelecterdPlan,

    ) : ViewModel() {
    @SuppressLint("AnnotateVersionCheck")
    fun isPiePlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    val idname = arrayOf("_one", "_two", "_three", "_four", "_five", "_six", "_seven", "_eight")

    init {
        onMain()
    }

    interface IsSelecterdPlan {
        fun monMonthPlan()
        fun monYearPlan()
        fun monWeekPlan()
        fun monBackPress()
    }

    fun onMain() {

        setSubScriptionUI()
        setUI()
        setLineView()
        initListener()
    }

    @SuppressLint("SetTextI18n")
    fun setSubScriptionUI() {
        with(binding) {
            val unlockTextColor = SUBButtonTextColor ?: R.color.black
            txtUnlockKriadl.setTextColor(ContextCompat.getColor(mActivity, unlockTextColor))
            val monthlyPackage = packagerenlist.getOrNull(0)
            val yearlyPackage = packagerenlist.getOrNull(1)
            if (mIsRevenuCat == true) {
                if (packagerenlist.isNotEmpty()) {

                    if (monthlyPackage?.freeTrialPeriod == null) {
                        txtUnlockKriadl.text = mActivity.getString(R.string.continue_txt)
                    } else {
                        val trialMessage = mActivity.getString(
                            R.string.enjoy_month_trial,
                            mActivity.getSubTrial(monthlyPackage.freeTrialPeriod.iso8601)
                        )
                        txtUnlockKriadl.text = mActivity.getString(R.string.continue_txt)
                    }

                    yearlyPackage?.price?.let { yearlyPrice ->
                        monthlyPackage?.price?.let { monthlyPrice ->
                            getMonthBaseYearlyDiscount(
                                monthlyPrice, yearlyPrice
                            ) { yearlyDiscountPercentage, yearlyMonthBaseDiscountPrice ->

                            }
                        }
                    }

                    packagerenlist?.forEachIndexed { index, packagesRen ->
                        val price = packagesRen.originalPrice
                        val pricemic = packagesRen.price


                        if (packagesRen.sku.contains("monthly")) {
                            txtMonthlyPrice.text = price.replace(".00", "")
                            try {
                                    textView10.paintFlags =
                                        textView10.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
//                                    monthstaticprice.text = "${price?.first()}${price?.substring(1)?.replace(",", "")?.toDouble()?.div(4)?.roundToInt()}${"/"}"
                                    monthstaticprice.text = "${price.first()}${
                                        pricemic.toDouble().div(4).roundToInt()
                                    }/"

                                    Log.d(
                                        "txtweekPrice",
                                        "setSubScriptionUI:txtweekPrice <--------> ${packagesRen}"
                                    )
                                } catch (e: Exception) {
                                }
                        }
                        else if (packagesRen.sku.contains("yearly")) {
                            txtYearlyPrice.text = price.replace(".00", "")
                            try {
//                                    yearstaticprice.text = "${price?.first()}${price?.substring(1)?.replace(",", "")?.toDouble()?.div(52)?.roundToInt()}${"/"}"
                                yearstaticprice.text = "${price.first()}${
                                    pricemic.toDouble().div(52).roundToInt()
                                }/"
                                Log.d(
                                    "",
                                    "setSubScriptionUI: txtYearlyPrice <--------> 1 ${price}"
                                )
                            } catch (e: Exception) {

                            }
                        }else if (packagesRen.sku.contains("lifetime")) {
                            txtlifePrice.text = price.replace(".00", "")
                        } else {
                            txtweekPrice.text = price.replace(".00", "")
                            try {
//                                    textView10.text = "${price?.first()}${price?.substring(1)?.replace(",", "")?.toDouble()?.times(52)?.roundToInt()}"
//                                    textView9.text = "${price?.first()}${price?.substring(1)?.replace(",", "")?.toDouble()?.times(4)?.roundToInt()}"

                                textView10.text = "${price.first()}${
                                    pricemic.toDouble().times(52).roundToInt()
                                }"
                                textView9.text = "${price.first()}${
                                    pricemic.toDouble().times(4).roundToInt()
                                }"

                                textView9.paintFlags =
                                    textView9.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                                Log.d(
                                    "txtweekPrice",
                                    "setSubScriptionUI:txtweekPrice <--------> ${packagesRen}"
                                )
                            } catch (e: Exception) {
                            }
                        }

                    /*    when (index) {
                            0 -> {
                                txtMonthlyPrice.text = price.replace(".00", "")
                                try {
                                    textView10.paintFlags =
                                        textView10.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
//                                    monthstaticprice.text = "${price?.first()}${price?.substring(1)?.replace(",", "")?.toDouble()?.div(4)?.roundToInt()}${"/"}"
                                    monthstaticprice.text = "${price.first()}${
                                        pricemic.toDouble().div(4).roundToInt()
                                    }/"

                                    Log.d(
                                        "txtweekPrice",
                                        "setSubScriptionUI:txtweekPrice <--------> ${packagesRen}"
                                    )
                                } catch (e: Exception) {
                                }
                            }

                            1 -> {
                                txtYearlyPrice.text = price.replace(".00", "")
                                try {
//                                    yearstaticprice.text = "${price?.first()}${price?.substring(1)?.replace(",", "")?.toDouble()?.div(52)?.roundToInt()}${"/"}"
                                    yearstaticprice.text = "${price.first()}${
                                        pricemic.toDouble().div(52).roundToInt()
                                    }/"
                                    Log.d(
                                        "",
                                        "setSubScriptionUI: txtYearlyPrice <--------> 1 ${price}"
                                    )
                                } catch (e: Exception) {

                                }
                            }

                            2 -> {
                                txtweekPrice.text = price.replace(".00", "")
                                try {
//                                    textView10.text = "${price?.first()}${price?.substring(1)?.replace(",", "")?.toDouble()?.times(52)?.roundToInt()}"
//                                    textView9.text = "${price?.first()}${price?.substring(1)?.replace(",", "")?.toDouble()?.times(4)?.roundToInt()}"

                                    textView10.text = "${price.first()}${
                                        pricemic.toDouble().times(52).roundToInt()
                                    }"
                                    textView9.text = "${price.first()}${
                                        pricemic.toDouble().times(4).roundToInt()
                                    }"

                                    textView9.paintFlags =
                                        textView9.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                                    Log.d(
                                        "txtweekPrice",
                                        "setSubScriptionUI:txtweekPrice <--------> ${packagesRen}"
                                    )
                                } catch (e: Exception) {
                                }
                            }

                            3 -> {
//                                txtlifePrice.text = "${price?.first()}${price?.substring(1)?.replace(",", "")?.toDouble()?.roundToInt().toString()}"
                                txtlifePrice.text = price.replace(".00", "")
                            }
                        }*/
                    }

//                    txtMonthDiscount.text = "${"48"}% ${"OFF"}"
//
//
//                    txtYearDiscount.text = "${"80"}% ${"OFF"}"

                    if (testpackagerenlist.isNotEmpty()) {
                        val month =
                            testpackagerenlist.find { it.packageType == PackageType.MONTHLY }
                        val year =
                            testpackagerenlist.find { it.packageType == PackageType.ANNUAL }
                        val week =
                            testpackagerenlist.find { it.packageType == PackageType.WEEKLY }

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

                        txtMonthDiscount.text = "${monthly?.second}% ${"OFF"}"

                        txtYearDiscount.text = "${yearly?.second}% ${"OFF"}"
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
//                                    txtMonthBottom.text = "" + mActivity.resources.getText(R.string.Enjoy) + "" + " ${
//                                        getSubTrial(
//                                            subscriptionManager.getString(PreferencesKeys.MONTH_TRIAL_PERIOD, "")
//                                        )
//                                    } " + mActivity.resources.getText(R.string.FREE_trial_new)
                                }
                            }
                        }
                        txtYearlyPrice.text = "${
                            price[PREMIUM_SKU]?.let {
                                it
                            }
                        }"
                        Log.d("jigar", "setSubScriptionUI:<------- jigar ----------> 2 ${
                            price[PREMIUM_SKU]?.let {
                                it
                            }
                        }")
                        txtMonthlyPrice.text = "${
                            price[PREMIUM_SIX_SKU]?.let {
                                it
                            }
                        }"





                    }

                }
                liveDataPriceMicro.observe(mActivity) {
                    val yearprice = ((it[Constants.PREMIUM_SKU]!! / 1000000)).toDouble()
                    val monthprice = ((it[Constants.BASIC_SKU]!! / 1000000)).toDouble()
                    val monthcount = (monthprice * 12)
                    val cal = (yearprice as Double / monthcount as Double * 100).toInt()
                    Log.d("TAG", "setSubScriptionUI: $cal--$yearprice--$monthprice--$monthcount")
                    val perfind = 100 - cal
//                    persenttxt.text = "Save\n$perfind%"

                }
            }


        }
    }

    @SuppressLint("NewApi", "SetTextI18n")
    fun setUI() {
        with(binding) {
//            yearDiscountShow.setCardBackgroundColor(yearDiscountShow.context.resources.getColor(R.color.selected_color))
//            txtYearDiscount.textColor = yearDiscountShow.context.resources.getColor(R.color.white)
//
//            monthDiscountShow.setCardBackgroundColor(yearDiscountShow.context.resources.getColor(R.color.unselected_color))
//            txtMonthDiscount.textColor = yearDiscountShow.context.resources.getColor(R.color.black)
//
//            yearconstraintid.setBackgroundResource(R.drawable.bgyear)
//            monthconstraintid.setBackgroundResource(R.drawable.bgyearopacity)
//            weekconstraintid.setBackgroundResource(R.drawable.bgyearopacity)
//            lifeconstraintid.setBackgroundResource(R.drawable.bgyearopacity)
//
//            mIVMonthSelection.setImageResource(R.drawable.uncheckicon)
//            mIVweekSelection.setImageResource(R.drawable.uncheckicon)
//            mIVlifeSelection.setImageResource(R.drawable.uncheckicon)
//            mIVYearSelection.setImageResource(R.drawable.mycheckiconsvg)
//            if (mIsRevenuCat!!) {
//                if (packagerenlist.isNotEmpty()) {
//                    if (packagerenlist!![1].freeTrialPeriod == null) {
//                        txtUnlockKriadl.text = mActivity.getString(R.string.continue_txt)
//                    } else {
//                        txtUnlockKriadl.text = mActivity.getString(R.string.start_free_trial)
//                        val featureDescription = mActivity.getString(
//                            R.string.feature_description,
//                            packagerenlist?.get(1)?.price,
//                            mActivity.getSubTrial(packagerenlist!![1].freeTrialPeriod!!.iso8601)
//                        )
////                        txtFeature.text = featureDescription
//                    }
//                }
//            } else {
//                PREMIUM_SKU.getProductInfo?.let { year ->
//                    if (year.freeTrialPeriod.equals("Not Found", true)) {
//                        txtUnlockKriadl.text = mActivity.getString(R.string.continue_txt)
//                    } else {
//                        txtUnlockKriadl.text = mActivity.getString(R.string.start_free_trial)
//                    }
//                }
//            }
//            plans = PREMIUM_SKU
//
//            if (packagerenlist!![1].freeTrialPeriod == null) {
//                textsub.text = mActivity.getString(
//                    R.string.year_price,
//                    packagerenlist?.get(1)?.price
//                )
//            } else {
//                textsub.text = mActivity.getString(
//                    R.string.price_with_trial_yearly,
//                    packagerenlist?.get(1)?.price,
//                    mActivity.getSubTrial(
//                        packagerenlist[1].freeTrialPeriod!!.iso8601
//                    )
//                )
//            }

            yearDiscountShow.setCardBackgroundColor(yearDiscountShow.context.resources.getColor(R.color.unselected_color))
            txtYearDiscount.setTextColor( yearDiscountShow.context.resources.getColor(R.color.black))

            monthDiscountShow.setCardBackgroundColor(yearDiscountShow.context.resources.getColor(R.color.selected_color))
            txtMonthDiscount.setTextColor( yearDiscountShow.context.resources.getColor(R.color.white))

            weekDiscountShow.setCardBackgroundColor(yearDiscountShow.context.resources.getColor(R.color.unselected_color))
            txtWeekDiscount.setTextColor( yearDiscountShow.context.resources.getColor(R.color.black))


            Log.d("", "initListener: mCLUnlockLayout <-------------> 3")
//                mIVYearSelection.background = mPremium_Cardunselected_Icon
//                mIVMonthSelection.background = mPremium_CardSelected_Icon
            mIVYearSelection.setImageResource(R.drawable.uncheckicon)
            mIVweekSelection.setImageResource(R.drawable.uncheckicon)
            mIVlifeSelection.setImageResource(R.drawable.uncheckicon)
            mIVMonthSelection.setImageResource(R.drawable.mycheckiconsvg)

            yearconstraintid.setBackgroundResource(R.drawable.bgyearopacity)
            weekconstraintid.setBackgroundResource(R.drawable.bgyearopacity)
            lifeconstraintid.setBackgroundResource(R.drawable.bgyearopacity)
            monthconstraintid.setBackgroundResource(R.drawable.bgyear)

            val monthlyPackage = packagerenlist.getOrNull(0)
            if (mIsRevenuCat == true) {
                if (packagerenlist.isNotEmpty()) {
                    if (monthlyPackage?.freeTrialPeriod == null) {
                        txtUnlockKriadl.text = mActivity.getString(R.string.continue_txt)
                    } else {
                        txtUnlockKriadl.text = mActivity.getString(R.string.continue_txt)

//                        val featureDescription = mActivity.getString(
//                            R.string.feature_description,
//                            packagerenlist?.get(0)?.price,
//                            mActivity.getSubTrial(packagerenlist!![0].freeTrialPeriod!!.iso8601)
//                        )
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
            plans = BASIC_SKU

            if (monthlyPackage != null) {
                if (monthlyPackage.freeTrialPeriod == null) {
//                textsub.text = mActivity.getString(
//                    R.string.monthly_price,
//                    packagerenlist?.get(0)?.price
//                )
                    if (monthlyPackage.price.isNotEmpty()) {
                        try {
//                            textsub.text =   txtMonthlyPrice.text

                            textsub.text = mActivity.getString(
                                R.string.monthly_price,
                                "${txtMonthlyPrice.text}"
                            )
//                            textsub.text = mActivity.getString(
//                                R.string.monthly_price,
//                                "${packagerenlist?.get(0)?.price?.first()}${
//                                    packagerenlist?.get(0)?.price?.substring(1)
//                                        ?.replace(",", "")?.toDouble()?.roundToInt()
//                                }"

//                            textsub.text = mActivity.getString(
//                                R.string.monthly_price,
//                                "${packagerenlist?.get(0)?.price?.first()}${
//                                    packagerenlist?.get(0)?.price?.substring(1)
//                                        ?.replace(",", "")?.toDouble()?.roundToInt()
//                                }"
//                            )

                        }catch (e:Exception){

                        }
                    }
                }
                else {
//                textsub.text = mActivity.getString(
//                    R.string.price_with_trial_monthly,
//                    packagerenlist?.get(0)?.price,
//                    mActivity.getSubTrial(
//                        packagerenlist[0].freeTrialPeriod!!.iso8601
//                    )
//                )
                    textsub.text =
                        "${mActivity.getString(R.string._7_days)}" +
                                "" + " " + "${mActivity.getString(R.string.FREE_trial_new_new)}" +
                                "" + " ${mActivity.getString(R.string.than_price_ex)} " + "${
                            monthlyPackage.price
                        }" +
                                "" + "${mActivity.getString(R.string.month_static)}"
                }
            }
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun setLineView() {
        with(binding) {
//            for (i in 0..7) {
//                logD(
//                    "mPremiumScreenLine",
//                    "----$i--${mPremiumScreenLine!!.size}--${idname[i]}"
//                )
//                if (mPremiumScreenLine!!.size <= i) {
//                    val id_name = "txt${idname[i]}"
//                    val redId =
//                        mActivity.resources.getIdentifier(id_name, "id", mActivity.packageName)
//                    val txt: TextView = mActivity.findViewById(redId)
//                    txt.visibility = View.GONE
//
//                    val id_name1 = "img_true${idname[i]}"
//                    val redId1 =
//                        mActivity.resources.getIdentifier(id_name1, "id", mActivity.packageName)
//                    val img_true: ImageView = mActivity.findViewById(redId1)
//                    img_true.visibility = View.GONE
//
//                } else {
//                    val id_name = "txt${idname[i]}"
//                    val redId =
//                        mActivity.resources.getIdentifier(id_name, "id", mActivity.packageName)
//                    val txt: TextView = mActivity.findViewById(redId)
//                    txt.visibility = View.VISIBLE
////                txt.text = mPremiumScreenLine!![i].mLine
//                    txt.textColor = mActivity.resources.getColor(mPremiumScreenLine!![i].mLineColor)
//
//                    val id_name1 = "img_true${idname[i]}"
//                    val redId1 =
//                        mActivity.resources.getIdentifier(id_name1, "id", mActivity.packageName)
//                    val img_true: ImageView = mActivity.findViewById(redId1)
//                    img_true.visibility = View.VISIBLE
//                    img_true.setImageDrawable(mPremiumScreenLine!![i].mIconLine)
//                }
//
//            }
        }
    }

    @SuppressLint("StringFormatInvalid", "StringFormatMatches")
    private fun initListener() {
        with(binding) {
//            binding.imgClose.setOnClickListener {
//                isSelecterdPlan.monBackPress()
//            }

            mCLLIFELayout.setOnClickListener {
                if (isbilingopen) {
                    return@setOnClickListener
                }
                isbilingopen = true

                yearDiscountShow.setCardBackgroundColor(
                    yearDiscountShow.context.resources.getColor(
                        R.color.unselected_color
                    )
                )
                txtYearDiscount.setTextColor(
                    yearDiscountShow.context.resources.getColor(R.color.black))

                monthDiscountShow.setCardBackgroundColor(
                    yearDiscountShow.context.resources.getColor(
                        R.color.unselected_color
                    )
                )
                txtMonthDiscount.setTextColor(
                    yearDiscountShow.context.resources.getColor(R.color.black))

                weekDiscountShow.setCardBackgroundColor(
                    yearDiscountShow.context.resources.getColor(
                        R.color.unselected_color
                    )
                )
                txtWeekDiscount.setTextColor(
                    yearDiscountShow.context.resources.getColor(R.color.black))

                mIVMonthSelection.setImageResource(R.drawable.uncheckicon)
                mIVYearSelection.setImageResource(R.drawable.uncheckicon)
                mIVweekSelection.setImageResource(R.drawable.uncheckicon)
                mIVlifeSelection.setImageResource(R.drawable.mycheckiconsvg)

                yearconstraintid.setBackgroundResource(R.drawable.bgyearopacity)
                monthconstraintid.setBackgroundResource(R.drawable.bgyearopacity)
                weekconstraintid.setBackgroundResource(R.drawable.bgyearopacity)
                lifeconstraintid.setBackgroundResource(R.drawable.bgyear)


                if (mIsRevenuCat!!) {
                    if (packagerenlist.isNotEmpty()) {
                        if (packagerenlist!![3].freeTrialPeriod == null) {
                            txtUnlockKriadl.text = mActivity.getString(R.string.continue_txt)
                        } else {
                            txtUnlockKriadl.text = mActivity.getString(R.string.continue_txt)
                            val featureDescription = mActivity.getString(
                                R.string.feature_description,
                                packagerenlist?.get(2)?.price,
                                mActivity.getSubTrial(packagerenlist!![3].freeTrialPeriod!!.iso8601)
                            )
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
//                    textsub.text = mActivity.getString(
//                        R.string.lifetime_price,
//                        packagerenlist?.get(3)?.price
//                    )
                        textsub.text = mActivity.getString(
                            R.string.lifetime_price,
                            "${packagerenlist?.get(3)?.price?.first()}${
                                packagerenlist?.get(3)?.price?.substring(1)
                                    ?.replace(",", "")?.toDouble()?.roundToInt()
                            }"
                        )
                    } else {
//                    textsub.text = mActivity.getString(
//                        R.string.price_with_trial_lifetime,
//                        packagerenlist?.get(3)?.price,
//                        mActivity.getSubTrial(
//                            packagerenlist[3].freeTrialPeriod!!.iso8601
//                        )
//                    )
                        textsub.text =
                            "${mActivity.getString(R.string._7_days)}" +
                                    "" + " " + "${mActivity.getString(R.string.FREE_trial_new_new)}" +
                                    "" + " ${mActivity.getString(R.string.than_price_ex)} " + "${
                                packagerenlist?.get(
                                    3
                                )?.price
                            }" +
                                    "" + "${mActivity.getString(R.string.year_static)}"
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

                yearDiscountShow.setCardBackgroundColor(
                    yearDiscountShow.context.resources.getColor(
                        R.color.unselected_color
                    )
                )
                txtYearDiscount.setTextColor(
                    yearDiscountShow.context.resources.getColor(R.color.black))

                monthDiscountShow.setCardBackgroundColor(
                    yearDiscountShow.context.resources.getColor(
                        R.color.unselected_color
                    )
                )
                txtMonthDiscount.setTextColor(
                            yearDiscountShow.context.resources.getColor(R.color.black))

                weekDiscountShow.setCardBackgroundColor(
                    yearDiscountShow.context.resources.getColor(
                        R.color.selected_color
                    ))

                txtWeekDiscount.setTextColor(
                                    yearDiscountShow.context.resources.getColor(R.color.white))

                mIVMonthSelection.setImageResource(R.drawable.uncheckicon)
                mIVYearSelection.setImageResource(R.drawable.uncheckicon)
                mIVlifeSelection.setImageResource(R.drawable.uncheckicon)
                mIVweekSelection.setImageResource(R.drawable.mycheckiconsvg)

                yearconstraintid.setBackgroundResource(R.drawable.bgyearopacity)
                monthconstraintid.setBackgroundResource(R.drawable.bgyearopacity)
                lifeconstraintid.setBackgroundResource(R.drawable.bgyearopacity)
                weekconstraintid.setBackgroundResource(R.drawable.bgyear)


                if (mIsRevenuCat!!) {
                    if (packagerenlist.isNotEmpty()) {
                        if (packagerenlist!![2].freeTrialPeriod == null) {
                            txtUnlockKriadl.text = mActivity.getString(R.string.continue_txt)
                        } else {
                            txtUnlockKriadl.text = mActivity.getString(R.string.continue_txt)
                            val featureDescription = mActivity.getString(
                                R.string.feature_description,
                                packagerenlist?.get(2)?.price,
                                mActivity.getSubTrial(packagerenlist!![2].freeTrialPeriod!!.iso8601)
                            )
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

                if (packagerenlist.isNotEmpty()) {
                    if (packagerenlist!![2].freeTrialPeriod == null) {
//                    textsub.text = mActivity.getString(
//                        R.string.week_price,
//                        packagerenlist?.get(2)?.price
//                    )

                        if (packagerenlist.isNotEmpty()) {

                            textsub.text = mActivity.getString(
                                R.string.week_price,
                                "${txtweekPrice.text}"
                            )
                         /*   textsub.text = mActivity.getString(
                                R.string.week_price,
                                "${packagerenlist?.get(2)?.price?.first()}${
                                    packagerenlist?.get(2)?.price?.substring(1)
                                        ?.replace(",", "")?.toDouble()?.roundToInt()
                                }"
                            )*/
                        }

                    } else {
//                    textsub.text = mActivity.getString(
//                        R.string.price_with_trial_weekly,
//                        packagerenlist?.get(2)?.price,
//                        mActivity.getSubTrial(
//                            packagerenlist[2].freeTrialPeriod!!.iso8601
//                        )
//                    )
                        if (packagerenlist.isNotEmpty()) {
                            textsub.text =
                                "${mActivity.getString(R.string._7_days)}" +
                                        "" + " " + "${mActivity.getString(R.string.FREE_trial_new_new)}" +
                                        "" + " ${mActivity.getString(R.string.than_price_ex)} " + "${
                                    packagerenlist?.get(
                                        2
                                    )?.price
                                }" +
                                        "" + "${mActivity.getString(R.string.week_static)}"
                        }
                    }

                    Log.d("PREMIUM_SIX_SKU", "initListener: PREMIUM_SIX_SKU <----> 1 ${plans}")
                    plans = PREMIUM_SIX_SKU
                    Log.d("PREMIUM_SIX_SKU", "initListener: PREMIUM_SIX_SKU <----> 2 ${plans}")
                    mCLUnlockLayout.performClick()
                } else {
                    isbilingopen = false
                }
            }


            mCLMonthLayout.setOnClickListener {

                if (isbilingopen) {
                    return@setOnClickListener
                }
                isbilingopen = true
                yearDiscountShow.setCardBackgroundColor(
                    yearDiscountShow.context.resources.getColor(
                        R.color.unselected_color
                    )
                )
                txtYearDiscount.setTextColor(
                    yearDiscountShow.context.resources.getColor(R.color.black))


                weekDiscountShow.setCardBackgroundColor(
                    yearDiscountShow.context.resources.getColor(
                        R.color.unselected_color
                    )
                )
                txtWeekDiscount.setTextColor(
                    yearDiscountShow.context.resources.getColor(R.color.black))

                monthDiscountShow.setCardBackgroundColor(
                    yearDiscountShow.context.resources.getColor(
                        R.color.selected_color
                    )
                )
                txtMonthDiscount.setTextColor(
                    yearDiscountShow.context.resources.getColor(R.color.white))


                Log.d("", "initListener: mCLUnlockLayout <-------------> 3")
//                mIVYearSelection.background = mPremium_Cardunselected_Icon
//                mIVMonthSelection.background = mPremium_CardSelected_Icon
                mIVYearSelection.setImageResource(R.drawable.uncheckicon)
                mIVweekSelection.setImageResource(R.drawable.uncheckicon)
                mIVlifeSelection.setImageResource(R.drawable.uncheckicon)
                mIVMonthSelection.setImageResource(R.drawable.mycheckiconsvg)

                yearconstraintid.setBackgroundResource(R.drawable.bgyearopacity)
                weekconstraintid.setBackgroundResource(R.drawable.bgyearopacity)
                lifeconstraintid.setBackgroundResource(R.drawable.bgyearopacity)
                monthconstraintid.setBackgroundResource(R.drawable.bgyear)

                if (mIsRevenuCat!!) {
                    if (packagerenlist.isNotEmpty()) {
                        if (packagerenlist!![0].freeTrialPeriod == null) {
                            txtUnlockKriadl.text = mActivity.getString(R.string.continue_txt)
                        } else {
                            txtUnlockKriadl.text = mActivity.getString(R.string.continue_txt)
                            val featureDescription = mActivity.getString(
                                R.string.feature_description,
                                packagerenlist?.get(0)?.price,
                                mActivity.getSubTrial(packagerenlist!![0].freeTrialPeriod!!.iso8601)
                            )
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
                plans = BASIC_SKU

                if (packagerenlist.isNotEmpty()) {
                    if (packagerenlist!![0].freeTrialPeriod == null) {
//                    textsub.text = mActivity.getString(
//                        R.string.monthly_price,
//                        packagerenlist?.get(0)?.price
//                    )
                        textsub.text = mActivity.getString(
                            R.string.monthly_price,
                            "${txtMonthlyPrice.text}"
                        )
                        /*  textsub.text = mActivity.getString(
                            R.string.monthly_price,
                             "${packagerenlist?.get(0)?.price?.first()}${
                                 packagerenlist?.get(0)?.price?.substring(1)
                                     ?.replace(",", "")?.toDouble()?.roundToInt()
                             }"
                         )*/
                    } else {
//                    textsub.text = mActivity.getString(
//                        R.string.price_with_trial_monthly,
//                        packagerenlist?.get(0)?.price,
//                        mActivity.getSubTrial(
//                            packagerenlist[0].freeTrialPeriod!!.iso8601
//                        )
//                    )
                        textsub.text =
                            "${mActivity.getString(R.string._7_days)}" +
                                    "" + " " + "${mActivity.getString(R.string.FREE_trial_new_new)}" +
                                    "" + " ${mActivity.getString(R.string.than_price_ex)} " + "${
                                packagerenlist?.get(
                                    0
                                )?.price
                            }" +
                                    "" + "${mActivity.getString(R.string.month_static)}"
                    }
                    mCLUnlockLayout.performClick()
                } else {
                    isbilingopen = false
                }
            }

            mCLYearLayout.setOnClickListener {
                if (isbilingopen) {
                    return@setOnClickListener
                }
                isbilingopen = true
                Log.d("", "initListener: mCLUnlockLayout <-------------> 2")
//                mIVYearSelection.background = mPremium_CardSelected_Icon
//                mIVMonthSelection.background = mPremium_Cardunselected_Icon

                yearDiscountShow.setCardBackgroundColor(
                    yearDiscountShow.context.resources.getColor(
                        R.color.selected_color
                    )
                )
                txtYearDiscount.setTextColor(
                    yearDiscountShow.context.resources.getColor(R.color.white))

                monthDiscountShow.setCardBackgroundColor(
                    yearDiscountShow.context.resources.getColor(
                        R.color.unselected_color
                    )
                )
                txtMonthDiscount.setTextColor(
                    yearDiscountShow.context.resources.getColor(R.color.black))

                weekDiscountShow.setCardBackgroundColor(
                    yearDiscountShow.context.resources.getColor(
                        R.color.unselected_color
                    )
                )
                txtWeekDiscount.setTextColor(
                    yearDiscountShow.context.resources.getColor(R.color.black))

                yearconstraintid.setBackgroundResource(R.drawable.bgyear)
                monthconstraintid.setBackgroundResource(R.drawable.bgyearopacity)
                weekconstraintid.setBackgroundResource(R.drawable.bgyearopacity)
                lifeconstraintid.setBackgroundResource(R.drawable.bgyearopacity)

                mIVMonthSelection.setImageResource(R.drawable.uncheckicon)
                mIVweekSelection.setImageResource(R.drawable.uncheckicon)
                mIVlifeSelection.setImageResource(R.drawable.uncheckicon)
                mIVYearSelection.setImageResource(R.drawable.mycheckiconsvg)
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
                    PREMIUM_SKU.getProductInfo?.let { year ->
                        if (year.freeTrialPeriod.equals("Not Found", true)) {
                            txtUnlockKriadl.text = mActivity.getString(R.string.continue_txt)
                        } else {
                            txtUnlockKriadl.text = mActivity.getString(R.string.continue_txt)
                        }
                    }
                }
                plans = PREMIUM_SKU

                if (packagerenlist.isNotEmpty()) {
                    if (packagerenlist!![1].freeTrialPeriod == null) {
//                    textsub.text = mActivity.getString(
//                        R.string.year_price,
//                        packagerenlist?.get(1)?.price
//                    )
                        textsub.text = mActivity.getString(
                            R.string.year_price,
                            "${txtYearlyPrice.text}"
                        )
                      /*  textsub.text = mActivity.getString(
                            R.string.year_price,
                            "${packagerenlist?.get(1)?.price?.first()}${
                                packagerenlist?.get(1)?.price?.substring(1)
                                    ?.replace(",", "")?.toDouble()?.roundToInt()
                            }"
                        )*/
                    } else {
//
                        textsub.text =
                            "${mActivity.getString(R.string._7_days)}" +
                                    "" + " " + "${mActivity.getString(R.string.FREE_trial_new_new)}" +
                                    "" + " ${mActivity.getString(R.string.than_price_ex)} " + "${
                                packagerenlist?.get(
                                    1
                                )?.price
                            }" +
                                    "" + "${mActivity.getString(R.string.year_static)}"
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
                openCustomTab(mActivity, customIntent.build(), Uri.parse(url))
            }
//            txtTerms.setOnClickListener {
//                Constants.isActivitychange = true
//                mActivity.startActivity(Intent(mActivity, TermsActivity::class.java))
//            }

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
                                                BaseSharedPreferences(mActivity).mIS_SUBSCRIBED =
                                                    true
                                                isSelecterdPlan.monBackPress()
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
                                                BaseSharedPreferences(mActivity).mIS_SUBSCRIBED =
                                                    true
                                                mActivity.onBackPressed()
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
                                                BaseSharedPreferences(mActivity).mIS_SUBSCRIBED =
                                                    true
                                                mActivity.onBackPressed()
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
                                                BaseSharedPreferences(mActivity).mIS_SUBSCRIBED =
                                                    true
                                                mActivity.onBackPressed()
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

}
