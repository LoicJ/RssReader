package com.linkvalue.rxkotlinrssreader.components

import android.util.Log
import com.linkvalue.rxkotlinrssreader.model.article.Article
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jsoup.Jsoup.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by loic - LinkValue on 19/05/17.
 */

private val RSS_URL = "http://www.frandroid.com/feed"

fun loadNews(url: String): List<Article>? {
    val tempArticleList = ArrayList<Article>()

    try {
        //Getting Rss feed
        val feedUrl = URL(url)
        val conn = feedUrl.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.connect()
        val stream = conn.inputStream
        tempArticleList.addAll(parseRssFeed(stream));
        addArticlesToCache(tempArticleList)
        stream.close()
    } catch (e: IOException) {
        Log.e("Contacting server", "Error while connecting server")
        e.printStackTrace()
    } catch (xe: XmlPullParserException) {
        Log.e("Creating XmlParser", "Error while creating Xml Parser")
        xe.printStackTrace()
    } catch (ge: Exception) {
        Log.e("Loading news", "Unknown Error")
        ge.printStackTrace()
    } finally {

        return tempArticleList
    }
}


/**
 * Parse a rss feed stream and returns a list of Article

 * @param stream
 * *
 * @return List of Article contained in RSS feed. Empty list if rss feed is empty or bad formatted
 * *
 * @throws IOException
 * *
 * @throws XmlPullParserException
 */
fun parseRssFeed(stream: InputStream): List<Article> {

    val tempArticleList = ArrayList<Article>()
    val xmlPullParserFactory = XmlPullParserFactory.newInstance()
    val xmlPullParser = xmlPullParserFactory.newPullParser()

    xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
    xmlPullParser.setInput(stream, null)
    var article: Article? = Article()
    var content = ""
    var isInAnItem = false
    //Parsing Rss Feed
    var eventType = xmlPullParser.eventType
    while (eventType != XmlPullParser.END_DOCUMENT) {
        when (eventType) {
            XmlPullParser.START_TAG ->

                if (xmlPullParser.name == "item") {
                    article = Article()
                    isInAnItem = true
                }

            XmlPullParser.TEXT ->

                content = xmlPullParser.text

            XmlPullParser.END_TAG ->

                when (xmlPullParser.name) {
                    "item" -> {
                        if (article != null) {
                            tempArticleList.add(article)
//                        CacheHelper.add(article)
//                        dbManager.save(article)
                        }
                        isInAnItem = false
                    }

                    "title" -> {

//                    if (CacheHelper.contains(id)) {
//                        article = null

//                    } else if (article != null) {
                        article?.title = content
                        article?.id = content.hashCode().toLong()
//                    }
                    }

                    "pubDate" -> {
                        val dateFormat = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US)
                        try {
                            val date = dateFormat.parse(content)
                            article?.date = date
                        } catch (e: ParseException) { //If something is wrong with date, article will be ignored
                            e.printStackTrace()
                            article = null
                        }
                    }
                    "description" -> {
                        if (isInAnItem) { //As description is also used as whole rss doc description, we have to differentiate an item description from a doc description
                            article?.description = content
                        }
                    }

                    "content:encoded" -> {
                        val document = parse(content)
                        val img = document.select("img").first()
                        article?.image = img.attr("src")
                    }
                }
        }
        eventType = xmlPullParser.next()
    }

    return tempArticleList
}

fun retrieveAllArticlesFromRemote(): Observable<List<Article>?> {

    return Observable.fromCallable { loadNews(RSS_URL) }
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

}
