package com.demo.adsmanage.SubscriptionBaseClass

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.demo.adsmanage.Commen.Constants.BASIC_SKU
import com.demo.adsmanage.Commen.Constants.PREMIUM_SIX_SKU
import com.demo.adsmanage.Commen.Constants.PREMIUM_SKU
import com.demo.adsmanage.Commen.Constants.isActivitychange
import com.demo.adsmanage.billing.BillingClientLifecycle
import com.demo.adsmanage.billing.productCurrencyCode
import com.demo.adsmanage.billing.productFormattedPrice
import com.demo.adsmanage.billing.productPriceAmountMicros
import com.demo.adsmanage.billing.productTrialPeriodIso
import com.demo.adsmanage.SubscriptionBaseClass.manager.PreferencesKeys
import com.demo.adsmanage.SubscriptionBaseClass.manager.SubscriptionManager
import com.demo.adsmanage.viewmodel.AppSubscription
import com.demo.adsmanage.billing.BillingViewModel
import com.messenger.phone.number.text.sms.service.apps.CommanClass.baseConfig
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isFLow1
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isFLow2
import com.messenger.phone.number.text.sms.service.apps.LanguageActivity

abstract class BaseSubscriptionActivity : AppCompatActivity() {

    private val mBillingViewModel: BillingViewModel by viewModels<BillingViewModel>()
    // val mSubscriptionViewModel: SubscriptionStatusViewModel by viewModels<SubscriptionStatusViewModel>()

    private lateinit var billingClientLifecycle: BillingClientLifecycle

    private var mPriceMap: HashMap<String, String> = HashMap()
    private var mPriceMapMicro: HashMap<String, Long> = HashMap()
    private var mTrialPeriod: HashMap<String, String> = HashMap()

    protected var liveDataPrice = MutableLiveData<HashMap<String, String>>()
    protected var liveDataPriceMicro = MutableLiveData<HashMap<String, Long>>()
    protected var liveDataPeriod = MutableLiveData<HashMap<String, String>>()
    protected var currencyCode = MutableLiveData<String>()

    lateinit var subscriptionManager: SubscriptionManager
    var payScreenFlow = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        subscriptionManager = SubscriptionManager(this)


        val appOpen = intent.getStringExtra("AppOpen")
        payScreenFlow = if(appOpen=="SettingsActivity"){
            "home"
        }else if(appOpen==null){
            ""
        }else{
            appOpen
        }

        mPriceMap[PREMIUM_SIX_SKU] = subscriptionManager.getString(PreferencesKeys.SIXMonth_PRICE,"₹100.00")
        mPriceMap[BASIC_SKU] = subscriptionManager.getString(PreferencesKeys.MONTH_PRICE,"₹249.00")
        mPriceMap[PREMIUM_SKU] = subscriptionManager.getString(PreferencesKeys.YEAR_PRICE,"₹1,999.00")

        mPriceMapMicro[PREMIUM_SIX_SKU] = subscriptionManager.getLong(PreferencesKeys.YSIX_PRICE_MICRO,100000000)
        mPriceMapMicro[BASIC_SKU] = subscriptionManager.getLong(PreferencesKeys.MONTH_PRICE_MICRO,249000000)
        mPriceMapMicro[PREMIUM_SKU] = subscriptionManager.getLong(PreferencesKeys.YEAR_PRICE_MICRO,1999000000)
//        Log.d(TAG, "onCreate:111 ${BASIC_SKU}--${subscriptionManager.getString(PreferencesKeys.MONTH_TRIAL_PERIOD,"")}")
        mTrialPeriod[BASIC_SKU] = subscriptionManager.getString(PreferencesKeys.MONTH_TRIAL_PERIOD,"")
        mTrialPeriod[PREMIUM_SKU] = subscriptionManager.getString(PreferencesKeys.YEAR_TRIAL_PERIOD,"")
        mTrialPeriod[PREMIUM_SIX_SKU] = subscriptionManager.getString(PreferencesKeys.SIX_TRIAL_PERIOD,"")
//        mTrialPeriod[PREMIUM_SIX_SKU] = subscriptionManager.getString(PreferencesKeys.SIX_TRIAL_PERIOD,"P1D")
        currencyCode.postValue(subscriptionManager.getString(PreferencesKeys.CURRENCY_CODE,"INR"))
        liveDataPrice.postValue(mPriceMap)
        liveDataPriceMicro.postValue(mPriceMapMicro)
        liveDataPeriod.postValue(mTrialPeriod)

        /*subscriptionManager.values.asLiveData().observe(this, Observer {
            it[PreferencesKeys.MONTH_PRICE]?.let {
                mPriceMap[BASIC_SKU] = it
            } ?: kotlin.run {
                mPriceMap[BASIC_SKU] = "₹250.00"
            }
            it[PreferencesKeys.YEAR_PRICE]?.let {
                mPriceMap[PREMIUM_SKU] = it
            } ?: kotlin.run {
                mPriceMap[PREMIUM_SKU] = "₹1,550.00"
            }

            it[PreferencesKeys.MONTH_TRIAL_PERIOD]?.let {
                mTrialPeriod[BASIC_SKU] = it
            } ?: kotlin.run {
                mTrialPeriod[BASIC_SKU] = "P3D"
            }
            it[PreferencesKeys.YEAR_TRIAL_PERIOD]?.let {
                mTrialPeriod[PREMIUM_SKU] = it
            } ?: kotlin.run {
                mTrialPeriod[PREMIUM_SKU] = "P3D"
            }

        })*/

        // Billing APIs are all handled in the this lifecycle observer.
        billingClientLifecycle = (application as AppSubscription).billingClientLifecycle
        lifecycle.addObserver(billingClientLifecycle)


