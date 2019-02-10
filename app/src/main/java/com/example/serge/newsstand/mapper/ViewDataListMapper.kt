package com.example.serge.newsstand.mapper

interface ViewDataListMapper<R, T> {
    fun map(list: List<R>): List<T>
}