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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.tabqy1.R;
import com.tabqy1.activity.DashBoradActivity;
import com.tabqy1.adapter.MenuSubTypeRecyclerAdapter;
import com.tabqy1.adapter.MenuTypeRecyclerAdapter;
import com.tabqy1.apputils.AppSession;
import com.tabqy1.apputils.AppUtils;
import com.tabqy1.apputils.ItemClickListener;
import com.tabqy1.models.Cusinie;
import com.tabqy1.models.FoodType;
import com.tabqy1.webservices.ApiResponse;
import com.tabqy1.webservices.RetroFitApis;
import com.tabqy1.webservices.RetrofitApiBuilder;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


/**
 * Created by jayant on 17-10-2016.
 */

public class MenuListFragment extends Fragment implements View.OnClickListener, TabLayout.OnTabSelectedListener, ItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String MENU_ID = "menuID";
    private static final String MENU_NAME = "menuName";

    // TODO: Rename and change types of parameters
    private String menuId;
    private String menuName;
    private View view;
    private RecyclerView rvMenuDishType, rvMenuSubOrder;
    private AppCompatActivity activity;
    private Button btnHomeDelivery, btnAddMore, btnSubmitOrder;
    private TextView tvTextWithTableNo;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Context context;
    private MenuTypeRecyclerAdapter menuTypeRecyclerAdapter;
    private MenuSubTypeRecyclerAdapter menuSubTypeRecyclerAdapter ;
    private ArrayList<Cusinie>  cusinies;
    private ArrayList<FoodType>  foodTypes;
    private ArrayList<FoodType>  foods;
    private String cusinieId;
    private AppSession appSession;
    private TextView tvFoodType;


    public MenuListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity=(AppCompatActivity)context;
        this.context = context;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MenuListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MenuListFragment newInstance(String menuId, String param2) {
        MenuListFragment fragment = new MenuListFragment();
        Bundle args = new Bundle();
        args.putString(MENU_ID, menuId);
        args.putString(MENU_NAME, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            menuId = getArguments().getString(MENU_ID);
            menuName = getArguments().getString(MENU_NAME);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_menu_sub_order, container, false); initView();
        //Initializing the tablayout
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        appSession = new AppSession(activity);
        setupRecycler();
        setupPager();
        callGetCusinesApi();

        AppUtils.setAppBackGroundImage(getActivity(),(ViewGroup) view,appSession.getRestaurantBackgroundImage());

        return  view;
    }
    public void initView(){
        TextView tvBack = (TextView) activity.findViewById(R.id.tv_back);
        tvBack.setVisibility(View.VISIBLE);
        TextView tvTitle=(TextView)activity.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.order_list);


        ImageButton btnOrder = (ImageButton) activity.findViewById(R.id.btnOrder) ;
        btnOrder.setVisibility(View.VISIBLE);


        rvMenuDishType = (RecyclerView) view.findViewById(R.id.rvMenuDishType) ;
        rvMenuSubOrder = (RecyclerView) view.findViewById(R.id.rvMenuSubOrder) ;
        tvFoodType = (TextView) view.findViewById(R.id.tv_food_type) ;
        if(menuName!=null) {
            tvFoodType.setText(menuName);
        }else{
            tvFoodType.setText("");
        }


    }
    protected void setupRecycler(){

        foodTypes =  new ArrayList<>();

        cusinies = new ArrayList<>();
        menuTypeRecyclerAdapter = new MenuTypeRecyclerAdapter(getContext(),cusinies);
        rvMenuDishType.setAdapter(menuTypeRecyclerAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvMenuDishType.setLayoutManager(layoutManager);
        menuTypeRecyclerAdapter.setClickListener(this);

        foods = new ArrayList<>();
        menuSubTypeRecyclerAdapter = new MenuSubTypeRecyclerAdapter(activity,foods);
        rvMenuSubOrder.setAdapter(menuSubTypeRecyclerAdapter);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext());
        rvMenuSubOrder.setLayoutManager(layoutManager1);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        }
    }


    public void setupPager(){
        tabLayout.removeAllTabs();

        for (int i = 0; i <foodTypes.size() ; i++) {
            //Adding the tabs using addTab() method
            tabLayout.addTab(tabLayout.newTab().setCustomView(getTabView(foodTypes.get(i).dish_name)));
        }
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

        if(foodTypes.size()>0)
        callGetFoodsApi(cusinieId,foodTypes.get(tab.getPosition()).foodtype_id);

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

    @Override
    public void onItemClicked(View view, int position) {

        cusinieId = cusinies.get(position).cusinie_id;
        callGetFoodTypeApi(cusinieId);
        for (int i = 0; i < cusinies.size(); i++) {
            cusinies.get(i).isSelected= false;
        }
        cusinies.get(position).isSelected =true;
        menuTypeRecyclerAdapter.notifyDataSetChanged();

    }


    public View getTabView(String text) {
        // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
        View v = LayoutInflater.from(context).inflate(R.layout.tab_menu_item, null);
        TextView tv = (TextView) v.findViewById(R.id.tvTabTitle);
        tv.setText(text);

        return v;
    }


    private void callGetCusinesApi(){
        AppUtils.showProgress(getActivity(),String.format(getResources().getString(R.string.loading_cuisines),menuName),false);
        RetroFitApis retroFitApis = RetrofitApiBuilder.getRetrofitRestaurant().create(RetroFitApis.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("resturant_id",appSession.getRestaurantId());

        Log.e("request","request"+jsonObject.toString());

        Call<ApiResponse> apiResponseCall = retroFitApis.getCusine(jsonObject);
        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Response<ApiResponse> response, Retrofit retrofit) {

                Log.e("Response",response.body().toString());
                if(response.body().status) {
                    cusinies.clear();
                    if (response.body().cusinie != null){
                        cusinies.addAll(response.body().cusinie);
                        cusinies.get(0).isSelected=true;
                        menuTypeRecyclerAdapter.notifyDataSetChanged();
                        cusinieId = cusinies.get(0).cusinie_id;
                        callGetFoodTypeApi(cusinieId);
                    }else{
                        AppUtils.hideProgress();
                    }

                }else{
                    AppUtils.hideProgress();
                   // Toast.makeText(activity, response.body().msg, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                AppUtils.hideProgress();
                Toast.makeText(activity, R.string.server_error, Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void callGetFoodTypeApi(final String cusineId){
        AppUtils.showProgress(getActivity(),getResources().getString(R.string.loading_food_types),false);
        RetroFitApis retroFitApis = RetrofitApiBuilder.getRetrofitRestaurant().create(RetroFitApis.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("resturant_id",appSession.getRestaurantId());
        jsonObject.addProperty("cusinie_id",cusineId);

        Log.e("request","request"+jsonObject.toString());


        Call<ApiResponse> apiResponseCall = retroFitApis.getFoodType(jsonObject);
        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Response<ApiResponse> response, Retrofit retrofit) {

                Log.e("Response",response.body().toString());
                if(response.body().status) {
                    foodTypes.clear();
                    if (response.body().foodtype != null){
                        foodTypes.addAll(response.body().foodtype);
                        callGetFoodsApi(cusineId, foodTypes.get(0).foodtype_id );

                    }
                    else{
                        AppUtils.hideProgress();
                    }


                }else{
                    AppUtils.hideProgress();
                    foodTypes.clear();
                   // Toast.makeText(activity, response.body().msg, Toast.LENGTH_SHORT).show();
                }

                setupPager();

            }

            @Override
            public void onFailure(Throwable t) {
                AppUtils.hideProgress();
                Toast.makeText(activity, R.string.server_error, Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void callGetFoodsApi(String cusineId, String foodTypeId){
        AppUtils.showProgress(getActivity(),getResources().getString(R.string.fetching_food_list),false);
        RetroFitApis retroFitApis = RetrofitApiBuilder.getRetrofitRestaurant().create(RetroFitApis.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("resturant_id",appSession.getRestaurantId());
        jsonObject.addProperty("cusinie_id",cusineId);
        jsonObject.addProperty("foodtype_id",foodTypeId);
        jsonObject.addProperty("menu_id",menuId);

        Log.e("request","request"+jsonObject.toString());


        Call<ApiResponse> apiResponseCall = retroFitApis.getFoods(jsonObject);
        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Response<ApiResponse> response, Retrofit retrofit) {
                AppUtils.hideProgress();
                if(response!=null && response.body().status) {
                    foods.clear();
                    if (response.body().food != null){
                        foods.addAll(((DashBoradActivity)activity).getExistingFoodType(response.body().food));
                        menuSubTypeRecyclerAdapter.notifyDataSetChanged();
                    }
                } else {
                    foods.clear();
                    menuSubTypeRecyclerAdapter.notifyDataSetChanged();
                    //Toast.makeText(activity, response.body().msg, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                AppUtils.hideProgress();
                Toast.makeText(activity, R.string.server_error, Toast.LENGTH_SHORT).show();
            }
        });


    }

}