        // Register purchases when they change.
        billingClientLifecycle.purchaseUpdateEvent.observe(this, Observer {
            registerPurchases(it)
        })

        // Launch the mbilling flow when the user clicks a button to buy something.
        mBillingViewModel.buyEvent.observe(this, Observer {
            it?.let {
                billingClientLifecycle.launchBillingFlow(this, it)
            }
        })

        mBillingViewModel.skusWithSkuDetails.observe(this, Observer { map ->
            map.values.forEach { productDetails ->
                val formattedPrice = productDetails.productFormattedPrice()
                val priceMicros = productDetails.productPriceAmountMicros()
                val trialPeriod = productDetails.productTrialPeriodIso()
                val currency = productDetails.productCurrencyCode()
                when (productDetails.productId) {
                    BASIC_SKU -> {
                        formattedPrice?.let { price -> subscriptionManager.setMonthPrice(price) }
                        priceMicros?.let { micros -> subscriptionManager.setMonthPrice(micros) }
                        subscriptionManager.setMonthTrialPeriod(trialPeriod)
                        currency?.let { code -> subscriptionManager.setCurrencyCode(code) }
                    }
                    PREMIUM_SKU -> {
                        formattedPrice?.let { price -> subscriptionManager.setYearPrice(price) }
                        priceMicros?.let { micros -> subscriptionManager.setYearPrice(micros) }
                        subscriptionManager.setYearTrialPeriod(trialPeriod)
                        currency?.let { code -> subscriptionManager.setCurrencyCode(code) }
                    }
                    PREMIUM_SIX_SKU -> {
                        formattedPrice?.let { price -> subscriptionManager.setSixMonthrPrice(price) }
                        priceMicros?.let { micros -> subscriptionManager.setSixPrice(micros) }
                        subscriptionManager.setSixTrialPeriod(trialPeriod)
                        currency?.let { code -> subscriptionManager.setCurrencyCode(code) }
                    }
                }
            }

        })


    }

    fun onMonthPlan() {
        mBillingViewModel.buyBasic()
    }

    fun onYearPlan() {
        mBillingViewModel.buyPremium()
    }
    fun onSixPlan() {
        mBillingViewModel.buySixPremium()
    }

   /* protected fun finishSubscriptionFlow() {
        val appOpen = intent.getStringExtra("AppOpen")
        val nextIntent = intent.getStringExtra("mNextActivityIntent")
        isActivitychange = true
        if (appOpen == "SplashScreen" && !nextIntent.isNullOrBlank()) {
            try {
                startActivity(Intent(this, Class.forName(nextIntent)))
            } catch (_: Exception) {
                // Fall through to finish when the target class is missing.
            }
        }
        finish()
    }*/
    protected fun finishSubscriptionFlow() {
        val appOpen = intent.getStringExtra("AppOpen")
       var Stringfrom = intent.getStringExtra("from")
        payScreenFlow = if(appOpen=="SettingsActivity"){
            "home"
        }else if(appOpen==null){
            ""
        }else{
            appOpen
        }
        val nextIntent = intent.getStringExtra("mNextActivityIntent")
        isActivitychange = true

       if (appOpen == "SplashScreen"&& isFLow2){
           val intent = Intent(this, LanguageActivity::class.java)
           intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
           intent.putExtra("mainopen", true)
           startActivity(intent)
           finish()
//           finish()
       }
       else if (isFLow1&&!Stringfrom.isNullOrEmpty() && Stringfrom.equals("VarA")&&  !config.languageShowFirstvarA){
           val intent = Intent(this, LanguageActivity::class.java)
           intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
           intent.putExtra("mainopen", true)
           intent.putExtra("needintervarA", true)
           startActivity(intent)
           finish()
       }else{
           finish()
       }
//        if (appOpen == "SplashScreen" && !nextIntent.isNullOrBlank()) {
//            try {
//                startActivity(Intent(this, Class.forName(nextIntent)))
//            } catch (_: Exception) {
//                // Fall through to finish when the target class is missing.
//            }
//        }

    }
    /**
     * Register SKUs and purchase tokens with the server.
     */
    private fun registerPurchases(purchaseList: List<Purchase>?) {
        purchaseList?.let { purchases ->
            Log.d(TAG, "registerPurchases: ${purchases.size}")

            if (purchases.isNotEmpty()) {
                val firstPurchase = purchases[0]

                val orderId = firstPurchase.orderId
                val sku = firstPurchase.skus.firstOrNull()   // or .getOrNull(0)

                if (orderId != null && sku != null) {
                    subscriptionManager.setSubscribe(true)
                    onPurchases(orderId, sku)
                } else {
                    // At least one critical field is missing → treat as not subscribed
                    // or log + fallback
                    Log.w(TAG, "Valid subscription purchase missing orderId or sku → ignoring")
                    subscriptionManager.setSubscribe(false)
                }
            } else {
                subscriptionManager.setSubscribe(false)
            }
        } ?: run {
            subscriptionManager.setSubscribe(false)
        }
    }
   /* private fun registerPurchases(purchaseList: List<Purchase>?) {
        purchaseList?.let {
            Log.d(TAG, "registerPurchases: ${purchaseList.size}")
            if (it.isNotEmpty()) {
                subscriptionManager.setSubscribe(true)
                onPurchases(it[0].orderId!!,it[0].skus[0])
            } else {
                subscriptionManager.setSubscribe(false)
            }
        } ?: kotlin.run {
            subscriptionManager.setSubscribe(false)
        }
    }
*/
    private fun refreshData() {
        billingClientLifecycle.queryPurchases()
        //mSubscriptionViewModel.manualRefresh()
    }

    /**
     * Callback for Purchases
     */
    abstract fun onPurchases(orderId : String,str: String)

    companion object {
        private const val TAG = "SubscriptionActivity"
    }
}
