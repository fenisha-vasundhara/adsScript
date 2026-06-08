package com.messenger.phone.number.text.sms.service.apps.CustomGallery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setWallpaperdone
import com.messenger.phone.number.text.sms.service.apps.CommanClass.updateStatusbarColorFullApp
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.adapter.GalleryPagerAdapter
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityGalleryBinding
import com.messenger.phone.number.text.sms.service.apps.setBaseTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GalleryActivity : AppCompatActivity() {
    lateinit var binding: ActivityGalleryBinding

    @Inject
    lateinit var galleryPagerAdapter: GalleryPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery)
        setBaseTheme(binding.vAnd15StatusBar)
        binding.galleryViewpager.adapter = galleryPagerAdapter
        binding.galleryTablayout.setupWithViewPager(binding.galleryViewpager)

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
        updateStatusbarColorFullApp()
    }
}