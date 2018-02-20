package com.tabqy1.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tabqy1.R;
import com.tabqy1.apputils.AppSession;
import com.tabqy1.apputils.AppUtils;
import com.tabqy1.models.ProfileDetail;
import com.tabqy1.webservices.ApiResponse;
import com.tabqy1.webservices.RetroFitApis;
import com.tabqy1.webservices.RetrofitApiBuilder;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;
    private ImageView ivProfile;
    private TextView tvName;
    private TextView tvDesignationName;
    private TextView tvEmployeeCode;
    private TextView tvTotalCost;
    private AppCompatActivity activity;
    private AppSession appSession;


    public MyProfileFragment() {
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
     * @return A new instance of fragment MyProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyProfileFragment newInstance(String param1, String param2) {
        MyProfileFragment fragment = new MyProfileFragment();
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
        view =  inflater.inflate(R.layout.fragment_my_profile, container, false);
        appSession = new AppSession(activity);
        initView();
        if(AppUtils.isNetworkAvailable(activity)){
            getMyProfileAPI();

        }else{
            Toast.makeText(activity, getString(R.string.msg_no_connection), Toast.LENGTH_SHORT).show();
        }

        AppUtils.setAppBackGroundImage(getActivity(),(ViewGroup) view,appSession.getRestaurantBackgroundImage());

        return  view;
    }

    private void initView(){
        TextView tvBack = (TextView) activity.findViewById(R.id.tv_back);
        tvBack.setVisibility(View.VISIBLE);
        TextView tvTitle=(TextView)activity.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.my_profile);

        ImageButton btnOrder = (ImageButton) activity.findViewById(R.id.btnOrder) ;
        btnOrder.setVisibility(View.GONE);


        ivProfile = (ImageView) view.findViewById(R.id.ivProfile);
        tvName = (TextView) view.findViewById(R.id.tvName);
        tvDesignationName = (TextView) view.findViewById(R.id.tvDesignation);
        tvEmployeeCode = (TextView) view.findViewById(R.id.tvEmployeeCode);
        tvTotalCost = (TextView) view.findViewById(R.id.tvTotalCost);
    }

    public void getMyProfileAPI(){
        AppUtils.showProgress(getActivity(),getResources().getString(R.string.loading_waiter_profile),false);
        RetroFitApis retroFitApis = RetrofitApiBuilder.getRetrofitRestaurant().create(RetroFitApis.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id",appSession.getUserId());

        Call<ApiResponse> apiResponseCall = retroFitApis.getMyProfile(jsonObject);
        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Response<ApiResponse> response, Retrofit retrofit) {
                AppUtils.hideProgress();
                Log.e("Response", new Gson().toJson(response.body()));
                if(response.body().status) {
                    if (response.body().profile_detail != null){
                        ProfileDetail profileDetail = response.body().profile_detail.get(0);
                        tvName.setText(profileDetail.username);
                        tvDesignationName.setText(getResources().getString(R.string.waiter));
                        tvEmployeeCode.setText(profileDetail.emp_code);
                        tvTotalCost.setText(appSession.getCurrency()+response.body().total_cost);
                        Glide.with(activity).load(RetrofitApiBuilder.BASE_URL_EMPIMAGE+profileDetail.profile_image).skipMemoryCache(true).into(ivProfile);

                    }


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
