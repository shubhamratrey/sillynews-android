package com.sillylife.sillynews.views.module

import com.sillylife.sillynews.events.IBaseView
import com.sillylife.sillynews.models.responses.UserResponse
import com.sillylife.sillynews.services.CallbackWrapper
import com.sillylife.sillynews.services.sharedpreference.SharedPreferenceManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class MainActivityModule(val iModuleListener: IModuleListener) : BaseModule() {


    fun getMe() {
        appDisposable.add(apiService
                .getMe()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CallbackWrapper<Response<UserResponse>>() {
                    override fun onSuccess(t: Response<UserResponse>) {
                        if (t.isSuccessful) {
                            SharedPreferenceManager.setUser(t.body()?.user!!)
                            iModuleListener.onGetMeApiSuccess(t.body()!!)
                        } else {
                            iModuleListener.onGetMeApiFailure(t.code(), "empty body")
                        }
                    }

                    override fun onFailure(code: Int, message: String) {
                        iModuleListener.onGetMeApiFailure(code, message)
                    }
                }))
    }

    interface IModuleListener : IBaseView {
        fun onGetMeApiSuccess(response: UserResponse)
        fun onGetMeApiFailure(statusCode: Int, message: String)
    }

}