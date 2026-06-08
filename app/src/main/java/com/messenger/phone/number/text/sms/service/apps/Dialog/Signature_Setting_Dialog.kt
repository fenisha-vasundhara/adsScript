package com.messenger.phone.number.text.sms.service.apps.Dialog

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
import androidx.fragment.app.DialogFragment
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.google.android.material.color.MaterialColors
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setCursorColorProgrammatically
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setCursorColorProgrammaticallyet
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.SignatureSettingDialogItemBinding
import javax.inject.Inject

class Signature_Setting_Dialog @Inject constructor() : MaterialDialogFragment() {

    private var bottomSheetDialog: Dialog? = null
    lateinit var binding: SignatureSettingDialogItemBinding
    var Signature_Setting_DialogDismiss: ((Boolean) -> Unit)? = null
    var isDonebuttonClick = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            val colors = requireContext().resolveDialogColors()
            binding.mainBg.setCardBackgroundColor(colors.surface)
            binding.filledTextField.setCardBackgroundColor(colors.optionFill)
            binding.drivingmodetxt.setBackgroundColor(colors.optionFill)
            binding.drivingmodetxt.setTextColor(colors.onSurface)
            binding.drivingmodetxt.setHintTextColor(colors.onSurfaceVariant)
            binding.textView.setTextColor(colors.onSurface)
            binding.drivingmodetxt.setCursorColorProgrammaticallyet(colors.primary)

            binding.imageView18.iconTint =
                android.content.res.ColorStateList.valueOf(colors.onSurfaceVariant)
            binding.savebtn.setCardBackgroundColor(colors.primary)
            (binding.savebtn.getChildAt(0) as? android.widget.TextView)?.setTextColor(
                MaterialColors.getColor(
                    binding.root,
                    com.google.android.material.R.attr.colorOnPrimary,
                    android.graphics.Color.WHITE
                )
            )
            binding.savebtn.applyDialogRipple(colors, alpha = 0.16f)
            binding.imageView18.applyDialogRipple(colors, alpha = 0.16f)

            drivingmodetxt.setText(context?.config?.userpreferenceSignature)
            imageView18.setOnClickListener {
                context?.config?.userpreferenceSignatureOnOff = false
                dismiss()
            }
            savebtn.setOnClickListener {
                val message = drivingmodetxt.text.toString()
                if (message == "Signature") {
                    context?.getString(R.string.Selecte_Signature_hint)?.let { it1 -> context?.toastMess(it1) }
                } else if (message.isEmpty()) {
                    context?.getString(R.string.Signature_hint)?.let { it1 -> context?.toastMess(it1) }
                } else {
                    context?.config?.userpreferenceSignature = message
                    context?.config?.userpreferenceSignatureOnOff = message != "Signature"
                    isDonebuttonClick = true
                    dismiss()
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.signature_setting_dialog_item, container, false)
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
