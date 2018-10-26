package com.night.xvideos.activity

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
import com.night.xvideos.R
import com.night.xvideos.webView.SonicJavaScriptInterface
import com.night.xvideos.webView.SonicRuntimeImpl
import com.night.xvideos.webView.SonicSessionClientImpl
import com.tencent.sonic.sdk.SonicConfig
import com.tencent.sonic.sdk.SonicEngine
import com.tencent.sonic.sdk.SonicSession
import com.tencent.sonic.sdk.SonicSessionConfig
import kotlinx.android.synthetic.main.activity_videoplay.*

@Suppress("DEPRECATION", "UNUSED_EXPRESSION")
class VideoActivity : BaseActivity() {
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
            // step 1: Initialize sonic engine if necessary, or maybe u can do this when application created
            if (!SonicEngine.isGetInstanceAllowed()) {
                SonicEngine.createInstance(SonicRuntimeImpl(application), SonicConfig.Builder().build())
            }

            // step 2: Create SonicSession
            sonicSession = SonicEngine.getInstance().createSession(videoUrl, SonicSessionConfig.Builder().build())
            if (null != sonicSession) {
                sonicSessionClient = SonicSessionClientImpl()
                sonicSession!!.bindClient(sonicSessionClient)
            } else {
                // this only happen when a same sonic session is already running,
                // u can comment following codes to feedback as a default mode.
                throw UnknownError("create session fail!")
            }

            videoplay_webView.loadUrl(videoUrl)
            videoplay_webView.addJavascriptInterface(this, "fullscreen")
            videoplay_webView.addJavascriptInterface(JsObject(), "onClick")
            videoplay_webView.webViewClient = object : WebViewClient() {

                override fun onPageStarted(p0: WebView?, p1: String?, p2: Bitmap?) {
                    runOnUiThread {
                        p0?.visibility = View.GONE
                    }
                    super.onPageStarted(p0, p1, p2)
                }

                override fun shouldOverrideUrlLoading(p0: WebView?, p1: String?): Boolean {
                    if (p1?.contains("https://www.xvideos.com/video")!!) {
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
                        videoplay_webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    }
                }
            }
            runOnUiThread {
                val objectAnimator: ObjectAnimator = ObjectAnimator.ofFloat(videoplay_loding, "rotation", 0f, 360f)
                objectAnimator.duration = 1000
                objectAnimator.repeatMode = ValueAnimator.INFINITE
                objectAnimator.repeatCount = 5
                objectAnimator.interpolator = AccelerateDecelerateInterpolator()
                //todo 动画优化
                /*objectAnimator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                    }
                })*/
                objectAnimator.start()
            }
            /**
             * 设置WebChromeClient类
             */
            chromeClient = object : WebChromeClient() {
                /**
                 * 获取加载进度
                 */
                override fun onProgressChanged(view: WebView?, newProgress: Int) {

                    if (newProgress == 100) {
                        runOnUiThread {
                            videoplay_loding.visibility = View.GONE
                        }
                    }
                    super.onProgressChanged(view, newProgress)
                }

                override fun onShowCustomView(p0: View?, p1: WebChromeClient.CustomViewCallback?) {
                    fullScreen()
                    videoplay_webView.visibility = View.GONE
                    videoplay_Container.visibility = View.VISIBLE
                    videoplay_Container.addView(p0)
                    callBack = p1
                    super.onShowCustomView(p0, p1)
                }

                override fun onHideCustomView() {
                    fullScreen()
                    if (callBack != null) {
                        callBack!!.onCustomViewHidden()
                    }
                    videoplay_webView.visibility = View.VISIBLE
                    videoplay_Container.removeAllViews()
                    videoplay_Container.visibility = View.GONE
                    super.onHideCustomView()
                }
            }
            videoplay_webView.webChromeClient = chromeClient

    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initWebSetting() {
        val webSettings = videoplay_webView.settings
        webSettings.javaScriptEnabled = true
        videoplay_webView.removeJavascriptInterface("searchBoxJavaBridge_")
        intent.putExtra("loadUrlTime", System.currentTimeMillis())
        videoplay_webView.addJavascriptInterface(SonicJavaScriptInterface(sonicSessionClient, intent), "sonic")
        webSettings.allowContentAccess = true
        webSettings.databaseEnabled = true
        webSettings.savePassword = false
        webSettings.saveFormData = false
        webSettings.useWideViewPort = true
        webSettings.setAppCacheEnabled(true)
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true // 关键点
        webSettings.allowFileAccess = true // 允许访问文件
        webSettings.setSupportZoom(true) // 支持缩放
        webSettings.loadWithOverviewMode = true
        webSettings.cacheMode = WebSettings.LOAD_NORMAL
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings.domStorageEnabled = true
        webSettings.pluginState = WebSettings.PluginState.ON
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            videoplay_webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        // step 5: webview is ready now, just tell session client to bind
        if (sonicSessionClient != null) {
            sonicSessionClient!!.bindWebView(videoplay_webView)
            sonicSessionClient!!.clientReady()
        } else { // default mode

            videoUrl = "https://www.xvideos.com/embedframe/${intent.getStringExtra("VIDEOURL")
                    .substring(6)}"
            videoplay_webView.loadUrl(videoUrl)
        }
    }

    @SuppressLint("WrongConstant")

    /* //正则匹配网址URL
   private fun getData() {
       try {
           val pattern = Pattern.compile("(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]")
           val url = intent.getStringExtra("VIDEOURL")
           val matcher = pattern.matcher(url)
           while (matcher.find()) {
               videoUrl = matcher.group()
           }
       } catch (e: IOException) {
           intent.getStringExtra("VIDEOURL")
       }
   }*/

    protected fun fullScreen() {
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
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        videoplay_webView.saveState(outState)
        Log.e("mlog", "onSaveInstanceState")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        videoplay_webView.restoreState(savedInstanceState)
        Log.e("mlog", "onRestoreInstanceState")

    }

    override fun onPause() {
        super.onPause()
        videoplay_webView.onPause()
        videoplay_webView.pauseTimers()
        Log.e("mlog", "onPause")

    }

    override fun onResume() {
        super.onResume()
        videoplay_webView.onResume()
        videoplay_webView.resumeTimers()
        //sonicSession?.refresh()
        Log.e("mlog", "onResume")

    }


    override fun onDestroy() {
        Log.e("mlog", "onDestroy")
        /**
         * 防止内存泄露
         */
        if (videoplay_webView != null)
            videoplay_webView.loadDataWithBaseURL(null, "", "text/回头ml", "utf-8", null)
        videoplay_webView.clearHistory()

        (videoplay_webView.parent as ViewGroup).removeView(videoplay_webView)
        videoplay_webView.destroy()
        if (null != sonicSession) {
            sonicSession!!.destroy()
            sonicSession = null
        }
        super.onDestroy()
    }
}