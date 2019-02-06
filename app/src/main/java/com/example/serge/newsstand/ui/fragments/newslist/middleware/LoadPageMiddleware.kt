package com.example.serge.newsstand.ui.fragments.newslist.middleware

import android.util.Log
import com.example.serge.newsstand.model.NewsItem
import com.example.serge.newsstand.pagination.Middleware
import com.example.serge.newsstand.pagination.MviAction
import com.example.serge.newsstand.repository.NewsRepository
import com.example.serge.newsstand.ui.fragments.newslist.InputAction.LoadMoreAction
import com.example.serge.newsstand.ui.fragments.newslist.InputAction.RefreshDataAction
import com.example.serge.newsstand.ui.fragments.newslist.InternalAction
import com.example.serge.newsstand.ui.fragments.newslist.model.NewsViewData
import com.example.serge.newsstand.ui.fragments.newslist.model.ViewData
import com.example.serge.newsstand.ui.fragments.newslist.viewmodel.NewsListViewModel
import io.reactivex.Observable
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.schedulers.Schedulers

class LoadPageMiddleware(val repository: NewsRepository) : Middleware {

    override fun bindMiddleware(action: Observable<MviAction>, state: Observable<NewsListViewModel.UiState>): Observable<MviAction> {
        return action.filter { act -> act is LoadMoreAction || act is RefreshDataAction }
                .withLatestFrom(state) { a, s -> a to s }
                .observeOn(Schedulers.io())
                .switchMapSingle {
                    repository.getTopHeadlinesNews(it.second.pageForLoad)
                            .map { newsResponse ->
                                Log.d("CHECK_ALL", "Total: ${newsResponse.totalResults}")
                                mapDataToViewData(newsResponse.articles)
                            }
                            .map<InternalAction> { viewDataList ->
                                if (viewDataList.isEmpty()) InternalAction.LoadEmptyDataAction
                                else InternalAction.LoadDataSuccessAction(viewDataList)
                            }
                            .onErrorReturn { throwable -> InternalAction.LoadDataErrorAction(throwable) }
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