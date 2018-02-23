package com.tabqy1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.tabqy1.R;
import com.tabqy1.apputils.AppSession;

public class SplashActivity extends AppCompatActivity {

    private Handler handler= new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler.postDelayed(runnable,2000);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if(new AppSession(SplashActivity.this).isLogin()){
                Intent intent = new Intent(SplashActivity.this, DashBoradActivity.class);
                startActivity(intent);
                finish();
            }else {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

        }
    };


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(runnable);

    }

}
