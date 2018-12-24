package com.example.serge.newsstand.ui.fragments.newslist

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.serge.newsstand.R
import com.example.serge.newsstand.navigation.Navigator
import com.example.serge.newsstand.utils.EndlessRecyclerOnScrollListener
import com.google.android.material.appbar.AppBarLayout
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_news_list.*
import javax.inject.Inject
import kotlin.math.abs

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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_news_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity!!.window.statusBarColor = Color.argb(40, 17, 17, 17)

        //TODO : need save status bar style state over device config changes
        if (savedInstanceState == null) {
            setStatusBarStyle(true)
        }

        list_fragment_root.setOnApplyWindowInsetsListener { v, insets ->
            (toolbar_list_fragment.layoutParams as LinearLayout.LayoutParams).topMargin = insets.systemWindowInsetTop
            insets
        }

        var isStatusBarLight = true

        val colorAnimatorDarker = ValueAnimator.ofInt(40, 150).apply {
            duration = 300
            addUpdateListener {
                activity!!.window.statusBarColor = Color.argb(animatedValue as Int, 17,17,17)
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}

                override fun onAnimationCancel(animation: Animator?) {}

                override fun onAnimationStart(animation: Animator?) {}

                override fun onAnimationEnd(animation: Animator?) { isStatusBarLight = false }

            })
        }

        val colorAnimatorLighter = ValueAnimator.ofInt(150, 40).apply {
            duration = 300
            addUpdateListener {
                activity!!.window.statusBarColor = Color.argb(animatedValue as Int, 17,17,17)
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}

                override fun onAnimationCancel(animation: Animator?) {}

                override fun onAnimationStart(animation: Animator?) {}

                override fun onAnimationEnd(animation: Animator?) { isStatusBarLight = true }

            })
        }

        app_bar_fragment_list.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) >= appBarLayout.height) {
                //TODO: status bar darker
                if (colorAnimatorLighter.isRunning) colorAnimatorLighter.cancel()

                if (isStatusBarLight) {
                    setStatusBarStyle(false)
                    colorAnimatorDarker.start() }
            } else {
                //TODO: status bar lighter
                if (colorAnimatorDarker.isRunning) colorAnimatorDarker.cancel()

                if (!isStatusBarLight) {
                    setStatusBarStyle(true)
                    colorAnimatorLighter.start() }
            }
        })

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
        super.onDestroyView()
        compositeDisposable.clear()
    }

    override fun onListItemClick() {
        navigator.openNewsDetailFragment(true)
    }

    private fun setStatusBarStyle(isDarker: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var uiFlags = activity!!.window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            uiFlags = if (isDarker) {
                uiFlags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                uiFlags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            activity!!.window.decorView.systemUiVisibility = uiFlags
        }

    }
}