package com.tabqy1.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tabqy1.R;
import com.tabqy1.apputils.AppSession;
import com.tabqy1.apputils.AppUtils;
import com.tabqy1.models.UserDetails;
import com.tabqy1.webservices.ApiResponse;
import com.tabqy1.webservices.RetroFitApis;
import com.tabqy1.webservices.RetrofitApiBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tvForgotPassword;
    private EditText etPassword,etUserName;
    private Button btnSignIn;
    private AppSession appSession;
    //private String ip ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        appSession = new AppSession(LoginActivity.this);
        initViews();
    }

    private void checkSubscription() {
        final String subscriptionKey  = appSession.getSubscriptionKey();
        final long timestamp  = appSession.getTimestamp();
        final long curr_timestamp =  Calendar.getInstance().getTimeInMillis() ;
        if(subscriptionKey== null && curr_timestamp>timestamp){
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("Subscription");
            LinearLayout layout = new LinearLayout(LoginActivity.this);
            layout.setOrientation(LinearLayout.VERTICAL);
            final EditText etSubscriptionKey =  new EditText(LoginActivity.this);
            etSubscriptionKey.setHint("Enter Subscription Key");
            etSubscriptionKey.setWidth(400);
            layout.addView(etSubscriptionKey);
            builder.setView(layout);
            builder.setPositiveButton("Subscribe", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String subscription_key  =  etSubscriptionKey.getText().toString().trim() ;
                    if(subscription_key.length()>0){
                        testingValidateSubscription(subscription_key) ;
                    }else{
                        Toast.makeText(LoginActivity.this, "Please enter Subscription Key.", Toast.LENGTH_SHORT).show();
                        checkSubscription();
                    }
                }
            });
            builder.create() ;
            builder.show() ;

        }
    }

    private void testingValidateSubscription(final String subscription_key){
        String subscription_end_date  =   "2017-06-16";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        Date dateSub = null;
        try {
            dateSub = format.parse(subscription_end_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        appSession.setTimestamp(dateSub!=null?dateSub.getTime():0);
        appSession.setSubscriptionKey(subscription_key);
    }


    private void validateSubscription(final String subscription_key) {
        AppUtils.showProgress(LoginActivity.this,getResources().getString(R.string.validate_subscription),false);
        final String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID);
        RetroFitApis retroFitApis = RetrofitApiBuilder.getRetrofitServer().create(RetroFitApis.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("subscription_key",subscription_key);
        jsonObject.addProperty("device_id",android_id);
        Call<ApiResponse> apiResponseCall = retroFitApis.validateSubscriptionKey(jsonObject);

        Log.e("request","request"+jsonObject.toString());

        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Response<ApiResponse> response, Retrofit retrofit) {
                AppUtils.hideProgress();
                Log.e("Response", new Gson().toJson(response.body()));

                if(response.body().status) {
                    String subscription_end_date  =   response.body().subscription_end_date;
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
                    Date dateSub = null;
                    try {
                        dateSub = format.parse(subscription_end_date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    appSession.setTimestamp(dateSub!=null?dateSub.getTime():0);
                    appSession.setSubscriptionKey(subscription_key);
                    checkSubscription();
                }else{
                    Toast.makeText(LoginActivity.this, response.body().msg, Toast.LENGTH_SHORT).show();
                    checkSubscription();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                AppUtils.hideProgress();
                Toast.makeText(LoginActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                checkSubscription();
            }
        });
    }


    private  void initViews(){
        etPassword=(EditText)findViewById(R.id.etPassword);
        etUserName=(EditText)findViewById(R.id.etUserName);
        tvForgotPassword=(TextView)findViewById(R.id.tvForgotPassword);
        btnSignIn=(Button)findViewById(R.id.btnLogin);
        btnSignIn.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);
        etUserName.requestFocus();

    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.btnLogin :
                if(AppUtils.isNetworkAvailable(LoginActivity.this)) {
                    callLoginApi();
                }else{
                    Toast.makeText(LoginActivity.this, R.string.msg_no_connection, Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.tvForgotPassword :

                intent = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(intent);
                break;
        }
    }




    private void callLoginApi(){
        AppUtils.showProgress(LoginActivity.this,getResources().getString(R.string.login_progress),false);
        final String username = etUserName.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        RetroFitApis retroFitApis = RetrofitApiBuilder.getRetrofitRestaurant().create(RetroFitApis.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username",username);
        jsonObject.addProperty("password",password);
        Call<ApiResponse> apiResponseCall = retroFitApis.doLogin(jsonObject);

        Log.e("request","request"+jsonObject.toString());

        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Response<ApiResponse> response, Retrofit retrofit) {
                AppUtils.hideProgress();
                Log.e("Response", new Gson().toJson(response.body()));

                if(response.body().status) {
                    UserDetails userDetails = response.body().user_detail;
                    appSession.setRestaurantID(userDetails,username,password);
                    Log.e("Login Activity", "resturant id: "+appSession.getRestaurantId());
                    Intent intent = new Intent(LoginActivity.this, DashBoradActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this, response.body().msg, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                AppUtils.hideProgress();
                Toast.makeText(LoginActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
            }
        });


    }

}
