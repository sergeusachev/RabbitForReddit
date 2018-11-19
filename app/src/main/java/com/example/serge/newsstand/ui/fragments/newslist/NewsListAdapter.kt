package com.example.serge.newsstand.ui.fragments.newslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.select_dialog_item_material.*

class NewsListAdapter(val listener: NewsAdapterItemClickListener): RecyclerView.Adapter<NewsListAdapter.NewsListViewHolder>() {

    private val items = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): NewsListViewHolder {
        return NewsListViewHolder(
                LayoutInflater.from(parent.context).inflate(
                        android.R.layout.simple_list_item_1,
                        parent,
                        false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: NewsListViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setItems(itemsToInsert: List<String>) {
        items.addAll(itemsToInsert)
    }

    inner class NewsListViewHolder(override val containerView: View):
            RecyclerView.ViewHolder(containerView), LayoutContainer {


        fun bind(name: String) {
            text1.text = name

            text1.setOnClickListener {
                listener.onListItemClick(name)
            }
        }
    }

    interface NewsAdapterItemClickListener {
        fun onListItemClick(data: String)
    }
}