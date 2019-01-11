package com.example.serge.newsstand.pagination

interface PaginatorState<T> {
    fun refresh() {}
    fun loadNewPage() {}
    fun release() {}
    fun newData(data: List<T>) {}
    fun fail(error: Throwable) {}
}