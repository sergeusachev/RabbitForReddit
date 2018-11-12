package com.example.serge.newsstand

import com.example.serge.newsstand.response.NewsSourceResponse
import com.example.serge.newsstand.response.TopHeadlinesNewsResponse
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NewsApi {

    @Headers("X-Api-Key: $API_KEY")
    @GET("sources")
    fun newsSourcesObservable(): Observable<NewsSourceResponse>

    /*@GET("everything")
    fun newsEverythingObservable(): Observable<>*/

    @Headers("X-Api-Key: $API_KEY")
    @GET("top-headlines")
    fun newsTopHeadlinesObservable(@Query("country") countryCode: String): Observable<TopHeadlinesNewsResponse>

    companion object {
        private const val BASE_URL = "https://newsapi.org/v2/"
        private const val API_KEY = "9cbe8b5b120b4360ae6544afe2a1100f"

        fun create(): NewsApi {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .build()

            return retrofit.create(NewsApi::class.java)
        }
    }
}