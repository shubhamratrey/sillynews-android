package com.sillylife.sillynews.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserProfile(
        var name: String?,
        var quote: String?,
        val n_pending_task: Int?,
        val n_total_task: Int?,
        val username: String?,
        @SerializedName("profile_pic_url") var instaProfilePhoto: String?,
        @SerializedName("profile_pic_url_hd") var instaProfilePhotoHd: String?,
        @SerializedName("profile_link") var instaProfileLink: String?,
        @SerializedName("id") var id: Int?,
        @SerializedName("sign_up_source") var signUpSource: String?,
        @SerializedName("original_avatar") var originalAvatar: String?
) : Parcelable
