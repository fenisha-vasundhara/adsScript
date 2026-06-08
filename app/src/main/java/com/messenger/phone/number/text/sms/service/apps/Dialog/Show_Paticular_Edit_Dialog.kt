package com.messenger.phone.number.text.sms.service.apps.Dialog

import android.annotation.SuppressLint
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
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.color.MaterialColors
import com.messenger.phone.number.text.sms.service.apps.CommanClass.copyToClipboard
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getDialogBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperSecondaryTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.ShowPaticularEditDialogItemBinding
import com.simplemobiletools.commons.extensions.toast
import android.graphics.drawable.GradientDrawable
import javax.annotation.Nullable

class Show_Paticular_Edit_Dialog : MaterialDialogFragment() {

    private var bottomSheetDialog: Dialog? = null
    lateinit var binding: ShowPaticularEditDialogItemBinding
    var edittxt = ""

    @SuppressLint("SetTextI18n")
    @Nullable
    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.show_paticular_edit_dialog_item, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyDialogTheme()
        with(binding) {
            binding.selectedTxt.setText(edittxt)
            binding.doneTxtCopy.setOnClickListener {
                val startSelection: Int = selectedTxt.selectionStart
                val endSelection: Int = selectedTxt.selectionEnd
                if (startSelection != -1 && endSelection != -1 && startSelection < endSelection) {
                    val selectedText: String = selectedTxt.text.toString().substring(startSelection, endSelection)
                    setSelection(selectedText)
                } else {
                    setSelection(selectedTxt.text.toString())
                }
                dismiss()
            }
        }
    }

    private fun applyDialogTheme() {
        val surfaceColor = requireContext().getDialogBackgroundColor()
        val onSurface = requireContext().getProperTextColor()
        val onSurfaceVariant = requireContext().getProperSecondaryTextColor()
        val primary = requireContext().getProperPrimaryColor()

        val container = (binding.root as? ViewGroup)?.getChildAt(0) ?: binding.root
        container.background = GradientDrawable().apply {
            cornerRadius = resources.getDimension(com.simplemobiletools.commons.R.dimen.material_dialog_corner_radius)
            setColor(surfaceColor)
        }

        binding.textView22.setTextColor(onSurfaceVariant)
        binding.selectedTxt.setTextColor(onSurface)
        binding.selectedTxt.setHintTextColor(onSurfaceVariant)
        binding.selectedTxt.background = GradientDrawable().apply {
            cornerRadius = resources.getDimension(com.intuit.sdp.R.dimen._6sdp)
            setColor(MaterialColors.layer(surfaceColor, onSurface, 0.08f))
        }
        binding.doneTxtCopy.setTextColor(primary)
    }

    private fun setSelection(selectedText: String) {
        requireActivity().copyToClipboard(selectedText.replace(Regex("^\\s+|\\s+$"), ""))
        requireActivity().toast(getString(R.string.message_copied_to_clipboard_new))
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

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        binding.selectedTxt.text.clear()
        fragmentManager?.beginTransaction()?.remove(this)?.commitAllowingStateLoss()
    }

}
