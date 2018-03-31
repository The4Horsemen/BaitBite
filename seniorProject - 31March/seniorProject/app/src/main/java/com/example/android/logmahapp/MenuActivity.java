package com.example.android.logmahapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Dish>>, SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String LOG_TAG = MenuActivity.class.getName();

    /** URL for new data from the Guardian dataset */
    private static final String GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search?q=";

    /**
     * Constant value for the new loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEW_LOADER_ID = 1;

    /** Adapter for the list of news */
    private DishAdapter dishAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);

        // Find a reference to the {@link ListView} in the layout
        ListView dishListView = (ListView) findViewById(R.id.list_view);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        dishListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of news as input
        dishAdapter = new DishAdapter(this, new ArrayList<Dish>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        dishListView.setAdapter(dishAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected new.
        dishListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current new that was clicked on
                Dish currentDish = dishAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newUri = Uri.parse(currentDish.getWebUrl());

                // Create a new intent to view the new URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager conMan = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (netInfo != null && netInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEW_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.lost_internet_connection);
        }

        FloatingActionButton fabAddNewDish = (FloatingActionButton) findViewById(R.id.fab_add_new_dish);
        fabAddNewDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add new Dish", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent intent = new Intent(MenuActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public Loader<List<Dish>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String section = sharedPrefs.getString(getString(R.string.settings_filter_section_by_key), getString(R.string.settings_filter_section_by_default));

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key), getString(R.string.settings_order_by_default));

        // Append query parameter and its value
        uriBuilder.appendQueryParameter("", section);
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("page-size", "20");
        uriBuilder.appendQueryParameter("api-key", "89867c4d-5bcd-46d1-8205-d34fedd9d876");

        // Return the completed uri
        return new DishLoader(this, uriBuilder.toString().replace("&=", ""));
    }

    @Override
    public void onLoadFinished(Loader<List<Dish>> loader, List<Dish> dishes) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No Dishes found."
        mEmptyStateTextView.setText(R.string.no_dishes);
        
        // If there is a valid list of {@link OrderNow}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (dishes != null && !dishes.isEmpty()) {
            dishAdapter.addAll(dishes);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Dish>> loader) {
        // Loader reset, so we can clear out our existing data.
        dishAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }
}
