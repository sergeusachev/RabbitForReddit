package com.example.serge.newsstand.pagination

import com.example.serge.newsstand.model.NewsItem
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import io.reactivex.observables.ConnectableObservable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

fun createStore(
        initialPaginatorState: PaginatorState,
        externalEvents: Observable<Event>,
        loadPageTransformer: ObservableTransformer<in Pair<PaginatorState, Event>, out Pair<PaginatorState, Event>>): ConnectableObservable<Event> {

    return Observable.create<Event> { emitter ->
        val paginatorState = BehaviorSubject.createDefault(initialPaginatorState)
        val allEvents = PublishSubject.create<Event>()
        allEvents.withLatestFrom(paginatorState, BiFunction<Event, PaginatorState, Pair<PaginatorState, Event>> { event, oldPaginatorState ->
            val newPaginatorState = oldPaginatorState.nextState(event)
            paginatorState.onNext(newPaginatorState)
            Pair(oldPaginatorState, event)
        })
                .compose(loadPageTransformer)
                .subscribe(
                        {
                            emitter.onNext(it.first.getUiSideEffectEvent())
                            allEvents.onNext(it.second)
                        },
                        { emitter.onError(it) }
                )
        externalEvents.subscribe(allEvents)
    }.publish()
}

interface PaginatorState {
    fun getUiSideEffectEvent(): Event
    fun nextState(event: Event): PaginatorState
}

class EMPTY_PROGRESS : PaginatorState {
    override fun getUiSideEffectEvent(): Event {
        return Event.LoadFullEvent(true)
    }

    override fun nextState(event: Event): PaginatorState {
        if (event is Event.DataLoadEvent) {
            return if (event.data.isEmpty()) {
                EMPTY_DATA()
            } else {
                DATA()
            }
        } else throw RuntimeException()
    }
}

class DATA : PaginatorState {
    override fun getUiSideEffectEvent(): Event {
        return Event.LoadFullEvent(false)
    }

    override fun nextState(event: Event): PaginatorState {
    }

}

class EMPTY_DATA : PaginatorState {

}

sealed class Event {
    data class LoadInitialEvent() : Event()
    data class RefreshEvent() : Event()
    data class LoadNewPageEvent(val show: Boolean) : Event()
    data class LoadFullEvent(val show: Boolean): Event()
    data class ErrorLoadingPageEvent(val t: Throwable): Event()
    data class DataLoadEvent(val data: List<NewsItem>): Event()
}

interface PagingState