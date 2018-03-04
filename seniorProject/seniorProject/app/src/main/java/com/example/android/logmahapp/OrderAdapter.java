package com.example.android.logmahapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * An {@link OrderAdapter} knows how to create a list item layout for each new
 * in the data source (a list of {@link Order} objects).
 *
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */
public class OrderAdapter extends ArrayAdapter<Order> {

    private static final String LOCATION_SEPARATOR = " of ";

    /**
     * Constructs a new {@link OrderAdapter}.
     *
     * @param context of the app
     * @param aOrders is the list of aOrders, which is the data source of the adapter
     */
    public OrderAdapter(Context context, List<Order> aOrders) {

        super(context, 0, aOrders);
    }

    /**
     * Returns a list item view that displays information about the earthquake at the given position
     * in the list of earthquakes.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.new_list_item, parent, false);
        }

        // Find the new at the given position in the list of news
        Order currentOrder = getItem(position);

        // Find the TextView with view ID articleTitle
        TextView articleTitleView = (TextView) listItemView.findViewById(R.id.articleTitle);
        // Display the articleTitle of the current new in that TextView
        articleTitleView.setText(currentOrder.getArticleTitle());

        // Find the TextView with view ID sectionName
        TextView sectionNameView = (TextView) listItemView.findViewById(R.id.sectionName);
        // Display the sectionName of the current new in that TextView
        sectionNameView.setText(currentOrder.getSectionName());

        // Find the TextView with view ID authorName
        TextView authorNameView = (TextView) listItemView.findViewById(R.id.authorName);
        // Display the authorName of the current new in that TextView
        authorNameView.setText(currentOrder.getAuthorName());

        // Find the TextView with view ID webPublicationDate
        TextView webPublicationDateView = (TextView) listItemView.findViewById(R.id.webPublicationDate);
        // Format the date string (i.e. "Mar 3, 1984")
        String formattedWebPublicationDateDate = formatDate(currentOrder.getWebPublicationDate());
        // Display the webPublicationDate of the current new in that TextView
        webPublicationDateView.setText(formattedWebPublicationDateDate);

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

    /**
     * Return the formatted date string
     */
    private String formatDate(String webPublicationDate) {
        String[] parts = webPublicationDate.split("T");
        return parts[0];
    }
}
