package com.xpsun.opencvdemo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kotlin.app.framework.BaseRecyclerAdapter

class HomeContentShowAdapter(context: Context) : BaseRecyclerAdapter<HomeContentShowAdapter.ViewHolder>(context) {

    var menus: List<String>? = null

    override fun onItemContentView(layoutInflater: LayoutInflater, viewGroup: ViewGroup, viewType: Int): View {
        return layoutInflater.inflate(R.layout.v_home_content_show_item_layout, viewGroup, false)
    }

    override fun onCreateViewHolder(itemView: View, viewType: Int): ViewHolder {
        return ViewHolder(itemView)
    }

    override fun onFindViewHolder(holder: ViewHolder, position: Int) {
        val text = menus!![position]
        holder.tvShow.text = text

    }

    override fun getItemCount(): Int {
        return if (null == menus) {
            0
        } else {
            menus!!.size
        }
    }


    inner class ViewHolder(itemView: View) : BaseRecyclerAdapter.BaseViewHolder(itemView) {
        var tvShow: TextView = itemView.findViewById(R.id.home_content_item_show)
    }
}