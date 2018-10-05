package com.night.xvideos.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.night.xvideos.R
import com.night.xvideos.bean.speakChinese
import kotlinx.android.synthetic.main.video_item.view.*

class VideoAdapter (private var context: Context, var list: MutableList<speakChinese>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mClickListener: ((View, Int) -> Unit)? = null
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        with(holder as ViewHolder) {
            holder.bind(list[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.video_item, parent, false)
                , mClickListener)

    }
    fun setOnItemClickListener(listener: ((View, Int) -> Unit)?) {
        mClickListener = listener
    }
    override fun getItemCount(): Int {
        return list.size
    }


    inner class ViewHolder(itemView: View, private var mClickListener: ((View, Int) -> Unit)?)
        : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            if (v != null) {
                mClickListener?.invoke(v, layoutPosition)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(bean: speakChinese) {
            Glide.with(context).load(bean.imgUrl).into(itemView.video_imageView)
            itemView.video_title.text = bean.title
            itemView.video_duration.text="视频时长：${bean.duration}"
        }
    }
}