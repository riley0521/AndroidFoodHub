package com.example.riley.androidfoodhub.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;

import com.example.riley.androidfoodhub.Model.Request;
import com.example.riley.androidfoodhub.Model.User;
import com.example.riley.androidfoodhub.Remote.APIService;
import com.example.riley.androidfoodhub.Remote.IGoogleService;
import com.example.riley.androidfoodhub.Remote.RetrofitClient;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by riley on 12/11/2017.
 */

public class Common {
    public static String PHONE_TEXT = "userPhone";
    public static String topicName = "News";
    public static User currentUser;
    public static Request currentRequest;

    public static String restaurantSelected="";
    public static final String INTENT_FOOD_ID = "FoodId";
    private static final String BASE_URL = "https://fcm.googleapis.com/";
    private static final String GOOGLE_API_URL = "https://maps.googleapis.com/";
    public static String currentKey;

    public static APIService getFCMService() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static IGoogleService getGoogleMapAPI() {
        return RetrofitClient.getGoogleClient(GOOGLE_API_URL).create(IGoogleService.class);
    }

    public static String convertCodeToStatus(String code) {
        if(code.equals("0"))
            return "Placed";
        else if(code.equals("1"))
            return "On the way";
        else if(code.equals("2"))
            return "Shipping";
        else
            return "Shipped";
    }


    public static final String DELETE = "Delete";
    public static final String USER_KEY = "user";
    public static final String PS_KEY = "psw";

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if(info != null) {
                for(int i=0;i<info.length;i++) {
                    if(info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

    public static BigDecimal formatCurrency(String amount, Locale locale) throws java.text.ParseException {
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        if(format instanceof DecimalFormat) {
            ((DecimalFormat)format).setParseBigDecimal(true);
        }
        return (BigDecimal)format.parse(amount.replace("[^\\d.,]",""));

    }
}
