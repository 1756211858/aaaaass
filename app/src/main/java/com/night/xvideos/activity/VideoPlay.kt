package com.night.xvideos.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import co.metalab.asyncawait.async
import android.os.PersistableBundle
import android.util.Log
import android.view.*
import com.night.xvideos.getJs
import java.io.IOException
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.webkit.*
import android.widget.Toast
import com.night.xvideos.R
import com.night.xvideos.webView.SonicJavaScriptInterface
import com.night.xvideos.webView.SonicSessionClientImpl
import com.tencent.sonic.sdk.SonicSession
import kotlinx.android.synthetic.main.activity_videoplay.*
import java.lang.Thread.sleep


@Suppress("DEPRECATION", "UNUSED_EXPRESSION")
class VideoPlay : BaseActivity() {
    private lateinit var videoTitle: String
    lateinit var videoUrl: String
    private lateinit var videoImgUrl: String
    private var chromeClient: WebChromeClient? = null
    private var callBack: WebChromeClient.CustomViewCallback? = null
    private var sonicSession: SonicSession? = null
    private var sonicSessionClient: SonicSessionClientImpl? = null
    @SuppressLint("WrongConstant")
    override fun setLayoutId(): Int {
        return R.layout.activity_videoplay
    }

    @SuppressLint("WrongConstant", "JavascriptInterface")
    override fun initContentView() {
        videoTitle = intent.getStringExtra("VIDEOTITLE")
        videoImgUrl = intent.getStringExtra("VIDEOIMGURL")
        videoUrl = "https://www.xvideos.com/embedframe/${intent.getStringExtra("VIDEOURL")
                .substring(6)}"
        Log.e("mlog", videoUrl)
        //硬件加速
        window.addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
        //硬件加速
        window.setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
        //webView设置
        initWebSetting()
        videoPlayWebView.loadUrl(videoUrl)
        videoPlayWebView.addJavascriptInterface(this, "fullscreen")
        videoPlayWebView.addJavascriptInterface(JsObject(), "onClick")
        videoPlayWebView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(p0: WebView?, p1: String?, p2: Bitmap?) {
                runOnUiThread {
                    p0?.visibility = View.GONE
                    //屏蔽设置
                }
                super.onPageStarted(p0, p1, p2)
            }

            override fun shouldOverrideUrlLoading(p0: WebView?, p1: String?): Boolean {
                if (p1?.contains("https://www.xvideos.com/video")!!) {
                    p0?.loadUrl("javascript:function setTop(){document" +
                            ".querySelector('.related')" +
                            ".style.display=\"none\";}setTop();")
                    blockAds(p0)
                    p0?.stopLoading()
                    return true
                }
                p0?.loadUrl(p1)
                return true
            }

