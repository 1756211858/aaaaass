package com.night.xvideos.activity

import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import com.night.xvideos.R
import com.night.xvideos.fragment.BaseFragment
import com.night.xvideos.fragment.BlackedFragment
import com.night.xvideos.fragment.OutofFragment
import com.night.xvideos.fragment.SpeakChineseFragment
import kotlinx.android.synthetic.main.activity_hotvideo.*

class HotVideo : BaseActivity() {
    private val titles = arrayOf("说中文的色情", "黑人", "中出")
    private var fragmentPagerAdapter: FragmentPagerAdapter? = null
    var fragmentList: MutableList<BaseFragment>? = mutableListOf()
    override fun setLayoutId(): Int {
        return R.layout.activity_hotvideo
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun initContentView() {
        fragmentList?.add(SpeakChineseFragment.get())
        fragmentList?.add(BlackedFragment.get())
        fragmentList?.add(OutofFragment.get())

        for (i in 0 until titles.size) {
            hotVideo_tabLayout.addTab(hotVideo_tabLayout.newTab())
            hotVideo_tabLayout.getTabAt(i)?.text = titles[i]
        }
        hotVideo_tabLayout.tabMode = TabLayout.MODE_FIXED

        fragmentPagerAdapter = object : FragmentPagerAdapter(this.supportFragmentManager) {
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
        hotVideo_tabLayout.setupWithViewPager(hotVideo_viewPager)
        hotVideo_viewPager.adapter = fragmentPagerAdapter
        hotVideo_viewPager.offscreenPageLimit=3
        hotVideo_tabLayout.getTabAt(0)?.select()
    }

    override fun onDestroy() {

        super.onDestroy()
    }
}