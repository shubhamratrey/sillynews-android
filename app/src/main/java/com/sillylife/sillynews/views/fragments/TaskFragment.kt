package com.sillylife.sillynews.views.fragments

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sillylife.sillynews.R
import com.sillylife.sillynews.SillyNews
import com.sillylife.sillynews.constants.Constants
import com.sillylife.sillynews.events.RxBus
import com.sillylife.sillynews.events.RxEvent
import com.sillylife.sillynews.models.Schedule
import com.sillylife.sillynews.models.Task
import com.sillylife.sillynews.models.responses.HomeDataResponse
import com.sillylife.sillynews.models.responses.TaskResponse
import com.sillylife.sillynews.services.AppDisposable
import com.sillylife.sillynews.services.CallbackWrapper
import com.sillylife.sillynews.services.NetworkConstants
import com.sillylife.sillynews.utils.CommonUtil
import com.sillylife.sillynews.views.adapter.HomeAllAdapter
import com.sillylife.sillynews.views.adapter.NewsAllAdapter
import com.sillylife.sillynews.views.adapter.TaskAllAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_news.*
import retrofit2.Response
import java.util.*

class TaskFragment : BaseFragment() {

    companion object {
        fun newInstance() = TaskFragment()
    }

    private val TAG = TaskFragment::class.java.simpleName
    var appDisposable: AppDisposable = AppDisposable()
    var tempTitle: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_task, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appDisposable.add(RxBus.listen(RxEvent.NetworkConnectivity::class.java).subscribe { action ->
            Log.d("onNetworkConnection 2", action.isConnected.toString())
            if (action.isConnected) {
                if (rcvAll?.adapter == null) {
                    getHomeData(1)
                }
            } else {
                Toast.makeText(context, "Make sure you have working internet connection", Toast.LENGTH_SHORT).show()
            }
        })

        view.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            view.getWindowVisibleDisplayFrame(r)
            val heightDiff = view.rootView.height - (r.bottom - r.top)
            if (heightDiff > 500) { // if more than 100 pixels, its probably a keyboard...
                Log.d(TAG, "onViewCreated Keyboard Shown")
            } else {
                if (rcvAll?.adapter != null) {
                    val adapter = rcvAll.adapter as TaskAllAdapter
                    if (adapter.getTask() != null && adapter.getTaskAdapterPosition() != null){
                        val task = adapter.getTask()!!
                        if (tempTitle!= null && tempTitle.equals(adapter.getTask()?.title!!, true)){
                            return@addOnGlobalLayoutListener
                        }
                        tempTitle = task.title
                        updateTask(task.id!!, adapter.getTaskAdapterPosition()!!, "", task.title!!, "")
                    }
                    Log.d(TAG, "onViewCreated Keyboard Hide" + adapter.getTask()?.title+ "   " + adapter.getTaskAdapterPosition())
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    fun getHomeData(pageNo: Int) {
        val hashMap = HashMap<String, String>()
        hashMap[NetworkConstants.API_PATH_QUERY_PAGE] = pageNo.toString()
        SillyNews.getInstance().getAPIService()
                .getTaskData(hashMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CallbackWrapper<Response<HomeDataResponse>>() {
                    override fun onSuccess(t: Response<HomeDataResponse>) {
                        if (t.body() != null) {
//                            val s = t.body()?.rssItems!![2].title
                            Log.d("getTaskData", t.body().toString())
                            setHomeAdapter(t.body()!!)
                        }
                    }

                    override fun onFailure(code: Int, message: String) {

                    }
                })
    }


    @SuppressLint("CheckResult")
    fun updateTask(taskId: Int, position: Int, status: String, title:String, scheduleId:String) {
        SillyNews.getInstance().getAPIService()
                .updateTask(taskId, status!!, title!!, scheduleId!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CallbackWrapper<Response<TaskResponse>>() {
                    override fun onSuccess(t: Response<TaskResponse>) {
                        if (t.body() != null) {
//                            val s = t.body()?.rssItems!![2].title
                            Log.d("updateTaskStatus", t.body()!!.task.status.toString())
                            val adapter = rcvAll?.adapter as TaskAllAdapter
                            adapter.notifyItemChanged(position, t.body()?.task)
//                            adapter.notifyItemChanged(position)
                        }
                    }

                    override fun onFailure(code: Int, message: String) {

                    }
                })
    }


    @SuppressLint("CheckResult")
    fun getScheduleData(pageNo: Int, position: Int) {
        val hashMap = HashMap<String, String>()
        hashMap[NetworkConstants.API_PATH_QUERY_PAGE] = pageNo.toString()
        hashMap[NetworkConstants.API_PATH_QUERY_TYPE] = "schedules"
        SillyNews.getInstance().getAPIService()
                .getTaskData(hashMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CallbackWrapper<Response<HomeDataResponse>>() {
                    override fun onSuccess(t: Response<HomeDataResponse>) {
                        if (t.body() != null) {
                            Log.d("getTaskData", t.body().toString())
                            val adapter = rcvAll.adapter as TaskAllAdapter
                            t.body()?.dataItems!!.forEach {
                                if (it.type == TaskAllAdapter.SCHEDULES) {
                                    adapter.notifyItemChanged(position, it)
                                }
                            }
                        }
                    }

                    override fun onFailure(code: Int, message: String) {

                    }
                })
    }


    private fun setHomeAdapter(response: HomeDataResponse) {
        if (rcvAll?.adapter == null) {
            val adapter = TaskAllAdapter(context!!, response) { it, position, type ->
                if (it is Int) {
                    if (it > 0) {
                        if (type == Constants.SCHEDULE_PAGINATE) {
                            getScheduleData(it, position)
                        } else if (type == Constants.TASK_PAGINATE) {
                            getHomeData(it)
                        }
                    } else {
                        if (it == HomeAllAdapter.SCROLLBACK_SHOW_ID) {
//                            scrollBack.visibility = View.VISIBLE
                        } else if (it == HomeAllAdapter.SCROLLBACK_HIDE_ID) {
//                            scrollBack.visibility = View.GONE
                        }
                    }
                } else if (it is Task) {
                    if (type == Constants.TASK_CHECKBOX) {
                        val status = if (it.status.equals(Constants.TASK_COMPLETED)) {
                            Constants.TASK_PENDING
                        } else {
                            Constants.TASK_COMPLETED
                        }
                        updateTask(it.id!!, position, status, "","")
                    }
                } else if (it is Schedule) {
                    if (type == Constants.EDIT_SCHEDULE) {
                        Log.d(TAG, "editttt" + it.title)
                    } else if (type == Constants.DELETE_SCHEDULE) {
                        Snackbar.make(rcvAll, "" + it.title + " deleted", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
            if (rcvAll?.itemDecorationCount == 0) {
                rcvAll?.addItemDecoration(ItemDecoration())
            }
            rcvAll?.layoutManager = NewsAllAdapter.WrapContentLinearLayoutManager(context!!)
            rcvAll?.setItemViewCacheSize(10)
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

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        appDisposable.dispose()
    }
}
