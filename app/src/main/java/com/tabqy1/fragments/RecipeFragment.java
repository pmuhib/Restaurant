package com.tabqy1.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
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
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.epson.epos2.printer.Printer;
import com.google.gson.JsonObject;
import com.tabqy1.R;
import com.tabqy1.activity.DashBoradActivity;
import com.tabqy1.apputils.AppSession;
import com.tabqy1.apputils.AppUtils;
import com.tabqy1.models.Coupon;
import com.tabqy1.models.FoodDetails;
import com.tabqy1.models.InvoiceData;
import com.tabqy1.models.Tax;
import com.tabqy1.printer.DiscoveryActivity;
import com.tabqy1.printer.SpnModelsItem;
import com.tabqy1.webservices.ApiResponse;
import com.tabqy1.webservices.RetroFitApis;
import com.tabqy1.webservices.RetrofitApiBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class RecipeFragment extends Fragment  implements View.OnClickListener{

    private static final String ARG_ORDER_ID = "orderID";
    private static final String ARG_TABLE = "tableNO";
    private static final String ARG_IS_HOME = "isHome";
    private static final String ARG_IS_COMPLETE = "isOrderCompleted";
    private AppCompatActivity activity;
    private String orderId;
    private List<FoodDetails> foodDetails;
    private List<Tax> taxes; // Atul
    private Coupon coupon ;
    private RecipeRecyclerViewAdapter recipeRecyclerViewAdapter;

    private TextView tvOrderNo,tvOrderDate,tvTableName,tv_table,tvWaiterName,tvSubTotal,tvTotal;
    private RecyclerView recyclerView;
    private GridLayout tax_container ;
    private AppSession appSession;
    private TextView tvDiscovery;
    private TextView tvPrint;
    private AppCompatSpinner spPrinterModel;
    private ArrayAdapter<SpnModelsItem> seriesAdapter;
    private InvoiceData invoiceData;
    private String tableNO;
    private boolean isHome,isOrderCompleted;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null) {
            orderId = getArguments().getString(ARG_ORDER_ID);
            tableNO = getArguments().getString(ARG_TABLE);
            isHome = getArguments().getBoolean(ARG_IS_HOME);
            isOrderCompleted = getArguments().getBoolean(ARG_IS_COMPLETE);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity=(AppCompatActivity)context;
    }

    public static RecipeFragment newInstance(String orderId,String tableNO,boolean isHome,boolean isOrderCompleted) {
        RecipeFragment fragment = new RecipeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ORDER_ID, orderId);
        args.putString(ARG_TABLE, tableNO);
        args.putBoolean(ARG_IS_HOME, isHome);
        args.putBoolean(ARG_IS_COMPLETE, isOrderCompleted);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        appSession= new AppSession(activity);
        getIds(view);
        callInvoiceApi(orderId);
        return view;
    }


    private void getIds(View view){
        TextView tvBack = (TextView) activity.findViewById(R.id.tv_back);
        tvBack.setVisibility(View.VISIBLE);
        TextView tvTitle=(TextView)activity.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.receipt);

        ImageButton btnOrder = (ImageButton) activity.findViewById(R.id.btnOrder) ;
        btnOrder.setVisibility(View.GONE);


        tvOrderNo= (TextView)view.findViewById(R.id.reciept_number) ;
        tvDiscovery= (TextView)view.findViewById(R.id.tv_discovery) ;
        tvPrint= (TextView)view.findViewById(R.id.tv_print) ;
        tvSubTotal= (TextView)view.findViewById(R.id.tv_subtotal_value);
        tvTotal= (TextView)view.findViewById(R.id.tv_total_value);
        spPrinterModel= (AppCompatSpinner)view.findViewById(R.id.spnModel);
        tvOrderDate= (TextView)view.findViewById(R.id.tv_date_value) ;
        tvTableName= (TextView)view.findViewById(R.id.tv_table_value) ;
        tv_table= (TextView)view.findViewById(R.id.tv_table) ;
        tvWaiterName= (TextView)view.findViewById(R.id.tv_served_by_value) ;
        tax_container= (GridLayout) view.findViewById(R.id.tax_container) ;
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_receipt);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        foodDetails= new ArrayList<>();
        taxes= new ArrayList<>(); // Atul
        recipeRecyclerViewAdapter = new RecipeRecyclerViewAdapter(activity,foodDetails,appSession);
        recyclerView.setAdapter(recipeRecyclerViewAdapter);
        tvPrint.setOnClickListener(this);
        tvDiscovery.setOnClickListener(this);
        setPrinterModel();

    }


    private  void setData(InvoiceData data)
    {
        tvOrderNo.setText(data.order_no);
        tvOrderDate.setText(data.order_date +" "+data.order_time);
        tvTableName.setText(data.table_name);
        tvWaiterName.setText(data.waiter_name);
        if(isHome){
            tv_table.setText(getResources().getString(R.string.home_delivery));
        }

    }

    private void callInvoiceApi(String orderNo){
        AppUtils.showProgress(getActivity(),getResources().getString(R.string.generate_invoice),false);
        RetroFitApis retroFitApis = RetrofitApiBuilder.getRetrofitRestaurant().create(RetroFitApis.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("order_no",orderNo);

        Log.e("request","request"+jsonObject.toString());
        Call<ApiResponse> apiResponseCall;
        if(isOrderCompleted) apiResponseCall = retroFitApis.getInvoice(jsonObject);
        else apiResponseCall = retroFitApis.getKitchenPrint(jsonObject);
        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Response<ApiResponse> response, Retrofit retrofit) {
                AppUtils.hideProgress();
                Log.e("Response",response.body().toString());
                if(!isAdded()){
                    return ;
                }
                if(response.body().status) {
                    invoiceData = response.body().invoice;
                    setData(invoiceData);
                    foodDetails.clear();
                    taxes.clear(); // Atul
                    if(response.body().food_details !=  null) {
                        foodDetails.addAll(response.body().food_details);
                    }

                    double sub_total = 0 ;
                    if(response.body().sub_total!=null){
                        sub_total = Double.parseDouble(response.body().sub_total) ;
                        Log.d("SUBTOTAL" ,response.body().sub_total);
                        setToGridView("Sub Total",appSession.getCurrency()+" "+String.format(Locale.US,"%.2f",sub_total)) ;
                        setToGridView("------------------------------------------------------------------","------------------");
                        tvSubTotal.setText(appSession.getCurrency()+" "+String.format(Locale.US,"%.2f",sub_total));
                    }

                    // Atul
                    if(response.body().tax_type !=  null && response.body().tax_type.size()>0) {
                        taxes.addAll(response.body().tax_type);
                        setTaxData(taxes);
                        setToGridView("------------------------------------------------------------------","------------------");
                    }

                    // Atul
                    if(response.body().tax_total !=  null) {
                        setTaxTotal(response.body().tax_total);
                    }

                    total_amount = sub_total+total_tax_amount ;
                    setToGridView("Total Amount",appSession.getCurrency()+" "+String.format(Locale.US,"%.2f", total_amount));
                    setToGridView("------------------------------------------------------------------","------------------");
                    if(response.body().coupon !=  null) {
                        coupon = response.body().coupon ;
                        discount_amount = total_amount * Double.parseDouble(coupon.coupon_discount)/100;
                        coupon.coupon_amount = discount_amount ;
                        setToGridView(String.format("%-60s","Discount @ "+coupon.coupon_discount+"%"),"-"+appSession.getCurrency()+" "+String.format(Locale.US,"%.2f", discount_amount));
                        //setCouponData(response.body().coupon);
                        setToGridView("------------------------------------------------------------------","------------------");
                    }

                    setToGridView("Total Payable Amount",appSession.getCurrency()+" "+String.format(Locale.US,"%.2f", (total_amount-discount_amount)));
                    tvTotal.setText(appSession.getCurrency()+" "+String.format(Locale.US,"%.2f", (total_amount-discount_amount)));
                    recipeRecyclerViewAdapter.notifyDataSetChanged();
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

    private void setToGridView(String s1 , String s2){

        TextView tv =  new TextView(activity);
        tv.setText(s1);
        if(Build.VERSION.SDK_INT <23) tv.setTextAppearance(activity, android.R.style.TextAppearance_Medium);
        else tv.setTextAppearance(android.R.style.TextAppearance_Medium);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextColor(Color.parseColor("#6b6a6a"));

        TextView tv1 =  new TextView(activity);
        tv1.setText(s2);
        if(Build.VERSION.SDK_INT <23) tv1.setTextAppearance(activity, android.R.style.TextAppearance_Medium);
        else tv1.setTextAppearance(android.R.style.TextAppearance_Medium);
        tv1.setTypeface(null, Typeface.BOLD);
        tv1.setTextColor(Color.parseColor("#6b6a6a"));
        tv1.setLayoutParams(new ViewGroup.LayoutParams(250, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv1.setPadding(0,5,0,5);
        tax_container.addView(tv);
        tax_container.addView(tv1);
    }

    private void setCouponData(Coupon coupon) {
        TextView tv =  new TextView(activity);
        tv.setText(String.format("%-60s","Discount ("+coupon.coupon_discount+"%)"));
        if(Build.VERSION.SDK_INT <23) tv.setTextAppearance(activity, android.R.style.TextAppearance_Medium);
        else tv.setTextAppearance(android.R.style.TextAppearance_Medium);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextColor(Color.parseColor("#6b6a6a"));
        discount_amount = total_amount * Double.parseDouble(coupon.coupon_discount)/100;

        TextView tv1 =  new TextView(activity);
        tv1.setText(appSession.getCurrency()+" "+String.format(Locale.US,"%.2f", discount_amount));
        if(Build.VERSION.SDK_INT <23) tv1.setTextAppearance(activity, android.R.style.TextAppearance_Medium);
        else tv1.setTextAppearance(android.R.style.TextAppearance_Medium);
        tv1.setTypeface(null, Typeface.BOLD);
        tv1.setLayoutParams(new ViewGroup.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv1.setTextColor(Color.parseColor("#6b6a6a"));
        tax_container.addView(tv);
        tax_container.addView(tv1);
    }

    // Atul
    private String tax_total ;
    double total_tax_amount = 0;
    double discount_amount = 0 ;
    double total_amount = 0 ;
    private void setTaxTotal(String tax_total) {
        this.tax_total =  tax_total ;
    }

    private void setTaxData(List<Tax> taxes) {
        for (int indexTax = 0; indexTax < taxes.size(); indexTax++) {
            Tax tax =  taxes.get(indexTax);
            String tax_name  = tax.tax_name ;
            String tax_value = tax.tax_value ;
            String tax_amount = String.format(Locale.US,"%.2f", tax.tax_amount) ;
            TextView tv =  new TextView(activity);
            tv.setText(String.format("%-60s",tax_name+" @ "+tax_value+"%"));
            if(Build.VERSION.SDK_INT <23) tv.setTextAppearance(activity, android.R.style.TextAppearance_Medium);
            else tv.setTextAppearance(android.R.style.TextAppearance_Medium);
            tv.setTypeface(null, Typeface.BOLD);
            tv.setTextColor(Color.parseColor("#6b6a6a"));
            total_tax_amount += tax.tax_amount;

            TextView tv1 =  new TextView(activity);
            tv1.setText(appSession.getCurrency()+" "+tax_amount);
            tv1.setLayoutParams(new ViewGroup.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT));
            if(Build.VERSION.SDK_INT <23) tv1.setTextAppearance(activity, android.R.style.TextAppearance_Medium);
            else tv1.setTextAppearance(android.R.style.TextAppearance_Medium);
            tv1.setTypeface(null, Typeface.BOLD);
            tv1.setTextColor(Color.parseColor("#6b6a6a"));
            tv1.setPadding(0,5,0,5);
            tax_container.addView(tv);
            tax_container.addView(tv1);
        }
        this.taxes =  taxes ;
    }


    @Override
    public void onClick(View v) {
        Intent intent = null;

        switch (v.getId()) {
            case R.id.tv_discovery:
                intent = new Intent(activity, DiscoveryActivity.class);
                startActivityForResult(intent, 0);
                break;

            case R.id.tv_print:
                Toast.makeText(activity.getApplicationContext(),getResources().getString(R.string.print_invoice),Toast.LENGTH_SHORT).show();
                updateState(false);
                if(invoiceData!=null && foodDetails.size()>0) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!((DashBoradActivity) activity).runPrintReceiptSequence(((SpnModelsItem) spPrinterModel.getSelectedItem()).getModelConstant(), Printer.MODEL_ANK, foodDetails, invoiceData,taxes,coupon,String.format(Locale.US,"%.2f", total_amount),tvSubTotal.getText().toString(),tvTotal.getText().toString(),isOrderCompleted,isHome)) {
                                updateState(true);
                                AppUtils.hideProgress();
                            }else{
                                if(isOrderCompleted) callFeedback();
                                else callHome();
                                AppUtils.hideProgress();
                            }
                        }
                    },100);

                }
                else{
                    Toast.makeText(activity,"There is no item to print the receipt",Toast.LENGTH_SHORT).show();
                    AppUtils.hideProgress();
                }
                break;

            default:
                // Do nothing
                break;
        }
    }

    private void callHome() {
        HomeFragment homeFragment = HomeFragment.newInstance("", "");
        AppUtils.setFragment(homeFragment, true, activity, R.id.fragmentContainer);
    }

    private void callFeedback() {
        if(isHome){
            FeedBackFragmentNew feedBackFragmentNew = FeedBackFragmentNew.newInstance();
            AppUtils.setFragment(feedBackFragmentNew, true, activity, R.id.fragmentContainer);
        }else {
            FeedBackFragmentNew feedBackFragmentNew = FeedBackFragmentNew.newInstance();
            Bundle bundle =  new Bundle() ;
            bundle.putString("order_number",orderId);
            bundle.putString("table_number",tableNO);
            feedBackFragmentNew.setArguments(bundle);
            AppUtils.setFragment(feedBackFragmentNew, true, activity, R.id.fragmentContainer);
        }
    }

    private void updateState(boolean state) {
        tvPrint.setEnabled(state);
        tvPrint.setFocusable(state);
    }


    private void setPrinterModel(){
        ArrayAdapter<SpnModelsItem> seriesAdapter = new ArrayAdapter<SpnModelsItem>(activity, android.R.layout.simple_spinner_item);
        seriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_m10), Printer.TM_M10));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_m30), Printer.TM_M30));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_p20), Printer.TM_P20));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_p60), Printer.TM_P60));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_p60ii), Printer.TM_P60II));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_p80), Printer.TM_P80));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t20), Printer.TM_T20));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t60), Printer.TM_T60));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t70), Printer.TM_T70));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t81), Printer.TM_T81));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t82), Printer.TM_T82));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t83), Printer.TM_T83));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t88), Printer.TM_T88));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t90), Printer.TM_T90));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t90kp), Printer.TM_T90KP));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_u220), Printer.TM_U220));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_u330), Printer.TM_U330));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_l90), Printer.TM_L90));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_h6000), Printer.TM_H6000));
        spPrinterModel.setAdapter(seriesAdapter);
        spPrinterModel.setSelection(0);

    }



}
