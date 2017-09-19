package com.linkvalue.rxkotlinrssreader.components

import android.arch.persistence.room.TypeConverter
import android.util.JsonReader
import android.util.JsonToken
import android.util.JsonWriter

import java.io.IOException
import java.text.DateFormat
import java.text.ParseException
import java.util.Date
import java.util.Locale

/**
 * Created by loic - LinkValue on 16/05/17. Specific DateTypeAdapter used only to save/restore profiles from
 * SharedPreferences. Inspired by https://github.com/google/gson/blob/master/gson/src/main/java/com/google/gson/internal/bind/DateTypeAdapter.java
 */

class DateTypeAdapter {

    private val enUsFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US)
    private val localFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT)


    @TypeConverter
    fun dateToString(date: Date): String {
        return localFormat.format(date)
    }


    @TypeConverter
    fun StringToDate(stringDate: String): Date {
        try {
            return localFormat.parse(stringDate)
        } catch (ignored: ParseException) {
        }

        try {
            return enUsFormat.parse(stringDate)
        } catch (ignored: ParseException) {
        }

        //Date has been saved with another locale/format. Should only happen with former profiles with bugged app. Let's retrun
        return Date()
    }
}
