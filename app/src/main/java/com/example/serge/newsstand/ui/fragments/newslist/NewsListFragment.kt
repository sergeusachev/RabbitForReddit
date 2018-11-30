package com.example.serge.newsstand.ui.fragments.newslist

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.serge.newsstand.R
import com.example.serge.newsstand.navigation.Navigator
import com.example.serge.newsstand.utils.EndlessRecyclerOnScrollListener
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_news_list.*
import javax.inject.Inject

private val DEBUG_TAG = NewsListFragment::class.java.simpleName
val RESPONSE_DEBUG_TAG = "Response_debug_tag"

class NewsListFragment : Fragment(), NewsListAdapter.NewsAdapterItemClickListener {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var viewModelFactory: NewsListViewModelFactory

    private val compositeDisposable = CompositeDisposable()
    private val adapter = NewsListAdapter(this)

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        Log.d(DEBUG_TAG, "onAttach()")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.d(DEBUG_TAG, "onCreateView()")
        return inflater.inflate(R.layout.fragment_news_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(DEBUG_TAG, "onViewCreated()")
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(NewsListViewModel::class.java)

        recycler_news.layoutManager = LinearLayoutManager(activity)
        recycler_news.adapter = adapter

        viewModel.observableTopHeadlines
                .subscribe(
                        { newsResponse ->
                            Log.d(RESPONSE_DEBUG_TAG, "From viewmodel: ${newsResponse.articles.size} items")
                            adapter.addAndUpdateItems(newsResponse.articles) },
                        { throwable -> throwable.printStackTrace() }
                )
                .addTo(compositeDisposable)

        recycler_news.addOnScrollListener(object : EndlessRecyclerOnScrollListener() {
            override fun onLoadMore() {
                Log.d(RESPONSE_DEBUG_TAG, "onLoadMore()")
                viewModel.sendEvent()
            }

        })

    }

    override fun onDestroyView() {
        Log.d(DEBUG_TAG, "onDestroyView()")
        super.onDestroyView()
        compositeDisposable.clear()
    }

    override fun onDestroy() {
        Log.d(DEBUG_TAG, "onDestroy()")
        super.onDestroy()
    }

    override fun onListItemClick() {
        navigator.openNewsDetailFragment(true)
    }
}