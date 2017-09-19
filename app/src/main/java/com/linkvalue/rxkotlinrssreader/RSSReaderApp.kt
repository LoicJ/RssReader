package com.linkvalue.rxkotlinrssreader

import android.app.Application
import android.arch.persistence.room.Room
import com.linkvalue.rxkotlinrssreader.model.dao.RSSReaderDatabase

/**
 * Created by loic - LinkValue on 23/06/2017.
 */

public class RSSReaderApp : Application() {
    override fun onCreate() {
        super.onCreate()
        RSSReaderApp.database = Room.databaseBuilder(this, RSSReaderDatabase::class.java, "articles.db").build();
    }


    companion object {
        var database: RSSReaderDatabase? = null
    }

}