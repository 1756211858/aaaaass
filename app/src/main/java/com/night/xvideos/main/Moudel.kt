package com.night.xvideos.main

import java.io.IOException

class Moudel {
    private var callBack : ((Boolean ) -> Unit)? = null
    private fun getNetWorkState() {
        try{
            val ip="www.baidu.com"
            var p:Process=Runtime.getRuntime().exec("ping -c 3 -w 100$ip")

        }catch (e:IOException){

        }
    }
}