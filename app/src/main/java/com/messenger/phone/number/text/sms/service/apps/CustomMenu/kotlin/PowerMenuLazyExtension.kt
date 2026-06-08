@file:Suppress("unused")

package com.messenger.phone.number.text.sms.service.apps.CustomMenu.kotlin

import android.view.View
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import com.messenger.phone.number.text.sms.service.apps.CustomMenu.PowerMenu
import com.skydoves.powermenu.kotlin.ViewPowerMenuLazy

/**
 * Returns a [Lazy] delegate to access the [ComponentActivity]'s PowerMenu property.
 * The PowerMenu property will be initialized lazily.
 *
 * @see [Lazy Initialization](https://github.com/skydoves/powermenu#lazy-initialization-in-kotlin)
 */
@MainThread
@JvmSynthetic
public inline fun <reified T : PowerMenu.Factory> ComponentActivity.powerMenu(): Lazy<PowerMenu> {
  return ActivityPowerMenuLazy(this, this, T::class)
}

/**
 * Returns a [Lazy] delegate to access the [Fragment]'s PowerMenu property.
 * The PowerMenu property will be initialized lazily.
 *
 * @see [Lazy Initialization](https://github.com/skydoves/powermenu#lazy-initialization-in-kotlin)
 */
@MainThread
@JvmSynthetic
public inline fun <reified T : PowerMenu.Factory> Fragment.powerMenu(): Lazy<PowerMenu?> {
  return FragmentPowerMenuLazy(this, T::class)
}

/**
 * Returns a [Lazy] delegate to access the custom [View]'s PowerMenu property.
 * The PowerMenu property will be initialized lazily.
 *
 * @see [Lazy Initialization](https://github.com/skydoves/powermenu#lazy-initialization-in-kotlin)
 */
@MainThread
@JvmSynthetic
public inline fun <reified T : PowerMenu.Factory> View.powerMenu(): Lazy<PowerMenu> {
  return ViewPowerMenuLazy(context, T::class)
}
