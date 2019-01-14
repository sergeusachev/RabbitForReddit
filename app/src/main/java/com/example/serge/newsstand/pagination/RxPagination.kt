package com.example.serge.newsstand.pagination

import com.example.serge.newsstand.model.NewsItem
import com.example.serge.newsstand.repository.NewsRepository
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import io.reactivex.observables.ConnectableObservable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

fun createStore(
        initialPaginatorState: PaginatorState,
        externalEvents: Observable<Event>,
        loadPageTransformer: ObservableTransformer<in Pair<PaginatorState, Event>, out Pair<PaginatorState, Event>>,
        repository: NewsRepository): ConnectableObservable<Event> {

    return Observable.create<Event> { emitter ->
        val paginatorState = BehaviorSubject.createDefault(initialPaginatorState)
        val allEvents = PublishSubject.create<Event>()
        allEvents.withLatestFrom(paginatorState, BiFunction<Event, PaginatorState, Pair<PaginatorState, Event>> { event, oldPaginatorState ->
            val newPaginatorState = oldPaginatorState.nextState(event)
            paginatorState.onNext(newPaginatorState)
            Pair(oldPaginatorState, event)
        })
                .flatMapSingle { pairStateEvent ->
                    repository.getTopHeadlinesNews(0)
                            .map { it.articles }
                            .map { Pair(pairStateEvent.first, Event.DataLoadEvent(it)) }

                }
                .subscribe(
                        {
                            it.first.getUiSideEffectEvents().forEach { ev -> emitter.onNext(ev) }
                            allEvents.onNext(it.second)
                        },
                        { emitter.onError(it) }
                )
        externalEvents.subscribe(allEvents)
    }.publish()
}

private fun processSideEffects(emitter: ObservableEmitter<Event>, events: List<Event>) {
   events.forEach { emitter.onNext(it) }
}

interface PaginatorState {
    fun getUiSideEffectEvents(): List<Event>
    fun nextState(event: Event): PaginatorState
}

class EMPTY : PaginatorState {

    override fun getUiSideEffectEvents(): List<Event> {
        return listOf(Event.LoadFullEvent(true))
    }

    override fun nextState(event: Event): PaginatorState {
        return if (event is Event.LoadInitialEvent) {
            EMPTY_PROGRESS()
        } else throw RuntimeException()
    }

}

class EMPTY_PROGRESS : PaginatorState {
    override fun getUiSideEffectEvents(): List<Event> {
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
    override fun getUiSideEffectEvents(): List<Event> {
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