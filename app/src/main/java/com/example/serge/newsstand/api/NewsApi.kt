package com.example.serge.newsstand.api

import com.example.serge.newsstand.AppConfig
import com.example.serge.newsstand.response.NewsSourceResponse
import com.example.serge.newsstand.response.NewsResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NewsApi {

    @Headers("X-Api-Key: ${AppConfig.API_KEY}")
    @GET("sources")
    fun newsSourcesObservable(): Observable<NewsSourceResponse>

    @Headers("X-Api-Key: ${AppConfig.API_KEY}")
    @GET("everything")
    fun newsEverythingObservable(
            @Query("q") query: String?,
            @Query("sources") sources: String?,
            @Query("domains") domains: String?,
            @Query("excludeDomains") excludeDomains: String?,
            @Query("from") from: String?,
            @Query("to") to: String?,
            @Query("language") language: String?,
            @Query("sortBy") sortBy: String?,
            @Query("pageSize") pageSize: Int?,
            @Query("page") page: Int?): Observable<NewsResponse>

    @Headers("X-Api-Key: ${AppConfig.API_KEY}")
    @GET("top-headlines")
    fun newsTopHeadlinesObservable(
            @Query("country") countryCode: String?,
            @Query("category") category: String?,
            @Query("sources") sources: String?,
            @Query("q") query: String?,
            @Query("pageSize") pageSize: Int?,
            @Query("page") page: Int?): Observable<NewsResponse>
}