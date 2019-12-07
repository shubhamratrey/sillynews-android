package com.sillylife.sillynews.views.fragments

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.sillylife.sillynews.R
import com.sillylife.sillynews.SillyNews
import com.sillylife.sillynews.constants.Constants
import com.sillylife.sillynews.database.MapDbEntities
import com.sillylife.sillynews.database.dao.ScheduleDao
import com.sillylife.sillynews.database.entities.ScheduleEntity
import com.sillylife.sillynews.events.RxBus
import com.sillylife.sillynews.events.RxEvent
import com.sillylife.sillynews.models.Schedule
import com.sillylife.sillynews.models.Task
import com.sillylife.sillynews.models.responses.HomeDataResponse
import com.sillylife.sillynews.models.responses.TaskResponse
import com.sillylife.sillynews.services.AppDisposable
import com.sillylife.sillynews.utils.CommonUtil
import com.sillylife.sillynews.utils.FragmentHelper
import com.sillylife.sillynews.views.activity.MainActivity
import com.sillylife.sillynews.views.adapter.NewsAllAdapter
import com.sillylife.sillynews.views.adapter.TaskAllAdapter
import com.sillylife.sillynews.views.adapter.TaskAllAdapter.Companion.SCHEDULES
import com.sillylife.sillynews.views.module.HomeFragmentModule
import com.sillylife.sillynews.views.viewmodal.HomeFragmentViewModel
import com.sillylife.sillynews.views.viewmodelfactory.FragmentViewModelFactory
import kotlinx.android.synthetic.main.fragment_news.*

class TaskFragment : BaseFragment(), HomeFragmentModule.IModuleListener {
    override fun onApiFailure(statusCode: Int, message: String) {

    }

    override fun onHomeApiSuccess(response: HomeDataResponse?) {
        if (response != null) {
            setHomeAdapter(response)
        }
    }

    override fun onTaskUpdateApiSuccess(response: TaskResponse?, position: Int) {
        if (response?.task != null) {
            val adapter = rcvAll?.adapter as TaskAllAdapter
            adapter.notifyItemChanged(position, response.task)
        }
    }

