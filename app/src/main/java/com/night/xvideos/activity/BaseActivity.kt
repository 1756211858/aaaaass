package com.night.xvideos.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.webkit.WebView
import com.ironsource.mediationsdk.IronSource

abstract class BaseActivity : AppCompatActivity() {
    var view: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = layoutInflater.inflate(setLayoutId(), null)
        setContentView(view)
        initWebSetting()
        initContentView()
        initData()
    }

    //初始化数据
    protected open fun initData() {

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    abstract fun setLayoutId(): Int
    //webView相关设置
    protected open fun initWebSetting() {

    }

    override fun onResume() {
        IronSource.onResume(this)
        super.onResume()
    }

    override fun onPause() {
        IronSource.onPause(this)
        super.onPause()
    }

    //屏蔽广告
    protected open fun blockAds(view: WebView?) {
        //#hlsplayer > div.buttons-bar.right > div 设置按钮
        view?.loadUrl("javascript:function setTop(){document" +
                ".getElementById('externalplayer')" +
                ".style.display=\"none\";}setTop();")
        view?.loadUrl("javascript:function setTop(){document" +
                ".getElementsByClassName('xv-logo')[0].remove()}setTop();")

        view?.loadUrl("javascript:function setTop(){document" +
                ".querySelector('.video-title')" +
                ".style.display=\"none\";}setTop();")

    }

    //初始化View
    abstract fun initContentView()


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