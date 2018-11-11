package com.night.xvideos.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.night.xvideos.R
import com.night.xvideos.activity.VideoPlay
import com.night.xvideos.adapter.SpeakChineseAdapter
import com.night.xvideos.bean.speakChinese
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView
import kotlinx.android.synthetic.main.fragment_speakchinese.*

/**
 * 说中文的色情
 */
class SpeakChineseFragment : BaseFragment() {
    private var mSpeakChineseAdapter: SpeakChineseAdapter? = null
    private var chineseList: MutableList<speakChinese>? = null
    private val bmobQuery: BmobQuery<speakChinese>? = BmobQuery<speakChinese>()
    private var position: Int = 10
    private val intent = Intent()
    private var currentDataSize: Int = 0
    private var flag = false

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: SpeakChineseFragment? = null
            get() {
                if (field == null) {
                    field = SpeakChineseFragment()
                }
                return field
            }

        fun get(): SpeakChineseFragment {
            return instance!!
        }
    }

    @SuppressLint("InflateParams")
    override fun initView(): Int {
        return R.layout.fragment_speakchinese
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun initData() {
        initRecyclerView()
        //开始动画
        startAnimation()
        bmobQuery?.order("-createdAt")
        bmobQuery?.setLimit(10)
        bmobQuery?.findObjects(object : FindListener<speakChinese>() {
            override fun done(p0: MutableList<speakChinese>?, p1: BmobException?) {
                chineseList = p0
                currentDataSize += p0!!.size
                setVideoList()
            }
        })

        //监听上滑刷新
        speakChineseRecyclerView.setOnPullLoadMoreListener(object : PullLoadMoreRecyclerView.PullLoadMoreListener {
            override fun onRefresh() {

            }

            override fun onLoadMore() {
                bmobQuery?.order("-createdAt")
                bmobQuery?.setLimit(10)
                bmobQuery?.setSkip(position)
                position += 10
                //开始加载动画
                startAnimation()
                bmobQuery?.findObjects(object : FindListener<speakChinese>() {
                    override fun done(p0: MutableList<speakChinese>?, p1: BmobException?) {
                        chineseList = p0
                        flag = true
                        currentDataSize += p0?.size!!
                        setVideoList()
                        speakChineseRecyclerView.setPullLoadMoreCompleted()
                    }
                })

            }
        })
    }

    /**
     * 给RecyclerView填充数据和点击事件
     */
    private fun setVideoList() {
        if (chineseList?.size!! >= 1) {
            //设置Adapter中的数据和点击事件
            speakChineseLoding.visibility = View.GONE
            //假如adapter中没有数据，那就代表第一次加载数据。
            if (!flag) {
                mSpeakChineseAdapter = SpeakChineseAdapter(this.mcontext!!, chineseList!!)
                speakChineseRecyclerView.setAdapter(mSpeakChineseAdapter)
            } else {
                mSpeakChineseAdapter?.addFooter(currentDataSize - chineseList!!.size, chineseList!!)
            }
            mSpeakChineseAdapter?.setOnItemClickListener { _, position ->

                mSpeakChineseAdapter!!.dataList[position].let {
                    val bundle = Bundle()
                    bundle.putString("VIDEOTITLE", it.title)
                    bundle.putString("VIDEOIMGURL", it.imgUrl)
                    bundle.putString("VIDEOURL", it.videoUrl)
                    intent.putExtras(bundle)
                }
                startActivity(intent.setClass(context, VideoPlay::class.java))
            }
        } else {
            speakChineseLoding.setImageResource(R.drawable.loding_error)
            Toast.makeText(mcontext, "点击图标重新加载", Toast.LENGTH_SHORT).show()
            speakChineseLoding.setOnClickListener {
                initData()
            }
        }
    }

    @SuppressLint("WrongConstant")
    private fun startAnimation() {
        speakChineseLoding.setImageResource(R.drawable.loding)
        speakChineseLoding.visibility = View.VISIBLE
        val objectAnimator: ObjectAnimator = ObjectAnimator.ofFloat(speakChineseLoding, "rotation", 0f, 360f)
        objectAnimator.duration = 1000
        objectAnimator.repeatMode = ValueAnimator.INFINITE
        objectAnimator.repeatCount = 8
        objectAnimator.interpolator = AccelerateInterpolator()
        objectAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                if (mSpeakChineseAdapter?.dataList?.size == 0) {
                    speakChineseLoding.setImageResource(R.drawable.loding_error)
                }
                //todo 点击重新加载
                super.onAnimationEnd(animation)
            }
        })
        objectAnimator.start()
    }

    //初始化RecyclerView
    private fun initRecyclerView() {
        //缓存数量
        speakChineseRecyclerView.setLinearLayout()
        speakChineseRecyclerView.setRefreshing(false)
        speakChineseRecyclerView.setFooterViewBackgroundColor(R.color.menu_transparent)
        speakChineseRecyclerView.setFooterViewTextColor(R.color.menu_transparent)
        speakChineseRecyclerView.setFooterViewText(" ")
    }
}
