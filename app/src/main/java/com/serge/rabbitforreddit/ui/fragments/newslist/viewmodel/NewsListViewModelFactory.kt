package com.serge.rabbitforreddit.ui.fragments.newslist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.serge.rabbitforreddit.repository.NewsRepository
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class NewsListViewModelFactory @Inject constructor(private val repository: NewsRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(NewsListViewModel::class.java)) {
            NewsListViewModel(repository) as T
        } else {
            throw IllegalArgumentException("model class $modelClass not found")
        }
    }

}