package com.sillylife.sillynews.models.responses

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.sillylife.sillynews.models.RssDataItem
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HomeDataResponse(
        @SerializedName("rss_news") var rssItems: ArrayList<RssDataItem>?,
        @SerializedName("has_more") var hasMore: Boolean?) : Parcelable