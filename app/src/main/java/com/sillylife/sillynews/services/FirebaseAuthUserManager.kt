package com.sillylife.sillynews.services

import android.annotation.SuppressLint
import android.os.Handler
import android.os.HandlerThread
import android.text.TextUtils
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.sillylife.sillynews.SillyNews
import com.sillylife.sillynews.services.sharedpreference.SharedPreferenceManager

object FirebaseAuthUserManager {

    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var mIdTokenListener: FirebaseAuth.IdTokenListener? = null
    private var firebaseUser: FirebaseUser? = null
    private var appDisposable: AppDisposable? = null

    private var firebaseTokenRefreshHandlerThread: HandlerThread? = null
    private var firebaseTokenRefreshHandler: Handler? = null
    private val firebaseTokenRefreshRunnable = Runnable {
        retrieveIdToken(true)
    }

    init {
        try {
            FirebaseApp.getInstance()
        } catch (e: Exception) {
            FirebaseApp.initializeApp(SillyNews.getInstance())
        }
        firebaseTokenRefreshHandlerThread = HandlerThread("AuthTokenRefreshHandlerThread")
        firebaseTokenRefreshHandlerThread!!.start()
        firebaseTokenRefreshHandler = Handler(firebaseTokenRefreshHandlerThread!!.looper)
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                firebaseUser!!.getIdToken(false).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        SharedPreferenceManager.storeFirebaseAuthToken(task.result!!.token!!)
                        registerFCMToken()
                    }
                }
            } else {
                unregisterFCMToken()
                SharedPreferenceManager.storeFirebaseAuthToken("")
            }
        }
        firebaseUser = FirebaseAuth.getInstance().currentUser
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener!!)
        startListeningForIdTokenChanges()
        retrieveIdToken(true)
    }

    fun retrieveIdToken(refresh: Boolean) {
        FirebaseAuth.getInstance().currentUser?.getIdToken(refresh)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val timeDiff = task.result!!.expirationTimestamp * 1000 - System.currentTimeMillis()
                if (timeDiff <= 0) {
                    retrieveIdToken(true)
                } else {
                    firebaseTokenRefreshHandler?.postDelayed(firebaseTokenRefreshRunnable, timeDiff)
                }
                SharedPreferenceManager.storeFirebaseAuthToken(task.result!!.token!!)
            }
        }
    }

    fun retrieveIdToken(refresh: Boolean, listener: TokenRetrieveListener) {
        FirebaseAuth.getInstance().currentUser?.getIdToken(refresh)?.addOnCompleteListener { task ->

            if (task.isSuccessful) {
                listener.onTokenRetrieved(success = true, token = task.result!!.token!!)
            } else {
                listener.onTokenRetrieved(success = false, token = "")
            }
        }
    }

    private fun startListeningForIdTokenChanges() {
        mIdTokenListener = FirebaseAuth.IdTokenListener { firebaseAuth ->
            firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                retrieveIdToken(false)
            }
        }
        FirebaseAuth.getInstance().addIdTokenListener(mIdTokenListener!!)
    }

    fun getFirebaseUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    fun getFirebaseUserId(): String? {
        return getFirebaseUser()?.uid
    }

    fun getFirebaseAuthToken(): String? {
        return SharedPreferenceManager.getFirebaseAuthToken()
    }

    fun isUserLoggedIn(): Boolean {
        return !(FirebaseAuth.getInstance().currentUser == null || FirebaseAuth.getInstance().currentUser!!.isAnonymous)
    }

    fun isAnonymousLoggedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null && FirebaseAuth.getInstance().currentUser!!.isAnonymous
    }

    @SuppressLint("CheckResult")
    fun registerFCMToken() {
        val userId = getFirebaseUserId()
        if (userId == null || TextUtils.isEmpty(userId)) {
            return
        }
        if (SharedPreferenceManager.isFCMRegisteredOnserver(userId)) {
            return
        }


    }

    @SuppressLint("CheckResult")
    private fun unregisterFCMToken() {

    }

    fun finalize() {
        try {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener!!)
            FirebaseAuth.getInstance().removeIdTokenListener(mIdTokenListener!!)
        } catch (e: Exception) {

        }
        firebaseTokenRefreshHandler?.removeCallbacks(firebaseTokenRefreshRunnable)
        firebaseTokenRefreshHandlerThread?.quit()
    }

    private fun getAppDisposable(): AppDisposable {
        if (appDisposable == null) {
            appDisposable = AppDisposable()
        }
        return appDisposable as AppDisposable
    }

    interface TokenRetrieveListener {
        fun onTokenRetrieved(success: Boolean, token: String)
    }
}