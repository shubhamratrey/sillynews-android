package com.sillylife.sillynews.models.responses

import com.google.gson.annotations.SerializedName
import com.sillylife.sillynews.models.Task
import com.sillylife.sillynews.models.UserProfile

/**
 * Created on 24/09/18.
 */
class TaskResponse(
    @SerializedName("task") val task: Task
)