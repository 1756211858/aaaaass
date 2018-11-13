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
import com.night.xvideos.isNetWorkAvailable
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView
import kotlinx.android.synthetic.main.activity_toprankings.*
import kotlinx.android.synthetic.main.fragment_creampie.*

/**
 * 中出频道
 */
class CreampieFragment : BaseFragment() {
    private var mCreapieAdapter: CreampieAdapter? = null
    private var mCreampieList: MutableList<Creampie>? = null
    private val mBmobQuery: BmobQuery<Creampie>? = BmobQuery<Creampie>()
    private var position: Int = 10
    private val intent = Intent()
    private var currentDataSize: Int = 0

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: CreampieFragment? = null
            get() {
                if (field == null) {
                    field = CreampieFragment()
                }
                return field
            }

        fun get(): CreampieFragment {
            return instance!!
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    @SuppressLint("InflateParams")
    override fun initView(): Int {
        return R.layout.fragment_creampie
    }

    override fun initData() {
        initRecyclerView()
        //开始动画
        startAnimation()
        mBmobQuery?.order("-createdAt")
        mBmobQuery?.setLimit(10)
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
                    mBmobQuery?.setLimit(10)
                    mBmobQuery?.setSkip(position)
                    position += 10
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
            if (mCreapieAdapter==null) {
                mCreapieAdapter = CreampieAdapter(this.mcontext!!, mCreampieList!!)
                creamPieRecyclerView.setAdapter(mCreapieAdapter)
            } else {
                mCreapieAdapter?.addFooter(currentDataSize - mCreampieList!!.size, mCreampieList!!)
            }
            mCreapieAdapter?.setOnItemClickListener { _, position ->

                mCreapieAdapter!!.dataList[position].let {
                    val bundle = Bundle()
                    bundle.putString("VIDEOTITLE", it.title)
                    bundle.putString("VIDEOIMGURL", it.imgUrl)
                    bundle.putString("VIDEOURL", it.videoUrl)
                    intent.putExtras(bundle)
                }
                startActivity(intent.setClass(context, VideoPlay::class.java))
            }
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
        val objectAnimator: ObjectAnimator = ObjectAnimator.ofFloat(creamPieLodingImageView, "rotation", 0f, 360f)
        objectAnimator.duration = 1000
        objectAnimator.repeatMode = ValueAnimator.INFINITE
        objectAnimator.repeatCount = 8
        objectAnimator.interpolator = AccelerateInterpolator()
        objectAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                if (mCreapieAdapter?.dataList?.size == 0) {
                    creamPieLodingImageView.setImageResource(R.drawable.loding_error)
                }
                //todo 点击重新加载
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