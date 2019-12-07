package com.sillylife.sillynews.views.module

import com.sillylife.sillynews.models.responses.HomeDataResponse
import com.sillylife.sillynews.models.responses.TaskResponse
import com.sillylife.sillynews.services.CallbackWrapper
import com.sillylife.sillynews.services.NetworkConstants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class HomeFragmentModule(val iModuleListener: IModuleListener) : BaseModule() {

    fun getHomeData(pageNo: Int) {
        val hashMap = HashMap<String, String>()
        hashMap[NetworkConstants.API_PATH_QUERY_PAGE] = pageNo.toString()
        appDisposable.add(apiService
                .getTaskData(hashMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeWith(object : CallbackWrapper<Response<HomeDataResponse>>() {
                    override fun onSuccess(t: Response<HomeDataResponse>) {
                        if (t.isSuccessful) {
                            iModuleListener.onHomeApiSuccess(t.body()!!)
                        } else {
                            iModuleListener.onApiFailure(t.code(), "empty body")
                        }
                    }

                    override fun onFailure(code: Int, message: String) {
                        iModuleListener.onApiFailure(code, message)
                    }
                }))
    }

    fun updateTask(taskId: Int, position: Int, status: String?, title: String?, scheduleId: String?) {
        appDisposable.add(apiService
                .updateTask(taskId, status!!, title!!, scheduleId!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeWith(object : CallbackWrapper<Response<TaskResponse>>() {
                    override fun onSuccess(t: Response<TaskResponse>) {
                        if (t.isSuccessful) {
                            iModuleListener.onTaskUpdateApiSuccess(t.body()!!, position)
                        } else {
                            iModuleListener.onApiFailure(t.code(), "empty body")
                        }
                    }

                    override fun onFailure(code: Int, message: String) {
                        iModuleListener.onApiFailure(code, message)
                    }
                }))
    }

    fun getSchedules(pageNo: Int, position: Int) {
        val hashMap = HashMap<String, String>()
        hashMap[NetworkConstants.API_PATH_QUERY_PAGE] = pageNo.toString()
        hashMap[NetworkConstants.API_PATH_QUERY_TYPE] = "schedules"
        appDisposable.add(apiService
                .getTaskData(hashMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeWith(object : CallbackWrapper<Response<HomeDataResponse>>() {
                    override fun onSuccess(t: Response<HomeDataResponse>) {
                        if (t.isSuccessful) {
                            iModuleListener.onSchedulesApiSuccess(t.body()!!, position)
                        } else {
                            iModuleListener.onApiFailure(t.code(), "empty body")
                        }
                    }

                    override fun onFailure(code: Int, message: String) {
                        iModuleListener.onApiFailure(code, message)
                    }
                }))
    }


    interface IModuleListener {
        fun onHomeApiSuccess(response: HomeDataResponse?)
        fun onTaskUpdateApiSuccess(response: TaskResponse?, position: Int)
        fun onSchedulesApiSuccess(response: HomeDataResponse?, position: Int)
        fun onApiFailure(statusCode: Int, message: String)
    }
}