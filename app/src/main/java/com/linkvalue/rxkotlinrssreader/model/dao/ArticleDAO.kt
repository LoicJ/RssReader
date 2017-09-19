package com.linkvalue.rxkotlinrssreader.model.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.linkvalue.rxkotlinrssreader.model.article.Article
import io.reactivex.Flowable

/**
 * Created by loic - LinkValue on 22/06/2017.
 */
@Dao
interface ArticleDAO {

    @Query("Select * FROM Articles")
    fun getAllArticles(): Flowable<List<Article>>


    @Insert(onConflict = REPLACE)
    fun addArticles(article: Article)

    @Insert(onConflict = REPLACE)
    fun addArticles(articles: List<Article>)

    @Delete
    fun deleteArticle(article: Article)


    @Query("Delete from Articles")
    fun deleteAllArticle()

}