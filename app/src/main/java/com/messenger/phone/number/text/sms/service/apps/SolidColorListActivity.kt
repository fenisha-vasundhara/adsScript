package com.messenger.phone.number.text.sms.service.apps

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import com.demo.adsmanage.Commen.firebaseEventMain
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getBottomNavigationBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperSecondaryTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperStatusBarColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setWallpaperdone
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.colorcodelist
import com.messenger.phone.number.text.sms.service.apps.adapter.SolidColorAdapter
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivitySolidColorListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SolidColorListActivity : AppCompatActivity() {

    lateinit var binding: ActivitySolidColorListBinding

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    @Inject
    lateinit var solidColorAdapter: SolidColorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_solid_color_list)
        setBaseTheme(binding.vAnd15StatusBar)
        binding.solidColorList.adapter = solidColorAdapter
        this.firebaseEventMain("Custom_Wallpaper_Solid_Color")

        fontSize10 = getTextSizeForeNormal10MS()
        fontSize13 = getTextSizeForeNormal13MS()
        fontSize18 = getTextSizeForeNormal18MS()
        fontSize8 = getTextSizeForeNormal8MS()
        fontSize15 = getTextSizeHometitleMS()

        binding.textsizechagefor10 = fontSize10
        binding.textsizechagefor13 = fontSize13
        binding.textsizechagefor18 = fontSize18
        binding.textsizechagefor8 = fontSize8
        binding.textsizechagefor15 = fontSize15

        CoroutineScope(Dispatchers.IO).launch {
            val colorlist = colorcodelist
            runOnUiThread {
                solidColorAdapter.colorlist = ArrayList(colorlist)

            }
        }
        solidColorAdapter.colorclick = {
            startActivity(
                Intent(this, CustomWallpaperActivity::class.java)
                    .putExtra("colorcode", it)
                    .putExtra("issolidecolor", true)
            )
        }

        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        ThemeSetup()
        if (setWallpaperdone) {
            finish()
        }
    }

    private fun ThemeSetup() {
        val surfaceColor = getProperBackgroundColor()
        val statusBarColor = getProperStatusBarColor()
        val navigationBarColor = getBottomNavigationBackgroundColor()
        val textColor = getProperTextColor()
        val secondaryTextColor = getProperSecondaryTextColor()

        binding.main.setBackgroundColor(surfaceColor)
        binding.vAnd15StatusBar.setBackgroundColor(statusBarColor)
        binding.defaulttoolshow.setBackgroundColor(surfaceColor)
        binding.solidColorList.setBackgroundColor(surfaceColor)

        binding.textView3.setTextColor(textColor)
        binding.backBtn.iconTint = ColorStateList.valueOf(secondaryTextColor)

        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        window.decorView.setBackgroundColor(surfaceColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isStatusBarContrastEnforced = false
            window.isNavigationBarContrastEnforced = false
        }
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightBars = ThemeModeManager.shouldUseLightSystemBars(this@SolidColorListActivity)
            isAppearanceLightStatusBars = useLightBars
            isAppearanceLightNavigationBars = useLightBars
        }
    }
}
