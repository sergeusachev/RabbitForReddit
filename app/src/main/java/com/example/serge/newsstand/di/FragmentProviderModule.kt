package com.example.serge.newsstand.di

import com.example.serge.newsstand.ui.fragments.NewsDetailFragment
import com.example.serge.newsstand.ui.fragments.newslist.NewsListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentProviderModule {

    @ContributesAndroidInjector(modules = [
        NewsListFragmentModule::class,
        NewsListViewModelFactoryModule::class])
    abstract fun contributeNewsListFragment(): NewsListFragment

    @ContributesAndroidInjector
    abstract fun contributeNewsDetailFragment(): NewsDetailFragment
}