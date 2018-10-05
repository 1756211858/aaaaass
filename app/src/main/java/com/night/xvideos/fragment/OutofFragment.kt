package com.night.xvideos.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.night.xvideos.R

/**
 * 中出频道
 */
class OutofFragment : BaseFragment() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: OutofFragment? = null
            get() {
                if (field == null) {
                    field = OutofFragment()
                }
                return field
            }

        fun get(): OutofFragment {
            return instance!!
        }
    }

    @SuppressLint("InflateParams")
    override fun initView(): View {
        val view = layoutInflater.inflate(R.layout.fragment_outof, null, false)

        return view
    }

    override fun initData() {
    }
}