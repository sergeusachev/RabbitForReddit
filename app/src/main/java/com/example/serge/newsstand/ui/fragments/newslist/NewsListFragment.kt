package com.example.serge.newsstand.ui.fragments.newslist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.serge.newsstand.R
import com.example.serge.newsstand.navigation.Navigator
import com.example.serge.newsstand.pagination.MviAction
import com.example.serge.newsstand.pagination.MviView
import com.example.serge.newsstand.ui.fragments.newslist.adapter.NewsListAdapter
import com.example.serge.newsstand.ui.fragments.newslist.viewmodel.NewsListViewModel
import com.example.serge.newsstand.ui.fragments.newslist.viewmodel.NewsListViewModelFactory
import com.example.serge.newsstand.utils.EndlessRecyclerOnScrollListener
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_news_list.*
import javax.inject.Inject

val MVI_DEBUG_TAG = "MVI_DEBUG_TAGG"

class NewsListFragment : Fragment(),
        NewsListAdapter.NewsAdapterItemClickListener,
        MviView
{

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var viewModelFactory: NewsListViewModelFactory

    override val scrollObservable: Observable<Int>
        get() = getScrollObservable(recycler_news, 5)

    override val swipeRefreshObservable: Observable<MviAction>
        get() = getSwipeRefreshObservable(swipeRefresh_newslist)


    private val compositeDisposable = CompositeDisposable()
    private lateinit var viewModel: NewsListViewModel
    private val adapter = NewsListAdapter()

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
        initRecycler()
        viewModel.bindView(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
        viewModel.unbindView()
    }

    override fun render(state: NewsListViewModel.UiState) {

        if (state.data.isNotEmpty()) {
            adapter.addAndUpdateItems(state.data)
        }

        if (state.lastLoadedPage == 0 && state.loading) {
            //Full progress
        } else if (state.lastLoadedPage == 0 && state.error != null) {
            //Full error
        } else if (state.lastLoadedPage == 1 && state.data.isEmpty()) {
            //Empty view
        }  else if (state.lastLoadedPage > 0 && state.loading) {
            //Page loading
        } else if (state.lastLoadedPage == state.pageForLoad && state.lastLoadedPage > 1) {
            //Page empty
        } else if (state.lastLoadedPage > 0 && state.error != null) {
            //Page error
        }
    }

    override fun onListItemClick() {
        navigator.openNewsDetailFragment(true)
    }

    private fun initRecycler() {
        recycler_news.adapter = adapter
        recycler_news.layoutManager = LinearLayoutManager(activity)
        recycler_news.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                app_bar_fragment_list.isSelected = recyclerView.canScrollVertically(-1)
            }
        })
    }

    private fun getScrollObservable(recylcerView: RecyclerView, threshold: Int): Observable<Int> {
        return Observable.create<Int> { emitter ->
            val scrollListener = EndlessRecyclerOnScrollListener(
                    recylcerView.layoutManager as LinearLayoutManager,
                    threshold) { totalItems -> emitter.onNext(totalItems) }
            recylcerView.addOnScrollListener(scrollListener)
            emitter.setCancellable { recylcerView.removeOnScrollListener(scrollListener) }
        }
    }

    private fun getSwipeRefreshObservable(swipeRefreshLayout: SwipeRefreshLayout): Observable<MviAction> {
        return Observable.create { emitter ->
            val swipeRefreshListener = SwipeRefreshLayout.OnRefreshListener {
                emitter.onNext(InputAction.RefreshData)
            }
            swipeRefreshLayout.setOnRefreshListener(swipeRefreshListener)
            emitter.setCancellable { swipeRefreshLayout.setOnRefreshListener(null) }
        }
    }
}