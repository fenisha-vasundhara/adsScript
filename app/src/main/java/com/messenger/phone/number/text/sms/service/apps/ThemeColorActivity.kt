package com.messenger.phone.number.text.sms.service.apps

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.SystemGeneratedIconSwitchAb
import com.messenger.phone.number.text.sms.service.apps.CommanClass.ThemeModeManager
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getBottomNavigationBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperBackgroundColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperPrimaryColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperSecondaryTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperStatusBarColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal10MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal13MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal18MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeForeNormal8MS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getTextSizeHometitleMS
import com.messenger.phone.number.text.sms.service.apps.CommanClass.isfromprofile
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.adapter.ViewPagerAdapter
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityThemeColorBinding
import com.messenger.phone.number.text.sms.service.apps.fragment.MaterialcolorFragment
import com.messenger.phone.number.text.sms.service.apps.fragment.More_color_Fragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ThemeColorActivity : AppCompatActivity() {

    private var fontSize10 = 16f
    private var fontSize13 = 16f
    private var fontSize18 = 16f
    private var fontSize8 = 16f
    private var fontSize15 = 16f

    companion object {
        var tredid = -0L
    }

    lateinit var binding: ActivityThemeColorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocal()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_theme_color)
        setBaseTheme(binding.vAnd15StatusBar)
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
        tredid = intent.getLongExtra("treadid", -0L)
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(MaterialcolorFragment(), "material")
        adapter.addFragment(More_color_Fragment(), "more color")
        binding.viewpager.setAdapter(adapter)

        binding.resetBtn.setOnClickListener {
            if (isfromprofile) {
                if (tredid == -1L) {
                    toastMess(resources.getString(R.string.Something_Went_Wrong))
                    finish()
                } else {
                    config.removeProfileThemeColor(tredid.toString())
                    SystemGeneratedIconSwitchAb = true
                    finish()
                }

            } else {
                config.themeselectedcolor =
                    ContextCompat.getColor(this@ThemeColorActivity, R.color.appcolor)
                config.ismorecolorselected = false
                finish()
            }
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.materialColor.setOnClickListener {
            binding.viewpager.currentItem = 0
        }

        binding.moreColor.setOnClickListener {
            binding.viewpager.currentItem = 1
        }

        binding.viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
            }

            override fun onPageSelected(position: Int) {
                applyTabSelection(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        applyTabSelection(binding.viewpager.currentItem)
        ThemeSetup()

    }

    override fun onStart() {
        super.onStart()
        ThemeSetup()
    }

    override fun onResume() {
        super.onResume()
        ThemeSetup()
    }

    private fun applyTabSelection(position: Int) {
        val selectedColor = getProperPrimaryColor()
        val unselectedColor = getProperSecondaryTextColor()


        when(position){
            0 -> {
                binding.materialColor.alpha = 1f
                binding.moreColor.alpha = 0.25f
            }

            1 -> {
                binding.moreColor.alpha = 1f
                binding.materialColor.alpha = 0.25f
            }

            else -> {

            }

        }

        binding.materialColor.setTextColor(if (position == 0) selectedColor else unselectedColor)
        binding.moreColor.setTextColor(if (position == 1) selectedColor else unselectedColor)
    }

    private fun ThemeSetup() {
        val surfaceColor = getProperBackgroundColor()
        val statusBarColor = getProperStatusBarColor()
        val navigationBarColor = getBottomNavigationBackgroundColor()
        val textColor = getProperTextColor()
        val iconTintColor = getProperSecondaryTextColor()
        val primaryColor = getProperPrimaryColor()

        binding.main.setBackgroundColor(surfaceColor)
        binding.mainMenuAr.setBackgroundColor(surfaceColor)
        binding.toolBarMain.setBackgroundColor(surfaceColor)
        binding.defaulttoolshow.setBackgroundColor(surfaceColor)
        binding.viewpager.setBackgroundColor(surfaceColor)
        binding.vAnd15StatusBar.setBackgroundColor(statusBarColor)
        binding.backBtn.iconTint = ColorStateList.valueOf(iconTintColor)
        binding.selectAllMessage.imageTintList = ColorStateList.valueOf(iconTintColor)
        binding.textView3.setTextColor(textColor)
        binding.resetBtn.setTextColor(primaryColor)
        applyTabSelection(binding.viewpager.currentItem)

        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        window.decorView.setBackgroundColor(surfaceColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isStatusBarContrastEnforced = false
            window.isNavigationBarContrastEnforced = false
        }
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            val useLightBars = ThemeModeManager.shouldUseLightSystemBars(this@ThemeColorActivity)
            isAppearanceLightStatusBars = useLightBars
            isAppearanceLightNavigationBars = useLightBars
        }
    }
}
