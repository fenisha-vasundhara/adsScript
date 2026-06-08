package com.demo.adsmanage.Activity

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

import com.demo.adsmanage.SliderViewPagerAdapter
import com.demo.adsmanage.SliderViewPagerExAdapter
import com.demo.adsmanage.SubscriptionBaseClass.BaseSubscriptionActivity
import com.demo.adsmanage.basemodule.BaseSharedPreferences
import com.demo.adsmanage.billing.ProductPurchaseHelper
import com.demo.adsmanage.helper.isOnline
import com.demo.adsmanage.viewmodel.SubscriptionExperimentViewModel
import com.messenger.phone.number.text.sms.service.apps.CommanClass.baseConfig
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isFLow1
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isFLow2
import com.messenger.phone.number.text.sms.service.apps.CommanClass.logOnboardingFunnelStep
import com.messenger.phone.number.text.sms.service.apps.R

import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityExperimentSubScreenBinding
import com.messenger.phone.number.text.sms.service.apps.setBaseThemeLight
//import com.vision.aftercall.sdk.ads.advanceAds.registerAdsConfigSubscribe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class Experiment_Sub_Screen_Activity : BaseSubscriptionActivity(),
    ProductPurchaseHelper.ProductPurchaseListener {

    companion object {
        var plans = PREMIUM_SKU
    }

    var isfirsttime = false
    lateinit var binding: ActivityExperimentSubScreenBinding
    lateinit var viewPagerAdapter: SliderViewPagerExAdapter

    //    var imageList: ArrayList<Drawable> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocal()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_experiment_sub_screen)
        setBaseThemeLight(binding.vAnd15StatusBar)
        logOnboardingFunnelStep("First_paywall")

        binding.lifetime.isSelected = true
        isfirsttime = intent.getBooleanExtra("isfirsttime", false)
        if (!BaseSharedPreferences(this@Experiment_Sub_Screen_Activity).mIS_SUBSCRIBED) {

        }
        binding.imgClose.setOnClickListener {
            backpressclick()
        }

        GlobalScope.launch {
            withContext(Dispatchers.IO) {

            }
            withContext(Dispatchers.Main) {
                with(binding) {

                    val imageResources = listOf(
                        R.drawable.auto_image_ex_1,
                        R.drawable.auto_image_ex_2,
                        R.drawable.auto_image_ex_3,
                        R.drawable.auto_image_ex_4,
                        R.drawable.auto_image_ex_5,
                    )

                    viewPagerAdapter = SliderViewPagerExAdapter(
                        this@Experiment_Sub_Screen_Activity,
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

        binding.viewmodel = SubscriptionExperimentViewModel(
            binding, this, liveDataPeriod,
            liveDataPrice, liveDataPriceMicro, subscriptionManager,
            object : SubscriptionExperimentViewModel.IsSelecterdPlan {
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
        if (!Stringfrom.isNullOrEmpty() && Stringfrom.equals("pinLimit")){
            finishSubscriptionFlow()
        }else if (isFLow1&&!Stringfrom.isNullOrEmpty() && Stringfrom.equals("VarA")&&  !config.languageShowFirstvarA){

            finishSubscriptionFlow()
        }
        else{
            if (!BaseSharedPreferences(this@Experiment_Sub_Screen_Activity).mIS_SUBSCRIBED) {
                baseConfig.onboardingHomeAgainPending = true
                if ( isFLow1 ) {
                    logOnboardingFunnelStep("First_interad")
                    finishSubscriptionFlow()
                } else {
                    finishSubscriptionFlow()
                }
            }else{
                finishSubscriptionFlow()
            }
        }


    }

    override fun onPurchases(orderId: String, str: String) {
        subPerchechCompleteNew?.onCompletedPurchaseNew(str,payScreenFlow)

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

    override fun onBackPressed() {
        backpressclick()
    }
}
