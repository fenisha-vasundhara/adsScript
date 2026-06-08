package com.demo.adsmanage.Activity

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.demo.adsmanage.AdsManage.subPerchechCompleteNew
import com.demo.adsmanage.Commen.All_Ads_On
import com.demo.adsmanage.Commen.Constants
import com.demo.adsmanage.Commen.Constants.PREMIUM_SKU
import com.demo.adsmanage.Commen.Constants.offerscreenshow
import com.demo.adsmanage.Commen.Constants.setLocal
import com.demo.adsmanage.Commen.Message_Interstial_Ad_Conversation_Click_Ex

import com.demo.adsmanage.Commen.beGone
import com.demo.adsmanage.Commen.beInvisible
import com.demo.adsmanage.Commen.beVisible
import com.demo.adsmanage.Commen.isadsloadfalse
import com.demo.adsmanage.Commen.log
import com.demo.adsmanage.Commen.myEdgeToEdge
import com.demo.adsmanage.Commen.oldclass_vs_newclass_SUB
import com.demo.adsmanage.Commen.openUrlInCustomTabOrFallback
import com.messenger.phone.number.text.sms.service.apps.R
import com.demo.adsmanage.SubscriptionBaseClass.BaseSubscriptionActivity
import com.demo.adsmanage.basemodule.BaseSharedPreferences
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityTimerOfferBinding
import com.demo.adsmanage.helper.GlobalTimer
import com.demo.adsmanage.helper.isOnline
import com.demo.adsmanage.billing.ProductPurchaseHelper
import com.demo.adsmanage.viewmodel.SubscriptionOnePlanTimerViewModel
import com.messenger.phone.number.text.sms.service.apps.CommanClass.myEdgeToEdge
//import com.vision.aftercall.sdk.ads.advanceAds.registerAdsConfigSubscribe
import kotlinx.coroutines.launch

class TimerOfferActivity : BaseSubscriptionActivity(),
    ProductPurchaseHelper.ProductPurchaseListener {

    lateinit var binding: ActivityTimerOfferBinding

    var isfirsttime = false

    companion object {
        var plans = PREMIUM_SKU
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocal()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timer_offer)
        myEdgeToEdge(binding.vAnd15StatusBar)

        isfirsttime = intent.getBooleanExtra("isfirsttime", false)

        if (!BaseSharedPreferences(this@TimerOfferActivity).mIS_SUBSCRIBED!!) {
            if (isOnline) {
               
            }
        }

        binding.cardView3.loopShakeTilt(angle = 4f, duration = 200L)

        binding.imgClose.setOnClickListener {
            backpressclick()
        }

        lifecycleScope.launch {
            GlobalTimer.remainingMs.collect { data ->
                with(binding) {
                    minutesCount.text = data.Min
                    secondsCount.text = data.Sec
                    msCount.text = data.Ms
                }
            }
        }

        binding.viewmodel = SubscriptionOnePlanTimerViewModel(
            binding,
            this,
            liveDataPeriod,
            liveDataPrice,
            liveDataPriceMicro,
            subscriptionManager,
            object : SubscriptionOnePlanTimerViewModel.IsSelecterdPlan {
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

        binding.txtBottom3.setOnClickListener {
            val url = Constants.mPrivacyPolicyURL
            val customIntent = CustomTabsIntent.Builder()
            customIntent.setToolbarColor(ContextCompat.getColor(this, R.color.white))
            it.context.openUrlInCustomTabOrFallback("https://privacypolicy.kriadl.com/sms-messages-and-text-messaging/privacy-policy")
//            openCustomTab(this, customIntent.build(), Uri.parse(url))
        }



        binding.txtBottom2.setOnClickListener {
            Constants.isActivitychange = true
//            startActivity(Intent(this, TermsActivity::class.java))
            it.context.openUrlInCustomTabOrFallback("https://privacypolicy.kriadl.com/sms-messages-and-text-messaging/terms-of-service")
        }

    }


    fun View.loopShakeTilt(angle: Float = 4f, duration: Long = 80L) {
        this.pivotX = this.width / 2f
        this.pivotY = this.height / 2f

        val animator = ObjectAnimator.ofFloat(this, View.ROTATION, -angle, angle).apply {
            this.duration = duration
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
        }
        animator.start()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onBackPressed() {
        backpressclick()
    }

    fun openCustomTab(activity: Activity, customTabsIntent: CustomTabsIntent, uri: Uri?) {
        try {
            val packageName = "com.android.chrome"
            customTabsIntent.intent.setPackage(packageName)
            customTabsIntent.launchUrl(activity, uri!!)
        } catch (e: Exception) {

        }
    }

    private fun backpressclick() {
        if (All_Ads_On){
            if (!isfirsttime) {
                if (!BaseSharedPreferences(this@TimerOfferActivity).mIS_SUBSCRIBED!!) {
                    if (Message_Interstial_Ad_Conversation_Click_Ex == "1") {
                        if (isOnline) {
                            isadsloadfalse = true
//                            "ca-app-pub-2033413118114270/3101513893"
                            isadsloadfalse = false
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
        }else{
            finishSubscriptionFlow()
        }

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


    override fun onStart() {
        super.onStart()
        val window = this.window;
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
        }
        window.statusBarColor =
            ContextCompat.getColor(this, R.color.status_color)


        WindowCompat.setDecorFitsSystemWindows(window, true)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
//        controller.hide(WindowInsetsCompat.Type.navigationBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        timesetupe()

    }

    private fun timesetupe() {
        with(binding){
            if (!offerscreenshow) {
//            binding.offerTimeContenar.gone()
                textView16.beVisible()
                timerContenar.beInvisible()
            } else {
                if (!GlobalTimer.shouldStartGlobal) {
//                binding.offerTimeContenar.gone()
                    textView16.beVisible()
                    timerContenar.beInvisible()
                }else{
//                binding.offerTimeContenar.visible()
                    textView16.beGone()
                    timerContenar.beVisible()
                }
            }
        }
    }

}
