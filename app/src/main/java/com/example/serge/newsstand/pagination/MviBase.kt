package com.example.serge.newsstand.pagination

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.serge.newsstand.ui.fragments.newslist.MVI_DEBUG_TAG
import com.example.serge.newsstand.utils.EndlessRecyclerOnScrollListener
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.withLatestFrom

class Store<A, S>(
        private val reducer: Reducer<S, A>,
        private val middlewares: List<Middleware<A, S>>,
        private val initialState: S,
        private val initialAction: A
) {
    private val state = BehaviorRelay.createDefault<S>(initialState)
    private val actions = PublishRelay.create<A>()

    fun wire(): Disposable {
        val disposable = CompositeDisposable()

        actions.doOnNext { Log.d(MVI_DEBUG_TAG, "Action(MAIN): $it") }
                .withLatestFrom(state) { action, state ->
                    reducer.reduce(state, action)
                }
                .distinctUntilChanged()
                .subscribe(state::accept)
                .addTo(disposable)

        Observable.merge<A>(middlewares.map { it.bindMiddleware(actions, state) })
                .doOnNext { Log.d(MVI_DEBUG_TAG, "Action(MIDDLEWARE): $it") }
                .subscribe(actions::accept)
                .addTo(disposable)

        actions.accept(initialAction)

        return disposable
    }

    fun bindView(view: MviView<A>): Disposable {
        val disposable = CompositeDisposable()

        view.viewActions.doOnNext { Log.d(MVI_DEBUG_TAG, "Action(UI): $it") }
                .subscribe(actions::accept)
                .addTo(disposable)

        return disposable
    }

    fun uiStateObservable(): Observable<S> = state.doOnNext { Log.d(MVI_DEBUG_TAG, "State: $it") }
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

interface MviView<A> {
    val viewActions: Observable<A>
}

interface Reducer<S, A> {
    fun reduce(state: S, action: A): S
}

interface Middleware<A, S> {
    fun bindMiddleware(action: Observable<A>, state: Observable<S>): Observable<A>
}

interface MviAction