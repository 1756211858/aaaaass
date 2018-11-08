package com.night.xvideos.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.support.v4.content.FileProvider
import android.util.Log
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import co.metalab.asyncawait.async
import com.afollestad.materialdialogs.MaterialDialog
import com.night.xvideos.*
import com.night.xvideos.adapter.ChannelAdapter
import com.night.xvideos.bean.ChannelBean
import com.night.xvideos.main.Contract
import com.night.xvideos.main.Presenter
import com.night.xvideos.update.update
import kotlinx.android.synthetic.main.activity_channel.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.text.NumberFormat


/**
 * 显示4个按钮的功能页
 */
class KadoYado : BaseActivity(), Contract.KadoYado {
    private var channelList: MutableList<ChannelBean>? = mutableListOf()
    private lateinit var apkUrl: String
    private var code: Int = 0
    private lateinit var text: String
    private lateinit var mSavePath: File
    private var mPresenter:Presenter?=null
    override fun showNetWorkError() {
        MaterialDialog.Builder(this).title("无法连接Google，导致无法播放国外视频")
                .content("首先找到本软件所提供的VPN地址，然后注册，测试可以访问Google后，就可以使用啦。")
                .show()
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_channel
    }

    override fun initData() {
        queryData()
        mPresenter= Presenter(this,null)
    }

    override fun initContentView() {
        swipe_target.setStaggeredGridLayout(2)
        channelList?.add(ChannelBean(R.mipmap.video, "热门视频"))
        channelList?.add(ChannelBean(R.mipmap.hot, "热门排行"))
        channelList?.add(ChannelBean(R.mipmap.description, "软件说明"))
        channelList?.add(ChannelBean(R.mipmap.mine, "我的详情"))
        val adapter = ChannelAdapter(context = this, list = this.channelList!!)
        adapter.setOnItemClickListener { _, position ->
            when (position) {
                0 ->if(isNetWorkAvailable(mContext = applicationContext)){
                    startActivity(intent.setClass(this, HotVideo::class.java))
                } else{
                    LongShow(applicationContext,"请连接网络并打开VPN")
                }
                1 -> ShortShow(this, "暂不支持$position")
                2 -> startActivity(intent.setClass(this, Description::class.java))
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
        MaterialDialog.Builder(this)
                .title("2.0版本").content(text)
                .positiveText("立即更新").positiveColor(resources.getColor(R.color.menu_item_blue_dark))
                .onPositive { _, _ ->
                    //下载apk文件
                    downloadAPK()
                    mIsCanDownLoad = true
                }
                .negativeText("暂不更新").negativeColor(resources.getColor(R.color.black))
                .cancelable(false)
                .show()
    }

    private var mIsCanDownLoad: Boolean = true
    /**
     * 下载apk文件
     */
    @SuppressLint("ResourceAsColor")
    private fun downloadAPK() {
        val progressBar = MaterialDialog.Builder(this)
                .title("正在下载文件....")
                .titleColor(Color.BLACK)
                .progress(false, 100, true)
                .progressNumberFormat("%1d/%2d")
                .progressPercentFormat(NumberFormat.getPercentInstance())
                .contentColor(resources.getColor(R.color.black))
                .cancelable(false)
                .positiveText("取消").positiveColor(R.color.black)
                .onPositive { _, _ -> mIsCanDownLoad = false }
                .build()
        progressBar.show()
        async {
            //todo 下载apk
            try {
                val mPath = "/data/data/com.night.xvideos/files"
                //val mPath="/mnt/sdcard/Download/"
                mSavePath = File(mPath)
                if (!mSavePath.exists()) {
                    mSavePath.mkdir()
                }
                await {
                    val con = URL(apkUrl).openConnection()
                    con.connect()
                    val inputStream = con.getInputStream()
                    val length = con.contentLength
                    val apkFile = File(mSavePath, getVerName(mContext = applicationContext))
                    val fileOutputStream = FileOutputStream(apkFile)
                    var count = 0
                    val buffer = ByteArray(1024)
                    while (mIsCanDownLoad) {
                        val numread = inputStream.read(buffer)
                        count += numread
                        progressBar.setProgress((count.toFloat() / length * 100).toInt())
                        if (numread < 0) {
                            progressBar.dismiss()
                            installApk()
                            break
                        }
                        fileOutputStream.write(buffer, 0, numread)
                    }
                    fileOutputStream.close()
                    inputStream.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 安装apk
     */
    private fun installApk() {
        val apkFile = File(mSavePath, getVerName(applicationContext))
        if (!apkFile.exists()) {
            Log.e("mlog", "没有找到apk")
            return
        }
        val command = arrayOf("chmod", "777", apkFile.path)
        val builder = ProcessBuilder(*command)
        try {
            builder.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            val uri = FileProvider.getUriForFile(this,
                    "com.night.xvideos.update.provider", apkFile)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile),
                    "application/vnd.android.package-archive")
        }
        startActivity(intent)
    }
}


