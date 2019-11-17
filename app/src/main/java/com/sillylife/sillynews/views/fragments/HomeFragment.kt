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
import com.sillylife.sillynews.models.InstaItem
import com.sillylife.sillynews.models.NewsItem
import com.sillylife.sillynews.models.responses.HomeDataResponse
import com.sillylife.sillynews.services.AppDisposable
import com.sillylife.sillynews.services.CallbackWrapper
import com.sillylife.sillynews.services.NetworkConstants
import com.sillylife.sillynews.utils.CommonUtil
import com.sillylife.sillynews.utils.FragmentHelper
import com.sillylife.sillynews.views.MainActivity
import com.sillylife.sillynews.views.adapter.HomeAllAdapter
import com.sillylife.sillynews.views.adapter.NewsAllAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_news.*
import retrofit2.Response
import java.util.*

class HomeFragment : BaseFragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    var appDisposable: AppDisposable = AppDisposable()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_news, null, false)
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
                .getHomeData(hashMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CallbackWrapper<Response<HomeDataResponse>>() {
                    override fun onSuccess(t: Response<HomeDataResponse>) {
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

    private fun setHomeAdapter(response: HomeDataResponse) {
        swipeRefresh.isRefreshing = false
        if (rcvAll?.adapter == null) {
            val adapter = HomeAllAdapter(context!!, response) { it, position ->
                if (it is Int) {
                    if (it > 0) {
                        getHomeData(it)
                    } else {
                        if (it == HomeAllAdapter.SCROLLBACK_SHOW_ID) {
//                            scrollBack.visibility = View.VISIBLE
                        } else if (it == HomeAllAdapter.SCROLLBACK_HIDE_ID) {
//                            scrollBack.visibility = View.GONE
                        }
                    }
                } else if (it is NewsItem) {
                    (activity as MainActivity).addFragment(WebViewFragment.newInstance(it.link!!), FragmentHelper.HOME_TO_WEBVIEW)
                } else if (it is InstaItem) {
                    (activity as MainActivity).addFragment(WebViewFragment.newInstance(it.profile?.instaProfileLink!!), FragmentHelper.HOME_TO_WEBVIEW)
                }
            }
            if (rcvAll?.itemDecorationCount == 0) {
                rcvAll?.addItemDecoration(NewsAllAdapter.ItemDecoration(CommonUtil.dpToPx(20), CommonUtil.dpToPx(25), CommonUtil.dpToPx(20), CommonUtil.dpToPx(15), CommonUtil.dpToPx(70)))
            }
            rcvAll?.layoutManager = NewsAllAdapter.WrapContentLinearLayoutManager(context!!)
            rcvAll?.setItemViewCacheSize(10)
            rcvAll?.adapter = adapter
        } else {
            val adapter = rcvAll.adapter as HomeAllAdapter
            adapter.addMoreData(response)
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
