package com.example.serge.newsstand.pagination

import io.reactivex.Single
import io.reactivex.disposables.Disposable

interface LoadingState<T> {
    fun refresh() {}
    fun loadNewPage() {}
    fun release() {}
    fun newData(data: List<T>) {}
    fun fail(error: Throwable) {}
}

interface ViewController<T> {
    fun showEmptyProgress(show: Boolean)
    fun showEmptyError(show: Boolean, error: Throwable? = null)
    fun showEmptyView(show: Boolean)
    fun showData(show: Boolean, data: List<T> = emptyList())
    fun showErrorMessage(error: Throwable)
    fun showRefreshProgress(show: Boolean)
    fun showPageProgress(show: Boolean)
}

class Paginator<T>(
        private val requestFactory: (Int) -> Single<List<T>>,
        private val viewController: ViewController<T>
) {
    private val FIRST_PAGE = 1

    private var currentState: LoadingState<T> = StateEmpty()
    private var currentPage = 0
    private val currentData = mutableListOf<T>()
    private var disposable: Disposable? = null

    fun refresh() = currentState.refresh()
    fun loadNewPage() = currentState.loadNewPage()
    fun release() = currentState.release()

    private inner class StateEmpty : LoadingState<T> {

        override fun refresh() {
            currentState = EmptyProgressState()
            viewController.showEmptyProgress(true)
            loadPage(FIRST_PAGE)
        }

        override fun release() {
            currentState = ReleasedState()
            disposable?.dispose()
        }
    }
}