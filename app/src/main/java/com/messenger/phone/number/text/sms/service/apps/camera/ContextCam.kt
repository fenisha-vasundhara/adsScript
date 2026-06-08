package com.messenger.phone.number.text.sms.service.apps.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.documentfile.provider.DocumentFile
import com.messenger.phone.number.text.sms.service.apps.CommanClass.GENERIC_PERM_HANDLER
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.simplemobiletools.commons.R
import com.simplemobiletools.commons.activities.BaseSimpleActivity
import com.simplemobiletools.commons.extensions.baseConfig
import com.simplemobiletools.commons.extensions.createAndroidSAFFile
import com.simplemobiletools.commons.extensions.createDocumentUriFromRootTree
import com.simplemobiletools.commons.extensions.createDocumentUriUsingFirstParentTreeUri
import com.simplemobiletools.commons.extensions.createSAFFileSdk30
import com.simplemobiletools.commons.extensions.getAndroidSAFUri
import com.simplemobiletools.commons.extensions.getDocumentFile
import com.simplemobiletools.commons.extensions.getDoesFilePathExist
import com.simplemobiletools.commons.extensions.getFilenameFromPath
import com.simplemobiletools.commons.extensions.getPermissionString
import com.simplemobiletools.commons.extensions.hasPermission
import com.simplemobiletools.commons.extensions.isAccessibleWithSAFSdk30
import com.simplemobiletools.commons.extensions.isRestrictedSAFOnlyRoot
import com.simplemobiletools.commons.extensions.needsStupidWritePermissions
import com.simplemobiletools.commons.extensions.showErrorToast
import com.simplemobiletools.commons.extensions.showFileCreateError
import com.simplemobiletools.commons.helpers.PERMISSION_CALL_PHONE
import com.simplemobiletools.commons.helpers.PERMISSION_CAMERA
import com.simplemobiletools.commons.helpers.PERMISSION_GET_ACCOUNTS
import com.simplemobiletools.commons.helpers.PERMISSION_MEDIA_LOCATION
import com.simplemobiletools.commons.helpers.PERMISSION_POST_NOTIFICATIONS
import com.simplemobiletools.commons.helpers.PERMISSION_READ_CALENDAR
import com.simplemobiletools.commons.helpers.PERMISSION_READ_CALL_LOG
import com.simplemobiletools.commons.helpers.PERMISSION_READ_CONTACTS
import com.simplemobiletools.commons.helpers.PERMISSION_READ_MEDIA_AUDIO
import com.simplemobiletools.commons.helpers.PERMISSION_READ_MEDIA_IMAGES
import com.simplemobiletools.commons.helpers.PERMISSION_READ_MEDIA_VIDEO
import com.simplemobiletools.commons.helpers.PERMISSION_READ_PHONE_STATE
import com.simplemobiletools.commons.helpers.PERMISSION_READ_SMS
import com.simplemobiletools.commons.helpers.PERMISSION_READ_STORAGE
import com.simplemobiletools.commons.helpers.PERMISSION_RECORD_AUDIO
import com.simplemobiletools.commons.helpers.PERMISSION_SEND_SMS
import com.simplemobiletools.commons.helpers.PERMISSION_WRITE_CALENDAR
import com.simplemobiletools.commons.helpers.PERMISSION_WRITE_CALL_LOG
import com.simplemobiletools.commons.helpers.PERMISSION_WRITE_CONTACTS
import com.simplemobiletools.commons.helpers.PERMISSION_WRITE_STORAGE
import com.simplemobiletools.commons.helpers.isQPlus
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val FLASH_OFF = 0
const val FLASH_ON = 1
const val FLASH_AUTO = 2
const val FLASH_ALWAYS_ON = 3

