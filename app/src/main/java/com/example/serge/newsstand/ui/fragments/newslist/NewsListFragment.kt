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
import com.example.serge.newsstand.pagination.getScrollObservable
import com.example.serge.newsstand.ui.fragments.newslist.adapter.NewsListAdapter
import com.example.serge.newsstand.ui.fragments.newslist.viewmodel.NewsListViewModel
import com.example.serge.newsstand.ui.fragments.newslist.viewmodel.NewsListViewModelFactory
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_news_list.*
import javax.inject.Inject

val MVI_DEBUG_TAG = "MVI_DEBUG_TAGG"

class NewsListFragment : Fragment(),
        NewsListAdapter.NewsAdapterItemClickListener {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var viewModelFactory: NewsListViewModelFactory

    private lateinit var scrollObservable: Observable<Int>
    private lateinit var swipeRefreshObservable: Observable<MviAction>

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

        scrollObservable = getScrollObservable(recycler_news, 5)

        swipeRefreshObservable = Observable.create { emitter ->
            val swipeRefreshListener = SwipeRefreshLayout.OnRefreshListener {
                emitter.onNext(InputAction.RefreshData)
            }
            swipeRefresh_newslist.setOnRefreshListener(swipeRefreshListener)
            emitter.setCancellable { swipeRefresh_newslist.setOnRefreshListener(null) }
        }

        viewModel.fullProgressObservable()
                .doOnNext { Log.d("SIDE_EFF", "Effect: $it") }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.show) {
                        recycler_news.visibility = View.GONE
                        pb_full_progress.visibility = View.VISIBLE
                    } else {
                        recycler_news.visibility = View.VISIBLE
                        pb_full_progress.visibility = View.GONE
                    }
                }.addTo(compositeDisposable)


        viewModel.pageProgressObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            if (it.show) {
                                Toast.makeText(activity!!, "PageProgress SHOW", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(activity!!, "PageProgress HIDE", Toast.LENGTH_SHORT).show()
                            }
                        },
                        { it.printStackTrace() }
                ).addTo(compositeDisposable)

        viewModel.fullErrorObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            if (it.show) {
                                Toast.makeText(activity!!, "FullError SHOW", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(activity!!, "FullError HIDE", Toast.LENGTH_SHORT).show()
                            }
                        },
                        { it.printStackTrace() }
                ).addTo(compositeDisposable)

        viewModel.pageErrorObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            if (it.show) {
                                Toast.makeText(activity!!, "PageError SHOW", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(activity!!, "PageError HIDE", Toast.LENGTH_SHORT).show()
                            }
                        },
                        { it.printStackTrace() }
                ).addTo(compositeDisposable)

        viewModel.emptyViewObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            if (it.show) {
                                Toast.makeText(activity!!, "EmptyView SHOW", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(activity!!, "EmptyView HIDE", Toast.LENGTH_SHORT).show()
                            }
                        },
                        { it.printStackTrace() }
                ).addTo(compositeDisposable)

        viewModel.emptyPageObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            if (it.show) {
                                Toast.makeText(activity!!, "EmptyPage SHOW", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(activity!!, "EmptyPage HIDE", Toast.LENGTH_SHORT).show()
                            }
                        },
                        { it.printStackTrace() }
                ).addTo(compositeDisposable)

        viewModel.dataObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            adapter.addAndUpdateItems(it.items)
                        },
                        { it.printStackTrace() }
                ).addTo(compositeDisposable)

        if(savedInstanceState == null) {
            viewModel.bindView(scrollObservable, swipeRefreshObservable, true)
        } else {
            viewModel.bindView(scrollObservable, swipeRefreshObservable, false)
        }

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