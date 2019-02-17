package com.serge.rabbitforreddit.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EndlessRecyclerOnScrollListener(
        val layoutManager: LinearLayoutManager,
        val threshold: Int,
        val onLoadMore: (Int) -> Unit
) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (layoutManager.findLastVisibleItemPosition() + threshold > layoutManager.itemCount) {
            onLoadMore(layoutManager.itemCount)
        }
    }
}
