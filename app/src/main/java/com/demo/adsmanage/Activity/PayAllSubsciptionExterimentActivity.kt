package com.demo.adsmanage.Activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.viewpager.widget.ViewPager
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.demo.adsmanage.AdsManage.subPerchechCompleteNew
import com.demo.adsmanage.Commen.Constants
import com.demo.adsmanage.Commen.Constants.PREMIUM_SKU
import com.demo.adsmanage.Commen.Constants.setLocal
import com.demo.adsmanage.Commen.Message_Interstial_Ad_Conversation_Click_Ex

import com.demo.adsmanage.Commen.beVisible
import com.demo.adsmanage.Commen.isadsloadfalse
import com.demo.adsmanage.Commen.log
import com.demo.adsmanage.Commen.openUrlInCustomTabOrFallback
import com.demo.adsmanage.Commen.updateStatusbarColorMAinAppApp
import com.messenger.phone.number.text.sms.service.apps.R
import com.demo.adsmanage.SliderViewPagerNewExperimentAdapter
import com.demo.adsmanage.SubscriptionBaseClass.BaseSubscriptionActivity
import com.demo.adsmanage.basemodule.BaseSharedPreferences
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityPayAllSubsciptionExterimentBinding
import com.demo.adsmanage.helper.isOnline
import com.demo.adsmanage.billing.ProductPurchaseHelper
import com.demo.adsmanage.model.SunCommantNewModel
import com.demo.adsmanage.viewmodel.SubscriptionExperimentPayAllExperimentViewModel
import com.google.android.material.color.DynamicColors
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.findBestInsetParent
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.CommanClass.windowInsetsSet
import com.messenger.phone.number.text.sms.service.apps.setBaseThemeLight
//import com.vision.aftercall.sdk.ads.advanceAds.registerAdsConfigSubscribe


class PayAllSubsciptionExterimentActivity : BaseSubscriptionActivity(),
    ProductPurchaseHelper.ProductPurchaseListener {

    lateinit var binding: ActivityPayAllSubsciptionExterimentBinding

    companion object {
        var plans = PREMIUM_SKU
    }

    var imageList: ArrayList<SunCommantNewModel> = arrayListOf()
    lateinit var viewPagerAdapter: SliderViewPagerNewExperimentAdapter
    var isfirsttime = false





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocal()
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_pay_all_subsciption_exteriment)
        setBaseThemeLight(binding.vAnd15StatusBar)


        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
//            view.updatePadding(bottom = navBars.bottom)
            insets
        }
        binding.lifetimetxt.isSelected = true
        isfirsttime = intent.getBooleanExtra("isfirsttime", false)


        binding.closeIcon.setOnClickListener {
            backpressclick()
        }

        imageList.add(
            SunCommantNewModel(
                4,
                resources.getString(R.string.Premium_Categories),
                resources.getString(R.string.con_1_new),
                resources.getString(R.string.con_1_name_new),
                image = R.drawable.experiment_img_1,
                startimage = R.drawable.pro_start
            )
        )
        imageList.add(
            SunCommantNewModel(
                4,
                resources.getString(R.string.Schedule_Messages),
                resources.getString(R.string.con_2_new),
                resources.getString(R.string.con_2_name_new),
                image = R.drawable.experiment_img_2,
                startimage = R.drawable.pro_start_2
            )
        )
        imageList.add(
            SunCommantNewModel(
                4,
                resources.getString(R.string.Theme_Notification),
                resources.getString(R.string.con_3_new),
                resources.getString(R.string.con_3_name_new),
                image = R.drawable.experiment_img_3,
                startimage = R.drawable.pro_start_5
            )
        )
        imageList.add(
            SunCommantNewModel(
                4,
                resources.getString(R.string.Global_Translation),
                resources.getString(R.string.con_4_new),
                resources.getString(R.string.con_4_name_new),
                image = R.drawable.experiment_img_4,
                startimage = R.drawable.pro_start_3
            )
        )

        imageList.add(
            SunCommantNewModel(
                4,
                resources.getString(R.string.Private_Chat),
                resources.getString(R.string.con_4_new),
                resources.getString(R.string.con_4_name_new),
                image = R.drawable.experiment_img_5,
                startimage = R.drawable.pro_start_4
            )
        )

        viewPagerAdapter = SliderViewPagerNewExperimentAdapter(this, imageList, binding.myviewpager)
        binding.myviewpager.adapter = viewPagerAdapter
        viewPagerAdapter.startAutoSlide()


        binding.myviewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {

            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> binding.indicator.setImageResource(R.drawable.in1)
                    1 -> binding.indicator.setImageResource(R.drawable.in2)
                    2 -> binding.indicator.setImageResource(R.drawable.in3)
                    3 -> binding.indicator.setImageResource(R.drawable.in4)
                    4 -> binding.indicator.setImageResource(R.drawable.in5)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

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
        binding.viewmodel = SubscriptionExperimentPayAllExperimentViewModel(
            binding,
            this,
            liveDataPeriod,
            liveDataPrice,
            liveDataPriceMicro,
            subscriptionManager,
            object : SubscriptionExperimentPayAllExperimentViewModel.IsSelecterdPlan {
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

    private fun backpressclick() {


        if (!isfirsttime) {
            if (!BaseSharedPreferences(this@PayAllSubsciptionExterimentActivity).mIS_SUBSCRIBED!!) {
                if (Message_Interstial_Ad_Conversation_Click_Ex == "1") {
                    if (isOnline) {
                        isadsloadfalse = true

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

    override fun onPurchases(orderId: String, str: String) {
        "onPurchases BaseSharedPreferences <--------------------> 4".log()
        subPerchechCompleteNew?.onCompletedPurchaseNew(str,payScreenFlow)
        BaseSharedPreferences(this).mIS_SUBSCRIBED = true
//        registerAdsConfigSubscribe(true)
//        setSubscriptionStatus =true
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
}
