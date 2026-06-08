package com.messenger.phone.number.text.sms.service.apps.Dialog

import android.app.Dialog
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.google.android.material.color.MaterialColors
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.EditAutoReplyMessageDrivingModeDialogItemBinding

class EditAutoReplMessageDrivingModeDialog : MaterialDialogFragment() {

    private var bottomSheetDialog: Dialog? = null
    lateinit var binding: EditAutoReplyMessageDrivingModeDialogItemBinding
    var dialogonDismiss: (() -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val colors = requireContext().resolveDialogColors()
        with(binding) {
            drivingmodetxt.setText(context?.config?.drivingmodemessage)
            textView.isSelected = true
            imageView18.setOnClickListener {
                dismiss()
            }
            savebtn.setOnClickListener {
                val message = drivingmodetxt.text.toString()
                if (message == context?.config?.drivingmodemessage) {
                    context?.getString(R.string.Please_Select_Another_Message)?.let { it1 -> context?.toastMess(it1) }
                } else if (message.isEmpty()) {
                    context?.getString(R.string.Please_Enter_Message)?.let { it1 -> context?.toastMess(it1) }
                } else {
                    context?.config?.drivingmodemessage = message
                    dismiss()
                }
            }

            binding.root.setBackgroundColor(colors.surface)
            binding.filledTextField.setBoxBackgroundColor(colors.optionFill)
            binding.filledTextField.setBoxStrokeColor(colors.outlineVariant)
            binding.drivingmodetxt.setTextColor(colors.onSurface)
            binding.drivingmodetxt.setHintTextColor(colors.onSurfaceVariant)
            binding.textView.setTextColor(colors.onSurface)
            binding.imageView18.iconTint =
                android.content.res.ColorStateList.valueOf(colors.onSurfaceVariant)
            binding.savebtn.backgroundTintList = ColorStateList.valueOf(colors.primary)
            binding.savebtn.setTextColor(
                MaterialColors.getColor(
                    binding.root,
                    com.google.android.material.R.attr.colorOnPrimary,
                    android.graphics.Color.WHITE
                )
            )
            val rippleColor = ColorStateList.valueOf(
                MaterialColors.layer(colors.surface, colors.onSurface, 0.16f)
            )
            binding.savebtn.rippleColor = rippleColor
            binding.imageView18.rippleColor = rippleColor
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.edit_auto_reply_message_driving_mode_dialog_item, container, false)
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
        dialogonDismiss?.invoke()
    }

}
