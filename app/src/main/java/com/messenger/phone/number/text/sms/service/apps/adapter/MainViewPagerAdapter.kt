package com.messenger.phone.number.text.sms.service.apps.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.messenger.phone.number.text.sms.service.apps.fragment.AllMassageFragment
import com.messenger.phone.number.text.sms.service.apps.fragment.PersonalFragment
import com.messenger.phone.number.text.sms.service.apps.fragment.UnknownFragment
import javax.inject.Inject

class MainViewPagerAdapter @Inject constructor(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        return if (position == 0) AllMassageFragment() else if (position == 1) PersonalFragment() else UnknownFragment()
    }

    override fun getPageTitle(position: Int): CharSequence {
        var title: String? = null
        return if (position == 0) "All Message" else if (position == 1) "Personal Message" else "Unknown Message"
    }
}