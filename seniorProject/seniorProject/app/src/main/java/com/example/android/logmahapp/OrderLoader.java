package com.example.android.logmahapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Loads a list of news by using an AsyncTaskLoader to perform the
 * network request to the given URL.
 */
public class OrderLoader extends AsyncTaskLoader<List<Order>> {

    /** Tag for log messages */
    private static final String LOG_TAG = OrderLoader.class.getName();

    /** Query URL */
    private String queryUrl;

    /**
     * Constructs a new {@link OrderLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public OrderLoader(Context context, String url) {
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
    public List<Order> loadInBackground() {
        if (queryUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of aOrders.
        List<Order> aOrders = QueryHelper.fetchNewData(queryUrl);
        return aOrders;
    }
}
