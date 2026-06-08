@file:JvmName("ContextExt")

package com.messenger.phone.number.text.sms.service.apps.CustomMenu.kotlin

import android.app.Activity
import android.content.Context

/** returns if an Activity is finishing or not. */
internal fun Context.isFinishing(): Boolean {
  return this is Activity && this.isFinishing
}
