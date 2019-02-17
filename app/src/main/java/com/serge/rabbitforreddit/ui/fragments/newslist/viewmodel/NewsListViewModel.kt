package com.serge.rabbitforreddit.ui.fragments.newslist.viewmodel

import androidx.lifecycle.ViewModel
import com.serge.rabbitforreddit.pagination.Store
import com.serge.rabbitforreddit.repository.NewsRepository
import com.serge.rabbitforreddit.ui.fragments.newslist.mapper.NewsListMapper
import com.serge.rabbitforreddit.ui.fragments.newslist.middleware.LoadPageMiddleware
import com.serge.rabbitforreddit.ui.fragments.newslist.reducer.LoadPageReducer
import io.reactivex.disposables.Disposable

class NewsListViewModel(repository: NewsRepository) : ViewModel() {

    private val store: Store = Store(
            LoadPageReducer(NewsListMapper()),
            listOf(LoadPageMiddleware(repository))
    )

    private val storeDisposable = store.wire()
    private var viewDisposable: Disposable? = null

    fun bindView(mviView: Store.MviView) {
        viewDisposable = store.bindView(mviView)
    }

    fun unbindView() {
        viewDisposable?.dispose()
    }

    override fun onCleared() {
        super.onCleared()
        storeDisposable.dispose()
    }
}