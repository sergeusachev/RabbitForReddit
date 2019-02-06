package com.example.serge.newsstand.ui.fragments.newslist

import com.example.serge.newsstand.pagination.MviAction
import com.example.serge.newsstand.ui.fragments.newslist.model.ViewData

sealed class InputAction : MviAction {
    object LoadMoreAction : InputAction()
    object RefreshDataAction : InputAction()
}

sealed class InternalAction : MviAction {
    data class LoadDataSuccessAction(val data: List<ViewData>) : InternalAction()
    object LoadEmptyDataAction : InternalAction()
    data class LoadDataErrorAction(val throwable: Throwable) : InternalAction()
}