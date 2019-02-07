package com.example.serge.newsstand.pagination

import com.example.serge.newsstand.ui.fragments.newslist.reducer.LoadPageReducer
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

        val initialActionObservable = Observable.create<MviAction> { emitter ->
            emitter.onNext(initialAction)
        }

        Observable.merge(
                mviView.scrollObservable.distinctUntilChanged()
                        .withLatestFrom(state)
                        .filter { pairCountState -> !pairCountState.second.loading }
                        .filter { it.second.pageForLoad > it.second.lastLoadedPage }
                        .map { LoadPageReducer.InputAction.LoadMoreAction },

                mviView.swipeRefreshObservable,

                initialActionObservable.withLatestFrom(state)
                        .filter { !it.second.loading && it.second.lastLoadedPage == 0 }
                        .map { it.first }
        )
                .subscribe(actions::accept)
                .addTo(disposable)

        return disposable
    }
}