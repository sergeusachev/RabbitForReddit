package com.serge.rabbitforreddit.ui.fragments.newslist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.serge.rabbitforreddit.R
import com.serge.rabbitforreddit.GlideApp
import com.serge.rabbitforreddit.ui.fragments.newslist.model.NewsViewData
import com.serge.rabbitforreddit.ui.fragments.newslist.model.ViewData
import com.serge.rabbitforreddit.ui.fragments.newslist.model.ViewType
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.card_news_item.*


class NewsListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = ArrayList<ViewData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ViewType.TYPE_DATA.typeCode -> NewsListViewHolder(layoutInflater.inflate(R.layout.card_news_item, parent, false))
            ViewType.TYPE_LOADING.typeCode -> LoadingViewHolder(layoutInflater.inflate(R.layout.card_news_loading, parent, false))
            ViewType.TYPE_EMPTY.typeCode -> EmptyViewHolder(layoutInflater.inflate(R.layout.card_news_empty, parent, false))
            else -> throw RuntimeException("Illegal viewType!")
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NewsListViewHolder) {
            holder.bind(items[position] as NewsViewData)
        }
    }

    override fun getItemViewType(position: Int) = items[position].getType()

    fun addAndUpdateItems(itemsToInsert: List<ViewData>) {
        val oldList = items
        val diffResult = DiffUtil.calculateDiff(NewsListDiffCallback(oldList, itemsToInsert))
        items.clear()
        items.addAll(itemsToInsert)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class NewsListViewHolder(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(newsItem: NewsViewData) {
            tv_news_title.text = newsItem.title
            tv_news_source_name.text = newsItem.sourceName
            tv_news_publish_date.text = newsItem.publishedAt

            GlideApp.with(containerView.context)
                    .load(newsItem.urlToImage)
                    .transforms(CenterCrop(), RoundedCorners(20))
                    .into(iv_news_photo)

        }
    }

    inner class LoadingViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer

    inner class EmptyViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer

    interface NewsAdapterItemClickListener {
        fun onListItemClick()
    }
}