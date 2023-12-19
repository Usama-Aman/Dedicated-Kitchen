package com.codingpixel.dedicatekitchen.activities.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.codingpixel.dedicatekitchen.R
import com.codingpixel.dedicatekitchen.base.BaseActivity
import com.codingpixel.dedicatekitchen.databinding.WebviewPopupBinding

class AppWebViewClient : BaseActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    private lateinit var mBinding: WebviewPopupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.webview_popup)
        changeStatusBarColor(statusBarColor = getColor(R.color.colorPrimaryDark))
        val url = intent.getStringExtra("connectURL")
        val title = if (intent.hasExtra("title")) intent.getStringExtra("title") else ""
        mBinding.tvTitle.text = title
        mBinding.webview.webViewClient = object : WebViewClient() {


            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                hideView(view = mBinding.pbLoader)
            }
        }
        if (url != null) {
            mBinding.webview.loadUrl(url)
        }
    }
}
