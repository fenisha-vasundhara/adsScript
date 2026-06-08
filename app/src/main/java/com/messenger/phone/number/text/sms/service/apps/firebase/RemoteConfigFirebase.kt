package com.messenger.phone.number.text.sms.service.apps.firebase

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.demo.adsmanage.Commen.Message_Interstial_Ad_Conversation_Click_Ex
import com.demo.adsmanage.Commen.log
import com.demo.adsmanage.Commen.newadsclass_vs_oldadsclass_MS_DE
import com.demo.adsmanage.Commen.oldclass_vs_newclass_SUB
import com.demo.adsmanage.basemodule.BaseSharedPreferences
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.messenger.phone.number.text.sms.service.apps.CommanClass.baseConfig
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.firebaseConfig
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isOnline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
//import com.vision.aftercall.sdk.ads.CallerAdMode
//import com.vision.aftercall.sdk.core.CallerModule
class RemoteConfigFirebase(private val context: Context, val scope: () -> Unit) {

    val TAG = "AdsManageRemoteConfig"
    private val configSettings: FirebaseRemoteConfigSettings
        get() {
            return FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(0)
                .build()
        }
    private val mFirebaseRemoteConfig: FirebaseRemoteConfig
        get() {
            return FirebaseRemoteConfig.getInstance()
        }

