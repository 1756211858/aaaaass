package com.night.xvideos.activity

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.night.xvideos.R
import com.night.xvideos.ShortShow
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_videoplay.*

class MyDetail : BaseActivity() {
    override fun setLayoutId(): Int {

        return R.layout.activity_detail
    }

    override fun initContentView() {
        initLodingView()
        webView.loadUrl("https://github.com/SmokeJeason/downloadAPK")
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                detailLodingImageView.visibility= View.GONE
                super.onPageFinished(view, url)
            }

            override fun onReceivedError(view: WebView?, errorCode: Int,
                                         description: String?, failingUrl: String?) {
                detailLodingImageView.visibility= View.GONE
                view?.visibility=View.GONE
                ShortShow(mContext = applicationContext, content = "无网络连接.")
                super.onReceivedError(view, errorCode, description, failingUrl)
            }
        }
    }
    @SuppressLint("WrongConstant")
    private fun initLodingView() {
        detailLodingImageView.setImageResource(R.drawable.loding)
        val objectAnimator: ObjectAnimator = ObjectAnimator
                .ofFloat(videoPlayLodingImageView, "rotation", 0f, 360f)
        objectAnimator.duration = 1000
        objectAnimator.repeatMode = ValueAnimator.INFINITE
        objectAnimator.repeatCount = 50
        objectAnimator.interpolator = AccelerateDecelerateInterpolator()
        objectAnimator.start()
    }
}