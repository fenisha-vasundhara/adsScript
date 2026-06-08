package com.demo.adsmanage.Activity

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.demo.adsmanage.AdsManage.subPerchechCompleteNew
import com.demo.adsmanage.Commen.Constants
import com.demo.adsmanage.Commen.Constants.PREMIUM_SKU
import com.demo.adsmanage.Commen.Constants.setLocal
import com.demo.adsmanage.Commen.Message_Interstial_Ad_Conversation_Click_Ex
import com.demo.adsmanage.Commen.openUrlInCustomTabOrFallback
import com.demo.adsmanage.SliderViewPager4planAdapter

import com.demo.adsmanage.SubscriptionBaseClass.BaseSubscriptionActivity
import com.demo.adsmanage.basemodule.BaseSharedPreferences
import com.demo.adsmanage.billing.ProductPurchaseHelper
import com.demo.adsmanage.helper.isOnline
import com.demo.adsmanage.viewmodel.SubscriptionExperimentViewModel
import com.demo.adsmanage.viewmodel.SubscriptionFourPlanViewModel
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.CommanClass.baseConfig
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getPaywallProperBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isFLow1
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isFLow2
import com.messenger.phone.number.text.sms.service.apps.CommanClass.logOnboardingFunnelStep
import com.messenger.phone.number.text.sms.service.apps.R

