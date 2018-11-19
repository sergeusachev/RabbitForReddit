package com.example.serge.newsstand.di

import com.example.serge.newsstand.di.scope.MainActivityScope
import com.example.serge.newsstand.ui.MainActivity
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