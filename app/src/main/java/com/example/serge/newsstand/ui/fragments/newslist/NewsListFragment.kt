package com.example.serge.newsstand.ui.fragments.newslist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.serge.newsstand.R
import com.example.serge.newsstand.navigation.Navigator
import com.example.serge.newsstand.pagination.MviAction
import com.example.serge.newsstand.pagination.MviView
import com.example.serge.newsstand.ui.fragments.newslist.NewsListViewModel.UiAction
import com.example.serge.newsstand.utils.EndlessRecyclerOnScrollListener
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_news_list.*
import timber.log.Timber
import javax.inject.Inject

val MVI_DEBUG_TAG = "MVI_DEBUG_TAGG"
val DEBUG_TAG = NewsListFragment::class.java.simpleName
val RESPONSE_DEBUG_TAG = "Response_debug_tag"

class NewsListFragment : Fragment(), NewsListAdapter.NewsAdapterItemClickListener, MviView<MviAction, NewsListViewModel.UiState> {

    override val actions: Observable<MviAction>
        get() = scrollObservable

    companion object {
        const val SCROLL_PREV_TOTAL = "SCROLL_PREVIOUS_TOTAL_COUNT"
        const val SCROLL_LOADING_STATE = "SCROLL_LOADING_STATE"
    }

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var viewModelFactory: NewsListViewModelFactory

    private lateinit var viewModel: NewsListViewModel
    private val adapter = NewsListAdapter()

    /*private lateinit var loadMoreObservable: Observable<MviAction>
    private lateinit var loadMoreListener: LoadMoreListener*/

    private lateinit var endlessRecyclerOnScrollListener: EndlessRecyclerOnScrollListener
    private lateinit var scrollObservable: Observable<MviAction>

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_news_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(NewsListViewModel::class.java)
        recycler_news.adapter = adapter

        /* scrollObservable = Observable.create { emitter ->
             endlessRecyclerOnScrollListener = object : EndlessRecyclerOnScrollListener() {
                 override fun onLoadMore() {
                     emitter.onNext(UiAction.LoadMoreAction)
                 }
             }
             recycler_news.addOnScrollListener(endlessRecyclerOnScrollListener)
             emitter.setCancellable { recycler_news.removeOnScrollListener(endlessRecyclerOnScrollListener) }
             if (recycler_news.adapter?.itemCount == 0) emitter.onNext(UiAction.LoadMoreAction)
         }

         if (savedInstanceState != null) {
             val scrollPreviousTotal = savedInstanceState.getInt(SCROLL_PREV_TOTAL)
             val scrollLoadingState = savedInstanceState.getBoolean(SCROLL_LOADING_STATE)
             endlessRecyclerOnScrollListener.apply {
                 previousTotal = scrollPreviousTotal
                 loading = scrollLoadingState
             }
         }*/

        scrollObservable = Observable.create { emitter ->
            val scrollListener = object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val lastVisiblePosition = (recyclerView.layoutManager!! as LinearLayoutManager).findLastVisibleItemPosition()
                    val totalCount = recyclerView.adapter!!.itemCount
                    if (lastVisiblePosition >= totalCount - 4) emitter.onNext(UiAction.LoadMoreAction)
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                }
            }
            recycler_news.addOnScrollListener(scrollListener)
            emitter.setCancellable { recycler_news.removeOnScrollListener(scrollListener) }
            if (savedInstanceState == null && recycler_news.adapter!!.itemCount == 0) emitter.onNext(UiAction.LoadMoreAction)

        }
        recycler_news.layoutManager = LinearLayoutManager(activity)
        recycler_news.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                app_bar_fragment_list.isSelected = recyclerView.canScrollVertically(-1)
            }
        })

        viewModel.bind(this)
    }

    /*override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SCROLL_PREV_TOTAL, endlessRecyclerOnScrollListener.previousTotal)
        outState.putBoolean(SCROLL_LOADING_STATE, endlessRecyclerOnScrollListener.loading)
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.unbind()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onListItemClick() {
        navigator.openNewsDetailFragment(true)
    }

    override fun render(state: NewsListViewModel.UiState) {

        if (state.currentPage == 0 && state.loading) {
            //Full progress
            pb_full_progress.visibility = View.VISIBLE
        } else if (state.currentPage > 0 && state.loading) {
            //Page progress
            Toast.makeText(activity, "Page loading", Toast.LENGTH_SHORT).show()
        } else if (state.currentPage == 0 && state.error != null) {
            //Full error
            Toast.makeText(activity, "Full error", Toast.LENGTH_SHORT).show()
        } else if (state.currentPage > 0 && state.error != null) {
            //Page error
            Toast.makeText(activity, "Page error", Toast.LENGTH_SHORT).show()
        } else if (state.currentPage == 0 && !state.loading && state.data.isEmpty()) {
            //Empty view
            Toast.makeText(activity, "Empty view", Toast.LENGTH_SHORT).show()
        } else if (state.currentPage > 0 && !state.loading && state.data.isEmpty()) {
            //New page is empty
            Toast.makeText(activity, "New page is empty", Toast.LENGTH_SHORT).show()
        } else if (!state.loading && state.data.isNotEmpty()) {
            //Show data
            pb_full_progress.visibility = View.GONE
            recycler_news.visibility = View.VISIBLE
            adapter.addAndUpdateItems(state.data)
        }
    }


}