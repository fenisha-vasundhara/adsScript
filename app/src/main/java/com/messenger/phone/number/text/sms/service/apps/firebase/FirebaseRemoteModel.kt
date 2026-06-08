package com.messenger.phone.number.text.sms.service.apps.firebase

data class FirebaseRemoteModel(
    var message_show_Interstitial_Ad: Boolean = true,
    var Message_ABTesting_BannerAndNative: String = "2",
    var Message_show_open_Ad: Boolean = true,
    var Message_Interstitial_Ad_Count: Long = 6,
    var Message_show_Rewarded_Ad: Boolean = true,
    var Message_Home_Native_Ad: Boolean = true,
    var Message_Language_Native_Ad: Boolean = true,
    var Message_Interstial_Ad_Conversation_Click: String = "1",
    var Message_Sendmessageactivity_Bannerad_show_or_hide: String = "1",
    var Message_Subscription_Backpress_InterstitialAd_show_or_not: String = "1",
    var Interstitial_SendMessage_Home: Boolean = true,
    var Reward_Sendmessage_Schedulemessage: Boolean = true,
    var Reward_Setting_BackupRestore: Boolean = true,
    var Reward_Setting_Schedulemessage: Boolean = true,
    var Reward_Setting_Swipmotion: Boolean = true,
    var Reward_Setting_Signature: Boolean = true,
    var Interstitial_Subscription_Home: Boolean = true
)