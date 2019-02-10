package com.example.serge.newsstand.ui.fragments.newslist.middleware

import com.example.serge.newsstand.model.NewsItem
import com.example.serge.newsstand.pagination.Store
import com.example.serge.newsstand.pagination.Store.InternalAction.*
import com.example.serge.newsstand.repository.NewsRepository
import com.example.serge.newsstand.ui.fragments.newslist.model.NewsViewData
import com.example.serge.newsstand.ui.fragments.newslist.model.ViewData
import io.reactivex.Observable
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.schedulers.Schedulers

class LoadPageMiddleware(val repository: NewsRepository) : Store.Middleware {

    override fun bindMiddleware(action: Observable<Store.MviAction>, uiState: Observable<Store.UiState>): Observable<Store.MviAction> {
        return action.filter { act -> act is Store.InputAction.LoadMoreAction || act is Store.InputAction.RefreshDataAction }
                .withLatestFrom(uiState) { a, s -> a to s }
                .observeOn(Schedulers.io())
                .switchMapSingle { pair_action_state ->
                    repository.getTopHeadlinesNews(pair_action_state.second.pageForLoad)
                            .map { it.articles }
                            .map<Store.InternalAction> {
                                if (it.isEmpty()) {
                                    LoadEmptyDataAction
                                } else {
                                    LoadDataSuccessAction(it)
                                }
                            }
                            .onErrorReturn { throwable -> LoadDataErrorAction(throwable) }
                }
    }
}