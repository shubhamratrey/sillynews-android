package com.sillylife.sillynews.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Schedule(
    var id: Int?,
    var title: String?,
    var start_time: String?,
    var end_time: String?,
    val icon_url: Int?,
    val slug: String?,
    val day: String?
) : Parcelable
