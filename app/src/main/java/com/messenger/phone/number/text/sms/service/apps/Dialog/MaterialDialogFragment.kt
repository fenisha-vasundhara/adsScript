package com.messenger.phone.number.text.sms.service.apps.Dialog

import com.messenger.phone.number.text.sms.service.apps.CommanClass.getMaterialDialogTheme

open class MaterialDialogFragment : BaseDialogFragment() {
    override fun getTheme(): Int {
        val ctx = context
        return if (ctx != null) {
            ctx.getMaterialDialogTheme()
        } else {
            super.getTheme()
        }
    }
}
