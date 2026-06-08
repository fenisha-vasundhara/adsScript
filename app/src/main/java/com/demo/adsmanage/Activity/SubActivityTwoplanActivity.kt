package com.demo.adsmanage.Activity

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.demo.adsmanage.AdsManage.subPerchechCompleteNew
import com.demo.adsmanage.Commen.Constants.PREMIUM_LIFTIME
import com.demo.adsmanage.Commen.Constants.setLocal
import com.demo.adsmanage.Commen.Message_Interstial_Ad_Conversation_Click_Ex

import com.demo.adsmanage.Commen.beVisible
import com.demo.adsmanage.Commen.isadsloadfalse
import com.demo.adsmanage.Commen.log
import com.demo.adsmanage.Commen.myEdgeToEdge
import com.demo.adsmanage.Commen.oldclass_vs_newclass_SUB
import com.demo.adsmanage.Commen.updateStatusbarColorMAinAppApp
import com.messenger.phone.number.text.sms.service.apps.R
import com.demo.adsmanage.SubscriptionBaseClass.BaseSubscriptionActivity
import com.demo.adsmanage.basemodule.BaseSharedPreferences
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivitySubTwoplanBinding
import com.demo.adsmanage.helper.isOnline
import com.demo.adsmanage.billing.ProductPurchaseHelper
import com.demo.adsmanage.viewmodel.SubActivityTwoplanViewModel
import com.messenger.phone.number.text.sms.service.apps.CommanClass.myEdgeToEdge
import com.messenger.phone.number.text.sms.service.apps.setBaseThemeLight
//import com.vision.aftercall.sdk.ads.advanceAds.registerAdsConfigSubscribe

