package com.example.android.opinio;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

/**
 * Loads a list of articles by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class ArticleLoader extends android.content.AsyncTaskLoader<List<Article>> {

    /** Tag for log messages */
    private static final String LOG_TAG = ArticleLoader.class.getName();

    /** Query URL */
    private String mUrl;

    /**
     * Constructs a new {@link ArticleLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public ArticleLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Article> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<Article> articles = QueryUtils.fetchArticleData(mUrl);

        return articles;
    }
}
