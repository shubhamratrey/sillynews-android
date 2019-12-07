package com.sillylife.sillynews.services.sharedpreference

import android.text.TextUtils
import com.google.gson.Gson
import com.sillylife.sillynews.models.UserProfile
import com.sillylife.sillynews.utils.CommonUtil


object SharedPreferenceManager {

    val sharedPreferences = SharedPreferences

    private val TAG = SharedPreferenceManager::class.java.simpleName

    private const val FIREBASE_AUTH_TOKEN = "firebase_auth_token"
    private const val FCM_REGISTERED_USER = "fcm_registered_user"
    private const val USER = "user"


    fun storeFirebaseAuthToken(firebaseAuthToken: String) {
        sharedPreferences.setString(FIREBASE_AUTH_TOKEN, firebaseAuthToken)
    }

    fun getFirebaseAuthToken(): String {
        return sharedPreferences.getString(FIREBASE_AUTH_TOKEN, "")!!
    }


    fun isFCMRegisteredOnserver(userId: String?): Boolean {
        return if (userId == null || TextUtils.isEmpty(userId)) {
            false
        } else sharedPreferences.getBoolean(FCM_REGISTERED_USER + userId, false)
    }

    fun setFCMRegisteredOnserver(userId: String) {
        sharedPreferences.setBoolean(FCM_REGISTERED_USER + userId, true)
    }

    fun setUser(user: UserProfile) {
        sharedPreferences.setString(USER, Gson().toJson(user))
    }

    fun getUser(): UserProfile? {
        val raw: String = sharedPreferences.getString(USER, "")!!
        if (!CommonUtil.textIsEmpty(raw)) {
            return Gson().fromJson(raw, UserProfile::class.java)
        }
        return null
    }



}