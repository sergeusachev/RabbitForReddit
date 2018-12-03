package com.example.serge.newsstand.ui.fragments.newslist

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.serge.newsstand.repository.NewsRepository
import com.example.serge.newsstand.response.NewsResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observables.ConnectableObservable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.math.BigInteger
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private val DEBUG_TAG = NewsListViewModel::class.java.simpleName

class NewsListViewModel(private val repository: NewsRepository): ViewModel() {

    val observableTopHeadlines: ConnectableObservable<NewsResponse>

    val myObs: Observable<Long>

    private val updateEventSubject = PublishSubject.create<Unit>()
    private val requestConfig = RequestConfig()

    init {
        Log.d(RESPONSE_DEBUG_TAG, "ViewModel Init block")

       myObs = Observable.timer(5, TimeUnit.SECONDS)

        observableTopHeadlines = updateEventSubject
                //.doOnNext { Log.d(RESPONSE_DEBUG_TAG, "Subject.onNext thread is ${Thread.currentThread().name}") }
                .startWith(Unit)
                .doOnNext { Log.d(RESPONSE_DEBUG_TAG, "Update list event!") }
                .doOnNext { requestConfig.page++ }
                .switchMap {
                    repository.getTopHeadlinesNews(
                            requestConfig.country,
                            requestConfig.category,
                            requestConfig.sources,
                            requestConfig.query,
                            requestConfig.pageSize,
                            requestConfig.page)
                            //.doOnNext { Log.d(RESPONSE_DEBUG_TAG, "Call API thread is ${Thread.currentThread().name}") }
                }
                //.doOnNext { Log.d(RESPONSE_DEBUG_TAG, "After switchMap thread is ${Thread.currentThread().name}") }
                .observeOn(AndroidSchedulers.mainThread())
                .replay()
                //.doOnNext { Log.d(RESPONSE_DEBUG_TAG, "After observeOn thread is ${Thread.currentThread().name}") }

        observableTopHeadlines.connect()
    }

    fun sendEvent() {
        updateEventSubject.onNext(Unit)
    }

    enum class CategoriesEnum(val categoryName: String) {
        BUSINESS("business"),
        ENTERTAINMENT("entertainment"),
        GENERAL("general"),
        HEALTH("health"),
        SCIENCE("science"),
        SPORTS("sports"),
        TECHNOLOGY("technology");
    }

    enum class CountryEnum(val countryCode: String) {
        RU("ru");
    }

    data class RequestConfig(
            val country: String? = CountryEnum.RU.countryCode,
            val category: String? = CategoriesEnum.GENERAL.categoryName,
            val sources: String? = null,
            val query: String? = null,
            val pageSize: Int? = null,
            var page: Int = 0
    )
}