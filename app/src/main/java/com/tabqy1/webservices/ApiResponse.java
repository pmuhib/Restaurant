package com.tabqy1.webservices;

import com.tabqy1.models.AboutRestaurant;
import com.tabqy1.models.Coupon;
import com.tabqy1.models.Cusinie;
import com.tabqy1.models.FoodDetails;
import com.tabqy1.models.FoodDetailsInfo;
import com.tabqy1.models.FoodType;
import com.tabqy1.models.InvoiceData;
import com.tabqy1.models.Menu;
import com.tabqy1.models.OrderHistory;
import com.tabqy1.models.ProfileDetail;
import com.tabqy1.models.QuestionList;
import com.tabqy1.models.Tax;
import com.tabqy1.models.TeamMember;
import com.tabqy1.models.UserDetails;
import com.tabqy1.models.Waitertable;

import java.util.List;

/**
 * Created by jayant on 04-11-2016.
 */

public class ApiResponse {

    public boolean status;
    public String msg;
    public float value;
    public String loding_point_detail;
    public String subscription_end_date ;
    public UserDetails user_detail;
    public List<Menu> menu;
    public List<Cusinie> cusinie;
    public List<FoodType> foodtype;
    public List<FoodType> todayspecial;
    public List<FoodType> hotdeals;
    public List<FoodType> relateditems;
    public List<FoodType> foodsearch;
    public List<FoodType> food;
    public List<Waitertable> waitertable;
    public List<OrderHistory> order_history;
    public List<OrderHistory> order_progess;
    public List<TeamMember> our_team;
    public List<AboutRestaurant> about_restaurant;
    public List<ProfileDetail> profile_detail;
    public List<Tax> tax_type;
    public String order_no;
    public String total_cost;
    public String order_date;
    public String order_time;
    public InvoiceData invoice;
    public Coupon coupon;
    public List<FoodDetails> food_details;
    public String sub_total;
    public String tax_total;
    public List<QuestionList> question;
    public List<FoodDetailsInfo> fooddetail;

}
