package com.messenger.phone.number.text.sms.service.apps.Dialog

import android.app.Activity
import android.app.AlertDialog
import android.view.View
import android.widget.TextView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getMaterialDialogTheme
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperSecondaryTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setupDialogStuff
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.DialogMessageBinding


class PermissionRequiredDialog(
    val activity: Activity,
    textId: Int,
    private val positiveActionCallback: () -> Unit,
    private val negativeActionCallback: (() -> Unit)? = null
) {
    private var dialog: androidx.appcompat.app.AlertDialog? = null

    init {
        val view = DialogMessageBinding.inflate(activity.layoutInflater, null, false)
        view.message.text = activity.getString(textId)

        MaterialAlertDialogBuilder(activity, activity.getMaterialDialogTheme())
            .setPositiveButton(R.string.grant_permission) { _, _ -> positiveActionCallback() }
            .setNegativeButton(R.string.cancel) { _, _ -> negativeActionCallback?.invoke()
            }.apply {
                val title = activity.getString(R.string.permission_required)
                activity.setupDialogStuff(view.root, this, titleText = title) { alertDialog ->
                    dialog = alertDialog
                    updateButtonColors(alertDialog)
                    alertDialog.window?.decorView?.postDelayed({
                        updateButtonColors(alertDialog)
                    }, 500)

                }

            }
    }
    private fun updateButtonColors(alertDialog: androidx.appcompat.app.AlertDialog) {
        alertDialog.setOnShowListener {
            alertDialog.window?.decorView?.postDelayed({
                alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                    ?.setTextColor(activity.getProperPrimaryColor())

                alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)
                    ?.setTextColor(activity.getProperSecondaryTextColor())
            }, 300)
            val titleView =
                alertDialog.findViewById<TextView>(androidx.appcompat.R.id.alertTitle)
                    ?: alertDialog.findViewById(com.google.android.material.R.id.alertTitle)

            titleView?.setTextColor(activity.getProperTextColor())
        }
    }}
