package com.messenger.phone.number.text.sms.service.apps.ads

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.ads.AdActivity
import com.onesignal.OneSignal
import com.onesignal.notifications.INotificationClickEvent
import com.onesignal.notifications.INotificationClickListener
import com.onesignal.user.state.IUserStateObserver
import com.onesignal.user.state.UserChangedState
import com.onesignal.user.subscriptions.IPushSubscriptionObserver
import com.onesignal.user.subscriptions.PushSubscriptionChangedState
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.interfaces.LogInCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

var onesignal_keyword = ""
var oneSignalCallBacks: OneSignalCallBacks? = null
var onesignal_currentActivity: Activity? = null
var onesignal_notification = false
var oneSignalClickObserver = MutableLiveData<String?>().apply { postValue(null) }
val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
var ONE_SIGNAL_SDK_ID = ""
var isInitialized = false

interface OneSignalCallBacks {
    fun oneSignalClick(keyword: String)
}

/*
*
* implementation("com.onesignal:OneSignal:5.1.36")
*
* // In application clas's onCreate
        initOneSignal(this)
        notificationHandle()
*
* */
fun initOneSignal(application: Application, appId: String, iUserManager: (String) -> Unit) {
    ONE_SIGNAL_SDK_ID = appId
    appScope.launch {
        OneSignal.initWithContext(application, ONE_SIGNAL_SDK_ID)
        OneSignal.Notifications.addClickListener(
            OneSignalListener(
                application.applicationContext
            )
        )
        isInitialized = true
        iUserManager(OneSignal.User.onesignalId)
        val existingId = OneSignal.User.onesignalId
        if (existingId.isNotEmpty()) {
            Purchases.sharedInstance.setOnesignalUserID(existingId)
        }
        OneSignal.User.pushSubscription.addObserver(object : IPushSubscriptionObserver {
            override fun onPushSubscriptionChange(state: PushSubscriptionChangedState) {
                val newId = state.current.id
                Log.d("OneSignal", "OneSignalSubscriptionId ready: $newId")

            }
        })

        OneSignal.User.addObserver(object : IUserStateObserver {
            override fun onUserStateChange(state: UserChangedState) {
                val newId = state.current.onesignalId
                if (newId.isNotEmpty()) {
                    iUserManager(newId)
                    Purchases.sharedInstance.setOnesignalUserID(newId)
//                    Purchases.sharedInstance.setOnesignalUserID(newId)
                }
            }
        })
        // ---------- 2. OneSignal ----------
        OneSignal.Debug.logLevel = com.onesignal.debug.LogLevel.VERBOSE // remove in production

        // Ask for notification permission (suspend; OneSignal handles coroutine context)
        CoroutineScope(Dispatchers.IO).launch {
//            OneSignal.Notifications.requestPermission(true)
        }
        onUserLoggedIn(existingId)

        // ---------- 3. The Bridge ----------
//        wireOneSignalToRevenueCat()

    }
}
private fun wireOneSignalToRevenueCat() {
    // (a) If OneSignal already has a user ID, send it to RevenueCat now
    val existingId = OneSignal.User.onesignalId
    if (!existingId.isNullOrEmpty()) {
        Purchases.sharedInstance.setOnesignalUserID(existingId)
    }

    // (b) Watch for changes — OneSignal IDs can change after login/logout
    OneSignal.User.addObserver(object : IUserStateObserver {
        override fun onUserStateChange(state: UserChangedState) {
            val newId = state.current.onesignalId
            if (!newId.isNullOrEmpty()) {
                Purchases.sharedInstance.setOnesignalUserID(newId)
            }
        }
    })
}
fun onUserLoggedIn(myAppUserId: String) {
    // Tell OneSignal who this user is
    OneSignal.login(myAppUserId)

    Purchases.sharedInstance.logIn(
        myAppUserId,
        object : LogInCallback {

            override fun onReceived(
                customerInfo: CustomerInfo,
                created: Boolean
            ) {
                Log.d("RC", "RevenueCat user identified")
            }

            override fun onError(error: PurchasesError) {
                Log.e("RC", "logIn failed: $error")
            }
        }
    )
}

fun setOnesignalPurchased(value: String) {
    if (!isInitialized) return
    OneSignal.User.addTag("iap_subscribed", value)
}

class OneSignalListener(val context: Context) : INotificationClickListener {

    override fun onClick(event: INotificationClickEvent) {
        val notification = event.notification
        val additionalData = notification.additionalData
        onesignal_keyword = additionalData?.getString("activity_name") ?: "".toString()
        onesignal_notification = true
        oneSignalClickObserver.postValue(onesignal_keyword)
    }
}

fun Context.checkNotificationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED
}


fun Application.notificationHandle(
    activities: List<Activity>,
    oneSignalCallBacksFromApp: OneSignalCallBacks
) {
    oneSignalCallBacks = oneSignalCallBacksFromApp
    /**
     * Check this current Activity is Ad or not
     * */
    var flag = activities.any {
        it == onesignal_currentActivity
    }
    if (onesignal_currentActivity is AdActivity || flag) return

    CoroutineScope(Dispatchers.Main).launch {
        if (onesignal_notification && checkNotificationPermission())
            oneSignalClickObserver.observeForever {
                it?.let { key ->
                    oneSignalCallBacks?.oneSignalClick(key)
                }
            }
    }

}