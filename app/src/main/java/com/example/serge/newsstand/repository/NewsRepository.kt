package com.example.serge.newsstand.repository

import android.util.Log
import com.example.serge.newsstand.api.NewsApi
import com.example.serge.newsstand.response.NewsResponse
import com.example.serge.newsstand.ui.fragments.newslist.RESPONSE_DEBUG_TAG
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class NewsRepository @Inject constructor(private val newsApi: NewsApi): INewsRepository {

    override fun getTopHeadlinesNews(country: String?, category: String?, sources: String?,
                                     query: String?, pageSize: Int?, page: Int?): Observable<NewsResponse> {
        return newsApi.newsTopHeadlinesObservable(country, category, sources, query, pageSize, page)
                .doOnNext { Log.d(RESPONSE_DEBUG_TAG, "REQUEST Total results: ${it.totalResults}") }
                .subscribeOn(Schedulers.io())
    }

}

interface INewsRepository {

    fun getTopHeadlinesNews(country: String? = null, category: String? = null, sources: String? = null,
                            query: String? = null, pageSize: Int? = null, page: Int? = null): Observable<NewsResponse>
}