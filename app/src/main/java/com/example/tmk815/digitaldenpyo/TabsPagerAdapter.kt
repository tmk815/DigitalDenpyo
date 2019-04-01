package com.example.tmk815.digitaldenpyo

class TabsPagerAdapter(
    fm: androidx.fragment.app.FragmentManager,
    private val tabsFragments: ArrayList<Class<out androidx.fragment.app.Fragment>>
) : androidx.fragment.app.FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return tabsFragments[position].newInstance()
    }

    override fun getCount(): Int {
        return tabsFragments.size
    }
}