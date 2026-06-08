package com.messenger.phone.number.text.sms.service.apps.CommanClass

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.messenger.phone.number.text.sms.service.apps.R

// Reference-style context theming helpers adapted to this app's 3-mode theme model.
fun Context.isDynamicTheme() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
        ThemeModeManager.getThemeMode(this) == ThemeModeManager.MODE_SYSTEM


fun Context.usesYouTheme() = isDynamicTheme()


fun Context.isSystemInDarkMode(): Boolean {
    return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}


fun Context.isAutoTheme() = Build.VERSION.SDK_INT < Build.VERSION_CODES.S &&
        ThemeModeManager.getThemeMode(this) == ThemeModeManager.MODE_SYSTEM


fun Context.getProperTextColor(): Int {
    return if (isDynamicTheme()) {
        resources.getColor(R.color.you_neutral_text_color, theme)
    } else if (ThemeModeManager.isDarkThemeActive(this)) {
        ContextCompat.getColor(this, R.color.white)
    } else {
        ContextCompat.getColor(this, R.color.black2)
    }
}


fun Context.getProperSecondaryTextColor(): Int {
    return if (isDynamicTheme()) {
        resources.getColor(R.color.you_neutral_text_color, theme)
    } else if (ThemeModeManager.isDarkThemeActive(this)) {
        ContextCompat.getColor(this, R.color.commanfornewtabselectiontxt)
    } else {
        ContextCompat.getColor(this, R.color.setting_icon_stock_color_1)
    }
}


fun Context.getProperBackgroundColor(): Int {
    return if (isDynamicTheme()) {
        // for system
        resources.getColor(R.color.you_background_color, theme)
    } else if (ThemeModeManager.isDarkThemeActive(this)) {
        // for dark . -->in wallpaper time activity recreate in activity
        ContextCompat.getColor(this, R.color.toolbarcolor3new)
    } else {
        ContextCompat.getColor(this, R.color.status_bar_ab_color)
    }
}

fun Context.getPaywallProperBackgroundColor(): Int {
    return  if (ThemeModeManager.isDarkThemeActive(this)) {
        // for dark . -->in wallpaper time activity recreate in activity
        ContextCompat.getColor(this, R.color.paywall_bg_dark_color)
    } else {
        ContextCompat.getColor(this, R.color.paywall_bg_light_color)
    }
}


fun Context.getPaywallUnselectedColor(): Int {
    return  if (ThemeModeManager.isDarkThemeActive(this)) {
        // for dark . -->in wallpaper time activity recreate in activity
        ContextCompat.getColor(this, R.color.paywall_plan_un_dark_color1)

    } else {
        ContextCompat.getColor(this, R.color.paywall_plan_un_light_color)
    }
}
fun Context.getPaywallUnselectedColor1(): Int {
    return  if (ThemeModeManager.isDarkThemeActive(this)) {
        // for dark . -->in wallpaper time activity recreate in activity
        ContextCompat.getColor(this, R.color.paywall_plan_un_dark_color1)

    } else {
        ContextCompat.getColor(this, R.color.paywall_plan_un_light_color1)
    }
}

//fun Context.getPaywallbg_BW_color(): Int {
//    return  if (ThemeModeManager.isDarkThemeActive(this)) {
//        // for dark . -->in wallpaper time activity recreate in activity
//        getPaywallProperBackgroundColor.adjustAlpha(0.54f)
//    } else {
//        ContextCompat.getColor(this, R.color.white)
//    }
//}






fun Context.getProperBackgroundColorForPermission(): Int {
    return if (isDynamicTheme()) {
        // for system
        resources.getColor(R.color.you_background_color, theme)
    } else if (ThemeModeManager.isDarkThemeActive(this)) {
        // for dark . -->in wallpaper time activity recreate in activity
        ContextCompat.getColor(this, R.color.toolbarcolor3new)
    } else {
        ContextCompat.getColor(this, R.color.status_bar_ab_color)
    }
}


fun Int.adjustAlpha(factor: Float): Int {
    val alpha = Math.round(Color.alpha(this) * factor)
    val red = Color.red(this)
    val green = Color.green(this)
    val blue = Color.blue(this)
    return Color.argb(alpha, red, green, blue)
}


