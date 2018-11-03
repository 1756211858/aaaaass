package com.night.xvideos.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.night.xvideos.main.Contract

/**
 * Created by 9 on 2018/3/6.
 */

abstract class BaseFragment : Fragment(),Contract.BaseFragment {
    protected var mcontext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mcontext = activity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return layoutInflater.inflate(initView(),null,false)
    }

    protected abstract fun initView(): Int

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()
    }

    override fun showNetWorkError() {

    }

    protected abstract fun initData()

}
