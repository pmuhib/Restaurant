package com.tabqy1.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;
import com.google.gson.JsonObject;
import com.tabqy1.R;
import com.tabqy1.apputils.AppSession;
import com.tabqy1.apputils.AppUtils;
import com.tabqy1.fragments.AboutFragment;
import com.tabqy1.fragments.FeedBackFragmentNew;
import com.tabqy1.fragments.HomeDeliveryFragment;
import com.tabqy1.fragments.HomeFragment;
import com.tabqy1.fragments.HotDealFragment;
import com.tabqy1.fragments.MenuFragment;
import com.tabqy1.fragments.MyProfileFragment;
import com.tabqy1.fragments.OrderFragment;
import com.tabqy1.fragments.OrderHistoryFragment;
import com.tabqy1.fragments.OrderProgressFragment;
import com.tabqy1.fragments.RecipeFragment;
import com.tabqy1.fragments.SearchFragment;
import com.tabqy1.fragments.SelectTableFragment;
import com.tabqy1.fragments.TodaySpecialFragment;
import com.tabqy1.models.AssociatedFood;
import com.tabqy1.models.Coupon;
import com.tabqy1.models.FinalAssociatedItem;
import com.tabqy1.models.FinalOrderList;
import com.tabqy1.models.FoodDetails;
import com.tabqy1.models.FoodType;
import com.tabqy1.models.InvoiceData;
import com.tabqy1.models.OrderAssociateFood;
import com.tabqy1.models.Tax;
import com.tabqy1.models.Waitertable;
import com.tabqy1.printer.ShowMsg;
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

public class DashBoradActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener,ReceiveListener {

