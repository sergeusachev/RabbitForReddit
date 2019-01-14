package com.example.serge.newsstand.ui.fragments.newslist

import androidx.lifecycle.ViewModel
import com.example.serge.newsstand.model.NewsItem
import com.example.serge.newsstand.pagination.*
import com.example.serge.newsstand.repository.NewsRepository
import io.reactivex.*
import io.reactivex.subjects.PublishSubject

private val DEBUG_TAG = NewsListViewModel::class.java.simpleName

class NewsListViewModel(private val repository: NewsRepository): ViewModel() {

    private val externalEventsSubject = PublishSubject.create<Event>()
//    private val requestConfig = RequestConfig()

    private val loadPageTransformer = ObservableTransformer<Pair<PaginatorState, Event>, Pair<PaginatorState, Event>> { upstream ->
        upstream.flatMap{ pairStateEvent ->
            repository.getTopHeadlinesNews(0).toObservable()
                    .map { it.articles }
                    .map { Pair(pairStateEvent.first, Event.DataLoadEvent(it)) }
        }
    }

    /*private val paginator = Paginator(
            { pageNumber -> repository.getTopHeadlinesNews(
                    requestConfig.country,
                    requestConfig.category,
                    requestConfig.sources,
                    requestConfig.query,
                    requestConfig.pageSize,
                    pageNumber).map { it.articles }
            },
            object : ViewController<NewsItem> {
                override fun showEmptyProgress(show: Boolean) {
                }

                override fun showEmptyError(show: Boolean, error: Throwable?) {
                }

                override fun showEmptyView(show: Boolean) {
                }

                override fun showData(show: Boolean, data: List<NewsItem>) {
                }

                override fun showErrorMessage(error: Throwable) {
                }

                override fun showRefreshProgress(show: Boolean) {
                }

                override fun showPageProgress(show: Boolean) {
                }
            }
    )*/

    init {
        val events = createStore(
                EMPTY(),
                externalEventsSubject,
                loadPageTransformer)
    }

    /*fun refreshData() = paginator.refresh()
    fun loadNextPage() = paginator.loadNewPage()*/

    enum class CategoriesEnum(val categoryName: String) {
        BUSINESS("business"),
        ENTERTAINMENT("entertainment"),
        GENERAL("general"),
        HEALTH("health"),
        SCIENCE("science"),
        SPORTS("sports"),
        TECHNOLOGY("technology");
    }

    enum class CountryEnum(val countryCode: String) {
        RU("ru");
    }

    /*data class RequestConfig(
            val country: String? = CountryEnum.RU.countryCode,
            val category: String? = CategoriesEnum.GENERAL.categoryName,
            val sources: String? = null,
            val query: String? = null,
            val pageSize: Int? = null,
            var page: Int = 0
    )*/
}