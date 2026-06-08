package com.messenger.phone.number.text.sms.service.apps.GoogleMobileAdsConsentManagerChack

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.demo.adsmanage.basemodule.BaseSharedPreferences
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentForm.OnConsentFormDismissedListener
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GoogleMobileAdsConsentManager private constructor(context: Context) {
    private val appContext = context.applicationContext
    private val tag = "GoogleAdsConsent"
    @Volatile
    private var consentInformation: ConsentInformation? = null
    private val consentInfoLock = Any()

    fun interface OnConsentGatheringCompleteListener {
        fun consentGatheringComplete(error: FormError?)
    }

    val canRequestAds: Boolean
        get() = consentInformation?.canRequestAds() ?: true

    val isPrivacyOptionsRequired: Boolean
        get() =
            consentInformation?.privacyOptionsRequirementStatus ==
                    ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    fun preload() {
        if (consentInformation != null) return
        synchronized(consentInfoLock) {
            if (consentInformation == null) {
                consentInformation = UserMessagingPlatform.getConsentInformation(appContext)
            }
        }
    }

    fun gatherConsent(
        activity: Activity,
        onConsentGatheringCompleteListener: OnConsentGatheringCompleteListener
    ) {
        if (BaseSharedPreferences(activity).mIS_SUBSCRIBED == true) {
            onConsentGatheringCompleteListener.consentGatheringComplete(null)
            return
        }
        if (!isActivityUsable(activity)) {
            onConsentGatheringCompleteListener.consentGatheringComplete(null)
            return
        }

        val debugSettings =
            ConsentDebugSettings.Builder(activity)
                .addTestDeviceHashedId("")
                .build()
        val params =
            ConsentRequestParameters.Builder().setConsentDebugSettings(debugSettings).build()

        ensureConsentInformation(activity) { consentInfo ->
            val requestConsent: () -> Unit = requestConsent@{
                if (!isActivityUsable(activity)) {
                    onConsentGatheringCompleteListener.consentGatheringComplete(null)
                    return@requestConsent
                }
                try {
                    consentInfo.requestConsentInfoUpdate(
                        activity,
                        params,
                        {
                            if (!isActivityUsable(activity)) {
                                onConsentGatheringCompleteListener.consentGatheringComplete(null)
                                return@requestConsentInfoUpdate
                            }
                            try {
                                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                                    onConsentGatheringCompleteListener.consentGatheringComplete(
                                        formError
                                    )
                                }
                            } catch (e: WindowManager.BadTokenException) {
                                Log.w(tag, "Consent dialog skipped due to invalid window token", e)
                                onConsentGatheringCompleteListener.consentGatheringComplete(null)
                            } catch (e: IllegalStateException) {
                                Log.w(tag, "Consent dialog skipped due to lifecycle state", e)
                                onConsentGatheringCompleteListener.consentGatheringComplete(null)
                            } catch (e: RuntimeException) {
                                Log.w(tag, "Consent dialog skipped due to runtime exception", e)
                                onConsentGatheringCompleteListener.consentGatheringComplete(null)
                            }
                        },
                        { requestConsentError ->
                            onConsentGatheringCompleteListener.consentGatheringComplete(
                                requestConsentError
                            )
                        }
                    )
                } catch (e: Exception) {
                    Log.w(tag, "requestConsentInfoUpdate failed safely", e)
                    onConsentGatheringCompleteListener.consentGatheringComplete(null)
                }
            }
            if (Looper.myLooper() == Looper.getMainLooper()) {
                requestConsent()
            } else {
                activity.runOnUiThread { requestConsent() }
            }
        }
    }

    fun showPrivacyOptionsForm(
        activity: Activity,
        onConsentFormDismissedListener: OnConsentFormDismissedListener
    ) {
        if (!activity.isFinishing && !(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed) && isActivityUsable(activity)) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                try {
                    UserMessagingPlatform.showPrivacyOptionsForm(
                        activity,
                        onConsentFormDismissedListener
                    )
                } catch (e: Exception) {
                    Log.w(tag, "showPrivacyOptionsForm failed safely", e)
                }
            } else {
                activity.runOnUiThread {
                    if (!isActivityUsable(activity)) return@runOnUiThread
                    try {
                        UserMessagingPlatform.showPrivacyOptionsForm(
                            activity,
                            onConsentFormDismissedListener
                        )
                    } catch (e: Exception) {
                        Log.w(tag, "showPrivacyOptionsForm failed safely", e)
                    }
                }
            }
        }
    }

    private fun ensureConsentInformation(
        activity: Activity,
        onReady: (ConsentInformation) -> Unit
    ) {
        val existing = consentInformation
        if (existing != null) {
            onReady(existing)
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            val info = synchronized(consentInfoLock) {
                consentInformation ?: UserMessagingPlatform.getConsentInformation(appContext).also {
                    consentInformation = it
                }
            }
            withContext(Dispatchers.Main) {
                if (!isActivityUsable(activity)) {
                    return@withContext
                }
                onReady(info)
            }
        }
    }

    private fun isActivityUsable(activity: Activity): Boolean {
        if (activity.isFinishing) return false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed) return false
        val owner = activity as? LifecycleOwner
        if (owner != null && !owner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) return false
        val decorView = activity.window?.decorView ?: return false
        if (!decorView.isAttachedToWindow) return false
        return true
    }

    companion object {
        @Volatile
        private var instance: GoogleMobileAdsConsentManager? = null

        fun getInstance(context: Context) =
            instance
                ?: synchronized(this) {
                    instance ?: GoogleMobileAdsConsentManager(context).also { instance = it }
                }
    }
}
