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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.serge.newsstand.R
import com.example.serge.newsstand.navigation.Navigator
import com.example.serge.newsstand.pagination.MviAction
import com.example.serge.newsstand.pagination.MviView
import com.example.serge.newsstand.pagination.getScrollObservable
import com.example.serge.newsstand.ui.fragments.newslist.adapter.NewsListAdapter
import com.example.serge.newsstand.ui.fragments.newslist.viewmodel.NewsListViewModel
import com.example.serge.newsstand.ui.fragments.newslist.viewmodel.NewsListViewModelFactory
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.withLatestFrom
import kotlinx.android.synthetic.main.fragment_news_list.*
import javax.inject.Inject

val MVI_DEBUG_TAG = "MVI_DEBUG_TAGG"

class NewsListFragment : Fragment(),
        NewsListAdapter.NewsAdapterItemClickListener,
        MviView<MviAction> {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var viewModelFactory: NewsListViewModelFactory

    private lateinit var scrollObservable: Observable<MviAction>
    private lateinit var swipeRefreshObservable: Observable<MviAction>

    private val compositeDisposable = CompositeDisposable()
    private lateinit var viewModel: NewsListViewModel
    private val adapter = NewsListAdapter()

    override val viewActions: Observable<MviAction>
        get() = Observable.merge(scrollObservable, swipeRefreshObservable)


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

        scrollObservable = getScrollObservable(recycler_news, 5)
                .distinctUntilChanged()
                .withLatestFrom(viewModel.getUiStateObservable())
                .filter { pairCountState -> !pairCountState.second.loading }
                .filter { it.second.pageForLoad > it.second.lastLoadedPage }
                .map { InputAction.LoadMoreAction }

        swipeRefreshObservable = Observable.create { emitter ->
            val swipeRefreshListener = SwipeRefreshLayout.OnRefreshListener {
                emitter.onNext(InputAction.RefreshData)
            }
            swipeRefresh_newslist.setOnRefreshListener(swipeRefreshListener)
            emitter.setCancellable { swipeRefresh_newslist.setOnRefreshListener(null) }
        }



        viewModel.bindView(this)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
        viewModel.unbindView()
    }

    override fun onDestroy() {
        super.onDestroy()
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
}