package com.example.serge.newsstand.ui.fragments.newslist.reducer

import com.example.serge.newsstand.pagination.MviAction
import com.example.serge.newsstand.pagination.Reducer
import com.example.serge.newsstand.ui.fragments.newslist.InternalAction
import com.example.serge.newsstand.ui.fragments.newslist.InputAction
import com.example.serge.newsstand.ui.fragments.newslist.viewmodel.NewsListViewModel

class LoadPageReducer : Reducer<NewsListViewModel.UiState, MviAction> {

    override fun reduce(state: NewsListViewModel.UiState, action: MviAction): NewsListViewModel.UiState {
        return when (action) {
            is InputAction.LoadMoreAction -> state.copy(loading = true)
            is InternalAction.LoadDataSuccessAction -> state.copy(
                    lastLoadedPage = state.lastLoadedPage + 1,
                    pageForLoad = state.pageForLoad + 1,
                    data = state.data + action.data,
                    loading = false
            )
            is InternalAction.LoadEmptyDataAction -> state.copy(
                    lastLoadedPage = state.lastLoadedPage + 1,
                    loading = false
            )
            else -> throw RuntimeException("Unexpected action: $action")
        }
    }
}