package com.sillylife.sillynews.views.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.sillylife.sillynews.R
import com.sillylife.sillynews.SillyNews
import com.sillylife.sillynews.events.RxBus
import com.sillylife.sillynews.events.RxEvent
import com.sillylife.sillynews.models.RssDataItem
import com.sillylife.sillynews.models.responses.HomeDataResponse
import com.sillylife.sillynews.services.AppDisposable
import com.sillylife.sillynews.services.CallbackWrapper
import com.sillylife.sillynews.services.NetworkConstants
import com.sillylife.sillynews.utils.CommonUtil
import com.sillylife.sillynews.utils.FragmentHelper
import com.sillylife.sillynews.views.MainActivity
import com.sillylife.sillynews.views.adapter.HomeAllAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.content_scrolling.*
import retrofit2.Response
import java.util.*

class HomeFragment : BaseFragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    var appDisposable: AppDisposable = AppDisposable()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(activity).inflate(R.layout.fragment_home, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        context.setSupportActionBar(toolbar)
//        toolbar.title = "SillyNews"
        swipeRefresh.isRefreshing = true
        swipeRefresh.setOnRefreshListener {
            val homeAllViewPagerAdapter = rcvAll.adapter as HomeAllAdapter
            homeAllViewPagerAdapter.clearData()
            rcvAll?.adapter = null
            getRssData(0)
        }

        appDisposable.add(RxBus.listen(RxEvent.NetworkConnectivity::class.java).subscribe { action ->
            Log.d("onNetworkConnection 2", action.isConnected.toString())
            if (action.isConnected) {
                if (rcvAll?.adapter == null) {
                    getRssData(0)
                }
            } else {
                Toast.makeText(context, "Make sure you have working internet connection", Toast.LENGTH_SHORT).show()
            }
        })

    }

    @SuppressLint("CheckResult")
    fun getRssData(pageNo: Int) {
        val hashMap = HashMap<String, String>()
        hashMap[NetworkConstants.API_PATH_QUERY_PAGE] = pageNo.toString()
        SillyNews.getInstance().getAPIService()
                .getHomeData(hashMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CallbackWrapper<Response<HomeDataResponse>>() {
                    override fun onSuccess(t: Response<HomeDataResponse>) {
                        if (t.body() != null) {
                            val s = t.body()?.rssItems!![2].title
                            Log.d("rss", s)
                            setHomeAdapter(t.body()!!)
                        }
                    }

                    override fun onFailure(code: Int, message: String) {

                    }
                })
    }

    private fun setHomeAdapter(homeDataResponse: HomeDataResponse) {
        swipeRefresh.isRefreshing = false
        if (rcvAll?.adapter == null) {
            val homeAllViewPagerAdapter = HomeAllAdapter(context!!, homeDataResponse) { it, position ->
                if (it is Int) {
                    if (it > 0) {
                        getRssData(it)
                    }
                } else if (it is RssDataItem) {
                    (activity as MainActivity).addFragment(WebViewFragment.newInstance(it.link!!), FragmentHelper.HOME_TO_WEBVIEW)
                }
            }
            if (rcvAll?.itemDecorationCount == 0) {
                rcvAll?.addItemDecoration(HomeAllAdapter.ItemDecoration(CommonUtil.dpToPx(20), CommonUtil.dpToPx(25), CommonUtil.dpToPx(20), CommonUtil.dpToPx(15), CommonUtil.dpToPx(70)))
            }
            rcvAll?.layoutManager = LinearLayoutManager(context)
            rcvAll?.setItemViewCacheSize(10)
            rcvAll?.adapter = homeAllViewPagerAdapter
        } else {
            val homeAllViewPagerAdapter = rcvAll.adapter as HomeAllAdapter
            homeAllViewPagerAdapter.addMoreData(homeDataResponse)
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
