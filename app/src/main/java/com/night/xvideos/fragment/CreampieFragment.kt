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
import com.night.xvideos.adapter.CreampieAdapter
import com.night.xvideos.bean.Creampie
import com.night.xvideos.bean.ErrorVideo
import com.night.xvideos.isNetWorkAvailable
import com.night.xvideos.showErrorVieoDialog
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView
import kotlinx.android.synthetic.main.activity_toprankings.*
import kotlinx.android.synthetic.main.fragment_creampie.*

/**
 * 中出频道
 */
class CreampieFragment : BaseFragment() {
    private var mCreampieAdapter: CreampieAdapter? = null
    private var mCreampieList: MutableList<Creampie>? = null
    private val mBmobQuery: BmobQuery<Creampie>? = BmobQuery()
    private var position: Int = 10
    private val intent = Intent()
    private var currentDataSize: Int = 0
    private lateinit var objectAnimator: ObjectAnimator
    private var errorVideo: ErrorVideo = ErrorVideo()

    @SuppressLint("InflateParams")
    override fun initView(): Int {
        return R.layout.fragment_creampie
    }

    override fun initData() {
        initRecyclerView()
        //开始动画
        startAnimation()
        mBmobQuery?.order("-createdAt")
        mBmobQuery?.setLimit(100)
        mBmobQuery?.findObjects(object : FindListener<Creampie>() {
            override fun done(p0: MutableList<Creampie>?, p1: BmobException?) {
                mCreampieList = p0
                currentDataSize += p0!!.size
                setVideoList()
            }
        })

        //监听上滑刷新
        creamPieRecyclerView.setOnPullLoadMoreListener(object : PullLoadMoreRecyclerView.PullLoadMoreListener {
            override fun onRefresh() {

            }

            override fun onLoadMore() {

                if (mcontext?.let { isNetWorkAvailable(mContext = it) }!!) {
                    mBmobQuery?.order("-createdAt")
                    mBmobQuery?.setLimit(100)
                    mBmobQuery?.setSkip(position)
                    position += 100
                    //开始加载动画
                    startAnimation()
                    mBmobQuery?.findObjects(object : FindListener<Creampie>() {
                        override fun done(p0: MutableList<Creampie>?, p1: BmobException?) {
                            mCreampieList = p0
                            currentDataSize += p0?.size!!
                            setVideoList()
                            creamPieRecyclerView.setPullLoadMoreCompleted()
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
        if (mCreampieList?.size!! >= 1) {
            //设置Adapter中的数据和点击事件
            creamPieLodingImageView.visibility = View.GONE
            //假如adapter中没有数据，那就代表第一次加载数据。
            if (mCreampieAdapter==null) {
                mCreampieAdapter = CreampieAdapter(this.mcontext!!, mCreampieList!!)
                creamPieRecyclerView.setAdapter(mCreampieAdapter)
            } else {
                mCreampieAdapter?.addFooter(currentDataSize - mCreampieList!!.size, mCreampieList!!)
            }
            mCreampieAdapter?.setOnItemClickListener( { _, position ->

                mCreampieAdapter!!.dataList[position].let {
                    val bundle = Bundle()
                    bundle.putString("VIDEOTITLE", it.title)
                    bundle.putString("VIDEOIMGURL", it.imgUrl)
                    bundle.putString("VIDEOURL", it.videoUrl)
                    intent.putExtras(bundle)
                }
                startActivity(intent.setClass(context, VideoPlay::class.java))
            },{_,position->
                showErrorVieoDialog(this.mcontext!!, errorVideo,
                        mCreampieAdapter!!.dataList[position].title!!,
                        mCreampieAdapter!!.dataList[position].videoUrl!!,
                        "CreampieFragment")

            })
        } else {
            creamPieLodingImageView.setImageResource(R.drawable.loding_error)
            Toast.makeText(mcontext, "点击图标重新加载", Toast.LENGTH_SHORT).show()
            creamPieLodingImageView.setOnClickListener {
                initData()
            }
        }
    }

    @SuppressLint("WrongConstant")
    private fun startAnimation() {
        creamPieLodingImageView.setImageResource(R.drawable.loding)
        creamPieLodingImageView.visibility = View.VISIBLE
        objectAnimator= ObjectAnimator.ofFloat(creamPieLodingImageView, "rotation", 0f, 360f)
        objectAnimator.duration = 1000
        objectAnimator.repeatMode = ValueAnimator.INFINITE
        objectAnimator.repeatCount = 8
        objectAnimator.interpolator = AccelerateInterpolator()
        objectAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                if (mCreampieAdapter?.dataList?.size == 0) {
                    objectAnimator.cancel()
                    creamPieLodingImageView.setImageResource(R.drawable.loding_error)
                }
                super.onAnimationEnd(animation)
            }
        })
        objectAnimator.start()
    }

    //初始化RecyclerView
    private fun initRecyclerView() {
        creamPieRecyclerView.pullRefreshEnable = false
        creamPieRecyclerView.setLinearLayout()
        creamPieRecyclerView.setRefreshing(false)
        creamPieRecyclerView.setFooterViewBackgroundColor(R.color.menu_transparent)
        creamPieRecyclerView.setFooterViewTextColor(R.color.menu_transparent)
    }
}