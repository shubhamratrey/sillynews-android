package com.sillylife.sillynews.views.viewmodal

import com.sillylife.sillynews.database.MapDbEntities
import com.sillylife.sillynews.database.entities.ScheduleEntity
import com.sillylife.sillynews.models.Schedule
import com.sillylife.sillynews.models.responses.HomeDataResponse
import com.sillylife.sillynews.models.responses.TaskResponse
import com.sillylife.sillynews.views.adapter.TaskAllAdapter
import com.sillylife.sillynews.views.fragments.BaseFragment
import com.sillylife.sillynews.views.module.BaseModule
import com.sillylife.sillynews.views.module.HomeFragmentModule
import org.jetbrains.anko.doAsync

class HomeFragmentViewModel(fragment: BaseFragment) : BaseViewModel(), HomeFragmentModule.IModuleListener {
    override fun onApiFailure(statusCode: Int, message: String) {
        viewListener.onApiFailure(statusCode, message)
    }

    override fun onHomeApiSuccess(response: HomeDataResponse?) {
        viewListener.onHomeApiSuccess(response)
        storeHomeData(response)
    }

    override fun onTaskUpdateApiSuccess(response: TaskResponse?, position: Int) {
        viewListener.onTaskUpdateApiSuccess(response, position)
    }

    override fun onSchedulesApiSuccess(response: HomeDataResponse?, position: Int) {
        viewListener.onSchedulesApiSuccess(response, position)
        storeHomeData(response)
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

    fun storeHomeData(response: HomeDataResponse?){
        response?.dataItems!!.forEach {
            when (TaskAllAdapter.SCHEDULES) {
                it.type -> {
                    storeScheduleInDb(it.schedules!!)
                }
            }
        }
    }

    private fun storeScheduleInDb(schedule: ArrayList<Schedule>) {
        doAsync {
            var entities: ArrayList<ScheduleEntity> = ArrayList()
            for (i in schedule) {
                var entity = getScheduleEntity(i)
                entities.add(entity)
            }
            getDatabase().scheduleDao().insertAll(*entities.toTypedArray())
        }
    }

    fun getScheduleEntity(i: Schedule): ScheduleEntity {
        var entity = getDatabase().scheduleDao().getContentUnitById(i.id!!)
        if (entity == null) {
            val e = MapDbEntities.scheduleToEntity(i)
            e?.timestamp = System.currentTimeMillis()
            entity = e!!
        } else {
            var dEntity = MapDbEntities.scheduleToEntity(i)
            dEntity?.timestamp = System.currentTimeMillis()
            entity = dEntity!!
        }
        return entity!!
    }

}