package com.sillylife.sillynews.models.responses

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.sillylife.sillynews.models.HomeDataItem

import kotlinx.android.parcel.Parcelize

@Parcelize
data class HomeDataResponse(
    @SerializedName("items") var dataItems: ArrayList<HomeDataItem>?,
    @SerializedName("has_more") var hasMore: Boolean?
) : Parcelable