package com.tabqy1.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
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
import com.tabqy1.adapter.TableGridAdapter;
import com.tabqy1.apputils.AppSession;
import com.tabqy1.apputils.AppUtils;
import com.tabqy1.models.Waitertable;
import com.tabqy1.uiutils.GridSpacingItemDecoration;
import com.tabqy1.webservices.ApiResponse;
import com.tabqy1.webservices.RetroFitApis;
import com.tabqy1.webservices.RetrofitApiBuilder;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class SelectTableFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TableGridAdapter adapter;
    private AppSession appSession;
    private FragmentActivity activity;
    private View view;
    private ArrayList<Waitertable> waitertables;

    public SelectTableFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SelectTableFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SelectTableFragment newInstance(String param1, String param2) {
        SelectTableFragment fragment = new SelectTableFragment();
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
        view =  inflater.inflate(R.layout.fragment_select_table, container, false);
        appSession= new AppSession(activity);
        initViews();

        if(AppUtils.isNetworkAvailable(activity)){
            callTableDetailsApi();
        }else {
            Toast.makeText(activity,R.string.msg_no_connection,Toast.LENGTH_SHORT).show();
        }

        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (FragmentActivity)context;

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }



    private void initViews(){
        TextView tvBack = (TextView) activity.findViewById(R.id.tv_back);
        tvBack.setVisibility(View.VISIBLE);
        TextView tvTitle=(TextView)activity.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.table);

        ImageButton btnOrder = (ImageButton) activity.findViewById(R.id.btnOrder) ;
        btnOrder.setVisibility(View.GONE);


        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(activity,4);
        recyclerView.setLayoutManager(layoutManager);
        int spanCount = 4; // 4 columns
        int spacing = activity.getResources().getDimensionPixelSize(R.dimen.dp_24); // 50px
        boolean includeEdge = true;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        waitertables= new ArrayList<>();
        adapter = new TableGridAdapter(activity,waitertables);
        recyclerView.setAdapter(adapter);


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
                AppUtils.hideProgress();
                if(response.body().status) {
                    waitertables.clear();
                    waitertables.addAll( response.body().waitertable);
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
