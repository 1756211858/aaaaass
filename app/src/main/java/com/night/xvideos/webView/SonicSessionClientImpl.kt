package com.night.xvideos.webView

import android.os.Bundle
import com.tencent.smtt.sdk.WebView
import com.tencent.sonic.sdk.SonicSessionClient
import java.util.HashMap


class SonicSessionClientImpl : SonicSessionClient() {


    private var webView: WebView? = null

    fun bindWebView(webView: WebView) {
        this.webView = webView
    }

    fun getWebView(): WebView? {
        return webView
    }

    override fun loadUrl(url: String, extraData: Bundle) {
        webView!!.loadUrl(url)
    }

    override fun loadDataWithBaseUrl(baseUrl: String, data: String, mimeType: String, encoding: String, historyUrl: String) {
        webView!!.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl)
    }

    override fun loadDataWithBaseUrlAndHeader(baseUrl: String?, data: String?, mimeType: String?, encoding: String?, historyUrl: String?, headers: HashMap<String, String>?) {
        loadDataWithBaseUrl(baseUrl!!, data!!, mimeType!!, encoding!!, historyUrl!!)
    }

    fun destroy() {
        if (null != webView) {
            webView!!.destroy()
            webView = null
        }
    }
}