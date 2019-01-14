package com.example.android.opinio;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * An {@link ArticleAdapter} knows how to create a list item layout for each article
 * in the data source (a list of {@link Article} objects).
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */
public class ArticleAdapter extends ArrayAdapter<Article> {

    /**
     * The part of the title string from the Guardian service that we use to split
     * off the title from the author ("Britain and food | John Smith").
     */
    private static final String TITLE_AUTHOR_SEPARATOR = " \\| ";

    /**
     * The character in the date string from the Guardian service that we use to split
     * off the time from the date proper ("2018-11-21T06:00:22Z").
     */
    private static final String DATE_TIME_SEPARATOR = "T";

    /**
     * The character in the date string from the Guardian service that we use to split
     * up the date into 3 parts ("2018-11-21").
     */
    private static final String DATE_SPLITTER = "-";


    /**
     * Constructs a new {@link ArticleAdapter}.
     *
     * @param context  of the app
     * @param articles is the list of articles, which is the data source of the adapter
     */
    public ArticleAdapter(Context context, List<Article> articles) {
        super(context, 0, articles);
    }


    /**
     * Returns a list item view that displays information about the article at the given position
     * in the list of articles.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.article_list_item, parent, false);
        }

        // Find the article at the given position in the list of articles
        Article currentArticle = getItem(position);


        // Save the author in the app preferences to use in a textview that is not one of the listviews
        String author = currentArticle.getAuthor();
        Context context = getContext();
        SharedPreferences authorPreferences = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = authorPreferences.edit();
        //save info about the article to authorPreferences
        editor.putString("author", author);
        editor.apply();

        // Find the TextView with view ID section
        TextView sectionView = listItemView.findViewById(R.id.section);
        // Get the section where the Guardian article appears
        String section = currentArticle.getSection();
        // Display the section of the current article in that TextView
        sectionView.setText(section);

        // Find the TextView with view ID title
        TextView titleView = listItemView.findViewById(R.id.title);
        // Get the title
        String titleAuthor = currentArticle.getTitle();
        String title;
        // Split the string into different parts (as an array of 2 Strings)
        // based on the " | " text, where
        // the first String will be "Britain and food" and the second String will be "George Monbiot".
        // We are only interested in the first string.
        String[] titleparts = titleAuthor.split(TITLE_AUTHOR_SEPARATOR);
        // set the title
        title = titleparts[0];
        // Display the title of the current article in that TextView
        titleView.setText(title);


        // Find the TextView with view ID date
        TextView dateView = listItemView.findViewById(R.id.date);
        // Get the date
        String dateTime = currentArticle.getDate();
        // Split the dateTime string into different parts (as an array of 2 Strings)
        // based on the "T" text, where
        // the first String will be "2018-11-21" and the second String will be "06:00:22Z".
        // We are only interested in the first string.
        String[] datepart = dateTime.split(DATE_TIME_SEPARATOR);
        // get the date part in full
        String fullDate = datepart[0];

        // Split the fullDate into different parts (as an array of 3 Strings)
        // based on the "-" character, where
        // the first String will be the year "2018", the second String will be the month "11" and the
        // third string will be the day "21".
        String[] splitDate = fullDate.split(DATE_SPLITTER);
        String year = splitDate[0];
        String month = splitDate[1];
        String day = splitDate[2];

        //convert the month from a number into a month name
        switch (month) {
            case "01":
                month = "Jan";
                break;
            case "02":
                month = "Feb";
                break;
            case "03":
                month = "Mar";
                break;
            case "04":
                month = "Apr";
                break;
            case "05":
                month = "May";
                break;
            case "06":
                month = "Jun";
                break;
            case "07":
                month = "Jul";
                break;
            case "08":
                month = "Aug";
                break;
            case "09":
                month = "Sept";
                break;
            case "10":
                month = "Oct";
                break;
            case "11":
                month = "Nov";
                break;
            case "12":
                month = "Dec";
                break;
        }

        String date = month + " " + day + ", " + year;
        // Display the date of the current article in that TextView
        dateView.setText(date);

        // Find the TextView with view ID tag_id
        TextView tagIdView = listItemView.findViewById(R.id.tag_id);
        // Get the tagId
        String tag = currentArticle.getTagId();
        // Display the author of the current article in that TextView
        tagIdView.setText(tag);

        // Find the TextView with view ID color_bar
        TextView verticalBarView = listItemView.findViewById(R.id.color_bar);

        //Set a vertical color that matches the color associated with the article's section
        verticalBarView.setBackgroundColor(getArticleColor(section));

        //Set a horizontal color that matches the color associated with the article's section
        sectionView.setBackgroundColor(getArticleColor(section));

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

    /**
     * Return the color for the vertical color bar for each article based on the section associated with the article.
     *
     * @param section of the article
     */
    private int getArticleColor(String section) {
        int articleColorResourceId;
        switch (section) {
            case "Business":
                articleColorResourceId = R.color.business;
                break;
            case "Opinion":
                articleColorResourceId = R.color.opinion;
                break;
            case "Film":
            case "Fashion":
            case "Stage":
            case "Books":
            case "Life and style":
            case "Music":
            case "Culture":
                articleColorResourceId = R.color.culture;
                break;
            case "Education":
                articleColorResourceId = R.color.education;
                break;
            case "Environment":
                articleColorResourceId = R.color.environment;
                break;
            case "Media":
                articleColorResourceId = R.color.media;
                break;
            case "Money":
                articleColorResourceId = R.color.money;
                break;
            case "Global":
            case "News":
                articleColorResourceId = R.color.news;
                break;
            case "Politics":
                articleColorResourceId = R.color.politics;
                break;
            case "Science":
                articleColorResourceId = R.color.science;
                break;
            case "Society":
                articleColorResourceId = R.color.society;
                break;
            case "Football":
            case "Sport":
                articleColorResourceId = R.color.sport;
                break;
            case "Technology":
                articleColorResourceId = R.color.technology;
                break;
            case "Travel":
                articleColorResourceId = R.color.travel;
                break;
            case "Television & radio":
                articleColorResourceId = R.color.televisionRadio;
                break;
            case "UK news":
                articleColorResourceId = R.color.ukNews;
                break;
            case "US news":
                articleColorResourceId = R.color.usNews;
                break;
            case "World news":
                articleColorResourceId = R.color.worldNews;
                break;
            default:
                articleColorResourceId = R.color.defaultCategory;
                break;
        }
        return ContextCompat.getColor(getContext(), articleColorResourceId);
    }
}