package com.sillylife.sillynews.views.module

import com.sillylife.sillynews.SillyNews
import com.sillylife.sillynews.services.AppDisposable

open class BaseModule {
    val application = SillyNews.getInstance()
    val apiService = application.getAPIService()
    val database = application.getDatabase()
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