package com.sillylife.sillynews.views.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sillylife.sillynews.R
import com.sillylife.sillynews.models.NewsItem
import com.sillylife.sillynews.utils.CommonUtil
import com.sillylife.sillynews.utils.ImageManager
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_instagram_view.rootLayout
import kotlinx.android.synthetic.main.item_news.*

class NewsItemAdapter(val context: Context,
                      val list: ArrayList<NewsItem>,
                      val listener: (NewsItem, Int) -> Unit) : RecyclerView.Adapter<NewsItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.newsTitle.text = item.title
        if (item.description != null && !CommonUtil.textIsEmpty(item.description)) {
            holder.newsDescription.text = item.description
            holder.newsDescription.visibility = View.VISIBLE
        } else {
            holder.newsDescription.visibility = View.GONE
        }

        holder.newsImage.setImageResource(R.drawable.ic_place_holder_episode)
        if (item.images?.original != null) {
            ImageManager.loadImage(holder.newsImage, item.images.original)
            holder.newsImage.visibility = View.VISIBLE
        } else if (item.images?.thumbnail != null) {
            ImageManager.loadImage(holder.newsImage, item.images.thumbnail)
            holder.newsImage.visibility = View.VISIBLE
        } else {
            holder.newsImage.visibility = View.GONE
        }

        if (item.source != null && !CommonUtil.textIsEmpty(item.source) && item.dateTime != null && !CommonUtil.textIsEmpty(item.dateTime)) {
            holder.newsSource.text = item.source + "   " + item.dateTime
            holder.newsSource.visibility = View.VISIBLE
        } else if (item.dateTime != null && !CommonUtil.textIsEmpty(item.dateTime)) {
            holder.newsSource.text = item.dateTime
            holder.newsSource.visibility = View.VISIBLE
        } else if (item.source != null && !CommonUtil.textIsEmpty(item.source)) {
            holder.newsSource.text = item.source
            holder.newsSource.visibility = View.VISIBLE
        } else {
            holder.newsSource.visibility = View.GONE
        }

        holder.rootLayout.setOnClickListener {
            if (item.link != null && !CommonUtil.textIsEmpty(item.link)) {
                listener(item, position)
            }
        }
    }

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer

}