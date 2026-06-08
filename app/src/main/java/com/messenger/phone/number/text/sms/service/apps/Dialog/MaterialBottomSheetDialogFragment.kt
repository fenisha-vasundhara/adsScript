package com.messenger.phone.number.text.sms.service.apps.Dialog

import android.view.WindowManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getMaterialBottomSheetTheme

open class MaterialBottomSheetDialogFragment : BottomSheetDialogFragment() {
    override fun getTheme(): Int {
        val ctx = context
        return if (ctx != null) {
            ctx.getMaterialBottomSheetTheme()
        } else {
            super.getTheme()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            window.setDimAmount(BaseDialogFragment.DEFAULT_DIM_AMOUNT)
        }
    }
}
