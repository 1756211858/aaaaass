package com.night.xvideos.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.night.xvideos.R

/**
 * 黑人频道
 */
class BlackedFragment : BaseFragment() {
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
    override fun initView(): View {
        val view = layoutInflater.inflate(R.layout.fragment_blacked, null, false)
        return view
    }

    override fun initData() {

    }
}