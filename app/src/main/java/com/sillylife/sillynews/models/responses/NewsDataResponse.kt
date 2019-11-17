package com.sillylife.sillynews.models.responses

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.sillylife.sillynews.models.NewsItem
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NewsDataResponse(
    @SerializedName("data") var rssItems: ArrayList<NewsItem>?,
    @SerializedName("has_more") var hasMore: Boolean?,
    @SerializedName("has_more_rss") var hasMoreRss: Boolean?
) : Parcelable