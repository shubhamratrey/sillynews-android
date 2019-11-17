package com.sillylife.sillynews.views.adapter

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sillylife.sillynews.R
import com.sillylife.sillynews.models.RssDataItem
import com.sillylife.sillynews.models.responses.HomeDataResponse
import com.sillylife.sillynews.utils.CommonUtil
import com.sillylife.sillynews.utils.ImageManager
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_home_news.*

class HomeAllAdapter(
        val context: Context,
        val homeDataResponse: HomeDataResponse,
        val listener: (Any, Int, Int) -> Unit) : RecyclerView.Adapter<HomeAllAdapter.ViewHolder>() {

    val commonItemLists = ArrayList<Any>()
    var pageNo = 1
    var rssPageNo = 1
    var scrollBackPosition: Int = 6
    var TAG = HomeAllAdapter::class.java.simpleName


    companion object {
        const val PROGRESS_VIEW = 0
        const val NEWS = 1
        const val SCROLLBACK_SHOW_ID = -111
        const val SCROLLBACK_HIDE_ID = -222
        const val UPDATE_RADIO = "update_radio"
    }

    init {
        if (homeDataResponse.rssItems != null && homeDataResponse.rssItems!!.isNotEmpty()) {
            pageNo++
            commonItemLists.addAll(homeDataResponse.rssItems!!)
            if (homeDataResponse.hasMore != null && homeDataResponse.hasMore!!) {
                commonItemLists.add(PROGRESS_VIEW)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (commonItemLists[position] is RssDataItem) {
            NEWS
        } else {
            PROGRESS_VIEW
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = when (viewType) {
            NEWS -> LayoutInflater.from(context).inflate(R.layout.item_home_news, parent, false)
            PROGRESS_VIEW -> LayoutInflater.from(context).inflate(R.layout.item_progress, parent, false)
            else -> LayoutInflater.from(context).inflate(R.layout.item_progress, parent, false)
        }
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return commonItemLists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder.itemViewType) {
            NEWS -> {
                setNewsView(holder, position)
            }

        }
        if (holder.adapterPosition == itemCount - 1) {
            if (homeDataResponse.hasMore != null && homeDataResponse.hasMore!!) {
                listener(pageNo, -1, rssPageNo)
            } else if (!homeDataResponse.hasMore!! && homeDataResponse.hasMoreRss!!) {
                listener(1, -1, rssPageNo)
            }

//            if (position > scrollBackPosition) {
//                // show scroll back visible
//                listener(SCROLLBACK_SHOW_ID, -1, rssPageNo)
//            } else {
//                // hide scroll back visible
//                listener(SCROLLBACK_HIDE_ID, -1, rssPageNo)
//            }
        }
    }

    private fun setNewsView(holder: ViewHolder, position: Int) {
        val item = commonItemLists[position] as RssDataItem
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
                listener(item, position, rssPageNo)
            }
        }
    }

    fun addMoreData(homeDataResponse: HomeDataResponse) {
        val oldSize = itemCount
        commonItemLists.remove(PROGRESS_VIEW)
        if (homeDataResponse.rssItems != null && homeDataResponse.rssItems!!.isNotEmpty()) {
            pageNo++
            this.homeDataResponse.rssItems!!.addAll(homeDataResponse.rssItems!!)
            this.homeDataResponse.hasMore = homeDataResponse.hasMore
            this.homeDataResponse.hasMoreRss = homeDataResponse.hasMoreRss
            commonItemLists.addAll(homeDataResponse.rssItems!!)
        }
        if (homeDataResponse.hasMore!!) {
            commonItemLists.add(PROGRESS_VIEW)
        }
        if (!homeDataResponse.hasMore!! && homeDataResponse.hasMoreRss!!){
            rssPageNo++
            pageNo=1
        }
        notifyItemRangeChanged(oldSize, itemCount)
    }

    fun clearData(){
        commonItemLists.clear()
    }

    class ItemDecoration(private val leftMargin: Int, private val topMargin: Int, private val rightMargin: Int, private val verticalSpaceHeight: Int, private val lastItemSpace: Int) :
            RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = topMargin
            }
            outRect.left = leftMargin
            outRect.right = rightMargin

            if (lastItemSpace != 0 && parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1) {
                outRect.bottom = lastItemSpace
            } else {
                outRect.bottom = verticalSpaceHeight
            }
        }
    }

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer
}