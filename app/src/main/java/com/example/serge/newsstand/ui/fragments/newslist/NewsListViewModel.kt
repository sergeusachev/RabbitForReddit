package com.example.serge.newsstand.ui.fragments.newslist

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.serge.newsstand.model.NewsItem
import com.example.serge.newsstand.pagination.*
import com.example.serge.newsstand.repository.NewsRepository
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.schedulers.Schedulers

class NewsListViewModel(repository: NewsRepository): ViewModel() {

    private val store: Store<MviAction, UiState> = Store(
            LoadPageReducer(),
            listOf(LoadPageMiddleware(repository)),
            UiState()
    )

    private val storeDisposable = store.wire()
    private var viewDisposable: Disposable? = null

    override fun onCleared() {
        super.onCleared()
        storeDisposable.dispose()
    }

    fun bind(view: MviView<MviAction, UiState>) {
        viewDisposable = store.bindView(view)
    }

    fun unbind() {
        viewDisposable?.dispose()
    }

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

    sealed class UiAction : MviAction {
        object LoadMoreAction : UiAction()
        object RefreshData : UiAction()
    }

    sealed class InternalAction : MviAction {
        data class LoadDataSuccessAction(val data: List<NewsItem>) : InternalAction()
        data class LoadDataFailAction(val throwable: Throwable) : InternalAction()
    }

    data class UiState(
            val currentPage: Int = 0,
            val loading: Boolean = true,
            val error: Throwable? = null,
            val data: List<NewsItem> = listOf()
    )

    class LoadPageMiddleware(val repository: NewsRepository) : Middleware<MviAction, UiState> {

        override fun bindMiddleware(action: Observable<MviAction>, state: Observable<UiState>): Observable<MviAction> {
            return action.ofType(UiAction.LoadMoreAction::class.java)
                    .withLatestFrom(state) { a, s -> a to s }
                    .doOnNext { Log.d(MVI_DEBUG_TAG, "Middleware Action: ${it.first}, State: ${it.second.currentPage}") }
                    .observeOn(Schedulers.io())
                    .switchMapSingle {
                        Log.d(MVI_DEBUG_TAG, "PAGE: ${it.second.currentPage}")
                        repository.getTopHeadlinesNews(it.second.currentPage)
                                .map<InternalAction> { newsResponse ->
                                    //Log.d(MVI_DEBUG_TAG, "PAGE: ${newsResponse.articles[0].title}")
                                    InternalAction.LoadDataSuccessAction(newsResponse.articles)
                                }
                                .onErrorReturn { throwable -> InternalAction.LoadDataFailAction(throwable) }
                    }
        }
    }

    class LoadPageReducer : Reducer<UiState, MviAction> {

        override fun reduce(state: UiState, action: MviAction): UiState {
            return when (action) {
                is UiAction.LoadMoreAction -> state.copy(loading = true)
                is InternalAction.LoadDataSuccessAction -> state.copy(
                        currentPage = state.currentPage + 1,
                        data = action.data,
                        loading = false
                )
                else -> throw RuntimeException("Unexpected action: $action")
            }
        }
    }
}