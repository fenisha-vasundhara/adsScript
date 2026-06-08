package com.messenger.phone.number.text.sms.service.apps.helper

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.media.audiofx.EnvironmentalReverb
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import java.util.*

class JNPPermission {

    companion object {
        @JvmStatic
        fun requestPermission(mContext: Context?, permission: Array<String>?, REQUEST_CODE: Int) {
            ActivityCompat.requestPermissions((mContext as Activity?)!!, permission!!, REQUEST_CODE)
        }
    }
}