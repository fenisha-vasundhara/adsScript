package com.messenger.phone.number.text.sms.service.apps.SwipeController

import android.view.View

interface InterfaceSwipAction {
    fun onSwipeLeft(pos: Int, itemView: View)
    fun onSwipeRight(pos: Int, itemView: View)
}