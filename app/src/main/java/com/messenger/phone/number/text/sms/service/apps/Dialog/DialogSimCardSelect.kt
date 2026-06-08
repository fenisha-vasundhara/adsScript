package com.messenger.phone.number.text.sms.service.apps.Dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import androidx.cardview.widget.CardView
import com.google.android.material.color.MaterialColors
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.DialogAddcategoryBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.SelectSimcardLayoutBinding
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.DialogCategouryAddAction
import com.messenger.phone.number.text.sms.service.apps.interfaceClass.SelectSimCardDialogInterface
import javax.annotation.Nullable

class DialogSimCardSelect : MaterialDialogFragment() {


    private var bottomSheetDialog: Dialog? = null

    lateinit var binding: SelectSimcardLayoutBinding

    lateinit var selecteSimCardDialogInterface: SelectSimCardDialogInterface


    @Nullable
    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate<SelectSimcardLayoutBinding?>(inflater, R.layout.select_simcard_layout, container, false)
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
            SimOne.setOnClickListener {
                dismiss()
                selecteSimCardDialogInterface.SimOne()
            }
            SimTwo.setOnClickListener {
                dismiss()
                selecteSimCardDialogInterface.SimTwo()
            }
        }
    }

    private fun applyDialogTheme() {
        val colors = requireContext().resolveDialogColors()
        binding.cardMain.setCardBackgroundColor(colors.surface)
        binding.SimOne.setTextColor(colors.onSurface)
        binding.SimTwo.setTextColor(colors.onSurface)
        val rippleColor = ColorStateList.valueOf(
            MaterialColors.layer(colors.surface, colors.onSurface, 0.16f)
        )
        binding.SimOne.rippleColor = rippleColor
        binding.SimTwo.rippleColor = rippleColor
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

    fun setinterface(selecteSimCardDialogInterface: SelectSimCardDialogInterface) {
        this.selecteSimCardDialogInterface = selecteSimCardDialogInterface
    }
}
