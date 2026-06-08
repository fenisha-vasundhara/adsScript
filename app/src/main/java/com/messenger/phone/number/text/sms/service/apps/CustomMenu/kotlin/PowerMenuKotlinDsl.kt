@file:Suppress("unused")

package com.messenger.phone.number.text.sms.service.apps.CustomMenu.kotlin

import android.content.Context
import androidx.annotation.MainThread
import com.messenger.phone.number.text.sms.service.apps.CustomMenu.PowerMenu

@DslMarker
internal annotation class PowerMenuDsl

/** creates an instance of [PowerMenu] by [PowerMenu.Builder] using kotlin dsl. */
@MainThread
@PowerMenuDsl
@JvmSynthetic
public inline fun createPowerMenu(
  context: Context,
  crossinline block: PowerMenu.Builder.() -> Unit,
): PowerMenu =
  PowerMenu.Builder(context).apply(block).build()
