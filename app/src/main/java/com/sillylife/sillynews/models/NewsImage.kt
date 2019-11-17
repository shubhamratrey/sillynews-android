package com.sillylife.sillynews.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NewsImage(
        @SerializedName("original_image") var original: String?,
        @SerializedName("thumbnail_url") var thumbnail: String?) : Parcelable
