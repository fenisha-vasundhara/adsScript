package com.messenger.phone.number.text.sms.service.apps.helperClass

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.Settings
import android.view.View

object NetworkHelper {
    fun isOnline(mContext: Context): Boolean {
        var haveConnectedWifi = false
        var haveConnectedMobile = false
        val cm: ConnectivityManager = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        @SuppressLint("MissingPermission") val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.type === ConnectivityManager.TYPE_WIFI) {
                if (activeNetwork.isConnected) haveConnectedWifi = true
            } else if (activeNetwork.type === ConnectivityManager.TYPE_MOBILE) {
                if (activeNetwork.isConnected) haveConnectedMobile = true
            }
        }
        return haveConnectedWifi || haveConnectedMobile
    }
}