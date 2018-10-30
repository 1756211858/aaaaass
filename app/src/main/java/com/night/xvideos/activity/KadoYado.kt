package com.night.xvideos.activity

import android.graphics.Color
import android.util.Log
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import co.metalab.asyncawait.async
import com.afollestad.materialdialogs.MaterialDialog
import com.night.xvideos.R
import com.night.xvideos.ShortShow
import com.night.xvideos.adapter.ChannelAdapter
import com.night.xvideos.bean.ChannelBean
import com.night.xvideos.getVersionCode
import com.night.xvideos.update.update
import kotlinx.android.synthetic.main.activity_channel.*
import java.text.NumberFormat


/**
 * 显示4个按钮的功能页
 */
class KadoYado : BaseActivity() {
    private var channelList: MutableList<ChannelBean>? = mutableListOf()
    private lateinit var apkUrl: String
    private var code: Int = 0
    private lateinit var text: String
    override fun setLayoutId(): Int {
        return R.layout.activity_channel
    }

    override fun initData() {
        queryData()
    }

    override fun initContentView() {
        swipe_target.setStaggeredGridLayout(2)
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
                1 -> ShortShow(this, "暂不支持$position")
                2 -> ShortShow(this, "暂不支持$position")
                3 -> ShortShow(this, "暂不支持$position")

            }
        }
        swipe_target.setAdapter(adapter)
        //禁用刷新
        swipe_target.pullRefreshEnable = false
        swipe_target.pushRefreshEnable = false
    }

    /**
     * 获取apk链接和版本code，以及更新内容
     */
    private fun queryData() {
        val bmobQuery = BmobQuery<update>()
        bmobQuery.findObjects(object : FindListener<update>() {
            override fun done(p0: MutableList<update>?, p1: BmobException?) {
                if (p1 == null) {
                    apkUrl = p0!![0].apk.url
                    code = p0[0].code.toInt()
                    text = p0[0].text
                    check()
                    Log.e("mlog", apkUrl)
                    Log.e("mlog", code.toString())
                    Log.e("mlog", text)
                } else {
                    Log.e("mlog", p1.message)
                }
            }
        })
    }

    /**
     * 判断版本大小
     */
    private fun check() {

        val i = getVersionCode(applicationContext)
        if (code > i) {
            showDialog()
            Log.e("mlog", "需要升级")
        }
    }

    private fun showDialog() {
        val dialog = MaterialDialog.Builder(applicationContext).title("2.0版本")
                .positiveText("立即更新").onPositive { dialog, which ->
                    dialog.cancel()
                    //下载apk文件
                    showProgressBar()

                }
                .negativeText("暂不更新").onNegative { dialog, which ->
                    dialog.cancel()
                }
        dialog.show()
    }

    /**
     * 下载apk文件
     */
    private fun showProgressBar() {
        val progressBar = MaterialDialog.Builder(this)
                .title("正在下载文件....")
                .titleColor(Color.BLACK)
                .progress(false, 100, true)
                .progressNumberFormat("%1d/%2d")
                .progressPercentFormat(NumberFormat.getPercentInstance())
                .contentColor(resources.getColor(R.color.black))
                .cancelable(false)
                .positiveText("取消")
                .build()
        progressBar.show()
        async {
            //todo

        }
    }
}


