package com.night.xvideos.activity

import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter
import com.night.xvideos.R
import com.night.xvideos.fragment.BaseFragment
import com.night.xvideos.fragment.BlackedFragment
import com.night.xvideos.fragment.CreampieFragment
import com.night.xvideos.fragment.SpeakChineseFragment
import kotlinx.android.synthetic.main.activity_hotvideo.*

class HotVideo : BaseActivity() {
    private val titles = arrayOf("说中文的色情", "黑人", "中出")
    private var fragmentPagerAdapter: FragmentStatePagerAdapter? = null
    var fragmentList: MutableList<BaseFragment>? = mutableListOf()
    override fun setLayoutId(): Int {
        return R.layout.activity_hotvideo
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun initContentView() {
        fragmentList?.add(SpeakChineseFragment())
        fragmentList?.add(BlackedFragment())
        fragmentList?.add(CreampieFragment())

        for (i in 0 until titles.size) {
            hotVideoTabLayout.addTab(hotVideoTabLayout.newTab())
            hotVideoTabLayout.getTabAt(i)?.text = titles[i]
        }
        hotVideoTabLayout.tabMode = TabLayout.MODE_FIXED

        fragmentPagerAdapter = object : FragmentStatePagerAdapter(this.supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return fragmentList?.get(position)!!
            }

            override fun getCount(): Int {
                return fragmentList?.size!!
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return titles[position]
            }

        }
        hotVideoTabLayout.setupWithViewPager(hotVideoViewPager)
        hotVideoViewPager.adapter = fragmentPagerAdapter
        hotVideoViewPager.offscreenPageLimit = 3
        hotVideoTabLayout.getTabAt(1)?.select()
    }
}