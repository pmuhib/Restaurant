package com.tabqy1.apputils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.tabqy1.R;
import com.tabqy1.models.AssociatedFood;
import com.tabqy1.models.OrderAssociateFood;
import com.tabqy1.webservices.RetrofitApiBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by RamSingh on 10/9/2016.
 */

public class AppUtils {

    public static final boolean DEBUG=true;

    public  static  void printLog(String msg){

        if(DEBUG){
            Log.e("TAG",msg);
        }

    }

    private static ProgressDialog progressDialog ;
    public static void showProgress(Activity activity,String msg, boolean cancelable){

        if(progressDialog==null) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage(msg);
            progressDialog.show();
            progressDialog.setCancelable(cancelable);
        }else{
            progressDialog.setMessage(msg);
        }
    }

    public static void hideProgress(){
        if(progressDialog!=null){
            progressDialog.hide();
            progressDialog= null ;
        }
    }




    public static void setAppBackGroundImage(Activity activity ,final ViewGroup view  , String name){
        Glide.with(activity).load(RetrofitApiBuilder.BASE_URL_BACKGROUND_IMAGE+name).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                Drawable drawable;
                drawable = new BitmapDrawable(resource);
                drawable.setAlpha(50);
                view.setBackground(drawable);
                }
        });
    }

    /**
     * method for setting fragment
     * @param fragment
     * @param removeStack
     * @param activity
     * @param mContainer
     */
    public static void setFragment(Fragment fragment, boolean removeStack, FragmentActivity activity, int mContainer) {

        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction ftTransaction = fragmentManager.beginTransaction();
        if (removeStack) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ftTransaction.replace(mContainer, fragment);
        } else {
            ftTransaction.replace(mContainer, fragment);
            ftTransaction.addToBackStack("hello");
        }
        ftTransaction.commit();
    }


    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager)activity. getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    public static String getCurrentDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }



     public static boolean isAvailable(String status){
         if(status!=null && status.equals("1")){
             return true;
         }else {
             return false;
         }
     }



    public static String getConcateAssociate(List<AssociatedFood> associatedFoods){
        StringBuilder stringBuilder= new StringBuilder();
        for (int i = 0; i < associatedFoods.size(); i++) {
              if(i!=0){
                  stringBuilder.append(", ");
              }

              stringBuilder.append(associatedFoods.get(i).associated_food_name);
        }
        return stringBuilder.toString();
    }

    public static String getPreConcateAssociate(List<OrderAssociateFood> associatedFoods){
        StringBuilder stringBuilder= new StringBuilder();
        for (int i = 0; i < associatedFoods.size(); i++) {
            if(i!=0){
                stringBuilder.append(", ");
            }

            stringBuilder.append(associatedFoods.get(i).name);
        }
        return stringBuilder.toString();
    }

    public static float getTotalPrice(List<AssociatedFood> associatedFoods,String foodPrice){

        float totalPrice=0;

        try {
            totalPrice=totalPrice+Float.parseFloat(foodPrice);
        } catch (NumberFormatException e) {
            totalPrice=totalPrice+0;
            e.printStackTrace();
        }

        for (int i = 0; i < associatedFoods.size(); i++) {
            try {
                totalPrice=totalPrice+Float.parseFloat(associatedFoods.get(i).associated_food_price);
            } catch (NumberFormatException e) {
                totalPrice=totalPrice+0;
                e.printStackTrace();
            }
        }
        return totalPrice;
    }


    public static float getPreTotalPrice(List<OrderAssociateFood> associatedFoods,String foodPrice){

        float totalPrice=0;

        try {
            totalPrice=totalPrice+Float.parseFloat(foodPrice);
        } catch (NumberFormatException e) {
            totalPrice=totalPrice+0;
            e.printStackTrace();
        }

        for (int i = 0; i < associatedFoods.size(); i++) {
            try {
                totalPrice=totalPrice+(associatedFoods.get(i).price);
            } catch (NumberFormatException e) {
                totalPrice=totalPrice+0;
                e.printStackTrace();
            }
        }
        return totalPrice;
    }


    public static   String makeErrorMessage(PrinterStatusInfo status,Context context) {
        String msg = "";

        if (status.getOnline() == Printer.FALSE) {
            msg += context.getString(R.string.handlingmsg_err_offline);
        }
        if (status.getConnection() == Printer.FALSE) {
            msg += context.getString(R.string.handlingmsg_err_no_response);
        }
        if (status.getCoverOpen() == Printer.TRUE) {
            msg += context.getString(R.string.handlingmsg_err_cover_open);
        }
        if (status.getPaper() == Printer.PAPER_EMPTY) {
            msg += context.getString(R.string.handlingmsg_err_receipt_end);
        }
        if (status.getPaperFeed() == Printer.TRUE || status.getPanelSwitch() == Printer.SWITCH_ON) {
            msg += context.getString(R.string.handlingmsg_err_paper_feed);
        }
        if (status.getErrorStatus() == Printer.MECHANICAL_ERR || status.getErrorStatus() == Printer.AUTOCUTTER_ERR) {
            msg += context.getString(R.string.handlingmsg_err_autocutter);
            msg += context.getString(R.string.handlingmsg_err_need_recover);
        }
        if (status.getErrorStatus() == Printer.UNRECOVER_ERR) {
            msg += context.getString(R.string.handlingmsg_err_unrecover);
        }
        if (status.getErrorStatus() == Printer.AUTORECOVER_ERR) {
            if (status.getAutoRecoverError() == Printer.HEAD_OVERHEAT) {
                msg += context.getString(R.string.handlingmsg_err_overheat);
                msg += context.getString(R.string.handlingmsg_err_head);
            }
            if (status.getAutoRecoverError() == Printer.MOTOR_OVERHEAT) {
                msg += context.getString(R.string.handlingmsg_err_overheat);
                msg += context.getString(R.string.handlingmsg_err_motor);
            }
            if (status.getAutoRecoverError() == Printer.BATTERY_OVERHEAT) {
                msg += context.getString(R.string.handlingmsg_err_overheat);
                msg += context.getString(R.string.handlingmsg_err_battery);
            }
            if (status.getAutoRecoverError() == Printer.WRONG_PAPER) {
                msg += context.getString(R.string.handlingmsg_err_wrong_paper);
            }
        }
        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_0) {
            msg += context.getString(R.string.handlingmsg_err_battery_real_end);
        }

        return msg;
    }

    public static  void dispPrinterWarnings(PrinterStatusInfo status, Context context) {
        String warningsMsg = "";

        if (status == null) {
            return;
        }

        if (status.getPaper() == Printer.PAPER_NEAR_END) {
            warningsMsg += context.getString(R.string.handlingmsg_warn_receipt_near_end);
        }

        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_1) {
            warningsMsg += context.getString(R.string.handlingmsg_warn_battery_near_end);
        }

        Toast.makeText(context,warningsMsg, Toast.LENGTH_LONG).show();
    }
}
