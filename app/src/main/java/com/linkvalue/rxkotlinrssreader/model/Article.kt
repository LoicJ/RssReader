package com.linkvalue.rxkotlinrssreader.model

import java.util.Date

/**
 * Created by loic on 03/10/14.
 */
//class Article (private var title: String, private var date: Date, private var description: String, private var image: String,
//               private var id: Long = 0, private var isRead: Boolean = false): Comparable<Article> {

class Article() : Comparable<Article> {

    val ARTICLE_ID = "Id"
    var title: String? = null
    var date: Date? = null
    var description: String? = null
    var image: String? = null
    var id: Long = 0
    var isRead: Boolean = false

    override fun compareTo(other: Article): Int {
        return other.date!!.compareTo(this.date)
    }

    constructor(title: String, date: Date, description: String, image: String, id: Long, read: Boolean) : this() {
        this.title = title
        this.date = date
        this.description = description
        this.image = image
        this.id = id
        this.isRead = read
    }

}
