package com.demo.adsmanage.NewAdsSDK.rewarded.callbacks

interface RewardedOnShowCallBack {
    fun onAdDismissedFullScreenContent() {}
    fun onAdFailedToShow()
    fun onAdShowedFullScreenContent() {}
    fun onAdImpression() {}
    fun onUserEarnedReward()
}