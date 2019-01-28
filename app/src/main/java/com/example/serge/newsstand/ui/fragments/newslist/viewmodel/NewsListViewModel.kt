package com.example.serge.newsstand.ui.fragments.newslist.viewmodel

import androidx.lifecycle.ViewModel
import com.example.serge.newsstand.model.NewsItem
import com.example.serge.newsstand.pagination.MviAction
import com.example.serge.newsstand.pagination.MviView
import com.example.serge.newsstand.pagination.Store
import com.example.serge.newsstand.repository.NewsRepository
import com.example.serge.newsstand.ui.fragments.newslist.UiAction
import com.example.serge.newsstand.ui.fragments.newslist.middleware.LoadPageMiddleware
import com.example.serge.newsstand.ui.fragments.newslist.reducer.LoadPageReducer
import io.reactivex.disposables.Disposable

class NewsListViewModel(repository: NewsRepository) : ViewModel() {

    data class UiState(
            val lastLoadedPage: Int = 0,
            val pageForLoad: Int = 1,
            val loading: Boolean = false,
            val error: Throwable? = null,
            val data: List<NewsItem> = listOf()
    )

    private val store: Store<MviAction, UiState> = Store(
            LoadPageReducer(),
            listOf(LoadPageMiddleware(repository)),
            UiState(),
            UiAction.LoadMoreAction
    )

    private val storeDisposable = store.wire()
    private var viewDisposable: Disposable? = null

    fun bindView(view: MviView<MviAction>) {
        viewDisposable = store.bindView(view)
    }

    fun unbindView() = viewDisposable?.dispose()

    fun getUiStateObservable() = store.uiStateObservable()

    override fun onCleared() {
        super.onCleared()
        storeDisposable.dispose()
    }
}