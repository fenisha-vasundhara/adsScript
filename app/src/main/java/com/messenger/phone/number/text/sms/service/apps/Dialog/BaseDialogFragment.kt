package com.messenger.phone.number.text.sms.service.apps.Dialog

import android.view.WindowManager
import androidx.fragment.app.DialogFragment

open class BaseDialogFragment : DialogFragment() {

    protected open val dimAmount: Float = DEFAULT_DIM_AMOUNT

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            window.setDimAmount(dimAmount)
        }
    }

    companion object {
        const val DEFAULT_DIM_AMOUNT = 0.2f
    }
}
