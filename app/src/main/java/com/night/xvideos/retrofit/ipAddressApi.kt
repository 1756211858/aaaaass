package com.night.xvideos.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ipAddressApi {
    @GET("getIpInfo.php?")
    fun getIpAddress(@Query("ip") ip: String): Call<ApiStore>

}