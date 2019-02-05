package com.example.serge.newsstand.ui.fragments.newslist.viewmodel

import androidx.lifecycle.ViewModel
import com.example.serge.newsstand.model.NewsItem
import com.example.serge.newsstand.pagination.MviAction
import com.example.serge.newsstand.pagination.MviView
import com.example.serge.newsstand.pagination.Store
import com.example.serge.newsstand.repository.NewsRepository
import com.example.serge.newsstand.ui.fragments.newslist.InputAction
import com.example.serge.newsstand.ui.fragments.newslist.middleware.LoadPageMiddleware
import com.example.serge.newsstand.ui.fragments.newslist.reducer.LoadPageReducer
import com.example.serge.newsstand.ui.fragments.newslist.reducer.PaginationSideEffectProcessor
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

class NewsListViewModel(repository: NewsRepository) : ViewModel() {

    data class UiState(
            val lastLoadedPage: Int = 0,
            val pageForLoad: Int = 1,
            val loading: Boolean = false,
            val error: Throwable? = null,
            val data: List<NewsItem> = listOf()
    )

    private val store: Store = Store(
            LoadPageReducer(),
            listOf(LoadPageMiddleware(repository)),
            UiState(),
            InputAction.LoadMoreAction
    )

    private val storeDisposable = store.wire()
    private var viewDisposable: Disposable? = null

    fun bindView(mviView: MviView) {
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