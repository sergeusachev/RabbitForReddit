package com.example.serge.newsstand.response

import com.example.serge.newsstand.model.NewsItem

data class NewsResponse(
        val status: String,
        val totalResults: Int,
        val articles: List<NewsItem>
)