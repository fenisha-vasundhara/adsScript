package com.messenger.phone.number.text.sms.service.apps.Dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.messenger.phone.number.text.sms.service.apps.CommanClass.EXPORT_FILE_EXT
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getMaterialDialogTheme
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setupDialogStuff
import com.messenger.phone.number.text.sms.service.apps.R
import com.simplemobiletools.commons.dialogs.FilePickerDialog
import com.simplemobiletools.commons.extensions.beGone
import com.simplemobiletools.commons.extensions.getCurrentFormattedDateTime
import com.simplemobiletools.commons.extensions.getParentPath
import com.simplemobiletools.commons.extensions.hideKeyboard
import com.simplemobiletools.commons.extensions.humanizePath
import com.simplemobiletools.commons.extensions.internalStoragePath
import com.simplemobiletools.commons.extensions.isAValidFilename
import com.simplemobiletools.commons.extensions.showKeyboard
import com.simplemobiletools.commons.extensions.toast
import com.simplemobiletools.commons.extensions.value
import com.simplemobiletools.commons.views.MyAppCompatCheckbox
import com.simplemobiletools.commons.views.MyTextInputLayout
import java.io.File

@SuppressLint("SetTextI18n")
class ExportMessagesDialog(
    private val activity: Activity,
    private val path: String,
    private val hidePath: Boolean,
    private val callback: (file: File) -> Unit,
) {
    private var realPath = if (path.isEmpty()) activity.internalStoragePath else path
    private val config = activity.config

    init {
        val view = (activity.layoutInflater.inflate(R.layout.dialog_export_messages, null) as ViewGroup)
        val export_messages_folder = view.findViewById<TextInputEditText>(R.id.export_messages_folder)
        val export_messages_filename = view.findViewById<TextInputEditText>(R.id.export_messages_filename)
        val export_sms_checkbox = view.findViewById<MyAppCompatCheckbox>(R.id.export_sms_checkbox)
        val export_mms_checkbox = view.findViewById<MyAppCompatCheckbox>(R.id.export_mms_checkbox)
        val export_messages_folder_hint = view.findViewById<MyTextInputLayout>(R.id.export_messages_folder_hint)


        export_messages_folder.setText(activity.humanizePath(realPath))
        export_messages_filename.setText("${activity.getString(R.string.messages)}_${activity.getCurrentFormattedDateTime()}")
        export_sms_checkbox.isChecked = config.exportSms
        export_mms_checkbox.isChecked = config.exportMms

        if (hidePath) {
            export_messages_folder_hint.beGone()
        } else {
            export_messages_folder.setOnClickListener {
                activity.hideKeyboard(export_messages_filename)
                FilePickerDialog(activity, realPath, false, showFAB = true) {
                    export_messages_folder.setText(activity.humanizePath(it))
                    realPath = it
                }
            }
        }

        MaterialAlertDialogBuilder(activity, activity.getMaterialDialogTheme())
            .setPositiveButton(R.string.ok, null)
            .setNegativeButton(R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(view, this, R.string.export_messages) { alertDialog ->
                    alertDialog.showKeyboard(export_messages_filename)
                    alertDialog.getButton(-1).setOnClickListener {
                        val filename = export_messages_filename.value
                        when {
                            filename.isEmpty() -> activity.toast(R.string.empty_name)
                            filename.isAValidFilename() -> {
                                val file = File(realPath, "$filename$EXPORT_FILE_EXT")
                                if (!hidePath && file.exists()) {
                                    activity.toast(R.string.name_taken)
                                    return@setOnClickListener
                                }

                                if (!export_sms_checkbox.isChecked && !export_mms_checkbox.isChecked) {
                                    activity.toast(R.string.no_option_selected)
                                    return@setOnClickListener
                                }
                                config.exportSms = export_sms_checkbox.isChecked
                                config.exportMms = export_mms_checkbox.isChecked
                                config.lastExportPath = file.absolutePath.getParentPath()
                                callback(file)
                                alertDialog.dismiss()
                            }

                            else -> activity.toast(R.string.invalid_name)
                        }
                    }
                }
            }

    }
}
