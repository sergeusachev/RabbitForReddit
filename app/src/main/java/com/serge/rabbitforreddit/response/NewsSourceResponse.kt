package com.serge.rabbitforreddit.response

import com.serge.rabbitforreddit.model.NewsSource

data class NewsSourceResponse(
        val status: String,
        val sources: List<NewsSource>
)

