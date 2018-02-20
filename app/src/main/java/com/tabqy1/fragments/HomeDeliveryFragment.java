package com.tabqy1.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.tabqy1.R;
import com.tabqy1.activity.DashBoradActivity;
import com.tabqy1.apputils.AppSession;
import com.tabqy1.apputils.AppUtils;
import com.tabqy1.webservices.ApiResponse;
import com.tabqy1.webservices.RetroFitApis;
import com.tabqy1.webservices.RetrofitApiBuilder;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeDeliveryFragment} factory method to
 * create an instance of this fragment.
 */
public class HomeDeliveryFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_ORDERID = "orderId";
    private static final String ARG_PARAM_ORDERDATE = "orderDate";
    private static final String ARG_PARAM_ORDERTIME = "orderTime";
    private static final String ARG_PARAM_TOTALCOST= "totalCost";
    private static final String ARG_PARAM_IS_FEEDBACK= "isFeedback";

    private String orderId;
    private String orderDate;
    private String orderTime;
    private String totalCost;
    private View view;
    private EditText etOrderId, etCustomerName,etTime,etDate, etTotalCost,etCustomerAddress,etCustomerPhone;
    private Button btnSubmit;
    private AppCompatActivity activity;
    private boolean isFeedback;

    public HomeDeliveryFragment() {
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
     *
     * @return A new instance of fragment HomeDeliveryFragment.
     */
    public static HomeDeliveryFragment newInstance(String orderId, String orderDate,String orderTime, String totalCost, boolean isFeedback) {
        HomeDeliveryFragment fragment = new HomeDeliveryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_ORDERID, orderId);
        args.putString(ARG_PARAM_ORDERDATE, orderDate);
        args.putString(ARG_PARAM_ORDERTIME, orderTime);
        args.putString(ARG_PARAM_TOTALCOST, totalCost);
        args.putBoolean(ARG_PARAM_IS_FEEDBACK, isFeedback);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            orderId = getArguments().getString(ARG_PARAM_ORDERID);
            orderDate = getArguments().getString(ARG_PARAM_ORDERDATE);
            orderTime = getArguments().getString(ARG_PARAM_ORDERTIME);
            totalCost = getArguments().getString(ARG_PARAM_TOTALCOST);
            isFeedback = getArguments().getBoolean(ARG_PARAM_IS_FEEDBACK);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_delivery, container, false);
        ((DashBoradActivity) activity).setDrawerEnabled(false);
        initView(view);
        return view;
    }

    @Override
    public void onDestroyView() {
        ((DashBoradActivity) activity).setDrawerEnabled(true);
        super.onDestroyView();
    }

    private void initView(View view) {
        TextView tvBack = (TextView) activity.findViewById(R.id.tv_back);
        tvBack.setVisibility(View.VISIBLE);
        TextView tvTitle=(TextView)activity.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.home_delivery);
        etOrderId=(EditText)view.findViewById(R.id.et_order_id);
        etDate=(EditText)view.findViewById(R.id.et_date);
        etTime=(EditText)view.findViewById(R.id.et_time);
        etCustomerName=(EditText)view.findViewById(R.id.et_customer_name);
        etCustomerAddress=(EditText)view.findViewById(R.id.et_customer_name);
        etCustomerPhone=(EditText)view.findViewById(R.id.et_customer_ph_no);
        etTotalCost=(EditText)view.findViewById(R.id.et_total_cost);
        btnSubmit=(Button) view.findViewById(R.id.btn_submit);

        etTotalCost.setEnabled(false);
        etOrderId.setText(orderId);
        etDate.setText(orderDate);
        etTime.setText(orderTime);
        AppSession appSession = new AppSession(activity);
        etTotalCost.setText(appSession.getCurrency()+totalCost);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isValid()) {
                    callSaveCustomerInformation();
                }
            }
        });
        etOrderId.setEnabled(false);
        etOrderId.setFocusable(false);

        etDate.setEnabled(false);
        etDate.setFocusable(false);

        etTime.setEnabled(false);
        etTime.setFocusable(false);

    }
    private void callSaveCustomerInformation(){

        AppUtils.showProgress(getActivity(),getResources().getString(R.string.saving_customer_info),false);

        RetroFitApis retroFitApis = RetrofitApiBuilder.getRetrofitRestaurant().create(RetroFitApis.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("order_no",orderId);
        jsonObject.addProperty("order_date",orderDate);
        jsonObject.addProperty("order_time",orderTime);
        jsonObject.addProperty("totalcost",totalCost);
        jsonObject.addProperty("customer_name",etCustomerName.getText().toString().trim());
        jsonObject.addProperty("customer_address",etCustomerAddress.getText().toString().trim());
        jsonObject.addProperty("phone_no",etCustomerPhone.getText().toString().trim());

        Log.e("request","request"+jsonObject.toString());
        Call<ApiResponse> apiResponseCall = retroFitApis.saveCustomerInformation(jsonObject);
        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Response<ApiResponse> response, Retrofit retrofit) {
                AppUtils.hideProgress();
                Log.e("Response",response.body().toString());
                if(response.body().status) {
                   ((DashBoradActivity) activity).clearOrder();


                    if(isFeedback){
                        FeedBackFragmentNew feedBackFragmentNew = FeedBackFragmentNew.newInstance();
                        AppUtils.setFragment(feedBackFragmentNew, true, activity, R.id.fragmentContainer);
                    }


                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Send request to print order to kitchen");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            RecipeFragment homeFragment = RecipeFragment.newInstance(orderId,"",true,false);
                            AppUtils.setFragment(homeFragment, true, activity, R.id.fragmentContainer);
                        }
                    });

                    builder.setCancelable(false);
                    builder.create() ;
                    builder.show() ;

//                    HomeFragment homeFragment = HomeFragment.newInstance("", "");
//                    AppUtils.setFragment(homeFragment, true, activity, R.id.fragmentContainer);
                    Toast.makeText(activity, response.body().msg, Toast.LENGTH_SHORT).show();

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


    private boolean isValid(){
        if(etCustomerName.getText().toString().trim().length()==0){
            Toast.makeText(activity,"Please enter customer name",Toast.LENGTH_LONG).show();

            return false;
        }if(etCustomerPhone.getText().toString().trim().length()==0){
            Toast.makeText(activity,"Please enter customer phone number",Toast.LENGTH_LONG).show();

            return false;
        }if(etCustomerAddress.getText().toString().trim().length()==0){
            Toast.makeText(activity,"Please enter customer address",Toast.LENGTH_LONG).show();

            return false;
        }else {

            return true;
        }
    }
}
