package com.example.serge.newsstand.ui.fragments.newslist.middleware

import com.example.serge.newsstand.pagination.Middleware
import com.example.serge.newsstand.pagination.MviAction
import com.example.serge.newsstand.repository.NewsRepository
import com.example.serge.newsstand.ui.fragments.newslist.InternalAction
import com.example.serge.newsstand.ui.fragments.newslist.InputAction
import com.example.serge.newsstand.ui.fragments.newslist.viewmodel.NewsListViewModel
import io.reactivex.Observable
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.schedulers.Schedulers

class LoadPageMiddleware(val repository: NewsRepository) : Middleware<MviAction, NewsListViewModel.UiState> {

    override fun bindMiddleware(action: Observable<MviAction>, state: Observable<NewsListViewModel.UiState>): Observable<MviAction> {
        return action.ofType(InputAction.LoadMoreAction::class.java)
                .withLatestFrom(state) { a, s -> a to s }
                .observeOn(Schedulers.io())
                .switchMapSingle {
                    repository.getTopHeadlinesNews(it.second.pageForLoad)
                            .map<InternalAction> { newsResponse ->
                                if (newsResponse.articles.isEmpty()) InternalAction.LoadEmptyDataAction
                                else InternalAction.LoadDataSuccessAction(newsResponse.articles)
                            }
                            .onErrorReturn { throwable -> InternalAction.LoadDataErrorAction(throwable) }
                }
    }
}