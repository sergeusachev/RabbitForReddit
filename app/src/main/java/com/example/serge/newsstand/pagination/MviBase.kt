package com.example.serge.newsstand.pagination

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.serge.newsstand.ui.fragments.newslist.InputAction
import com.example.serge.newsstand.ui.fragments.newslist.InternalAction
import com.example.serge.newsstand.ui.fragments.newslist.MVI_DEBUG_TAG
import com.example.serge.newsstand.ui.fragments.newslist.OutputAction
import com.example.serge.newsstand.ui.fragments.newslist.viewmodel.NewsListViewModel
import com.example.serge.newsstand.ui.fragments.newslist.viewmodel.NewsListViewModel.UiState
import com.example.serge.newsstand.utils.EndlessRecyclerOnScrollListener
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.withLatestFrom

class Store(
        private val sideEffectProcessor: SideEffectProcessor,
        private val reducer: Reducer,
        private val middlewares: List<Middleware>,
        private val initialState: UiState,
        private val initialAction: MviAction
) {
    private val paginationState = BehaviorRelay.createDefault<PaginationState>(PaginationState.NONE)
    private val state = BehaviorRelay.createDefault<UiState>(initialState)
    private val actions = PublishRelay.create<MviAction>()

    fun wire(): Disposable {
        val disposable = CompositeDisposable()

        actions.filter { it is InputAction || it is InternalAction }
                .withLatestFrom(state) { action, state ->
                    reducer.reduce(state, action)
                }
                .distinctUntilChanged()
                .subscribe(state::accept)
                .addTo(disposable)

        actions.filter { it is InputAction || it is InternalAction }
                .withLatestFrom(paginationState) { action, paginationState ->
                    sideEffectProcessor.process(paginationState, action)
                }
                .flatMap { newState_sideEffects ->
                    paginationState.accept(newState_sideEffects.first)
                    Observable.fromIterable(newState_sideEffects.second)
                }
                .doOnNext { Log.d("SIDE_EFF", "Effect MviBAse: $it") }
                .subscribe(actions::accept)
                .addTo(disposable)

        Observable.merge<MviAction>(middlewares.map { it.bindMiddleware(actions, state) })
                .subscribe(actions::accept)
                .addTo(disposable)

        return disposable
    }

    fun bindView(
            scrollObservable: Observable<Int>,
            refreshObservable: Observable<MviAction>,
            needInitialAction: Boolean): Disposable {
        val disposable = CompositeDisposable()

        Observable.merge(
                scrollObservable.distinctUntilChanged()
                        .withLatestFrom(state)
                        .filter { pairCountState -> !pairCountState.second.loading }
                        .filter { it.second.pageForLoad > it.second.lastLoadedPage }
                        .map { InputAction.LoadMoreAction },
                refreshObservable
        ).doOnNext { Log.d(MVI_DEBUG_TAG, "Action(UI): $it") }
                .subscribe(actions::accept)
                .addTo(disposable)

        if (needInitialAction) {
            actions.accept(initialAction)
        }

        return disposable
    }

    fun fullProgressObservable() = actions.ofType(OutputAction.ProgressFullAction::class.java)

    fun pageProgressObservable() = actions.ofType(OutputAction.ProgressPageAction::class.java)

    fun fullErrorObservable() = actions.ofType(OutputAction.FullErrorAction::class.java)

    fun pageErrorObservable() = actions.ofType(OutputAction.PageErrorAction::class.java)

    fun emptyViewObservable() = actions.ofType(OutputAction.EmptyFullAction::class.java)

    fun emptyPageObservable() = actions.ofType(OutputAction.EmptyPageAction::class.java)

    fun dataObservable() = actions.ofType(OutputAction.ShowDataAction::class.java)
}

fun getScrollObservable(recylcerView: RecyclerView, threshold: Int): Observable<Int> {
    return Observable.create<Int> { emitter ->
        val scrollListener = EndlessRecyclerOnScrollListener(
                recylcerView.layoutManager as LinearLayoutManager,
                threshold) { totalItems -> emitter.onNext(totalItems) }
        recylcerView.addOnScrollListener(scrollListener)
        emitter.setCancellable { recylcerView.removeOnScrollListener(scrollListener) }
    }
}

interface Reducer {
    fun reduce(state: NewsListViewModel.UiState, action: MviAction): UiState
}

interface SideEffectProcessor {
    fun process(state: PaginationState, action: MviAction): Pair<PaginationState, List<MviAction>>
}

interface Middleware {
    fun bindMiddleware(action: Observable<MviAction>, state: Observable<UiState>): Observable<MviAction>
}

interface MviAction

sealed class PaginationState {
    object NONE : PaginationState()
    object PROGRESS_FULL : PaginationState()
    object PROGRESS_PAGE : PaginationState()
    object ERROR_FULL : PaginationState()
    object ERROR_PAGE : PaginationState()
    object EMPTY_FULL : PaginationState()
    object EMPTY_PAGE : PaginationState()
    object DATA : PaginationState()
}