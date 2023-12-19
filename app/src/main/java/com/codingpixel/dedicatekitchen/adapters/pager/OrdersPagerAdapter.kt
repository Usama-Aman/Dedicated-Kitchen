package com.codingpixel.dedicatekitchen.adapters.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.codingpixel.dedicatekitchen.fragments.orders.ActiveOrdersFragment
import com.codingpixel.dedicatekitchen.fragments.orders.PastOrdersFragment
import com.codingpixel.dedicatekitchen.helpers.Constants

class OrdersPagerAdapter(fragmentManager: FragmentManager) :
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {


    override fun getItem(position: Int): Fragment {
//        return when (position) {
//            0 -> ActiveOrdersFragment.newInstance()
//            1 -> PastOrdersFragment.newInstance()
//            else -> ActiveOrdersFragment.newInstance()
//        }
        return PastOrdersFragment.newInstance()
    }
    override fun getCount(): Int {
//        return 2
        return 1
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {

//            0 -> Constants.ALL_ORDERS
//
//            1 -> Constants.HISTORY

            else -> Constants.HISTORY

        }
    }
}