            override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
                val response: WebResourceResponse?
                if (url.contains("logo")) {
                    try {
                        val localCopy = assets.open("droidyue.png")
                        response = WebResourceResponse("image/png", "UTF-8", localCopy)
                        //response = WebResourceResponse(null, null, null)
                        return response
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                if (sonicSession != null) {
                    val requestResponse = sonicSessionClient?.requestResource(url)
                    if (requestResponse is WebResourceResponse) {
                        return requestResponse
                    }
                }
                return null
            }

            override fun onPageFinished(p0: WebView?, p1: String?) {
                async {
                    blockAds(p0)
                    p0?.loadUrl(getJs())
                    Thread.sleep(100)
                    p0?.visibility = View.VISIBLE
                    if (sonicSession != null) {
                        sonicSession!!.sessionClient.pageFinish(videoUrl)
                    }
                }
            }

            override fun onReceivedSslError(p0: WebView?, p1: SslErrorHandler?, p2: SslError?) {
                p1?.proceed()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    videoPlayWebView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                }
            }

            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                view?.loadUrl("file:///android_asset/videoError.html")
                async {
                    runOnUiThread {
                        videoPlayDescription.visibility = View.VISIBLE
                        Toast.makeText(applicationContext, "可能你的网络偷情养别人了.", Toast.LENGTH_SHORT).show()
                        sleep(5000)
                        finish()
                    }
                }
            }
        }
        runOnUiThread {
            videoPlayLodingImageView.setImageResource(R.drawable.loding)
            val objectAnimator: ObjectAnimator = ObjectAnimator.ofFloat(videoPlayLodingImageView, "rotation", 0f, 360f)
            objectAnimator.duration = 1000
            objectAnimator.repeatMode = ValueAnimator.INFINITE
            objectAnimator.repeatCount = 50
            objectAnimator.interpolator = AccelerateDecelerateInterpolator()
            objectAnimator.start()
        }

        chromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                lodingText.text = "加载中\n$newProgress%"
                if (newProgress == 100) {
                    runOnUiThread {
                        videoPlayLodingImageView.visibility = View.GONE
                        lodingText.visibility = View.GONE
                        cooperation.visibility = View.GONE
                    }
                }
                super.onProgressChanged(view, newProgress)
            }

            override fun onShowCustomView(p0: View?, p1: WebChromeClient.CustomViewCallback?) {
                fullScreen()
                videoPlayWebView.visibility = View.GONE
                videoplayContainer.visibility = View.VISIBLE
                videoplayContainer.addView(p0)
                callBack = p1
                super.onShowCustomView(p0, p1)
            }

            override fun onHideCustomView() {
                fullScreen()
                if (callBack != null) {
                    callBack!!.onCustomViewHidden()
                }
                videoPlayWebView.visibility = View.VISIBLE
                videoplayContainer.removeAllViews()
                videoplayContainer.visibility = View.GONE
                super.onHideCustomView()
            }

            override fun onReceivedTitle(view: WebView?, title1: String?) {

                super.onReceivedTitle(view, videoTitle)
            }
        }
        videoPlayWebView.webChromeClient = chromeClient

    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initWebSetting() {
        val webSettings = videoPlayWebView.settings
        webSettings.javaScriptEnabled = true
        videoPlayWebView.removeJavascriptInterface("searchBoxJavaBridge_")
        intent.putExtra("loadUrlTime", System.currentTimeMillis())
        videoPlayWebView.addJavascriptInterface(SonicJavaScriptInterface(sonicSessionClient, intent), "sonic")
        webSettings.allowContentAccess = true
        videoPlayWebView.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        webSettings.setNeedInitialFocus(false)//禁止webview上面控件获取焦点(黄色边框)
        webSettings.databaseEnabled = true
        webSettings.savePassword = false
        webSettings.saveFormData = false
        webSettings.useWideViewPort = false
        webSettings.setAppCacheEnabled(true)
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true // 关键点
        webSettings.allowFileAccess = true // 允许访问文件
        webSettings.setSupportZoom(true) // 支持缩放
        webSettings.loadWithOverviewMode = true
        webSettings.mediaPlaybackRequiresUserGesture = false
        webSettings.cacheMode = WebSettings.LOAD_NORMAL
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings.domStorageEnabled = true
        webSettings.pluginState = WebSettings.PluginState.ON

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            videoPlayWebView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        if (sonicSessionClient != null) {
            sonicSessionClient!!.bindWebView(videoPlayWebView)
            sonicSessionClient!!.clientReady()
        } else {

            videoUrl = "https://www.xvideos.com/embedframe/${intent.getStringExtra("VIDEOURL")
                    .substring(6)}"
            videoPlayWebView.loadUrl(videoUrl)
        }
    }

    @SuppressLint("WrongConstant")
    private fun fullScreen() {
        requestedOrientation = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun onConfigurationChanged(config: Configuration) {
        super.onConfigurationChanged(config)
        when (config.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }
            Configuration.ORIENTATION_PORTRAIT -> {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            }
        }
        Log.e("mlog", "onConfigurationChanged")
    }

    private inner class JsObject {
        @JavascriptInterface
        fun fullscreen() {
            //监听到用户点击全屏按钮
            fullScreen()
        }

        fun videoState(s: String) {
            Log.e("mlog", s)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        videoPlayWebView.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        videoPlayWebView.restoreState(savedInstanceState)

    }

    override fun onPause() {
        super.onPause()
        videoPlayWebView.onPause()
        videoPlayWebView.pauseTimers()

    }

    override fun onResume() {
        super.onResume()
        videoPlayWebView.onResume()
        videoPlayWebView.resumeTimers()
    }


    override fun onDestroy() {
        if (null != sonicSession) {
            sonicSession!!.destroy()
            sonicSession = null
        }
        if (videoPlayWebView != null) {
            videoPlayWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            videoPlayWebView.clearHistory()
            videoPlayWebView.removeAllViews()
            videoPlayWebView.destroy()
        }
        super.onDestroy()
    }
}