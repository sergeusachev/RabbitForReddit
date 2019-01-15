package com.example.serge.newsstand.pagination

import android.util.Log
import com.example.serge.newsstand.model.NewsItem
import com.example.serge.newsstand.repository.NewsRepository
import com.example.serge.newsstand.ui.fragments.newslist.DEBUG_TAG
import com.example.serge.newsstand.ui.fragments.newslist.NewsListFragment
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import io.reactivex.observables.ConnectableObservable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class RxPaginator(private val repository: NewsRepository) {

    private val loadNewPageEffect = ObservableTransformer<Event, Event> { upstream ->
        upstream.ofType(Event.LoadNewPageEvent::class.java)
                .observeOn(Schedulers.io())
                .switchMapSingle {
                    repository.getTopHeadlinesNews(it.pageNumberToLoad)
                }
                .map { it.articles }
                .map { Event.ShowDataEvent(it) }
    }

    fun createStore(
            //initialState: PagingState,
            externalEvents: Observable<Event>
            //transformer: ObservableTransformer<in Event, out Event>
    ): ConnectableObservable<Event> {

        return Observable.create<Event> { emitter ->
            val pagingState = BehaviorSubject.createDefault(PagingState())
            val allEvents = PublishSubject.create<Event>()
            allEvents.withLatestFrom(pagingState, BiFunction<Event, PagingState, List<Event>> { event, oldPagingState ->
                Log.d(DEBUG_TAG, "OLD S: ${oldPagingState.paginatorState}")
                val newPagingState = reduce(oldPagingState, event)
                Log.d(DEBUG_TAG, "NEW S: ${newPagingState}")
                val ev = if (event is Event.LoadMoreEvent) Event.LoadNewPageEvent(newPagingState.currentPage) else event
                val sideEvents = oldPagingState.transitionEvents.toMutableList()
                sideEvents.add(ev)
                pagingState.onNext(newPagingState)
                sideEvents
            })
                    .flatMap { sideEvents -> Observable.fromIterable(sideEvents) }
                    .compose(loadNewPageEffect)
                    .subscribe(
                            {
                                emitter.onNext(it)
                                allEvents.onNext(it)
                            },
                            { emitter.onError(it) }
                    )
            externalEvents.subscribe(allEvents)
        }.publish()
    }

    private fun reduce(oldState: PagingState, event: Event): PagingState {
        return when(event) {
            is Event.ShowDataEvent -> {
                val nextState = oldState.paginatorState.nextState(event)
                oldState.copy(
                        paginatorState = nextState,
                        currentPage = oldState.currentPage + 1,
                        currentData = oldState.currentData + event.data,
                        transitionEvents = oldState.paginatorState.getTransitionEvents(event)
                )
            }
            is Event.RefreshEvent -> {
                val nextState = oldState.paginatorState.nextState(event)
                oldState.copy(
                        paginatorState = nextState,
                        currentPage = 0,
                        transitionEvents = oldState.paginatorState.getTransitionEvents(event)
                )
            }
            else -> {
                val nextState = oldState.paginatorState.nextState(event)
                oldState.copy(
                        paginatorState = nextState,
                        transitionEvents = oldState.paginatorState.getTransitionEvents(event)
                )
            }
        }
    }

}

data class PagingState(
     val paginatorState: PaginatorState = EMPTY(),
     val currentPage: Int = 0,
     val currentData: List<NewsItem> = listOf(),
     val transitionEvents: List<Event> = listOf()
)

sealed class Event {
    //object LoadFirstPageEvent : Event()
    object LoadMoreEvent : Event()
    object RefreshEvent : Event()

    data class LoadNewPageEvent(val pageNumberToLoad: Int) : Event()

    data class ShowFullProgressEvent(val show: Boolean): Event()
    data class ShowPageProgressEvent(val show: Boolean): Event()
    data class ShowDataEvent(val data: List<NewsItem>): Event()
    data class ShowEmptyViewEvent(val show: Boolean): Event()

    data class ShowFullErrorEvent(val t: Throwable): Event()
    data class ShowPageLoadingErrorEvent(val t: Throwable): Event()
}