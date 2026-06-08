package com.demo.adsmanage.Commen

import android.content.Context

class ConADS (context: Context) : BaseConfigADS(context) {

    companion object {
        fun newInstance(context: Context) = ConADS(context)
    }
}