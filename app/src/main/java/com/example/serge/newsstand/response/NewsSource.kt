package com.example.serge.newsstand.response

data class NewsSource(
        val id: String,
        val name: String,
        val description: String,
        val url: String,
        val category: String,
        val language: String,
        val country: String
)

data class NewsSourceResponse(
        val status: String,
        val sources: List<NewsSource>
)

