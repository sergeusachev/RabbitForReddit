package com.example.serge.newsstand.model

data class NewsItem(
        val source: NewsSourceShort,
        val author: String,
        val title: String,
        val description: String,
        val url: String,
        val urlToImage: String,
        val publishedAt: String,
        val content: String
)