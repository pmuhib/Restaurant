package com.tabqy1.printer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.discovery.DeviceInfo;
import com.epson.epos2.discovery.Discovery;
import com.epson.epos2.discovery.DiscoveryListener;
import com.epson.epos2.discovery.FilterOption;
import com.tabqy1.R;
import com.tabqy1.apputils.AppSession;
import com.tabqy1.webservices.RetrofitApiBuilder;

import java.util.ArrayList;
import java.util.HashMap;

public class DiscoveryActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private Context mContext = null;
    private ArrayList<HashMap<String, String>> mPrinterList = null;
    private SimpleAdapter mPrinterListAdapter = null;
    private FilterOption mFilterOption = null;
    private AppSession appSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);
        appSession= new AppSession(DiscoveryActivity.this);
        mContext = this;
        ImageButton btnOrder = (ImageButton) findViewById(R.id.btnOrder);
        ImageButton btnSearch = (ImageButton) findViewById(R.id.btnSearch);
        btnOrder.setVisibility(View.INVISIBLE);
        btnSearch.setVisibility(View.INVISIBLE);
        Button button = (Button)findViewById(R.id.btnRestart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView tvTitle=(TextView)findViewById(R.id.tv_title);
        tvTitle.setText("Discover Printer");
        TextView tvAppTitle = (TextView) findViewById(R.id.tv_app_title);
        TextView tvBack = (TextView) findViewById(R.id.tv_back);
        tvBack.setOnClickListener(this);
        ImageView ivAppTitle = (ImageView) findViewById(R.id.iv_app_title);
        if (appSession.getRestaurantColor() != null && appSession.getRestaurantColor().length() > 0) {
            toolbar.setBackgroundColor(Color.parseColor(appSession.getRestaurantColor()));
        }
        Glide.with(DiscoveryActivity.this).load(RetrofitApiBuilder.BASE_URL_RESTAURANT_LOGO + appSession.getRestaurantLogo()).skipMemoryCache(true).into(ivAppTitle);

        tvAppTitle.setText(appSession.getRestaurantName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle("");
        button.setOnClickListener(this);

        mPrinterList = new ArrayList<HashMap<String, String>>();
        mPrinterListAdapter = new SimpleAdapter(this, mPrinterList, R.layout.list_at,
                                                new String[] { "PrinterName", "Target" },
                                                new int[] { R.id.PrinterName, R.id.Target });
        ListView list = (ListView)findViewById(R.id.lstReceiveData);
        list.setAdapter(mPrinterListAdapter);
        list.setOnItemClickListener(this);

        mFilterOption = new FilterOption();
        mFilterOption.setDeviceType(Discovery.TYPE_PRINTER);
        mFilterOption.setEpsonFilter(Discovery.FILTER_NAME);
        try {
            Discovery.start(this, mFilterOption, mDiscoveryListener);
        }
        catch (Exception e) {
           e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        while (true) {
            try {
                Discovery.stop();
                break;
            }
            catch (Epos2Exception e) {
                if (e.getErrorStatus() != Epos2Exception.ERR_PROCESSING) {
                    break;
                }
            }
        }

        mFilterOption = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRestart:
                restartDiscovery();
                break;
            case R.id.tv_back:
                 finish();
                break;

            default:
                // Do nothing
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        HashMap<String, String> item  = mPrinterList.get(position);
        intent.putExtra(getString(R.string.title_target), item.get("Target"));
        setResult(RESULT_OK, intent);
        finish();
    }

    private void restartDiscovery() {
        while (true) {
            try {
                Discovery.stop();
                break;
            }
            catch (Epos2Exception e) {
                if (e.getErrorStatus() != Epos2Exception.ERR_PROCESSING) {
                   e.printStackTrace();
                    return;
                }
            }
        }

        mPrinterList.clear();
        mPrinterListAdapter.notifyDataSetChanged();

        try {
            Discovery.start(this, mFilterOption, mDiscoveryListener);
        }
        catch (Exception e) {
           e.printStackTrace();
        }
    }

    private DiscoveryListener mDiscoveryListener = new DiscoveryListener() {
        @Override
        public void onDiscovery(final DeviceInfo deviceInfo) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    HashMap<String, String> item = new HashMap<String, String>();
                    item.put("PrinterName", deviceInfo.getDeviceName());
                    item.put("Target", deviceInfo.getTarget());
                    mPrinterList.add(item);
                    mPrinterListAdapter.notifyDataSetChanged();
                }
            });
        }
    };
}
