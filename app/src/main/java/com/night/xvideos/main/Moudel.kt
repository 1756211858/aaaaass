package com.night.xvideos.main

import android.content.Context
import android.util.Log
import com.night.xvideos.AppConfig
import com.night.xvideos.getIPAddress
import com.night.xvideos.retrofit.ApiStore
import com.night.xvideos.retrofit.ipAddressApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class Moudel {
    var mContext:Context?=null

    private var callBack : ((Boolean ) -> Unit)? = null
    /**
     * 分析ip地址是否属于国外
     */

}