package com.codingpixel.dedicatekitchen.activities.auth

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.databinding.ActivityForgotPasswordBinding
import com.codingpixel.dedicatekitchen.helpers.*
import com.codingpixel.dedicatekitchen.viewmodels.UserViewModel

class ForgotPasswordActivity : BaseActivity() {

    private lateinit var mBinding: ActivityForgotPasswordBinding
    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        initClickListener()
        initApiResponseObserver()
    }

    private fun initApiResponseObserver() {
        viewModel.getDbApiStatus().observe(this) {
            when (it.applicationEnum) {
                ApplicationEnum.FORGET_PASSWORD_SUCCESS -> {
                    AppProgressDialog.dismissProgressDialog()
                    successPopup(it.message, this, false)
                    val code = JsonManager.getInstance().getIntValue(it.resultObject, "code")
                    val email = JsonManager.getInstance().getString(it.resultObject, "email")
                    startActivity(
                        Intent(
                            this@ForgotPasswordActivity,
                            VerificationCodeActivity::class.java
                        ).putExtra(IntentParams.CODE, code).putExtra(IntentParams.EMAIL, email)
                            .putExtra(IntentParams.TYPE, "forgot")
                    )
                }
                ApplicationEnum.FORGET_PASSWORD_ERROR -> {
                    AppProgressDialog.dismissProgressDialog()
                    errorDialogue(it.message, this)
                }
                else -> {
                    AppProgressDialog.dismissProgressDialog()
                    errorDialogue(it.message, this)
                }
            }
        }
    }

    private fun initClickListener() {
        mBinding.ivBackArrow.setOnClickListener {
            finish()
        }

        mBinding.btnSendLink.setOnClickListener {
            if (validateForm()) {
                AppProgressDialog.showProgressDialog(this)
                forgetPasswordApi()
//                startActivity(Intent(this@ForgotPasswordActivity, SignInActivity::class.java))
//                finish()
            }
        }
    }

    private fun forgetPasswordApi() {
        viewModel.forgetPassword(mBinding.etEmai.text.toString().trim())

    }

    private fun validateForm(): Boolean {
        return if (mBinding.etEmai.text.toString().trim().isEmpty()) {
            showShortToast(message = "Email address required")
            false
        } else if (!CommonMethods.isValidEmail(target = mBinding.etEmai.text.toString().trim())) {
            showShortToast(message = "Enter valid email address")
            false
        } else {
            true
        }
    }
}