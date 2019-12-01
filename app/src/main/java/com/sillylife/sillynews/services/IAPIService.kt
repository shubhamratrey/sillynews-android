package com.sillylife.sillynews.services

import com.sillylife.sillynews.models.responses.HomeDataResponse
import com.sillylife.sillynews.models.responses.NewsDataResponse
import com.sillylife.sillynews.models.responses.TaskResponse
import com.sillylife.sillynews.models.responses.UserResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface IAPIService {

    @GET("${NetworkConstants.V1}/users/me/")
    fun getMe(): Observable<Response<UserResponse>>

    @GET("${NetworkConstants.V1}/news/all")
    fun getNewsData(@QueryMap queryMap: Map<String, String>): Observable<Response<NewsDataResponse>>

    @GET("${NetworkConstants.V1}/home/all")
    fun getHomeData(@QueryMap queryMap: Map<String, String>): Observable<Response<HomeDataResponse>>

    @GET("${NetworkConstants.V1}/home/task")
    fun getTaskData(@QueryMap queryMap: Map<String, String>): Observable<Response<HomeDataResponse>>

    @FormUrlEncoded
    @POST("${NetworkConstants.V1}/home/{task_id}/update-task/")
    fun updateTask(@Path("task_id") taskId: Int,
                         @Field("status") status: String,
                         @Field("title") title: String,
                         @Field("schedule_id") scheduleId: String
    ): Observable<Response<TaskResponse>>

}