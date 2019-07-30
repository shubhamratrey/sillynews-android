package com.sillylife.sillynews

import android.app.Application
import android.content.IntentFilter
import android.net.ConnectivityManager
import com.sillylife.sillynews.events.RxBus
import com.sillylife.sillynews.events.RxEvent
import com.sillylife.sillynews.services.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


class SillyNews : Application(), ConnectivityReceiverListener {
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        appDisposable.dispose()
        if (!isConnected) {
            appDisposable.add(Observable.timer(1, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        RxBus.publish(RxEvent.NetworkConnectivity(isConnected))
                    })
        } else {
            RxBus.publish(RxEvent.NetworkConnectivity(isConnected))
        }
    }

    @Volatile
    private var mIAPIService: IAPIService? = null
    @Volatile
    private var mIAPIServiceCache: IAPIService? = null

    private var connectivityReceiver: ConnectivityReceiver? = null

    var appDisposable: AppDisposable = AppDisposable()

    companion object {
        @Volatile
        private var sillyNewsApplication: SillyNews? = null

        @Synchronized
        fun getInstance(): SillyNews {
            return sillyNewsApplication!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        sillyNewsApplication = this@SillyNews

        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        connectivityReceiver = ConnectivityReceiver(this)
        registerReceiver(connectivityReceiver, intentFilter)
    }

    @Synchronized
    fun getAPIService(): IAPIService {
        if (mIAPIService == null) {
            mIAPIService = APIService.build()
        }
        return mIAPIService!!
    }

    @Synchronized
    fun getAPIService(cacheEnabled: Boolean): IAPIService {
        if (mIAPIService == null) {
            mIAPIService = APIService.build()
        }
        return if (cacheEnabled) mIAPIServiceCache!! else mIAPIService!!
    }

}