package com.example.android.baitbite.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.android.baitbite.Model.Chef;
import com.example.android.baitbite.Model.Customer;

public class Common {
    public static Customer currentCustomer;
    public static String chefId;
    public static Chef currentChef;

    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";
    public static final String CUSTOMER_KEY = "Customer";

    public static final String CHEF_KEY = "Chef";

    public static final int PICK_IMAGE_REQUEST = 71;

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

    public static String convertCodeToStatus(String code){
        if(code.equals("0")){
            return "Placed";
        }else if(code.equals("1")){
            return "Preparing";
        }else{
            return "Ready to pickup";
        }
    }


}
