package com.messenger.phone.number.text.sms.service.apps.CustomGallery.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.fragment.GalleryFragment
import com.messenger.phone.number.text.sms.service.apps.CustomGallery.fragment.RecentsFragment
import com.messenger.phone.number.text.sms.service.apps.R
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.annotations.Nullable
import javax.inject.Inject


class GalleryPagerAdapter @Inject constructor(@ApplicationContext var context: Context, supportFragmentManager: FragmentManager) : FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    var fragment: Fragment? = null
    override fun getItem(position: Int): Fragment {

        if (position == 0) fragment = RecentsFragment() else if (position == 1) fragment = GalleryFragment()
        return fragment!!
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title: String? = null
        if (position == 0) title = context.resources?.getString(R.string.Resents) else if (position == 1) title = context.resources?.getString(R.string.Gallery)
        return title
    }

    override fun getCount(): Int {
        return 2
    }
}