fun Context.getFileOutputStreamSync(
    path: String,
    mimeType: String,
    parentDocumentFile: DocumentFile? = null,
): OutputStream? {
    val targetFile = File(path)

    return when {
        isRestrictedSAFOnlyRoot(path) -> {
            val uri = getAndroidSAFUri(path)
            if (!getDoesFilePathExist(path)) {
                createAndroidSAFFile(path)
            }
            applicationContext.contentResolver.openOutputStream(uri, "wt")
        }

        needsStupidWritePermissions(path) -> {
            var documentFile = parentDocumentFile
            if (documentFile == null) {
                if (getDoesFilePathExist(targetFile.parentFile.absolutePath)) {
                    documentFile = getDocumentFile(targetFile.parent)
                } else {
                    documentFile = getDocumentFile(targetFile.parentFile.parent)
                    documentFile = documentFile!!.createDirectory(targetFile.parentFile.name)
                        ?: getDocumentFile(targetFile.parentFile.absolutePath)
                }
            }

            if (documentFile == null) {
                val casualOutputStream = createCasualFileOutputStream(targetFile)
                return if (casualOutputStream == null) {
                    showFileCreateError(targetFile.parent)
                    null
                } else {
                    casualOutputStream
                }
            }

            try {
                val uri = if (getDoesFilePathExist(path)) {
                    createDocumentUriFromRootTree(path)
                } else {
                    documentFile.createFile(mimeType, path.getFilenameFromPath())!!.uri
                }
                applicationContext.contentResolver.openOutputStream(uri, "wt")
            } catch (e: Exception) {
                showErrorToast(e)
                null
            }
        }

        isAccessibleWithSAFSdk30(path) -> {
            try {
                val uri = createDocumentUriUsingFirstParentTreeUri(path)
                if (!getDoesFilePathExist(path)) {
                    createSAFFileSdk30(path)
                }
                applicationContext.contentResolver.openOutputStream(uri, "wt")
            } catch (e: Exception) {
                null
            } ?: createCasualFileOutputStream(targetFile)
        }

        else -> return createCasualFileOutputStream(targetFile)
    }
}

fun Context.showFileCreateError(path: String) {
    val error = String.format(getString(R.string.could_not_create_file), path)
    baseConfig.sdTreeUri = ""
    showErrorToast(error)
}

private fun createCasualFileOutputStream(activity: Activity, targetFile: File): OutputStream? {
    if (targetFile.parentFile?.exists() == false) {
        targetFile.parentFile?.mkdirs()
    }

    return try {
        FileOutputStream(targetFile)
    } catch (e: Exception) {
        activity.showErrorToast(e)
        null
    }
}

private fun Context.createCasualFileOutputStream(targetFile: File): OutputStream? {
    if (targetFile.parentFile?.exists() == false) {
        targetFile.parentFile?.mkdirs()
    }

    return try {
        FileOutputStream(targetFile)
    } catch (e: Exception) {
        showErrorToast(e)
        null
    }
}

fun Context.getPermissionStringCam(id: Int) = when (id) {
    PERMISSION_READ_STORAGE -> Manifest.permission.READ_EXTERNAL_STORAGE
    PERMISSION_WRITE_STORAGE -> Manifest.permission.WRITE_EXTERNAL_STORAGE
    PERMISSION_CAMERA -> Manifest.permission.CAMERA
    PERMISSION_RECORD_AUDIO -> Manifest.permission.RECORD_AUDIO
    PERMISSION_READ_CONTACTS -> Manifest.permission.READ_CONTACTS
    PERMISSION_WRITE_CONTACTS -> Manifest.permission.WRITE_CONTACTS
    PERMISSION_READ_CALENDAR -> Manifest.permission.READ_CALENDAR
    PERMISSION_WRITE_CALENDAR -> Manifest.permission.WRITE_CALENDAR
    PERMISSION_CALL_PHONE -> Manifest.permission.CALL_PHONE
    PERMISSION_READ_CALL_LOG -> Manifest.permission.READ_CALL_LOG
    PERMISSION_WRITE_CALL_LOG -> Manifest.permission.WRITE_CALL_LOG
    PERMISSION_GET_ACCOUNTS -> Manifest.permission.GET_ACCOUNTS
    PERMISSION_READ_SMS -> Manifest.permission.READ_SMS
    PERMISSION_SEND_SMS -> Manifest.permission.SEND_SMS
    PERMISSION_READ_PHONE_STATE -> Manifest.permission.READ_PHONE_STATE
    PERMISSION_MEDIA_LOCATION -> if (isQPlus()) Manifest.permission.ACCESS_MEDIA_LOCATION else ""
    PERMISSION_POST_NOTIFICATIONS -> Manifest.permission.POST_NOTIFICATIONS
    PERMISSION_READ_MEDIA_IMAGES -> Manifest.permission.READ_MEDIA_IMAGES
    PERMISSION_READ_MEDIA_VIDEO -> Manifest.permission.READ_MEDIA_VIDEO
    PERMISSION_READ_MEDIA_AUDIO -> Manifest.permission.READ_MEDIA_AUDIO
    PERMISSION_READ_MEDIA_VISUAL_USER_SELECTED -> Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
    PERMISSION_ACCESS_COARSE_LOCATION -> Manifest.permission.ACCESS_COARSE_LOCATION
    PERMISSION_ACCESS_FINE_LOCATION -> Manifest.permission.ACCESS_FINE_LOCATION
    PERMISSION_READ_MEDIA_VISUAL_USER_SELECTED -> Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
    else -> ""
}

