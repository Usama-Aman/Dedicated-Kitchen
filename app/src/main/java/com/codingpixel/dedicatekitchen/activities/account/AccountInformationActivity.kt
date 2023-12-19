package com.codingpixel.dedicatekitchen.activities.account

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.databinding.ActivityAccountInformationBinding
import com.codingpixel.dedicatekitchen.helpers.*
import com.codingpixel.dedicatekitchen.viewmodels.UserViewModel


class AccountInformationActivity : BaseActivity() {

    private lateinit var mBinding: ActivityAccountInformationBinding
    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_account_information)
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        changeStatusBarColor(statusBarColor = getColor(R.color.white))
        initApiResponseObserver()
        initClickListener()
        setData()
    }

    private fun initApiResponseObserver() {
        viewModel.getDbApiStatus().observe(this) {
            when (it.applicationEnum) {
                ApplicationEnum.UPDATE_PROFILE_SUCCESSS -> {
                    showShortToast(it.message)
                    val user = JsonManager.getInstance()
                        .getUser(jsonObject = it.resultObject, keyName = "user")
                    if (user != null) {
                        AppPreferenceManager.addUserToSharedPreferences(user = user)


                        var userPhone = mBinding.tvPhoneNumber.text.toString().trim()
//                        if (!userPhone.contains("+1")) {
//                            userPhone = "+1$userPhone"
//                        }
                        userPhone = userPhone.replace("(", "")
                        userPhone = userPhone.replace(")", "")
                        userPhone = userPhone.replace("-", "")
                        userPhone = userPhone.replace(" ", "")
                        hideLoading()

                        viewModel.updateDataCandyProfile(
                            url = RequestBodyUtil.updateDataCandyProfileUrl(
                                cardId = getLoggedInUser()?.dc_card_id ?: "",
                                firstName = mBinding.etFirstName.text.toString().trim(),
                                lastName = mBinding.etLastName.text.toString().trim(),
                                phone = mBinding.tvPhoneNumber.text.toString().trim()
                            ), phone = userPhone
                        )
                    } else {
                        showWarningToast(message = "Profile was not update on Server")
                        hideLoading()
                    }

                }

                ApplicationEnum.UPDATE_DC_PROFILE_SUCCESS -> {
                    hideLoading()
                    finish()
                }

                ApplicationEnum.UPDATE_DC_PROFILE_ERROR -> {
                    hideLoading()
                    showWarningToast(message = ApplicationEnum.UPDATE_DC_PROFILE_ERROR.toString())
                }

                else -> {
                    AppProgressDialog.dismissProgressDialog()
                    showShortToast(it.message)

                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setData() {
        mBinding.etEmailAddress.text = getLoggedInUser()?.email ?: ""
        mBinding.etFirstName.setText(getLoggedInUser()?.fname ?: "")
        mBinding.etLastName.setText(getLoggedInUser()?.lname ?: "")
        //if (!getLoggedInUser()!!.phone.isNullOrEmpty())
        mBinding.tvPhoneNumber.text = getLoggedInUser()?.phone ?: ""
        mBinding.etLoyaltyCardId.setText(getLoggedInUser()?.dc_card_id ?: "N/A")
    }

    private fun initClickListener() {
        mBinding.buttonSave.setOnClickListener {

            if (validate())
                updateInfoApi()
        }
    }

    private fun validate(): Boolean {
        when {

            mBinding.etFirstName.text.toString().trim().isEmpty() -> {
                showWarningToast(message = "First name is required")
                return false
            }

            mBinding.etLastName.text.toString().trim().isEmpty() -> {
                showWarningToast(message = "Last name is required")
                return false
            }

            mBinding.tvPhoneNumber.text.toString().trim().isEmpty() -> {
                showWarningToast(message = "Phone number is required")
                return false
            }

            else -> {
                return true
            }
        }
    }

    private fun updateInfoApi() {
        AppProgressDialog.showProgressDialog(this)
        viewModel.updateInfo(
            getLoggedInUser()!!.email,
            mBinding.etFirstName.text.toString().trim(),
            mBinding.etLastName.text.toString().trim(),
            mBinding.tvPhoneNumber.text.toString().trim()
        )
    }
}