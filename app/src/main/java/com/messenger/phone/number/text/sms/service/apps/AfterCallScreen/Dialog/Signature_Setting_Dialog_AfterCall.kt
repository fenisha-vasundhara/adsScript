package com.messenger.phone.number.text.sms.service.apps.AfterCallScreen.Dialog

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.messenger.phone.number.text.sms.service.apps.Dialog.BaseDialogFragment
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.EditAutoReplyMessageDrivingModeDialogItemBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.SignatureSettingDialogItemBinding
import javax.inject.Inject

class Signature_Setting_Dialog_AfterCall constructor(
    var dialogtitle: String,
    var hinttext: String,
    var edittexttext: String,
    var okbuttonclick: ((String) -> Unit)
) : BaseDialogFragment() {

    private var bottomSheetDialog: Dialog? = null
    lateinit var binding: SignatureSettingDialogItemBinding
    var Signature_Setting_DialogDismiss: ((Boolean) -> Unit)? = null
    var isDonebuttonClick = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            val config = requireContext().config

            if (config.activeThemeSelection == 1) {
                binding.filledTextField.setCardBackgroundColor(resources.getColor(R.color.commandialogposbutton))
                binding.drivingmodetxt.setBackgroundColor(resources.getColor(R.color.commandialogposbutton))
                binding.drivingmodetxt.setTextColor(resources.getColor(R.color.signachurdialog))
                binding.drivingmodetxt.setHintTextColor(resources.getColor(R.color.signachurdialog))
            } else if (config.activeThemeSelection == 2) {
                binding.filledTextField.setCardBackgroundColor(resources.getColor(R.color.chatbgcolor22))
                binding.drivingmodetxt.setBackgroundColor(resources.getColor(R.color.chatbgcolor22))
                binding.drivingmodetxt.setTextColor(resources.getColor(R.color.white))
                binding.drivingmodetxt.setHintTextColor(resources.getColor(R.color.white))
            } else if (config.activeThemeSelection == 3) {
                binding.filledTextField.setCardBackgroundColor(resources.getColor(R.color.toolbarcolor3new3))
                binding.drivingmodetxt.setBackgroundColor(resources.getColor(R.color.toolbarcolor3new3))
                binding.drivingmodetxt.setTextColor(resources.getColor(R.color.white))
                binding.drivingmodetxt.setHintTextColor(resources.getColor(R.color.white))
            } else {
                binding.filledTextField.setCardBackgroundColor(resources.getColor(R.color.commandialogposbutton))
                binding.drivingmodetxt.setBackgroundColor(resources.getColor(R.color.commandialogposbutton))
                binding.drivingmodetxt.setTextColor(resources.getColor(R.color.white))
                binding.drivingmodetxt.setHintTextColor(resources.getColor(R.color.white))
            }
            drivingmodetxt.setHint(hinttext)
            textView.text = dialogtitle
            drivingmodetxt.setText(edittexttext)
            imageView18.setOnClickListener {
                dismiss()
            }
            savebtn.setOnClickListener {
                val message = drivingmodetxt.text.toString()
                if (message.isBlank()) {
                    requireActivity().toastMess("please enter remainder text")
                } else {
                    okbuttonclick.invoke(message)
                    dismiss()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.signature_setting_dialog_item,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bottomSheetDialog = dialog
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        var width = displayMetrics.widthPixels
        width -= width / 8
        bottomSheetDialog!!.window!!.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
        bottomSheetDialog?.setCancelable(false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Signature_Setting_DialogDismiss?.invoke(isDonebuttonClick)
    }


}