import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityExperimentSubScreenBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityPaywallFourPlanBinding
import com.messenger.phone.number.text.sms.service.apps.setBaseThemeLight
import com.messenger.phone.number.text.sms.service.apps.setBaseThemePaywall
//import com.vision.aftercall.sdk.ads.advanceAds.registerAdsConfigSubscribe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class Paywall_FourPlan_Activity : BaseSubscriptionActivity(),
    ProductPurchaseHelper.ProductPurchaseListener {

    companion object {
        var plans = PREMIUM_SKU
    }

    var isfirsttime = false
    lateinit var binding: ActivityPaywallFourPlanBinding
    lateinit var viewPagerAdapter: SliderViewPager4planAdapter

    //    var imageList: ArrayList<Drawable> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocal()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_paywall_four_plan)

        setBaseThemePaywall(binding.vAnd15StatusBar)
        logOnboardingFunnelStep("First_paywall")
        themeAply()
        binding.lifetime.isSelected = true
        isfirsttime = intent.getBooleanExtra("isfirsttime", false)
        if (!BaseSharedPreferences(this@Paywall_FourPlan_Activity).mIS_SUBSCRIBED!!) {

        }
        binding.imgClose.setOnClickListener {
            backpressclick()
        }

        GlobalScope.launch {
            withContext(Dispatchers.IO) {

            }
            withContext(Dispatchers.Main) {
                with(binding) {


                    val imageResourceslight = listOf(
                        R.drawable.auto_img_4plan_light_1,
                        R.drawable.auto_img_4plan_light_2,
                        R.drawable.auto_img_4plan_light_3
                    )

                    val imageResourcesdark = listOf(
                        R.drawable.auto_img_4plan_dark_1,
                        R.drawable.auto_img_4plan_dark_2,
                        R.drawable.auto_img_4plan_dark_3
                    )

                val imageResources = if (ThemeModeManager.isDarkThemeActive(this@Paywall_FourPlan_Activity)) {
                    imageResourcesdark
                } else {
                    imageResourceslight
                }
                    viewPagerAdapter = SliderViewPager4planAdapter(
                        this@Paywall_FourPlan_Activity,
                        imageResources,
                        binding.myviewpager
                    )
                    viewPagerAdapter.istextviewshow = true
                    binding.myviewpager.adapter = viewPagerAdapter
                wormDotsIndicator.setViewPager(binding.myviewpager)
                    viewPagerAdapter.startAutoSlide()
                    Log.d("", "'imageResources: imageResources <-----------> 2")
                }
            }
        }

        binding.viewmodel = SubscriptionFourPlanViewModel(
            binding, this, liveDataPeriod,
            liveDataPrice, liveDataPriceMicro, subscriptionManager,
            object : SubscriptionFourPlanViewModel.IsSelecterdPlan {
                override fun monMonthPlan() {
                    onMonthPlan()
                }

                override fun monYearPlan() {
                    onYearPlan()
                }

                override fun monWeekPlan() {
//                onMonthPlan()
                    onSixPlan()
                }

                override fun monBackPress() {
                    onBackPressed()
                }
            })


        binding.txtPrivacy.setOnClickListener {
            val url = Constants.mPrivacyPolicyURL
            val customIntent = CustomTabsIntent.Builder()
            customIntent.setToolbarColor(ContextCompat.getColor(this, R.color.white))
            it.context.openUrlInCustomTabOrFallback("https://privacypolicy.kriadl.com/sms-messages-and-text-messaging/privacy-policy")
        }
        binding.txtTerms.setOnClickListener {
            Constants.isActivitychange = true
            it.context.openUrlInCustomTabOrFallback("https://privacypolicy.kriadl.com/sms-messages-and-text-messaging/terms-of-service")
        }

//        if (!BaseSharedPreferences(this@Experiment_Sub_Screen_Activity).mIS_SUBSCRIBED!!) {
//            preloadInterstitialAdWithMultiAdsUnit(
//                adUnits = listOfPaywallInter,
//                respectInterval = false,
//                "newPayWall"
//            )
//        }
    }

    private fun backpressclick() {
        var Stringfrom = intent.getStringExtra("from")
        if (!Stringfrom.isNullOrEmpty() && Stringfrom.equals("pinLimit")) {
            finishSubscriptionFlow()
        } else if (isFLow1 && !Stringfrom.isNullOrEmpty() && Stringfrom.equals("VarA") && !config.languageShowFirstvarA) {

            finishSubscriptionFlow()
        } else {
            if (!BaseSharedPreferences(this@Paywall_FourPlan_Activity).mIS_SUBSCRIBED!!) {
                baseConfig.onboardingHomeAgainPending = true
                if (isFLow1) {
                    logOnboardingFunnelStep("First_interad")
                    finishSubscriptionFlow()
                } else {
                    finishSubscriptionFlow()
                }
            } else {
                finishSubscriptionFlow()
            }
        }


    }

    override fun onPurchases(orderId: String, str: String) {
        subPerchechCompleteNew?.onCompletedPurchaseNew(str, payScreenFlow)

        BaseSharedPreferences(this).mIS_SUBSCRIBED = true
//        registerAdsConfigSubscribe(true)
        onBackPressed()
    }

    override fun onPurchasedSuccess(purchase: Purchase) {
        Log.d("", "onPurchases: <------------> 2")
        BaseSharedPreferences(this).mIS_SUBSCRIBED = true
//        registerAdsConfigSubscribe(true)
        onBackPressed()
    }

    override fun onProductAlreadyOwn() {
        Log.d("", "onPurchases: <------------> 3")
        BaseSharedPreferences(this).mIS_SUBSCRIBED = true
//        registerAdsConfigSubscribe(true)
        onBackPressed()
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {

    }

    override fun onBillingKeyNotFound(productId: String) {

    }

    fun themeAply() {
        val surfaceColor = getPaywallProperBackgroundColor()
        val textColor = getProperTextColor()
        val textColorlight = textColor.adjustAlpha(0.55f)
        val textColorlightPro = textColor.adjustAlpha(0.25f)
        val primaycolor = ContextCompat.getColor(this, R.color.paywall_primry_color)

        binding.main.setBackgroundColor(surfaceColor)
        binding.textTitle.setTextColor(textColor)
        binding.textTitleDis.setTextColor(textColorlight)
        binding.imgClose.imageTintList = ColorStateList.valueOf(textColorlight)
        binding.wormDotsIndicator.dotsColor = textColorlight
        binding.txtBottom.setTextColor(textColorlight)
        binding.textsub.setTextColor(textColor)

        binding.view.setBackgroundColor(textColorlightPro)


        binding.txtAutoRenewYear.setTextColor(textColor)
        binding.txtAutoRenewMonth.setTextColor(textColor)
//        binding.monthstaticprice.setTextColor(textColor)
        binding.monthstatic.setTextColor(textColor)

        binding.txtAutoRenewWeek.setTextColor(textColor)
//        binding.weekstaticprice.setTextColor(textColor)
        binding.weekstatic.setTextColor(textColor)

        binding.txtAutoRenewLife.setTextColor(textColor)
//        binding.txtlifePrice.setTextColor(textColor)

        binding.txtYearlyPrice.setTextColor(textColorlightPro)
        binding.yeartxtcustom.setTextColor(textColorlightPro)
        binding.txtMonthlyPrice.setTextColor(textColorlightPro)
        binding.txtweekPrice.setTextColor(textColorlightPro)
        binding.lifetime.setTextColor(textColorlightPro)



    }

    override fun onBackPressed() {
        backpressclick()
    }

    override fun onResume() {
        super.onResume()
        if (::binding.isInitialized) {
            themeAply()
        }
    }
}
