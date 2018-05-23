package com.kotlin.app.framework

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bzmaster.cloudextendinsurance.app.interfaces.OnItemRecyclerViewClickListener

/**
 * Created by sunxianpeng on 2018/3/27.
 */
abstract class BaseRecyclerAdapter<VH : RecyclerView.ViewHolder>(context: Context) : RecyclerView.Adapter<VH>() {

    protected lateinit var itemRootView: View
    protected val context: Context = context

    open var onItemRecyclerViewClickListener: OnItemRecyclerViewClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        itemRootView = onItemContentView(LayoutInflater.from(context), parent, viewType)
        return onCreateViewHolder(itemRootView, viewType)
    }

    /**
     * 绑定adapter View视图
     * @param layoutInflater
     * @param viewGroup
     * @return
     */
    protected abstract fun onItemContentView(layoutInflater: LayoutInflater, viewGroup: ViewGroup, viewType: Int): View

    /**
     * 实例化Viewholder
     * @param itemView
     * @param viewType
     * @return
     */
    protected abstract fun onCreateViewHolder(itemView: View, viewType: Int): VH

    override fun onBindViewHolder(holder: VH, position: Int) {
        onFindViewHolder(holder, position)
        val view = initItemViewGroup(holder)
        view?.setOnClickListener {
            onItemRecyclerViewClickListener?.onItemClickListener(view, position)
        }
    }

    /**
     * 设置item点击事件
     * @param holder
     * @return 最外层的主布局
     */
    protected fun initItemViewGroup(holder: VH): View? {
        return itemRootView
    }

    /**
     * 处理adapter数据逻辑
     * @param hold
     * @param position
     */
    protected abstract fun onFindViewHolder(holder: VH, position: Int)

    abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
