package com.codingpixel.dedicatekitchen.activities.account

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.activities.auth.SignInActivity
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.databinding.ActivityChangePasswordBinding
import com.codingpixel.dedicatekitchen.helpers.*
import com.codingpixel.dedicatekitchen.viewmodels.UserViewModel

class ChangePasswordActivity : BaseActivity() {

    private lateinit var mBinding: ActivityChangePasswordBinding
    private lateinit var viewModel: UserViewModel
    private var type: String = ""
    private var email: String = ""
    private var code: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_change_password)
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        changeStatusBarColor(statusBarColor = getColor(R.color.white))
        type = intent.getStringExtra(IntentParams.TYPE)!!
        email = intent.getStringExtra(IntentParams.EMAIL)!!
        code = intent.getIntExtra(IntentParams.CODE, 0)
        setViews()
        initClickListener()
        initApiResponseObserver()
    }

    private fun setViews() {
        if (type == "resetPassword") {
            hideView(mBinding.tvPassword)
            hideView(mBinding.tilPassword)
            hideView(mBinding.passwordSeparator)
        } else {
            showView(mBinding.tvPassword)
            showView(mBinding.tilPassword)
            showView(mBinding.passwordSeparator)
        }
    }

    private fun initApiResponseObserver() {
        viewModel.getDbApiStatus().observe(this) {
            when (it.applicationEnum) {

                ApplicationEnum.CHANGE_PASSWORD_SUCCESS -> {
                    viewModel.updateDataCandyPassword(
                        url = RequestBodyUtil.updateDataCandyPasswordUrl(
                            cardId = getLoggedInUser()?.dc_card_id ?: "",
                            password = mBinding.etNewPassword.text.toString().trim()
                        )
                    )
                }

                ApplicationEnum.UPDATE_DC_PROFILE_SUCCESS -> {
                    AppProgressDialog.dismissProgressDialog()
                    showShortToast(it.message)
                    finish()
                }

                ApplicationEnum.UPDATE_DC_PASSWORD_ERROR -> {
                    hideLoading()
                    showWarningToast(message = ApplicationEnum.UPDATE_DC_PASSWORD_ERROR.toString())
                }

                ApplicationEnum.RESET_PASSWORD_SUCCESS -> {
                    AppProgressDialog.dismissProgressDialog()
                    showShortToast(it.message)
                    startActivity(Intent(this, SignInActivity::class.java))
                    finish()
                }

                else -> {
                    AppProgressDialog.dismissProgressDialog()
                    showShortToast(it.message)
                }
            }
        }
    }

    private fun initClickListener() {
        mBinding.buttonSave.setOnClickListener {
            if (type == "changePassword") {
                if (checkDataEnteredForChangePassword()) {
                    AppProgressDialog.showProgressDialog(this)
                    changePasswordApi()
                }

            } else {
                if (checkDataEnteredForResetPassword()) {
                    AppProgressDialog.showProgressDialog(this)
                    resetPasswordApi()
                }

            }

        }

    }

    private fun resetPasswordApi() {
        viewModel.resetPassword(
            email,
            mBinding.etNewPassword.text.toString().trim(),
            mBinding.etConfirmPassword.text.toString().trim(), code = code
        )
    }

    private fun checkDataEnteredForChangePassword(): Boolean {
        if (isEmpty(mBinding.etPasswrod)) {
            showWarningToast("Enter Current Password.")
            return false
        }
        if (isEmpty(mBinding.etNewPassword)) {
            showWarningToast("Enter New Password.")
            return false
        }
        if (isEmpty(mBinding.etConfirmPassword)) {
            showWarningToast(" Enter Confirm Password.")
            return false
        } else if (!CommonMethods.validatePassword(
                text = mBinding.etNewPassword.text.toString().trim()
            )
        ) {
            showWarningToast(message = "Password must be like Qwerty@123 etc")
            return false
        }
        return if (isPasswordValid(mBinding.etNewPassword)) {
            if (!isPasswordMatch(mBinding.etNewPassword, mBinding.etConfirmPassword)) {
                showWarningToast("Password & Confirm Password must be same")
                false
            } else {
                true
            }
        } else {
            true
        }
    }

    private fun checkDataEnteredForResetPassword(): Boolean {
        if (isEmpty(mBinding.etNewPassword)) {
            showWarningToast("Enter New Password.")
            return false
        }
        if (isEmpty(mBinding.etConfirmPassword)) {
            showWarningToast(" Enter Confirm Password.")
            return false
        } else if (!CommonMethods.validatePassword(
                text = mBinding.etNewPassword.text.toString().trim()
            )
        ) {
            showWarningToast(message = "Password must be like Qwerty@123 etc")
            return false
        }
        return if (isPasswordValid(mBinding.etNewPassword)) {
            if (!isPasswordMatch(mBinding.etNewPassword, mBinding.etConfirmPassword)) {
                showWarningToast("Password & Confirm Password must be same")
                false
            } else {
                true
            }
        } else {
            true
        }
    }

    private fun changePasswordApi() {
        viewModel.changePassword(
            oldPassword = mBinding.etPasswrod.text.toString().trim(),
            newPassword = mBinding.etNewPassword.text.toString().trim(),
            confirmPassword = mBinding.etConfirmPassword.text.toString().trim()
        )
    }
}