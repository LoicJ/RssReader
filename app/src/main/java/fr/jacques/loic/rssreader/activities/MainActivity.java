package fr.jacques.loic.rssreader.activities;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.jacques.loic.rssreader.R;
import fr.jacques.loic.rssreader.adapters.RssItemsAdapter;
import fr.jacques.loic.rssreader.model.Article;
import fr.jacques.loic.rssreader.model.ArticleDBManager;
import fr.jacques.loic.rssreader.utils.CacheHelper;

public class MainActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener, RssItemsAdapter.OnItemClickListener {

    private static final String RSS_URL = "http://feedpress.me/frandroid";

    private RecyclerView mRecyclerView;
    private RssItemsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout mPullToRefreshLayout;
    private List<Article> articleList;
    private ArticleDBManager dbManager;
    private View lastViewClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.L) {
            getWindow().setExitTransition(new Explode());

        }
        mRecyclerView = (RecyclerView) findViewById(R.id.rss_items);
        mPullToRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.container);
        mPullToRefreshLayout.setOnRefreshListener(this);
        //mPullToRefreshLayout.setColorSchemeResources()

        articleList = new ArrayList<Article>();
        dbManager = new ArticleDBManager(this);
        articleList = CacheHelper.loadCache(this); //First, let's load articles from cache DB
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RssItemsAdapter(articleList, this, dbManager);
        mAdapter.setItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        LoadNews loadNews = new LoadNews(); //Then we load articles from the internetS
        loadNews.execute();

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (lastViewClicked != null) {
            CardView cardview = (CardView) lastViewClicked.findViewById(R.id.card_view);
            if (cardview.getAlpha() != RssItemsAdapter.IS_READ_ALPHA) {
                ObjectAnimator anim = ObjectAnimator.ofFloat(cardview, "alpha", 1f, 0.5f);
                anim.setDuration(1000);
                anim.start();
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                lastViewClicked.findViewById(R.id.article_pic).setViewName(null);
                lastViewClicked.findViewById(R.id.article_title).setViewName(null);
            }

            lastViewClicked = null;
        }

    }

    @Override
    public void onRefresh() {
        LoadNews loadNews = new LoadNews();
        loadNews.execute();
    }


    @Override
    public void onItemClick(View view, int position) {
        Article clickedArticle = articleList.get(position);
        if (!clickedArticle.isRead()) {
            clickedArticle.setRead(true);
            dbManager.upDateReadStatus(clickedArticle, true);
        }

        lastViewClicked = view;
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(Article.ARTICLE_ID, clickedArticle.getId());
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                    Pair.create(view.findViewById(R.id.article_pic), "articlePic"),
                    Pair.create(view.findViewById(R.id.article_title), "articleTitle"));
            startActivity(intent, options.toBundle());
        }
        else{
            startActivity(intent);
        }
    }

    /**
     * Everything is in the title. Asynctask charged of loading and parsing Rss feed content from the internet
     */
    private class LoadNews extends AsyncTask<Void, Void, List<Article>> {

        @Override
        protected List<Article> doInBackground(Void... voids) {
            List<Article> tempArticleList = null;

            try {
                //Getting Rss feed
                URL url = new URL(RSS_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                InputStream stream = conn.getInputStream();

                tempArticleList = parseRssFeed(stream);

                stream.close();
            } catch (
                    IOException e
                    )

            {
                Log.e("Contacting server", "Error while connecting server");
                e.printStackTrace();
            } catch (
                    XmlPullParserException xe
                    )

            {
                Log.e("Creating XmlParser", "Error while creating Xml Parser");
                xe.printStackTrace();
            } finally {

                return tempArticleList;

            }
        }

        @Override
        protected void onPostExecute(List<Article> tempArticleList) {
            if (mPullToRefreshLayout.isRefreshing()) {
                mPullToRefreshLayout.setRefreshing(false);
            }
            if (tempArticleList != null && !tempArticleList.isEmpty()) {
                articleList.addAll(0, tempArticleList);
                Collections.sort(articleList);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Parse a rss feed stream and returns a list of Article
     *
     * @param stream
     * @return List of Article contained in RSS feed. Empty list if rss feed is empty or bad formatted
     * @throws IOException
     * @throws XmlPullParserException
     */
    private List<Article> parseRssFeed(InputStream stream) throws IOException, XmlPullParserException {

        List<Article> tempArticleList = new ArrayList<Article>();
        XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
        XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();

        xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        xmlPullParser.setInput(stream, null);
        Article article = new Article();
        String content = "";
        boolean isInAnItem = false;
        //Parsing Rss Feed
        int eventType = xmlPullParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (xmlPullParser.getName().equals("item")) {
                        article = new Article();
                        isInAnItem = true;
                    }
                    break;

                case XmlPullParser.TEXT:
                    content = xmlPullParser.getText();
                    break;

                case XmlPullParser.END_TAG:
                    if (xmlPullParser.getName().equals("item")) {
                        if (article != null) {
                            tempArticleList.add(article);
                            CacheHelper.add(article);
                            dbManager.save(article);
                        }
                        isInAnItem = false;
                    } else if (xmlPullParser.getName().equals("title")) {
                        long id = content.hashCode();
                        if (CacheHelper.contains(id)) {
                            article = null;
                            break;

                        } else if (article != null) {
                            article.setTitle(content);
                            article.setId(content.hashCode());
                        }


                    } else if (xmlPullParser.getName().equals("pubDate")) {
                        if (article != null) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
                            try {
                                Date date = dateFormat.parse(content);
                                article.setDate(date);
                            } catch (ParseException e) { //If something is wrong with date, article will be ignored
                                e.printStackTrace();
                                article = null;
                                break;
                            }
                        }

                    } else if (xmlPullParser.getName().equals("description")) {
                        if (isInAnItem && article != null) { //As description is also used as whole rss doc description, we have to differentiate an item description from a doc description
                            article.setDescription(content);
                        }

                    } else if (xmlPullParser.getName().equals("content:encoded")) {
                        if (article != null) {
                            Document document = Jsoup.parse(content);
                            Element img = document.select("img").first();
                            article.setImage(img.attr("src"));
                        }
                    }
                    break;

                default:
                    break;
            }
            eventType = xmlPullParser.next();
        }

        return tempArticleList;
    }
}
