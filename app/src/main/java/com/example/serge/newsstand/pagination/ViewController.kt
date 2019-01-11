package com.example.serge.newsstand.pagination

interface ViewController<T> {
    fun showEmptyProgress(show: Boolean)
    fun showEmptyError(show: Boolean, error: Throwable? = null)
    fun showEmptyView(show: Boolean)
    fun showData(show: Boolean, data: List<T> = emptyList())
    fun showErrorMessage(error: Throwable)
    fun showRefreshProgress(show: Boolean)
    fun showPageProgress(show: Boolean)
}