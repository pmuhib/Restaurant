package com.tabqy1.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.tabqy1.R;
import com.tabqy1.apputils.AppUtils;
import com.tabqy1.webservices.ApiResponse;
import com.tabqy1.webservices.RetroFitApis;
import com.tabqy1.webservices.RetrofitApiBuilder;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ForgotPasswordActivity extends AppCompatActivity {

   private EditText etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initView();
    }


    private void  initView(){
       etEmail=(EditText)findViewById(R.id.etUserName);
       Button btnSubmit=(Button)findViewById(R.id.btnLogin);
       btnSubmit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

              if(etEmail.getText().toString().trim().length()!=0){
                  if(AppUtils.isNetworkAvailable(ForgotPasswordActivity.this)){
                      callForgetPasswordApi();
                  }else {
                      Toast.makeText(ForgotPasswordActivity.this,R.string.msg_no_connection,Toast.LENGTH_SHORT).show();
                  }
              }else {
                  Snackbar.make(etEmail,"Please enter email address",Snackbar.LENGTH_SHORT).show();
              }

           }
       });

    }

    private void callForgetPasswordApi(){
        AppUtils.showProgress(ForgotPasswordActivity.this,getResources().getString(R.string.reset_password),false);
        String email = etEmail.getText().toString().trim();
        RetroFitApis retroFitApis = RetrofitApiBuilder.getRetrofitRestaurant().create(RetroFitApis.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email",email);
        Call<ApiResponse> apiResponseCall = retroFitApis.forgetpassword(jsonObject);
        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Response<ApiResponse> response, Retrofit retrofit) {
                AppUtils.hideProgress();
                Log.e("Response",response.body().toString());
                if(response.body().status) {
                    Toast.makeText(ForgotPasswordActivity.this, response.body().msg, Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(ForgotPasswordActivity.this, response.body().msg, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                AppUtils.hideProgress();
                Toast.makeText(ForgotPasswordActivity.this, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });


    }
}
