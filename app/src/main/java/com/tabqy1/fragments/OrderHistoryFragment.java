package com.tabqy1.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.tabqy1.R;
import com.tabqy1.adapter.OrderHistoryRecyclerAdapter;
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
 * Use the {@link OrderHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderHistoryFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;
    private RecyclerView recycler;
    private AppCompatSpinner spinnerTable;
    private AppCompatSpinner spinnerWaiter;
    private AppCompatActivity activity;
    private AppSession appSession;
    private TextView tvToDate,tvTo,tvFromDate,tvFrom,tvGo;
    private boolean isFrom;
    private ArrayList<List<OrderHistory>> orderHistories = new ArrayList<>(2);
    private String fromDate= "";
    private String toDate= "";
    private String tableNo= "";
    private String waiter= "";
    private ArrayList<Waitertable> waitertables;


    public OrderHistoryFragment() {
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
     * @return A new instance of fragment OrderHistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderHistoryFragment newInstance(String param1, String param2) {
        OrderHistoryFragment fragment = new OrderHistoryFragment();
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
        view =  inflater.inflate(R.layout.fragment_order_history, container, false);
        appSession= new AppSession(activity);
        waitertables = new ArrayList<>();
        initView();
        fromDate = AppUtils.getCurrentDate();
        toDate = AppUtils.getCurrentDate();
        tvFromDate.setText(fromDate);
        tvToDate.setText(toDate);

        if(AppUtils.isNetworkAvailable(activity)){
            callTableDetailsApi();
            callGetOrderHistory(toDate,fromDate,tableNo);
        }else {
            Toast.makeText(activity,R.string.msg_no_connection,Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    public void initView(){
        TextView tvBack = (TextView) activity.findViewById(R.id.tv_back);
        tvBack.setVisibility(View.VISIBLE);
        TextView tvTitle=(TextView)activity.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.order_history);

        ImageButton btnOrder = (ImageButton) activity.findViewById(R.id.btnOrder) ;
        btnOrder.setVisibility(View.GONE);

        recycler = (RecyclerView) view.findViewById(R.id.rvOrderHistory) ;
        spinnerTable = (AppCompatSpinner) view.findViewById(R.id.spinnerTable);
        spinnerWaiter = (AppCompatSpinner) view.findViewById(R.id.spinnerWaiter);

        tvFromDate= (TextView)view.findViewById(R.id.tv_from_date_value);
        tvFrom= (TextView)view.findViewById(R.id.tv_from_date);
        tvTo= (TextView)view.findViewById(R.id.tv_to_date);
        tvToDate= (TextView)view.findViewById(R.id.tv_to_date_value);
        tvGo= (TextView)view.findViewById(R.id.tvGo);

        tvFrom.setOnClickListener(this);
        tvFromDate.setOnClickListener(this);
        tvTo.setOnClickListener(this);
        tvToDate.setOnClickListener(this);
        tvGo.setOnClickListener(this);
        spinnerWaiter.setVisibility(View.GONE);

        spinnerTable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(waitertables.size()>0) {
                    tableNo = waitertables.get(i).table_id;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerWaiter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
    protected void setupRecycler(){
        OrderHistoryRecyclerAdapter adapter = new OrderHistoryRecyclerAdapter(activity,orderHistories);
        recycler.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(layoutManager);
    }

    private void
    callGetOrderHistory(String toDate,String fromDate,String tableId ){
        //toDate="";
        AppUtils.showProgress(getActivity(),getResources().getString(R.string.loading_order_history),false);
        RetroFitApis retroFitApis = RetrofitApiBuilder.getRetrofitRestaurant().create(RetroFitApis.class);

        JsonObject jsonObject = new JsonObject();
        // jsonObject.addProperty("resturant_id", appSession.getRestaurantId());
        jsonObject.addProperty("user_id", appSession.getUserId());
        jsonObject.addProperty("to_date", toDate);
        jsonObject.addProperty("from_date", fromDate);
        jsonObject.addProperty("table_id",tableId);
        Log.e("request","request"+jsonObject.toString());
        Call<ApiResponse> apiResponseCall = retroFitApis.getOrderHistory(jsonObject);
        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Response<ApiResponse> response, Retrofit retrofit) {
                AppUtils.hideProgress();
                orderHistories.clear();
                if(response.body().status) {

                    if(response.body().order_history!=null){

                        ArrayList<OrderHistory> orderInProgress = new ArrayList<OrderHistory>();
                        ArrayList<OrderHistory> orderCompleted = new ArrayList<OrderHistory>();

                        for (int i = 0; i < response.body().order_history.size() ; i++) {
                            if (response.body().order_history.get(i).order_status.equals("0")){
                                orderInProgress.add(response.body().order_history.get(i));
                            }else{
                                orderCompleted.add(response.body().order_history.get(i));
                            }

                        }
                        orderHistories.add(0,orderInProgress);
                        orderHistories.add(1,orderCompleted);

                    }

                }else{
                    final List<OrderHistory> orderH =  new ArrayList<>();
                    orderHistories.add(0,orderH);
                    orderHistories.add(1,orderH);
                    Toast.makeText(activity, response.body().msg, Toast.LENGTH_SHORT).show();
                }

                setupRecycler();
            }

            @Override
            public void onFailure(Throwable t) {
                AppUtils.hideProgress();
                Toast.makeText(activity, R.string.server_error, Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onClick(View view) {
        DialogFragment picker;
        switch (view.getId()){

            case R.id.tv_from_date :
            case R.id.tv_from_date_value:
                picker = new DatePickerFragment(dateSetListener);
                picker.show(getFragmentManager(), "datePicker");
                isFrom=true;

                break;

            case R.id.tv_to_date :
            case R.id.tv_to_date_value:
                picker = new DatePickerFragment(dateSetListener);
                picker.show(getFragmentManager(), "datePicker");
                isFrom=false;

                break;

            case R.id.tvGo :
                if(AppUtils.isNetworkAvailable(activity)){
                    callGetOrderHistory(toDate,fromDate,tableNo);
                }else {
                    Toast.makeText(activity,R.string.msg_no_connection,Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }



    DatePickerFragment.DateSetListener dateSetListener = new DatePickerFragment.DateSetListener() {
        @Override
        public void returnDate(String date) {

            if(isFrom){
                fromDate=date;
                tvFromDate.setText(date);
            }else {
                toDate=date;
                tvToDate.setText(date);
            }

        }
    };

    private void callTableDetailsApi(){

        RetroFitApis retroFitApis = RetrofitApiBuilder.getRetrofitRestaurant().create(RetroFitApis.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", appSession.getUserId());

        Log.e("request","request"+jsonObject.toString());


        Call<ApiResponse> apiResponseCall = retroFitApis.getAssignTable(jsonObject);
        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Response<ApiResponse> response, Retrofit retrofit) {
                if(response.body().status) {
                    waitertables.clear();
                    Waitertable waitertable = new Waitertable();
                    waitertable.table_id = "";
                    waitertable.table_name = "All";
                    waitertable.resturant_id = appSession.getRestaurantId();
                    waitertables.add(waitertable);
                    waitertables.addAll( response.body().waitertable);
                    if(waitertables.size()>0){
                        ArrayAdapter<Waitertable> adapter =
                                new ArrayAdapter<Waitertable>(activity.getApplicationContext(),R.layout.spinner_item, waitertables);
                        adapter.setDropDownViewResource(R.layout.spinner_item);

                        spinnerTable.setAdapter(adapter);
                    }

                }else{
                    //Toast.makeText(activity, response.body().msg, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(activity, R.string.server_error, Toast.LENGTH_SHORT).show();
            }
        });


    }


}


