package com.sillylife.sillynews.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserProfile(
    var name: String?,
    var quote: String?,
    val n_pending_task: Int?,
    val n_total_task: Int?
) : Parcelable
