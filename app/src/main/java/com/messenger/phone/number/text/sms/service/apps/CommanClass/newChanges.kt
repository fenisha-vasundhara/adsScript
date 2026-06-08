package com.messenger.phone.number.text.sms.service.apps.CommanClass

import android.app.Activity
import android.app.AlertDialog
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.Settings
import android.provider.Telephony
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperSecondaryTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isDynamicTheme
import com.messenger.phone.number.text.sms.service.apps.DefaultSmsInstructionActivity
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.DialogMessageBinding

//    ----------
fun setDefaultApp_1(context: Activity) {
    val makeDefaultAppRequest = 360

    if (context.packageName == Telephony.Sms.getDefaultSmsPackage(context)) {
        return
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        launchRoleManagerDefaultSmsRequest(context, makeDefaultAppRequest)
    } else {
        val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT).apply {
            putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, context.packageName)
        }
        context.startActivityForResult(intent, makeDefaultAppRequest)
    }
}

@androidx.annotation.RequiresApi(Build.VERSION_CODES.Q)
private fun launchRoleManagerDefaultSmsRequest(context: Activity, requestCode: Int) {
    val roleManager = context.getSystemService(RoleManager::class.java) ?: return
    if (!roleManager.isRoleAvailable(RoleManager.ROLE_SMS) || roleManager.isRoleHeld(RoleManager.ROLE_SMS)) {
        return
    }

    val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS)
    context.startActivityForResult(intent, requestCode)
}

private fun Activity.openDefaultSmsInstruction() {

    val intent = Intent(this, DefaultSmsInstructionActivity::class.java)
    startActivity(intent)
}

fun handleDefaultSmsClick_1(context: Activity) {

    if (context.config.defaultSmsDialogSuppressed) {
//        showDefaultSmsSettingsDialog_1(context)
        context.openDefaultSmsInstruction()
        return
    }
    sendmessagebuttondefaultset = true
    setDefaultApp_1(context)
}

fun showDefaultSmsSettingsDialog_1(context: Activity) {
    val view = DialogMessageBinding.inflate(context.layoutInflater)

    val bodyTextColor = context.getProperTextColor()

    view.message.text = context.getString(R.string.default_sms_settings_message)
    view.message.setTextColor(bodyTextColor)

    val dialog = MaterialAlertDialogBuilder(context)
        .setTitle(R.string.Set_as_Default_SMS_app)
        .setView(view.root)
        .setPositiveButton(R.string.set_as_default_app) { _, _ ->
            openDefaultSmsSettings_1(context)
        }
        .setNegativeButton(R.string.cancel, null)
        .create()

    dialog.setOnShowListener {

        // Buttons
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(context.getProperPrimaryColor())

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(context.getProperSecondaryTextColor())

        // Title
        val titleView =
            dialog.findViewById<View>(androidx.appcompat.R.id.alertTitle)
                ?: dialog.findViewById(com.google.android.material.R.id.alertTitle)
        (titleView as? TextView)?.setTextColor(bodyTextColor)
    }

    dialog.show()
}

fun openDefaultSmsSettings_1(context: Activity) {
    val intent = try {
        Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
    } catch (_: Exception) {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
    }
    try {
        gooutside = true
        context.startActivity(intent)
    } catch (_: Exception) {
        val fallback = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        gooutside = true
        context.startActivity(fallback)
    }
}


//    ----
