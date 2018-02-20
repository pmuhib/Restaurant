package com.tabqy1.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tabqy1.R;
import com.tabqy1.activity.DashBoradActivity;
import com.tabqy1.apputils.AppSession;
import com.tabqy1.apputils.AppUtils;
import com.tabqy1.apputils.ViewDialog;
import com.tabqy1.listeners.OrderModifiedListener;
import com.tabqy1.models.FoodType;
import com.tabqy1.webservices.RetrofitApiBuilder;

import java.util.ArrayList;

/**
 * Created by jayant on 16-10-2016.
 */

public class OrderListRecyclerAdapter extends RecyclerView.Adapter<OrderListRecyclerAdapter.MyViewHolder> {

    public ArrayList<FoodType> orderList;
    public Context context;
    private OrderModifiedListener orderModifiedListener;
    private AppSession appSession;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivItem,ivCross;
        private TextView tvItemPrice, tvItemDesc,tvItemName, tvCount;
        private ImageButton btnAdd, btnRemove;
        public TextView tvAssociate,tvAssociateTxt;

        public MyViewHolder(View view) {
            super(view);

            tvItemName = (TextView) view.findViewById(R.id.tvItemName);
            tvAssociate = (TextView) view.findViewById(R.id.tvAssociate);
            tvAssociateTxt = (TextView) view.findViewById(R.id.tvAssociateTxt);
            ivItem = (ImageView) view.findViewById(R.id.iv_item);
            ivCross = (ImageView) view.findViewById(R.id.ivCross);
            tvItemPrice= (TextView) view.findViewById(R.id.tvItemPrice);
            tvItemDesc = (TextView) view.findViewById(R.id.tvItemDesc);
            tvCount =  (TextView) view.findViewById(R.id.tvCount);

            btnAdd = (ImageButton) view.findViewById(R.id.ivAdd);
            btnRemove = (ImageButton) view.findViewById(R.id.ivRemove);
            btnAdd.setVisibility(View.INVISIBLE);
            btnRemove.setVisibility(View.INVISIBLE);

            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.e("Qty","qty"+orderList.get(getAdapterPosition()).qty);
                    if(orderList.get(getAdapterPosition()).qty >0) {
                        orderList.get(getAdapterPosition()).qty = orderList.get(getAdapterPosition()).qty-1;
                        tvCount.setText(String.valueOf(orderList.get(getAdapterPosition()).qty));
                        ((DashBoradActivity)context).setCountOfFood(orderList.get(getAdapterPosition()));
                        if( orderList.get(getAdapterPosition()).qty==0 && orderList.get(getAdapterPosition()).isSelected){
                            ((DashBoradActivity)context).removeFoodToOrder(orderList.get(getAdapterPosition()));
                            orderList.get(getAdapterPosition()).isSelected = false;

                        }
                        orderModifiedListener.onItemModified();
                        notifyDataSetChanged();

                    }
                }
            });

            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(orderList.get(getAdapterPosition()).associated_food!= null && orderList.get(getAdapterPosition()).associated_food.size()>0) {
                        new ViewDialog(orderModifiedListener).showAssociateDialog(context, "Associate Items", orderList.get(getAdapterPosition()));
                    }
                    orderList.get(getAdapterPosition()).qty = orderList.get(getAdapterPosition()).qty+1;
                    Log.e("Qty","qty"+orderList.get(getAdapterPosition()).qty);
                    tvCount.setText(String.valueOf(orderList.get(getAdapterPosition()).qty));
                    ((DashBoradActivity)context).setCountOfFood(orderList.get(getAdapterPosition()));
                    if( orderList.get(getAdapterPosition()).qty==1 && !orderList.get(getAdapterPosition()).isSelected) {
                        orderList.get(getAdapterPosition()).isSelected=true;
                        ((DashBoradActivity) context).addFoodToOrder(orderList.get(getAdapterPosition()));
                    }
                    orderModifiedListener.onItemModified();
                    notifyDataSetChanged();

                }
            });


            ivCross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ((DashBoradActivity)context).removeFoodFromOrderList(orderList.get(getAdapterPosition()));
                     orderList.remove(getAdapterPosition());
                     orderModifiedListener.onItemModified();
                     notifyDataSetChanged();

                }
            });

        }

    }


    public OrderListRecyclerAdapter(Context context, ArrayList<FoodType> orderList,OrderModifiedListener orderModifiedListener) {
        this.context = context;
         this.orderList = orderList;
         this.orderModifiedListener = orderModifiedListener;
        appSession = new AppSession(context);
    }

    @Override
    public OrderListRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_order_list, parent, false);

        return new OrderListRecyclerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OrderListRecyclerAdapter.MyViewHolder holder, int position) {
        String imageUrl= RetrofitApiBuilder.BASE_URL_FOODIMAGE + orderList.get(position).food_image;
        Glide.with(context).load(imageUrl).into(holder.ivItem);
        holder.tvItemName.setText(orderList.get(position).name);

        holder.tvItemDesc.setText(orderList.get(position).description);
        holder.tvCount.setText(""+orderList.get(position).itemQty);
        if(orderList.get(position).selectedAssociatedItems.containsKey(0)) {
            holder.tvAssociate.setText(AppUtils.getConcateAssociate(orderList.get(position).selectedAssociatedItems.get(0)));
            holder.tvAssociateTxt.setVisibility(View.VISIBLE);
        }else {
            holder.tvAssociate.setText("");
            holder.tvAssociateTxt.setVisibility(View.INVISIBLE);
        }

        if(orderList.get(position).selectedAssociatedItems.containsKey(0)) {
            holder.tvItemPrice.setText(appSession.getCurrency()+""+AppUtils.getTotalPrice(orderList.get(position).selectedAssociatedItems.get(0),orderList.get(position).price));
        }else {
            holder.tvItemPrice.setText(appSession.getCurrency()+getTotalPrice(orderList.get(position).price,orderList.get(position).itemQty));
        }

    }

    @Override
    public int getItemCount() {
        return this.orderList.size();
    }




    public String getTotalPrice(String price, int qty){

        float fPrice=0;
        try {
             fPrice =Float.parseFloat(price);
             return String.format("%.2f", fPrice*qty);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return "";
    }


}
