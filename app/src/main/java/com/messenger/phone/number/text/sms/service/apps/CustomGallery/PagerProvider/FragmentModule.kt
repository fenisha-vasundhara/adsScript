package com.messenger.phone.number.text.sms.service.apps.CustomGallery.PagerProvider

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.adapter.GalleryPagerAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@InstallIn(ActivityComponent::class)
@Module
class FragmentModule {

    @Provides
    fun provideMainViewPagerAdapter(@ApplicationContext context: Context, activity: FragmentActivity): GalleryPagerAdapter {
        return GalleryPagerAdapter(context, activity.supportFragmentManager)
    }

}