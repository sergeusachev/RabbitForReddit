package com.example.serge.newsstand.ui.fragments.newslist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.serge.newsstand.R
import com.example.serge.newsstand.navigation.Navigator
import com.example.serge.newsstand.utils.EndlessRecyclerOnScrollListener
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_news_list.*
import javax.inject.Inject

private val DEBUG_TAG = NewsListFragment::class.java.simpleName
val RESPONSE_DEBUG_TAG = "Response_debug_tag"

class NewsListFragment : Fragment(), NewsListAdapter.NewsAdapterItemClickListener {

    companion object {
        const val SCROLL_PREV_TOTAL = "SCROLL_PREVIOUS_TOTAL_COUNT"
        const val SCROLL_LOADING_STATE = "SCROLL_LOADING_STATE"
    }

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var viewModelFactory: NewsListViewModelFactory

    private val compositeDisposable = CompositeDisposable()
    private val adapter = NewsListAdapter(this)

    private lateinit var endlessRecyclerOnScrollListener: EndlessRecyclerOnScrollListener

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        Log.d("INSET_CHECK", "onAttach()")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.d("INSET_CHECK", "onCreateView()")
        return inflater.inflate(R.layout.fragment_news_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("INSET_CHECK", "onViewCreated()")
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(NewsListViewModel::class.java)

        endlessRecyclerOnScrollListener = object : EndlessRecyclerOnScrollListener() {
            override fun onLoadMore() {
                viewModel.sendEvent()
            }
        }

        if (savedInstanceState != null) {
            val scrollPreviousTotal = savedInstanceState.getInt(SCROLL_PREV_TOTAL)
            val scrollLoadingState = savedInstanceState.getBoolean(SCROLL_LOADING_STATE)
            endlessRecyclerOnScrollListener.apply {
                previousTotal = scrollPreviousTotal
                loading = scrollLoadingState
            }
        }

        recycler_news.layoutManager = LinearLayoutManager(activity)
        recycler_news.adapter = adapter
        recycler_news.addOnScrollListener(endlessRecyclerOnScrollListener)

        recycler_news.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                app_bar_fragment_list.isSelected = recyclerView.canScrollVertically(-1)
            }
        })

        viewModel.observableTopHeadlines
                .subscribe(
                        { newsResponse ->
                            Log.d(RESPONSE_DEBUG_TAG, "From viewmodel: ${newsResponse.articles.size} items")
                            adapter.addAndUpdateItems(newsResponse.articles) },
                        { throwable -> throwable.printStackTrace() }
                )
                .addTo(compositeDisposable)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SCROLL_PREV_TOTAL, endlessRecyclerOnScrollListener.previousTotal)
        outState.putBoolean(SCROLL_LOADING_STATE, endlessRecyclerOnScrollListener.loading)
    }

    override fun onDestroyView() {
        Log.d("INSET_CHECK", "onDestroyView()")
        super.onDestroyView()
        compositeDisposable.clear()
    }

    override fun onDestroy() {
        Log.d("INSET_CHECK", "onDestroy()")
        super.onDestroy()
    }

    override fun onListItemClick() {
        navigator.openNewsDetailFragment(true)
    }
}