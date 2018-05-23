package com.xpsun.opencvdemo.framework

import android.app.Activity
import android.app.Application


/**
 * Created by sunxianpeng on 2018/3/27.
 */
open class AppApplication : Application() {

    var activityList: MutableList<Activity> = arrayListOf()

    open fun cleanAllActivity() {
        for (i in activityList.listIterator()) {
            i.finish()
        }
        activityList.clear()
    }

    open fun removeActivity(activity: Activity) {
        if (activityList.contains(activity)) {
            activityList.remove(activity)
        }
    }

    companion object {
        lateinit var instance: AppApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

    }
}