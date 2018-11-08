package com.night.xvideos

import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast
import com.night.xvideos.activity.KadoYado
import com.night.xvideos.main.Moudel
import java.io.IOException
import android.net.NetworkInfo


fun Any.ShortShow(mContext: Context, content: String) {
    Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show()
}

fun Any.LongShow(mContext: Context, content: String) {
    Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show()

}

/**
 * 网页的全屏按钮监听
 */
fun Any.getJs(): String {
    //#hlsplayer > div.buttons-bar.right > img:nth-child(4)
    return "javascript:document.getElementsByClassName('buttons-bar.right')[3].addEventListener('click',function(){onClick.fullscreen();return false;});"
}

/**
 * 获取当前的app版本
 */
fun Any.getVersionCode(mContext: Context): Int {
    var versionCode = 0
    try {
        versionCode = mContext.packageManager.getPackageInfo(mContext.packageName, 0).versionCode
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return versionCode
}

/**
 *获取版本名称
 */
fun Any.getVerName(mContext: Context): String {
    var verName = ""
    try {
        verName = mContext.packageManager.getPackageInfo(mContext.packageName, 0).versionName + ".apk"
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return verName
}

/**
 * 判断网络连接是否可用
 */
fun KadoYado.isNetWorkAvailable(mContext: Context): Boolean {
    val connectivityManager = mContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if(connectivityManager.activeNetworkInfo!=null){
        if (connectivityManager.activeNetworkInfo.isAvailable) {
            return true
        }
    }
    return false
}

