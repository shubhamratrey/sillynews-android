package com.sillylife.sillynews.models.responses

import com.google.gson.annotations.SerializedName
import com.sillylife.sillynews.models.UserProfile

/**
 * Created on 24/09/18.
 */
class UserResponse(
    @SerializedName("user") val user: UserProfile?,
    @SerializedName("is_self") val isSelf: Boolean?
)