package com.codingpixel.dedicatekitchen.activities.auth

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.activities.dashboard.MainBottomBarActivity
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.databinding.ActivityAddMobileNumberBinding
import com.codingpixel.dedicatekitchen.helpers.IntentParams

class AddMobileNumberActivity : BaseActivity() {

    private lateinit var mBinding: ActivityAddMobileNumberBinding

    private var fromDidNotGetCode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_mobile_number)
        getIntentData()
        initClickListeners()
    }

    private fun getIntentData() {
        val dataIntent = intent
        if (dataIntent != null) {
            if (dataIntent.hasExtra(IntentParams.FROM_DID_NOT_GET_CODE) && dataIntent.getBooleanExtra(
                    IntentParams.FROM_DID_NOT_GET_CODE,
                    false
                )
            ) {
                fromDidNotGetCode = true
                mBinding.tvAddMobileNumber.text = getString(R.string.havent_received_code)
                mBinding.tvEnterNumberMessage.text = getString(R.string.check_correct_phone_number)
                mBinding.tvSendCode.text = getString(R.string.send_code_again)
                hideView(view = mBinding.tvSkip)
            }
        }
    }

    private fun validateForm(): Boolean {
        return if (mBinding.etPhoneNumber.text.toString().trim().isEmpty()) {
            showShortToast(message = "Enter Mobile Number")
            false
        } else {
            true
        }
    }

    private fun initClickListeners() {

        mBinding.ivBackArrow.setOnClickListener {
            finish()
        }

        mBinding.tvSkip.setOnClickListener {
            startActivity(Intent(this@AddMobileNumberActivity, MainBottomBarActivity::class.java))
            finishAffinity()
        }

        mBinding.btnSendCode.setOnClickListener {
            if (validateForm()) {
                startActivity(
                    Intent(
                        this@AddMobileNumberActivity,
                        VerificationCodeActivity::class.java
                    )
                )
            }
        }
    }
}