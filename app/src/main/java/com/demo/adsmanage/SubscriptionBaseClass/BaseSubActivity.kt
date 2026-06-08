package com.demo.adsmanage.SubscriptionBaseClass

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.demo.adsmanage.SubscriptionBaseClass.manager.PreferencesKeys
import com.demo.adsmanage.SubscriptionBaseClass.manager.SubscriptionManager

open class BaseSubActivity : AppCompatActivity() {

    private lateinit var subscriptionManager: SubscriptionManager

    private var isSubscribe = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        subscriptionManager = SubscriptionManager(this)

        isSubscribe = subscriptionManager.geBoolean(PreferencesKeys.SUBSCRIBE, false)
//        isSubscribe = true


        //Log.d("ASTSG", "onCreate: ${AppSubscription().isDebug()} ${BuildConfig.DEBUG}")

        /*if(AppSubscription().isDebug() && BuildConfig.DEBUG){
            isSubscribe = true
        }*/

        /*subscriptionManager.values.asLiveData().observe(this, Observer {
            it[PreferencesKeys.SUBSCRIBE]?.let {
                isSubscribe = it
            }
        })*/
    }

    /**
     * return result for subscribe
     */
    fun isSubscribe() = isSubscribe
    fun isResfresh() {
        isSubscribe = subscriptionManager.geBoolean(PreferencesKeys.SUBSCRIBE, false)
    }
}