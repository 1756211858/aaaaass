package com.night.xvideos.activity

import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.night.xvideos.R
import kotlinx.android.synthetic.main.activity_detail.*

class MyDetail : BaseActivity() {
    override fun setLayoutId(): Int {

        return R.layout.activity_detail
    }

    override fun initContentView() {
        webView.loadUrl("https://github.com/SmokeJeason/downloadAPK")
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true
            }
        }
    }
}