package com.sillylife.sillynews.views.adapter

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sillylife.sillynews.R
import com.sillylife.sillynews.constants.Constants
import com.sillylife.sillynews.models.HomeDataItem
import com.sillylife.sillynews.models.responses.HomeDataResponse
import com.sillylife.sillynews.utils.CommonUtil
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_home_insta.*


class HomeAllAdapter(
        val context: Context,
        private val homeDataResponse: HomeDataResponse,
        val listener: (Any, Int) -> Unit
) : RecyclerView.Adapter<HomeAllAdapter.HomeAllViewPagerHolder>() {

    val commonItemLists = ArrayList<Any>()
    var pageNo = 1

    companion object {
        const val PROGRESS_VIEW = 0
        const val NEWS_GROUP = 1
        const val TWEETS = 2
        const val INSTA_FEED = 3
        const val TASK_FEED = 4

        const val SCROLLBACK_SHOW_ID = -111
        const val SCROLLBACK_HIDE_ID = -222

        const val SCROLL_BACK_POSITION: Int = 6
        val TAG = HomeAllAdapter::class.java.simpleName
    }

    init {
        if (homeDataResponse.dataItems != null && homeDataResponse.dataItems!!.isNotEmpty()) {
            pageNo++
            commonItemLists.addAll(homeDataResponse.dataItems!!)
            if (homeDataResponse.hasMore != null && homeDataResponse.hasMore!!) {
                commonItemLists.add(PROGRESS_VIEW)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            commonItemLists[position] is Int -> PROGRESS_VIEW
            commonItemLists[position] is HomeDataItem -> {
                val homeDataItem = commonItemLists[position] as HomeDataItem
                when {
                    homeDataItem.type.equals(Constants.TWEETS) -> TWEETS
                    homeDataItem.type.equals(Constants.INSTA_FEED) -> INSTA_FEED
                    homeDataItem.type.equals(Constants.TASK_FEED) -> TASK_FEED
                    homeDataItem.type.equals(Constants.NEWS_GROUP) -> NEWS_GROUP
                    else -> NEWS_GROUP
                }
            }
            else -> PROGRESS_VIEW
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAllViewPagerHolder {
        val view = when (viewType) {
            NEWS_GROUP -> LayoutInflater.from(context).inflate(R.layout.item_home_news, parent, false)
            INSTA_FEED -> LayoutInflater.from(context).inflate(R.layout.item_home_insta, parent, false)
            TASK_FEED -> LayoutInflater.from(context).inflate(R.layout.item_home_task, parent, false)
            else -> LayoutInflater.from(context).inflate(R.layout.item_progress, parent, false)
        }
        return HomeAllViewPagerHolder(view)
    }

    override fun getItemCount(): Int {
        return commonItemLists.size
    }

    override fun onBindViewHolder(holder: HomeAllViewPagerHolder, position: Int) {
        when (holder.itemViewType) {
            NEWS_GROUP -> {
                setNewsGroup(holder)
            }
            TWEETS -> {
                setTweets(holder)
            }
            INSTA_FEED -> {
                setInstaFeed(holder)
            }
            TASK_FEED -> {
                setTaskDetails(holder)
            }
        }
        if (holder.adapterPosition == itemCount - 1) {
            if (homeDataResponse.hasMore != null && homeDataResponse.hasMore!!) {
                listener(pageNo, -1)
            }
        }

        if (position > SCROLL_BACK_POSITION) {
            // show scroll back visible
            listener(SCROLLBACK_SHOW_ID, -1)
        } else {
            // hide scroll back visible
            listener(SCROLLBACK_HIDE_ID, -1)
        }

    }

    private fun setTaskDetails(holder: HomeAllViewPagerHolder) {
        val homeDataItem = commonItemLists[holder.adapterPosition] as HomeDataItem
        if (homeDataItem.userInfo != null) {

        }
    }

    private fun setInstaFeed(holder: HomeAllViewPagerHolder) {
        val homeDataItem = commonItemLists[holder.adapterPosition] as HomeDataItem
        if (homeDataItem.instaFeed != null) {
//
//        if (holder.commonRcv.itemDecorationCount == 0) {
//            val resource = context.resources
//            val startMargin = resource.getDimensionPixelSize(R.dimen.dp_14)
//            val endMargin = resource.getDimensionPixelSize(R.dimen.dp_8)
//            val betweenMargin = resource.getDimensionPixelSize(R.dimen.dp_4)
//            val bottomMargin = resource.getDimensionPixelSize(R.dimen.dp_9)
//            holder.commonRcv.addItemDecoration(MixedItemDecoration(startMargin, betweenMargin, endMargin, bottomMargin))
//        }
            holder.commonRcv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            val adapter = InstaFeedItemAdapter(context, homeDataItem.instaFeed!!) { it, position ->
                listener(it, position)
            }
            //adapter.setHasStableIds(true)
            holder.commonRcv.setHasFixedSize(true)
            holder.commonRcv.setItemViewCacheSize(10)
            holder.commonRcv.isNestedScrollingEnabled = false
            holder.commonRcv.adapter = adapter
            holder.commonRcv.clearOnScrollListeners()
            holder.commonRcv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_SETTLING) {

                    }
                }
            })
        }
    }

    private fun setTweets(holder: HomeAllViewPagerHolder) {
        val homeDataItem = commonItemLists[holder.adapterPosition] as HomeDataItem
    }

    private fun setNewsGroup(holder: HomeAllViewPagerHolder) {
        val homeDataItem = commonItemLists[holder.adapterPosition] as HomeDataItem
        if (homeDataItem.news != null) {
            if (holder.commonRcv.itemDecorationCount == 0) {
                holder.commonRcv.addItemDecoration(RadiosItemDecoration(0, CommonUtil.dpToPx(10), 0))
            }
            holder.commonRcv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            val adapter = NewsItemAdapter(context, homeDataItem.news!!) { it, position ->
                listener(it, position)
            }
            //adapter.setHasStableIds(true)
            holder.commonRcv.setHasFixedSize(true)
            holder.commonRcv.setItemViewCacheSize(10)
            holder.commonRcv.isNestedScrollingEnabled = false
            holder.commonRcv.adapter = adapter
            holder.commonRcv.clearOnScrollListeners()
            holder.commonRcv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_SETTLING) {

                    }
                }
            })
        }
    }


    fun addMoreData(homeDataResponse: HomeDataResponse) {
        val oldSize = itemCount
        commonItemLists.remove(PROGRESS_VIEW)
        if (homeDataResponse.dataItems != null && homeDataResponse.dataItems!!.isNotEmpty()) {
            pageNo++
            this.homeDataResponse.dataItems!!.addAll(homeDataResponse.dataItems!!)
            this.homeDataResponse.hasMore = homeDataResponse.hasMore
            commonItemLists.addAll(homeDataResponse.dataItems!!)
        }
        commonItemLists.add(PROGRESS_VIEW)
        notifyItemRangeChanged(oldSize, itemCount)
    }

    class HomeAllViewPagerHolder(override val containerView: View) :
            RecyclerView.ViewHolder(containerView),
            LayoutContainer

    class ChannelsItemDecoration(val startMargin: Int, val betweenMargin: Int, val endMargin: Int) :
            RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            val adapter = parent.adapter as HomeAllAdapter
            if (position != RecyclerView.NO_POSITION) {
                when (position) {
                    0 -> {
                        outRect.left = startMargin
                        outRect.right = betweenMargin / 2
                    }
                    adapter.itemCount - 1 -> {
                        outRect.right = endMargin
                        outRect.left = betweenMargin / 2
                    }
                    else -> {
                        outRect.right = betweenMargin / 2
                        outRect.left = betweenMargin / 2
                    }
                }
            }
        }
    }

    class RadiosItemDecoration(val startMargin: Int, val betweenMargin: Int, val endMargin: Int) :
            RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            val adapter = parent.adapter as NewsItemAdapter
            if (position != RecyclerView.NO_POSITION) {
                when (position) {
                    0 -> {
                        outRect.left = startMargin
                        outRect.right = betweenMargin / 2
                    }
                    adapter.itemCount - 1 -> {
                        outRect.right = endMargin
                        outRect.left = betweenMargin / 2
                    }
                    else -> {
                        outRect.right = betweenMargin / 2
                        outRect.left = betweenMargin / 2
                    }
                }
            }
        }
    }


}