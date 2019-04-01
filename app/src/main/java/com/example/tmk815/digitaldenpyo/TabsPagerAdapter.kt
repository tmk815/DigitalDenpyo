package com.example.tmk815.digitaldenpyo

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class TabsPagerAdapter(
    fm: androidx.fragment.app.FragmentManager,
    tabsFragments: ArrayList<Class<out androidx.fragment.app.Fragment>>
) : androidx.fragment.app.FragmentPagerAdapter(fm) {
    val tabsFragments: ArrayList<Class<out androidx.fragment.app.Fragment>> = tabsFragments

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return tabsFragments[position].newInstance()
    }

    override fun getCount(): Int {
        return tabsFragments.size
    }
}