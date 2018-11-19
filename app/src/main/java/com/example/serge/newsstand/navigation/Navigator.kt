package com.example.serge.newsstand.navigation

import androidx.fragment.app.FragmentManager
import com.example.serge.newsstand.ui.fragments.NewsDetailFragment
import com.example.serge.newsstand.ui.fragments.newslist.NewsListFragment

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