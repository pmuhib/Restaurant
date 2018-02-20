package com.tabqy1.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tabqy1.R;
import com.tabqy1.apputils.AppSession;
import com.tabqy1.apputils.AppUtils;
import com.tabqy1.models.FoodDetails;
import com.tabqy1.webservices.RetrofitApiBuilder;

import java.util.ArrayList;

/**
 * Created by jayant on 03-02-2017.
 */

public class PreviousOrderRecyclerAdapter extends RecyclerView.Adapter<PreviousOrderRecyclerAdapter.MyViewHolder> {

    public ArrayList<FoodDetails> orderList;
    public Context context;
    private AppSession appSession;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivItem,ivCross;
        private TextView tvItemPrice, tvItemDesc,tvItemName, tvCount,tvAssociate,tvAssociateTxt;
        private ImageButton btnAdd, btnRemove;

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

            ivCross.setVisibility(View.INVISIBLE);
            btnAdd.setVisibility(View.INVISIBLE);
            btnRemove.setVisibility(View.INVISIBLE);

        }

    }


    public PreviousOrderRecyclerAdapter(Context context, ArrayList<FoodDetails> orderList) {
        this.context = context;
        this.orderList = orderList;
        appSession = new AppSession(context);
    }

    @Override
    public PreviousOrderRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_order_list, parent, false);

        return new PreviousOrderRecyclerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PreviousOrderRecyclerAdapter.MyViewHolder holder, int position) {
        String imageUrl= RetrofitApiBuilder.BASE_URL_PREVIOUS_ORDER_IMAGE + orderList.get(position).food_image;
        Glide.with(context).load(imageUrl).into(holder.ivItem);
        holder.tvItemName.setText(orderList.get(position).name);
        holder.tvItemPrice.setText(appSession.getCurrency()+""+AppUtils.getPreTotalPrice(orderList.get(position).associated_food,orderList.get(position).price));
        holder.tvItemDesc.setText(orderList.get(position).description);
        holder.tvCount.setText(String.valueOf(orderList.get(position).qty));
        if(AppUtils.getPreConcateAssociate(orderList.get(position).associated_food).trim().length()>0) {
            holder.tvAssociate.setText(AppUtils.getPreConcateAssociate(orderList.get(position).associated_food));
            holder.tvAssociateTxt.setVisibility(View.VISIBLE);
        }else {
            holder.tvAssociateTxt.setVisibility(View.INVISIBLE);
            holder.tvAssociate.setText("");
        }

    }

    @Override
    public int getItemCount() {
        return this.orderList.size();
    }





}