    private FrameLayout fragmentContainer;
    private ImageButton btnOrder, btnSearch;
    private ArrayList<FoodType> orderArrayList = new ArrayList<>();
    private Waitertable tableNumber;
    private String orderNumber = "";
    private int orderType = 0;
    private double orderPrice = 0;
    private AppSession appSession;
    private List<Tax> taxList = new ArrayList<>();
    private List<FinalOrderList> finalOrderLists = new ArrayList<>();
    private String orderValue = "0";
    private String orderValueNoTax = "0";
    private String customerStatus = "0";
    private FragmentManager fragmentManager;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private Printer  mPrinter = null;
    private String defaultTarget= "TCP:192.168.192.168";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_borad);

        appSession = new AppSession(DashBoradActivity.this);
        if (appSession.getPreviousOrder() != null) {
            orderArrayList.addAll(appSession.getPreviousOrder());
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView tvAppTitle = (TextView) findViewById(R.id.tv_app_title);
        TextView tvBack = (TextView) findViewById(R.id.tv_back);
        tvBack.setOnClickListener(this);
        ImageView ivAppTitle = (ImageView) findViewById(R.id.iv_app_title);
        if (appSession.getRestaurantColor() != null && appSession.getRestaurantColor().length() > 0) {
            toolbar.setBackgroundColor(Color.parseColor(appSession.getRestaurantColor()));
        }

        if(!appSession.getRestaurantLogo().isEmpty())
            Glide.with(DashBoradActivity.this).load(RetrofitApiBuilder.BASE_URL_RESTAURANT_LOGO + appSession.getRestaurantLogo()).skipMemoryCache(true).into(ivAppTitle);
        else ivAppTitle.setImageResource(R.mipmap.app_icon);

        tvAppTitle.setText(appSession.getRestaurantName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setTitle("");

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        initViews();
        setTableNumber(setDefaultTableNumber());

    }

    private void initViews() {
        fragmentContainer = (FrameLayout) findViewById(R.id.fragmentContainer);
        HomeFragment homeFragment = HomeFragment.newInstance("", "");
        AppUtils.setFragment(homeFragment, true, DashBoradActivity.this, R.id.fragmentContainer);
        btnOrder = (ImageButton) findViewById(R.id.btnOrder);
        btnSearch = (ImageButton) findViewById(R.id.btnSearch);
        btnOrder.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppUtils.isNetworkAvailable(DashBoradActivity.this)) {
            callGetTaxes();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0 && isFragmentExist() instanceof  HomeFragment) {
                exitDialog();
            } else {
                if(isFragmentExist() instanceof HomeDeliveryFragment) {
                    Toast.makeText(DashBoradActivity.this,R.string.complete_address,Toast.LENGTH_SHORT).show();
                    return;
                }
                if ((isFragmentExist() instanceof FeedBackFragmentNew) || (isFragmentExist() instanceof RecipeFragment)  ) {
                    HomeFragment homeFragment = HomeFragment.newInstance("", "");
                    AppUtils.setFragment(homeFragment, true, DashBoradActivity.this, R.id.fragmentContainer);
                } else {
                    super.onBackPressed();
                }

            }
        }
    }

    private void exitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private Fragment isFragmentExist() {
        fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragmentContainer);
        return currentFragment;

    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.table) {
            if (!(isFragmentExist() instanceof SelectTableFragment)) {
                SelectTableFragment selectTableFragment = SelectTableFragment.newInstance("", "");
                AppUtils.setFragment(selectTableFragment, false, DashBoradActivity.this, R.id.fragmentContainer);
            }

        } else if (id == R.id.dashboard) {
            if (!(isFragmentExist() instanceof HomeFragment)) {
                HomeFragment homeFragment = HomeFragment.newInstance("", "");
                AppUtils.setFragment(homeFragment, true, DashBoradActivity.this, R.id.fragmentContainer);
            }

        } else if (id == R.id.today_s_special) {
            if (!(isFragmentExist() instanceof TodaySpecialFragment)) {
                TodaySpecialFragment todaySpecialFragment = TodaySpecialFragment.newInstance("", "");
                AppUtils.setFragment(todaySpecialFragment, false, DashBoradActivity.this, R.id.fragmentContainer);
            }

        } else if (id == R.id.hot_deal) {
            if (!(isFragmentExist() instanceof HotDealFragment)) {
                HotDealFragment hotDealFragment = HotDealFragment.newInstance("", "");
                AppUtils.setFragment(hotDealFragment, false, DashBoradActivity.this, R.id.fragmentContainer);
            }

        } else if (id == R.id.order_in_progress) {
            if (!(isFragmentExist() instanceof OrderProgressFragment)) {
                OrderProgressFragment orderProgressFragment = OrderProgressFragment.newInstance("", "");
                AppUtils.setFragment(orderProgressFragment, false, DashBoradActivity.this, R.id.fragmentContainer);
            }

        } else if (id == R.id.order_history) {
            if (!(isFragmentExist() instanceof OrderHistoryFragment)) {
                OrderHistoryFragment orderHistoryFragment = OrderHistoryFragment.newInstance("", "");
                AppUtils.setFragment(orderHistoryFragment, false, DashBoradActivity.this, R.id.fragmentContainer);
            }
        } else if (id == R.id.feedback) {
            if (!(isFragmentExist() instanceof FeedBackFragmentNew)) {
                FeedBackFragmentNew feedBackFragment = FeedBackFragmentNew.newInstance();
                AppUtils.setFragment(feedBackFragment, false, DashBoradActivity.this, R.id.fragmentContainer);
            }

        } else if (id == R.id.about_restaurant) {
            if (!(isFragmentExist() instanceof AboutFragment)) {
                AboutFragment aboutFragment = AboutFragment.newInstance("", "");
                AppUtils.setFragment(aboutFragment, false, DashBoradActivity.this, R.id.fragmentContainer);
            }

        } else if (id == R.id.log_out) {

            showLogoutDialog();
        } else if (id == R.id.profile) {
            if (!(isFragmentExist() instanceof MyProfileFragment)) {
                MyProfileFragment profileFragment = MyProfileFragment.newInstance("", "");
                AppUtils.setFragment(profileFragment, false, DashBoradActivity.this, R.id.fragmentContainer);
            }
        } else if (id == R.id.menu) {
            if (!(isFragmentExist() instanceof MenuFragment)) {
                MenuFragment menuFragment = MenuFragment.newInstance("", "");
                AppUtils.setFragment(menuFragment, false, DashBoradActivity.this, R.id.fragmentContainer);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /* this method is used for showing the logout*/
    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.logout_text)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new AppSession(DashBoradActivity.this).resetSession();
                        finish();
                        if (orderArrayList != null) {
                            orderArrayList.clear();
                        }
                        dialog.dismiss();
                        appSession.resetSession();
                        Intent intent  = new Intent(DashBoradActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnOrder:
                if (!(isFragmentExist() instanceof OrderFragment)) {
                    if(isFragmentExist() instanceof HomeDeliveryFragment) {
                        Toast.makeText(DashBoradActivity.this,R.string.complete_address,Toast.LENGTH_SHORT).show();
                        return;
                    }
                    boolean isClearStack =  false ;
                    if(isFragmentExist() instanceof RecipeFragment) {
                        isClearStack = true  ;
                    }
                    OrderFragment orderFragment = OrderFragment.newInstance(orderNumber, orderType, getTableNumber().table_id, orderValue, orderValueNoTax, customerStatus);
                    AppUtils.setFragment(orderFragment, isClearStack, DashBoradActivity.this, R.id.fragmentContainer);

                }
                break;
            case R.id.btnSearch:
                if (!(isFragmentExist() instanceof SearchFragment)) {
                    if(isFragmentExist() instanceof HomeDeliveryFragment) {
                        Toast.makeText(DashBoradActivity.this, R.string.complete_address,Toast.LENGTH_SHORT).show();
                        return;
                    }
                    SearchFragment searchFragment = SearchFragment.newInstance("", "");
                    AppUtils.setFragment(searchFragment, false, DashBoradActivity.this, R.id.fragmentContainer);
                }
                break;
            case R.id.tv_back:
                if(isFragmentExist() instanceof HomeDeliveryFragment) {
                    Toast.makeText(DashBoradActivity.this,R.string.complete_address,Toast.LENGTH_SHORT).show();
                    return;
                }
                onBackPressed();

                break;


        }
    }

    public void addFoodToOrder(FoodType foodType) {

        FoodType foodType1 = new FoodType();
        foodType1.positionID = foodType.positionID;
        foodType1.food_id = foodType.food_id;
        foodType1.food_image = foodType.food_image;
        foodType1.name = foodType.name;
        foodType1.price = foodType.price;
        foodType1.description = foodType.description;
        foodType1.discount_price = foodType.discount_price;
        foodType1.dish_name = foodType.dish_name;
        foodType1.categories = foodType.categories;
        foodType1.cusinie_id = foodType.cusinie_id;
        foodType1.qty = foodType.qty;
        foodType1.food_code = foodType.food_code;
        foodType1.isSelected = foodType.isSelected;
        foodType1.itemQty = 1;
        foodType1.isWithAssociated = foodType.isWithAssociated;
        orderArrayList.add(foodType1);

        updatePrice();
    }


    public void removeFoodToOrder(FoodType foodType) {
        FoodType foodTypeToRemove = null;
        for (int i = 0; i < orderArrayList.size(); i++) {
            if (foodType.positionID.equals(orderArrayList.get(i).positionID)) {
                foodTypeToRemove = orderArrayList.get(i);
                break;
            }
        }
        if (foodTypeToRemove != null) {
            orderArrayList.remove(foodTypeToRemove);
        }
        updatePrice();
    }

    public void removeFoodToOrderUncheck(FoodType foodType){
        ArrayList<FoodType> foodsTypeToRemove = new ArrayList<>();
        for (int i = 0; i < orderArrayList.size(); i++) {
            if(foodType.food_id.equals(orderArrayList.get(i).food_id))
            {
                foodsTypeToRemove.add(orderArrayList.get(i));

            }
        }
        for (FoodType foodTypeToRemove: foodsTypeToRemove) {
            orderArrayList.remove(foodTypeToRemove);
        }

        updatePrice();
    }


    public void removeFoodFromOrderList(FoodType foodType) {
        ArrayList<FoodType> foodTypestoRemove = new ArrayList<>();
        for (int i = 0; i < orderArrayList.size(); i++) {

            if (!foodType.isWithAssociated) {

                if (foodType.food_id.equals(orderArrayList.get(i).food_id) && !orderArrayList.get(i).isWithAssociated) {
                    foodTypestoRemove.add(orderArrayList.get(i));


                }
            } else {
                if (foodType.positionID.equals(orderArrayList.get(i).positionID)) {
                    foodTypestoRemove.add(orderArrayList.get(i));
                    break;
                }
            }
        }

        for (int i = 0; i < foodTypestoRemove.size(); i++) {
            orderArrayList.remove(foodTypestoRemove.get(i));
        }

        updatePrice();
    }

    public void setCountOfFood(FoodType foodType) {
        for (int i = 0; i < orderArrayList.size(); i++) {
            if (foodType.food_id.equalsIgnoreCase(orderArrayList.get(i).food_id)) {
                orderArrayList.get(i).qty = foodType.qty;
            }
        }
        updatePrice();
    }

    public void setCountOfAssociateFoodAdd(FoodType foodType, List<AssociatedFood> associatedFoods) {
        for (int i = 0; i < orderArrayList.size(); i++) {

            if (foodType.positionID.equalsIgnoreCase(orderArrayList.get(i).positionID)) {
                orderArrayList.get(i).selectedAssociatedItems.put(0, associatedFoods);

                orderArrayList.get(i).isWithAssociated = associatedFoods.size() > 0 ? true : false;

            }
        }
        updatePrice();
    }

    int selection = 0;

    public ArrayList<FoodType> getDisplayOrderList() {
        ArrayList<FoodType> orderListForDisplay = new ArrayList<>();
        for (int i = 0; i < orderArrayList.size(); i++) {
            selection = 0;
            if (isItemExist(orderArrayList.get(i), orderListForDisplay)) {
                orderListForDisplay.get(selection).itemQty++;
            } else {
                orderArrayList.get(i).itemQty = 1;
                orderListForDisplay.add(orderArrayList.get(i));
            }

        }

        return orderListForDisplay;
    }


    public boolean isItemExist(FoodType foodType, ArrayList<FoodType> orderListForDisplay) {
        boolean status = false;
        for (int i = 0; i < orderListForDisplay.size(); i++) {
            if (orderListForDisplay.get(i).food_id.equals(foodType.food_id)) {
                if (!foodType.isWithAssociated && !orderListForDisplay.get(i).isWithAssociated) {
                    selection = i;
                    status = true;
                }
            }
        }
        return status;
    }

    public void clearOrder() {
        orderArrayList.clear();
        setTableNumber(setDefaultTableNumber());
        orderNumber = "";
        orderType = 0;
        orderPrice = 0;
        clearPreviousAmount();
    }

    private void clearPreviousAmount(){
        orderValue = "0.0";
        orderValueNoTax = "0.0";
    }


    public Waitertable setDefaultTableNumber() {
        Waitertable waitertable = new Waitertable();
        waitertable.table_name = "";
        waitertable.table_id = "";
        return waitertable;
    }

    public ArrayList<FoodType> getOrderArraylist() {
        return this.orderArrayList;
    }

    public List<FoodType> getExistingFoodType(List<FoodType> arrayListFood) {
        for (FoodType food : orderArrayList) {
            for (int i = 0; i < arrayListFood.size(); i++) {
                if (food.food_id.equalsIgnoreCase(arrayListFood.get(i).food_id) && food.food_code.equalsIgnoreCase(arrayListFood.get(i).food_code)) {
                    arrayListFood.get(i).qty = food.qty;
                    arrayListFood.get(i).isSelected = food.isSelected;

                }
            }
        }
        return arrayListFood;
    }

    public Waitertable getTableNumber() {
        return this.tableNumber;
    }

    public void setTableNumber(Waitertable tableNumber) {
        this.tableNumber = tableNumber;
    }


    public void setOrderNumberAndType(String orderNumber, int orderType, String orderValue,String orderValueNoTax, String customerStatus) {
        this.orderNumber = orderNumber;
        this.orderType = orderType;
        this.orderValue = orderValue;
        this.orderValueNoTax = orderValueNoTax;
        this.customerStatus = customerStatus;
    }


    public double updatePrice() {
        double price = 0.00;
        finalOrderLists.clear();
        for (FoodType foodType : getDisplayOrderList()) {
            price = price + Double.parseDouble(foodType.price) * foodType.itemQty;
            FinalOrderList finalOrderList = new FinalOrderList();
            finalOrderList.food_id = foodType.food_id;
            finalOrderList.food_code = foodType.food_code;
            finalOrderList.qty = foodType.itemQty;
            finalOrderList.price = foodType.price;
            finalOrderList.name = foodType.name;

            finalOrderList.items = new ArrayList<>();

            if (foodType.selectedAssociatedItems.containsKey(0)) {
                List<AssociatedFood> associatedFoods = foodType.selectedAssociatedItems.get(0);
                for (int i = 0; i < associatedFoods.size(); i++) {
                    if (associatedFoods.get(i).isSelected) {
                        associatedFoods.get(i).asscioted_price = associatedFoods.get(i).associated_food_price;
                        associatedFoods.get(i).asscioted_food_code = associatedFoods.get(i).associated_food_food_code;
                        price = price + Double.parseDouble(associatedFoods.get(i).associated_food_price);
                    }

                }

                for (AssociatedFood associatedFood : associatedFoods) {

                    FinalAssociatedItem finalAssociatedItem = new FinalAssociatedItem();
                    finalAssociatedItem.asscioted_food_id = associatedFood.associated_food_id;
                    finalAssociatedItem.asscioted_food_code = associatedFood.associated_food_food_code;
                    finalAssociatedItem.asscioted_food_name = associatedFood.associated_food_name;
                    finalAssociatedItem.asscioted_price = associatedFood.associated_food_price;
                    finalAssociatedItem.asscioted_qty = 1;
                    finalOrderList.items.add(finalAssociatedItem);
                }
            }
            finalOrderLists.add(finalOrderList);

        }
        orderPrice = price;
        appSession.savePreviousOrder(orderArrayList);
        return price;
    }

    public List<FinalOrderList> getFinalOrderList() {
        return finalOrderLists;
    }

    public double getPrice() { return orderPrice; }

    public List<Tax> getTaxList() {
        return taxList;
    }

    public double getTaxCalculatedPrice() {
        double totalPrice = 0;
        for (int i = 0; i < taxList.size(); i++) {
            totalPrice = totalPrice + ((Double.parseDouble(taxList.get(i).tax_value) * getPrice()) / 100);
        }
        totalPrice = totalPrice + getPrice();
        return (double) Math.round(totalPrice * 100d) / 100d;
    }

    public double getTaxPrice(double taxPrice) {
        return (double) Math.round(((taxPrice * getPrice()) / 100) * 100d) / 100d;
    }

    private void callGetTaxes() {
        RetroFitApis retroFitApis = RetrofitApiBuilder.getRetrofitRestaurant().create(RetroFitApis.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("resturant_id", appSession.getRestaurantId());
        Call<ApiResponse> apiResponseCall = retroFitApis.getTax(jsonObject);
        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Response<ApiResponse> response, Retrofit retrofit) {
                Log.e("Response", response.body().toString());
                if (response.body().status) {
                    taxList.clear();
                    taxList.addAll(response.body().tax_type);
                } else {
                    // Toast.makeText(DashBoradActivity.this, response.body().msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(DashBoradActivity.this, R.string.server_error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        appSession.savePreviousOrder(orderArrayList);
    }

    public void setDrawerEnabled(boolean enabled) {
        int lockMode = enabled ? DrawerLayout.LOCK_MODE_UNLOCKED :DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
        drawer.setDrawerLockMode(lockMode);

    }





    public boolean runPrintReceiptSequence(int modelConstant, int languageModelConstant, List<FoodDetails> foodDetails, InvoiceData data, List<Tax> taxes, Coupon coupon, String total_w_tax , String subTotal,String total,boolean isOrderCompleted,boolean isHome) {

        if (!initializeObject(modelConstant,languageModelConstant)) {
            return false;
        }

        if (!createReceiptData(foodDetails,data,taxes,coupon,total_w_tax,subTotal,total,isOrderCompleted,isHome)) {
            finalizeObject();
            return false;
        }

        if (!printData()) {
            finalizeObject();
            return false;
        }

        return true;
    }



    @Override
    public void onPtrReceive(final Printer printerObj, final int code, final PrinterStatusInfo status, final String printJobId) {
        runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                ShowMsg.showResult(code, AppUtils.makeErrorMessage(status,DashBoradActivity.this), DashBoradActivity.this);
                AppUtils.dispPrinterWarnings(status,DashBoradActivity.this);

                // updateButtonState(true);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        disconnectPrinter();
                    }
                }).start();
            }
        });
    }
    private boolean initializeObject(int modelConstant,int languageModelConstant) {
        try {
            mPrinter = new Printer(modelConstant,languageModelConstant,DashBoradActivity.this);
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        mPrinter.setReceiveEventListener(this);

        return true;
    }

    private String getFormat(String data, int size){

        if(data.length()>size){
            return data.substring(0,size);
        }else{
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(data);
            for (int i = 0; i < (size-data.length())  ; i++) {
                stringBuilder.append(" ");
            }
            return stringBuilder.toString();
        }

    }


    private boolean createReceiptData(List<FoodDetails> foodDetails, InvoiceData invoiceData,List<Tax> taxes,Coupon coupon,String total_w_tax ,String subTotal,String total,boolean  isOrderCompleted,boolean isHome) {
        String method = "";
        Bitmap logoData = BitmapFactory.decodeResource(getResources(), R.drawable.login_logo);
        StringBuilder textData = new StringBuilder();

        if (mPrinter == null) {
            return false;
        }

        try {
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);

            method = "addImage";
            mPrinter.addImage(logoData, 0, 0,
                    logoData.getWidth(),
                    logoData.getHeight(),
                    Printer.COLOR_1,
                    Printer.MODE_MONO,
                    Printer.HALFTONE_DITHER,
                    Printer.PARAM_DEFAULT,
                    Printer.COMPRESS_AUTO);

            method = "addFeedLine";
            mPrinter.addFeedLine(1);
            //------------------------------------------------
            mPrinter.addTextSize(1, 1);
            String where =  isOrderCompleted?"Cashier":"Kitchen" ;
            textData.append(appSession.getRestaurantName()+" - "+where+"\n\n");
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            mPrinter.addTextSize(1, 1);
            textData.append(String.format(Locale.US,"%-11s - %-20s","Served By",invoiceData.waiter_name)+"\n");
            if(!isHome) {
                textData.append(String.format(Locale.US, "%-11s - %-20s", "Table No.", invoiceData.table_name) + "\n");
            }else{
                textData.append(String.format(Locale.US, "%-34s", "Home Delivery") + "\n");
            }
            textData.append(String.format(Locale.US,"%-11s - %-20s","Receipt No.",invoiceData.order_no)+"\n");
            textData.append(String.format(Locale.US,"%-11s - %-20s","Date",invoiceData.order_date +" "+invoiceData.order_time)+"\n\n");
            textData.append("---------------------------------------------\n");
            textData.append(getFormat("Item#",15)+" "+getFormat("   Price#",11)+" "+getFormat("Qty#",4)+" "+ getFormat(" Price("+appSession.getCurrency()+")",12)+ "\n");
            textData.append("---------------------------------------------\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            for (FoodDetails  details: foodDetails) {
                textData.append(getFormat(details.name,15)+" "+getFormat(String.format(Locale.US,"%10.2f",Double.parseDouble(details.unit_price)),11)+" "  +getFormat(details.qty,4)+" "+getFormat(String.format(Locale.US,"%11.2f",Double.parseDouble(details.price)),12)+"\n");
                if(details.associated_food!=null) {
                    for (OrderAssociateFood orderAssociateFood : details.associated_food) {
                        textData.append(getFormat(orderAssociateFood.name,15)+" "+getFormat(String.format(Locale.US,"%10.2f",Double.parseDouble(orderAssociateFood.unit_price)),11)+" "+getFormat(orderAssociateFood.qty,4)+" "+getFormat(String.format(Locale.US,"%11.2f",orderAssociateFood.price)+"",12)+"\n");

                    }
                }
            }

            textData.append("---------------------------------------------\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0,textData.length());

            textData.append(getFormat("SUB TOTAL",28)+" "+getFormat(subTotal,16)+"\n");
            textData.append("---------------------------------------------\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            // Atul
            if(taxes.size()>0) {
                for (Tax tax : taxes) {
                    textData.append(getFormat(tax.tax_name + " @ " + tax.tax_value + " %", 28) +" "+ getFormat(appSession.getCurrency(),3)+" " + getFormat(String.format(Locale.US,"%11.2f",tax.tax_amount), 12) + "\n");
                }
                textData.append("---------------------------------------------\n");
            }
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0,textData.length());

            textData.append(getFormat("TOTAL AMOUNT",28)+" "+getFormat(appSession.getCurrency(),3)+" "+getFormat(String.format(Locale.US,"%11.2f",Double.parseDouble(total_w_tax)),12)+"\n") ;
            textData.append("---------------------------------------------\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            coupon = new Coupon();
            if(coupon!=null){
                textData.append(getFormat("DISCOUNT @ "+0+"%",28)+" "+getFormat(appSession.getCurrency(),3)+" "+getFormat(String.format(Locale.US,"%11.2f",coupon.coupon_amount),12)+"\n");
                textData.append("---------------------------------------------\n");
                method = "addText";
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
            }
            mPrinter.addTextSize(2, 2);
            textData.append("GRAND TOTAL AMOUNT\n");
            textData.append(total+"\n");
            method = "addText";
            mPrinter.addText(textData.toString());

            textData.delete(0, textData.length());

            mPrinter.addTextSize(1, 1);
            textData.append("---------------------------------------------\n\n\n");
            textData.append("Thank you for dinner with us !\n");
            textData.append("Please come again !\n");
            method = "addText";
            mPrinter.addText(textData.toString());

            textData.delete(0, textData.length());
            // Atul

            method = "addFeedLine";
            mPrinter.addFeedLine(2);
            method = "addCut";
            mPrinter.addCut(Printer.CUT_FEED);



        }
        catch (Exception e) {
            ShowMsg.showException(e, method, DashBoradActivity.this);
            return false;
        }

        textData = null;

        return true;
    }




    private void finalizeObject() {
        if (mPrinter == null) {
            return;
        }

        mPrinter.clearCommandBuffer();

        mPrinter.setReceiveEventListener(null);

        mPrinter = null;
    }

    private boolean connectPrinter() {
        boolean isBeginTransaction = false;

        if (mPrinter == null) {
            return false;
        }

        try {

            mPrinter.connect(defaultTarget, Printer.PARAM_DEFAULT);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "connect", DashBoradActivity.this);
            return false;
        }

        try {
            mPrinter.beginTransaction();
            isBeginTransaction = true;
        }
        catch (Exception e) {
            ShowMsg.showException(e, "beginTransaction", DashBoradActivity.this);
        }

        if (isBeginTransaction == false) {
            try {
                mPrinter.disconnect();
            }
            catch (Epos2Exception e) {
                // Do nothing
                return false;
            }
        }

        return true;
    }

    private boolean printData() {
        if (mPrinter == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        PrinterStatusInfo status = mPrinter.getStatus();

        AppUtils.dispPrinterWarnings(status,DashBoradActivity.this);

        if (!isPrintable(status)) {
            ShowMsg.showMsg(AppUtils.makeErrorMessage(status,DashBoradActivity.this), DashBoradActivity.this);
            try {
                mPrinter.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        try {
            mPrinter.sendData(Printer.PARAM_DEFAULT);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "sendData", DashBoradActivity.this);
            try {
                mPrinter.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        return true;
    }



    private boolean isPrintable(PrinterStatusInfo status) {
        if (status == null) {
            return false;
        }

        if (status.getConnection() == Printer.FALSE) {
            return false;
        }
        else if (status.getOnline() == Printer.FALSE) {
            return false;
        }
        else {
            ;//print available
        }

        return true;
    }
    private void disconnectPrinter() {
        if (mPrinter == null) {
            return;
        }

        try {
            mPrinter.endTransaction();
        }
        catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    ShowMsg.showException(e, "endTransaction", DashBoradActivity.this);
                }
            });
        }

        try {
            mPrinter.disconnect();
        }
        catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    ShowMsg.showException(e, "disconnect", DashBoradActivity.this);
                }
            });
        }

        finalizeObject();
    }


    @Override
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        if (data != null && resultCode == RESULT_OK) {
            String target = data.getStringExtra(getString(R.string.title_target));
            if (target != null) {
                defaultTarget=target;
            }
        }
    }


}
