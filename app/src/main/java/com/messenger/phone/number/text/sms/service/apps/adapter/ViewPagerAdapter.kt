package com.messenger.phone.number.text.sms.service.apps.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import io.reactivex.annotations.Nullable

class ViewPagerAdapter : FragmentPagerAdapter {
    private final var fragmentList1: ArrayList<Fragment> = ArrayList()
    private final var fragmentTitleList1: ArrayList<String> = ArrayList()

    public constructor(supportFragmentManager: FragmentManager)
            : super(supportFragmentManager)

    override fun getItem(position: Int): Fragment {
        return fragmentList1.get(position)
    }

    override fun getPageTitle(position: Int): CharSequence {
        return fragmentTitleList1.get(position)
    }

    override fun getCount(): Int {
        return fragmentList1.size
    }

    // this function adds the fragment and title in 2 separate  arraylist.
    fun addFragment(fragment: Fragment, title: String) {
        fragmentList1.add(fragment)
        fragmentTitleList1.add(title)
    }
}