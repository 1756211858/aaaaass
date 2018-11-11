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
import com.night.xvideos.adapter.BlackManAdapter
import com.night.xvideos.bean.BlackMan
import com.night.xvideos.isNetWorkAvailable
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView
import kotlinx.android.synthetic.main.activity_toprankings.*
import kotlinx.android.synthetic.main.fragment_blacked.*

/**
 * 黑人频道
 */
class BlackedFragment : BaseFragment() {
    private var mBlackManAdapter: BlackManAdapter? = null
    private var mBlackManList: MutableList<BlackMan>? = null
    private val mBmobQuery: BmobQuery<BlackMan>? = BmobQuery<BlackMan>()
    private var position: Int = 10
    private val intent = Intent()
    private var currentDataSize: Int = 0
    private var flag = false

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: BlackedFragment? = null
            get() {
                if (field == null) {
                    field = BlackedFragment()
                }
                return field
            }

        fun get(): BlackedFragment {
            return instance!!
        }
    }

    @SuppressLint("InflateParams")
    override fun initView(): Int {
        return R.layout.fragment_blacked
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun initData() {
        initRecyclerView()
        //开始动画
        startAnimation()
        mBmobQuery?.order("-createdAt")
        mBmobQuery?.setLimit(10)
        mBmobQuery?.findObjects(object : FindListener<BlackMan>() {
            override fun done(p0: MutableList<BlackMan>?, p1: BmobException?) {
                mBlackManList = p0
                currentDataSize += p0!!.size
                setVideoList()
            }
        })

        //监听上滑刷新
        blackManRecyclerView.setOnPullLoadMoreListener(object : PullLoadMoreRecyclerView.PullLoadMoreListener {
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
                mBmobQuery?.findObjects(object : FindListener<BlackMan>() {
                    override fun done(p0: MutableList<BlackMan>?, p1: BmobException?) {
                        mBlackManList = p0
                        flag = true
                        currentDataSize += p0?.size!!
                        setVideoList()
                        blackManRecyclerView.setPullLoadMoreCompleted()
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
        if (mBlackManList?.size!! >= 1) {
            //设置Adapter中的数据和点击事件
            blackManLoading.visibility = View.GONE
            //假如adapter中没有数据，那就代表第一次加载数据。
            if (!flag) {
                mBlackManAdapter = BlackManAdapter(this.mcontext!!, mBlackManList!!)
                blackManRecyclerView.setAdapter(mBlackManAdapter)
            } else {
                mBlackManAdapter?.addFooter(currentDataSize - mBlackManList!!.size, mBlackManList!!)
            }
            mBlackManAdapter?.setOnItemClickListener { _, position ->

                mBlackManAdapter!!.dataList[position].let {
                    val bundle = Bundle()
                    bundle.putString("VIDEOTITLE", it.title)
                    bundle.putString("VIDEOIMGURL", it.imgUrl)
                    bundle.putString("VIDEOURL", it.videoUrl)
                    intent.putExtras(bundle)
                }
                startActivity(intent.setClass(context, VideoPlay::class.java))
            }
        } else {
            blackManLoading.setImageResource(R.drawable.loding_error)
            Toast.makeText(mcontext, "点击图标重新加载", Toast.LENGTH_SHORT).show()
            blackManLoading.setOnClickListener {
                initData()
            }
        }
    }
    @SuppressLint("WrongConstant")
    private fun startAnimation() {
        blackManLoading.setImageResource(R.drawable.loding)
        blackManLoading.visibility = View.VISIBLE
        val objectAnimator: ObjectAnimator = ObjectAnimator.ofFloat(blackManLoading, "rotation", 0f, 360f)
        objectAnimator.duration = 1000
        objectAnimator.repeatMode = ValueAnimator.INFINITE
        objectAnimator.repeatCount = 8
        objectAnimator.interpolator = AccelerateInterpolator()
        objectAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                if (mBlackManAdapter?.dataList?.size == 0) {
                    blackManLoading.setImageResource(R.drawable.loding_error)
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
        blackManRecyclerView.pullRefreshEnable=false
        blackManRecyclerView.setLinearLayout()
        blackManRecyclerView.setRefreshing(false)
        blackManRecyclerView.setFooterViewBackgroundColor(R.color.menu_transparent)
        blackManRecyclerView.setFooterViewTextColor(R.color.menu_transparent)
        blackManRecyclerView.setFooterViewText(" ")
    }
}