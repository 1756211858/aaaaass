package com.night.xvideos.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.metalab.asyncawait.async
import com.night.xvideos.main.Contract

/**
 * Created by 9 on 2018/3/6.
 */

abstract class BaseFragment : Fragment(), Contract.BaseFragment {
    protected var mcontext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mcontext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return layoutInflater.inflate(initView(), container, false)
    }

    protected abstract fun initView(): Int

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initData()
        super.onActivityCreated(savedInstanceState)
    }

    override fun showNetWorkError() {

    }

    protected abstract fun initData()

}
