package com.linkvalue.rxkotlinrssreader

import android.app.Activity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.linkvalue.rxkotlinrssreader.adapters.RssItemsAdapter
import com.linkvalue.rxkotlinrssreader.model.Article
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers.newThread
import org.reactivestreams.Subscription
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : Activity(), RssItemsAdapter.OnItemClickListener{

    private val RSS_URL = "http://www.frandroid.com/feed"
    private var mSubscription: Disposable? = null
    private var mArticleList: MutableList<Article> = ArrayList<Article>()

//    private var mRecyclerView: RecyclerView? = null
//    private var mPullToRefreshLayout : SwipeRefreshLayout? = null

    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    private var mAdapter: RssItemsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mRecyclerView = findViewById(R.id.rss_items) as RecyclerView
        mSwipeRefreshLayout = findViewById(R.id.container) as SwipeRefreshLayout
        mSwipeRefreshLayout!!.setOnRefreshListener {
            loadRSSData()
        }


//        mArticleList = CacheHelper.loadCache(this) //First, let's load articles from cache DB
        var mLayoutManager = LinearLayoutManager(this)
        mRecyclerView.setLayoutManager(mLayoutManager)
        mAdapter = RssItemsAdapter(mArticleList, this)
        mAdapter!!.setItemClickListener(this)
        mRecyclerView.setAdapter(mAdapter)
//        val loadNews = LoadNews() //Then we load articles from the internetS
//        loadNews.execute()
        loadRSSData()

    }

    override fun onDestroy() {
        super.onDestroy()
        mSubscription?.dispose()
    }

    private fun loadRSSData() {
        mSwipeRefreshLayout?.setRefreshing(true)
        var observable: Observable<List<Article>?> = Observable.fromCallable { loadNews(RSS_URL) }
                .subscribeOn(newThread())
                .observeOn(AndroidSchedulers.mainThread());

        mSubscription = observable.subscribe({
            articles ->
            refreshArticles(articles)
        })

    }


    private fun refreshArticles(articles: List<Article>?) {
        mSwipeRefreshLayout?.setRefreshing(false)

        if (articles != null && !articles.isEmpty()) {
            mArticleList.addAll(0, articles)
            Collections.sort(mArticleList)
            mAdapter?.notifyDataSetChanged()
        }
    }

    override fun onItemClick(view: View, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
