package com.messenger.phone.number.text.sms.service.apps.inAppUpdate

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability


class InAppUpdate(activity: Activity) : InstallStateUpdatedListener {

    private var version: Int? = null
    var appUpdateManager: AppUpdateManager? = null
    private val MY_REQUEST_CODE = 500
    private var parentActivity: Activity = activity
    private var currentType = AppUpdateType.FLEXIBLE

    init {


        try {
            val pInfo: PackageInfo = activity.packageManager.getPackageInfo(activity.packageName, 0)
            version = pInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        version?.let { versioncode ->

            appUpdateManager = AppUpdateManagerFactory.create(parentActivity)
            appUpdateManager?.appUpdateInfo?.addOnSuccessListener { info ->
                if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    if (info.availableVersionCode() > versioncode) {
                        startUpdate(info, AppUpdateType.FLEXIBLE)
                    }
                } else {
                }
            }
            appUpdateManager?.registerListener(this)

        }
    }

    private fun startUpdate(info: AppUpdateInfo, type: Int) {
        try {
            appUpdateManager?.startUpdateFlowForResult(info, type, parentActivity, MY_REQUEST_CODE)
            currentType = type
        } catch (e: Exception) {

        }
    }

    fun onResume() {
        appUpdateManager?.appUpdateInfo?.addOnSuccessListener { info ->
            if (currentType == AppUpdateType.FLEXIBLE) {
                // If the update is downloaded but not installed, notify the user to complete the update.
                if (info.installStatus() == InstallStatus.DOWNLOADED)
                    flexibleUpdateDownloadCompleted()
            } else if (currentType == AppUpdateType.IMMEDIATE) {
                // for AppUpdateType.IMMEDIATE only, already executing updater
                if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    startUpdate(info, AppUpdateType.IMMEDIATE)
                }
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != AppCompatActivity.RESULT_OK) {
                // If the update is cancelled or fails, you can request to start the update again.
                Log.e("ERROR", "Update flow failed! Result code: $resultCode")
            }
        }
    }

    private fun flexibleUpdateDownloadCompleted() {
//        Snackbar.make(
//            parentActivity.findViewById(android.R.id.content),
//            "An update has just been downloaded.",
//            Snackbar.LENGTH_INDEFINITE
//        ).apply {
//            setAction("RESTART") {
//                appUpdateManager?.completeUpdate()
//            }
//            setActionTextColor(Color.WHITE)
//            show()
//        }
        appUpdateManager?.completeUpdate()
    }

    fun onDestroy() {
        appUpdateManager?.unregisterListener(this)
    }

    override fun onStateUpdate(state: InstallState) {
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            flexibleUpdateDownloadCompleted()
        }
    }

}