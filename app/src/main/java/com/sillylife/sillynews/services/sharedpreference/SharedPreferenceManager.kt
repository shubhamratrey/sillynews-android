package com.sillylife.sillynews.services.sharedpreference

import android.text.TextUtils


object SharedPreferenceManager {

    val sharedPreferences = SharedPreferences

    private val TAG = SharedPreferenceManager::class.java.simpleName

    private const val FIREBASE_AUTH_TOKEN = "firebase_auth_token"
    private const val FCM_REGISTERED_USER = "fcm_registered_user"


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


}