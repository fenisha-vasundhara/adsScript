package com.messenger.phone.number.text.sms.service.apps.Dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import android.content.res.ColorStateList
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.color.MaterialColors
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.DialogAddcategoryBinding
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.DialogCategouryAddAction
import javax.annotation.Nullable


class DialogCategouryAdd : MaterialDialogFragment() {

    private var bottomSheetDialog: Dialog? = null

    lateinit var dialogCategouryAddAction: DialogCategouryAddAction
    lateinit var binding: DialogAddcategoryBinding

    @Nullable
    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate<DialogAddcategoryBinding?>(inflater, R.layout.dialog_addcategory, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val colors = requireContext().resolveDialogColors()
        with(binding) {
            Cancelbtn.setOnClickListener {
                dismiss()
                dialogCategouryAddAction.DialogCategouryAddOnCancelClick()
                editText.text?.clear()
            }
            okbtn.setOnClickListener {
                if (editText.text?.trim()?.isEmpty() == true) {
                    Toast.makeText(view.context, view.context.resources.getString(R.string.Please_Category_Name), Toast.LENGTH_SHORT).show()
                } else {
                    dismiss()
                    dialogCategouryAddAction.DialogCategouryAddOnOkClick(editText.text.toString())
                    editText.text?.clear()
                }
            }
            root.setBackgroundColor(colors.surface)
            cardMain.setCardBackgroundColor(colors.surface)
            textView.setTextColor(colors.onSurface)
            filledTextField.setBoxBackgroundColor(colors.optionFill)
            filledTextField.setBoxStrokeColor(colors.outlineVariant)
            editText.setTextColor(colors.onSurface)
            editText.setHintTextColor(colors.onSurfaceVariant)
            okbtn.setTextColor(colors.primary)
            Cancelbtn.setTextColor(colors.onSurfaceVariant)
            val rippleColor = ColorStateList.valueOf(
                MaterialColors.layer(colors.surface, colors.onSurface, 0.16f)
            )
            okbtn.rippleColor = rippleColor
            Cancelbtn.rippleColor = rippleColor
        }
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
    }

    fun setinterface(dialogCategouryAddAction: DialogCategouryAddAction) {
        this.dialogCategouryAddAction = dialogCategouryAddAction
    }

}
