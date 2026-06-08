package com.messenger.phone.number.text.sms.service.apps.subscription

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
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
import com.demo.adsmanage.Commen.openUrlInCustomTabOrFallback
import com.demo.adsmanage.SliderViewPager4planAdapter

import com.demo.adsmanage.SubscriptionBaseClass.BaseSubscriptionActivity
import com.demo.adsmanage.basemodule.BaseSharedPreferences
import com.demo.adsmanage.billing.ProductPurchaseHelper
import com.messenger.phone.number.text.sms.service.apps.subscription.SubscriptionDynamicViewModel
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.CommanClass.baseConfig
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getPaywallProperBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isFLow1
import com.messenger.phone.number.text.sms.service.apps.CommanClass.logOnboardingFunnelStep
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityPaywallDynamicBinding
import com.messenger.phone.number.text.sms.service.apps.setBaseThemePaywall
//import com.vision.aftercall.sdk.ads.advanceAds.registerAdsConfigSubscribe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class Paywall_dynamic_Activity : BaseSubscriptionActivity(),
    ProductPurchaseHelper.ProductPurchaseListener {

    companion object {
        var plans = PREMIUM_SKU
        private const val CLOSE_BUTTON_DELAY_MS = 3_000L
        private const val CLOSE_BUTTON_REVEAL_DURATION_MS = 1280L
    }

    var isfirsttime = false
    lateinit var binding: ActivityPaywallDynamicBinding
    lateinit var viewPagerAdapter: SliderViewPager4planAdapter
    private val closeButtonHandler = Handler(Looper.getMainLooper())
    private val showCloseButtonRunnable = Runnable {
        if (!isDestroyed && !isFinishing) {
            binding.imgClose.animate().cancel()
            binding.imgClose.isEnabled = true
            binding.imgClose.isClickable = true
            binding.imgClose.visibility = View.VISIBLE
            binding.imgClose.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(CLOSE_BUTTON_REVEAL_DURATION_MS)
                .setInterpolator(OvershootInterpolator(0.7f))
                .start()
        }
    }

    //    var imageList: ArrayList<Drawable> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocal()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_paywall_dynamic)
        applyWindowInsets()

        setBaseThemePaywall(binding.vAnd15StatusBar)
//        logOnboardingFunnelStep("First_paywall")
        isfirsttime = intent.getBooleanExtra("isfirsttime", false)
        if (!BaseSharedPreferences(this@Paywall_dynamic_Activity).mIS_SUBSCRIBED!!) {

        }
        binding.imgClose.alpha = 0f
        binding.imgClose.scaleX = 0.88f
        binding.imgClose.scaleY = 0.88f
        binding.imgClose.isEnabled = false
        binding.imgClose.isClickable = false
        binding.imgClose.setOnClickListener {
            backpressclick()
        }
        binding.imgClose111.setOnClickListener {
            finishSubscriptionFlow()
        }
        binding.imgClose.post {
            if (isDestroyed || isFinishing) {
                return@post
            }
            binding.imgClose.animate().cancel()
            binding.imgClose.alpha = 0f
            binding.imgClose.scaleX = 0.88f
            binding.imgClose.scaleY = 0.88f
            binding.imgClose.isEnabled = false
            binding.imgClose.isClickable = false
            closeButtonHandler.removeCallbacks(showCloseButtonRunnable)
            closeButtonHandler.postDelayed(showCloseButtonRunnable, CLOSE_BUTTON_DELAY_MS)
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

                    val imageResources =
                        if (ThemeModeManager.isDarkThemeActive(this@Paywall_dynamic_Activity)) {
                            imageResourcesdark
                        } else {
                            imageResourceslight
                        }
                    viewPagerAdapter = SliderViewPager4planAdapter(
                        this@Paywall_dynamic_Activity,
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

        binding.viewmodel = SubscriptionDynamicViewModel(
            binding, this, liveDataPeriod,
            liveDataPrice, liveDataPriceMicro, subscriptionManager,
            object : SubscriptionDynamicViewModel.IsSelecterdPlan {
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

        themeAply()

    }

    override fun onDestroy() {
        closeButtonHandler.removeCallbacks(showCloseButtonRunnable)
        super.onDestroy()
    }

    private fun backpressclick() {
        var Stringfrom = intent.getStringExtra("from")
        if (!Stringfrom.isNullOrEmpty() && Stringfrom.equals("pinLimit")) {
            finishSubscriptionFlow()
        } else if (isFLow1 && !Stringfrom.isNullOrEmpty() && Stringfrom.equals("VarA") && !config.languageShowFirstvarA) {

            finishSubscriptionFlow()
        } else {
            if (!BaseSharedPreferences(this@Paywall_dynamic_Activity).mIS_SUBSCRIBED!!) {
                baseConfig.onboardingHomeAgainPending = true
                if (isFLow1) {
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

        binding.txtYearlyPrice.setTextColor(textColor)
        binding.yeartxtcustom.setTextColor(textColor)

        binding.txtMonthlyPrice.setTextColor(textColorlightPro)
        binding.txtweekPrice.setTextColor(textColorlightPro)
        binding.lifetime.setTextColor(textColorlightPro)


    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        backpressclick()
    }

    override fun onResume() {
        super.onResume()
        if (::binding.isInitialized) {
            themeAply()
        }
    }

    private fun applyWindowInsets() {
        val statusLayout = binding.vAnd15StatusBar
        val footerLayout = binding.mCLUnlockLayout2
        val baseStatusHeight = statusLayout.layoutParams.height
        val footerParams = footerLayout.layoutParams as ViewGroup.MarginLayoutParams
        val baseFooterBottomMargin = footerParams.bottomMargin

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            statusLayout.layoutParams = statusLayout.layoutParams.apply {
                height = baseStatusHeight + systemBars.top
            }

//            footerLayout.layoutParams = footerParams.apply {
//                bottomMargin = /*baseFooterBottomMargin +*/ systemBars.bottom
//            }

            insets
        }
        ViewCompat.requestApplyInsets(binding.main)
    }
}
