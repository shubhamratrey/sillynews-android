package com.sillylife.sillynews.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InstaItem(
    var thumbnail_src: String?,
    var display_url: String?,
    var caption: String?,
    val n_likes: Int?,
    val n_comments: Int?,
    val location: String?,
    var profile: UserProfile?
) : Parcelable
