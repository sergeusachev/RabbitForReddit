package com.serge.rabbitforreddit.di

import com.serge.rabbitforreddit.di.scope.MainActivityScope
import com.serge.rabbitforreddit.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityProviderModule {

    @MainActivityScope
    @ContributesAndroidInjector(modules = [
        MainActivityModule::class,
        FragmentProviderModule::class])
    abstract fun contributeMainActivity(): MainActivity
}