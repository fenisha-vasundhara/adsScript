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
import com.messenger.phone.number.text.sms.service.apps.Dialog.BaseDialogFragment
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.databinding.MaliciousWebDialogItemBinding
import com.messenger.phone.number.text.sms.service.apps.databinding.PemissionDialogLayoutBinding

class PemissionDIalog(var conbuttonclick: (() -> Unit)? = null) : BaseDialogFragment() {

    private var bottomSheetDialog: Dialog? = null

    lateinit var binding: PemissionDialogLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireContext().setLocal()
        binding = PemissionDialogLayoutBinding.inflate(inflater, container, false)
        binding.con.setOnClickListener {
            conbuttonclick?.invoke()
            dismiss()
        }
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
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

}
