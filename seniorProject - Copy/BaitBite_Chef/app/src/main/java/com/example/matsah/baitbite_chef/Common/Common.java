package com.example.matsah.baitbite_chef.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.matsah.baitbite_chef.Model.Chef;
import com.example.matsah.baitbite_chef.Model.Dish;

import java.util.List;

/**
 * Created by MATSAH on 3/29/2018.
 */

public class Common {
    public static Chef currentChef;
    public static List<Dish> dishes;

    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";

    public static final String CHEF_KEY = "Chef";

    public static final int PICK_IMAGE_REQUEST = 71;

    //Check Internet Connection
    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null){
            NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
            if(networkInfos != null){
                for(int i = 0; i < networkInfos.length; i++){
                    if(networkInfos[i].getState() == NetworkInfo.State.CONNECTED){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