    override fun onSchedulesApiSuccess(response: HomeDataResponse?, position: Int) {
        if (response != null) {
            val adapter = rcvAll.adapter as TaskAllAdapter
            response.dataItems!!.forEach {
                when (SCHEDULES) {
                    it.type -> {
                        adapter.notifyItemChanged(position, it)
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance() = TaskFragment()
    }

    private val TAG = TaskFragment::class.java.simpleName
    var appDisposable: AppDisposable = AppDisposable()
    private var viewModel: HomeFragmentViewModel? = null
    var tempTitle: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_task, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this, FragmentViewModelFactory(this@TaskFragment))
                .get(HomeFragmentViewModel::class.java)

        appDisposable.add(RxBus.listen(RxEvent.NetworkConnectivity::class.java).subscribe { action ->
            Log.d("onNetworkConnection 2", action.isConnected.toString())
            if (action.isConnected) {
                if (rcvAll?.adapter == null) {
                    viewModel?.getHomeData(1)
                }
            } else {
                Toast.makeText(context, "Make sure you have working internet connection", Toast.LENGTH_SHORT).show()
            }
        })

//        view.viewTreeObserver.addOnGlobalLayoutListener {
//            val r = Rect()
//            view.getWindowVisibleDisplayFrame(r)
//            val heightDiff = view.rootView.height - (r.bottom - r.top)
//            if (heightDiff > 500) { // if more than 100 pixels, its probably a keyboard...
//                Log.d(TAG, "onViewCreated Keyboard Shown")
//            } else {
//                if (rcvAll?.adapter != null) {
//                    val adapter = rcvAll.adapter as TaskAllAdapter
//                    if (adapter.getTask() != null && adapter.getTaskAdapterPosition() != null){
//                        val task = adapter.getTask()!!
//                        if (tempTitle!= null && tempTitle.equals(adapter.getTask()?.title!!, true)){
//                            return@addOnGlobalLayoutListener
//                        }
//                        tempTitle = task.title
//                        updateTask(task.id!!, adapter.getTaskAdapterPosition()!!, "", task.title!!, "")
//                    }
//                    Log.d(TAG, "onViewCreated Keyboard Hide" + adapter.getTask()?.title+ "   " + adapter.getTaskAdapterPosition())
//                }
//            }
//        }
    }

    private fun setHomeAdapter(response: HomeDataResponse) {
        if (rcvAll?.adapter == null) {
            val adapter = TaskAllAdapter(context!!, response) { it, position, type ->
                if (it is Int) {
                    if (it > 0) {
                        when (type) {
                            Constants.SCHEDULE_PAGINATE -> viewModel?.getScheduleData(it, position)
                            Constants.TASK_PAGINATE -> viewModel?.getHomeData(it)
                            Constants.ADD_SCHEDULE -> (activity as MainActivity).addFragment(AddScheduleFragment.newInstance(), FragmentHelper.HOME_TO_ADD_SCHEDULE)
                        }
                    }
                } else if (it is Task) {
                    if (type == Constants.TASK_CHECKBOX) {
                        val status = if (it.status.equals(Constants.TASK_COMPLETED)) {
                            Constants.TASK_PENDING
                        } else {
                            Constants.TASK_COMPLETED
                        }
                        viewModel?.updateTask(it.id!!, position, status, "", "")
                    }
                } else if (it is Schedule) {
                    when (type) {
                        Constants.EDIT_SCHEDULE -> Log.d(TAG, "editttt" + it.title)
                        Constants.DELETE_SCHEDULE -> Snackbar.make(rcvAll, "" + it.title + " deleted", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
            if (rcvAll?.itemDecorationCount == 0) {
                rcvAll?.addItemDecoration(ItemDecoration())
            }
            rcvAll?.layoutManager = NewsAllAdapter.WrapContentLinearLayoutManager(context!!)
//            rcvAll?.setItemViewCacheSize(10)
            rcvAll?.clearOnScrollListeners()
            rcvAll?.adapter = adapter
            val itemTouchHelperCallback = adapter.RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
            if (itemTouchHelperCallback != null) {
                ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rcvAll)
            }
            rcvAll?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                        CommonUtil.hideKeyboard(context!!)
                    }
                }
            })
        } else {
            val adapter = rcvAll.adapter as TaskAllAdapter
            adapter.addMoreData(response)
        }
    }

    class ItemDecoration : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view)
            val viewType = parent.adapter!!.getItemViewType(position)
            if (position != RecyclerView.NO_POSITION) {
                if (viewType == TaskAllAdapter.SCHEDULE) {
                    outRect.top = CommonUtil.dpToPx(20)
                    outRect.bottom = CommonUtil.dpToPx(20)
                } else if (viewType == TaskAllAdapter.TASK) {
//                    outRect.top = CommonUtil.dpToPx(5)
                    outRect.left = CommonUtil.dpToPx(16)
                    outRect.right = CommonUtil.dpToPx(16)
//                    outRect.bottom = CommonUtil.dpToPx(5)
                } else if (viewType == TaskAllAdapter.INFO) {
                    outRect.top = CommonUtil.dpToPx(20)
                } else if (viewType == TaskAllAdapter.HEADER) {
                    outRect.right = CommonUtil.dpToPx(20)
                    outRect.left = CommonUtil.dpToPx(20)
                    outRect.bottom = CommonUtil.dpToPx(10)
                    outRect.top = CommonUtil.dpToPx(10)
                } else {
                    if (parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1) {
                        outRect.bottom = CommonUtil.dpToPx(20)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        appDisposable.dispose()
        viewModel?.onDestroy()
    }
}
