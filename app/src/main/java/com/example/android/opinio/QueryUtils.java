package com.example.android.opinio;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving article data from the Guardian.
 */
public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Guardian dataset and return a list of {@link Article} objects.
     */
    public static List<Article> fetchArticleData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Article}s
        List<Article> articles = extractDetailFromJson(jsonResponse);

        // Return the list of {@link Article}s
        return articles;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }


    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode() + ", " + url);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the article JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Article} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Article> extractDetailFromJson(String articleJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(articleJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding articles to
        List<Article> articles = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(articleJSON);

            // Extract the JSONObject associated with the key called "response",
            // which represents the response details.
            JSONObject responseObject = baseJsonResponse.getJSONObject("response");

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of results (or articles).
            JSONArray articleArray = responseObject.getJSONArray("results");

            // For each article in the articleArray, create an {@link Article} object
            for (int i = 0; i < articleArray.length(); i++) {

                // Get a single article at position i within the list of articles
                JSONObject currentArticle = articleArray.getJSONObject(i);

                // Extract the value for the key called "section"
                String section = currentArticle.getString("sectionName");

                // Extract the value for the key called "webPublicationDate"
                String date = currentArticle.getString("webPublicationDate");

                // Extract the value for the key called "webTitle"
                String title = currentArticle.getString("webTitle");

                // Extract the value for the key called "webUrl"
                String url = currentArticle.getString("webUrl");

                // Extract the JSONArray associated with the key called "tags",
                // which represents a list of tags including author name ("contributor") and keywords
                // for the given article.
                JSONArray tagArray = currentArticle.getJSONArray("tags");

                //For each tag in the tagArray, check if it returns a String that reads
                //"contributor" for the key called "type". If so, use the value
                //to update the author variable.
                String author = "";
                int occurrences = 0;
                for (int j = 0; j < tagArray.length(); j++) {

                    // Get a single tag at position j within the list of tags
                    JSONObject currentTag = tagArray.getJSONObject(j);

                    String type = currentTag.getString("type");

                    if (type.equals("contributor")) {
                        occurrences++;

                        if (occurrences == 1) {
                            // Extract the value for the key called "webTitle"
                            author = currentTag.getString("webTitle");
                        } else if (occurrences > 1) {
                            author = "Various journalists";
                        } else {
                            author = "Unknown";
                        }
                    }
                }

                //For each tag in the tagArray, check if it returns a String that reads
                //"keyword" for the key called "type". If so, use the value
                //to update the tagID variable.
                String tagId = "";
                for (int k = 0; k < tagArray.length(); k++) {

                    // Get a single tag at position k within the list of tags
                    JSONObject currentTag = tagArray.getJSONObject(k);

                    String type = currentTag.getString("type");

                    if (type.equals("keyword")) {
                        // Extract the value for the key called "sectionName"
                        tagId = currentTag.getString("sectionName");
                        break;
                    }
                }

                // Create a new {@link Article} object with the author, title, date,
                // url and tagId from the JSON response.
                Article article = new Article(author, section, title, date, url, tagId);

                // Add the new {@link Article} to the list of articles.
                articles.add(article);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the Guardian JSON results", e);
        }
        // Return the list of articles
        return articles;
    }
}