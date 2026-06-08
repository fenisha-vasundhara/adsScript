package com.messenger.phone.number.text.sms.service.apps.ads

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper

object NetworkObserver {

    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var isRegistered = false

    fun observeOnce(context: Context, onConnected: () -> Unit) {

        if (isRegistered) return

        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Handler(Looper.getMainLooper()).post {
                    onConnected()
                    unregister(context)
                }
            }
        }

        cm.registerDefaultNetworkCallback(networkCallback!!)
        isRegistered = true
    }

    fun unregister(context: Context) {
        try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            networkCallback?.let {
                cm.unregisterNetworkCallback(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            networkCallback = null
            isRegistered = false
        }
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}