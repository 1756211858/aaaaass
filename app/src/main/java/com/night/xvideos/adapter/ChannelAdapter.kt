package com.night.xvideos.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.night.xvideos.R
import com.night.xvideos.bean.ChannelBean
import kotlinx.android.synthetic.main.channel_item.view.*

/**
 * 第一个主界面
 */
class ChannelAdapter(private var context: Context,
                     private var list: MutableList<ChannelBean>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mClickListener: ((View, Int) -> Unit)? = null
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //holder.setIsRecyclable(false)
        with(holder as ChannelViewHolder) {
            holder.bind(list[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ChannelViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.channel_item, parent, false)
                , mClickListener)

    }
    fun setOnItemClickListener(listener: ((View, Int) -> Unit)?) {
        mClickListener = listener
    }
    override fun getItemCount(): Int {
        return list.size
    }


    inner class ChannelViewHolder(itemView: View,
                                  private var mClickListener: ((View, Int) -> Unit)?) :
            RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            if (v != null) {
                mClickListener?.invoke(v, layoutPosition)
            }
        }

        fun bind(bean: ChannelBean) {
            Glide.with(context).load(bean.imgUrl).into(itemView.channel_imageView)
            itemView.channel_textView.text = bean.imgText
        }
    }
}
