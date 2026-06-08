package com.messenger.phone.number.text.sms.service.apps.helper

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager

class SafeViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {
    override fun onRestoreInstanceState(state: Parcelable?) {
        try {
            super.onRestoreInstanceState(state)
        } catch (e: IllegalStateException) {
            // Ignore invalid fragment state to avoid crash during restore.
        }
    }
}
