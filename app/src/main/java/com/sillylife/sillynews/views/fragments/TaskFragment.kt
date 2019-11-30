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
import androidx.recyclerview.widget.RecyclerView
import com.sillylife.sillynews.R
import com.sillylife.sillynews.SillyNews
import com.sillylife.sillynews.events.RxBus
import com.sillylife.sillynews.events.RxEvent
import com.sillylife.sillynews.models.Task
import com.sillylife.sillynews.models.responses.HomeDataResponse
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
    var mItemTouchHelper: ItemTouchHelper? = null
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
    fun getScheduleData(pageNo: Int) {
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
//                            val s = t.body()?.rssItems!![2].title
                            Log.d("getTaskData", t.body().toString())
//                        setHomeAdapter(t.body()!!)
                            val adapter = rcvAll.adapter as TaskAllAdapter
                            adapter.addMoreScheduleData(t.body()!!)
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
                        if (type == "schedule") {
                            getScheduleData(it)
                        } else {
                            getHomeData(it)
                        }
                    } else {
                        if (it == HomeAllAdapter.SCROLLBACK_SHOW_ID) {
//                            scrollBack.visibility = View.VISIBLE
                        } else if (it == HomeAllAdapter.SCROLLBACK_HIDE_ID) {
//                            scrollBack.visibility = View.GONE
                        }
                    }
                } else if(it is Task){
                    Log.d(TAG, it.title + "  " + it.id)
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
                    outRect.top = CommonUtil.dpToPx(10)
                    outRect.left = CommonUtil.dpToPx(10)
                    outRect.right = CommonUtil.dpToPx(10)
                    outRect.bottom = CommonUtil.dpToPx(10)
                } else if (viewType == TaskAllAdapter.INFO) {
                    outRect.top = CommonUtil.dpToPx(20)
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
