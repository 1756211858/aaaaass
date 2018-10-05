package com.night.xvideos.activity

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.night.xvideos.R
import com.night.xvideos.fragment.BaseFragment
import com.night.xvideos.fragment.BlackedFragment
import com.night.xvideos.fragment.OutofFragment
import com.night.xvideos.fragment.SpeakChineseFragment
import kotlinx.android.synthetic.main.activity_hotvideo.*

class HotVideo : AppCompatActivity() {
    private val titles = arrayOf("说中文的色情", "黑人", "中出")
    var fragmentPagerAdapter: FragmentPagerAdapter? = null
    var fragmentList: MutableList<BaseFragment>? = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotvideo)
        initView()
    }

    /**
     * tabLayout的初始化
     */
    private fun initView() {
        for (i in 0 until titles.size) {
            hotVideo_tabLayout.addTab(hotVideo_tabLayout.newTab())
            hotVideo_tabLayout.getTabAt(i)?.text = titles[i]
        }
        hotVideo_tabLayout.tabMode = TabLayout.MODE_FIXED
        initFragment()
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
        //设置刚进入时显示哪个tab
        hotVideo_tabLayout.getTabAt(0)?.select()
    }

    private fun initFragment() {
        fragmentList?.add(SpeakChineseFragment.get())
        fragmentList?.add(BlackedFragment.get())
        fragmentList?.add(OutofFragment.get())
    }
}