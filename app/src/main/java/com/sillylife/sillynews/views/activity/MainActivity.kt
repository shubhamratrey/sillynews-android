package com.sillylife.sillynews.views.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.sillylife.sillynews.R
import com.sillylife.sillynews.models.responses.UserResponse
import com.sillylife.sillynews.services.FirebaseAuthUserManager
import com.sillylife.sillynews.services.sharedpreference.SharedPreferenceManager
import com.sillylife.sillynews.utils.FragmentHelper
import com.sillylife.sillynews.views.fragments.TaskFragment
import com.sillylife.sillynews.views.module.MainActivityModule
import com.sillylife.sillynews.views.viewmodal.MainActivityViewModel
import com.sillylife.sillynews.views.viewmodelfactory.ActivityViewModelFactory


class MainActivity : BaseActivity(), MainActivityModule.IModuleListener {
    override fun onGetMeApiSuccess(response: UserResponse) {
        if (!isFinishing) {
            FragmentHelper.replace(R.id.container, supportFragmentManager, TaskFragment.newInstance(), FragmentHelper.HOME)
        }
    }

    override fun onGetMeApiFailure(statusCode: Int, message: String) {
    }

    val RC_SIGN_IN = 12132
    private val TAG = MainActivity::class.java.simpleName
    private var viewModel: MainActivityViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this, ActivityViewModelFactory(this)).get(MainActivityViewModel::class.java)
        if (!FirebaseAuthUserManager.isUserLoggedIn()) {
            val providers = arrayListOf(AuthUI.IdpConfig.PhoneBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build())
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(), RC_SIGN_IN)
        } else {
            if (SharedPreferenceManager.getUser() != null) {
                FragmentHelper.replace(R.id.container, supportFragmentManager, TaskFragment.newInstance(), FragmentHelper.HOME)
            } else {
                viewModel?.getMe()
            }
        }
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
                viewModel?.getMe()
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
        viewModel?.onDestroy()
    }

    fun addFragment(fragment: Fragment, tag: String) {
        FragmentHelper.add(R.id.container, supportFragmentManager, fragment, tag)
    }
}