class SubActivityTwoplanActivity : BaseSubscriptionActivity(),
    ProductPurchaseHelper.ProductPurchaseListener {

    lateinit var binding: ActivitySubTwoplanBinding
    var isfirsttime = false
    var text1 = ""
    var text2 = ""
    var text3 = ""
    var text4 = ""
    var text5 = ""

    companion object {
        var plans = PREMIUM_LIFTIME
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocal()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sub_twoplan)
        setBaseThemeLight(binding.vAnd15StatusBar)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
//            view.updatePadding(bottom = navBars.bottom)
            insets
        }
        isfirsttime = intent.getBooleanExtra("isfirsttime", false)
        text1 = getString(R.string.upgrade)
        text2 = getString(R.string.and)
        text3 = getString(R.string.chat)
        text4 = getString(R.string.without)
        text5 = getString(R.string.limits)
        setTextView()
        if (!BaseSharedPreferences(this@SubActivityTwoplanActivity).mIS_SUBSCRIBED!!) {
            if (isOnline) {
                
            }
        }
        binding.closeIcon.setOnClickListener {
            backpressclick()
        }


        binding.viewmodel = SubActivityTwoplanViewModel(
            binding,
            this,
            liveDataPeriod,
            liveDataPrice,
            liveDataPriceMicro,
            subscriptionManager,
            object : SubActivityTwoplanViewModel.IsSelecterdPlan {
                override fun monMonthPlan() {
                    onMonthPlan()
                }

                override fun monYearPlan() {
                    onYearPlan()
                }

                override fun monWeekPlan() {
                    onSixPlan()
                }

                override fun monBackPress() {
                    onBackPressed()
                }

            })

        Handler(Looper.getMainLooper()).postDelayed({
            binding.closeIcon.apply {
                alpha = 0f // start invisible
                beVisible() // make it visible but transparent
                animate()
                    .alpha(1f) // fade to full opacity
                    .setDuration(500) // animation duration (ms)
                    .start()
            }
        }, 3000)


    }


    private fun setTextView() {
        val fullText = "$text1 $text2 $text3\n$text4 $text5"
        val spannable = SpannableString(fullText)
        spannable.setSpan(
            ForegroundColorSpan(Color.BLACK),
            0,
            text1.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            text1.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(Color.BLACK),
            text1.length + 1,
            text1.length + 2,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val startChat = text1.length + 3
        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#2196F3")),
            startChat,
            startChat + text3.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            startChat,
            startChat + text3.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val start4 = fullText.indexOf(text4)
        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#2196F3")),
            start4,
            start4 + text4.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val start5 = fullText.indexOf(text5)
        spannable.setSpan(
            ForegroundColorSpan(Color.BLACK),
            start5,
            start5 + text5.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvFancy.setShadowLayer(5f, 3f, 3f, Color.GRAY)
        binding.tvFancy.text = spannable
    }

    override fun onPurchases(orderId: String, str: String) {
        "onPurchases BaseSharedPreferences <--------------------> 4".log()
        subPerchechCompleteNew?.onCompletedPurchaseNew(str,payScreenFlow)
        BaseSharedPreferences(this).mIS_SUBSCRIBED = true
//        registerAdsConfigSubscribe(true)
        onBackPressed()
    }

    override fun onPurchasedSuccess(purchase: Purchase) {
        Log.d("", "onPurchases: <------------> 2")
        "onPurchases BaseSharedPreferences <--------------------> 5".log()

        BaseSharedPreferences(this).mIS_SUBSCRIBED = true
//        registerAdsConfigSubscribe(true)
        onBackPressed()
    }

    override fun onProductAlreadyOwn() {
        Log.d("", "onPurchases: <------------> 3")
        "onPurchases BaseSharedPreferences <--------------------> 6".log()

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


    override fun onStart() {
        super.onStart()
        updateStatusbarColorMAinAppApp()
    }

    private fun backpressclick() {


        if (!isfirsttime) {
            if (!BaseSharedPreferences(this@SubActivityTwoplanActivity).mIS_SUBSCRIBED!!) {
                if (Message_Interstial_Ad_Conversation_Click_Ex == "1") {
                    if (isOnline) {
                        isadsloadfalse = true

//                        BackPressInter(
//                            interId = "ca-app-pub-2033413118114270/3101513893",
//                            adEnable = true,
//                            activity = this,
//                            preloadshow = false
//                        ) {
//                            isadsloadfalse = false
//                            finish()
//                        }

//                        val AdsListInter: ArrayList<AdConfigmodel> = arrayListOf(
//                            AdConfigmodel(
//                                no = 1,
//                                Adsid = "ca-app-pub-2033413118114270/3101513893"
//                            )
//                        )
//
//                        Handler(Looper.getMainLooper()).postDelayed({
//                            Admob.getInstance()?.ShowonTimeInterstital(
//                                context = this,
//                                id = null,
//                                multiAdsID = true,
//                                Adsidlist = AdsListInter,
//                                maxWaitingTime = 12,
//                                adCallback = object : AdCallback() {
//                                    override fun onAdLoaded() {}
//                                    override fun onInterstitialLoad(interstitialAd: InterstitialAd?) {}
//                                    override fun onAdClosed() {}
//                                    override fun onAdFailedToLoad(i: LoadAdError?) {
//                                        isadsloadfalse = false
//                                        finish()
//                                    }
//
//                                    override fun onAdFailedToShow(adError: AdError?) {
//                                        isadsloadfalse = false
//                                        finish()
//                                    }
//
//                                    override fun onAdClicked() {}
//                                    override fun onAdImpression() {}
//                                    override fun onAdDismissedFullScreenContent() {
//                                        super.onAdDismissedFullScreenContent()
//                                        isadsloadfalse = false
//                                        finish()
//                                    }
//                                })
//                        }, 200)


//                        if (newadsclass_vs_oldadsclass_MS_DE=="2"){
//                            BackPressInter(
//                                interId = "ca-app-pub-2033413118114270/3101513893",
//                                adEnable = true,
//                                activity = this,
//                                preloadshow = false
//                            ) {
//                                isadsloadfalse = false
//                                finish()
//                            }
//                        }else{
//                            if (oldclass_vs_newclass_SUB == "1") {
//                                admanager.showInterstitialAd(
//                                    this, 0,
//                                    onAdDismissed = {
//                                        isadsloadfalse = false
//                                        finish()
//                                    },
//                                    onAdFailedToLoad = {
//                                        isadsloadfalse = false
//                                        finish()
//                                    })
//                            } else {
//                                admanager.OnClickloadInterstitialAd(
//                                    true,this, 0,
//                                    onAdDismissed = {
//                                        isadsloadfalse = false
//                                        finish()
//                                    },
//                                    onAdFailedToLoad = {
//                                        isadsloadfalse = false
//                                        finish()
//                                    })
//                            }
//                        }
                        finishSubscriptionFlow()
                    } else {
                        finishSubscriptionFlow()
                    }
                } else {
                    finishSubscriptionFlow()
                }
            } else {
                finishSubscriptionFlow()
            }
        } else {
            finishSubscriptionFlow()
        }
    }
}
