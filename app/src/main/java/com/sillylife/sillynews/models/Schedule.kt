package com.sillylife.sillynews.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Schedule(
    var id: Int?,
    var title: String?,
    var start_time: String?,
    var end_time: String?,
    var status: String?,
    val icon_url: String?,
    val slug: String?,
    val day: String?
) : Parcelable
