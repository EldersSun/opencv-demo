package com.xpsun.opencvdemo.activity

import android.content.Context
import android.content.Intent
import com.xpsun.opencvdemo.R
import com.xpsun.opencvdemo.framework.BaseActivity

/**
 * 检测目标
 */
class DetectionTargetActivity : BaseActivity() {

    override fun initWidgetsLayoutId(): Int {
        return R.layout.ac_detection_target_layout
    }

    override fun initWidgets() {
    }

    override fun initWidgetsInstance() {
    }

    override fun initWidgetsEvent() {
    }

    companion object {
        fun start(context: Context){
            val intent :Intent = Intent(context,DetectionTargetActivity::class.java)
            context.startActivity(intent)
        }
    }

}