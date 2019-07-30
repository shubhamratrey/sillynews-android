package com.sillylife.sillynews.models.responses

data class BasicResponse<T>(var isError: Boolean = false, var data: T, var message: String? = null)
