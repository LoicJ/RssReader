package fr.jacques.loic.rssreader.model;

import java.util.Date;

/**
 * Created by loic on 03/10/14.
 */
public class Article implements Comparable<Article> {

    public static final String ARTICLE_ID = "Id";
    private String title;
    private Date date;
    private String description;
    private String image;
    private long id;
    private boolean read;

    public Article(String title, Date date, String description, String image, long id, boolean read) {
        this.title = title;
        this.date = date;
        this.description = description;
        this.image = image;
        this.id = id;
        this.read = read;
    }

    public Article() {
        this.read = false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    @Override
    public int compareTo(Article another) {
        return another.getDate().compareTo(this.date);
    }


}
