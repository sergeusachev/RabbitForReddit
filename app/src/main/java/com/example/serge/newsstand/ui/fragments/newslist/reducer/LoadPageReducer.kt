package com.example.serge.newsstand.ui.fragments.newslist.reducer

import com.example.serge.newsstand.pagination.MviAction
import com.example.serge.newsstand.pagination.Reducer
import com.example.serge.newsstand.ui.fragments.newslist.InputAction.LoadMoreAction
import com.example.serge.newsstand.ui.fragments.newslist.InputAction.RefreshDataAction
import com.example.serge.newsstand.ui.fragments.newslist.InternalAction.LoadDataSuccessAction
import com.example.serge.newsstand.ui.fragments.newslist.InternalAction.LoadEmptyDataAction
import com.example.serge.newsstand.ui.fragments.newslist.model.EmptyViewData
import com.example.serge.newsstand.ui.fragments.newslist.model.LoadingViewData
import com.example.serge.newsstand.ui.fragments.newslist.model.ViewData
import com.example.serge.newsstand.ui.fragments.newslist.viewmodel.NewsListViewModel.UiState

class LoadPageReducer : Reducer {

    override fun reduce(state: UiState, action: MviAction): UiState {
        return when (action) {
            is LoadMoreAction -> reduceInternal(state, action)
            is LoadDataSuccessAction -> reduceInternal(state, action)
            is LoadEmptyDataAction -> reduceInternal(state, action)
            is RefreshDataAction ->reduceInternal(state, action)
            else -> throw RuntimeException("Unexpected action: $action")
        }
    }

    private fun reduceInternal(state: UiState, action: RefreshDataAction): UiState {
        return when {
            state.lastLoadedPage == 0 -> {
                state.copy(
                        lastLoadedPage = 0,
                        pageForLoad = 1,
                        loading = true,
                        error = null,

                        fullProgress = true
                )
            }
            state.lastLoadedPage > 0 -> {
                state.copy(
                        lastLoadedPage = 0,
                        pageForLoad = 1,
                        loading = true,
                        error = null
                )
            }
            else -> throw RuntimeException("Illegal state.lastLoadedPage!")
        }
    }

    private fun reduceInternal(state: UiState, action: LoadEmptyDataAction): UiState {
        return when {
            state.lastLoadedPage == 0 -> {
                state.copy(
                        lastLoadedPage = state.lastLoadedPage + 1,
                        loading = false,

                        fullProgress = false,
                        fullEmpty = true,
                        fullError = null
                )
            }
            state.lastLoadedPage > 0 -> {
                state.copy(
                        lastLoadedPage = state.lastLoadedPage + 1,
                        loading = false,

                        data = changeLoadingViewType_On_EmptyViewType(state.data)
                )
            }
            else -> throw RuntimeException("Illegal state.lastLoadedPage!")
        }
    }

    private fun reduceInternal(state: UiState, action: LoadMoreAction): UiState {
        return when {
            state.lastLoadedPage > 0 -> {
                state.copy(
                        loading = true,
                        data = addPageLoadingViewType(state.data)
                )
            }
            else -> throw RuntimeException("Illegal state.lastLoadedPage!")
        }
    }

    private fun reduceInternal(state: UiState, action: LoadDataSuccessAction): UiState {
        return when {
            state.lastLoadedPage == 0 -> {
                state.copy(
                        lastLoadedPage = state.lastLoadedPage + 1,
                        pageForLoad = state.pageForLoad + 1,
                        loading = false,

                        fullProgress = false,
                        fullEmpty = false,
                        fullError = null,
                        data = state.data + action.data
                )
            }
            state.lastLoadedPage > 0 -> {
                state.copy(
                        lastLoadedPage = state.lastLoadedPage + 1,
                        pageForLoad = state.pageForLoad + 1,
                        loading = false,

                        fullProgress = false,
                        data = removeLast(state.data) + action.data
                )
            }
            else -> throw RuntimeException("Illegal state.lastLoadedPage: ${state.lastLoadedPage}")
        }
    }

    private fun removeLast(data: List<ViewData>): List<ViewData> = data.subList(0, data.size - 2)

    private fun addPageLoadingViewType(data: List<ViewData>): List<ViewData> {
        val newList = mutableListOf<ViewData>()
        newList.addAll(data)
        newList.add(LoadingViewData)
        return newList
    }

    private fun addEmptyViewType(data: List<ViewData>): List<ViewData> {
        val newList = mutableListOf<ViewData>()
        newList.addAll(data)
        newList.add(EmptyViewData)
        return newList
    }

    private fun changeLoadingViewType_On_EmptyViewType(data: List<ViewData>): List<ViewData> {
        val newList = mutableListOf<ViewData>()
        newList.addAll(data)
        newList[data.size - 1] = EmptyViewData
        return newList
    }
}