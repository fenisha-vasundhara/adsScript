package com.messenger.phone.number.text.sms.service.apps.ads

import android.content.Context
import android.util.Log
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config


var testBanner = "ca-app-pub-3940256099942544/9214589741"
var testONative = "ca-app-pub-3940256099942544/2247696110"
var testOpen = "ca-app-pub-3940256099942544/9257395921"
var testInter = "ca-app-pub-3940256099942544/1033173712"

var is_testMode = false

var MS_Native_Aftercall_Low =
    if (is_testMode) testONative else "ca-app-pub-2033413118114270/8386429193"
var MS_Banner_Aftercall_Low =
    if (is_testMode) testBanner else "ca-app-pub-2033413118114270/7134686347"

//var MS_Banner_Aftercall_Low = if (is_testMode) testBanner else testBanner
var MS_Banner_AFTERCALL_MREC =
    if (is_testMode) testBanner else "ca-app-pub-2033413118114270/7151378696"






