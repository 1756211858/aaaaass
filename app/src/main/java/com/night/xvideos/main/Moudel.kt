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
    var config=AppConfig()
    private var callBack : ((Boolean ) -> Unit)? = null
    /**
     * 分析ip地址是否属于国外
     */
    fun analyzeIP(callback:(Boolean)->Unit) {
        try{
            if(mContext!=null){
                val retrofit=Retrofit.Builder().baseUrl(config.baseUrl).build()
                val apiStore=retrofit.create(ipAddressApi::class.java)
                val call = apiStore.getIpAddress(getIPAddress(mContext!!)!!)
                call.enqueue(object :Callback<ApiStore>{
                    override fun onFailure(call: Call<ApiStore>, t: Throwable) {

                    }

                    override fun onResponse(call: Call<ApiStore>, response: Response<ApiStore>) {
                        val list=response.body()

                    }
                })
            }else{
                Log.e("mlog","no mContext！")
            }
        }catch (e:IOException){

        }
    }
}