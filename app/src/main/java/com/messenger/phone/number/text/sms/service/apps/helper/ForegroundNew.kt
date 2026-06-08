package com.messenger.phone.number.text.sms.service.apps.helper

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log

class ForegroundNew : Application.ActivityLifecycleCallbacks {

    interface Listener {
        fun foreground()
        fun background()
    }

    companion object {
//        private val instance = ForegroundNew()

        val instance: ForegroundNew by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ForegroundNew()
        }

        fun init(application: Application) {
            application.registerActivityLifecycleCallbacks(instance)
        }

        fun addListener(listener: Listener) {
            instance.addListener(listener)
        }

        fun removeListener(listener: Listener) {
            instance.removeListener(listener)
        }

        fun isForeground(): Boolean {
            return instance.isForeground()
        }

        fun isBackground(): Boolean {
            return instance.isBackground()
        }

        fun removeAllListener() {
            instance.listeners.clear()
        }

    }

    val listeners: ArrayList<Listener> = ArrayList()
    var numStarted = 0

    private fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    private fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }


    private fun isForeground(): Boolean {
        return (numStarted > 0)
    }

    private fun isBackground(): Boolean {
        return (numStarted == 0)
    }

    private fun notifyForeground() {
        listeners.forEach { listener ->
            listener.foreground()
        }
    }

    private fun notifyBackground() {
        listeners.forEach { listener ->
            listener.background()
        }
    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityStarted(activity: Activity) {

        Log.d("", "onActivityStarted: ForegroundNew 1 <--------------> ${activity}")
//
//        //  Notify before increment
//        if (numStarted == 0)
//
//        numStarted++
//        notifyForeground()
        if (numStarted == 0) {
            numStarted++
            notifyForeground()
        } else {
            numStarted++
        }
    }

    override fun onActivityDestroyed(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityStopped(activity: Activity) {
        Log.d("", "onActivityStarted: ForegroundNew 2 <--------------> ${activity}")
//        numStarted--
//        //  Notify after decrement
//        if (numStarted == 0)
//            notifyBackground()
        numStarted--
        if (numStarted == 0) {
            Log.d("ForegroundNew", "notifyBackground() called")
            notifyBackground()
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

}