package com.codingpixel.dedicatekitchen.activities.onboarding

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.activities.auth.RegisterActivity
import com.codingpixel.dedicatekitchen.activities.auth.SignInActivity
import com.codingpixel.dedicatekitchen.activities.dashboard.MainBottomBarActivity
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.databinding.ActivityIntermediateBinding
import com.codingpixel.dedicatekitchen.helpers.ApplicationEnum
import com.codingpixel.dedicatekitchen.helpers.RequestBodyUtil
import com.codingpixel.dedicatekitchen.viewmodels.SessionViewModel

class IntermediateActivity : BaseActivity() {

    private lateinit var mBinding: ActivityIntermediateBinding
    private lateinit var viewModel: SessionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_intermediate)
        viewModel = ViewModelProvider(this).get(SessionViewModel::class.java)
        initApiResponseObserver()
        setFullScreen()
        initClickListener()

        /*Edited by Usama*/
//        initSessionViewModel()
    }

    private fun initApiResponseObserver() {
        viewModel.getApiStatus().observe(this) {
            when (it.enum) {
                ApplicationEnum.SESSION_RESTORED -> {
//                    val sessionId = it.dataObject.getJSONObject("session").getString("id")
//                    AppPreferenceManager.setValue(
//                        "sessionId",
//                        sessionId
//                    )
                }
                else -> {

                }
            }
        }
    }

    private fun initClickListener() {
        mBinding.cvSignIn.setOnClickListener {
            startActivity(Intent(this@IntermediateActivity, SignInActivity::class.java))
        }

        mBinding.cvRegister.setOnClickListener {
            startActivity(Intent(this@IntermediateActivity, RegisterActivity::class.java))
        }

        mBinding.btnSignInFacebook.setOnClickListener {
            showShortToast(message = "Facebook")
        }

        mBinding.tvContinueAsGuest.setOnClickListener {
            startActivity(Intent(this@IntermediateActivity, MainBottomBarActivity::class.java))
            finishAffinity()
        }

        mBinding.btnSignInGoogle.setOnClickListener {
            showShortToast(message = "Google")
        }
    }
}