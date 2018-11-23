package com.example.serge.newsstand.ui.fragments.newslist

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.serge.newsstand.R
import com.example.serge.newsstand.model.NewsItem
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.news_item.*
import kotlinx.android.synthetic.main.select_dialog_item_material.*

class NewsListAdapter(val listener: NewsAdapterItemClickListener): RecyclerView.Adapter<NewsListAdapter.NewsListViewHolder>() {

    private val items = ArrayList<NewsItem>()

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): NewsListViewHolder {
        return NewsListViewHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.news_item,
                        parent,
                        false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: NewsListViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun addAndUpdateItems(itemsToInsert: List<NewsItem>) {
        items.addAll(itemsToInsert)
        Log.d(RESPONSE_DEBUG_TAG, "${items.size} items in Adapter now")
        notifyDataSetChanged()
    }

    inner class NewsListViewHolder(override val containerView: View):
            RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(newsItem: NewsItem) {
            tv_title.text = newsItem.title
            tv_description.text = newsItem.description
        }
    }

    interface NewsAdapterItemClickListener {
        fun onListItemClick()
    }
}