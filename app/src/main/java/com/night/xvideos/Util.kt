package com.night.xvideos

import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast
import com.night.xvideos.activity.KadoYado
import java.io.IOException
import android.net.wifi.WifiManager
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException


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
    if (connectivityManager.activeNetworkInfo != null) {
        if (connectivityManager.activeNetworkInfo.isAvailable) {
            return true
        }
    }
    return false
}

/**
 * 获取当前ip地址
 */
fun getIPAddress(context: Context): String? {
    val info = (context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
    if (info != null && info.isConnected) {
        if (info.type == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
            try {
                //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                val en = NetworkInterface.getNetworkInterfaces()
                while (en.hasMoreElements()) {
                    val intf = en.nextElement()
                    val enumIpAddr = intf.inetAddresses
                    while (enumIpAddr.hasMoreElements()) {
                        val inetAddress = enumIpAddr.nextElement()
                        if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                            return inetAddress.getHostAddress()
                        }
                    }
                }
            } catch (e: SocketException) {
                e.printStackTrace()
            }

        } else if (info.type == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
            val wifiManager = context.applicationContext
                    .getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo
            return intIP2StringIP(wifiInfo.ipAddress)
        }
    } else {
        //当前无网络连接,请在设置中打开网络
    }
    return null
}

/**
 * 将得到的int类型的IP转换为String类型
 *
 * @param ip
 * @return
 */
fun intIP2StringIP(ip: Int): String {
    return (ip and 0xFF).toString() + "." +
            (ip shr 8 and 0xFF) + "." +
            (ip shr 16 and 0xFF) + "." +
            (ip shr 24 and 0xFF)
}


