package com.codingpixel.dedicatekitchen.activities.common


import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.databinding.DataBindingUtil
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.databinding.ActivityWebViewBinding
import com.codingpixel.dedicatekitchen.helpers.IntentParams

class WebViewActivity : BaseActivity() {

    private lateinit var mBinding: ActivityWebViewBinding

    private var title: String = ""
    private var webUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_web_view)
        getIntentData()
        setUpWebView()
        setData()
        initClickListener()
    }

    private fun initClickListener() {
        mBinding.ivBackArrow.setOnClickListener {
            finish()
        }
    }

    private fun getIntentData() {
        val dataIntent = intent
        if (dataIntent != null) {
            if (dataIntent.hasExtra(IntentParams.TITLE))
                title = dataIntent.getStringExtra(IntentParams.TITLE)!!

            if (dataIntent.hasExtra(IntentParams.WEB_URL))
                webUrl = dataIntent.getStringExtra(IntentParams.WEB_URL)!!
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView() {
        mBinding.webView.settings.javaScriptEnabled = true
//        mBinding.webView.clearMatches()
//        mBinding.webView.clearFormData()
//        mBinding.webView.clearSslPreferences()
//        CookieManager.getInstance().removeAllCookies {
//            if (it)
//                Log.d("Remove Cookies", "Suuccess")
//            else
//                Log.d("Remove Cookies", "Failed")
//        }
        mBinding.webView.webChromeClient = object : WebChromeClient() {

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                mBinding.pbLoading.progress = newProgress
                if (newProgress == 100) {
                    hideView(view = mBinding.pbLoading)
                }
                //super.onProgressChanged(view, newProgress)
            }

        }
//
//        mBinding.webView.webViewClient = object : WebViewClient() {
//
//            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
//                super.onPageStarted(view, url, favicon)
//
//                if (connectStripe) {
//                    if (url != null && url.isNotEmpty() && url.contains("dashboard/success")) {
//                        Log.d("WebUrl", url)
//                        //http://139.162.3.157/drawsome-dev/dashboard/success
//                        setResult(Activity.RESULT_OK, Intent().apply {
//                            putExtra(IntentParams.STRIPE_ACCOUNT_STATUS, true)
//                        })
//
//                        // Set Dummy Payment Id
//                        AppPreferenceManager.addUserToSharedPreferences(
//                            user = AppPreferenceManager.getUserFromSharedPreferences()?.apply {
//                                stripe_payout_account_id = "asas"
//                            }!!
//                        )
//                        finish()
//                    } else {
//                        Log.d("WebUrl", "Null/Empty")
//                    }
//                } else
//                    Log.d("Not Stripe Flow", "True")
//
//            }
//
//            override fun onPageFinished(view: WebView?, url: String?) {
//                super.onPageFinished(view, url)
//
//            }
//
//
//        }
    }

    private fun setData() {
        mBinding.tvTitle.text = title
        mBinding.webView.loadUrl(webUrl)
    }
}