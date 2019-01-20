package com.example.serge.newsstand.ui.fragments.newslist

import android.content.Context
import android.os.Bundle
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
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_news_list.*
import javax.inject.Inject

val MVI_DEBUG_TAG = "MVI_DEBUG_TAGG"
val DEBUG_TAG = NewsListFragment::class.java.simpleName
val RESPONSE_DEBUG_TAG = "Response_debug_tag"

class NewsListFragment : Fragment(),
        NewsListAdapter.NewsAdapterItemClickListener,
        MviView<MviAction> {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var viewModelFactory: NewsListViewModelFactory

    private lateinit var adapterObservable: Observable<Int>
    private val compositeDisposable = CompositeDisposable()
    private lateinit var viewModel: NewsListViewModel
    private val adapter = NewsListAdapter()

    override val viewActions: Observable<MviAction>
        get() = adapterObservable
                .distinctUntilChanged()
                .map { NewsListViewModel.UiAction.LoadMoreAction }


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

        adapterObservable = Observable.create<Int> { emitter ->
            adapter.loadPageListener = object : NewsListAdapter.NewsAdapterLoadPageListener {
                override fun onLoadNewPage(totalItemCount: Int) {
                    emitter.onNext(totalItemCount)
                }
            }
            if (savedInstanceState == null) emitter.onNext(0)
        }

        viewModel.getUiStateObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { uiState ->
                    render(uiState)
                }
                .addTo(compositeDisposable)

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

    private fun render(state: NewsListViewModel.UiState) {

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