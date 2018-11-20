package com.night.xvideos

import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast
import com.night.xvideos.activity.KadoYado
import java.io.IOException
import android.net.wifi.WifiManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import com.afollestad.materialdialogs.MaterialDialog
import com.night.xvideos.bean.ErrorVideo
import com.night.xvideos.retrofit.ApiStore
import com.night.xvideos.retrofit.ipAddressApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*


fun Any.ShortShow(mContext: Context, content: String) {
    Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show()
}

fun Any.LongShow(mContext: Context, content: String) {
    Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show()

}

/**
 * 网页的全屏按钮监听
 */
fun Any.getFullScreenJS(): String {
    //#hlsplayer > div.buttons-bar.right > img:nth-child(4)
    return "javascript:document.getElementsByClassName('buttons-bar.right')" +
            "[3].addEventListener('click',function(){onClick.fullscreen();return false;});"
}

/**
 *添加一个view
 */
fun Any.setView(): String {
    return "javascript:document.getElementsByClassName(‘’)"
}

/**
 * 获取当前的app版本
 */
fun Any.getVersionCode(mContext: Context): Float {
    var versionCode = 0.0F
    try {
        versionCode = mContext.packageManager
                .getPackageInfo(mContext.packageName, 0)
                .versionCode.toFloat()
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
        verName = mContext.packageManager
                .getPackageInfo(mContext.packageName, 0).versionName + ".apk"
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return verName
}

/**
 * 判断网络连接是否可用
 */
fun Any.isNetWorkAvailable(mContext: Context): Boolean {
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
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
            .activeNetworkInfo
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

/**
 * 获取iP地址
 */
fun Any.analyzeIP(context: Context): Boolean {
    val config = AppConfig()
    var bool: Boolean? = false
    try {
        val retrofit = Retrofit.Builder().baseUrl(config.baseUrl).build()
        val apiStore = retrofit.create(ipAddressApi::class.java)
        val call = apiStore.getIpAddress(getIPAddress(context)!!)
        call.enqueue(object : Callback<ApiStore> {
            override fun onFailure(call: Call<ApiStore>, t: Throwable) {

            }

            override fun onResponse(call: Call<ApiStore>, response: Response<ApiStore>) {
                val list = response.body()
                bool = true
            }
        })
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return bool!!
}

fun Any.showErrorVieoDialog(mContext: Context, errorVideo: ErrorVideo,
                            title: String, videoUrl: String, className: String) {
    val dialog = MaterialDialog.Builder(mContext)
            .title("报告视频错误")
            .content("谢谢你帮助作者删除无效视频，wish you happines！")
            .negativeText("取消").negativeColor(mContext.resources.getColor(R.color.black))
            .positiveText("确定").positiveColor(mContext.resources.getColor(R.color.buttonColor))
            .onPositive { _, _ ->
                errorVideo.videoUrl = videoUrl
                errorVideo.title = title
                errorVideo.className = className
                errorVideo.save(object : SaveListener<String>() {
                    override fun done(p0: String?, p1: BmobException?) {
                        Toast.makeText(mContext, "上传成功.", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            .cancelable(false)
            .show()
}

fun isVpnUsed(): Boolean {
    try {
        val niList = NetworkInterface.getNetworkInterfaces()
        if (niList != null) {
            for (intf in Collections.list(niList)) {
                if (!intf.isUp || intf.interfaceAddresses.size == 0) {
                    continue
                }
                Log.d("-----", "isVpnUsed() NetworkInterface Name: " + intf.name)
                if ("tun0" == intf.name || "ppp0" == intf.name) {
                    return true // The VPN is up
                }
            }
        }
    } catch (e: Throwable) {
        e.printStackTrace()
    }

    return false
}