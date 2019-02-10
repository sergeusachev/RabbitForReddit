package com.example.serge.newsstand.ui.fragments.newslist.mapper

import com.example.serge.newsstand.mapper.ViewDataListMapper
import com.example.serge.newsstand.model.NewsItem
import com.example.serge.newsstand.ui.fragments.newslist.model.NewsViewData
import com.example.serge.newsstand.ui.fragments.newslist.model.ViewData

class NewsListMapper : ViewDataListMapper<NewsItem, ViewData> {

    override fun map(list: List<NewsItem>): List<ViewData> {
        return list.map {
            NewsViewData(
                    it.source.id,
                    it.source.name,
                    it.author,
                    it.title,
                    it.description,
                    it.url,
                    it.urlToImage,
                    it.publishedAt,
                    it.content
            )
        }
    }
}