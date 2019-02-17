package com.serge.rabbitforreddit.mapper

interface ViewDataListMapper<R, T> {
    fun map(list: List<R>): List<T>
}