package com.messenger.phone.number.text.sms.service.apps.helper

import android.content.Context
import android.util.AttributeSet
import android.os.Parcelable
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class NonSwipeableViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    private var isPagingEnabled: Boolean = false

    init {
        this.isPagingEnabled = true // Enable swipe by default
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (this.isPagingEnabled) {
            super.onTouchEvent(event)
        } else false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (this.isPagingEnabled) {
            super.onInterceptTouchEvent(event)
        } else false
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        try {
            super.onRestoreInstanceState(state)
        } catch (e: IllegalStateException) {
            // Ignore invalid fragment state to avoid crash during restore.
        }
    }

    fun setPagingEnabled(enabled: Boolean) {
        this.isPagingEnabled = enabled
    }
}
