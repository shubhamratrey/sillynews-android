package com.sillylife.sillynews.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.sillylife.sillynews.R
import com.sillylife.sillynews.SillyNews
import com.sillylife.sillynews.models.responses.UserResponse
import com.sillylife.sillynews.services.CallbackWrapper
import com.sillylife.sillynews.services.FirebaseAuthUserManager
import com.sillylife.sillynews.utils.FragmentHelper
import com.sillylife.sillynews.views.fragments.HomeFragment
import com.sillylife.sillynews.views.fragments.TaskFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    var homeFragment: HomeFragment? = null
    var taskFragment: TaskFragment? = null
    val RC_SIGN_IN = 12132
    private val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!FirebaseAuthUserManager.isUserLoggedIn()) {
            val providers = arrayListOf(
                AuthUI.IdpConfig.PhoneBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build()
            )

            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
                RC_SIGN_IN
            )
        } else {
            getUser()
        }
        homeFragment = HomeFragment.newInstance()
        taskFragment = TaskFragment.newInstance()
        FragmentHelper.replace(R.id.container, supportFragmentManager, taskFragment!!, FragmentHelper.HOME)
    }

    fun getUser(){
        SillyNews.getInstance().appDisposable.add(
            SillyNews.getInstance().getAPIService()
                .getMe()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CallbackWrapper<Response<UserResponse>>() {
                    override fun onSuccess(t: Response<UserResponse>) {
                        if (t.isSuccessful) {
                            Log.d(TAG, t.toString())
                            FragmentHelper.replace(R.id.container, supportFragmentManager, taskFragment!!, FragmentHelper.HOME)
                        }
                    }

                    override fun onFailure(code: Int, message: String) {

                    }
                })
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                Log.d(
                    "onActivityResult",
                    "using FirebaseAuthUserManager ${FirebaseAuthUserManager.getFirebaseAuthToken()}"
                )
                getUser()
                FirebaseAuth.getInstance().currentUser?.getIdToken(true)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("onActivityResult", task.result!!.token!!)
                        }
                    }
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun addFragment(fragment: Fragment, tag: String) {
        FragmentHelper.add(R.id.container, supportFragmentManager, fragment, tag)
    }
}
