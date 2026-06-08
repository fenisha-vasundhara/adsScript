package com.messenger.phone.number.text.sms.service.apps.Dialog

import android.app.Activity
import android.app.ProgressDialog
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.messenger.phone.number.text.sms.service.apps.CommanClass.baseConfig
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.gone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isimportstart
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setupDialogStuff
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityBekupBinding
import com.messenger.phone.number.text.sms.service.apps.helper.MessagesImporter
import com.simplemobiletools.commons.extensions.toast
import com.simplemobiletools.commons.helpers.ensureBackgroundThread
import com.simplemobiletools.commons.views.MyAppCompatCheckbox
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class ImportMessagesDialog(
    private val activity: Activity,
    private val path: String,
    val binding: ActivityBekupBinding,
) {

    private val config = activity.config
    var ignoreClicks = false

    init {

        val view =
            (activity.layoutInflater.inflate(R.layout.dialog_import_messages, null) as ViewGroup)
        val import_sms_checkbox = view.findViewById<MyAppCompatCheckbox>(R.id.import_sms_checkbox)
        val import_mms_checkbox = view.findViewById<MyAppCompatCheckbox>(R.id.import_mms_checkbox)

        import_sms_checkbox.isChecked = config.importSms
        import_mms_checkbox.isChecked = config.importMms


        importStart()


//        MaterialAlertDialogBuilder(activity).setPositiveButton(R.string.ok, null).setNegativeButton(R.string.cancel, null).apply {
//            activity.setupDialogStuff(view, this, R.string.import_messages) { alertDialog ->
//                alertDialog.getButton(-1).setOnClickListener {
//
//                }
//                alertDialog.getButton(-2).setOnClickListener {
//                    isimportstart = false
//                    val cacheDir: File = activity.cacheDir
//                    val fileName = "MessageBackup.json"
//                    val file = File(cacheDir, fileName)
//                    alertDialog.dismiss()
//                    CoroutineScope(Dispatchers.Main).launch {
//                        if (file.exists()) {
//                            restoreCancel()
//                        } else {
//                            importfail()
//                        }
//                    }
//                }
//            }
//        }
    }

    private fun importStart() {

        if (ignoreClicks) {
            return
        }

//        if (!import_sms_checkbox.isChecked && !import_mms_checkbox.isChecked) {
//            activity.toast(R.string.no_option_selected)
//            return
//        }

        ignoreClicks = true
        CoroutineScope(Dispatchers.Main).launch { importstart() }
        isimportstart = true
        activity.toast(R.string.importing)
        config.importSms = true
        config.importMms = false
        ensureBackgroundThread {
            MessagesImporter(activity).importMessages(path) {
                Log.d("MessagesImporter", ": <------------> MessagesImporter 1 ${path}")
                handleParseResult(it)
            }
        }

    }


    fun importDone() {
        binding.backupProgressCard.visible()
        CoroutineScope(Dispatchers.Main).launch {
            binding.textView41.setText(binding.backupProgressCard.context.resources.getString(R.string.restoring_up_messages))
            binding.textView41Ab.setText(binding.backupProgressCard.context.resources.getString(R.string.restoring_up_messages))
        }
        Handler(Looper.getMainLooper()).postDelayed({
            binding.backupProgressCard.gone()
            binding.backupProgressCardAb.gone()
        }, 2000)
    }

    fun restoreCancel() {
        binding.backupProgressCard.visible()
        CoroutineScope(Dispatchers.Main).launch {
            binding.textView41.setText(binding.backupProgressCard.context.resources.getString(R.string.restoring_up_messages))
            binding.textView41Ab.setText(binding.backupProgressCard.context.resources.getString(R.string.restoring_up_messages))
        }
        Handler(Looper.getMainLooper()).postDelayed({
            binding.backupProgressCard.gone()
            binding.backupProgressCardAb.gone()
        }, 2000)
    }

    fun importfail() {
        binding.backupProgressCard.visible()
        binding.backupProgressCardAb.visible()
        CoroutineScope(Dispatchers.Main).launch {
            binding.textView41.setText(binding.backupProgressCard.context.resources.getString(R.string.restoring_up_messages))
            binding.textView41Ab.setText(binding.backupProgressCard.context.resources.getString(R.string.restoring_up_messages))
        }
        Handler(Looper.getMainLooper()).postDelayed({
            binding.backupProgressCard.gone()
            binding.backupProgressCardAb.gone()
        }, 2000)
    }

    fun importstart() {
        binding.backupProgressCard.visible()
        CoroutineScope(Dispatchers.Main).launch {
            binding.textView41.setText(binding.backupProgressCard.context.resources.getString(R.string.restoring_up_messages))
            binding.textView41Ab.setText(binding.backupProgressCard.context.resources.getString(R.string.restoring_up_messages))
        }
        Handler(Looper.getMainLooper()).postDelayed({
            binding.backupProgressCard.gone()
            binding.backupProgressCardAb.gone()
        }, 2000)
    }


    private fun handleParseResult(result: MessagesImporter.ImportResult) {
        Log.d("MessagesImporter", ": <------------> MessagesImporter 2 ${result}")
        isimportstart = false
        val message: String
        when (result) {
            MessagesImporter.ImportResult.IMPORT_OK -> {
                CoroutineScope(Dispatchers.Main).launch { importDone() }
                message = activity.getString(R.string.import_messages)
            }

            MessagesImporter.ImportResult.IMPORT_PARTIAL -> {
                CoroutineScope(Dispatchers.Main).launch { importfail() }
                message = activity.getString(R.string.importing_some_entries_failed)
            }

            else -> {
                CoroutineScope(Dispatchers.Main).launch { importfail() }
                message = activity.getString(R.string.no_items_found)
            }
        }

        activity.toast(message)
    }
}
