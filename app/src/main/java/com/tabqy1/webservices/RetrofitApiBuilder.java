package com.tabqy1.webservices;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by jayant on 04-11-2016.
 */

public class RetrofitApiBuilder {

    public static void setAppIp(String appIp) {
        APP_IP = appIp;
        BASE_URL = "http://"+appIp+"/" ;
        BASE_URL_MENU_BACKGROUND = BASE_URL+"resturant/assets/uploads/backgroundimage/";
        BASE_URL_RESTAURANT = BASE_URL+"resturant/";
        BASE_URL_CUISINEIMAGE = BASE_URL+"resturant/assets/uploads/thumbs/cuisineimage/";
        BASE_URL_FOODIMAGE = BASE_URL+"resturant/assets/uploads/foodimage/";
        BASE_URL_RESTAURANT_LOGO = BASE_URL+"resturant/assets/uploads/thumbs/resturantlogo/";
        BASE_URL_BACKGROUND_IMAGE = BASE_URL+"resturant/assets/uploads/backgroundimage/";
        BASE_URL_MEAL = BASE_URL+"resturant/assets/uploads/thumbs/meal/";
        BASE_URL_EMPIMAGE = BASE_URL+"resturant/assets/uploads/empimage/";
        BASE_URL_RESTURANT_IMAGE = BASE_URL+"resturant/assets/uploads/resturantimage/";
        BASE_URL_MENU_IMAGE = BASE_URL+"resturant/assets/uploads/thumbs/meal/";
        BASE_URL_ASSOCIATED_IMAGE = BASE_URL+"resturant/assets/uploads/thumbs/foodimage/";
        BASE_URL_PREVIOUS_ORDER_IMAGE = BASE_URL+"resturant/assets/uploads/thumbs/foodimage/";
    }
    public final static String DEFAULT_IP = "tabqy.com"  ;
    public static String  APP_IP = DEFAULT_IP ;
    public static  String BASE_URL  =  "http://"+APP_IP+"/" ;
    public static  String BASE_URL_MENU_BACKGROUND = BASE_URL+"resturant/assets/uploads/backgroundimage/";
    public static  String BASE_URL_RESTAURANT = BASE_URL+"resturant/";
    public static  String BASE_URL_CUISINEIMAGE = BASE_URL+"resturant/assets/uploads/thumbs/cuisineimage/";
    public static  String BASE_URL_FOODIMAGE = BASE_URL+"resturant/assets/uploads/foodimage/";
    public static  String BASE_URL_RESTAURANT_LOGO = BASE_URL+"resturant/assets/uploads/thumbs/resturantlogo/";
    public static  String BASE_URL_BACKGROUND_IMAGE = BASE_URL+"resturant/assets/uploads/backgroundimage/";
    public static  String BASE_URL_MEAL = BASE_URL+"resturant/assets/uploads/thumbs/meal/";
    public static  String BASE_URL_EMPIMAGE = BASE_URL+"resturant/assets/uploads/empimage/";
    public static  String BASE_URL_RESTURANT_IMAGE = BASE_URL+"resturant/assets/uploads/resturantimage/";
    public static  String BASE_URL_MENU_IMAGE = BASE_URL+"resturant/assets/uploads/thumbs/meal/";
    public static  String BASE_URL_ASSOCIATED_IMAGE = BASE_URL+"resturant/assets/uploads/thumbs/foodimage/";
    public static  String BASE_URL_PREVIOUS_ORDER_IMAGE = BASE_URL+"resturant/assets/uploads/thumbs/foodimage/";

    public static Retrofit getRetrofitGlobal(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_RESTAURANT)
                .addConverterFactory(GsonConverterFactory.create())

                .build();

        return retrofit;
    }



    public static Retrofit getRetrofitRestaurant(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_RESTAURANT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }

    public static Retrofit getRetrofitServer(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://"+RetrofitApiBuilder.APP_IP+"resturant/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }
}
