package com.codingpixel.dedicatekitchen.activities.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.activities.account.ChangePasswordActivity
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.databinding.ActivityVerificationCodeBinding
import com.codingpixel.dedicatekitchen.helpers.AppProgressDialog
import com.codingpixel.dedicatekitchen.helpers.ApplicationEnum
import com.codingpixel.dedicatekitchen.helpers.IntentParams
import com.codingpixel.dedicatekitchen.helpers.JsonManager
import com.codingpixel.dedicatekitchen.viewmodels.UserViewModel

class VerificationCodeActivity : BaseActivity() {
    private var code: Int = 0
    private var email: String = ""
    private var type: String = ""
    private lateinit var viewModel: UserViewModel
    private lateinit var mBinding: ActivityVerificationCodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_verification_code)
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        code = intent.getIntExtra(IntentParams.CODE, 0)
        email = intent.getStringExtra(IntentParams.EMAIL) ?: ""
        type = intent.getStringExtra(IntentParams.TYPE) ?: ""
        mBinding.tvEmailOrCell.text = email
        initClickListener()
        initTextWatcher()
        initApiResponseObserver()
    }


    private fun initApiResponseObserver() {
        viewModel.getDbApiStatus().observe(this) {
            when (it.applicationEnum) {
                ApplicationEnum.USER_VERIFY_SUCCESS -> {
                    showShortToast(it.message)
                    startActivity(
                        Intent(
                            this@VerificationCodeActivity,
                            SignInActivity::class.java
                        )
                    )
                    finishAffinity()
                }

                ApplicationEnum.USER_VERIFY_ERROR -> {
                    showWarningToast(message = it.message)
                    mBinding.etCode.setText("")
                }

                ApplicationEnum.RESEND_CODE_SUCCESS -> {
                    AppProgressDialog.dismissProgressDialog()
                    code = JsonManager.getInstance().getIntValue(it.resultObject, "code")
                    showShortToast(code.toString())
                }

                else -> {

                }
            }
        }
    }

    private fun initClickListener() {

        mBinding.ivBackArrow.setOnClickListener {
            finish()
        }

        mBinding.tvDidNotGetCode.setOnClickListener {
            if (type == "register") {
                resendCodeForSignUp(email)

            } else {
                startActivity(
                    Intent(
                        this@VerificationCodeActivity,
                        ForgotPasswordActivity::class.java
                    ).apply {
                        putExtra(IntentParams.FROM_DID_NOT_GET_CODE, true)
                    })
            }
        }

        mBinding.cvContinue.setOnClickListener {
            if (validateForm()) {
                if (code == mBinding.etCode.text.toString().toInt()) {
                    if (type == "register") {
                        showLoading()
                        verifyEmailApi()
                    } else
                        goToResetPasswordScreen()
                } else {
                    showWarningToast("Incorrect code")
                    mBinding.etCode.text.clear()
                }
            }
        }
    }

    private fun resendCodeForSignUp(email: String) {
        AppProgressDialog.showProgressDialog(this)
        viewModel.resendCode(email)
    }

    private fun goToResetPasswordScreen() {
        startActivity(
            Intent(this, ChangePasswordActivity::class.java).putExtra(
                IntentParams.TYPE,
                "resetPassword"
            ).putExtra(IntentParams.EMAIL, email).putExtra(IntentParams.CODE, code)
        )

    }

    private fun verifyEmailApi() {
        viewModel.verifyUser(email, code)
    }

    private fun validateForm(): Boolean {

        return if (mBinding.etCode.text.toString().trim().isEmpty()) {
            showShortToast(message = "Enter 4-digit verification code")
            false
        } else {
            true
        }
    }

    private fun initTextWatcher() {
        mBinding.etCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null)
                    setOtpNumbers(otp = s.toString())
                else
                    setOtpNumbers(otp = "")
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }


    private fun setOtpNumbers(otp: String) {
        when {
            otp.isEmpty() -> {
                mBinding.tvDigit1.text = ""
                mBinding.tvDigit2.text = ""
                mBinding.tvDigit3.text = ""
                mBinding.tvDigit4.text = ""
            }
            otp.length == 1 -> {
                mBinding.tvDigit1.text = otp
                mBinding.tvDigit2.text = ""
                mBinding.tvDigit3.text = ""
                mBinding.tvDigit4.text = ""
            }
            otp.length == 2 -> {
                mBinding.tvDigit1.text = otp.substring(0, 1)
                mBinding.tvDigit2.text = otp.substring(1, 2)
                mBinding.tvDigit3.text = ""
                mBinding.tvDigit4.text = ""
            }
            otp.length == 3 -> {
                mBinding.tvDigit1.text = otp.substring(0, 1)
                mBinding.tvDigit2.text = otp.substring(1, 2)
                mBinding.tvDigit3.text = otp.substring(2, 3)
                mBinding.tvDigit4.text = ""
            }
            otp.length == 4 -> {
                mBinding.tvDigit1.text = otp.substring(0, 1)
                mBinding.tvDigit2.text = otp.substring(1, 2)
                mBinding.tvDigit3.text = otp.substring(2, 3)
                mBinding.tvDigit4.text = otp.substring(3, 4)
            }
        }
    }
}