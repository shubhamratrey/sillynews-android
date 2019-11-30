package com.sillylife.sillynews.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HomeDataItem(
    var type: String?,
    var id: Int?,
    var title: String?,
    var slug: String?,
    var news: ArrayList<NewsItem>?,
    var category: String?,
    @SerializedName("has_more") var hasMore: Boolean?,
    @SerializedName("insta_feed") var instaFeed: ArrayList<InstaItem>?,
    @SerializedName("content_type") var contentType: String?,
    @SerializedName("user_info") var userInfo: UserProfile?,
    @SerializedName("schedules") var schedules: ArrayList<Schedule>?,
    @SerializedName("tasks") var tasks: ArrayList<Task>
) : Parcelable