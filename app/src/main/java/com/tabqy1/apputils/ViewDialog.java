package com.tabqy1.apputils;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.tabqy1.R;
import com.tabqy1.activity.DashBoradActivity;
import com.tabqy1.adapter.AssociateItemAdapter;
import com.tabqy1.listeners.OrderModifiedListener;
import com.tabqy1.models.AssociatedFood;
import com.tabqy1.models.FoodType;

import java.util.ArrayList;
import java.util.List;

/** this class is used for display associate item*/
public class ViewDialog {
    private OrderModifiedListener orderModifiedListener;

    public ViewDialog() {

    }
    public ViewDialog(OrderModifiedListener orderModifiedListener) {
        this.orderModifiedListener = orderModifiedListener;
    }

    public  void showAssociateDialog(final Context activity, String msg, final FoodType foodType){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_associated);
        TextView text = (TextView) dialog.findViewById(R.id.tv_title);
        RecyclerView rvRecycler = (RecyclerView) dialog.findViewById(R.id.rv_associated);
        List<AssociatedFood> associatedFoods = new ArrayList<>();
        associatedFoods.addAll(foodType.associated_food);
        final AssociateItemAdapter associateItemAdapter= new AssociateItemAdapter(activity,associatedFoods);
        rvRecycler.setLayoutManager(new LinearLayoutManager(activity));
        rvRecycler.setAdapter(associateItemAdapter);
        text.setText(msg);

        Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((DashBoradActivity)activity).setCountOfAssociateFoodAdd(foodType, associateItemAdapter.getRelatedItmes());
                  if(orderModifiedListener!=null) {
                    orderModifiedListener.onItemModified();
                 }

                dialog.dismiss();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        dialog.show();

    }
}