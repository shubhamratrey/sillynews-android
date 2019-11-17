package com.sillylife.sillynews.services

import com.sillylife.sillynews.models.responses.HomeDataResponse
import com.sillylife.sillynews.models.responses.NewsDataResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface IAPIService {

    @GET("${NetworkConstants.V1}/news/all")
    fun getNewsData(@QueryMap queryMap: Map<String, String>): Observable<Response<NewsDataResponse>>

    @GET("${NetworkConstants.V1}/home/all")
    fun getHomeData(@QueryMap queryMap: Map<String, String>): Observable<Response<HomeDataResponse>>

}