package fr.jacques.loic.rssreader.activities;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

import fr.jacques.loic.rssreader.R;
import fr.jacques.loic.rssreader.model.Article;
import fr.jacques.loic.rssreader.utils.CacheHelper;

/**
 * Created by Doky on 04/10/2014.
 */
public class DetailsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_details, R.anim.slide_out_main);
        setContentView(R.layout.activity_details);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Article article;
            long id = bundle.getLong(Article.ARTICLE_ID, 0);
            if ((article = CacheHelper.getArticle(id)) != null) {
                ImageView imageView = (ImageView) findViewById(R.id.image);
                TextView title = (TextView) findViewById(R.id.title);
                TextView date = (TextView) findViewById(R.id.date);
                WebView description = (WebView) findViewById(R.id.description);


                if (!CacheHelper.getPic(this, id, imageView)) {
                    Picasso.with(this)
                            .load(Uri.parse(article.getImage()))
                            .into(imageView);
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE d MMM yyyy HH:mm", Locale.FRANCE);
                date.setText(dateFormat.format(article.getDate()));
                title.setText(article.getTitle());
                description.loadData(article.getDescription(), "text/html; charset=UTF-8", null);

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            overridePendingTransition(R.anim.slide_in_main, R.anim.slide_out_details);
        }
    }
}
