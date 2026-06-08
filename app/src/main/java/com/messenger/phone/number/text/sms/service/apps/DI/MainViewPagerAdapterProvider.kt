package com.messenger.phone.number.text.sms.service.apps.DI

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.messenger.phone.number.text.sms.service.apps.adapter.MainViewPagerAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@InstallIn(ActivityComponent::class)
@Module
class MainViewPagerAdapterProvider {

    @Provides
    fun setMainViewPagerAdapterProvider(fragment: FragmentActivity): MainViewPagerAdapter {
        return MainViewPagerAdapter(fragment.supportFragmentManager)
    }

}