package com.codingpixel.dedicatekitchen.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.codingpixel.dedicatekitchen.activities.meal_prep.CustomMealFragment
import com.codingpixel.dedicatekitchen.activities.meal_prep.MealPrepMenusActivity
import com.codingpixel.dedicatekitchen.activities.meal_prep.PreBuildFragment
import com.codingpixel.dedicatekitchen.fragments.account.AccountInformationDialogFragment
import com.codingpixel.dedicatekitchen.fragments.account.ChangePasswordFragment

class MealPrepMenuTabAdapter(
    private val myContext: Context?, fm: FragmentManager?, private var totalTabs: Int
) : FragmentPagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {

        return when (position) {
            0 ->  PreBuildFragment()
            1 -> CustomMealFragment()
            // 2 -> ArrangedChallenges()
            else -> PreBuildFragment()
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }
}