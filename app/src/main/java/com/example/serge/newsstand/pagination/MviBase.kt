package com.example.serge.newsstand.pagination

import com.example.serge.newsstand.ui.fragments.newslist.InputAction
import com.example.serge.newsstand.ui.fragments.newslist.viewmodel.NewsListViewModel
import com.example.serge.newsstand.ui.fragments.newslist.viewmodel.NewsListViewModel.UiState
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.withLatestFrom

class Store(
        private val reducer: Reducer,
        private val middlewares: List<Middleware>,
        private val initialState: UiState,
        private val initialAction: MviAction
) {
    private val state = BehaviorRelay.createDefault<UiState>(initialState)
    private val actions = PublishRelay.create<MviAction>()

    fun wire(): Disposable {
        val disposable = CompositeDisposable()

        actions.withLatestFrom(state) { action, state ->
                    reducer.reduce(state, action)
                }
                .distinctUntilChanged()
                .subscribe(state::accept)
                .addTo(disposable)

        Observable.merge<MviAction>(middlewares.map { it.bindMiddleware(actions, state) })
                .subscribe(actions::accept)
                .addTo(disposable)

        return disposable
    }

    fun bindView(mviView: MviView): Disposable {
        val disposable = CompositeDisposable()

        state.observeOn(AndroidSchedulers.mainThread())
                .subscribe(mviView::render)
                .addTo(disposable)

        Observable.merge(
                mviView.scrollObservable.distinctUntilChanged()
                        .withLatestFrom(state)
                        .filter { pairCountState -> !pairCountState.second.loading }
                        .filter { it.second.pageForLoad > it.second.lastLoadedPage }
                        .map { InputAction.LoadMoreAction },
                mviView.swipeRefreshObservable
        )
                .subscribe(actions::accept)
                .addTo(disposable)

        actions.accept(initialAction)

        return disposable
    }
}

interface MviView {
    val scrollObservable: Observable<Int>
    val swipeRefreshObservable: Observable<MviAction>
    fun render(state: UiState)
}

interface Reducer {
    fun reduce(state: NewsListViewModel.UiState, action: MviAction): UiState
}

interface Middleware {
    fun bindMiddleware(action: Observable<MviAction>, state: Observable<UiState>): Observable<MviAction>
}

interface MviAction