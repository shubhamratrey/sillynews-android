package com.sillylife.sillynews.views

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sillylife.sillynews.R
import com.sillylife.sillynews.utils.FragmentHelper
import com.sillylife.sillynews.views.fragments.NewsFragment


class MainActivity : AppCompatActivity() {

    var homeFragment: NewsFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        homeFragment = NewsFragment.newInstance()
        FragmentHelper.replace(R.id.container, supportFragmentManager, homeFragment!!, FragmentHelper.HOME)
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
