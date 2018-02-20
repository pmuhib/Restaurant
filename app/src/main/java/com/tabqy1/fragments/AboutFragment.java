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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.tabqy1.R;
import com.tabqy1.adapter.TeamAdapter;
import com.tabqy1.apputils.AppSession;
import com.tabqy1.apputils.AppUtils;
import com.tabqy1.models.TeamMember;
import com.tabqy1.webservices.ApiResponse;
import com.tabqy1.webservices.RetroFitApis;
import com.tabqy1.webservices.RetrofitApiBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class AboutFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    private AppCompatActivity activity;
    private View view;

    private TextView tvDescriptionTitle;
    private TextView tvDescription;
    private ImageView imageView;

    private RecyclerView rvTeam;

    private AppSession appSession;
    private TeamAdapter teamAdapter;
    private List<TeamMember> teamMembers;

    public AboutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AboutFragment.
     */
    public static AboutFragment newInstance(String param1, String param2) {
        AboutFragment fragment = new AboutFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_about, container, false);
        appSession = new AppSession(activity);
        initViews();
        if(AppUtils.isNetworkAvailable(activity)){
            callAboutRestaurantApi();

        }else{
            Toast.makeText(activity, getString(R.string.msg_no_connection), Toast.LENGTH_SHORT).show();
        }

        AppUtils.setAppBackGroundImage(getActivity(),(ViewGroup) view,appSession.getRestaurantBackgroundImage());

        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AppCompatActivity)context;
    }


    private void initViews(){

        TextView tvTitle=(TextView)activity.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.about_restaurant);
        TextView tvBack = (TextView) activity.findViewById(R.id.tv_back);
        tvBack.setVisibility(View.VISIBLE);

        ImageButton btnOrder = (ImageButton) activity.findViewById(R.id.btnOrder) ;
        btnOrder.setVisibility(View.GONE);


        rvTeam=(RecyclerView)view.findViewById(R.id.rv_team) ;
        rvTeam.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL, false));
        imageView= (ImageView)view.findViewById(R.id.imageView);
        tvDescriptionTitle= (TextView) view.findViewById(R.id.tvDescriptionTitle);
        tvDescription= (TextView) view.findViewById(R.id.tvDescription);
        teamMembers= new ArrayList<>();
        teamAdapter= new TeamAdapter(activity,teamMembers);
        rvTeam.setAdapter(teamAdapter);


    }


    private void callAboutRestaurantApi(){
        AppUtils.showProgress(getActivity(),getResources().getString(R.string.loading_restaurant_profile),false);
        RetroFitApis retroFitApis = RetrofitApiBuilder.getRetrofitRestaurant().create(RetroFitApis.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("resturant_id",appSession.getRestaurantId());
        Call<ApiResponse> apiResponseCall = retroFitApis.getAboutRestaurant(jsonObject);
        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Response<ApiResponse> response, Retrofit retrofit) {
                AppUtils.hideProgress();
                Log.e("Response",response.body().toString());
                if(response.body().status) {
                    if (response.body().about_restaurant != null){

                        Glide.with(activity).load(RetrofitApiBuilder.BASE_URL_RESTURANT_IMAGE+response.body().about_restaurant.get(0).restaurant_image).skipMemoryCache(true).into(imageView);
                        tvDescription.setText(response.body().about_restaurant.get(0).description);
                        teamMembers.clear();
                        teamMembers.addAll(response.body().our_team);
                        teamAdapter.notifyDataSetChanged();
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
