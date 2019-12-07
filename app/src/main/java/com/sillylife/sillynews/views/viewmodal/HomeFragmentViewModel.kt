package com.sillylife.sillynews.views.viewmodal

import com.sillylife.sillynews.models.responses.HomeDataResponse
import com.sillylife.sillynews.models.responses.TaskResponse
import com.sillylife.sillynews.views.fragments.BaseFragment
import com.sillylife.sillynews.views.module.BaseModule
import com.sillylife.sillynews.views.module.HomeFragmentModule

class HomeFragmentViewModel(fragment: BaseFragment) : BaseViewModel(), HomeFragmentModule.IModuleListener {
    override fun onApiFailure(statusCode: Int, message: String) {
        viewListener.onApiFailure(statusCode, message)
    }

    override fun onHomeApiSuccess(response: HomeDataResponse?) {
        viewListener.onHomeApiSuccess(response)
    }

    override fun onTaskUpdateApiSuccess(response: TaskResponse?, position: Int) {
        viewListener.onTaskUpdateApiSuccess(response, position)
    }

    override fun onSchedulesApiSuccess(response: HomeDataResponse?, position: Int) {
        viewListener.onSchedulesApiSuccess(response, position)
    }

    val module = HomeFragmentModule(this)
    val viewListener = fragment as HomeFragmentModule.IModuleListener
    override fun setViewModel(): BaseModule {
        return module
    }

    fun getHomeData(pageNo: Int){
        module.getHomeData(pageNo)
    }

    fun getScheduleData(pageNo: Int, position: Int){
        module.getSchedules(pageNo, position)
    }

    fun updateTask(taskId: Int, position: Int, status: String?, title: String?, scheduleId: String?) {
        module.updateTask(taskId, position, status!!, title!!, scheduleId!!)
    }

}