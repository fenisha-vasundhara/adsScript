package com.messenger.phone.number.text.sms.service.apps

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.google.android.material.color.DynamicColors
import com.messenger.phone.number.text.sms.service.apps.CommanClass.findBestInsetParent
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getBottomNavigationBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperStatusBarColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.visible
import com.messenger.phone.number.text.sms.service.apps.CommanClass.windowInsetsSet
import androidx.activity.enableEdgeToEdge
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getPaywallProperBackgroundColor

class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}

fun ComponentActivity.setBaseThemeLight(vAnd15StatusBar: View) {
    ThemeModeManager.applyThemeMode(this)
    if (ThemeModeManager.getThemeMode(this) == ThemeModeManager.MODE_SYSTEM) {
        DynamicColors.applyToActivityIfAvailable(this)
    }

    val useLightBars =true
    if (useLightBars) {
        setTheme(R.style.mainScreen)
    } else {
        setTheme(R.style.mainScreenDark)
    }

    val surfaceColor = ContextCompat.getColor(this, R.color.white)
    val statusBarColor =   ContextCompat.getColor(this, R.color.white)
    val navigationBarColor =  ContextCompat.getColor(this, R.color.white)

    enableEdgeToEdge(
        navigationBarStyle = SystemBarStyle.auto(
            navigationBarColor,
            navigationBarColor
        )
    )

    WindowCompat.getInsetsController(window, window.decorView).apply {
        isAppearanceLightStatusBars = useLightBars
        isAppearanceLightNavigationBars = useLightBars
    }

    val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
    window.statusBarColor = statusBarColor
    window.navigationBarColor = navigationBarColor
    window.decorView.setBackgroundColor(surfaceColor)
    vAnd15StatusBar.setBackgroundColor(statusBarColor)
    vAnd15StatusBar.visible()

    ViewCompat.setOnApplyWindowInsetsListener(vAnd15StatusBar) { view, insets ->
        val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
        val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom

        // Status bar spacer height
        view.updateLayoutParams {
            height = statusBarHeight
        }

        // 🔥 Apply nav bar padding to root parent
        val rootView = view.findBestInsetParent()
        rootView.updatePadding(
            bottom = navBarHeight
        )

        view.windowInsetsSet(windowInsetsController, insets)
        insets
    }

}

fun ComponentActivity.setBaseTheme(vAnd15StatusBar: View) {
    ThemeModeManager.applyThemeMode(this)
    if (ThemeModeManager.getThemeMode(this) == ThemeModeManager.MODE_SYSTEM) {
        DynamicColors.applyToActivityIfAvailable(this)
    }

    val useLightBars = ThemeModeManager.shouldUseLightSystemBars(this)
    if (useLightBars) {
        setTheme(R.style.mainScreen)
    } else {
        setTheme(R.style.mainScreenDark)
    }

    val surfaceColor = getProperBackgroundColor()
    val statusBarColor = getProperStatusBarColor()
    val navigationBarColor = getBottomNavigationBackgroundColor()

    enableEdgeToEdge(
        navigationBarStyle = SystemBarStyle.auto(
            navigationBarColor,
            navigationBarColor
        )
    )

    WindowCompat.getInsetsController(window, window.decorView).apply {
        isAppearanceLightStatusBars = useLightBars
        isAppearanceLightNavigationBars = useLightBars
    }

    val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
    window.statusBarColor = statusBarColor
    window.navigationBarColor = navigationBarColor
    window.decorView.setBackgroundColor(surfaceColor)
    vAnd15StatusBar.setBackgroundColor(statusBarColor)
    vAnd15StatusBar.visible()

    ViewCompat.setOnApplyWindowInsetsListener(vAnd15StatusBar) { view, insets ->
        val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
        val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom

        // Status bar spacer height
        view.updateLayoutParams {
            height = statusBarHeight
        }

        // 🔥 Apply nav bar padding to root parent
        val rootView = view.findBestInsetParent()
        rootView.updatePadding(
            bottom = navBarHeight
        )

        view.windowInsetsSet(windowInsetsController, insets)
        insets
    }

}


fun ComponentActivity.setBaseThemePaywall(vAnd15StatusBar: View) {
    ThemeModeManager.applyThemeMode(this)
    if (ThemeModeManager.getThemeMode(this) == ThemeModeManager.MODE_SYSTEM) {
        DynamicColors.applyToActivityIfAvailable(this)
    }

    val useLightBars = ThemeModeManager.shouldUseLightSystemBars(this)
    if (useLightBars) {
        setTheme(R.style.mainScreen)
    } else {
        setTheme(R.style.mainScreenDark)
    }

    val surfaceColor = getPaywallProperBackgroundColor()

    enableEdgeToEdge(
        navigationBarStyle = SystemBarStyle.auto(
            surfaceColor,
            surfaceColor
        )
    )

    WindowCompat.getInsetsController(window, window.decorView).apply {
        isAppearanceLightStatusBars = useLightBars
        isAppearanceLightNavigationBars = useLightBars
    }

    val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
    window.statusBarColor = surfaceColor
    window.navigationBarColor = surfaceColor
    window.decorView.setBackgroundColor(surfaceColor)
    vAnd15StatusBar.setBackgroundColor(surfaceColor)
    vAnd15StatusBar.visible()

    ViewCompat.setOnApplyWindowInsetsListener(vAnd15StatusBar) { view, insets ->
        val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
        val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom

        // Status bar spacer height
        view.updateLayoutParams {
            height = statusBarHeight
        }

        // 🔥 Apply nav bar padding to root parent
        val rootView = view.findBestInsetParent()
        rootView.updatePadding(
            bottom = navBarHeight
        )

        view.windowInsetsSet(windowInsetsController, insets)
        insets
    }

}




