package com.night.xvideos.activity

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.SystemClock
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.webkit.*
import co.metalab.asyncawait.async
import com.night.xvideos.R
import com.night.xvideos.ShortShow
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_videoplay.*
import java.util.*
import kotlin.concurrent.thread


class MyDetail : BaseActivity() {
    override fun setLayoutId(): Int {

        return R.layout.activity_detail
    }

    override fun initContentView() {
        showLodingView()
        detailWebView.settings.blockNetworkImage = false
        detailWebView.loadUrl("https://github.com/SmokeJeason/downloadAPK/blob/master/README.md")
        detailWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(webView: WebView, s: String): Boolean {
                return false
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                detailWebView.visibility=View.GONE
                async {
                    val timer = java.util.Timer(true)
                    val timerTask = object : TimerTask() {
                        override fun run() {
                            runOnUiThread {
                                if (detailWebView.progress < 20) {
                                    detailWebView.visibility = View.GONE
                                    detailLodingImageView.visibility = View.GONE
                                    thread {
                                        SystemClock.sleep(5000)
                                        finish()
                                    }
                                }
                            }
                        }
                    }
                    timer.schedule(timerTask, 5000)
                }
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                detailLodingImageView.visibility = View.GONE
                detailWebView.visibility=View.VISIBLE
                super.onPageFinished(view, url)
            }

            override fun onReceivedError(view: WebView?, errorCode: Int,
                                         description: String?, failingUrl: String?) {
                view?.loadUrl("file:///android_asset/videoPlayLodingError.html")
                detailLodingImageView.visibility = View.GONE
                view?.visibility = View.GONE
                ShortShow(mContext = applicationContext, content = "无网络连接")
                super.onReceivedError(view, errorCode, description, failingUrl)
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                handler?.proceed()// 接受所有网站的证书
                //super.onReceivedSslError(view, handler, error)
            }
        }
        detailWebView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                Log.e("mlog", newProgress.toString())
                if (newProgress == 100) {
                    runOnUiThread {
                        detailLodingImageView.visibility=View.GONE
                        detailWebView.visibility=View.VISIBLE
                    }
                }
                super.onProgressChanged(view, newProgress)
            }

        }
    }

    override fun onKeyDown(keyCode: Int, keyEvent: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//监听返回键，如果可以后退就后退
            if (detailWebView.canGoBack()) {
                detailWebView.goBack()
                return true
            }
        }
        return super.onKeyDown(keyCode, keyEvent)
    }

    @SuppressLint("WrongConstant")
    private fun showLodingView() {
        detailLodingImageView.setImageResource(R.drawable.loding)
        val objectAnimator: ObjectAnimator = ObjectAnimator.ofFloat(detailLodingImageView, "rotation", 0f, 360f)
        objectAnimator.duration = 1000
        objectAnimator.repeatMode = ValueAnimator.INFINITE
        objectAnimator.repeatCount = 50
        objectAnimator.interpolator = AccelerateDecelerateInterpolator()
        objectAnimator.start()
    }
}