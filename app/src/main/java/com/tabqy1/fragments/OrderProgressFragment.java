package com.tabqy1.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tabqy1.R;
import com.tabqy1.adapter.OrderInProgressRecyclerAdapter;
import com.tabqy1.apputils.AppSession;
import com.tabqy1.apputils.AppUtils;
import com.tabqy1.models.OrderHistory;
import com.tabqy1.models.Waitertable;
import com.tabqy1.webservices.ApiResponse;
import com.tabqy1.webservices.RetroFitApis;
import com.tabqy1.webservices.RetrofitApiBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link OrderProgressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderProgressFragment extends Fragment implements TabLayout.OnTabSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //This is our tablayout
    private TabLayout tabLayout;

    //This is our viewPager
    private ViewPager viewPager;
    private View view;
    private AppCompatActivity context;
    private ArrayList<Waitertable> waitertables;
    private AppSession appSession;
    private ArrayList<OrderHistory> orderHistories;
    private OrderInProgressRecyclerAdapter adapter;
    private RecyclerView recycler;

    public OrderProgressFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderProgressFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderProgressFragment newInstance(String param1, String param2) {
        OrderProgressFragment fragment = new OrderProgressFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_order_progress, container, false);
        appSession= new AppSession(context);
        waitertables=new ArrayList<>();
        orderHistories= new ArrayList<>();
        setupRecycler();

        if(AppUtils.isNetworkAvailable(context)){
            callTableDetailsApi();
        }else {
            Toast.makeText(context,R.string.msg_no_connection,Toast.LENGTH_SHORT).show();
        }
        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = (AppCompatActivity)context;

    }


    public void setupPager( ArrayList<Waitertable> waitertables){

        //Initializing the tablayout
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);

        for (int i = 0; i <waitertables.size() ; i++) {
            //Adding the tabs using addTab() method
            tabLayout.addTab(tabLayout.newTab().setCustomView(getTabView(waitertables.get(i).table_name)));
        }

        tabLayout.addTab(tabLayout.newTab().setCustomView(getTabView(getString(R.string.home_delivery))));

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        //Adding onTabSelectedListener to swipe views
        tabLayout.addOnTabSelectedListener (this);


    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        View view = tab.getCustomView();
        TextView tv = (TextView) view.findViewById(R.id.tvTabTitle);
        tv.setSelected(true);

        if(tv.getText().toString().equals(getString(R.string.home_delivery))){
            if (AppUtils.isNetworkAvailable(context)) {
                if (waitertables.size() > 0)
                    callOrderInProgressApi("", false,true);
            } else {
                Toast.makeText(context, R.string.msg_no_connection, Toast.LENGTH_SHORT).show();
            }

        }else {
            if (AppUtils.isNetworkAvailable(context)) {
                if (waitertables.size() > 0)
                    callOrderInProgressApi(waitertables.get(tab.getPosition()).table_id, true,false);
            } else {
                Toast.makeText(context, R.string.msg_no_connection, Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        View view = tab.getCustomView();
        TextView tv = (TextView) view.findViewById(R.id.tvTabTitle);
        tv.setSelected(false);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        View view = tab.getCustomView();
        TextView tv = (TextView) view.findViewById(R.id.tvTabTitle);
        tv.setSelected(true);
    }



    public View getTabView(String tableName) {
        // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
        View v = LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
        TextView tv = (TextView) v.findViewById(R.id.tvTabTitle);
        tv.setText(tableName);
        return v;
    }


    private void callTableDetailsApi(){
        AppUtils.showProgress(getActivity(),getResources().getString(R.string.fetching_booked_table_list),false);
        RetroFitApis retroFitApis = RetrofitApiBuilder.getRetrofitRestaurant().create(RetroFitApis.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("waiter_id", appSession.getUserId());

        Log.e("request","request"+jsonObject.toString());

        Call<ApiResponse> apiResponseCall = retroFitApis.getProgressOrderTableList(jsonObject);
        //   Call<ApiResponse> apiResponseCall = retroFitApis.getAssignTable(jsonObject);
        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Response<ApiResponse> response, Retrofit retrofit) {
                if(response.body().status) {
                    waitertables.clear();

                    if(response.body().waitertable!=null) {
                        waitertables.addAll(response.body().waitertable);
                        setupPager(waitertables);
                        if (waitertables.size() > 0)
                            callOrderInProgressApi(waitertables.get(0).table_id, true, false);
                    }else{
                        setupPager(waitertables);
                        callOrderInProgressApi("", false,true);
                        Toast.makeText(context, "No Table Order in progress.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    setupPager(waitertables);
                    callOrderInProgressApi("", false,true);
                    Toast.makeText(context, response.body().msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                AppUtils.hideProgress();
                Toast.makeText(context, R.string.server_error, Toast.LENGTH_SHORT).show();
            }
        });


    }




    protected void setupRecycler(){

        TextView tvBack = (TextView) context.findViewById(R.id.tv_back);
        tvBack.setVisibility(View.VISIBLE);
        TextView tvTitle=(TextView)context.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.order_in_progress);

        ImageButton btnOrder = (ImageButton) context.findViewById(R.id.btnOrder) ;
        btnOrder.setVisibility(View.GONE);


        recycler = (RecyclerView) view.findViewById(R.id.rvOrderHistory) ;
        adapter = new OrderInProgressRecyclerAdapter(getContext(),orderHistories);
        recycler.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(layoutManager);

    }

    /** this method is used for display order in a progress*/
    private void callOrderInProgressApi(String tableID, boolean isCalled, final boolean isHomeDelivery){
        AppUtils.showProgress(getActivity(),getResources().getString(R.string.loading_progress_order_list),false);
        RetroFitApis retroFitApis = RetrofitApiBuilder.getRetrofitRestaurant().create(RetroFitApis.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", appSession.getUserId());
        jsonObject.addProperty("from_date",AppUtils.getCurrentDate());
        jsonObject.addProperty("to_date", AppUtils.getCurrentDate());
        if(isCalled){
            jsonObject.addProperty("table_id", tableID);
        }

        Log.e("request","request"+jsonObject.toString());

        Call<JsonObject> apiResponseCall;

        if(isHomeDelivery){
            apiResponseCall = retroFitApis.getOrderInProgessListByHomeDelivery(jsonObject);
        }else {
            apiResponseCall = retroFitApis.getOrderInProgessListByTable(jsonObject);
        }

        apiResponseCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response, Retrofit retrofit) {
                AppUtils.hideProgress();
                orderHistories.clear();
                try {
                    orderHistories.addAll(getOrderHistory(response.body(),isHomeDelivery));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Throwable t) {
                AppUtils.hideProgress();
                t.printStackTrace();
                Toast.makeText(context, R.string.server_error, Toast.LENGTH_SHORT).show();
            }
        });


    }

    private List<OrderHistory> getOrderHistory(JsonObject jsonObject,final boolean isHomeDelivery) throws Exception{
        List<OrderHistory> historyList = new ArrayList<>();
        boolean status= jsonObject.get("status").getAsBoolean();
        if(status) {
            JsonArray orderProgess = jsonObject.get("order_progess").getAsJsonArray();
            for (int i = 0; i < orderProgess.size(); i++) {

                OrderHistory orderHistory = new OrderHistory();
                orderHistory.order_no = orderProgess.get(i).getAsJsonObject().get("order_no").getAsString();
                orderHistory.order_date = orderProgess.get(i).getAsJsonObject().get("order_date").getAsString();
                orderHistory.order_time = orderProgess.get(i).getAsJsonObject().get("order_time").getAsString();
                orderHistory.waiter_name = orderProgess.get(i).getAsJsonObject().get("waiter_name").getAsString();
                orderHistory.type = orderProgess.get(i).getAsJsonObject().get("type").getAsString();

                JsonObject food_orders_name = jsonObject.get("food_orders_name").getAsJsonObject();
                JsonArray foodArray = food_orders_name.getAsJsonArray(orderProgess.get(i).getAsJsonObject().get("order_no").getAsString());
                List<String> detailsList = new ArrayList<>();
                for (int j = 0; j < foodArray.size(); j++) {
                    // FoodDetails foodDetails = new FoodDetails();
                    // foodDetails.food_code = foodArray.get(i).getAsJsonObject().get("food_code").getAsString();
                    //foodDetails.name = foodArray.get(i).getAsJsonObject().get("name").getAsString();
                    detailsList.add(foodArray.get(j).getAsJsonObject().get("name").getAsString());
                }
                orderHistory.detailsList = detailsList;
                historyList.add(orderHistory);
            }
        }else{
            String msg= jsonObject.get("msg").getAsString();
            if(isHomeDelivery){
                msg= "No Home delivery Order Found!" ;
            }
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
        
        
        
        return historyList;
    }

}
