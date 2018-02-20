package com.tabqy1.webservices;

import com.google.gson.JsonObject;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by jayant on 04-11-2016.
 */

public interface RetroFitApis {

    @POST("webservice/get_subscription_day")
    Call<ApiResponse> validateSubscriptionKey(@Body JsonObject jsonObject);

    @POST("webservice/login")
    Call<ApiResponse> doLogin(@Body JsonObject jsonObject);

    @POST("webservice/forgetpassword")
    Call<ApiResponse> forgetpassword(@Body JsonObject jsonObject);

    @POST("webservice/menu")
    Call<ApiResponse> getMenu(@Body JsonObject jsonObject);

    @POST("webservice/cusinie")
    Call<ApiResponse> getCusine(@Body JsonObject jsonObject);

    @POST("webservice/foodtype")
    Call<ApiResponse> getFoodType(@Body JsonObject jsonObject);

    @POST("webservice/food")
    Call<ApiResponse> getFoods(@Body JsonObject jsonObject);

    @POST("webservice/fooddetails")
    Call<ApiResponse> getFoodDetail(@Body JsonObject jsonObject);

    @POST("webservice/todayspecial")
    Call<ApiResponse> getTodaySpecial(@Body JsonObject jsonObject);

    @POST("webservice/hotdeals")
    Call<ApiResponse> getHotDeals(@Body JsonObject jsonObject);

    @POST("webservice/foodsearch")
    Call<ApiResponse> getFoodSearch(@Body JsonObject jsonObject);


    @POST("webservice/table_assign_to_waiter")
    Call<ApiResponse> getAssignTable(@Body JsonObject jsonObject);

    @POST("webservice/waiter_progress_table")
    Call<ApiResponse> getProgressOrderTableList(@Body JsonObject jsonObject);

    @POST("webservice/about_restaurant")
    Call<ApiResponse> getAboutRestaurant(@Body JsonObject jsonObject);


    @POST("webservice/myprofile")
    Call<ApiResponse> getMyProfile(@Body JsonObject jsonObject);

    @POST("webservice/invoice")
    Call<ApiResponse> getInvoice(@Body JsonObject jsonObject);

    @POST("webservice/kitchen_print")
    Call<ApiResponse> getKitchenPrint(@Body JsonObject jsonObject);

    @POST("webservice/order_history")
    Call<ApiResponse> getOrderHistory(@Body JsonObject jsonObject);

    @POST("webservice/order_in_progess_list")
    Call<ApiResponse> getOrderInProgessList(@Body JsonObject jsonObject);

    @POST("webservice/order_in_progess_list_by_table")
    Call<JsonObject> getOrderInProgessListByTable(@Body JsonObject jsonObject);

    @POST("webservice/order_in_progess_list_home_delivery")
    Call<JsonObject> getOrderInProgessListByHomeDelivery(@Body JsonObject jsonObject);

    @POST("webservice/add_food_orders")
    Call<ApiResponse> addFoodOrders(@Body JsonObject jsonObject);

    @POST("webservice/re_food_orders")
    Call<ApiResponse> reFoodOrders(@Body JsonObject jsonObject);

    @POST("webservice/order_completed")
    Call<ApiResponse> orderCompleted(@Body JsonObject jsonObject);

    @POST("webservice/home_delivery_completed")
    Call<ApiResponse> homeDeliveryCompleted(@Body JsonObject jsonObject);

    @POST("webservice/home_delivery")
    Call<ApiResponse> homeDelivery(@Body JsonObject jsonObject);

    @POST("webservice/customer_information")
    Call<ApiResponse> saveCustomerInformation(@Body JsonObject jsonObject);

    @POST("webservice/tax_management")
    Call<ApiResponse> getTax(@Body JsonObject jsonObject);

    @POST("webservice/feedback_question")
    Call<ApiResponse> getQestionList(@Body JsonObject jsonObject);

    @POST("webservice/feedback_answer")
    Call<ApiResponse> sendFeedback(@Body JsonObject jsonObject);

    @POST("webservice/previous_order")
    Call<ApiResponse> getPreviousOrder(@Body JsonObject jsonObject);

    @POST("webservice/apply_coupon")
    Call<ApiResponse> validateCouponCode(@Body JsonObject jsonObject);


}
