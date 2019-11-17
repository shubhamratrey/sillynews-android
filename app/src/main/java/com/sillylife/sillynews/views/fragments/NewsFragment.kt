package com.sillylife.sillynews.views.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.sillylife.sillynews.R
import com.sillylife.sillynews.SillyNews
import com.sillylife.sillynews.events.RxBus
import com.sillylife.sillynews.events.RxEvent
import com.sillylife.sillynews.models.RssDataItem
import com.sillylife.sillynews.models.responses.NewsDataResponse
import com.sillylife.sillynews.services.AppDisposable
import com.sillylife.sillynews.services.CallbackWrapper
import com.sillylife.sillynews.services.NetworkConstants
import com.sillylife.sillynews.utils.CommonUtil
import com.sillylife.sillynews.utils.FragmentHelper
import com.sillylife.sillynews.views.MainActivity
import com.sillylife.sillynews.views.adapter.NewsAllAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.content_scrolling.*
import retrofit2.Response
import java.util.*

class NewsFragment : BaseFragment() {

    companion object {
        fun newInstance() = NewsFragment()
    }


    private val initialPageNo = 1
    private val initialRssPageNo = 1

    var appDisposable: AppDisposable = AppDisposable()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(activity).inflate(R.layout.fragment_home, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefresh.isRefreshing = true
        swipeRefresh.setOnRefreshListener {
            val adapter = rcvAll.adapter as NewsAllAdapter
            adapter.clearData()
            rcvAll?.adapter = null
            getRssData(initialPageNo, initialRssPageNo)
        }

        appDisposable.add(RxBus.listen(RxEvent.NetworkConnectivity::class.java).subscribe { action ->
            Log.d("onNetworkConnection 2", action.isConnected.toString())
            if (action.isConnected) {
                if (rcvAll?.adapter == null) {
                    getRssData(initialPageNo, initialRssPageNo)
                }
            } else {
                Toast.makeText(context, "Make sure you have working internet connection", Toast.LENGTH_SHORT).show()
            }
        })

    }

    @SuppressLint("CheckResult")
    fun getRssData(pageNo: Int, rssPageNo: Int) {
        val hashMap = HashMap<String, String>()
        hashMap[NetworkConstants.API_PATH_QUERY_PAGE] = pageNo.toString()
        hashMap[NetworkConstants.API_PATH_QUERY_RSS_PAGE] = rssPageNo.toString()
        SillyNews.getInstance().getAPIService()
                .getHomeData(hashMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CallbackWrapper<Response<NewsDataResponse>>() {
                    override fun onSuccess(t: Response<NewsDataResponse>) {
                        if (t.body() != null) {
//                            val s = t.body()?.rssItems!![2].title
//                            Log.d("rss", s)
                            setHomeAdapter(t.body()!!)
                        }
                    }

                    override fun onFailure(code: Int, message: String) {

                    }
                })
    }

    private fun setHomeAdapter(newsDataResponse: NewsDataResponse) {
        swipeRefresh.isRefreshing = false
        if (rcvAll?.adapter == null) {
            val adapter = NewsAllAdapter(context!!, newsDataResponse) { it, position, rssPageNo ->
                if (it is Int) {
                    getRssData(it, rssPageNo)
                } else if (it is RssDataItem) {
                    (activity as MainActivity).addFragment(WebViewFragment.newInstance(it.link!!), FragmentHelper.HOME_TO_WEBVIEW)
                }
            }
            if (rcvAll?.itemDecorationCount == 0) {
                rcvAll?.addItemDecoration(NewsAllAdapter.ItemDecoration(CommonUtil.dpToPx(20), CommonUtil.dpToPx(25), CommonUtil.dpToPx(20), CommonUtil.dpToPx(15), CommonUtil.dpToPx(70)))
            }
            rcvAll?.layoutManager = NewsAllAdapter.WrapContentLinearLayoutManager(context!!)
            rcvAll?.setItemViewCacheSize(10)
            rcvAll?.adapter = adapter
        } else {
            val adapter = rcvAll.adapter as NewsAllAdapter
            adapter.addMoreData(newsDataResponse)
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
