package com.codingpixel.dedicatekitchen.activities.checkout

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.databinding.PersonalInfoLayoutBinding

class PersonalInfo : BaseActivity() {
    private lateinit var mBinding: PersonalInfoLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.personal_info_layout)
        changeStatusBarColor(statusBarColor = getColor(R.color.grayBgColor))
    }
}