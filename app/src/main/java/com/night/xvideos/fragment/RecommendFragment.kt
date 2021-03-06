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
import com.night.xvideos.adapter.RecommendAdapter
import com.night.xvideos.bean.ErrorVideo
import com.night.xvideos.bean.Recommend
import com.night.xvideos.isNetWorkAvailable
import com.night.xvideos.showErrorVieoDialog
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView
import kotlinx.android.synthetic.main.activity_toprankings.*
import kotlinx.android.synthetic.main.fragment_recommend.*
import kotlinx.android.synthetic.main.fragment_speakchinese.*

/**
 * 黑人频道
 */
class RecommendFragment : BaseFragment() {
    private var mRecommendAdapter: RecommendAdapter? = null
    private var mRecommendList: MutableList<Recommend>? = null
    private val mBmobQuery: BmobQuery<Recommend>? = BmobQuery()
    private var position: Int = 10
    private val intent = Intent()
    private var currentDataSize: Int = 0
    private lateinit var objectAnimator: ObjectAnimator
    private var errorVideo: ErrorVideo = ErrorVideo()

    @SuppressLint("InflateParams")
    override fun initView(): Int {
        return R.layout.fragment_recommend
    }

    override fun initData() {
        initRecyclerView()
        //开始动画
        startAnimation()
        mBmobQuery?.order("-createdAt")
        mBmobQuery?.setLimit(100)
        mBmobQuery?.findObjects(object : FindListener<Recommend>() {
            override fun done(p0: MutableList<Recommend>?, p1: BmobException?) {
                mRecommendList = p0
                currentDataSize += p0!!.size
                setVideoList()
            }
        })

        //监听上滑刷新
        recommendRecyclerView.setOnPullLoadMoreListener(object :
                PullLoadMoreRecyclerView.PullLoadMoreListener {
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
                    mBmobQuery?.findObjects(object : FindListener<Recommend>() {
                        override fun done(p0: MutableList<Recommend>?, p1: BmobException?) {
                            mRecommendList = p0
                            currentDataSize += p0?.size!!
                            setVideoList()
                            recommendRecyclerView.setPullLoadMoreCompleted()
                        }
                    })
                }else {
                    recommendLodingImageView.visibility=View.GONE
                    objectAnimator.cancel()
                    Toast.makeText(mcontext, "没有更多啦", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    /**
     * 给RecyclerView填充数据和点击事件
     */
    private fun setVideoList() {
        if (mRecommendList?.size!! >= 1) {
            //设置Adapter中的数据和点击事件
            recommendLodingImageView.visibility = View.GONE
            //假如adapter中没有数据，那就代表第一次加载数据。
            if (mRecommendAdapter == null) {
                mRecommendAdapter = RecommendAdapter(this.mcontext!!, mRecommendList!!)
                recommendRecyclerView.setAdapter(mRecommendAdapter)
            } else {
                mRecommendAdapter?.addFooter(currentDataSize - mRecommendList!!.size,
                        mRecommendList!!)
            }
            mRecommendAdapter?.setOnItemClickListener({ _, position ->
                mRecommendAdapter!!.dataList[position].let {
                    val bundle = Bundle()
                    bundle.putString("VIDEOTITLE", it.title)
                    bundle.putString("VIDEOIMGURL", it.imgUrl)
                    bundle.putString("VIDEOURL", it.videoUrl)
                    intent.putExtras(bundle)
                }
                startActivity(intent.setClass(context, VideoPlay::class.java))
            }, { _, position ->
                showErrorVieoDialog(this.mcontext!!, errorVideo,
                        mRecommendAdapter!!.dataList[position].title!!,
                        mRecommendAdapter!!.dataList[position].videoUrl!!,
                        "RecommendFragment")
            })
        } else {
            recommendLodingImageView.visibility=View.GONE
            objectAnimator.cancel()
            Toast.makeText(mcontext, "没有更多啦", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("WrongConstant")
    private fun startAnimation() {
        recommendLodingImageView.setImageResource(R.drawable.loding)
        recommendLodingImageView.visibility = View.VISIBLE
        objectAnimator = ObjectAnimator.ofFloat(recommendLodingImageView,
                "rotation", 0f, 360f)
        objectAnimator.duration = 1000
        objectAnimator.repeatMode = ValueAnimator.INFINITE
        objectAnimator.repeatCount = 8
        objectAnimator.interpolator = AccelerateInterpolator()
        objectAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                if (mRecommendAdapter?.dataList?.size == 0) {
                    recommendLodingImageView.setImageResource(R.drawable.loding_error)
                    objectAnimator.cancel()
                }
                super.onAnimationEnd(animation)
            }
        })
        objectAnimator.start()
    }

    //初始化RecyclerView
    private fun initRecyclerView() {
        //缓存数量
        recommendRecyclerView.pullRefreshEnable = false
        recommendRecyclerView.setLinearLayout()
        recommendRecyclerView.setRefreshing(false)
        recommendRecyclerView.setFooterViewBackgroundColor(R.color.menu_transparent)
        recommendRecyclerView.setFooterViewTextColor(R.color.menu_transparent)

    }
}