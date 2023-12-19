package com.codingpixel.dedicatekitchen.activities.auth

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.activities.dashboard.MainBottomBarActivity
import com.codingpixel.dedicatekitchen.databinding.ActivitySignInBinding
import com.codingpixel.dedicatekitchen.helpers.*
import com.codingpixel.dedicatekitchen.viewmodels.UserViewModel

class SignInActivity : BaseActivity() {

    private lateinit var mBinding: ActivitySignInBinding

    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
//        window.decorView.systemUiVisibility = (
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
//        window.statusBarColor = Color.TRANSPARENT
        changeStatusBarColor(statusBarColor = getColor(R.color.appColor))
        initClickListener()
        initViewModel()
        initApiResponseObserver()
    }

    private fun initApiResponseObserver() {
        viewModel.getDbApiStatus().observe(this) {

            run {
                hideLoading()
                when (it.applicationEnum) {

                    ApplicationEnum.LOG_IN_ACCOUNT_NOT_VERIFIED -> {

                        val otpCode = JsonManager.getInstance().getString(
                            jsonObject = it.resultObject,
                            keyName = "code"
                        )
                        if (otpCode.isNotEmpty()) {
                            startActivity(
                                Intent(
                                    this@SignInActivity,
                                    VerificationCodeActivity::class.java
                                ).apply {
                                    putExtra(IntentParams.CODE, otpCode)
                                    putExtra(IntentParams.EMAIL, mBinding.etEmail.toString().trim())
                                    putExtra(IntentParams.TYPE, "register")
                                }
                            )
                        } else {
                            showWarningToast(message = "Account Not Verified, But Got Empty Code")
                        }
                    }

                    ApplicationEnum.LOG_IN_SUCCESS -> {
                        val user = JsonManager.getInstance()
                            .getUser(jsonObject = it.resultObject, keyName = "user")
                        val token = JsonManager.getInstance().getString(it.resultObject, "token")
                        if (user != null) {
                            AppPreferenceManager.addUserToSharedPreferences(user = user)
                        }
                        AppPreferenceManager.setValue(PrefFlags.ACCESS_TOKEN, token)
                        startActivity(
                            Intent(
                                this@SignInActivity,
                                MainBottomBarActivity::class.java
                            )
                        )
                        finishAffinity()
                    }

                    ApplicationEnum.LOG_IN_ERROR -> {
                        errorDialogue(it.message, this@SignInActivity)
                    }

                    else -> {
                        errorDialogue(it.message, this@SignInActivity)
                    }
                }

            }
        }
    }

    private fun initViewModel() {

    }

    private fun initClickListener() {
        mBinding.ivBackArrow.setOnClickListener {
            finish()
        }

        mBinding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this@SignInActivity, ForgotPasswordActivity::class.java))
        }

        mBinding.cvSignIn.setOnClickListener {
            if (checkDataEntered()) {
                loginUserApi()
                //AppProgressDialog.showProgressDialog(this)
            }
        }
        mBinding.tvSkip.setOnClickListener {
            //throw RuntimeException("Test Crash")
            startActivity(Intent(this@SignInActivity, MainBottomBarActivity::class.java))
            finishAffinity()
        }
        mBinding.tvSignUp.setOnClickListener {
            startActivity(Intent(this@SignInActivity, RegisterActivity::class.java))
            finish()
        }
    }

    private fun loginUserApi() {
        showLoading()
        viewModel.login(
            email = mBinding.etEmail.text.toString().trim(),
            password = mBinding.etPassword.text.toString().trim()
        )
    }

    private fun checkDataEntered(): Boolean {
        if (isEmpty(mBinding.etEmail)) {
            showWarningToast("Enter Email Address.")
            return false
        }
//        if (isEmpty(mBinding.etPhone)) {
//            errorDialogue("Enter Phone Number.", this)
//            return false
//        }
        if (!isEmpty(mBinding.etEmail)) {
            if (!isEmail(mBinding.etEmail)) {
                showWarningToast("Enter a Valid Email Address.")
                return false
            }
        }
        return if (isEmpty(mBinding.etPassword)) {
            showWarningToast("Enter Password.")
            false
        } else true
    }
}
