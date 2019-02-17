package com.serge.rabbitforreddit.ui.fragments.newslist.mapper

import com.serge.rabbitforreddit.mapper.ViewDataListMapper
import com.serge.rabbitforreddit.model.NewsItem
import com.serge.rabbitforreddit.ui.fragments.newslist.model.NewsViewData
import com.serge.rabbitforreddit.ui.fragments.newslist.model.ViewData

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