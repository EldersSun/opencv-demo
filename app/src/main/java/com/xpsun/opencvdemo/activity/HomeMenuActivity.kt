package com.xpsun.opencvdemo.activity

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import bzmaster.cloudextendinsurance.app.interfaces.OnItemRecyclerViewClickListener
import com.xpsun.opencvdemo.HomeContentShowAdapter
import com.xpsun.opencvdemo.R
import com.xpsun.opencvdemo.framework.BaseActivity

class HomeMenuActivity : BaseActivity() {

    private lateinit var homeMenuContentShow: RecyclerView
    private lateinit var homeContentShowAdapter: HomeContentShowAdapter

    override fun initWidgetsLayoutId(): Int {
        return R.layout.ac_home_menu_layout
    }

    override fun initWidgets() {
        homeMenuContentShow = findViewById(R.id.home_menu_content_show)
    }

    override fun initWidgetsInstance() {
        homeMenuContentShow.layoutManager = LinearLayoutManager(this)
        homeContentShowAdapter = HomeContentShowAdapter(this)
        homeMenuContentShow.adapter = homeContentShowAdapter

        homeContentShowAdapter.menus = menus
        homeContentShowAdapter.notifyDataSetChanged()

    }

    override fun initWidgetsEvent() {

        homeContentShowAdapter.onItemRecyclerViewClickListener = object : OnItemRecyclerViewClickListener {
            override fun onItemClickListener(view: View?, position: Int) {
                when (position) {
                    0 -> {
                        HomeActivity.start(this@HomeMenuActivity,menus[position])
                    }
                    1 -> {
                        BaseImageInfoActivity.start(this@HomeMenuActivity,menus[position])
                    }
                    2 -> {
                        DetectionTargetActivity.start(this@HomeMenuActivity,menus[position])
                    }
                    else -> {

                    }
                }
            }
        }
    }

    override fun isShowTitleBackEvent():Boolean{
        return false
    }

    companion object {
        private val menus = listOf<String>("第一章 为图像添加效果", "第二章 检测图像的基本特征",
                "第三章 检测目标")
    }
}