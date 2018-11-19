package com.example.serge.newsstand.di

import androidx.fragment.app.FragmentManager
import com.example.serge.newsstand.R
import com.example.serge.newsstand.di.scope.MainActivityScope
import com.example.serge.newsstand.navigation.Navigator
import com.example.serge.newsstand.ui.MainActivity
import dagger.Module
import dagger.Provides

@Module
class MainActivityModule {

    @MainActivityScope
    @Provides
    fun provideFragmentManager(mainActivity: MainActivity): FragmentManager =
            mainActivity.supportFragmentManager

    @MainActivityScope
    @Provides
    fun provideContainerResId(): Int = R.id.root_container

    @MainActivityScope
    @Provides
    fun provideNavigator(fragmentManager: FragmentManager, containerResId: Int): Navigator
            = Navigator(fragmentManager, containerResId)

}