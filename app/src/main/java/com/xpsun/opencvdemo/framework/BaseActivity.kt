package com.xpsun.opencvdemo.framework

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jph.takephoto.app.TakePhotoFragmentActivity
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader

/**
 * Created by sunxianpeng on 2018/3/27.
 */
abstract class BaseActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(initWidgetsLayoutId())
        initWidgets()
        initWidgetsInstance()
        initWidgetsEvent()
    }

    abstract fun initWidgetsLayoutId():Int

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
}