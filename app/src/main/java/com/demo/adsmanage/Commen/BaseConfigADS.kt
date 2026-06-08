package com.demo.adsmanage.Commen

import android.content.Context

open class BaseConfigADS(context: Context) {

    protected val prefs = context.getSharedPrefs()

    companion object {
        fun newInstance(context: Context) = BaseConfigADS(context)
    }

    var SubBuyADS: String
        get() = prefs.getString("SubBuyADS", "Nothing").toString()
        set(lens) = prefs.edit().putString("SubBuyADS", lens).apply()
}