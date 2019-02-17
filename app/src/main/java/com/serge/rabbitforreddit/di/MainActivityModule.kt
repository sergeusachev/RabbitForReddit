package com.serge.rabbitforreddit.di

import androidx.fragment.app.FragmentManager
import com.example.serge.rabbitforreddit.R
import com.serge.rabbitforreddit.di.scope.MainActivityScope
import com.serge.rabbitforreddit.navigation.Navigator
import com.serge.rabbitforreddit.ui.MainActivity
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