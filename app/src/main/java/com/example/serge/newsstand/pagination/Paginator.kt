package com.example.serge.newsstand.pagination

import io.reactivex.Single
import io.reactivex.disposables.Disposable

class Paginator<T>(
        private val requestFactory: (Int) -> Single<List<T>>,
        private val viewController: ViewController<T>
) {
    private val FIRST_PAGE = 1

    private var currentState: PaginatorState<T> = EmptyState()
    private var currentPage = 0
    private val currentData = mutableListOf<T>()
    private var disposable: Disposable? = null

    fun refresh() = currentState.refresh()
    fun loadNewPage() = currentState.loadNewPage()
    fun release() = currentState.release()

    private inner class EmptyState : PaginatorState<T> {

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

    private inner class EmptyProgressState : PaginatorState<T> {

        override fun newData(data: List<T>) {
            if (data.isNotEmpty()) {
                currentState = DataState()
                currentData.clear()
                currentData.addAll(data)
                currentPage = FIRST_PAGE
                viewController.showData(true, currentData)
                viewController.showEmptyProgress(false)
            } else {
                currentState = EmptyDataState()
                viewController.showEmptyProgress(false)
                viewController.showEmptyView(true)
            }
        }

        override fun fail(error: Throwable) {
            currentState = EmptyErrorState()
            viewController.showEmptyProgress(false)
            viewController.showEmptyError(true, error)
        }

        override fun release() {
            currentState = ReleasedState()
            disposable?.dispose()
        }
    }

    private inner class EmptyErrorState : PaginatorState<T> {

        override fun refresh() {
            currentState = EmptyProgressState()
            viewController.showEmptyError(false)
            viewController.showEmptyProgress(true)
            loadPage(FIRST_PAGE)
        }

        override fun release() {
            currentState = ReleasedState()
            disposable?.dispose()
        }
    }

    private inner class DataState : PaginatorState<T> {

        override fun refresh() {
            currentState = RefreshState()
            viewController.showRefreshProgress(true)
            loadPage(FIRST_PAGE)
        }

        override fun loadNewPage() {
            currentState = PageProgressState()
            viewController.showPageProgress(true)
            loadPage(currentPage + 1)
        }

        override fun release() {
            currentState = ReleasedState()
            disposable?.dispose()
        }
    }

    private inner class EmptyDataState : PaginatorState<T> {

        override fun refresh() {
            currentState = EmptyProgressState()
            viewController.showEmptyView(false)
            viewController.showEmptyProgress(true)
            loadPage(FIRST_PAGE)
        }

        override fun release() {
            currentState = ReleasedState()
            disposable?.dispose()
        }
    }

    private inner class PageProgressState : PaginatorState<T> {

        override fun newData(data: List<T>) {
            if (data.isNotEmpty()) {
                currentState = DataState()
                currentData.addAll(data)
                currentPage++
                viewController.showPageProgress(false)
                viewController.showData(true, currentData)
            } else {
                currentState = AllDataState()
                viewController.showPageProgress(false)
            }
        }

        override fun refresh() {
            currentState = RefreshState()
            viewController.showPageProgress(false)
            viewController.showRefreshProgress(true)
            loadPage(FIRST_PAGE)
        }

        override fun fail(error: Throwable) {
            currentState = DataState()
            viewController.showPageProgress(false)
            viewController.showErrorMessage(error)
        }

        override fun release() {
            currentState = ReleasedState()
            disposable?.dispose()
        }
    }

    private inner class RefreshState : PaginatorState<T> {

        override fun newData(data: List<T>) {
            if (data.isNotEmpty()) {
                currentState = DataState()
                currentData.clear()
                currentData.addAll(data)
                currentPage = FIRST_PAGE
                viewController.showRefreshProgress(false)
                viewController.showData(true, currentData)
            } else {
                currentState = EmptyDataState()
                currentData.clear()
                viewController.showData(false)
                viewController.showRefreshProgress(false)
                viewController.showEmptyView(true)
            }
        }

        override fun fail(error: Throwable) {
            currentState = DataState()
            viewController.showRefreshProgress(false)
            viewController.showErrorMessage(error)
        }

        override fun release() {
            currentState = ReleasedState()
            disposable?.dispose()
        }
    }

    private inner class AllDataState: PaginatorState<T> {

        override fun refresh() {
            currentState = RefreshState()
            viewController.showRefreshProgress(true)
            loadPage(FIRST_PAGE)
        }

        override fun release() {
            currentState = ReleasedState()
            disposable?.dispose()
        }
    }

    private inner class ReleasedState : PaginatorState<T>

    private fun loadPage(page: Int) {
        disposable?.dispose()
        disposable = requestFactory.invoke(page)
                .subscribe(
                        { currentState.newData(it) },
                        { currentState.fail(it) }
                )
    }

    /*private sealed class Event {
        data class LoadFirstPage(): Event()
        data class LoadNewPage(): Event()
        data class Refresh(): Event()
    }

    private data class PaginationState(
            val pageNumber: Int,
            val loadingFullscreen: Boolean,
            val loadingPage: Boolean,
            val refreshing: Boolean,
            val data: List<>,
            val errorFullscreen: Throwable,
            val errorPageLoading: Throwable
    )*/
}