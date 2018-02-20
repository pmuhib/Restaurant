package com.tabqy1.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tabqy1.R;
import com.tabqy1.activity.DashBoradActivity;
import com.tabqy1.adapter.OrderListRecyclerAdapter;
import com.tabqy1.adapter.PreviousOrderRecyclerAdapter;
import com.tabqy1.apputils.AppSession;
import com.tabqy1.apputils.AppUtils;
import com.tabqy1.listeners.OrderModifiedListener;
import com.tabqy1.models.FoodDetails;
import com.tabqy1.models.FoodType;
import com.tabqy1.models.Tax;
import com.tabqy1.models.Waitertable;
import com.tabqy1.webservices.ApiResponse;
import com.tabqy1.webservices.RetroFitApis;
import com.tabqy1.webservices.RetrofitApiBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderFragment extends Fragment implements View.OnClickListener,OrderModifiedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_ORDER_NUMBER= "orderNumber";
    private static final String ARG_PARAM_ORDER_TYPE = "orderType";
    private static final String ARG_PARAM_TABLE_NO = "tableNo";
    private static final String ARG_PARAM_ORDER_VALUE = "orderValue";
    private static final String ARG_PARAM_ORDER_VALUE_NO_TAX = "orderValueNoTax";
    private static final String ARG_PARAM_CUSTOMER_STATUS = "customerStatus";
    private static final int ORDER_TYPE_HOME_DELIVERY = 1;
    private static final int ORDER_TYPE_TABLE = 2;

    // TODO: Rename and change types of parameters
    private String orderNumber;
    private String tableNo = "";
    private String customerStatus = "";
    private int orderType;
    private View view;
    private RecyclerView recycler;
    private AppCompatActivity activity;
    private Button btnHomeDelivery, btnAddMore, btnSubmitOrder,btnCompleteOrder;
    private TextView tvTextWithTableNo;
    private TextView tabPrevious;
    private TextView tabCurrent;
    private AppSession appSession;
    private TextView totalPrice,grandTotalPrice;
    private OrderModifiedListener orderModifiedListener;
    private LinearLayout llTab;
    private double previousPrice,previousPriceNoTax;
    private ArrayList<FoodDetails> foodDetails = new ArrayList<>();
    private PreviousOrderRecyclerAdapter previousAdapter;



    /**Atul*/
    private Button btn_apply_coupon_code ;
    private EditText etCouponCode ;
    private TextView tvCouponResponse ;
    private RelativeLayout containerCoupon ;

    public OrderFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity=(AppCompatActivity)context;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment MenuListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderFragment newInstance(String orderNumber, int orderType,String tableNo, String orderValue,String orderValueNoTax, String customerStatus ) {
        OrderFragment fragment = new OrderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_ORDER_NUMBER, orderNumber);
        args.putInt(ARG_PARAM_ORDER_TYPE, orderType);
        args.putString(ARG_PARAM_TABLE_NO, tableNo);
        args.putString(ARG_PARAM_ORDER_VALUE, orderValue);
        args.putString(ARG_PARAM_ORDER_VALUE_NO_TAX, orderValueNoTax);
        args.putString(ARG_PARAM_CUSTOMER_STATUS, customerStatus);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderNumber = getArguments().getString(ARG_PARAM_ORDER_NUMBER);
            orderType = getArguments().getInt(ARG_PARAM_ORDER_TYPE);
            tableNo = getArguments().getString(ARG_PARAM_TABLE_NO);
            customerStatus = getArguments().getString(ARG_PARAM_CUSTOMER_STATUS);
            previousPrice = Double.parseDouble(getArguments().getString(ARG_PARAM_ORDER_VALUE));
            previousPriceNoTax = Double.parseDouble(getArguments().getString(ARG_PARAM_ORDER_VALUE_NO_TAX));

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_order, container, false);
        initView();
        appSession = new AppSession(activity);
        orderModifiedListener = this;
        if(orderNumber!=null && orderNumber.length()>0){
            callPreviousOrderApi(orderNumber);
        }
        setupRecycler();
        onItemModified();

        AppUtils.setAppBackGroundImage(getActivity(),(ViewGroup) view,appSession.getRestaurantBackgroundImage());

        return  view;
    }
    public void initView(){
        TextView tvBack = (TextView) activity.findViewById(R.id.tv_back);
        tvBack.setVisibility(View.VISIBLE);
        TextView tvTitle=(TextView)activity.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.order_list);

        ImageButton btnOrder = (ImageButton) activity.findViewById(R.id.btnOrder) ;
        btnOrder.setVisibility(View.GONE);

        recycler = (RecyclerView) view.findViewById(R.id.rvOrderList) ;
        btnHomeDelivery = (Button) view.findViewById(R.id.btnHomeDelivery) ;
        btnSubmitOrder = (Button) view.findViewById(R.id.btnSubmitOrder) ;
        btnCompleteOrder = (Button) view.findViewById(R.id.btnCompleteOrder) ;
        btnAddMore = (Button) view.findViewById(R.id.btnAddMore) ;
        tvTextWithTableNo = (TextView) view.findViewById(R.id.tvTextWithTableNo) ;
        totalPrice = (TextView) view.findViewById(R.id.totalPrice) ;
        grandTotalPrice = (TextView) view.findViewById(R.id.grandTotalPrice) ;
        tabPrevious = (TextView) view.findViewById(R.id.tabPrevious) ;
        tabCurrent = (TextView) view.findViewById(R.id.tabCurrent) ;
        llTab = (LinearLayout) view.findViewById(R.id.llTab) ;

        initCouponCodeView() ;
        grandTotalPrice.setVisibility(View.GONE);
        if(orderNumber!=null && orderNumber.length()>0){
            if(orderType == ORDER_TYPE_HOME_DELIVERY){
                tvTextWithTableNo.setText("Order list for Home Delivery");
                btnCompleteOrder.setVisibility(View.VISIBLE);
                btnSubmitOrder.setVisibility(View.GONE);
                btnHomeDelivery.setVisibility(View.VISIBLE);
                btnAddMore.setVisibility(View.VISIBLE);
                containerCoupon.setVisibility(View.VISIBLE);

            }else  if(orderType == ORDER_TYPE_TABLE){
                tvTextWithTableNo.setText("Order list for table number "+((DashBoradActivity)activity).getTableNumber().table_name);
                btnCompleteOrder.setVisibility(View.VISIBLE);
                btnSubmitOrder.setVisibility(View.VISIBLE);
                btnHomeDelivery.setVisibility(View.GONE);
                btnAddMore.setVisibility(View.VISIBLE);
                containerCoupon.setVisibility(View.VISIBLE);
            }
            llTab.setVisibility(View.VISIBLE);
            tabCurrent.setSelected(true);
            tabPrevious.setSelected(false);

        }else{
            tvTextWithTableNo.setText("Order list for table number "+((DashBoradActivity)activity).getTableNumber().table_name);
            btnAddMore.setVisibility(View.VISIBLE);
            btnCompleteOrder.setVisibility(View.GONE);
            btnSubmitOrder.setVisibility(View.VISIBLE);
            btnHomeDelivery.setVisibility(View.VISIBLE);
            llTab.setVisibility(View.GONE);
            containerCoupon.setVisibility(View.GONE);
        }
        btnHomeDelivery.setOnClickListener(this);
        btnSubmitOrder.setOnClickListener(this);
        btnCompleteOrder.setOnClickListener(this);
        btnAddMore.setOnClickListener(this);
        tabCurrent.setOnClickListener(this);
        tabPrevious.setOnClickListener(this);

    }
    protected void setupRecycler(){
        ArrayList<FoodType> orders = new ArrayList();

        orders.addAll(((DashBoradActivity)activity).getDisplayOrderList());

        OrderListRecyclerAdapter adapter = new OrderListRecyclerAdapter(getContext(),orders,orderModifiedListener);
        recycler.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(layoutManager);
    }

    protected void setupPreviousRecycler(){
        previousAdapter = new PreviousOrderRecyclerAdapter(getContext(),foodDetails);
        recycler.setAdapter(previousAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(layoutManager);
    }

    private void initCouponCodeView() {
        containerCoupon = (RelativeLayout) view.findViewById(R.id.containerCoupon);
        tvCouponResponse = (TextView)view.findViewById(R.id.tvCouponResponse);
        btn_apply_coupon_code = (Button)view.findViewById(R.id.btn_apply_coupon_code);
        etCouponCode = (EditText) view.findViewById(R.id.etCouponCode);
        tvCouponResponse.setVisibility(View.GONE);
        btn_apply_coupon_code.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        if(isProcess){
            return;
        }
        isProcess = true  ;
        switch (view.getId()){
            case R.id.btnHomeDelivery:
                if (((DashBoradActivity) activity).getOrderArraylist().size() > 0) {
                    if (orderNumber != null && orderNumber.length() > 0) callReOrderApi(orderNumber, ORDER_TYPE_HOME_DELIVERY, "");
                    else callAddFoodsApi(ORDER_TYPE_HOME_DELIVERY);
                }else{
                    setProcess(false);
                    Toast.makeText(activity, "Please add items to order.", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btnSubmitOrder:
                if(tableNo!=null && tableNo.length()>0) takeOrder();
                else callTableDetailsApi();
                break;

            case R.id.btnCompleteOrder:
                confirmOrder(orderType);
                break;

            case R.id.btnAddMore:
                if(orderType==1){
                    Toast.makeText(getActivity(), "You can't add more item in Home delivery.\nPLEASE COMPLETE ORDER.", Toast.LENGTH_SHORT).show();
                    setProcess(false);
                    return ;
                }
                ((DashBoradActivity)activity).setOrderNumberAndType(orderNumber,orderType,String.valueOf(previousPrice),String.valueOf(previousPriceNoTax),customerStatus);
                MenuFragment menuFragment = MenuFragment.newInstance("","");
                AppUtils.setFragment(menuFragment,false,activity,R.id.fragmentContainer);
                setProcess(false);
                break;
            case R.id.tabCurrent:
                tabCurrent.setSelected(true);
                tabPrevious.setSelected(false);
                setupRecycler();
                setProcess(false);
                break;
            case R.id.tabPrevious:

                tabCurrent.setSelected(false);
                tabPrevious.setSelected(true);
                setupPreviousRecycler();
                setProcess(false);
                break;

            case R.id.btn_apply_coupon_code :
                String couponCode  = etCouponCode.getText().toString().trim() ;
                String restaurant_id = appSession.getRestaurantId() ;
                if(!couponCode.isEmpty()) {
                    tvCouponResponse.setVisibility(View.GONE);
                    callValidateCouponCode(couponCode,restaurant_id);
                }else setProcess(false);


        }
    }

    private void takeOrder() {
        if (((DashBoradActivity) activity).getOrderArraylist().size() > 0) {

            if (orderNumber != null && orderNumber.length() > 0) {
                callReOrderApi(orderNumber, ORDER_TYPE_TABLE, tableNo);
            } else {
                callAddFoodsApi(ORDER_TYPE_TABLE);
            }
        }else{
            setProcess(false);
            Toast.makeText(activity, "Please add items to order.", Toast.LENGTH_SHORT).show();
        }
    }

    double discountPrice ;
    float discount ;
    String couponCode  ;

    private void callValidateCouponCode(final String coupon_code, String restaurant_id) {
        RetroFitApis retroFitApis = RetrofitApiBuilder.getRetrofitRestaurant().create(RetroFitApis.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("coupon_code",coupon_code);
        jsonObject.addProperty("restaurant_id",restaurant_id);
        Log.e("request","request"+jsonObject.toString());

        Call<ApiResponse> apiResponseCall = retroFitApis.validateCouponCode(jsonObject);

        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Response<ApiResponse> response, Retrofit retrofit) {
                setProcess(false);
                Log.e("Response",new Gson().toJson(response.body()));
                if(response.body().status) {
                    if(response.body().value!=0.0f) {
                        // Find Out Discount amount
                        couponCode = coupon_code ;
                        discount = response.body().value ;
                        double totalPrice  = ((DashBoradActivity) activity).getTaxCalculatedPrice()+previousPrice;
                        discountPrice  = (discount)*.01*totalPrice;
                        totalPrice -= discountPrice  ;

                        tvCouponResponse.setText(response.body().value+" % discount coupon applied Successfully \n Discount Price is " +
                                ""+appSession.getCurrency()+" "+String.format(Locale.US,"%.2f", discountPrice));
                        tvCouponResponse.setVisibility(View.VISIBLE);
                        grandTotalPrice.setText("Grand Total : "+appSession.getCurrency()+String.format(Locale.US,"%.2f", totalPrice));
                        grandTotalPrice.setVisibility(View.VISIBLE);
                        tvCouponResponse.setTextColor(Color.parseColor("#FF0000"));
                    }
                }else{
                    couponCode = null;
                    discount = 0.0f ;
                    discountPrice= 0.0 ;
                    // Toast.makeText(activity, response.body().msg, Toast.LENGTH_SHORT).show();
                    tvCouponResponse.setText(response.body().msg);
                    tvCouponResponse.setVisibility(View.VISIBLE);
                    grandTotalPrice.setVisibility(View.GONE);
                    tvCouponResponse.setTextColor(Color.parseColor("#FF0000"));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                setProcess(false);
                Toast.makeText(activity, R.string.server_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callAddFoodsApi(final int ordertype){
        AppUtils.showProgress(getActivity(),getResources().getString(R.string.adding_order),false);
        RetroFitApis retroFitApis = RetrofitApiBuilder.getRetrofitRestaurant().create(RetroFitApis.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id",appSession.getUserId());
        if(ordertype == ORDER_TYPE_TABLE) {
            jsonObject.addProperty("type", "2");
        }else{
            jsonObject.addProperty("type", "1");
        }
        jsonObject.addProperty("status","0");
        jsonObject.addProperty("table_id",((DashBoradActivity)activity).getTableNumber().table_id);
        jsonObject.addProperty("resturant_id",appSession.getRestaurantId());
        jsonObject.addProperty("total_price",((DashBoradActivity)activity).getPrice());
        jsonObject.addProperty("payable_amount",((DashBoradActivity)activity).getTaxCalculatedPrice());
        for (Tax tax: ((DashBoradActivity)activity).getTaxList()) {
            jsonObject.addProperty(tax.tax_name,tax.tax_value);
        }

        jsonObject.add("orders",new Gson().toJsonTree(((DashBoradActivity)activity).getFinalOrderList()).getAsJsonArray());

        Log.e("request","request"+jsonObject.toString());
        Call<ApiResponse> apiResponseCall = null;
        if(ordertype == ORDER_TYPE_TABLE) {
            apiResponseCall = retroFitApis.addFoodOrders(jsonObject);
        }else if (ordertype ==ORDER_TYPE_HOME_DELIVERY){
            apiResponseCall = retroFitApis.homeDelivery(jsonObject);
        }
        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Response<ApiResponse> response, Retrofit retrofit) {
                setProcess(false);
                Log.e("Response",new Gson().toJson(response.body()));
                if(response.body().status) {
                    ((DashBoradActivity) activity).clearOrder();
                    if(ordertype == ORDER_TYPE_HOME_DELIVERY){

                        HomeDeliveryFragment homeDeliveryFragment= HomeDeliveryFragment.newInstance(response.body().order_no,response.body().order_date,response.body().order_time,response.body().total_cost,false);
                        AppUtils.setFragment(homeDeliveryFragment,false,activity,R.id.fragmentContainer);

                    }else if(ordertype == ORDER_TYPE_TABLE) {
                        popupToAskPrint(false,response.body().order_no);
                    }
                    Toast.makeText(activity, response.body().msg, Toast.LENGTH_SHORT).show();

                }else{

                    Toast.makeText(activity, response.body().msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                setProcess(false);
                Toast.makeText(activity, R.string.server_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callReOrderApi(String orderId,final int ordertype,final String tableNo){
        AppUtils.showProgress(getActivity(),getResources().getString(R.string.adding_order),false);
        RetroFitApis retroFitApis = RetrofitApiBuilder.getRetrofitRestaurant().create(RetroFitApis.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id",appSession.getUserId());
        if(ordertype == ORDER_TYPE_TABLE) {
            jsonObject.addProperty("type", "2");
        }else{
            jsonObject.addProperty("type", "1");
        }
        jsonObject.addProperty("status","0");
        jsonObject.addProperty("table_id",tableNo);//TODO: add dynamic tablel number
        jsonObject.addProperty("order_no",orderId);
        jsonObject.addProperty("resturant_id",appSession.getRestaurantId());
        jsonObject.addProperty("total_price",((DashBoradActivity)activity).getPrice()+previousPriceNoTax);
        jsonObject.addProperty("payable_amount",((DashBoradActivity)activity).getTaxCalculatedPrice()+previousPrice);
        for (Tax tax: ((DashBoradActivity)activity).getTaxList()) {
            jsonObject.addProperty(tax.tax_name,tax.tax_value);
        }
        jsonObject.add("orders",new Gson().toJsonTree(((DashBoradActivity)activity).getFinalOrderList()).getAsJsonArray());

        Log.e("request","request"+jsonObject.toString());

        Call<ApiResponse> apiResponseCall = retroFitApis.reFoodOrders(jsonObject);

        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Response<ApiResponse> response, Retrofit retrofit) {
                setProcess(false);
                Log.e("Response",new Gson().toJson(response.body()));

                if(response!=null) {

                    if (response.body().status) {
                        ((DashBoradActivity) activity).clearOrder();
                        if (ordertype == ORDER_TYPE_HOME_DELIVERY) {
                            //TODO: parse order number
                            if(customerStatus.equals("0")) {
                                HomeDeliveryFragment homeDeliveryFragment = HomeDeliveryFragment.newInstance(response.body().order_no, response.body().order_date, response.body().order_time, response.body().total_cost,false);
                                AppUtils.setFragment(homeDeliveryFragment, false, activity, R.id.fragmentContainer);
                            }else{
                                popupToAskPrint(true,response.body().order_no);
                            }

                        } else if (ordertype == ORDER_TYPE_TABLE) {
                            popupToAskPrint(false,response.body().order_no);
                        }
                        Toast.makeText(activity, response.body().msg, Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(activity, response.body().msg, Toast.LENGTH_SHORT).show();
                    }
                }else{

                    Toast.makeText(activity, "Error in server. Please try again.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                setProcess(false);
                Toast.makeText(activity, R.string.server_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void popupToAskPrint(final boolean isHome,final String order_no) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Send request to print order to kitchen");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                RecipeFragment homeFragment = RecipeFragment.newInstance(order_no,tableNo,isHome,false);
                AppUtils.setFragment(homeFragment, true, activity, R.id.fragmentContainer);
            }
        });
        builder.setCancelable(false);
        builder.create() ;
        builder.show() ;
    }

    public void setProcess(boolean process) {
        isProcess = process;
        AppUtils.hideProgress();
    }

    private boolean isProcess  = false  ;
    private void confirmOrder(final int ordertype){
        AppUtils.showProgress(getActivity(),getResources().getString(R.string.completing_order),false);
        RetroFitApis retroFitApis = RetrofitApiBuilder.getRetrofitRestaurant().create(RetroFitApis.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("coupon_discount",discount);
        jsonObject.addProperty("discount_amount",discountPrice);
        jsonObject.addProperty("coupon_code",couponCode);
        jsonObject.addProperty("order_no",orderNumber);
        jsonObject.addProperty("currency_code",appSession.getCurrency());
        jsonObject.addProperty("status","1");
        Log.e("request","request"+jsonObject.toString());
        Call<ApiResponse> apiResponseCall = null;
        if(ordertype == ORDER_TYPE_TABLE) {
            apiResponseCall = retroFitApis.orderCompleted(jsonObject);
        }else if (ordertype ==ORDER_TYPE_HOME_DELIVERY){
            apiResponseCall = retroFitApis.homeDeliveryCompleted(jsonObject);
        }
        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Response<ApiResponse> response, Retrofit retrofit) {
                setProcess(false);
                if(response.body()== null){
                    Toast.makeText(activity, "Internal Server Error", Toast.LENGTH_SHORT).show();
                    return ;
                }

                Log.e("Response",response.body().toString());
                if(response.body().status) {
                    ((DashBoradActivity) activity).clearOrder();
                    if(ordertype == ORDER_TYPE_HOME_DELIVERY){

                        if(customerStatus.equals("0")) {
                            String price ;
                            if(couponCode!=null){
                                price  = String.valueOf(grandTotalPrice.getText().toString());
                            }
                            else {
                                price = String.valueOf(totalPrice.getText().toString()) ;
                            }
                            HomeDeliveryFragment homeDeliveryFragment = HomeDeliveryFragment.newInstance(orderNumber, response.body().order_date, response.body().order_time, price,true);
                            AppUtils.setFragment(homeDeliveryFragment, false, activity, R.id.fragmentContainer);
                        }else{

                            RecipeFragment homeFragment = RecipeFragment.newInstance(orderNumber,tableNo,true,true);
                            AppUtils.setFragment(homeFragment, true, activity, R.id.fragmentContainer);

                        }
                    }else if(ordertype == ORDER_TYPE_TABLE) {
                        setProcess(false);
                        RecipeFragment homeFragment = RecipeFragment.newInstance(orderNumber,tableNo,false,true);
                        AppUtils.setFragment(homeFragment, true, activity, R.id.fragmentContainer);
                    }
                    Toast.makeText(activity, response.body().msg, Toast.LENGTH_SHORT).show();

                }else{

                    Toast.makeText(activity, response.body().msg, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                setProcess(false);
                Toast.makeText(activity, R.string.server_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callTableDetailsApi(){
        AppUtils.showProgress(getActivity(),getResources().getString(R.string.fetching_booked_table_list),false);
        RetroFitApis retroFitApis = RetrofitApiBuilder.getRetrofitRestaurant().create(RetroFitApis.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", appSession.getUserId());

        Log.e("request","request"+jsonObject.toString());
        Call<ApiResponse> apiResponseCall = retroFitApis.getAssignTable(jsonObject);
        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Response<ApiResponse> response, Retrofit retrofit) {
                setProcess(false);
                if(response.body().status) {
                    showTableSelector(response.body().waitertable);
//                    final List<Waitertable> tables =  response.body().waitertable ;
//                    final List<Waitertable> notBookedTables = new ArrayList<>();
//                    int tableCount  = tables.size();
//                    if(tableCount>0) {
//                        for (int indexTable = 0; indexTable < tableCount; indexTable++) {
//                            final boolean isBooked = tables.get(indexTable).isBooked;
//                            if (!isBooked) {
//                                notBookedTables.add(tables.get(indexTable));
//                            }
//                        }
//                        if(notBookedTables.size()>0)
//                            showTableSelector(notBookedTables);
//                        else
//                            Toast.makeText(activity, "All Tables are booked.", Toast.LENGTH_SHORT).show();
//                    }else{
//                        Toast.makeText(activity, response.body().msg, Toast.LENGTH_SHORT).show();
//                    }
                }else{
                    setProcess(false);
                    Toast.makeText(activity, response.body().msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                setProcess(false);
                Toast.makeText(activity, R.string.server_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTableSelector(final List<Waitertable> waitertables){
        String [] tablename= new String[waitertables.size()];
        for (int i=0;i<waitertables.size();i++) {
            tablename[i]=waitertables.get(i).table_name;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Select Table");
        builder.setIcon(R.drawable.table_icon);
        builder.setItems(tablename, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!waitertables.get(which).isBooked.toLowerCase().equals("booked")) {
                    dialog.dismiss();
                    tvTextWithTableNo.setText("Order list for table number " + waitertables.get(which).table_name);
                    tableNo = waitertables.get(which).table_id;
                    ((DashBoradActivity) activity).setTableNumber(waitertables.get(which));
                    takeOrder();
                }else {
                    Toast.makeText(activity, "Already Booked. Please choose another table.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        builder.setCancelable(false);
        Dialog myDialog = builder.create();
        myDialog.show();
    }


    @Override
    public void onItemModified() {
        ((DashBoradActivity)activity).updatePrice();
        List<Tax> taxes = ((DashBoradActivity)activity).getTaxList();
        StringBuilder stringBuilder = new StringBuilder();
        if(orderNumber!=null){
            stringBuilder.append(String.format("%-25s%9s\n","Previous total price :",appSession.getCurrency()+String.format(Locale.US,"%.2f", previousPrice)));
            stringBuilder.append(String.format("%-25s%9s\n","Ongoing total price :",appSession.getCurrency()+String.format(Locale.US,"%.2f", ((DashBoradActivity)activity).getPrice())));

        }else {
            stringBuilder.append(String.format("%-25s%9s\n","Total price :",appSession.getCurrency()+String.format(Locale.US,"%.2f", ((DashBoradActivity)activity).getPrice())));
        }

        if(taxes.size()>0) {

            for (int i = 0; i < taxes.size(); i++) {
                stringBuilder.append(String.format("%-25s%9s\n", taxes.get(i).tax_name + " @" + taxes.get(i).tax_value + "% :", appSession.getCurrency() + String.format(Locale.US, "%.2f", ((DashBoradActivity) activity).getTaxPrice(Double.parseDouble(taxes.get(i).tax_value)))));
            }
            stringBuilder.append("__________________________________\n\n");
        }
        if(orderNumber!=null){

            stringBuilder.append(String.format("%-25s%9s\n","Total payable amount :",appSession.getCurrency()+String.format(Locale.US,"%.2f",(((DashBoradActivity) activity).getTaxCalculatedPrice()+previousPrice))));
        }else {
            stringBuilder.append(String.format("%-25s%9s\n","Total payable amount :",appSession.getCurrency()+String.format(Locale.US,"%.2f",(((DashBoradActivity) activity).getTaxCalculatedPrice()))));
        }
        stringBuilder.append("__________________________________\n");

        totalPrice.setText(stringBuilder);
        grandTotalPrice.setVisibility(View.GONE);
    }

    private void callPreviousOrderApi(String orderNo){

        RetroFitApis retroFitApis = RetrofitApiBuilder.getRetrofitRestaurant().create(RetroFitApis.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("order_no",orderNo);

        Log.e("request","request"+jsonObject.toString());

        Call<ApiResponse> apiResponseCall = retroFitApis.getPreviousOrder(jsonObject);

        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Response<ApiResponse> response, Retrofit retrofit) {
                Log.e("Response",new Gson().toJson(response.body()));
                if(response.body().status) {
                    foodDetails.clear();
                    if(response.body().food_details!=null) {

                        foodDetails.addAll(response.body().food_details);
                    }

                }else{
                    Toast.makeText(activity, response.body().msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(activity, R.string.server_error, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
