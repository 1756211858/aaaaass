package com.night.xvideos.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.afollestad.materialdialogs.MaterialDialog
import com.night.xvideos.R
import com.night.xvideos.activity.VideoActivity
import com.night.xvideos.adapter.VideoAdapter
import com.night.xvideos.bean.speakChinese
import kotlinx.android.synthetic.main.fragment_speakchinese.*

/**
 * 说中文的色情
 */
class SpeakChineseFragment : BaseFragment() {
    var videoAdapter: VideoAdapter? = null
    var chineseList: MutableList<speakChinese>? = null

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

    @SuppressLint("WrongConstant")
    override fun initData() {

        val objectAnimator: ObjectAnimator = ObjectAnimator.ofFloat(speakChineseLoding, "rotation", 0f, 360f)
        objectAnimator.duration = 1000
        objectAnimator.repeatMode = ValueAnimator.INFINITE
        objectAnimator.repeatCount = 20
        objectAnimator.interpolator = AccelerateInterpolator()
        objectAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                speakChineseLoding.visibility = View.GONE
                super.onAnimationEnd(animation)
            }
        })
        objectAnimator.start()
        val intent = Intent()
        speakChineseRecyclerView.layoutManager = LinearLayoutManager(mcontext)
        val bmobQuery: BmobQuery<speakChinese>? = BmobQuery<speakChinese>()
        bmobQuery?.order("-createdAt")
        bmobQuery?.findObjects(object : FindListener<speakChinese>() {
            override fun done(p0: MutableList<speakChinese>?, p1: BmobException?) {
                chineseList = p0
                videoAdapter = context?.let { chineseList?.let { it1 -> VideoAdapter(it, it1) } }
                videoAdapter?.setOnItemClickListener { _, position ->
                    chineseList!![position].let {
                        Log.e("mlog", it.videoUrl)
                        Log.e("mlog", it.imgUrl)
                        Log.e("mlog", it.title)
                        val bundle = Bundle()
                        bundle.putString("VIDEOTITLE", it.title)
                        bundle.putString("VIDEOIMGURL", it.imgUrl)
                        bundle.putString("VIDEOURL", it.videoUrl)
                        intent.putExtras(bundle)
                    }
                    startActivity(intent.setClass(context, VideoActivity::class.java))
                }
                speakChineseRecyclerView.adapter = videoAdapter
            }
        })
    }
}