package com.demo.adsmanage.SubscriptionBaseClass

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.demo.adsmanage.SubscriptionBaseClass.manager.SubscriptionManager

open class BaseSubFragment : Fragment() {

    protected lateinit var subscriptionManager: SubscriptionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        subscriptionManager = SubscriptionManager(requireContext())

    }


    /**
     * return result for subscribe
     */
//    fun isSubscribe() = if (AppSubscription().isDebug() && BuildConfig.DEBUG)
//        true
//    else subscriptionManager.geBoolean(PreferencesKeys.SUBSCRIBE, false)
}