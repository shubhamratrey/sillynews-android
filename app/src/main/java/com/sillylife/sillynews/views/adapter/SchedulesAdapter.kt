package com.sillylife.sillynews.views.adapter

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sillylife.sillynews.R
import com.sillylife.sillynews.models.HomeDataItem
import com.sillylife.sillynews.models.Schedule
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_schedule.*


class SchedulesAdapter(
    val context: Context,
    private val response: HomeDataItem,
    val listener: (Any, Int) -> Unit
) : RecyclerView.Adapter<SchedulesAdapter.Holder>() {

    val commonItemLists = ArrayList<Any>()
    var pageNo = 1

    companion object {
        const val PROGRESS_VIEW = 0
        const val SCHEDULE = 1

        val TAG = SchedulesAdapter::class.java.simpleName
    }

    init {
        if (response.schedules != null && response.schedules?.isNotEmpty()!!) {
            pageNo++
            response.schedules!!.forEach {
                commonItemLists.add(it)
            }
            if (response.hasMore != null && response.hasMore!!) {
                commonItemLists.add(PROGRESS_VIEW)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            commonItemLists[position] is Int -> PROGRESS_VIEW
            commonItemLists[position] is Schedule -> SCHEDULE
            else -> PROGRESS_VIEW
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = when (viewType) {
            SCHEDULE -> LayoutInflater.from(context).inflate(
                R.layout.item_schedule,
                parent,
                false
            )
            else -> LayoutInflater.from(context).inflate(R.layout.item_progress, parent, false)
        }
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return commonItemLists.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        when (holder.itemViewType) {
            SCHEDULE -> {
                setSchedule(holder)
            }
        }
        if (holder.adapterPosition == itemCount - 1) {
            if (response.hasMore != null && response.hasMore!!) {
                listener(pageNo, -1)
            }
        }
    }

    private fun setSchedule(holder: Holder) {
        val item = commonItemLists[holder.adapterPosition] as Schedule
        holder.startTime.text = item.start_time
        holder.title.text = item.title

    }

    fun addMoreData(response: HomeDataItem) {
        val oldSize = itemCount
        commonItemLists.remove(PROGRESS_VIEW)
        if (response.schedules != null && response.schedules!!.isNotEmpty()) {
            pageNo++
            this.response.schedules!!.addAll(response.schedules!!)
            this.response.hasMore = response.hasMore

            response.schedules!!.forEach {
                commonItemLists.add(it)
            }
//            commonItemLists.addAll(response.schedules!!)
        }
        commonItemLists.add(PROGRESS_VIEW)
        notifyItemRangeChanged(oldSize, itemCount)
    }

    class Holder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer

    class ItemDecoration(val startMargin: Int, val betweenMargin: Int, val endMargin: Int) :
        RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            val adapter = parent.adapter as SchedulesAdapter
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