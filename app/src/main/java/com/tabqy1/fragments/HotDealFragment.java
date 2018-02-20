package com.tabqy1.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.google.gson.JsonObject;
import com.tabqy1.R;
import com.tabqy1.activity.DashBoradActivity;
import com.tabqy1.adapter.HotDealsRecyclerAdapter;
import com.tabqy1.apputils.AppSession;
import com.tabqy1.apputils.AppUtils;
import com.tabqy1.models.FoodType;
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
 * Use the {@link HotDealFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HotDealFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recycler;
    private AppCompatActivity activity;
    private View view;
    private List<FoodType> hotDeals;
    private HotDealsRecyclerAdapter adapter;
    private AppSession appSession;

    public HotDealFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity=(AppCompatActivity)context;
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HotDealFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HotDealFragment newInstance(String param1, String param2) {
        HotDealFragment fragment = new HotDealFragment();
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
        view=inflater.inflate(R.layout.fragment_hot_deal, container, false);
        appSession = new AppSession(activity);
        initView();
        setupRecycler();
        if(AppUtils.isNetworkAvailable(activity)) {
            callGetTodaysSpecialApi();
        }else{
            Toast.makeText(activity,R.string.msg_no_connection,Toast.LENGTH_SHORT).show();
        }

        AppUtils.setAppBackGroundImage(getActivity(),(ViewGroup) view,appSession.getRestaurantBackgroundImage());
        return  view;
    }

    private void initView(){
        TextView tvBack = (TextView) activity.findViewById(R.id.tv_back);
        tvBack.setVisibility(View.VISIBLE);
        TextView tvTitle=(TextView)activity.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.hot_deal);

        ImageButton btnOrder = (ImageButton) activity.findViewById(R.id.btnOrder) ;
        btnOrder.setVisibility(View.VISIBLE);

        recycler = (RecyclerView) view.findViewById(R.id.rvTodaySpecial) ;


    }


    protected void setupRecycler(){

        hotDeals = new ArrayList<>();
         adapter = new HotDealsRecyclerAdapter(activity,hotDeals);
        recycler.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(layoutManager);
    }

    private void callGetTodaysSpecialApi(){
        AppUtils.showProgress(getActivity(),getResources().getString(R.string.loading_today_hot_deals),false);
        RetroFitApis retroFitApis = RetrofitApiBuilder.getRetrofitRestaurant().create(RetroFitApis.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("resturant_id", appSession.getRestaurantId());
        Call<ApiResponse> apiResponseCall = retroFitApis.getHotDeals(jsonObject);
        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Response<ApiResponse> response, Retrofit retrofit) {
                AppUtils.hideProgress();
                Log.e("Response",response.body().toString());
                if(response.body().status) {
                    hotDeals.clear();
                    hotDeals.addAll(((DashBoradActivity)activity).getExistingFoodType(response.body().hotdeals));
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
}
