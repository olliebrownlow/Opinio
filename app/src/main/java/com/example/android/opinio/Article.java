package com.example.android.opinio;

/**
 * An {@link Article} object contains information related to a single article.
 */
public class Article {

    /** Author of the article */
    private String mAuthor;

    /** Section name for the article */
    private String mSection;

    /** Title of the article */
    private String mTitle;

    /** Date of the article */
    private String mDate;

    /** Website URL of the article */
    private String mUrl;

    /** First tag id for the article */
    private String mTagId;


    /**
     * Constructs a new {@link Article} object.
     *
     * @param author is the writer of the article
     * @param section is the section in the guardian in which the article appears
     * @param title is the title of the article
     * @param date is the date when the article was put online
     * @param url is the website URL to read the article
     * @param tagId is the most relevant topic area of the article (taken from the first
     *                keyword tagged to the article
     */
    public Article(String author, String section, String title, String date, String url, String tagId) {
        mAuthor = author;
        mSection = section;
        mTitle = title;
        mDate = date;
        mUrl = url;
        mTagId = tagId;
    }

    /**
     * Returns the author of the article.
     */
    public String getAuthor() {
        return mAuthor;
    }

    /**
     * Returns the author of the article.
     */
    public String getSection() {
        return mSection;
    }

    /**
     * Returns the title of the article.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns the date of the article.
     */
    public String getDate() {
        return mDate;
    }

    /**
     * Returns the website URL for the article.
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * Returns the topic area for the article.
     */
    public String getTagId() {
        return mTagId;
    }
}