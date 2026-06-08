package com.demo.adsmanage.helper

import com.demo.adsmanage.Commen.Constants.Noads
import com.demo.adsmanage.Commen.Constants.editor
import com.demo.adsmanage.Commen.Constants.mPreferences
import com.google.gson.Gson

object MySharedPreferences {

     var AD_Interstitial:String?
        get(){
          return mPreferences!!.getString("ad_Interstitial", Noads)
        }
        set(string) {
            editor!!.putString("ad_Interstitial", string!!)
            editor!!.commit()
        }
    var AD_NativeAds:String?
        get() {
            return mPreferences!!.getString("ad_NativeAds", Noads)
        }
        set(string) {
            editor!!.putString("ad_NativeAds", string!!)
            editor!!.commit()
        }
      var AD_Banner:String?
        get() = mPreferences!!.getString("ad_Banner", Noads)
        set(string) {
            editor!!.putString("ad_Banner", string!!)
            editor!!.commit()
        }
    var AD_AppOpen:String?
        get() = mPreferences!!.getString("ad_AppOpen", Noads)
        set(string) {
            editor!!.putString("ad_AppOpen", string!!)
            editor!!.commit()
        }
    var AD_RewardedAds:String?
        get() = mPreferences!!.getString("ad_RewardedAds", Noads)
        set(string) {
            editor!!.putString("ad_RewardedAds", string!!)
            editor!!.commit()
        }
    var FB_Interstitial:String?
        get() = mPreferences!!.getString("fb_Interstitial", Noads)
        set(string) {
            editor!!.putString("fb_Interstitial", string!!)
            editor!!.commit()
        }
    var FB_Banner:String?
        get() = mPreferences!!.getString("fb_Banner", Noads)
        set(string) {
            editor!!.putString("fb_Banner", string!!)
            editor!!.commit()
        }
    var FB_NativeAds:String?
        get() = mPreferences!!.getString("fb_NativeAds", Noads)
        set(string) {
            editor!!.putString("fb_NativeAds", string!!)
            editor!!.commit()
        }
    var FB_RewardedAds:String?
        get() = mPreferences!!.getString("fb_RewardedAds", Noads)
        set(string) {
            editor!!.putString("fb_RewardedAds", string!!)
            editor!!.commit()
        }
    var Interstitial_CountShow:Int?
        get() = mPreferences!!.getInt("Interstitial_Click_CountShow", 1)
        set(number) {
            editor!!.putInt("Interstitial_Click_CountShow", number!!)
            editor!!.commit()
        }
}