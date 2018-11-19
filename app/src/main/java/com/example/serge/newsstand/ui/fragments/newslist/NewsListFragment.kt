package com.example.serge.newsstand.ui.fragments.newslist

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.serge.newsstand.R
import com.example.serge.newsstand.navigation.Navigator
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_news_list.*
import javax.inject.Inject

class NewsListFragment : Fragment(), NewsListAdapter.NewsAdapterItemClickListener {

    @Inject
    lateinit var navigator: Navigator

    private val adapter = NewsListAdapter(this)

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_news_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProviders.of(this).get(NewsListViewModel::class.java)

        recycler_news.layoutManager = LinearLayoutManager(activity)
        recycler_news.adapter = adapter
        adapter.setItems(listOf("aasas", "asddad", "sdadwedwed"))

    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onListItemClick(data: String) {
        navigator.openNewsDetailFragment(true)
    }
}