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