package com.night.xvideos.main

class Contract{
    interface KadoYado:BaseView<Presenter>

    interface BaseFragment:BaseView<Presenter>

    interface Presenter:BasePresenter{
        fun checkNetWork()
    }
}