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
import com.night.xvideos.activity.VideoActivity
import com.night.xvideos.adapter.VideoAdapter
import com.night.xvideos.bean.speakChinese
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView
import kotlinx.android.synthetic.main.fragment_speakchinese.*

/**
 * 说中文的色情
 */
class SpeakChineseFragment : BaseFragment() {
    private var videoAdapter: VideoAdapter? = null
    private var chineseList: MutableList<speakChinese>? = null
    private val bmobQuery: BmobQuery<speakChinese>? = BmobQuery<speakChinese>()
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
    private var position:Int=10
    private val intent = Intent()
    override fun initData() {
        PMRV.setLinearLayout()
        startAnimation()
        bmobQuery?.order("-createdAt")
        bmobQuery?.setLimit(10)

        bmobQuery?.findObjects(object : FindListener<speakChinese>() {
            override fun done(p0: MutableList<speakChinese>?, p1: BmobException?) {
                chineseList = p0
                if (chineseList?.size!! >= 1) {
                    speakChineseLoding.visibility = View.GONE
                } else if (chineseList?.size!! <= 1) {
                    //todo 对话框提示
                    Toast.makeText(mcontext, "请检查网络", Toast.LENGTH_SHORT).show()
                }
            }
        })


        videoAdapter = context?.let { chineseList?.let { it1 -> VideoAdapter(it, it1) } }
        PMRV.setAdapter(videoAdapter)









       /* PMRV.setOnPullLoadMoreListener(object : PullLoadMoreRecyclerView.PullLoadMoreListener {
                    override fun onRefresh() {

                    }

                    override fun onLoadMore() {
                        bmobQuery?.order("-createdAt")
                        bmobQuery?.setLimit(10)
                        position+=10
                        bmobQuery?.setSkip(position)
                        bmobQuery?.findObjects(object : FindListener<speakChinese>() {
                            override fun done(p0: MutableList<speakChinese>?, p1: BmobException?) {
                                videoAdapter?.list= p0!!
                                if (chineseList?.size!! >= 1) {
                                    speakChineseLoding.visibility = View.GONE
                                } else if (chineseList?.size!! <= 1) {
                                    //todo 对话框提示
                                    Toast.makeText(mcontext, "请检查网络", Toast.LENGTH_SHORT).show()
                                }
                    }
                })
            }
        })*/
        //点击事件

        videoAdapter?.setOnItemClickListener { _, position ->
            chineseList!![position].let {
                val bundle = Bundle()
                bundle.putString("VIDEOTITLE", it.title)
                bundle.putString("VIDEOIMGURL", it.imgUrl)
                bundle.putString("VIDEOURL", it.videoUrl)
                intent.putExtras(bundle)
            }
            startActivity(intent.setClass(context, VideoActivity::class.java))
        }

    }

    @SuppressLint("WrongConstant")
    private fun startAnimation() {
        val objectAnimator: ObjectAnimator = ObjectAnimator.ofFloat(speakChineseLoding, "rotation", 0f, 360f)
        objectAnimator.duration = 900
        objectAnimator.repeatMode = ValueAnimator.INFINITE
        objectAnimator.repeatCount = 5
        objectAnimator.interpolator = AccelerateInterpolator()
        objectAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                speakChineseLoding.visibility = View.GONE
                super.onAnimationEnd(animation)
            }
        })
        objectAnimator.start()
    }

}
