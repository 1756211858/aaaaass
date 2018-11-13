package com.night.xvideos.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.LinearLayout
import android.widget.Toast
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.night.xvideos.R
import com.night.xvideos.activity.VideoPlay
import com.night.xvideos.adapter.SpeakChineseAdapter
import com.night.xvideos.bean.SpeakChinese
import com.night.xvideos.isNetWorkAvailable
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView
import kotlinx.android.synthetic.main.activity_toprankings.*
import kotlinx.android.synthetic.main.fragment_speakchinese.*

/**
 * 说中文的色情
 */
class SpeakChineseFragment : BaseFragment() {
    private var mSpeakChineseAdapter: SpeakChineseAdapter? = null
    private var mChineseList: MutableList<SpeakChinese>? = null
    private val mBmobQuery: BmobQuery<SpeakChinese>? = BmobQuery<SpeakChinese>()
    private var position: Int = 10
    private val intent = Intent()
    private var currentDataSize: Int = 0

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    @SuppressLint("InflateParams")
    override fun initView(): Int {
        return R.layout.fragment_speakchinese
    }


    override fun initData() {
        initRecyclerView()
        //开始动画
        startAnimation()
        mBmobQuery?.order("-createdAt")
        mBmobQuery?.setLimit(10)
        mBmobQuery?.findObjects(object : FindListener<SpeakChinese>() {
            override fun done(p0: MutableList<SpeakChinese>?, p1: BmobException?) {
                mChineseList = p0
                currentDataSize += p0!!.size
                setVideoList()
            }
        })

        //监听上滑刷新
        speakChineseRecyclerView.setOnPullLoadMoreListener(object : PullLoadMoreRecyclerView.PullLoadMoreListener {
            override fun onRefresh() {

            }

            override fun onLoadMore() {
                if (mcontext?.let { isNetWorkAvailable(mContext = it) }!!) {
                    mBmobQuery?.order("-createdAt")
                    mBmobQuery?.setLimit(10)
                    mBmobQuery?.setSkip(position)
                    position += 10
                    //开始加载动画
                    startAnimation()
                    mBmobQuery?.findObjects(object : FindListener<SpeakChinese>() {
                        override fun done(p0: MutableList<SpeakChinese>?, p1: BmobException?) {
                            mChineseList = p0
                            currentDataSize += p0?.size!!
                            setVideoList()
                            speakChineseRecyclerView.setPullLoadMoreCompleted()
                        }
                    })
                } else {
                    topRankingsRecyclerView.setPullLoadMoreCompleted()
                    Toast.makeText(mcontext, "网络连接失败", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    /**
     * 给RecyclerView填充数据和点击事件
     */
    private fun setVideoList() {
        if (mChineseList?.size!! >= 1) {
            //设置Adapter中的数据和点击事件
            speakChineseLodingImageView.visibility = View.GONE
            //假如adapter中没有数据，那就代表第一次加载数据。
            if (mSpeakChineseAdapter==null) {
                mSpeakChineseAdapter = SpeakChineseAdapter(this.mcontext!!, mChineseList!!)
                speakChineseRecyclerView.setAdapter(mSpeakChineseAdapter)
            } else {
                mSpeakChineseAdapter?.addFooter(currentDataSize - mChineseList!!.size, mChineseList!!)
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
            speakChineseLodingImageView.setImageResource(R.drawable.loding_error)
            Toast.makeText(mcontext, "点击图标重新加载", Toast.LENGTH_SHORT).show()
            speakChineseLodingImageView.setOnClickListener {
                initData()
            }
        }
    }

    @SuppressLint("WrongConstant")
    private fun startAnimation() {
        speakChineseLodingImageView.setImageResource(R.drawable.loding)
        speakChineseLodingImageView.visibility = View.VISIBLE
        val objectAnimator: ObjectAnimator = ObjectAnimator.ofFloat(speakChineseLodingImageView, "rotation", 0f, 360f)
        objectAnimator.duration = 1000
        objectAnimator.repeatMode = ValueAnimator.INFINITE
        objectAnimator.repeatCount = 8
        objectAnimator.interpolator = AccelerateInterpolator()
        objectAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                if (mSpeakChineseAdapter?.dataList?.size == 0) {
                    speakChineseLodingImageView.setImageResource(R.drawable.loding_error)
                }
                //todo 点击重新加载
                super.onAnimationEnd(animation)
            }
        })
        objectAnimator.start()
    }

    //初始化RecyclerView
    private fun initRecyclerView() {
        speakChineseRecyclerView.pullRefreshEnable = false
        speakChineseRecyclerView.setLinearLayout()
        speakChineseRecyclerView.setRefreshing(false)
        speakChineseRecyclerView.setFooterViewBackgroundColor(R.color.menu_transparent)
        speakChineseRecyclerView.setFooterViewTextColor(R.color.menu_transparent)
    }
}
