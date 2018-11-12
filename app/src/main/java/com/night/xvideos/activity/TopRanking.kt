package com.night.xvideos.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.AdapterView
import android.widget.Toast
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import co.metalab.asyncawait.async
import com.night.xvideos.R
import com.night.xvideos.adapter.TopRankingsAdapter
import com.night.xvideos.bean.TopRankings
import com.night.xvideos.isNetWorkAvailable
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView
import kotlinx.android.synthetic.main.activity_toprankings.*

class TopRanking : BaseActivity() {
    override fun setLayoutId(): Int {
        return R.layout.activity_toprankings
    }

    private var mTopRankingsAdapter: TopRankingsAdapter? = null
    private var mTopRankingLists: MutableList<TopRankings>? = null
    private val mBmobQuery: BmobQuery<TopRankings>? = BmobQuery<TopRankings>()
    private var position: Int = 10
    private var currentDataSize: Int = 0
    private var flag = false

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun initContentView() {
        initRecyclerView()
        //开始动画
        startAnimation()
        mBmobQuery?.order("-createdAt")
        mBmobQuery?.setLimit(10)
        mBmobQuery?.findObjects(object : FindListener<TopRankings>() {
            override fun done(p0: MutableList<TopRankings>?, p1: BmobException?) {
                mTopRankingLists = p0
                currentDataSize += p0!!.size
                setVideoList()
            }


        })

        //监听上滑刷新
        topRankingsRecyclerView.setOnPullLoadMoreListener(object : PullLoadMoreRecyclerView.PullLoadMoreListener {
            override fun onRefresh() {

            }

            override fun onLoadMore() {
                //检测网络是否可用
                if (isNetWorkAvailable(applicationContext)) {
                    mBmobQuery?.order("-createdAt")
                    mBmobQuery?.setLimit(10)
                    mBmobQuery?.setSkip(position)
                    position += 10
                    //开始加载动画
                    startAnimation()
                    mBmobQuery?.findObjects(object : FindListener<TopRankings>() {
                        override fun done(p0: MutableList<TopRankings>?, p1: BmobException?) {
                            mTopRankingLists = p0
                            flag = true
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
        //缓存数量
        topRankingsRecyclerView.pullRefreshEnable = false
        topRankingsRecyclerView.setLinearLayout()
        topRankingsRecyclerView.setRefreshing(false)
        topRankingsRecyclerView.setFooterViewBackgroundColor(R.color.menu_transparent)
        topRankingsRecyclerView.setFooterViewTextColor(R.color.menu_transparent)
        topRankingsRecyclerView.setFooterViewText(" ")
    }

    @SuppressLint("WrongConstant")
    private fun startAnimation() {
        topRankingsLodingImageView.setImageResource(R.drawable.loding)
        topRankingsLodingImageView.visibility = View.VISIBLE
        val objectAnimator: ObjectAnimator = ObjectAnimator
                .ofFloat(topRankingsLodingImageView, "rotation", 0f, 360f)
        objectAnimator.duration = 1000
        objectAnimator.repeatMode = ValueAnimator.INFINITE
        objectAnimator.repeatCount = 8
        objectAnimator.interpolator = AccelerateInterpolator()
        objectAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                if (mTopRankingsAdapter?.dataList?.size == 0) {
                    topRankingsLodingImageView.setImageResource(R.drawable.loding_error)
                }
                //todo 点击重新加载
                super.onAnimationEnd(animation)
            }
        })
        objectAnimator.start()
    }

    /**
     * 给RecyclerView填充数据和点击事件
     */
    private fun setVideoList() {
        if (mTopRankingLists?.size!! >= 1) {
            //设置Adapter中的数据和点击事件
            topRankingsLodingImageView.visibility = View.GONE
            //假如adapter中没有数据，那就代表第一次加载数据。
            if (!flag) {
                mTopRankingsAdapter = TopRankingsAdapter(applicationContext, mTopRankingLists!!)
                topRankingsRecyclerView.setAdapter(mTopRankingsAdapter)
            } else {
                mTopRankingsAdapter?.addFooter(currentDataSize - mTopRankingLists!!.size, mTopRankingLists!!)
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
            }, listener2 = { _: View, i: Int ->
                Toast.makeText(applicationContext, "长按上传错误视频ID", Toast.LENGTH_SHORT).show()
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