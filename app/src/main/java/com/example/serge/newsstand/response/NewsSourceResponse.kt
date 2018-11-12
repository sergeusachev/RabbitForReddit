package com.example.serge.newsstand.response

import com.example.serge.newsstand.model.NewsSource

data class NewsSourceResponse(
        val status: String,
        val sources: List<NewsSource>
)

