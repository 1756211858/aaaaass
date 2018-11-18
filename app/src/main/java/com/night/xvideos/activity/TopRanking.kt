package com.night.xvideos.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener

import com.night.xvideos.R
import com.night.xvideos.adapter.TopRankingsAdapter
import com.night.xvideos.bean.ErrorVideo
import com.night.xvideos.bean.TopRankings
import com.night.xvideos.isNetWorkAvailable
import com.night.xvideos.showErrorVieoDialog
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView
import kotlinx.android.synthetic.main.activity_toprankings.*

class TopRanking : BaseActivity() {
    private var mTopRankingsAdapter: TopRankingsAdapter? = null
    private var mTopRankingLists: MutableList<TopRankings>? = null
    private val mBmobQuery: BmobQuery<TopRankings>? = BmobQuery()
    private var position: Int = 10
    private var currentDataSize: Int = 0
    private lateinit var objectAnimator: ObjectAnimator
    private var errorVideo: ErrorVideo = ErrorVideo()

    override fun setLayoutId(): Int {
        return R.layout.activity_toprankings
    }

    override fun initContentView() {
        initRecyclerView()
        //开始动画
        startAnimation()
        mBmobQuery?.order("-createdAt")
        mBmobQuery?.setLimit(100)
        mBmobQuery?.findObjects(object : FindListener<TopRankings>() {
            override fun done(p0: MutableList<TopRankings>?, p1: BmobException?) {
                mTopRankingLists = p0
                currentDataSize += p0!!.size
                setVideoList()
            }
        })

        //监听上滑刷新
        topRankingsRecyclerView.setOnPullLoadMoreListener(object :
                PullLoadMoreRecyclerView.PullLoadMoreListener {
            override fun onRefresh() {

            }

            override fun onLoadMore() {
                //检测网络是否可用
                if (isNetWorkAvailable(applicationContext)) {
                    mBmobQuery?.order("-createdAt")
                    mBmobQuery?.setLimit(100)
                    mBmobQuery?.setSkip(position)
                    position += 100
                    //开始加载动画
                    startAnimation()
                    mBmobQuery?.findObjects(object : FindListener<TopRankings>() {
                        override fun done(p0: MutableList<TopRankings>?, p1: BmobException?) {
                            mTopRankingLists = p0
                            currentDataSize += p0?.size!!
                            setVideoList()
                            topRankingsRecyclerView.setPullLoadMoreCompleted()
                        }
                    })
                } else {
                    topRankingsRecyclerView.setPullLoadMoreCompleted()
                    Toast.makeText(applicationContext, "网络连接失败", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    //初始化RecyclerView
    private fun initRecyclerView() {
        topRankingsRecyclerView.pullRefreshEnable = false
        topRankingsRecyclerView.setLinearLayout()
        topRankingsRecyclerView.setRefreshing(false)
        topRankingsRecyclerView.setFooterViewBackgroundColor(R.color.menu_transparent)
        topRankingsRecyclerView.setFooterViewTextColor(R.color.menu_transparent)
    }

    @SuppressLint("WrongConstant")
    private fun startAnimation() {
        topRankingsLodingImageView.setImageResource(R.drawable.loding)
        topRankingsLodingImageView.visibility = View.VISIBLE
        objectAnimator = ObjectAnimator.ofFloat(topRankingsLodingImageView,
                "rotation", 0f, 360f)
        objectAnimator.duration = 1000
        objectAnimator.repeatMode = ValueAnimator.INFINITE
        objectAnimator.repeatCount = 8
        objectAnimator.interpolator = AccelerateInterpolator()
        objectAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                if (mTopRankingsAdapter?.dataList?.size == 0) {
                    objectAnimator.cancel()
                    topRankingsLodingImageView.setImageResource(R.drawable.loding_error)
                }
                super.onAnimationEnd(animation)
            }
        })
        objectAnimator.start()
    }

    private fun setVideoList() {
        if (mTopRankingLists?.size!! >= 1) {
            topRankingsLodingImageView.visibility = View.GONE
            if (mTopRankingsAdapter == null) {
                mTopRankingsAdapter = TopRankingsAdapter(applicationContext, mTopRankingLists!!)
                topRankingsRecyclerView.setAdapter(mTopRankingsAdapter)
            } else {
                mTopRankingsAdapter?.addFooter(currentDataSize - mTopRankingLists!!.size,
                        mTopRankingLists!!)
            }
            mTopRankingsAdapter?.setOnItemListener(listener = { view: View, i: Int ->
                mTopRankingsAdapter!!.dataList[i].let {
                    val bundle = Bundle()
                    bundle.putString("VIDEOTITLE", it.title)
                    bundle.putString("VIDEOIMGURL", it.imgUrl)
                    bundle.putString("VIDEOURL", it.videoUrl)
                    intent.putExtras(bundle)
                }
                startActivity(intent.setClass(applicationContext, VideoPlay::class.java))
            }, listener2 = { _: View, _: Int ->

                showErrorVieoDialog(this,errorVideo,
                        mTopRankingsAdapter!!.dataList[position].title!!,
                        mTopRankingsAdapter!!.dataList[position].videoUrl!!,
                        "SpeakChineseFragment")
            })
        } else {
            topRankingsLodingImageView.setImageResource(R.drawable.loding_error)
            Toast.makeText(applicationContext, "点击图标重新加载", Toast.LENGTH_SHORT).show()
            topRankingsLodingImageView.setOnClickListener {
                initData()
            }
        }
    }
}