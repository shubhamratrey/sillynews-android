package com.sillylife.sillynews.views.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sillylife.sillynews.R
import com.sillylife.sillynews.models.InstaItem
import com.sillylife.sillynews.utils.ImageManager

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_instagram_view.*

class InstaFeedItemAdapter(val context: Context,
                           val list: ArrayList<InstaItem>,
                           val listener: (InstaItem, Int) -> Unit) : RecyclerView.Adapter<InstaFeedItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_instagram_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataItem = list[holder.adapterPosition]
        if (dataItem != null) {
            if (dataItem.profile != null) {
                holder.instaUsername.text = dataItem.profile?.username!!
                ImageManager.loadImageCircular(holder.instaProfilePicture, dataItem.profile?.instaProfilePhoto!!)
            }
            if (dataItem.location != null) {
                holder.instaLocation.text = dataItem.location!!
            }
            ImageManager.loadImage(holder.instaPost, dataItem.thumbnail_src)
            ImageManager.loadImage(holder.instaPost, dataItem.display_url)
            holder.containerView.setOnClickListener {
                listener(dataItem, holder.adapterPosition)
            }
        }
    }

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer

}