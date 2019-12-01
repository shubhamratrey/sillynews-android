package com.sillylife.sillynews.views.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sillylife.sillynews.R
import com.sillylife.sillynews.constants.Constants
import com.sillylife.sillynews.models.HomeDataItem
import com.sillylife.sillynews.models.Task
import com.sillylife.sillynews.models.UserProfile
import com.sillylife.sillynews.models.responses.HomeDataResponse
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_header.*
import kotlinx.android.synthetic.main.item_home_brief.*
import kotlinx.android.synthetic.main.item_schedules.*
import kotlinx.android.synthetic.main.item_task.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs


class TaskAllAdapter(
        val context: Context,
        private val response: HomeDataResponse,
        val listener: (Any, Int, String) -> Unit
) : RecyclerView.Adapter<TaskAllAdapter.HomeAllViewPagerHolder>() {

    val commonItemLists = ArrayList<Any>()
    var pageNo = 1

    companion object {
        const val PROGRESS_VIEW = 0
        const val INFO = 1
        const val SCHEDULE = 2
        const val TASK = 3
        const val HEADER = 4
        const val USER_INFO = "user_info"
        const val TASKS_HEADER = "tasks_header"
        const val SCHEDULES = "schedules"
        const val TASKS = "tasks"

        val TAG = TaskAllAdapter::class.java.simpleName
    }

    init {
        if (response.dataItems != null && response.dataItems!!.isNotEmpty()) {
            pageNo++
            response.dataItems!!.forEach {
                if (it.type == USER_INFO) {
                    commonItemLists.add(it.userInfo!!)
                } else if (it.type == SCHEDULES) {
                    commonItemLists.add(it)
                } else if (it.type == TASKS) {
                    it.tasks.forEach { task ->
                        commonItemLists.add(task)
                    }
                }
            }
            commonItemLists.add(2, TASKS_HEADER)
            if (response.hasMore != null && response.hasMore!!) {
                commonItemLists.add(PROGRESS_VIEW)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            commonItemLists[position] is Int -> PROGRESS_VIEW
            commonItemLists[position] is String -> HEADER
            commonItemLists[position] is Task -> TASK
            commonItemLists[position] is UserProfile -> INFO
            commonItemLists[position] is HomeDataItem -> SCHEDULE
            else -> PROGRESS_VIEW
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAllViewPagerHolder {
        val view = when (viewType) {
            INFO -> LayoutInflater.from(context).inflate(R.layout.item_home_brief, parent, false)
            SCHEDULE -> LayoutInflater.from(context).inflate(R.layout.item_schedules, parent, false)
            TASK -> LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
            HEADER -> LayoutInflater.from(context).inflate(R.layout.item_header, parent, false)
            else -> LayoutInflater.from(context).inflate(R.layout.item_progress, parent, false)
        }
        return HomeAllViewPagerHolder(view)
    }

    override fun getItemCount(): Int {
        return commonItemLists.size
    }

    override fun onBindViewHolder(holder: HomeAllViewPagerHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads != null && payloads.isNotEmpty()) {
            for (any in payloads) {
                if (any is Task) {
                    if (commonItemLists[holder.adapterPosition] is Task) {
                        val item = commonItemLists[holder.adapterPosition] as Task
                        item.status = any.status
                        setTask(holder)
                    }
                } else if (any is HomeDataItem) {
                    if (commonItemLists[holder.adapterPosition] is HomeDataItem) {
                        val item = commonItemLists[holder.adapterPosition] as HomeDataItem
                        item.schedules = any.schedules
                        item.hasMore = any.hasMore
                        val adapter = holder.commonRcv.adapter as SchedulesAdapter
                        adapter.addMoreData(item)
                    }
                }
            }
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onBindViewHolder(holder: HomeAllViewPagerHolder, position: Int) {
        when (holder.itemViewType) {
            INFO -> {
                setInfo(holder)
            }
            SCHEDULE -> {
                setSchedules(holder)
            }
            TASK -> {
                setTask(holder)
            }
            HEADER -> {
                holder.header_text.text = "Tasks"
            }
        }
        if (holder.adapterPosition == itemCount - 1) {
            if (response.hasMore != null && response.hasMore!!) {
                listener(pageNo, -1, Constants.TASK_PAGINATE)
            }
        }
    }

    private fun setTask(holder: HomeAllViewPagerHolder) {
        val item = commonItemLists[holder.adapterPosition] as Task?
        if (item != null) {
            holder.task_title.text = item.title
            if (item.status != null && item.status == Constants.TASK_COMPLETED) {
                holder.task_title.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                holder.task_check.isChecked = true
            } else {
                holder.task_title.paintFlags = 0
                holder.task_check.isChecked = false
            }

            holder.task_check.setOnClickListener {
                listener(item, holder.adapterPosition, Constants.TASK_CHECKBOX)
            }
        }
    }

    private fun setInfo(holder: HomeAllViewPagerHolder) {
        val item = commonItemLists[holder.adapterPosition] as UserProfile?
        if (item != null) {
            holder.username.text = item.name
            holder.wishTitle.text = "Good Morning,"
        }
    }

    var schedulesAdapter: SchedulesAdapter? = null
    private fun setSchedules(holder: HomeAllViewPagerHolder) {
        val homeDataItem = commonItemLists[holder.adapterPosition] as HomeDataItem
        if (homeDataItem.schedules != null) {
            if (holder.commonRcv.itemDecorationCount == 0) {
                val resource = context.resources
                val startMargin = resource.getDimensionPixelSize(R.dimen.dp_20)
                val endMargin = resource.getDimensionPixelSize(R.dimen.dp_20)
                val betweenMargin = resource.getDimensionPixelSize(R.dimen.dp_10)
                holder.commonRcv.addItemDecoration(SchedulesAdapter.ItemDecoration(startMargin, betweenMargin, endMargin))
            }
            holder.commonRcv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            schedulesAdapter = SchedulesAdapter(context, homeDataItem) { it, position, type ->
                listener(it, holder.adapterPosition, type)
            }
            //adapter.setHasStableIds(true)
            holder.commonRcv.setHasFixedSize(true)
            holder.commonRcv.setItemViewCacheSize(10)
            holder.commonRcv.isNestedScrollingEnabled = false
            holder.commonRcv.adapter = schedulesAdapter
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

    fun addMoreScheduleData(response: HomeDataResponse) {
        if (schedulesAdapter != null) {
            response.dataItems!!.forEach {
                if (it.type == SCHEDULES) {
                    schedulesAdapter?.addMoreData(it)
                }
            }
        }
    }

    fun addMoreData(homeDataResponse: HomeDataResponse) {
        val oldSize = itemCount
        commonItemLists.remove(PROGRESS_VIEW)
        if (homeDataResponse.dataItems != null && homeDataResponse.dataItems!!.isNotEmpty()) {
            pageNo++
            this.response.dataItems!!.addAll(homeDataResponse.dataItems!!)
            this.response.hasMore = homeDataResponse.hasMore
            homeDataResponse.dataItems!!.forEach {
                if (it.type == USER_INFO) {
                    commonItemLists.add(it.userInfo!!)
                } else if (it.type == SCHEDULES) {
                    commonItemLists.add(it)
                } else if (it.type == TASKS) {
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


    class HomeAllViewPagerHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer

    inner class RecyclerItemTouchHelper(dragDirs: Int, swipeDirs: Int) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            if (target.adapterPosition == 0 || target.adapterPosition == 1 || target.adapterPosition == 2) {
                return false
            }
            if (commonItemLists[viewHolder.adapterPosition] is Task) {
                val task = commonItemLists[viewHolder.adapterPosition] as Task
                Collections.swap(commonItemLists, viewHolder.adapterPosition, target.adapterPosition);
                notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition);
                return true
            }
            return false
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            if (viewHolder != null) {
                val foregroundView = (viewHolder as HomeAllViewPagerHolder).containerView
                getDefaultUIUtil().onSelected(foregroundView)
            }
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            val foregroundView = (viewHolder as HomeAllViewPagerHolder).containerView
            getDefaultUIUtil().clearView(foregroundView)
        }


        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            if (commonItemLists[viewHolder.adapterPosition] is Task) {
                val task = commonItemLists[viewHolder.adapterPosition] as Task
                listener(task, viewHolder.adapterPosition, "")
            }
        }

        override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
            return super.convertToAbsoluteDirection(flags, layoutDirection)
        }

        override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            if (viewHolder.adapterPosition == -1 || viewHolder.adapterPosition > commonItemLists.size - 1) {
                return 0
            }
            return if (commonItemLists[viewHolder.adapterPosition] is Task)
                super.getSwipeDirs(recyclerView, viewHolder)
            else
                0
        }

        override fun isLongPressDragEnabled(): Boolean {
            return true
        }

        override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                try {
                    var width = viewHolder.itemView.width
                    val alpha = 1.0f - abs(dX) / width.toFloat()
                    viewHolder.itemView.alpha = alpha
                    viewHolder.itemView.translationX = dX
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            return if (commonItemLists[viewHolder.adapterPosition] is Task) {
                if (recyclerView.layoutManager is GridLayoutManager) {
                    val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                    val swipeFlags = 0
                    ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
                } else {
                    val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                    val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
                    ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
                }
            } else {
                0
            }
        }
    }
}