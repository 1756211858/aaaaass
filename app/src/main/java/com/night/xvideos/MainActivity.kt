package com.night.xvideos

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import cn.bmob.v3.Bmob
import com.night.xvideos.activity.KadoYado

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Bmob.initialize(this, "15772a852037706c6c1a4c404048f3c5")
        //广告页显示2秒后跳转到功能页
        val handler = Handler()
        handler.postDelayed({
            val intent = Intent(applicationContext, KadoYado::class.java)
            startActivity(intent)
            finish()
        }, 1300)
    }
}
