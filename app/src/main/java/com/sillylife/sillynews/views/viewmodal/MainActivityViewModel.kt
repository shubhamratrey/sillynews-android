package com.sillylife.sillynews.views.viewmodal

import com.sillylife.sillynews.models.responses.UserResponse
import com.sillylife.sillynews.views.activity.BaseActivity
import com.sillylife.sillynews.views.module.BaseModule
import com.sillylife.sillynews.views.module.MainActivityModule

class MainActivityViewModel(activity: BaseActivity) : BaseViewModel(), MainActivityModule.IModuleListener {

    override fun onGetMeApiSuccess(response: UserResponse) {
        viewListener.onGetMeApiSuccess(response)
    }

    override fun onGetMeApiFailure(statusCode: Int, message: String) {
        viewListener.onGetMeApiFailure(statusCode, message)
    }

    val module = MainActivityModule(this)
    val viewListener = activity as MainActivityModule.IModuleListener
    override fun setViewModel(): BaseModule {
        return module
    }

    fun getMe() {
        module.getMe()
    }
}
