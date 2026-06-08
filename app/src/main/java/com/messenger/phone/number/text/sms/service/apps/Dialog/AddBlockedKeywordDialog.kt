package com.messenger.phone.number.text.sms.service.apps.Dialog

import android.app.Activity
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getMaterialDialogTheme
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setupDialogStuff
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toArrayList
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.DialogAddBlockedKeywordBinding
import com.simplemobiletools.commons.activities.BaseSimpleActivity
import com.simplemobiletools.commons.extensions.getAlertDialogBuilder
import com.simplemobiletools.commons.extensions.showKeyboard
import com.simplemobiletools.commons.extensions.toast
import com.simplemobiletools.commons.extensions.value

class AddBlockedKeywordDialog(
    val activity: Activity,
    private val originalKeyword: String? = null,
    val callback: () -> Unit
) {
    init {
        val binding = DialogAddBlockedKeywordBinding.inflate(activity.layoutInflater).apply {
            if (originalKeyword != null) {
                addBlockedKeywordEdittext.setText(originalKeyword)
            }
        }

        MaterialAlertDialogBuilder(activity, activity.getMaterialDialogTheme())
            .setPositiveButton(R.string.ok, null)
            .setNegativeButton(R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(
                    binding.root,
                    this,
                    R.string.blocked_keywords_new_ui
                ) { alertDialog ->
                    alertDialog.showKeyboard(binding.addBlockedKeywordEdittext)
                    val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    val updatePositiveState = {
                        val hasText = binding.addBlockedKeywordEdittext.text?.toString()?.trim()
                            .isNullOrEmpty().not()
                        positiveButton.isEnabled = hasText
                        positiveButton.alpha = if (hasText) 1f else 0.5f
                    }

                    updatePositiveState()

                    binding.addBlockedKeywordEdittext.doAfterTextChanged {
                        updatePositiveState()
                    }

                    positiveButton.setOnClickListener {
                        val newBlockedKeyword = binding.addBlockedKeywordEdittext.value.trim()

                        if (activity.config.blockedKeywords.toArrayList()
                                .contains(newBlockedKeyword)
                        ) {
                            activity.toast(activity.resources.getString(R.string.Thiskeyword))
                        }

                        if (originalKeyword != null && newBlockedKeyword != originalKeyword) {
                            Log.d("2343432", "newBlockedKeyword : <-------> 11111")
                            activity.config.removeBlockedKeyword(originalKeyword)
                        }

                        if (newBlockedKeyword.isNotEmpty()) {
                            activity.config.addBlockedKeyword(newBlockedKeyword)

                        }
                        callback()
                        alertDialog.dismiss()
                    }
                }
            }
    }
}
