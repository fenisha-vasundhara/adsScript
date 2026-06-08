package com.demo.adsmanage.viewmodel

import android.app.Application
import com.demo.adsmanage.Commen.Constants.mHEIGHT
import com.demo.adsmanage.Commen.Constants.mWIDTH
import com.demo.adsmanage.db.AppDatabase
import com.demo.adsmanage.db.data.LocalDataSource
import com.demo.adsmanage.db.executors.AppExecutors
import com.demo.adsmanage.helper.displayMetrics
import com.demo.adsmanage.billing.BillingClientLifecycle
import com.demo.adsmanage.billing.repository.DataRepository
//import com.vision.aftercall.sdk.ads.advanceAds.AppLifecycleObserver

abstract class AppSubscription : Application() {
//abstract class AppSubscription : AppLifecycleObserver() {

    companion object {
        private var isDebug = false
    }

    abstract fun getListTestDeviceIdSub(): List<String>

    abstract fun showTestIdAlertSub(): Boolean

    abstract fun AutosetTestDeviceIdSub(): Boolean

    abstract fun enableopenAdsSub(): Boolean

    abstract fun disablebackgroundAdsSub(): Boolean

    abstract fun getOpenAppAdIdSub(): String?


    override fun onCreate() {
        super.onCreate()
//        mHEIGHT =(displayMetrics.heightPixels / resources.displayMetrics.density).toInt()
//        mWIDTH =(displayMetrics.widthPixels / resources.displayMetrics.density).toInt()
    }

    private val executors = AppExecutors()
    private val database: AppDatabase
        get() = AppDatabase.getInstance(this)
    private val localDataSource: LocalDataSource
        get() = LocalDataSource.getInstance(executors, database)
    val billingClientLifecycle: BillingClientLifecycle
        get() = BillingClientLifecycle.getInstance(this)
    val repository: DataRepository
        get() = DataRepository.getInstance(localDataSource, billingClientLifecycle)
    fun initDebug(debug : Boolean) {
        isDebug = debug
    }
    fun isDebug() = isDebug
}
