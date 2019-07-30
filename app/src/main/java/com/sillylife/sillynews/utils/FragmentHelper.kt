package com.sillylife.sillynews.utils

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * Created on 26/09/18.
 */
object FragmentHelper {

    const val HOME = "home"
    const val HOME_TO_WEBVIEW= "home_to_webview"

    fun replace(@IdRes containerId: Int, fragmentManager: FragmentManager, fragment: Fragment, tag: String) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(containerId, fragment, tag)
        fragmentTransaction.commitAllowingStateLoss()
    }

    fun add(@IdRes containerId: Int, fragmentManager: FragmentManager, fragment: Fragment, tag: String) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(containerId, fragment, tag)
        fragmentTransaction.addToBackStack(tag)

        val displayedFragment = fragmentManager.findFragmentById(containerId)

        if (displayedFragment != null) {
            fragmentTransaction.hide(displayedFragment)
        }

        fragmentTransaction.commitAllowingStateLoss()
    }

    fun add(@IdRes containerId: Int, fragmentManager: FragmentManager, fragment: Fragment, tag: String,
            enterAnim: Int, exitAnim: Int) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(containerId, fragment, tag)
        fragmentTransaction.setCustomAnimations(enterAnim, exitAnim, enterAnim, exitAnim)
        fragmentTransaction.addToBackStack(tag)

        val displayedFragment = fragmentManager.findFragmentById(containerId)

        if (displayedFragment != null) {
            fragmentTransaction.hide(displayedFragment)
        }

        fragmentTransaction.commitAllowingStateLoss()
    }

}