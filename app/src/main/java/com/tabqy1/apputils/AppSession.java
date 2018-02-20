package com.tabqy1.apputils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tabqy1.models.FoodType;
import com.tabqy1.models.UserDetails;
import com.tabqy1.webservices.RetrofitApiBuilder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RamSingh on 10/9/2016.
 */

public class AppSession {

    private static final String SHARED = "pure_Preferences";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private final String RESTAURANT_ID = "restaurantID";
    private final String USER_ID = "userID";
    private final String CURRENCY = "currency";
    private final String EMAIL = "email";
    private final String PASSWORD = "password";
    private final String LOGIN_STATUS = "loginStatus";
    private final String MENU_IMAGE = "menu_image";



    private final String RESTURANT_NAME =  "resturant_name";
    private final String RESTURANT_TAGLINE =  "resturant_tagline";
    private final String RESTURANT_COLOR =  "resturant_color";
    private final String RESTURANT_BACKGROUND_IMAGE =  "resturant_background_image";
    private final String RESTURANT_LOGO =  "resturant_logo";
    private final String CODE_PHONE =  "code_phone";
    private final String RESTURANT_ADDRESS =  "resturant_address";
    private final String TAXES =  "taxes";
    private final String PREVIOUS_ORDER =  "previous_order";
    private final String APP_IP =  "app_ip";
    private final String SUBSCRIPTION_KEY =  "subscription_key";
    private final String TIMESTAMP =  "timestamp";

    public AppSession(Context context) {
        sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public  void setRestaurantID(UserDetails userDetails, String email, String password) {
        editor.putString(RESTAURANT_ID,userDetails.resturant_id);
        editor.putString(USER_ID,userDetails.user_id);
        editor.putBoolean(LOGIN_STATUS,true);
        editor.putString(EMAIL,email);
        editor.putString(PASSWORD,password);
        editor.putString(CURRENCY,userDetails.resturant_currency);
        editor.putString(RESTURANT_NAME,userDetails.resturant_name);
        editor.putString(RESTURANT_TAGLINE,userDetails.resturant_tagline);
        editor.putString(RESTURANT_COLOR,userDetails.resturant_color);
        editor.putString(RESTURANT_BACKGROUND_IMAGE,userDetails.resturant_background_image);
        editor.putString(RESTURANT_LOGO,userDetails.resturant_logo);
        editor.putString(CODE_PHONE,userDetails.resturant_phone);
        editor.putString(RESTURANT_ADDRESS,userDetails.resturant_address);
        editor.putString(MENU_IMAGE,userDetails.resturant_menu_image);
        editor.apply();
    }

    public void setTimestamp(long timestamp){
        editor.putLong(TIMESTAMP,timestamp);
        editor.commit();
    }
    public long getTimestamp(){
        return sharedPref.getLong(TIMESTAMP,0);
    }

     public void resetSession(){
         editor.putBoolean(LOGIN_STATUS,false);
         editor.putString(PREVIOUS_ORDER,"");
         editor.commit();
     }

    public String getRestaurantId(){
        return sharedPref.getString(RESTAURANT_ID,"");
    }

    public String getRestaurantColor(){
        return sharedPref.getString(RESTURANT_COLOR,"");
    }


    public String getRestaurantName(){
        return sharedPref.getString(RESTURANT_NAME,"");
    }

    public String getRestaurantBackgroundImage(){
        return sharedPref.getString(RESTURANT_BACKGROUND_IMAGE,"");
    }

    public String getRestaurantLogo(){
        return sharedPref.getString(RESTURANT_LOGO,"");
    }

    public String getUserId(){
        return sharedPref.getString(USER_ID,"");
    }

    public String getCurrency(){
        return sharedPref.getString(CURRENCY,"");
    }

    public boolean isLogin() {
        return sharedPref.getBoolean(LOGIN_STATUS,false);
    }



    public String getMenuImage() {
        return sharedPref.getString(MENU_IMAGE,"");
    }


    public void setTaxes(String taxes){
        editor.putString(TAXES,taxes);
        editor.commit();
    }
    public String getTaxes(){
        return sharedPref.getString(TAXES,"");
    }

    public void resetPreviousOrder(){
        editor.putString(PREVIOUS_ORDER,"");
        editor.commit();
    }

/** this method is used for save previous order list */
    public void savePreviousOrder(List<FoodType> orderArrayList) {
        Gson gson= new Gson();
        Type listType = new TypeToken<ArrayList<FoodType>>(){}.getType();
        String jsonOrder= gson.toJson(orderArrayList,listType);
        editor.putString(PREVIOUS_ORDER,jsonOrder);
        editor.commit();

    }

/** this method is used for getting previous order list */
    public  List<FoodType> getPreviousOrder() {
        List<FoodType> orderArrayList= new ArrayList<FoodType>();
        String jsonOrder = sharedPref.getString(PREVIOUS_ORDER,"");
        Gson gson= new Gson();
        Type listType = new TypeToken<ArrayList<FoodType>>(){}.getType();
        orderArrayList= gson.fromJson(jsonOrder,listType);
        return orderArrayList;

    }

    public String getSubscriptionKey(){
       return   sharedPref.getString(SUBSCRIPTION_KEY,null);
    }

    public void setSubscriptionKey(String subscriptionKey){
        editor.putString(SUBSCRIPTION_KEY,subscriptionKey);
        editor.commit();
    }



}
