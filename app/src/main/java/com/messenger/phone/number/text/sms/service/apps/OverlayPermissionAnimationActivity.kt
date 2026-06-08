package com.messenger.phone.number.text.sms.service.apps

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.demo.adsmanage.helper.click
import com.messenger.phone.number.text.sms.service.apps.CommanClass.baseConfig
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityOverlayPermissionAnimationBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OverlayPermissionAnimationActivity : AppCompatActivity() {
    val binding: ActivityOverlayPermissionAnimationBinding by lazy {
        ActivityOverlayPermissionAnimationBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Log.d("OverlayPermissionAnimationActivity", "onCreate: --->${baseConfig.activeThemeSelection}")
        if (baseConfig.activeThemeSelection == 3) {
            binding.constMyApp.setBackgroundColor(getColor(R.color.overLayerAppBgDark))
            binding.txtAppTitle.setTextColor(getColor(R.color.overLayerAppNameDark))
            binding.txtAppDis.setTextColor(getColor(R.color.dark_gray))
        } else {
            binding.constMyApp.setBackgroundColor(getColor(R.color.overLayerAppBgLight))
            binding.txtAppTitle.setTextColor(getColor(R.color.overLayerAppNameLight))
            binding.txtAppDis.setTextColor(getColor(R.color.dark_gray))
        }
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updatePadding(bottom = navBars.bottom)
            insets
        }
        initViews()
    }

    private fun initViews() {

        binding.root.click {
            try {
                finish()
            } catch (_: Exception) {
            }
        }

        lifecycleScope.launch {
            delay(1000)
            binding.lottie.playAnimation()
            delay(2500)
            if (!isDestroyed) {
                binding.constMyApp.visibility = View.GONE
                finish()
            }
        }

    }
}
