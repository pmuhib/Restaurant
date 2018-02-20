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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.tabqy1.R;
import com.tabqy1.activity.DashBoradActivity;
import com.tabqy1.adapter.SearchListRecyclerAdapter;
import com.tabqy1.apputils.AppSession;
import com.tabqy1.apputils.AppUtils;
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
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private AppCompatActivity activity;
    private View view ;
    private RecyclerView recycler;
    private ArrayList<FoodType> foodTypes;
    private FoodType hotDeals;
    private AppSession appSession;
    private SearchListRecyclerAdapter adapter;
    private TextView tvHotDealProductName,tvHotDealProductDescription,tvHotDealProductPrice;
    private Button btnSearch;
    private EditText etSearch;
    private ImageView ivProductImage;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity=(AppCompatActivity)context;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_search, container, false);
        appSession= new AppSession(activity);

        initView();
        setupRecycler();

        if(AppUtils.isNetworkAvailable(activity)){
            callGetHotDealApi();
        }else {
            Toast.makeText(activity,R.string.msg_no_connection,Toast.LENGTH_SHORT).show();

        }

        AppUtils.setAppBackGroundImage(getActivity(),(ViewGroup) view,appSession.getRestaurantBackgroundImage());

        return view;
    }


    private void initView(){
        TextView tvBack = (TextView) activity.findViewById(R.id.tv_back);
        tvBack.setVisibility(View.VISIBLE);

        ImageButton btnOrder = (ImageButton) activity.findViewById(R.id.btnOrder) ;
        btnOrder.setVisibility(View.VISIBLE);


        tvHotDealProductName= (TextView)view.findViewById(R.id.tv_productName);
        tvHotDealProductDescription= (TextView)view.findViewById(R.id.tv_productDescription);
        tvHotDealProductPrice= (TextView)view.findViewById(R.id.tv_productPrice);
        etSearch= (EditText)view.findViewById(R.id.et_search);
        btnSearch= (Button) view.findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(this);

        TextView tvTitle=(TextView)activity.findViewById(R.id.tv_title);
        recycler=(RecyclerView) view.findViewById(R.id.rvSearchOrder);
        tvTitle.setText(R.string.search);
        ivProductImage=(ImageView)view.findViewById(R.id.iv_product_image);

    }
    protected void setupRecycler(){
        foodTypes= new ArrayList<>();
        adapter = new SearchListRecyclerAdapter(activity,foodTypes);
        recycler.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(layoutManager);
    }




    private void callSearchFoodApi(String searchText){
        AppUtils.showProgress(getActivity(),getResources().getString(R.string.searching_food),false);
        RetroFitApis retroFitApis = RetrofitApiBuilder.getRetrofitRestaurant().create(RetroFitApis.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("resturant_id", appSession.getRestaurantId());
        jsonObject.addProperty("food_name", searchText);
        Log.e("request","request"+jsonObject.toString());

        Call<ApiResponse> apiResponseCall = retroFitApis.getFoodSearch(jsonObject);
        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Response<ApiResponse> response, Retrofit retrofit) {
                AppUtils.hideProgress();
                Log.e("Response",response.body().toString());
                if(response.body().status) {

                    if(response.body().foodsearch!=null){
                        foodTypes.clear();
                        foodTypes.addAll(((DashBoradActivity)activity).getExistingFoodType(response.body().foodsearch));
                        adapter.notifyDataSetChanged();

                    }
                }else{
                    foodTypes.clear();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(activity, "No food item found!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                AppUtils.hideProgress();
                Toast.makeText(activity, R.string.server_error, Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void callGetHotDealApi(){

        RetroFitApis retroFitApis = RetrofitApiBuilder.getRetrofitRestaurant().create(RetroFitApis.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("resturant_id", appSession.getRestaurantId());
        Call<ApiResponse> apiResponseCall = retroFitApis.getHotDeals(jsonObject);
        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Response<ApiResponse> response, Retrofit retrofit) {
                if(response.body().status) {
                    if(response.body().hotdeals!=null  && response.body().hotdeals.size()>0){
                        loadHotDeal(response.body().hotdeals.get(0));
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

    private void loadHotDeal(FoodType foodType) {

        tvHotDealProductPrice.setText(appSession.getCurrency()+foodType.price);
        tvHotDealProductName.setText(foodType.name);
        tvHotDealProductDescription.setText(foodType.description);
        String imageUrl= RetrofitApiBuilder.BASE_URL_FOODIMAGE + foodType.food_image;
        Glide.with(activity).load(imageUrl).into(ivProductImage);



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_search :
                String search= etSearch.getText().toString().trim();

                if(search.length()>0){

                    if(AppUtils.isNetworkAvailable(activity))
                        callSearchFoodApi(search);
                    else{
                        Toast.makeText(activity,R.string.msg_no_connection,Toast.LENGTH_SHORT).show();
                    }
                }else{
                    foodTypes.clear();
                    adapter.notifyDataSetChanged();
                }


                break;

        }

    }
}
