package com.serge.rabbitforreddit.response

import com.serge.rabbitforreddit.model.NewsItem

data class NewsResponse(
        val status: String,
        val totalResults: Int,
        val articles: List<NewsItem>
)