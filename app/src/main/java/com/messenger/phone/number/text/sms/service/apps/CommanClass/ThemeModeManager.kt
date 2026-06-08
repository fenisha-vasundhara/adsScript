package com.messenger.phone.number.text.sms.service.apps.CommanClass

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemeModeManager {
    const val MODE_SYSTEM = 0
    const val MODE_LIGHT = 1
    const val MODE_DARK = 2

    fun getThemeMode(context: Context): Int {
        return when (context.config.theme_mode) {
            MODE_LIGHT -> MODE_LIGHT
            MODE_DARK -> MODE_DARK
            else -> MODE_SYSTEM
        }
    }

    fun setThemeMode(context: Context, themeMode: Int) {
        context.config.theme_mode = normalizeThemeMode(themeMode)
        applyThemeMode(context)
    }

    fun applyThemeMode(context: Context) {
        applyThemeMode(getThemeMode(context))
    }

    fun applyThemeMode(mode: Int) {
        AppCompatDelegate.setDefaultNightMode(
            when (mode) {
                MODE_SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                MODE_DARK -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun isDarkThemeActive(context: Context): Boolean {
        return when (getThemeMode(context)) {
            MODE_SYSTEM -> isDarkMode(context)
            MODE_DARK -> true
            else -> false
        }
    }

    fun shouldUseLightSystemBars(context: Context): Boolean = !isDarkThemeActive(context)

    private fun normalizeThemeMode(themeMode: Int): Int {
        return when (themeMode) {
            MODE_SYSTEM, MODE_LIGHT, MODE_DARK -> themeMode
            else -> MODE_SYSTEM
        }
    }
}
