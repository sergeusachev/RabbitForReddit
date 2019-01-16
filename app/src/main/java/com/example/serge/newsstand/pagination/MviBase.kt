package com.example.serge.newsstand.pagination

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.withLatestFrom

class Store<A, S>(
        private val reducer: Reducer<S, A>,
        private val middlewares: List<Middleware<A, S>>,
        private val initialState: S
) {
    private val state = BehaviorRelay.createDefault<S>(initialState)
    private val actions = PublishRelay.create<A>()

    fun wire(): Disposable {
        val disposable = CompositeDisposable()

        actions.withLatestFrom(state) { action, state ->
            reducer.reduce(state, action)
        }
                .distinctUntilChanged()
                .subscribe(state::accept)
                .addTo(disposable)

        Observable.merge<A>(middlewares.map { it.bindMiddleware(actions, state) })
                .subscribe(actions::accept)
                .addTo(disposable)

        return disposable
    }

    fun bindView(view: MviView<A, S>): Disposable {
        val disposable = CompositeDisposable()

        state.observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::render)
                .addTo(disposable)

        view.actions.subscribe(actions::accept)
                .addTo(disposable)

        return disposable
    }
}

interface MviView<A, S> {
    val actions: Observable<A>
    fun render(state: S)
}

interface Reducer<S, A> {
    fun reduce(state: S, action: A): S
}

interface Middleware<A, S> {
    fun bindMiddleware(action: Observable<A>, state: Observable<S>): Observable<A>
}

interface MviAction