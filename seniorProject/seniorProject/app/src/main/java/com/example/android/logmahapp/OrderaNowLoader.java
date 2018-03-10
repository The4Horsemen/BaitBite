package com.example.android.logmahapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Loads a list of news by using an AsyncTaskLoader to perform the
 * network request to the given URL.
 */
public class OrderaNowLoader extends AsyncTaskLoader<List<OrderNow>> {

    /** Tag for log messages */
    private static final String LOG_TAG = OrderaNowLoader.class.getName();

    /** Query URL */
    private String queryUrl;

    /**
     * Constructs a new {@link OrderaNowLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public OrderaNowLoader(Context context, String url) {
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
    public List<OrderNow> loadInBackground() {
        if (queryUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of aOrderNows.
        List<OrderNow> aOrderNows = OrdersNowQueryHelper.fetchNewData(queryUrl);
        return aOrderNows;
    }
}
