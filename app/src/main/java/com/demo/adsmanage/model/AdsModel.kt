package com.demo.adsmanage.model

import com.google.gson.annotations.SerializedName

data class AdsModel(

	@field:SerializedName("App_Changing")
	val appChanging: AppChanging? = null
)

data class FaceBook(
	@field:SerializedName("fb_Interstitial")
	val fbInterstitial: String? = null,

	@field:SerializedName("fb_RewardedAds")
	val fbRewardedAds: String? = null,

	@field:SerializedName("fb_NativeAds")
	val fbNativeAds: String? = null,

	@field:SerializedName("fb_Banner")
	val fbBanner: String? = null
)

data class AppChanging(

	@field:SerializedName("test_Ads_Show")
	val testAdsShow: Boolean? = null,

	@field:SerializedName("Is_Show_AdmobAds")
	val misShowAdmobAds: Boolean? = null,

	@field:SerializedName("is_home_nativeShow")
	val misHomeNativeShow: Boolean? = null,

	@field:SerializedName("is_setting_nativeShow")
	val misSettingNativeShow: Boolean? = null,

	@field:SerializedName("is_creation_nativeShow")
	val misCreationNativeShow: Boolean? = null,

	@field:SerializedName("Interstitial_Click_CountShow")
	val interstitialClickCountShow: Int? = null,

	@field:SerializedName("FaceBook")
	val faceBook: FaceBook? = null,

	@field:SerializedName("Admob")
	val admob: Admob? = null,

	@field:SerializedName("is_ProgressShow")
	val misProgressShow: Boolean? = null,

	@field:SerializedName("Is_BackAdsShow")
	val misBackAdsShow: Boolean? = null,

	@field:SerializedName("Is_ABTest")
	val mIs_ABTest: Int? = null
)

data class Admob(

	@field:SerializedName("ad_NativeAds")
	val adNativeAds: String? = null,

	@field:SerializedName("ad_Interstitial")
	val adInterstitial: String? = null,

	@field:SerializedName("ad_Banner")
	val adBanner: String? = null,

	@field:SerializedName("ad_AppOpen")
	val adAppOpen: String? = null,

	@field:SerializedName("ad_RewardedAds")
	val adRewardedAds: String? = null
)
