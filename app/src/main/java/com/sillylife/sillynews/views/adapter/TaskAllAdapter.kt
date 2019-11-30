package com.sillylife.sillynews.views.adapter

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sillylife.sillynews.R
import com.sillylife.sillynews.models.HomeDataItem
import com.sillylife.sillynews.models.Schedule
import com.sillylife.sillynews.models.Task
import com.sillylife.sillynews.models.UserProfile
import com.sillylife.sillynews.models.responses.HomeDataResponse
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_home_brief.*
import kotlinx.android.synthetic.main.item_home_insta.*
import kotlinx.android.synthetic.main.item_schedule.view.*
import kotlinx.android.synthetic.main.item_task.*


class TaskAllAdapter(
    val context: Context,
    private val response: HomeDataResponse,
    val listener: (Any, Int) -> Unit
) : RecyclerView.Adapter<TaskAllAdapter.HomeAllViewPagerHolder>() {

    val commonItemLists = ArrayList<Any>()
    var pageNo = 1

    companion object {
        const val PROGRESS_VIEW = 0
        const val INFO = 1
        const val SCHDEULE = 2
        const val TASK = 3
        const val USER_INFO = "user_info"
        const val SCHEDULES = "schedules"
        const val TASKS = "tasks"

        val TAG = TaskAllAdapter::class.java.simpleName
    }

    init {
        if (response.dataItems != null && response.dataItems!!.isNotEmpty()) {
            pageNo++
//            commonItemLists.addAll(response.dataItems!!)

            response.dataItems!!.forEach {
                if (it.type == USER_INFO){
                    commonItemLists.add(it.userInfo!!)
                } else if (it.type == SCHEDULES){
                    commonItemLists.add(it)
                } else if (it.type == TASKS){
                    it.tasks.forEach { task ->
                        commonItemLists.add(task)
                    }
                }
            }

            if (response.hasMore != null && response.hasMore!!) {
                commonItemLists.add(PROGRESS_VIEW)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            commonItemLists[position] is Int -> PROGRESS_VIEW
            commonItemLists[position] is Task -> TASK
            commonItemLists[position] is UserProfile -> INFO
            commonItemLists[position] is HomeDataItem-> SCHDEULE
//            commonItemLists[position] is HomeDataItem -> {
//                val homeDataItem = commonItemLists[position] as HomeDataItem
//                when {
//                    homeDataItem.type.equals(USER_INFO) -> INFO
//                    homeDataItem.type.equals(SCHEDULES) -> SCHDEULE
//                    homeDataItem.type.equals(TASKS) -> TASK
//                    else -> PROGRESS_VIEW
//                }
//            }
            else -> PROGRESS_VIEW
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAllViewPagerHolder {
        val view = when (viewType) {
            INFO -> LayoutInflater.from(context).inflate(R.layout.item_home_brief, parent, false)
            SCHDEULE -> LayoutInflater.from(context).inflate(
                R.layout.item_home_insta,
                parent,
                false
            )
            TASK -> LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
            else -> LayoutInflater.from(context).inflate(R.layout.item_progress, parent, false)
        }
        return HomeAllViewPagerHolder(view)
    }

    override fun getItemCount(): Int {
        return commonItemLists.size
    }

    override fun onBindViewHolder(holder: HomeAllViewPagerHolder, position: Int) {
        when (holder.itemViewType) {
            INFO -> {
                setInfo(holder)
            }
            SCHDEULE -> {
                setSchedules(holder)
            }
            TASK -> {
                setTask(holder)
            }
        }
        if (holder.adapterPosition == itemCount - 1) {
            if (response.hasMore != null && response.hasMore!!) {
                listener(pageNo, -1)
            }
        }
    }

    private fun setTask(holder: HomeAllViewPagerHolder) {
        val item = commonItemLists[holder.adapterPosition] as Task?
        if (item != null) {
            holder.task_title.text = item.title
        }
    }

    private fun setInfo(holder: HomeAllViewPagerHolder) {
        val item = commonItemLists[holder.adapterPosition] as UserProfile?
        if (item != null) {
            holder.username.text = item.name
        }
    }

    private fun setSchedules(holder: HomeAllViewPagerHolder) {
        val homeDataItem = commonItemLists[holder.adapterPosition] as HomeDataItem
        if (homeDataItem.schedules != null) {
//
            if (holder.commonRcv.itemDecorationCount == 0) {
                val resource = context.resources
                val startMargin = resource.getDimensionPixelSize(R.dimen.dp_4)
                val endMargin = resource.getDimensionPixelSize(R.dimen.dp_4)
                val betweenMargin = resource.getDimensionPixelSize(R.dimen.dp_4)
                holder.commonRcv.addItemDecoration(
                    SchedulesAdapter.ItemDecoration(
                        startMargin,
                        betweenMargin,
                        endMargin
                    )
                )
            }
            holder.commonRcv.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val adapter = SchedulesAdapter(context, homeDataItem) { it, position ->
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
            this.response.dataItems!!.addAll(homeDataResponse.dataItems!!)
            this.response.hasMore = homeDataResponse.hasMore
//            commonItemLists.addAll(homeDataResponse.dataItems!!)
            homeDataResponse.dataItems!!.forEach {
                if (it.type == USER_INFO){
                    commonItemLists.add(it.userInfo!!)
                } else if (it.type == SCHEDULES){
                    commonItemLists.add(it)
                } else if (it.type == TASKS){
                    it.tasks.forEach { task ->
                        commonItemLists.add(task)
                    }
                }
            }
            if (homeDataResponse.hasMore!!) {
                commonItemLists.add(PROGRESS_VIEW)
            }
        }
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
            val adapter = parent.adapter as TaskAllAdapter
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