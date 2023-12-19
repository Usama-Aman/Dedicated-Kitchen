package com.codingpixel.dedicatekitchen.activities.dashboard

import android.os.Bundle

import androidx.databinding.DataBindingUtil
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.databinding.ActivityScanQRCodeBinding
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

class ScanQRCodeActivity : BaseActivity() {

    private lateinit var mBinding: ActivityScanQRCodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_scan_q_r_code)
        setFullScreen()
        initBarCode()
    }

    private fun initBarCode() {

        mBinding.tvCardNumber.text = getLoggedInUser()?.dc_card_id ?: "No Card ID found"

        mBinding.tvName.text = "${getLoggedInUser()?.fname ?: ""} ${getLoggedInUser()?.lname ?: ""}"
        mBinding.tvEmail.text = getLoggedInUser()?.email ?: "N/A"

        try {
            showLoading()
            mBinding.ivBarCode.viewTreeObserver.addOnPreDrawListener {
                mBinding.ivBarCode.viewTreeObserver.removeOnPreDrawListener { true }
                val barcodeEncoder = BarcodeEncoder()
                val bitmap =
                    barcodeEncoder.encodeBitmap(
                        getLoggedInUser()?.dc_card_id ?: "",
                        BarcodeFormat.CODE_128,
                        mBinding.ivBarCode.measuredWidth + 0,
                        mBinding.ivBarCode.measuredHeight + 5
                    )
                hideLoading()
                if (bitmap != null)
                    mBinding.ivBarCode.setImageBitmap(bitmap)
                else {
                    this@ScanQRCodeActivity.runOnUiThread {
                        showWarningToast(message = "Could not generate Bar Code")
                    }
                }
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}