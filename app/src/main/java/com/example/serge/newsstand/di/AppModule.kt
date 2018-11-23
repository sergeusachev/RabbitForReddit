package com.example.serge.newsstand.di

import android.app.Application
import android.content.Context
import com.example.serge.newsstand.AppConfig
import com.example.serge.newsstand.api.NewsApi
import com.example.serge.newsstand.di.scope.AppScope
import com.example.serge.newsstand.repository.NewsRepository
import com.example.serge.newsstand.utils.RxNetworkChecker
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class AppModule {

    @AppScope
    @Provides
    fun provideContext(app: Application): Context = app.applicationContext

    @AppScope
    @Provides
    fun provideRxNetworkChecker(context: Context): RxNetworkChecker = RxNetworkChecker(context)

    @AppScope
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(AppConfig.BASE_URL)
                .build()
    }

    @AppScope
    @Provides
    fun provideNewsApi(retrofit: Retrofit): NewsApi = retrofit.create(NewsApi::class.java)

    @AppScope
    @Provides
    fun provideRepository(newsApi: NewsApi): NewsRepository = NewsRepository(newsApi)
}