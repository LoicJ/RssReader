package fr.jacques.loic.rssreader.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.LongSparseArray;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import fr.jacques.loic.rssreader.model.Article;
import fr.jacques.loic.rssreader.model.ArticleDBManager;

/**
 * Created by loic on 03/10/2014.
 */
public class CacheHelper {

    private static LongSparseArray<Article> itemcache;
    public static final int BUFFER_SIZE = 1024 * 8;

    public static void saveArticle(Article article, int id) {

    }

    public static void savePic(Context context, Bitmap picture, long id) {
        File file = new File(context.getFilesDir().getPath() + "/" + String.valueOf(id) + ".jpg");
        if (!file.exists()) {
            SavePicToDiskTask saveTask = new SavePicToDiskTask(picture, file);
            saveTask.execute();
        }
    }

    public static boolean getPic(Context context, long id, ImageView imageView) {
        File file = new File(context.getFilesDir().getPath() + "/" + String.valueOf(id) + ".jpg");
        if (file.exists()) {

            Picasso.with(context)
                    .load(file)
                    .into(imageView);

            return true;

        } else {
            return false;
        }
    }

    public static List<Article> loadCache(Context context) {
        itemcache = new LongSparseArray<Article>();
        List<Article> list = ArticleDBManager.getSharedInstance(context).getAllArticles();
        for (Article article : list) {
            itemcache.put(article.getId(), article);
        }

        return list;
    }

    private static class SavePicToDiskTask extends AsyncTask<Void, Void, Void> {

        private Bitmap picture;
        private File file;

        private SavePicToDiskTask(Bitmap picture, File file) {
            this.picture = picture;
            this.file = file;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                final BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER_SIZE);
                picture.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static boolean contains(long id) {
        if (itemcache != null && itemcache.get(id) != null) {
            return true;
        } else {
            return false;
        }

    }

    public static void add(Article article) {
        if (itemcache == null) {
            itemcache = new LongSparseArray<Article>();
        }
        itemcache.put(article.getId(), article);
    }

    public static Article getArticle(long id) {
        if (itemcache == null || itemcache.get(id) == null) {
            return null;
        } else {
            return itemcache.get(id);
        }
    }
}
