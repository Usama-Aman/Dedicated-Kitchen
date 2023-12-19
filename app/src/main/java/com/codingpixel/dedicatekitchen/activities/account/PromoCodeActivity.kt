package com.codingpixel.dedicatekitchen.activities.account

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.databinding.ActivityPromoCodeBinding

class PromoCodeActivity : BaseActivity() {

    private lateinit var mBinding : ActivityPromoCodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_promo_code)
        changeStatusBarColor(statusBarColor = getColor(R.color.white))
    }
}