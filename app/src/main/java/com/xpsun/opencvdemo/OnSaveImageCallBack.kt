package com.xpsun.opencvdemo

/**
 * Created by sunxianpeng on 2018/3/29.
 */
interface OnSaveImageCallBack {

    fun onSuccess(imagePath: String)

    fun onFail()
    
}