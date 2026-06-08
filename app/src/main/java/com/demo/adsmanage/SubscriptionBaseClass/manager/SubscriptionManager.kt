package com.demo.adsmanage.SubscriptionBaseClass.manager

import android.content.Context
import com.demo.adsmanage.SubscriptionBaseClass.manager.PreferencesKeys

class SubscriptionManager(val mContext: Context) {

    /**
     * create data store object with name
     */
    private val dataStore = mContext.getSharedPreferences("SubscriptionLocal", Context.MODE_PRIVATE)


    fun setMonthPrice(price: String) {
        dataStore.edit().putString(PreferencesKeys.MONTH_PRICE, price).apply()
    }

    fun setYearPrice(price: String) {
        dataStore.edit().putString(PreferencesKeys.YEAR_PRICE, price).apply()
    }
    fun setSixMonthrPrice(price: String) {
        dataStore.edit().putString(PreferencesKeys.SIXMonth_PRICE, price).apply()
    }

    fun setMonthPrice(price: Long) {
        dataStore.edit().putLong(PreferencesKeys.MONTH_PRICE_MICRO, price).apply()
    }

    fun setYearPrice(price: Long) {
        dataStore.edit().putLong(PreferencesKeys.YEAR_PRICE_MICRO, price).apply()
    }
    fun setSixPrice(price: Long) {
        dataStore.edit().putLong(PreferencesKeys.YSIX_PRICE_MICRO, price).apply()
    }

    fun setCurrencyCode(code: String) {
        dataStore.edit().putString(PreferencesKeys.CURRENCY_CODE, code).apply()
    }

    fun setSubscribe(subscribe: Boolean) {
        dataStore.edit().putBoolean(PreferencesKeys.SUBSCRIBE, subscribe).apply()
    }

    fun setMonthTrialPeriod(trial: String) {
        dataStore.edit().putString(PreferencesKeys.MONTH_TRIAL_PERIOD, trial).apply()
    }

    fun setYearTrialPeriod(trial: String) {
        dataStore.edit().putString(PreferencesKeys.YEAR_TRIAL_PERIOD, trial).apply()
    }
    fun setSixTrialPeriod(trial: String) {
        dataStore.edit().putString(PreferencesKeys.SIX_TRIAL_PERIOD, trial).apply()
    }

    fun setEventEnable(event: Boolean) {
        dataStore.edit().putBoolean(PreferencesKeys.KEY_EVENT_ADDED, event).apply()
    }

    fun getString(key : String,default : String = "") = dataStore.getString(key,default)!!

    fun geBoolean(key : String,default : Boolean = false) = dataStore.getBoolean(key,default)!!
//    fun geBoolean(key : String,default : Boolean = false) = false

    fun getLong(key : String,default : Long = 0L) = dataStore.getLong(key,default)!!

}