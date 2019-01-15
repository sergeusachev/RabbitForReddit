package com.example.serge.newsstand.ui.fragments.newslist

import androidx.lifecycle.ViewModel
import com.example.serge.newsstand.pagination.Event
import com.example.serge.newsstand.pagination.RxPaginator
import com.example.serge.newsstand.repository.NewsRepository
import io.reactivex.observables.ConnectableObservable
import io.reactivex.subjects.PublishSubject

class NewsListViewModel(repository: NewsRepository): ViewModel() {

    private val externalEventsSubject = PublishSubject.create<Event>()
    private val paginator: RxPaginator = RxPaginator(repository)
    val eventsToView: ConnectableObservable<Event>

    init {
        eventsToView = paginator.createStore(externalEventsSubject)
        eventsToView.connect()
    }

    fun sendEvent(event: Event) {
        externalEventsSubject.onNext(event)
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