package com.linkvalue.rxkotlinrssreader.model.dao

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverter
import android.arch.persistence.room.TypeConverters
import com.linkvalue.rxkotlinrssreader.components.DateTypeAdapter
import com.linkvalue.rxkotlinrssreader.model.article.Article

/**
 * Created by loic - LinkValue on 22/06/2017.
 */


@Database(entities = arrayOf(Article::class), version = 1)
@TypeConverters(DateTypeAdapter::class)
abstract class RSSReaderDatabase : RoomDatabase() {

    abstract fun articleDAO(): ArticleDAO
}