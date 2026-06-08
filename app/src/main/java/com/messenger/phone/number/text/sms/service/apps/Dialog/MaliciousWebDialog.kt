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
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.databinding.MaliciousWebDialogItemBinding

class MaliciousWebDialog : MaterialDialogFragment() {

    companion object {
        const val TAG = "MaliciousWebDialog"

        fun newInstance(): MaliciousWebDialog = MaliciousWebDialog()
    }

    private var bottomSheetDialog: Dialog? = null
    private var onPositiveClick: (() -> Unit)? = null
    lateinit var binding: MaliciousWebDialogItemBinding
    var forwhatdialog = "Not"

    fun setOnPositiveClick(onPositiveClick: (() -> Unit)?): MaliciousWebDialog {
        this.onPositiveClick = onPositiveClick
        return this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireContext().setLocal()
        binding = MaliciousWebDialogItemBinding.inflate(inflater, container, false)
        val config = requireContext().config
        with(binding) {
            negativebuttonclick.setOnClickListener {
                dismiss()
            }
            positivebuttonclick.setOnClickListener {
                onPositiveClick?.invoke()
                dismiss()
            }
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
