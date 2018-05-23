package com.xpsun.opencvdemo.framework

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.jph.takephoto.app.TakePhotoFragmentActivity
import com.xpsun.opencvdemo.R
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader

/**
 * Created by sunxianpeng on 2018/3/27.
 */
abstract class BaseActivity : AppCompatActivity() {

    private var actionBar: ActionBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(initWidgetsLayoutId())

        if (isShowTitleBackEvent()) {
            showTitleback()
        }

        initWidgets()
        initWidgetsInstance()
        initWidgetsEvent()
    }

    abstract fun initWidgetsLayoutId(): Int

    abstract fun initWidgets()

    abstract fun initWidgetsInstance()

    abstract fun initWidgetsEvent()

    override fun onResume() {
        super.onResume()
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_13, this, openCVCallBack)
    }

    protected val openCVCallBack = object : BaseLoaderCallback(AppApplication.instance) {
        override fun onManagerConnected(status: Int) {
            super.onManagerConnected(status)
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {

                }
                else -> {
                    super.onManagerConnected(status)
                }
            }
        }
    }

    private fun showTitleback() {
        actionBar = supportActionBar
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    open fun setTitleText(title: String) {
        actionBar?.title = title
    }

    open fun isShowTitleBackEvent(): Boolean = true

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                this.finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}