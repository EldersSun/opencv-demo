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
        val title = intent.getStringExtra(TITLE_TEXT_TAG)
        setTitleText(title)
    }

    override fun initWidgetsEvent() {
    }

    companion object {
        private val TITLE_TEXT_TAG: String = "title_text_tag"

        fun start(context: Context, title: String) {
            val intent: Intent = Intent(context, DetectionTargetActivity::class.java)
            intent.putExtra(TITLE_TEXT_TAG, title)
            context.startActivity(intent)
        }
    }

}