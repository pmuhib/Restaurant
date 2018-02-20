package com.tabqy1.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tabqy1.R;
import com.tabqy1.Validation;
import com.tabqy1.adapter.FeedbackAdapter;
import com.tabqy1.apputils.AppSession;
import com.tabqy1.apputils.AppUtils;
import com.tabqy1.models.OrderHistory;
import com.tabqy1.models.QuestionList;
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
 * Use the {@link FeedBackFragmentNew#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedBackFragmentNew extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View view;
    private Button btnSubmit;
    private AppCompatActivity activity;
    private AppSession appSession;
    private List<QuestionList> questionList;
    private FeedbackAdapter feedbackAdapter;
    private ArrayList<Waitertable> waitertables;
    private AppCompatSpinner spTable,spOrder;
    private ArrayList<OrderHistory> orderCompleted = new ArrayList<OrderHistory>();
    private EditText etName,etEmail,etPhone;
    private String order_number,table_number;

    public FeedBackFragmentNew() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment FeedBackFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedBackFragmentNew newInstance() {
        FeedBackFragmentNew fragment = new FeedBackFragmentNew();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity=(AppCompatActivity)context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_feed_back_new, container, false);
        appSession= new AppSession(activity);
        initView();

        Bundle bundle  = getArguments() ;
        if(bundle!=null) {
            order_number =   bundle.getString("order_number");
            table_number =   bundle.getString("table_number");
            spOrder.setEnabled(false);
            spTable.setEnabled(false);
        }

        callTableDetailsApi();
        callGetOrderHistory();
        callGetQustionApi();

        AppUtils.setAppBackGroundImage(getActivity(),(ViewGroup) view,appSession.getRestaurantBackgroundImage());

        return view;
    }

    private  void initView (){
        TextView tvBack = (TextView) activity.findViewById(R.id.tv_back);
        tvBack.setVisibility(View.VISIBLE);

        ImageButton btnOrder = (ImageButton) activity.findViewById(R.id.btnOrder) ;
        btnOrder.setVisibility(View.GONE);



        etEmail=(EditText)view.findViewById(R.id.et_email) ;
        etName=(EditText)view.findViewById(R.id.et_name) ;
        etPhone=(EditText)view.findViewById(R.id.et_phone) ;
        TextView tvTitle=(TextView)activity.findViewById(R.id.tv_title);
        waitertables= new ArrayList<>();
        spTable=(AppCompatSpinner) view.findViewById(R.id.sp_table);
        spOrder = (AppCompatSpinner) view.findViewById(R.id.sp_order);
        tvTitle.setText(R.string.feedback);
        btnSubmit=(Button)view.findViewById(R.id.btn_submit);
        RecyclerView rvFeedback= (RecyclerView)view.findViewById(R.id.rv_feedback);
        rvFeedback.setLayoutManager(new LinearLayoutManager(activity));
        questionList= new ArrayList<>();
        feedbackAdapter= new FeedbackAdapter(activity,questionList);
        rvFeedback.setAdapter(feedbackAdapter);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_submit :
                if(isValid()) {
                    sendFeedbackApi();
                }
                break;
        }
    }

    private boolean isValid(){

        String orderId ;
        if(orderCompleted.size()<=0){
            Toast.makeText(activity,"Please select order no",Toast.LENGTH_SHORT).show();
            return false ;
        }

        if(waitertables.size()<=0){
            Toast.makeText(activity,"Please select table no",Toast.LENGTH_SHORT).show();
            return false ;
        }
        orderId =  orderCompleted.get(spOrder.getSelectedItemPosition()).order_no;
        String tableId = waitertables.get(spTable.getSelectedItemPosition()).table_id;

        if(orderId.equalsIgnoreCase("no order")){
            Toast.makeText(activity,"Please select order no",Toast.LENGTH_SHORT).show();
            return false;
        }else  if(tableId.equalsIgnoreCase("no table")){
            Toast.makeText(activity,"Please select table no",Toast.LENGTH_SHORT).show();
            return false;
        }else  if(etName.getText().toString().trim().length()==0){
            Toast.makeText(activity,"Please enter name",Toast.LENGTH_SHORT).show();
            return false;
        }else  if(etPhone.getText().toString().trim().length()==0){
            Toast.makeText(activity,"Please enter phone number",Toast.LENGTH_SHORT).show();
            return false;
        }
       else if (!etEmail.getText().toString().trim().isEmpty() && !Validation.checkValidEmail(etEmail.getText().toString().trim())) {
            Toast.makeText(activity,"Please enter valid email",Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }
    }

    /** this method is used for getting completed order history*/
    private void callGetOrderHistory( ){

        RetroFitApis retroFitApis = RetrofitApiBuilder.getRetrofitRestaurant().create(RetroFitApis.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("resturant_id", appSession.getRestaurantId());
        jsonObject.addProperty("user_id", appSession.getUserId());
        jsonObject.addProperty("from_date", AppUtils.getCurrentDate());
        jsonObject.addProperty("to_date", AppUtils.getCurrentDate());


        Call<ApiResponse> apiResponseCall = retroFitApis.getOrderHistory(jsonObject);
        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Response<ApiResponse> response, Retrofit retrofit) {
                if(response.body().status) {
                    if(response.body().order_history!=null){
                        orderCompleted.clear();

                        if(table_number!=null) {
                            for (int i = 0; i < response.body().order_history.size(); i++) {
//                            if (response.body().order_history.get(i).order_status.equals("1")){
//                                orderCompleted.add(response.body().order_history.get(i));
//                            }
                                if (response.body().order_history.get(i).table_id.toLowerCase().trim().equals(table_number.trim().toLowerCase())) {
                                    orderCompleted.add(response.body().order_history.get(i));
                                }
                            }
                        }else{
                            orderCompleted.addAll(response.body().order_history);
                        }


                        ArrayAdapter<OrderHistory> adapter =
                                new ArrayAdapter<OrderHistory>(activity.getApplicationContext(),R.layout.spinner_item, orderCompleted);
                        adapter.setDropDownViewResource(R.layout.spinner_item);
                        spOrder.setAdapter(adapter);

                        int selectedOrderIndex = 0 ;
                        if(order_number!=null) {
                            for (int i = 0; i < orderCompleted.size(); i++) {
                                if (orderCompleted.get(i).order_no.trim().toLowerCase().equals(order_number.toLowerCase().trim())) {
                                    selectedOrderIndex = i;
                                    break;
                                }
                            }
                        }
                        spOrder.setSelection(selectedOrderIndex);
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

    /** this method is used for getting assigned waiter table*/
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
                    waitertables.addAll( response.body().waitertable);
                    ArrayAdapter<Waitertable> adapter =
                            new ArrayAdapter<Waitertable>(activity.getApplicationContext(),R.layout.spinner_item, waitertables);
                    adapter.setDropDownViewResource(R.layout.spinner_item);
                    spTable.setAdapter(adapter);
                    int selectedTableIndex = 0 ;
                    if(table_number!=null) {
                        for (int i = 0; i < waitertables.size(); i++) {
                            if (waitertables.get(i).table_id.trim().toLowerCase().equals(table_number.toLowerCase().trim())) {
                                selectedTableIndex = i;
                                break;
                            }
                        }
                    }
                    spTable.setSelection(selectedTableIndex);

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

    /** this method is used for getting feedback question list*/


    private void callGetQustionApi() {
        AppUtils.showProgress(getActivity(),getResources().getString(R.string.loading_feedback_questions),false);
        RetroFitApis retroFitApis = RetrofitApiBuilder.getRetrofitRestaurant().create(RetroFitApis.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("resturant_id", appSession.getRestaurantId());
        Call<ApiResponse> apiResponseCall = retroFitApis.getQestionList(jsonObject);
        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Response<ApiResponse> response, Retrofit retrofit) {
                AppUtils.hideProgress();
                Log.e("Response", response.body().toString());
                if (response.body().status) {
                    questionList.clear();
                    questionList.addAll(response.body().question);
                    feedbackAdapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(activity, response.body().msg, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                AppUtils.hideProgress();
                t.printStackTrace();
                Gson gson = new Gson();
                ApiResponse response= gson.fromJson(respone,ApiResponse.class);
                if (response.status) {
                    questionList.clear();
                    questionList.addAll(response.question);
                    feedbackAdapter.notifyDataSetChanged();
                }

                Toast.makeText(activity, R.string.server_error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    /** this method is used for send feedback question list*/
    private void sendFeedbackApi() {
        AppUtils.showProgress(getActivity(),getResources().getString(R.string.sending_feedback_response),false);
        String orderId = orderCompleted.get(spOrder.getSelectedItemPosition()).order_no;
        String tableId = waitertables.get(spTable.getSelectedItemPosition()).table_id;

        RetroFitApis retroFitApis = RetrofitApiBuilder.getRetrofitRestaurant().create(RetroFitApis.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("resturant_id", appSession.getRestaurantId());
        jsonObject.addProperty("user_id", appSession.getUserId());
        jsonObject.addProperty("order_no",orderId );
        jsonObject.addProperty("table_id", tableId);
        jsonObject.addProperty("name",etName.getText().toString().trim());
        jsonObject.addProperty("email", etEmail.getText().toString().trim());
        jsonObject.addProperty("phone", etPhone.getText().toString().trim());
        jsonObject.add("feedback_response", new Gson().toJsonTree(feedbackAdapter.getFeedbacks(orderId,appSession.getRestaurantId())));

        Log.e("request","request"+jsonObject.toString());
        Call<ApiResponse> apiResponseCall = retroFitApis.sendFeedback(jsonObject);
        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Response<ApiResponse> response, Retrofit retrofit) {
                AppUtils.hideProgress();
                Log.e("Response", response.body().toString());
                if (response.body().status) {

                    ThankYouFragment fragment = ThankYouFragment.newInstance("", "");
                    AppUtils.setFragment(fragment, true, activity, R.id.fragmentContainer);

                } else {
                    Toast.makeText(activity, response.body().msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                AppUtils.hideProgress();
                t.printStackTrace();
                Gson gson = new Gson();
                ApiResponse response= gson.fromJson(respone,ApiResponse.class);
                if (response.status) {
                    questionList.clear();
                    questionList.addAll(response.question);
                    feedbackAdapter.notifyDataSetChanged();
                }
                Toast.makeText(activity, R.string.server_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public final String respone= "{\"status\": true,\"question\": [{\"question_id\": \"1\",\"question\": {\"name\": \"Overall Experiance\",\"choices\": \"Poor,Fair,Average,Good,Very Good\"},\"question_type\": \"1\"}, {\t\"question_id\": \"2\",\"question\": {\"name\": \"Service\"},\"question_type\": \"2\"}, {\"question_id\": \"3\",\"question\": {\"name\": \"Food\"},\"question_type\": \"3\"}, {\"question_id\": \"4\",\"question\": {\"name\": \"Ambience\",\"choices\": \"Poor,Fair,Average,Good,Very Good\"},\"question_type\": \"1\"}]}";
}