fun Activity.handlePartialMediaPermissions(
    permissionIds: Collection<Int>,
    force: Boolean = false,
    callback: (granted: Boolean) -> Unit,
) {
    actionOnPermission = null
    if (isUpsideDownCakePlus()) {
        if (hasPermission(PERMISSION_READ_MEDIA_VISUAL_USER_SELECTED) && !force) {
            callback(true)
        } else {
            isAskingPermissions = true
            actionOnPermission = callback
            Log.d("", "handlePartialMediaPermissions: <-----------> ${permissionIds.map { getPermissionStringCam(it) }}")
            ActivityCompat.requestPermissions(this, permissionIds.map { getPermissionStringCam(it) }.toTypedArray(), GENERIC_PERM_HANDLER)
        }
    } else {
        if (hasAllPermissions(permissionIds)) {
            callback(true)
        } else {
            isAskingPermissions = true
            actionOnPermission = callback
            ActivityCompat.requestPermissions(this, permissionIds.map { getPermissionString(it) }.toTypedArray(), GENERIC_PERM_HANDLER)
        }
    }
}

fun Context.hasAllPermissions(permIds: Collection<Int>) = permIds.all(this::hasPermission)

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
fun isUpsideDownCakePlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
const val PERMISSION_READ_MEDIA_VISUAL_USER_SELECTED = 23
var actionOnPermission: ((granted: Boolean) -> Unit)? = null
var isAskingPermissions = false

fun Activity.handlePermission(permissionId: Int, callback: (granted: Boolean) -> Unit) {
    actionOnPermission = null
    if (hasPermission(permissionId)) {
        callback(true)
    } else {
        isAskingPermissions = true
        actionOnPermission = callback
        ActivityCompat.requestPermissions(
            this,
            arrayOf(getPermissionString(permissionId)),
            GENERIC_PERM_HANDLER
        )
    }
}

const val ORIENT_PORTRAIT = 0
const val ORIENT_LANDSCAPE_LEFT = 1
const val ORIENT_LANDSCAPE_RIGHT = 2


fun compensateDeviceRotation(orientation: Int) = when (orientation) {
    ORIENT_LANDSCAPE_LEFT -> 270
    ORIENT_LANDSCAPE_RIGHT -> 90
    else -> 0
}

fun Context.getOutputMediaFilePath(isPhoto: Boolean): String {
    val mediaStorageDir = File(config.savePhotosFolder)

    if (!mediaStorageDir.exists()) {
        if (!mediaStorageDir.mkdirs()) {
            return ""
        }
    }

    val mediaName = getRandomMediaName(isPhoto)
    return if (isPhoto) {
        "${mediaStorageDir.path}/$mediaName.jpg"
    } else {
        "${mediaStorageDir.path}/$mediaName.mp4"
    }
}

fun Context.getOutputMediaFileName(isPhoto: Boolean): String {
    val mediaName = getRandomMediaName(isPhoto)
    return if (isPhoto) {
        "$mediaName.jpg"
    } else {
        "$mediaName.mp4"
    }
}

fun getRandomMediaName(isPhoto: Boolean): String {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    return if (isPhoto) {
        "IMG_$timestamp"
    } else {
        "VID_$timestamp"
    }
}

const val PERMISSION_ACCESS_FINE_LOCATION = 22
const val PERMISSION_ACCESS_COARSE_LOCATION = 21

fun Context.checkLocationPermission(): Boolean {
    return hasPermission(PERMISSION_ACCESS_FINE_LOCATION) || hasPermission(
        PERMISSION_ACCESS_COARSE_LOCATION
    )
}
