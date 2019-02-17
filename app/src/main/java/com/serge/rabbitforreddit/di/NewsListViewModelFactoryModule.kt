package com.serge.rabbitforreddit.di

import androidx.lifecycle.ViewModelProvider
import com.serge.rabbitforreddit.ui.fragments.newslist.viewmodel.NewsListViewModelFactory
import dagger.Binds
import dagger.Module

@Module
abstract class NewsListViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: NewsListViewModelFactory): ViewModelProvider.Factory
}