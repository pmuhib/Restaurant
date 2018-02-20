package com.tabqy1.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.tabqy1.R;
import com.tabqy1.adapter.MenuItemAdapter;
import com.tabqy1.apputils.AppSession;
import com.tabqy1.apputils.AppUtils;
import com.tabqy1.models.Menu;
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
 * Use the {@link MenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuFragment extends Fragment  {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private AppCompatActivity activity;
    private View view;
    private TextView tvProductPrice,tvProductName,tvProductDescription;
    private ImageView ivProductImage;
    private List<Menu> menuList;
    private MenuItemAdapter adapter;
    private AppSession appSession;

    public MenuFragment() {
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
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MenuFragment.
     */
    public static MenuFragment newInstance(String param1, String param2) {
        MenuFragment fragment = new MenuFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_menu, container, false);
        appSession = new AppSession(activity);
        initView();
        if(AppUtils.isNetworkAvailable(activity)) {
            callMenuItemApi();
           // callGetHotDealsApi();
        }else{
            Toast.makeText(activity,R.string.msg_no_connection,Toast.LENGTH_SHORT).show();
        }

        AppUtils.setAppBackGroundImage(getActivity(),(ViewGroup) view,appSession.getRestaurantBackgroundImage());
        return view;
    }


    private void initView(){
        TextView tvBack = (TextView) activity.findViewById(R.id.tv_back);
        tvBack.setVisibility(View.VISIBLE);
        TextView tvTitle=(TextView)activity.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.menu);

        ImageButton btnOrder = (ImageButton) activity.findViewById(R.id.btnOrder) ;
        btnOrder.setVisibility(View.VISIBLE);


        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.menu_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(activity,2);
        recyclerView.setLayoutManager(layoutManager);

        menuList= new ArrayList<>();
        adapter = new MenuItemAdapter(activity,menuList);
        recyclerView.setAdapter(adapter);


        tvProductDescription=(TextView)view.findViewById(R.id.tv_productDescription);
        tvProductPrice=(TextView)view.findViewById(R.id.tv_productPrice);
        tvProductName=(TextView)view.findViewById(R.id.tv_productName);
        ivProductImage=(ImageView)view.findViewById(R.id.iv_product_image);

        Glide.with(activity).load( RetrofitApiBuilder.BASE_URL_MENU_BACKGROUND+appSession.getMenuImage()).skipMemoryCache(true).into(ivProductImage);



    }

    private void callMenuItemApi(){
        AppUtils.showProgress(getActivity(),getResources().getString(R.string.loading_menu_option),false);
        RetroFitApis retroFitApis = RetrofitApiBuilder.getRetrofitRestaurant().create(RetroFitApis.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("resturant_id",appSession.getRestaurantId());
        Call<ApiResponse> apiResponseCall = retroFitApis.getMenu(jsonObject);
        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Response<ApiResponse> response, Retrofit retrofit) {
                AppUtils.hideProgress();
                Log.e("Response",response.body().toString());
                if(response.body().status) {
                    menuList.clear();
                    menuList.addAll(response.body().menu);
                    adapter.notifyDataSetChanged();


                }else{
                    Toast.makeText(activity, response.body().msg, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                AppUtils.hideProgress();
                Toast.makeText(activity, R.string.server_error, Toast.LENGTH_SHORT).show();
            }
        });


        }


    private void callGetHotDealsApi(){

        RetroFitApis retroFitApis = RetrofitApiBuilder.getRetrofitRestaurant().create(RetroFitApis.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("resturant_id", appSession.getRestaurantId());
        Call<ApiResponse> apiResponseCall = retroFitApis.getHotDeals(jsonObject);
        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Response<ApiResponse> response, Retrofit retrofit) {
                Log.e("Response",response.body().toString());
                if(response.body().status) {

                    if(response.body().hotdeals!=null && response.body().hotdeals.size()>0){

                        tvProductDescription.setText(response.body().hotdeals.get(0).description);
                        tvProductPrice.setText(appSession.getCurrency()+response.body().hotdeals.get(0).price);
                        tvProductName.setText(response.body().hotdeals.get(0).name);
                        Glide.with(activity).load( RetrofitApiBuilder.BASE_URL_FOODIMAGE+response.body().hotdeals.get(0).food_image).skipMemoryCache(true).into(ivProductImage);
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

