package com.example.serge.newsstand.ui.fragments.newslist.reducer

import com.example.serge.newsstand.pagination.MviAction
import com.example.serge.newsstand.pagination.PaginationState
import com.example.serge.newsstand.pagination.SideEffectProcessor
import com.example.serge.newsstand.ui.fragments.newslist.InputAction
import com.example.serge.newsstand.ui.fragments.newslist.InternalAction
import com.example.serge.newsstand.ui.fragments.newslist.OutputAction

class PaginationSideEffectProcessor : SideEffectProcessor {

    override fun process(state: PaginationState, action: MviAction): Pair<PaginationState, List<MviAction>> {
        return when (state) {
            PaginationState.NONE -> {
                when (action) {
                    is InputAction.LoadMoreAction -> {
                        Pair(
                                PaginationState.PROGRESS_FULL,
                                listOf(OutputAction.ProgressFullAction(true))
                        )
                    }
                    else -> throw RuntimeException("Illegal Action - $action")
                }

            }
            PaginationState.PROGRESS_FULL -> {
                when (action) {
                    is InternalAction.LoadDataSuccessAction -> {
                        Pair(
                                PaginationState.DATA,
                                listOf(
                                        OutputAction.ProgressFullAction(false),
                                        OutputAction.ShowDataAction(action.data)
                                )
                        )
                    }
                    is InternalAction.LoadDataErrorAction -> {
                        Pair(
                                PaginationState.ERROR_FULL,
                                listOf(
                                        OutputAction.ProgressFullAction(false),
                                        OutputAction.FullErrorAction(true)
                                )
                        )
                    }
                    is InternalAction.LoadEmptyDataAction -> {
                        Pair(
                                PaginationState.EMPTY_FULL,
                                listOf(
                                        OutputAction.ProgressFullAction(false),
                                        OutputAction.EmptyFullAction(true)
                                )
                        )
                    }
                    else -> throw RuntimeException("Illegal Action - $action")
                }
            }
            PaginationState.PROGRESS_PAGE -> {
                when (action) {
                    is InternalAction.LoadDataSuccessAction -> {
                        Pair(
                                PaginationState.DATA,
                                listOf(
                                        OutputAction.ProgressPageAction(false),
                                        OutputAction.ShowDataAction(action.data)
                                )
                        )
                    }
                    is InternalAction.LoadDataErrorAction -> {
                        Pair(
                                PaginationState.ERROR_PAGE,
                                listOf(
                                        OutputAction.ProgressPageAction(false),
                                        OutputAction.PageErrorAction(true)
                                )
                        )
                    }
                    is InternalAction.LoadEmptyDataAction -> {
                        Pair(
                                PaginationState.EMPTY_PAGE,
                                listOf(
                                        OutputAction.ProgressPageAction(false),
                                        OutputAction.EmptyPageAction(true)
                                )
                        )
                    }
                    else -> throw RuntimeException("Illegal Action - $action")
                }
            }
            PaginationState.ERROR_FULL -> {
                when (action) {
                    is InputAction.RefreshData -> {
                        Pair(
                                PaginationState.PROGRESS_FULL,
                                listOf(
                                        OutputAction.FullErrorAction(false),
                                        OutputAction.ProgressFullAction(true)
                                )
                        )
                    }
                    else -> throw RuntimeException("Illegal Action - $action")
                }
            }
            PaginationState.ERROR_PAGE -> {
                when (action) {
                    is InputAction.RefreshData -> {
                        Pair(
                                PaginationState.PROGRESS_FULL,
                                listOf(
                                        OutputAction.PageErrorAction(false),
                                        OutputAction.ProgressFullAction(true)
                                )
                        )
                    }
                    else -> throw RuntimeException("Illegal Action - $action")
                }
            }
            PaginationState.EMPTY_FULL -> {
                when (action) {
                    is InputAction.RefreshData -> {
                        Pair(
                                PaginationState.PROGRESS_FULL,
                                listOf(
                                        OutputAction.EmptyFullAction(false),
                                        OutputAction.ProgressFullAction(true)
                                )
                        )
                    }
                    else -> throw RuntimeException("Illegal Action - $action")
                }
            }
            PaginationState.EMPTY_PAGE -> {
                when (action) {
                    is InputAction.RefreshData -> {
                        Pair(
                                PaginationState.PROGRESS_FULL,
                                listOf(
                                        OutputAction.EmptyPageAction(false),
                                        OutputAction.ProgressFullAction(true)
                                )
                        )
                    }
                    else -> throw RuntimeException("Illegal Action - $action")
                }
            }
            PaginationState.DATA -> {
                when (action) {
                    is InputAction.RefreshData -> {
                        Pair(
                                PaginationState.PROGRESS_FULL,
                                listOf(
                                        OutputAction.ShowDataAction(listOf()),
                                        OutputAction.ProgressFullAction(true)
                                )
                        )
                    }
                    is InputAction.LoadMoreAction -> {
                        Pair(
                                PaginationState.PROGRESS_PAGE,
                                listOf(OutputAction.ProgressPageAction(true))
                        )
                    }
                    else -> throw RuntimeException("Illegal Action - $action")
                }
            }
        }
    }
}