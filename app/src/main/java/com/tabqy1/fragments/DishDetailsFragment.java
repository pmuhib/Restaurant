package com.tabqy1.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.tabqy1.adapter.RelatedItemAdapter;
import com.tabqy1.apputils.AppSession;
import com.tabqy1.apputils.AppUtils;
import com.tabqy1.apputils.ItemClickListener;
import com.tabqy1.models.FoodDetailsInfo;
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
 * Use the {@link DishDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DishDetailsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private String foodID;
    private View view;
    private AppCompatActivity activity;
    private RecyclerView rvReleatedItem;
    private ImageView ivItem;
    private ArrayList<FoodType> relatedItmes;
    private RelatedItemAdapter relatedItemAdapter;
    private TextView tvCategories;
    private TextView tvDescription;
    private TextView tvItemName;
    private TextView tvItemPrice,tvAssociate;
    private AppSession session;


    public DishDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AppCompatActivity)context;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment DishDetailsFragment.
     */
    public static DishDetailsFragment newInstance(String param1) {
        DishDetailsFragment fragment = new DishDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            foodID = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_dish_details, container, false);
        session= new AppSession(activity);
        initViews();
        if(AppUtils.isNetworkAvailable(activity)) {
            callFoodDetailApi(foodID);
        }else{
            Toast.makeText(activity,R.string.msg_no_connection,Toast.LENGTH_SHORT).show();
        }

        AppUtils.setAppBackGroundImage(getActivity(),(ViewGroup) view,session.getRestaurantBackgroundImage());
        return view;
    }

    private void initViews(){
        TextView tvBack = (TextView) activity.findViewById(R.id.tv_back);
        tvBack.setVisibility(View.VISIBLE);
        TextView tvTitle=(TextView)activity.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.food_detail);

        ImageButton btnOrder = (ImageButton) activity.findViewById(R.id.btnOrder) ;
        btnOrder.setVisibility(View.VISIBLE);


        ivItem=(ImageView)view.findViewById(R.id.imageView);
        tvDescription=(TextView)view.findViewById(R.id.tv_productDescription);
        tvItemName=(TextView)view.findViewById(R.id.tv_item_name);
        tvAssociate=(TextView)view.findViewById(R.id.tv_associated_value);
        tvItemPrice=(TextView)view.findViewById(R.id.tv_item_price);
        tvCategories=(TextView)view.findViewById(R.id.tv_categories);
        rvReleatedItem=(RecyclerView)view.findViewById(R.id.rv_related_items);

        relatedItmes = new ArrayList<>();

        relatedItemAdapter= new RelatedItemAdapter(activity,relatedItmes);
        rvReleatedItem.setAdapter(relatedItemAdapter);
        rvReleatedItem.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false));
        relatedItemAdapter.setClickListener(itemClickListener);
    }


    private ItemClickListener itemClickListener= new ItemClickListener() {
        @Override
        public void onItemClicked(View view, int position) {

            if(AppUtils.isNetworkAvailable(activity)){
                callFoodDetailApi(relatedItmes.get(position).food_id);
            }else {
                Toast.makeText(activity,R.string.msg_no_connection,Toast.LENGTH_SHORT).show();
            }


        }
    };


    private void callFoodDetailApi(String foodID){

        RetroFitApis retroFitApis = RetrofitApiBuilder.getRetrofitRestaurant().create(RetroFitApis.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("food_id",foodID);
        jsonObject.addProperty("resturant_id",new AppSession(activity).getRestaurantId());
        Call<ApiResponse> apiResponseCall = retroFitApis.getFoodDetail(jsonObject);
        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Response<ApiResponse> response, Retrofit retrofit) {
                if(response.body().status) {
                    if (response.body().fooddetail != null){
                        if(response.body().fooddetail.size()>0)
                        setFoodDetail(response.body().fooddetail.get(0));
                    }
                    if (response.body().relateditems != null){
                        relatedItmes.clear();
                        relatedItmes.addAll(response.body().relateditems);
                        relatedItemAdapter.notifyDataSetChanged();
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

    private void setFoodDetail(FoodDetailsInfo foodType){

        Glide.with(activity).load(RetrofitApiBuilder.BASE_URL_FOODIMAGE+foodType.food_image).skipMemoryCache(true).into(ivItem);
        tvItemName.setText(foodType.name);
        tvItemPrice.setText(session.getCurrency()+foodType.price);
        tvDescription.setText(foodType.description);
        tvCategories.setText(foodType.categories);
        tvAssociate.setText(AppUtils.getConcateAssociate(foodType.associated_food));

    }

}
