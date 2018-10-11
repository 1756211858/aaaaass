package com.night.xvideos.activity

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.JavascriptInterface

abstract class BaseActivity : AppCompatActivity() {
    private var view: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = layoutInflater.inflate(setLayoutId(), null)
        setContentView(view)
        initContentView()
        initWebSetting()
    }

    abstract fun setLayoutId(): Int
    //webView相关设置
    protected open fun initWebSetting() {

    }

    //屏蔽广告
    protected open fun blockAds(view: com.tencent.smtt.sdk.WebView?) {
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

    //初始化View
    abstract fun initContentView()


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

    protected open inner class JsObject {
        @JavascriptInterface
        fun fullscreen() {
            //监听到用户点击全屏按钮
            fullScreen()
        }
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
    ShortShow(this, "再按一次退出视频播放界面")
    } else {
    finish()
    }
    Log.e("mlog", "onBackPressed")
    }
    return super.onKeyDown(keyCode, event)
    }
     */
}