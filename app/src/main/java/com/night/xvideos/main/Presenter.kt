package com.night.xvideos.main

import android.content.Context

class Presenter(var context:Context,var kadoYado: Contract.KadoYado,var baseFragment: Contract.BaseFragment?) : Contract.Presenter {
    private var moudel:Moudel=Moudel()

    override fun analyzeIP() {
        moudel.mContext=context
        moudel.analyzeIP {

        }
    }
}