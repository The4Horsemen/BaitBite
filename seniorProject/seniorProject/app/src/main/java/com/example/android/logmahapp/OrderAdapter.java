package com.example.android.logmahapp;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
     * Returns a list item view that displays information about the orderNow at the given position
     * in the list of ordersNow.
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.order_list_item, parent, false);
        }

        // Find the new at the given position in the list of news
        Order currentOrder = getItem(position);

        // Find the TextView with view ID textView_OrderNowTitle
        TextView textView_orderNowTitle = (TextView) listItemView.findViewById(R.id.textView_orderNow_title);
        // Display the textView_OrderNowTitle of the current new in that TextView
        textView_orderNowTitle.setText(currentOrder.getArticleTitle());

        // Find the TextView with view ID textView_OrderNowID
        TextView sectionNameView = (TextView) listItemView.findViewById(R.id.textView_orderNow_ID);
        // Display the textView_OrderNowID of the current new in that TextView
        sectionNameView.setText(currentOrder.getSectionName());

        // Find the TextView with view ID textView_orderNow_customerName
        TextView textView_orderNowBuyerName = (TextView) listItemView.findViewById(R.id.textView_orderNow_customerName);
        // Display the textView_orderNow_customerName of the current new in that TextView
        textView_orderNowBuyerName.setText(currentOrder.getAuthorName());

        // Find the TextView with view ID textView_OrderNowDate
        TextView textView_OrderNowDate = (TextView) listItemView.findViewById(R.id.textView_orderNow_date);
        // Format the date string (i.e. "Mar 3, 1984")
        String formattedWebPublicationDateDate = formatDate(currentOrder.getWebPublicationDate());
        // Display the textView_OrderNowDate of the current new in that TextView
        textView_OrderNowDate.setText(formattedWebPublicationDateDate);

        // Button for Finish order function to send a notification to the customer
        Button button_ordersNow_finish = (Button) listItemView.findViewById(R.id.button_orderNow_finish);
        button_ordersNow_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Send Notification to the customer "+position, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
