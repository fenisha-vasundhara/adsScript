package com.messenger.phone.number.text.sms.service.apps.modelClass

import android.graphics.drawable.Drawable

data class Customwallpaperadaptermodel(
    var name: String?=null,
    var img: Drawable?=null,
    var isselected: Boolean = false,
    var shareprefselected: Int?=null,
    var toolbarcolor: String?=null,
    var backgroundcolor: String?=null,
    var inmessagebackground: String?=null,
    var outmessagebackground: String?=null,
    var startreplaycolor: String?=null
)