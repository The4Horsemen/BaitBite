package com.example.android.logmahapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Loads a list of news by using an AsyncTaskLoader to perform the
 * network request to the given URL.
 */
public class DishLoader extends AsyncTaskLoader<List<Dish>> {

    /** Tag for log messages */
    private static final String LOG_TAG = OrderaNowLoader.class.getName();

    /** Query URL */
    private String queryUrl;

    /**
     * Constructs a new {@link DishLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public DishLoader(Context context, String url) {
        super(context);
        queryUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Dish> loadInBackground() {
        if (queryUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of dishes.
        List<Dish> dishes = DishQueryHelper.fetchNewData(queryUrl);
        return dishes;
    }
}
