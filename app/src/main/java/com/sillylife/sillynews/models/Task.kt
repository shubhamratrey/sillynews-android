package com.sillylife.sillynews.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Task(
    var id: Int?,
    var title: String?,
    var status: String?,
    val slug: String?,
    val schedule: Schedule?
) : Parcelable
