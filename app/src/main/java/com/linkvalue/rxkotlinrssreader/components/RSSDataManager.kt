package com.linkvalue.rxkotlinrssreader.components

import com.linkvalue.rxkotlinrssreader.RSSReaderApp
import com.linkvalue.rxkotlinrssreader.model.article.Article
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by loic - LinkValue on 23/06/2017.
 */


fun retrieveAllArticlesFromCache(): Flowable<List<Article>>? {

    return RSSReaderApp.database?.articleDAO()?.getAllArticles()
            ?.subscribeOn(Schedulers.newThread())
            ?.observeOn(AndroidSchedulers.mainThread())

}

fun addArticlesToCache(articles: List<Article>) {
    Observable.fromCallable {
        RSSReaderApp.database?.articleDAO()
                ?.addArticles(articles)
    }
            .subscribeOn(Schedulers.newThread())
            .subscribe()
}
