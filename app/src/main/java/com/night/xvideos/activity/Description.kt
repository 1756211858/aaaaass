package com.night.xvideos.activity

import com.bumptech.glide.Glide
import com.night.xvideos.R
import kotlinx.android.synthetic.main.activity_description.*
import android.content.Intent
import android.net.Uri


class Description : BaseActivity() {

    override fun setLayoutId(): Int {
        return R.layout.activity_description
    }

    override fun initContentView() {
        Glide.with(applicationContext).load(R.drawable.k1).into(imageView1)
        Glide.with(applicationContext).load(R.drawable.k2).into(imageView2)
        Glide.with(applicationContext).load(R.drawable.k3).into(imageView3)
        Glide.with(applicationContext).load(R.drawable.k4).into(imageView4)
        Glide.with(applicationContext).load(R.drawable.k5).into(imageView5)
        Glide.with(applicationContext).load(R.drawable.k6).into(imageView6)

        buyButton.setOnClickListener {
            val uri = Uri.parse("https://client.kkfast.com/aff.php?aff=46")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        downloadButton.setOnClickListener {
            val uri = Uri.parse("https://github.com/SmokeJeason/downloadAPK")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }
}
