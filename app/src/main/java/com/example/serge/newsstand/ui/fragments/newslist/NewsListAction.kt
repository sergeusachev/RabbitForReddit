package com.example.serge.newsstand.ui.fragments.newslist

import com.example.serge.newsstand.model.NewsItem
import com.example.serge.newsstand.pagination.MviAction

sealed class UiAction : MviAction {
    object LoadMoreAction : UiAction()
    object RefreshData : UiAction()
}

sealed class InternalAction : MviAction {
    data class LoadDataSuccessAction(val data: List<NewsItem>) : InternalAction()
    object LoadEmptyDataAction : InternalAction()
    data class LoadDataFailAction(val throwable: Throwable) : InternalAction()
}

sealed class OutputAction : MviAction {

}