package com.example.serge.newsstand.ui.fragments.newslist

import androidx.lifecycle.ViewModel
import com.example.serge.newsstand.model.NewsItem
import com.example.serge.newsstand.pagination.*
import com.example.serge.newsstand.repository.NewsRepository
import io.reactivex.*
import io.reactivex.subjects.PublishSubject

private val DEBUG_TAG = NewsListViewModel::class.java.simpleName

class NewsListViewModel(repository: NewsRepository): ViewModel() {

    private val externalEventsSubject = PublishSubject.create<Event>()
    private val paginator: RxPaginator = RxPaginator(repository)
    val eventsToView: Observable<Event>

    init {
        eventsToView = paginator.createStore(externalEventsSubject)
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
}