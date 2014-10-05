package fr.jacques.loic.rssreader.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by loic on 03/10/2014.
 */
public class ArticleDBManager extends SQLiteOpenHelper {

    private static ArticleDBManager sharedInstance;
    private SQLiteDatabase mDatabase;
    private Context context;
    private SQLiteStatement insertStatement;
    private SQLiteStatement readUpdateStatement;
    boolean isNewDB;

    private static final String COL_ID = "ID";
    private static final String COL_TITLE = "TITLE";
    private static final String COL_DATE = "ITEM_DATE";
    private static final String COL_IMAGE = "IMAGE";
    private static final String COL_DESCRIPTION = "DESCRIPTION";
    private static final String COL_IS_READ = "IS_READ";
    private static final String TABLE_NAME = "ARTICLE";

    private static final String CREATE_ARTICLE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
            "(" + COL_ID + " INTEGER PRIMARY KEY," + COL_TITLE + " TEXT," + COL_DATE + " INTEGER," +
            COL_IMAGE + " TEXT," + COL_DESCRIPTION + " TEXT," + COL_IS_READ + " INTEGER)";

    private static final String DB_NAME = "RssDB.db";
    private static final int DB_VERSION = 1;

    public ArticleDBManager(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        this.context.getDatabasePath(DB_NAME);
        isNewDB = false;
        mDatabase = getWritableDatabase();
        if (isNewDB) {
            close();
            this.context.getDatabasePath(DB_NAME).delete();
            mDatabase = getWritableDatabase();
        }
        insertStatement = mDatabase.compileStatement("INSERT INTO " + TABLE_NAME + " (" +
                COL_ID + "," +
                COL_TITLE + "," +
                COL_DATE + "," +
                COL_IMAGE + "," +
                COL_DESCRIPTION + "," +
                COL_IS_READ + ") " +
                "VALUES(?,?,?,?,?,?)");
        readUpdateStatement = mDatabase.compileStatement("UPDATE " + TABLE_NAME + "" +
                " SET " +
                COL_IS_READ + " = ? WHERE " +
                COL_ID + " = ? ");

    }

    public static ArticleDBManager getSharedInstance(Context context) {
        if (sharedInstance == null) {
            sharedInstance = new ArticleDBManager(context);
        }
        return sharedInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ARTICLE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            isNewDB = true;

        }
    }

    @Override
    public synchronized void close() {
        if (mDatabase != null) {
            mDatabase.close();
        }
        super.close();
    }


    /**
     * Requests section
     */

    public List<Article> getAllArticles() {
        List<Article> result = new ArrayList<Article>();
        String request = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_DATE + " DESC";
        Cursor cursor = mDatabase.rawQuery(request, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                Long id;
                String title, description, image;
                Date date;
                boolean read;
                do {
                    id = cursor.getLong(cursor.getColumnIndex(COL_ID));
                    title = cursor.getString(cursor.getColumnIndex(COL_TITLE));
                    date = new Date(cursor.getLong(cursor.getColumnIndex(COL_DATE)));
                    description = cursor.getString(cursor.getColumnIndex(COL_DESCRIPTION));
                    image = cursor.getString(cursor.getColumnIndex(COL_IMAGE));
                    read = cursor.getInt(cursor.getColumnIndex(COL_IS_READ)) == 0 ? false : true;
                    result.add(new Article(title, date, description, image, id, read));
                }
                while (cursor.moveToNext());
            }
        }

        return result;
    }

    public void save(Article article) {
        if (insertStatement != null) {
            insertStatement.bindLong(1, article.getId());
            insertStatement.bindString(2, article.getTitle());
            insertStatement.bindLong(3, article.getDate().getTime());
            insertStatement.bindString(4, article.getImage());
            insertStatement.bindString(5, article.getDescription());
            insertStatement.bindLong(6, article.isRead() ? 1 : 0);

            insertStatement.executeInsert();
            insertStatement.clearBindings();
        }
    }

    public void upDateReadStatus(Article article, boolean status) {
        if (readUpdateStatement != null) {
            readUpdateStatement.bindLong(1, status ? 1 : 0);
            readUpdateStatement.bindLong(2, article.getId());
            readUpdateStatement.execute();
            readUpdateStatement.clearBindings();
        }
    }
}

