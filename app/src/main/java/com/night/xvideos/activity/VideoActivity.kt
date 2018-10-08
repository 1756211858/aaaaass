package com.night.xvideos.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_videoplay.*
import android.webkit.WebSettings
import co.metalab.asyncawait.async
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import android.content.pm.ActivityInfo
import android.os.PersistableBundle
import android.util.Log
import android.view.*
import android.webkit.JavascriptInterface
import com.night.xvideos.getJs
import com.tencent.smtt.export.external.interfaces.*
import java.io.IOException
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import com.night.xvideos.R
import com.tencent.smtt.sdk.QbSdk
import java.util.regex.Pattern


@Suppress("DEPRECATION")
class VideoActivity : AppCompatActivity() {
    lateinit var videoTitle: String
    lateinit var videoUrl: String
    lateinit var videoImgUrl: String
    var chromeClient: WebChromeClient? = null
    private var callBack: IX5WebChromeClient.CustomViewCallback? = null
    @SuppressLint("WrongConstant")
    private fun getVideoUrl() {
        try {
            val pattern = Pattern.compile("(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]")
            val url=intent.getStringExtra("VIDEOURL")
            val matcher = pattern.matcher(url)
            while(matcher.find()){
                videoUrl =  matcher.group()
            }
        }
        catch (e:IOException){
            "https://www.xvideos.com/embedframe/31433477"
        }
        Log.e("mlog", videoUrl)
    }

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("mlog", "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videoplay)
        getVideoUrl()
        videoTitle = intent.getStringExtra("VIDEOTITLE")
        videoImgUrl = intent.getStringExtra("VIDEOIMGURL")
        val cb = object : QbSdk.PreInitCallback {
            override fun onViewInitFinished(arg0: Boolean) {
                Log.d("app", " onViewInitFinished is $arg0")
            }

            override fun onCoreInitFinished() {}
        }
        QbSdk.initX5Environment(applicationContext, cb)
        //硬件加速
        window.setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
        initWebSetting()
        videoplay_webView.addJavascriptInterface(this, "fullscreen")
        //屏蔽广告，获取标题，加载Url
        async {
            if (savedInstanceState == null) {
                videoplay_webView.loadUrl(videoUrl)
                videoplay_webView.addJavascriptInterface(JsObject(), "onClick")
                videoplay_webView.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(p0: com.tencent.smtt.sdk.WebView?, p1: String?): Boolean {
                        if (p1?.contains("https://www.xvideos.com/video")!!) {
                            blockAds(p0)
                            p0?.stopLoading()
                            return true
                        }
                        p0?.loadUrl(p1)
                        return true
                    }

                    override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
                        var response: WebResourceResponse? = null
                        if (url.contains("logo")) {
                            try {
                                val localCopy = assets.open("droidyue.png")
                                response = WebResourceResponse("image/png", "UTF-8", localCopy)
                                //response = WebResourceResponse(null, null, null)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                        return response
                    }

                    override fun onReceivedError(p0: WebView?, p1: Int, p2: String?, p3: String?) {
                        super.onReceivedError(p0, p1, p2, p3)
                    }

                    override fun onPageStarted(p0: WebView?, p1: String?, p2: Bitmap?) {
                        runOnUiThread {
                            p0?.visibility = View.GONE
                        }
                        super.onPageStarted(p0, p1, p2)
                    }

                    override fun onPageFinished(p0: WebView?, p1: String?) {
                        async {
                            blockAds(p0)
                            p0?.loadUrl(getJs())
                            Thread.sleep(100)
                            p0?.visibility = View.VISIBLE
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
                    objectAnimator.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                        }
                    })
                    objectAnimator.start()
                }
                /**
                 * 设置WebChromeClient类
                 */
                chromeClient = object : WebChromeClient() {
                    /**
                     * 获取加载进度
                     */
                    override fun onProgressChanged(view: com.tencent.smtt.sdk.WebView?, newProgress: Int) {

                        if (newProgress == 100) {
                            runOnUiThread {
                                videoplay_loding.visibility = View.GONE
                            }
                        }
                        super.onProgressChanged(view, newProgress)
                    }

                    override fun onShowCustomView(p0: View?, p1: IX5WebChromeClient.CustomViewCallback?) {
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
        }
    }


    /**
     * 初始化WebView设置
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebSetting() {
        val webSettings = videoplay_webView.settings
        webSettings.setAppCacheEnabled(true)
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true // 关键点
        webSettings.allowFileAccess = true // 允许访问文件
        webSettings.setSupportZoom(true) // 支持缩放
        webSettings.loadWithOverviewMode = true
        webSettings.cacheMode = WebSettings.LOAD_NORMAL
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings.domStorageEnabled = true
        webSettings.pluginState = com.tencent.smtt.sdk.WebSettings.PluginState.ON
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            videoplay_webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        videoplay_webView.settings.useWideViewPort = true
    }

    /**
     * 屏蔽广告
     */
    fun blockAds(view: com.tencent.smtt.sdk.WebView?) {
        //屏蔽右下角的Button
        view?.loadUrl("javascript:function setTop(){document" +
                ".querySelector('buttons-bar.right')[3]" +
                ".style.display=\"none\";}setTop();")
        view?.loadUrl("javascript:function setTop(){document" +
                ".querySelector('.xv-logo')" +
                ".style.display=\"none\";}setTop();")

        view?.loadUrl("javascript:function setTop(){document" +
                ".querySelector('.video-title')" +
                ".style.display=\"none\";}setTop();")
    }


    /**
     * 监听返回键~

    var timeSpace: Long = 0

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
    if (keyCode == KeyEvent.KEYCODE_BACK) {

    if (System.currentTimeMillis() - timeSpace > 2000) {
    timeSpace = System.currentTimeMillis()
    if (chromeClient != null) {
    chromeClient!!.onHideCustomView()
    }
    ToastShortShow(this, "再按一次退出视频播放界面")
    } else {
    finish()
    }
    Log.e("mlog", "onBackPressed")
    }
    return super.onKeyDown(keyCode, event)
    }
     */

    private fun fullScreen() {
        requestedOrientation = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }


    private open inner class JsObject {

        @JavascriptInterface
        fun fullscreen() {
            //监听到用户点击全屏按钮
            fullScreen()
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
        Log.e("mlog", "onResume")

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
        super.onDestroy()
    }
}