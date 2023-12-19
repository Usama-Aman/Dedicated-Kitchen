package com.codingpixel.dedicatekitchen.activities.auth

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.databinding.ActivityAccountCreatedBinding

class AccountCreatedActivity : BaseActivity() {


    private lateinit var mBinding: ActivityAccountCreatedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_account_created)
        initClickListener()
    }

    private fun initClickListener() {
        mBinding.btnContinue.setOnClickListener {
            startActivity(Intent(this@AccountCreatedActivity, AddMobileNumberActivity::class.java))
            finish()
        }
    }
}