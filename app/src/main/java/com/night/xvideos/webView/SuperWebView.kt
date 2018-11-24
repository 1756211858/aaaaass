package com.night.xvideos.webView

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView

class SuperWebView : WebView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    //重写onScrollChanged 方法
    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        scrollTo(0, 0)
    }
}