    init {
//        Handler(Looper.getMainLooper()).post {
        if (context.isOnline()) {
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
                    mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            firebaseConfig.message_show_Interstitial_Ad =
                                mFirebaseRemoteConfig.getBoolean("message_show_Interstitial_Ad")


                            context.config.subscriptionModelStart =
                                mFirebaseRemoteConfig.getBoolean("Subscription_Mode_Me")

                            context.config.Get_Offer_Config_MS =
                                mFirebaseRemoteConfig.getString("Get_Offer_Config_MS")

                            firebaseConfig.Message_show_open_Ad =
                                mFirebaseRemoteConfig.getBoolean("Message_show_open_Ad")
                            firebaseConfig.Message_ABTesting_BannerAndNative = "2"
                            firebaseConfig.Message_show_Rewarded_Ad =
                                mFirebaseRemoteConfig.getBoolean("Message_show_Rewarded_Ad")
                            firebaseConfig.Message_Home_Native_Ad =
                                mFirebaseRemoteConfig.getBoolean("Message_Home_Native_Ad")
//                            firebaseConfig.Message_Subscription_Backpress_InterstitialAd_show_or_not = mFirebaseRemoteConfig.getString("Message_Subscription_Backpress_InterstitialAd_show_or_not")
                            firebaseConfig.Message_Subscription_Backpress_InterstitialAd_show_or_not =
                                "1"
                            Message_Interstial_Ad_Conversation_Click_Ex =
                                firebaseConfig.Message_Subscription_Backpress_InterstitialAd_show_or_not
                            firebaseConfig.Message_Interstial_Ad_Conversation_Click = "2"
                            firebaseConfig.Message_Sendmessageactivity_Bannerad_show_or_hide =
                                mFirebaseRemoteConfig.getString("Message_Sendmessageactivity_Bannerad_show_or_hide_Experiment")
                            context.config.isadativebennarload =
                                mFirebaseRemoteConfig.getBoolean("Message_Home_Native_Ad")
                            context.config.isadativebennarloadlang =
                                mFirebaseRemoteConfig.getBoolean("Message_Language_Native_Ad")
                            context.config.Message_full_App_Font_Style = "2"
                            context.config.Message_Send_Activity = "2"
                            context.config.Message_Home_screen_Native_Ad_AB =
                                mFirebaseRemoteConfig.getString("Message_Home_screen_Native_Ad")

                            context.config.US_subscription_flow_MS = "1"

                            context.config.Sub_show_or_not =
                                mFirebaseRemoteConfig.getString("Sub_show_vs_not_show_only_non_US_MS")
//                            context.config.OnBordingFlow_AB ="3"
                            context.config.OnBordingFlow_AB =
//                                "2"
                                mFirebaseRemoteConfig.getString("OnBordingExp_AB")


                            FirebaseAnalytics.getInstance(context).setUserProperty("exp_first_flow", context.config.OnBordingFlow_AB)


                            context.config.homeBannerId_AB = mFirebaseRemoteConfig.getString("Home_banner_exp_key")


                            context.config.Message_Home_screen_Top_Native_Ad_Show =
                                mFirebaseRemoteConfig.getBoolean("Home_screen_Top_Native_Ad")
                            context.config.Language_Screen_banner_vs_native = "1"
                            context.config.NewHomeScreen_TopBanner_VS_BottomBanner = "1"

                            context.config.Home_App_OpenAD_VS_InterAd_MS = "1"

                            context.config.AfterCall_Banner_VS_Native_MS =
                                mFirebaseRemoteConfig.getString("AfterCall_Banner_VS_Native_MS")

                            context.config.AfterCall_Collapsible_Vs_Native_Vs_Random_MS =
                                mFirebaseRemoteConfig.getString("AfterCall_Collapsible_Vs_Native_Vs_Random_MS")


                            context.config.First_Language_Banner_Vs_Native_Vs_Random_MS =
                                mFirebaseRemoteConfig.getString("First_Language_Banner_Vs_Native_Vs_Random_MS")

                            context.config.oldclass_vs_newclass = "2"

                            oldclass_vs_newclass_SUB = context.config.oldclass_vs_newclass


                            context.baseConfig.oldopenadsflow_vs_newopenadsflow = "2"
                            context.baseConfig.oldbanneradsflow_vs_newbanneradsflow = "1"

                            context.baseConfig.NewOnboardingFlow_VS_OldOnboardingFlow_MS =
                                mFirebaseRemoteConfig.getString("OldOnboardingFlow_VS_NewOnboardingFlow_MS")

                            context.baseConfig.Subscription_Show_VS_NotShow_MS = "1"

                            context.baseConfig.ZopIconget_Ms =
                                mFirebaseRemoteConfig.getString("Astrozop_VS_Criczop_VS_Random_MS")


                            context.baseConfig.Home_banner_experiment =
                                mFirebaseRemoteConfig.getString("Home_Half_banner_VS_Small_MS")

                            context.baseConfig.Single_Vs_Multiple_Ads_id =
                                mFirebaseRemoteConfig.getString("Single_Vs_Multiple_Ads_id_MS")

                            context.baseConfig.isFeedBackOptionShow =
                                mFirebaseRemoteConfig.getBoolean("isFeedBackOptionShow")


                            context.baseConfig.After_call_banner_vs_native = "1"

                            "OnBordingFlow_AB 1 <----------> ${context.baseConfig.OnBordingFlow_AB}".log()
//                            "OnBordingFlow_AB 1fff .  <----------> ${  mFirebaseRemoteConfig.getString("OnBording3Flow_AB")}".log()
   "homeBannerId_AB 1 <----------> ${context.baseConfig.homeBannerId_AB}".log()
                            "homeBannerId_AB 1fff .  <----------> ${  mFirebaseRemoteConfig.getString("Home_banner_exp_key")}".log()

//                            Toast.makeText(context, "Flow_AB = ${context.baseConfig.OnBordingFlow_AB}", Toast.LENGTH_SHORT).show()
//                            Toast.makeText(context, "Home_banner = ${context.baseConfig.homeBannerId_AB}", Toast.LENGTH_SHORT).show()

                            context.baseConfig.Home_Banner_VS_Native_VS_Random =
                                mFirebaseRemoteConfig.getString("Home_Banner_VS_Native_VS_Random_MS")


                            context.baseConfig.PermissionOnboardingFlow_VS_ExperimentOnboardingFlow_MS = "1"

                            context.baseConfig.newadsclass_vs_oldadsclass_MS = "2"

//                            oldadsclass_vs_newadsclass_MS

                            newadsclass_vs_oldadsclass_MS_DE =
                                context.baseConfig.newadsclass_vs_oldadsclass_MS


                            context.config.setABHomeActivityPref = "2"

                            context.config.Interstitial_Ads_Intro_VS_Default_MS =
                                mFirebaseRemoteConfig.getString("Interstitial_Home_Default_Button_Show_VS_Not")

                            context.config.home_overlay_banner =
                                mFirebaseRemoteConfig.getString("home_overlay_banner")
                            context.config.home_overlay_permission_dialog =
                                mFirebaseRemoteConfig.getString("home_overlay_permission_dialog")



                            context.config.Interstitial_Notification_Back_Press_Show_VS_Not_MS =
                                "1"

                            context.config.Message_splash_ads_dialog_show =
                                mFirebaseRemoteConfig.getBoolean("Message_splash_ads_dialog_show")

                            context.config.All_Ads_On =
                                mFirebaseRemoteConfig.getBoolean("All_Ads_On")
//                                Log.e("FFF", ": context.config.All_Ads_On____ .   ${context.config.All_Ads_On} " )
                            context.config.Misscall_Aftercall_OFF =
                                mFirebaseRemoteConfig.getBoolean("Misscall_Aftercall_OFF")


                            context.config.afterCall_Delay =
                                mFirebaseRemoteConfig.getLong("Aftercall_Delay")

                            context.config.aftercall_Priority =
                                mFirebaseRemoteConfig.getString("Aftercall_Priority").toIntOrNull() ?: 0

                            context.config.aftercall_Sequence =
                                mFirebaseRemoteConfig.getString("Aftercall_Sequence").toIntOrNull() ?: 123
                            Log.d(
                                "TAG",
                                "onCreate:response:firebase----- ${context.config.aftercall_Priority}"
                            )

                            com.demo.adsmanage.Commen.All_Ads_On = context.config.All_Ads_On

                            if (!context.config.All_Ads_On) {
                                context.config.isSplashBannerAdOn = false
                                context.config.isHomeBannerAdOn = false
                                context.config.isSearchBannerAdOn = false
                                context.config.isAftercallBannerAdOn = false
                                context.config.isAftercallNativeAdOn = false
                                context.config.isLanguageNativeAdOn = false
                                context.config.isChatNativeAdOn = false
                                context.config.isProfileNativeAdOn = false
                                context.config.isProfileNativeAdOn = false

                            } else {
                                context.config.isSplashBannerAdOn =
                                    mFirebaseRemoteConfig.getBoolean("Message_isSplashBannerAdOn")
                                context.config.isHomeBannerAdOn =
                                    mFirebaseRemoteConfig.getBoolean("Message_isHomeBannerAdOn")
                                context.config.isSearchBannerAdOn =
                                    mFirebaseRemoteConfig.getBoolean("Message_isSearchBannerAdOn")
                                context.config.isAftercallBannerAdOn =
                                    mFirebaseRemoteConfig.getBoolean("Message_isAftercallBannerAdOn")
                                context.config.isLanguageNativeAdOn =
                                    mFirebaseRemoteConfig.getBoolean("Message_isLanguageNativeAdOn")
                                context.config.AfterCall_Native_Admob_VS_Applovin =
                                    mFirebaseRemoteConfig.getString("AfterCall_Native_Admob_VS_Applovin")
                                context.config.isChatNativeAdOn =
                                    mFirebaseRemoteConfig.getBoolean("Message_isChatNativeAdOn")
                                context.config.isProfileNativeAdOn =
                                    mFirebaseRemoteConfig.getBoolean("Message_isProfileNativeAdOn")
                            }
                            Log.d(
                                TAG,
                                "context.config.AfterCall_Native_Admob_VS_Applovin--->: ${context.config.AfterCall_Native_Admob_VS_Applovin}"
                            )

           /*                 if (BaseSharedPreferences(context).mIS_SUBSCRIBED) {
                                CallerModule.setAdMode(CallerAdMode.NONE)
                            } else {

                                CallerModule.setAdMode(if (context.config.isAftercallNativeAdOn) CallerAdMode.NATIVE else CallerAdMode.NONE)
                            }*/
                            // After-call SDK removed; no SDK ad mode sync.

                            context.config.Backpresscount =
                                mFirebaseRemoteConfig.getLong("SendMessage_Backpress_Count_MS")

                            context.config.Banner_Home_Old_App_VS_New_App =
                                mFirebaseRemoteConfig.getString("Banner_Home_Old_App_VS_New_App_MS")

//                            context.config.Home_Banner_VS_Collapsible =
//                                mFirebaseRemoteConfig.getString("Home_Banner_VS_Collapsible_Me")

                            context.config.Home_Banner_VS_Collapsible = "2"


                            context.config.Language_Screen_banner_vs_native_show_or_hide =
                                mFirebaseRemoteConfig.getBoolean("Language_Screen_banner_vs_native_show_or_hide")

                            context.config.openad_baground =
                                mFirebaseRemoteConfig.getBoolean("Open_Background")

                            context.config.openad_baground_count =
                                mFirebaseRemoteConfig.getLong("Open_Background_Count")

//                            context.config.Message_Home_screen_Native_Ad_Card_And_Normal =
//                                mFirebaseRemoteConfig.getString("Language_And_Main_Screen_banner_vs_native")

                            context.config.Message_Home_screen_Native_Ad_Card_And_Normal = "2"


                            ///new firebase

                            context.config.Banner_Home_Top =
                                mFirebaseRemoteConfig.getBoolean("Banner_Home_Top_Me")

//                            context.config.Default_Flow_OLD_VS_NEW =
//                                mFirebaseRemoteConfig.getString("Default_Flow_OLD_VS_NEW_Me")
//                            CoroutineScope(Dispatchers.Main).launch {
//                                context.toastMess("firebase event log <------> ${context.config.Default_Flow_OLD_VS_NEW}")
//                            }
//                            "firebase event log <-----------------> ${context.config.Default_Flow_OLD_VS_NEW}".log()


                            context.config.Banner_Home_Bottom =
                                mFirebaseRemoteConfig.getBoolean("Banner_Home_Bottom_Me")

                            context.config.Banner_Language_Bottom =
                                mFirebaseRemoteConfig.getBoolean("Banner_Language_Bottom_Me")


                            context.config.Banner_Bottom_VS_Top =
                                mFirebaseRemoteConfig.getString("Banner_Home_Bottom_VS_Top")

                            context.config.Interstitial_SendMessage_Home =
                                mFirebaseRemoteConfig.getBoolean("Interstitial_SendMessage_Home_Me")

                            firebaseConfig.Message_Interstitial_Ad_Count =
                                mFirebaseRemoteConfig.getLong("Message_Home_InterstitialAds_Show_Count")


                            firebaseConfig.Reward_Sendmessage_Schedulemessage =
                                mFirebaseRemoteConfig.getBoolean("Reward_Sendmessage_Schedulemessage_Me")
                            firebaseConfig.Reward_Setting_BackupRestore =
                                mFirebaseRemoteConfig.getBoolean("Reward_Setting_BackupRestore_Me")
                            firebaseConfig.Reward_Setting_Schedulemessage =
                                mFirebaseRemoteConfig.getBoolean("Reward_Setting_Schedulemessage_Me")
                            firebaseConfig.Reward_Setting_Signature =
                                mFirebaseRemoteConfig.getBoolean("Reward_Setting_Signature_Me")
                            firebaseConfig.Reward_Setting_Swipmotion =
                                mFirebaseRemoteConfig.getBoolean("Reward_Setting_Swipmotion_Me")
                            firebaseConfig.Interstitial_Subscription_Home =
                                mFirebaseRemoteConfig.getBoolean("Interstitial_Subscription_Home_Me")
//                            Interstitial_Subscription_Home_New =
//                                firebaseConfig.Interstitial_Subscription_Home

                            context.config.Intro_Home_USA_Not_Show_Interstitial_pref =
                                mFirebaseRemoteConfig.getString("Intro_Home_USA_Not_Show_Interstitial")

                        }
                        scope()
                    }.addOnFailureListener { exception ->
                        Log.d(TAG, "AdsManageRemoteConfig: ${exception.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching remote config: ${e.message}", e)
            }
        } else {
            scope()
            Log.d(TAG, "AdsManageRemoteConfig net OFF: ")
        }
//        }

    }

}
