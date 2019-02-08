package com.example.serge.newsstand.ui.fragments.newslist.viewmodel

import androidx.lifecycle.ViewModel
import com.example.serge.newsstand.pagination.Store
import com.example.serge.newsstand.repository.NewsRepository
import com.example.serge.newsstand.ui.fragments.newslist.middleware.LoadPageMiddleware
import com.example.serge.newsstand.ui.fragments.newslist.model.ViewData
import com.example.serge.newsstand.ui.fragments.newslist.reducer.LoadPageReducer
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

class NewsListViewModel(repository: NewsRepository) : ViewModel() {

    private val store: Store = Store(
            LoadPageReducer(),
            listOf(LoadPageMiddleware(repository)),
            Store.UiState(),
            Store.InputAction.RefreshDataAction
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