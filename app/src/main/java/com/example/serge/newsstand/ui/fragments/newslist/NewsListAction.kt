package com.example.serge.newsstand.ui.fragments.newslist

import com.example.serge.newsstand.model.NewsItem
import com.example.serge.newsstand.pagination.MviAction

sealed class InputAction : MviAction {
    object LoadMoreAction : InputAction()
    object RefreshData : InputAction()
}

sealed class InternalAction : MviAction {
    data class LoadDataSuccessAction(val data: List<NewsItem>) : InternalAction()
    object LoadEmptyDataAction : InternalAction()
    data class LoadDataErrorAction(val throwable: Throwable) : InternalAction()
}

sealed class OutputAction : MviAction {
    data class ProgressFullAction(val show: Boolean): OutputAction()
    data class ProgressPageAction(val show: Boolean): OutputAction()
    data class FullErrorAction(val show: Boolean): OutputAction()
    data class PageErrorAction(val show: Boolean): OutputAction()
    data class EmptyFullAction(val show: Boolean): OutputAction()
    data class EmptyPageAction(val show: Boolean): OutputAction()
    data class ShowDataAction(val items: List<NewsItem>): OutputAction()

}