fun Context.getProperPrimaryColor(): Int {

    return if (isDynamicTheme()) {
        try {
            ContextCompat.getColor(this, R.color.you_primary_color)
        } catch (e: Resources.NotFoundException) {
            // Fallback to a safe default (e.g. your primary color or Material You blue)
            ContextCompat.getColor(this, R.color.appcolor)
        }

    } else {
        config.themeselectedcolor
    }
}

fun TextInputEditText.setCursorColorProgrammatically(dynamicColor: Int) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // API 29+: Use the official textCursorDrawable property
        val cursorDrawable = textCursorDrawable?.mutate() ?: return
        cursorDrawable.colorFilter = BlendModeColorFilter(dynamicColor, BlendMode.SRC_IN)
        textCursorDrawable = cursorDrawable
    } else {
        // API < 29: Use reflection to access the internal 'mEditor' and its drawables
        try {
            // Get the cursor resource from TextView
            val fCursorRes = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            fCursorRes.isAccessible = true
            val cursorResId = fCursorRes.getInt(this)

            // Access the Editor field
            val fEditor = TextView::class.java.getDeclaredField("mEditor")
            fEditor.isAccessible = true
            val editor = fEditor.get(this)

            // Access the mCursorDrawable array inside Editor
            val fCursorDrawable = editor.javaClass.getDeclaredField("mCursorDrawable")
            fCursorDrawable.isAccessible = true

            // Tint the drawable from the resource ID
            val drawable =
                androidx.core.content.ContextCompat.getDrawable(context, cursorResId)?.mutate()
                    ?: return
            drawable.setColorFilter(dynamicColor, PorterDuff.Mode.SRC_IN)

            // Re-apply to the editor (expects an array of 2 drawables)
            val drawables = arrayOf(drawable, drawable)
            fCursorDrawable.set(editor, drawables)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


fun EditText.setCursorColorProgrammaticallyet(dynamicColor: Int) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // API 29+: Use the official textCursorDrawable property
        val cursorDrawable = textCursorDrawable?.mutate() ?: return
        cursorDrawable.colorFilter = BlendModeColorFilter(dynamicColor, BlendMode.SRC_IN)
        textCursorDrawable = cursorDrawable
    } else {
        // API < 29: Use reflection to access the internal 'mEditor' and its drawables
        try {
            // Get the cursor resource from TextView
            val fCursorRes = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            fCursorRes.isAccessible = true
            val cursorResId = fCursorRes.getInt(this)

            // Access the Editor field
            val fEditor = TextView::class.java.getDeclaredField("mEditor")
            fEditor.isAccessible = true
            val editor = fEditor.get(this)

            // Access the mCursorDrawable array inside Editor
            val fCursorDrawable = editor.javaClass.getDeclaredField("mCursorDrawable")
            fCursorDrawable.isAccessible = true

            // Tint the drawable from the resource ID
            val drawable =
                androidx.core.content.ContextCompat.getDrawable(context, cursorResId)?.mutate()
                    ?: return
            drawable.setColorFilter(dynamicColor, PorterDuff.Mode.SRC_IN)

            // Re-apply to the editor (expects an array of 2 drawables)
            val drawables = arrayOf(drawable, drawable)
            fCursorDrawable.set(editor, drawables)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun Context.getProperStatusBarColor() = getProperBackgroundColor()


fun Context.getColoredMaterialStatusBarColor() = getProperBackgroundColor()


fun Context.getBottomNavigationBackgroundColor() = getProperBackgroundColor()


fun Context.getDialogBackgroundColor(): Int {
    return if (isDynamicTheme()) {
        // for system
        resources.getColor(R.color.you_dialog_background_color, theme)
    } else if (ThemeModeManager.isDarkThemeActive(this)) {
        // for dark . -->in wallpaper time activity recreate in activity
        ContextCompat.getColor(this, R.color.dilog_dark_color)
    } else {
        ContextCompat.getColor(this, R.color.dilog_light_color)
    }
}
