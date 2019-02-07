package com.example.serge.newsstand.ui.fragments.newslist.model

enum class ViewType(val typeCode: Int) {
    TYPE_DATA(0),
    TYPE_LOADING(1),
    TYPE_EMPTY(2)
}

data class NewsViewData(
        val sourceId: String?,
        val sourceName: String?,
        val author: String?,
        val title: String?,
        val description: String?,
        val url: String?,
        val urlToImage: String?,
        val publishedAt: String?,
        val content: String?
) : ViewData {
    override fun getId() = url.hashCode()
    override fun getType() = ViewType.TYPE_DATA.typeCode
}

object LoadingViewData : ViewData {
    override fun getId() = -200
    override fun getType() = ViewType.TYPE_LOADING.typeCode
}

object EmptyViewData : ViewData {
    override fun getId() = -100
    override fun getType() = ViewType.TYPE_EMPTY.typeCode
}

interface ViewData {
    fun getId(): Int
    fun getType(): Int
}