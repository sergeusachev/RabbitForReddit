package com.example.serge.newsstand.ui.fragments.newslist

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

    fun bindView(view: MviView<MviAction>) {
        viewDisposable = store.bindView(view)
    }

    fun unbindView() {
        viewDisposable?.dispose()
    }

    fun getUiStateObservable() = store.uiStateObservable()

    override fun onCleared() {
        super.onCleared()
        storeDisposable.dispose()
    }

    class LoadPageMiddleware(val repository: NewsRepository) : Middleware<MviAction, UiState> {

        override fun bindMiddleware(action: Observable<MviAction>, state: Observable<UiState>): Observable<MviAction> {
            return action.ofType(UiAction.LoadMoreAction::class.java)
                    .withLatestFrom(state) { a, s -> a to s }
                    .observeOn(Schedulers.io())
                    .switchMapSingle {
                        repository.getTopHeadlinesNews(it.second.currentPage)
                                .map<InternalAction> { newsResponse -> InternalAction.LoadDataSuccessAction(newsResponse.articles) }
                                .onErrorReturn { throwable -> InternalAction.LoadDataFailAction(throwable) }
                    }
        }
    }

    class LoadPageReducer : Reducer<UiState, MviAction> {

        override fun reduce(state: UiState, action: MviAction): UiState {
            return when (action) {
                is UiAction.LoadMoreAction -> state.copy(
                        currentPage = state.currentPage + 1,
                        loading = true
                )
                is InternalAction.LoadDataSuccessAction -> state.copy(
                        data = state.data + action.data,
                        loading = false
                )
                else -> throw RuntimeException("Unexpected action: $action")
            }
        }
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
}