package com.sillylife.sillynews.views.module

import com.sillylife.sillynews.SillyNews
import com.sillylife.sillynews.services.AppDisposable

open class BaseModule {
    val mKukuFMApplication = SillyNews.getInstance()
    val apiService = mKukuFMApplication.getAPIService()
    var appDisposable = AppDisposable()

    fun onDestroy() {
        if (appDisposable != null) {
            appDisposable.dispose()
        }
    }

    fun getDisposable(): AppDisposable {
        if (appDisposable == null) {
            appDisposable = AppDisposable()
        }
        return appDisposable
    }


}