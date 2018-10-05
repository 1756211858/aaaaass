package com.night.xvideos.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.StaggeredGridLayoutManager
import com.night.xvideos.R
import com.night.xvideos.ToastShortShow
import com.night.xvideos.adapter.ChannelAdapter
import com.night.xvideos.bean.ChannelBean
import kotlinx.android.synthetic.main.activity_channel.*

/**
 * 显示4个按钮的功能页
 */
class KadoYado : AppCompatActivity() {
    private var channelList: MutableList<ChannelBean>? = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel)
        swipe_target.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        initList()
    }

    private fun initList() {
        channel_toolBar.setNavigationOnClickListener {
            finish()
        }
        channelList?.add(ChannelBean(R.mipmap.video, "热门视频"))
        channelList?.add(ChannelBean(R.mipmap.hot, "热门排行"))
        channelList?.add(ChannelBean(R.mipmap.setting, "软件设置"))
        channelList?.add(ChannelBean(R.mipmap.mine, "我的详情"))
        val adapter = ChannelAdapter(context = this, list = this.channelList!!)
        adapter.setOnItemClickListener { _, position ->
            when (position) {
                0 -> startActivity(intent.setClass(this, HotVideo::class.java))
                1 -> ToastShortShow(this, "暂不支持$position")
                2 -> ToastShortShow(this, "暂不支持$position")
                3 -> ToastShortShow(this, "暂不支持$position")

            }
        }
        swipe_target.adapter = adapter
    }

}


