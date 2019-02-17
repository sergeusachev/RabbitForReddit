package com.serge.rabbitforreddit.pagination

import com.serge.rabbitforreddit.model.NewsItem
import com.serge.rabbitforreddit.pagination.Store.InputAction.RefreshDataAction
import com.serge.rabbitforreddit.ui.fragments.newslist.model.ViewData
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
        private val middlewares: List<Middleware>
) {

    sealed class InputAction : MviAction {
        object LoadMoreAction : InputAction()
        object RefreshDataAction : InputAction()
    }

    sealed class InternalAction : MviAction {
        data class LoadDataSuccessAction(val data: List<NewsItem>) : InternalAction()
        object LoadEmptyDataAction : InternalAction()
        data class LoadDataErrorAction(val throwable: Throwable) : InternalAction()
    }

    data class UiState(
            val lastLoadedPage: Int = 0,
            val pageForLoad: Int = 1,
            val loading: Boolean = false,
            val error: Throwable? = null,

            val fullProgress: Boolean = false,
            val fullEmpty: Boolean = false,
            val fullError: Throwable? = null,
            val data: List<ViewData> = listOf()
    )

    private val initialState = UiState()
    private val initialAction = RefreshDataAction
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
                        .map { InputAction.LoadMoreAction },

                mviView.swipeRefreshObservable,

                initialActionObservable.withLatestFrom(state)
                        .filter { !it.second.loading && it.second.lastLoadedPage == 0 }
                        .map { it.first }
        )
                .subscribe(actions::accept)
                .addTo(disposable)

        return disposable
    }

    interface MviView {
        val scrollObservable: Observable<Int>
        val swipeRefreshObservable: Observable<MviAction>
        fun render(uiState: UiState)
    }

    interface Reducer {
        fun reduce(uiState: UiState, action: MviAction): UiState
    }

    interface Middleware {
        fun bindMiddleware(action: Observable<MviAction>, uiState: Observable<UiState>): Observable<MviAction>
    }

    interface MviAction
}