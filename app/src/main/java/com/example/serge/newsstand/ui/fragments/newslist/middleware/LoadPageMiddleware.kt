package com.example.serge.newsstand.ui.fragments.newslist.middleware

import com.example.serge.newsstand.model.NewsItem
import com.example.serge.newsstand.pagination.Store
import com.example.serge.newsstand.repository.NewsRepository
import com.example.serge.newsstand.ui.fragments.newslist.model.NewsViewData
import com.example.serge.newsstand.ui.fragments.newslist.model.ViewData
import io.reactivex.Observable
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.schedulers.Schedulers

class LoadPageMiddleware(val repository: NewsRepository) : Store.Middleware {

    override fun bindMiddleware(action: Observable<Store.MviAction>, state: Observable<Store.UiState>): Observable<Store.MviAction> {
        return action.filter { act -> act is Store.InputAction.LoadMoreAction || act is Store.InputAction.RefreshDataAction }
                .withLatestFrom(state) { a, s -> a to s }
                .observeOn(Schedulers.io())
                .switchMapSingle {
                    repository.getTopHeadlinesNews(it.second.pageForLoad)
                            .map { newsResponse ->
                                mapDataToViewData(newsResponse.articles)
                            }
                            .map<Store.InternalAction> { viewDataList ->
                                if (viewDataList.isEmpty()) Store.InternalAction.LoadEmptyDataAction
                                else Store.InternalAction.LoadDataSuccessAction(viewDataList)
                            }
                            .onErrorReturn { throwable -> Store.InternalAction.LoadDataErrorAction(throwable) }
                }
    }

    private fun mapDataToViewData(data: List<NewsItem>): List<ViewData> {
        return data.map {
            NewsViewData(
                    it.source.id,
                    it.source.name,
                    it.author,
                    it.title,
                    it.description,
                    it.url,
                    it.urlToImage,
                    it.publishedAt,
                    it.content
            )
        }
    }
}