package com.example.serge.newsstand.di

import androidx.lifecycle.ViewModelProvider
import com.example.serge.newsstand.ui.fragments.newslist.viewmodel.NewsListViewModelFactory
import dagger.Binds
import dagger.Module

@Module
abstract class NewsListViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: NewsListViewModelFactory): ViewModelProvider.Factory
}