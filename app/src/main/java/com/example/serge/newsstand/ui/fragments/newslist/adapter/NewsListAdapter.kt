package com.example.serge.newsstand.ui.fragments.newslist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.serge.newsstand.GlideApp
import com.example.serge.newsstand.R
import com.example.serge.newsstand.model.NewsItem
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.card_news_item.*


class NewsListAdapter : RecyclerView.Adapter<NewsListAdapter.NewsListViewHolder>() {

    private val items = ArrayList<NewsItem>()

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): NewsListViewHolder {
        return NewsListViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.card_news_item, parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: NewsListViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun addAndUpdateItems(itemsToInsert: List<NewsItem>) {
        items.clear()
        items.addAll(itemsToInsert)
        notifyDataSetChanged()
    }


    inner class NewsListViewHolder(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(newsItem: NewsItem) {
            tv_news_title.text = newsItem.title
            tv_news_source_name.text = newsItem.source.name
            tv_news_publish_date.text = newsItem.publishedAt

            GlideApp.with(containerView.context)
                    .load(newsItem.urlToImage)
                    .transforms(CenterCrop(), RoundedCorners(20))
                    .into(iv_news_photo)

        }
    }

    interface NewsAdapterItemClickListener {
        fun onListItemClick()
    }

    interface NewsAdapterLoadPageListener {
        fun onLoadNewPage(totalItemCount: Int)
    }
}