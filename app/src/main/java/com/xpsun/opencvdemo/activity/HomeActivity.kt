package com.xpsun.opencvdemo.activity

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import bzmaster.cloudextendinsurance.app.interfaces.OnItemRecyclerViewClickListener
import com.xpsun.opencvdemo.HomeContentShowAdapter
import com.xpsun.opencvdemo.R
import com.xpsun.opencvdemo.framework.BaseActivity

class HomeActivity : BaseActivity(), OnItemRecyclerViewClickListener {

    private lateinit var homeContentShow: RecyclerView
    private lateinit var homeContentShowAdapter: HomeContentShowAdapter

    override fun initWidgetsLayoutId(): Int {
        return R.layout.ac_home_layout
    }

    override fun initWidgets() {
        homeContentShow = findViewById(R.id.home_content_show)
    }

    override fun initWidgetsInstance() {
        homeContentShow.layoutManager = LinearLayoutManager(this)
        homeContentShowAdapter = HomeContentShowAdapter(this)
        homeContentShow.adapter = homeContentShowAdapter

        homeContentShowAdapter.menus = menus
        homeContentShowAdapter.notifyDataSetChanged()

    }

    override fun initWidgetsEvent() {
        homeContentShowAdapter.onItemRecyclerViewClickListener = this
    }

    /**
     * 0.模糊
     * 1.高斯模糊
     * 2.中值模糊
     * 3.图像锐化
     * 4.膨胀图像
     * 5.腐蚀图像
     * 6.阈值
     */
    override fun onItemClickListener(view: View?, position: Int) {
        when (position) {
            in 0..6 -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(ACTION_MODE_TAG, position)
                startActivity(intent)
            }
        }
    }

    companion object {
        const val ACTION_MODE_TAG: String = "ACTION_MODE"
        private val menus = listOf<String>(
                "mean blur - 模糊", "gaussian blur - 高斯模糊", "median blur - 中值模糊",
                "sharpen - 图像锐化", "dilate - 膨胀图像", "erode - 腐蚀图像","threshold - 阈值")
    }
}