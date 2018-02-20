package com.tabqy1.adapter;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tabqy1.R;
import com.tabqy1.models.OrderHistory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jayant on 13-10-2016.
 */

public class OrderInProgressRecyclerAdapter extends RecyclerView.Adapter<OrderInProgressRecyclerAdapter.MyViewHolder> {

    public Context context;
    private ArrayList<OrderHistory> orderHistories;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvOrderNo, tvOrderTime, tvWaiterName,tvOrderDate;
        public AppCompatCheckBox cbDelivery;

        public MyViewHolder(View view) {
            super(view);
            tvOrderNo = (TextView)view.findViewById(R.id.tvOrderNo);
            tvOrderTime = (TextView)view.findViewById(R.id.tvOrderTime);
            tvOrderDate = (TextView)view.findViewById(R.id.tvOrderDate);
            tvWaiterName = (TextView)view.findViewById(R.id.tvWaiterName);
            cbDelivery = (AppCompatCheckBox) view.findViewById(R.id.checkboxDelivery);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog(orderHistories.get(getAdapterPosition()).detailsList);
                }
            });
        }
    }


    public OrderInProgressRecyclerAdapter(Context context, ArrayList<OrderHistory> orderHistories) {
        this.context = context;
        this.orderHistories=orderHistories;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_order_in_progress, parent, false);




        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        OrderHistory orderHistoryModel = orderHistories.get(position);
        holder.tvOrderNo.setText(orderHistoryModel.order_no);
        holder.tvOrderTime.setText(orderHistoryModel.order_time);
        holder.tvOrderDate.setText(orderHistoryModel.order_date);
        holder.tvWaiterName.setText(orderHistoryModel.waiter_name);

        if(orderHistoryModel.type.equals("1")) {
            holder.cbDelivery.setChecked(true);
        }else {
            holder.cbDelivery.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return orderHistories.size();
    }


    public void showDialog(List<String> detailsList){
        final CharSequence[] items = detailsList.toArray(new String[detailsList.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Order items");
        builder.setItems(items,null);
        AlertDialog alert = builder.create();
        alert.show();
    }
    }


