package com.linkvalue.rxkotlinrssreader.ui

import android.support.v7.widget.LinearLayoutManager
import com.linkvalue.rxkotlinrssreader.adapters.RssItemsAdapter
import com.linkvalue.rxkotlinrssreader.components.retrieveAllArticlesFromCache
import com.linkvalue.rxkotlinrssreader.components.retrieveAllArticlesFromRemote
import com.linkvalue.rxkotlinrssreader.model.article.Article
import java.util.*

class MainActivity : android.app.Activity(), com.linkvalue.rxkotlinrssreader.adapters.RssItemsAdapter.OnItemClickListener {

    private var mSubscription: io.reactivex.disposables.Disposable? = null
    private var mArticleList: MutableList<com.linkvalue.rxkotlinrssreader.model.article.Article> = kotlin.collections.ArrayList<Article>()

//    private var mRecyclerView: RecyclerView? = null
//    private var mPullToRefreshLayout : SwipeRefreshLayout? = null

    private var mSwipeRefreshLayout: android.support.v4.widget.SwipeRefreshLayout? = null
    private var mAdapter: com.linkvalue.rxkotlinrssreader.adapters.RssItemsAdapter? = null

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.linkvalue.rxkotlinrssreader.R.layout.activity_main)

        val mRecyclerView = findViewById(com.linkvalue.rxkotlinrssreader.R.id.rss_items) as android.support.v7.widget.RecyclerView
        mSwipeRefreshLayout = findViewById(com.linkvalue.rxkotlinrssreader.R.id.container) as android.support.v4.widget.SwipeRefreshLayout
        mSwipeRefreshLayout!!.setOnRefreshListener {
            loadRSSData()
        }


        //First, let's load articles from cache DB

        val observable = retrieveAllArticlesFromCache()
        observable?.subscribe { listArticles -> refreshArticles(listArticles) }

        val mLayoutManager = LinearLayoutManager(this)
        mRecyclerView.setLayoutManager(mLayoutManager)
        mAdapter = RssItemsAdapter(mArticleList, this)
        mAdapter!!.setItemClickListener(this)
        mRecyclerView.setAdapter(mAdapter)
        loadRSSData()

    }

    override fun onDestroy() {
        super.onDestroy()
        mSubscription?.dispose()
    }

    private fun loadRSSData() {
        mSwipeRefreshLayout?.setRefreshing(true)
        val observable = retrieveAllArticlesFromRemote()
        mSubscription = observable.subscribe({
            articles ->
            refreshArticles(articles)

        })

    }


    private fun refreshArticles(articles: List<Article>?) {
        mSwipeRefreshLayout?.setRefreshing(false)

        if (articles != null && !articles.isEmpty()) {
            mArticleList.addAll(0, articles)
            java.util.Collections.sort(mArticleList)
            mAdapter?.notifyDataSetChanged()
        }
    }

    override fun onItemClick(view: android.view.View, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
