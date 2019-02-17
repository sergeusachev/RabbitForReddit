package com.serge.rabbitforreddit.navigation

import androidx.fragment.app.FragmentManager
import com.serge.rabbitforreddit.ui.fragments.newsdetail.NewsDetailFragment
import com.serge.rabbitforreddit.ui.fragments.newslist.NewsListFragment

class Navigator(
        private val fragmentManager: FragmentManager,
        private val containerResId: Int) {

    fun openNewsListFragment() {
        fragmentManager.beginTransaction()
                .replace(containerResId, NewsListFragment())
                .commit()
    }

    fun openNewsDetailFragment(addToBackStack: Boolean) {
        if (addToBackStack) {
            fragmentManager.beginTransaction()
                    .replace(containerResId, NewsDetailFragment())
                    .addToBackStack(null)
                    .commit()
        } else {
            fragmentManager.beginTransaction()
                    .replace(containerResId, NewsDetailFragment())
                    .commit()
        }
    }

    fun goBack() {
        fragmentManager.popBackStack()
    }
}