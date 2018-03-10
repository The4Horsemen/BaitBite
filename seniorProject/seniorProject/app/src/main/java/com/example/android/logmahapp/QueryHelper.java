package com.example.android.logmahapp;

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
 * Helper methods related to requesting and receiving new data.
 */
public final class QueryHelper {

    /** Tag for the log messages */
    private static final String LOG_TAG = QueryHelper.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryHelper} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryHelper (and an object instance of QueryHelper is not needed).
     */
    private QueryHelper() {
    }

    /**
     * Query the Guardian dataset and return a list of {@link Order} objects.
     */
    public static List<Order> fetchNewData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Order}s
        List<Order> aOrders = extractInfoFromJson(jsonResponse);

        // Return the list of {@link Order}s
        return aOrders;
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
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the new JSON results.", e);
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
     * Return a list of {@link Order} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Order> extractInfoFromJson(String newJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding ordersNowList to
        List<Order> ordersNowList = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject root = new JSONObject(newJSON);

            //Travers to the JSONObject associated with the key called "response"
            JSONObject response = root.getJSONObject("response");
            Log.i("response", "index= response");
            // Extract the JSONArray associated with the key called "results"
            JSONArray resultArray = response.getJSONArray("results");

            // For each new in the resultArray, create an {@link Order} object
            for (int i = 0; i < resultArray.length(); i++) {

                // Get a single aOrder at position i within the list of ordersNowList
                JSONObject currentNew = resultArray.getJSONObject(i);

                // Extract the value for the key called "webTitle"
                String articleTitle = currentNew.getString("webTitle");

                // Extract the value for the key called "sectionName"
                String sectionName = currentNew.getString("sectionName");

                // Extract the value for the key called "webUrl"
                String webURL = currentNew.getString("webUrl");

                // Extract the value for the key called "webPublicationDate"
                String webPublicationDate = currentNew.getString("webPublicationDate");

                //Travers to the JSONArray associated with the key called "tags"
                JSONArray tagArray = currentNew.getJSONArray("tags");

                String authorName = "";

                if(tagArray.length() == 0){
                    authorName = "By: No Name";
                }else{
                    for (int j = 0; j < tagArray.length(); j++) {

                        JSONObject jObjectTags = tagArray.getJSONObject(j);
                        String firstName = jObjectTags.getString("firstName");
                        String lastName = jObjectTags.getString("lastName");

                        if (firstName.equals("")) {
                            authorName = "By: " + lastName;
                        } else if (lastName.equals("")) {
                            authorName = "By: " + firstName;
                        } else {
                            authorName = "By: " + firstName + " " + lastName;
                        }
                    }
                }

                // Create a new {@link Order} object with the textView_OrderNowTitle, textView_OrderNowID, webURL,
                // and textView_OrderNowDate from the JSON response.
                Order aOrder = new Order(articleTitle, sectionName, authorName, webPublicationDate, webURL);

                // Add the new {@link Order} to the list of ordersNowList.
                ordersNowList.add(aOrder);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryHelper", "Problem parsing the new JSON results", e);
        }

        // Return the list of ordersNowList
        return ordersNowList;
    }

}
