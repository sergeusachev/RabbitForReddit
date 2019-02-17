package com.serge.rabbitforreddit.di

import com.serge.rabbitforreddit.ui.fragments.newsdetail.NewsDetailFragment
import com.serge.rabbitforreddit.ui.fragments.newslist.NewsListFragment
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