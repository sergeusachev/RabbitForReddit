package com.serge.rabbitforreddit.ui.fragments.newslist.adapter

import androidx.recyclerview.widget.DiffUtil
import com.serge.rabbitforreddit.ui.fragments.newslist.model.ViewData

class NewsListDiffCallback(
        private val oldList: List<ViewData>,
        private val newList: List<ViewData>) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].getId() == newList[newItemPosition].getId()
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return true
    }
}