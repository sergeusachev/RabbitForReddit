package com.example.serge.newsstand

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

private val DEBUG_TAG = MainActivity::class.java.simpleName

class MainActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val newsApi = NewsApi.create()

        newsApi.newsSourcesObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { response -> Log.d(DEBUG_TAG, "Response status: ${response.status}, response count: ${response.sources.size}") },
                        { throwable -> throwable.printStackTrace() }
                )
                .addTo(compositeDisposable)

        newsApi.newsTopHeadlinesObservable("ru", null, null, null, null, null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { response -> Log.d(DEBUG_TAG, response.articles.toString()) },
                        { throwable -> throwable.printStackTrace()}
                )
                .addTo(compositeDisposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
