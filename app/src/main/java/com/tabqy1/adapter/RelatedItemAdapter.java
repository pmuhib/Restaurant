package com.tabqy1.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tabqy1.R;
import com.tabqy1.apputils.AppSession;
import com.tabqy1.apputils.ItemClickListener;
import com.tabqy1.models.FoodType;
import com.tabqy1.webservices.RetrofitApiBuilder;

import java.util.ArrayList;

/**
 * Created by jayant on 16-10-2016.
 */

public class RelatedItemAdapter extends RecyclerView.Adapter<RelatedItemAdapter.MyViewHolder> {

    private ArrayList<FoodType> relatedItmes;
    private Context context;
    private ItemClickListener itemClickListener = null;
    AppSession appSession;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvItemPrice, tvItemName;
        public ImageView ivItem;


        public MyViewHolder(View view) {
            super(view);

            tvItemName = (TextView) view.findViewById(R.id.tv_item_name);
            tvItemPrice= (TextView) view.findViewById(R.id.tv_item_price);
            ivItem = (ImageView) view.findViewById(R.id.iv_item);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClicked(view, getAdapterPosition());
                    }
                }
            });

        }

    }
    public void setClickListener(ItemClickListener itemClickListener ) {
        this.itemClickListener = itemClickListener ;
    }


    public RelatedItemAdapter(Context context, ArrayList<FoodType> relatedItmes) {
        this.context = context;
        this.relatedItmes = relatedItmes;
        appSession = new AppSession(context);
    }

    @Override
    public RelatedItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_releated_items, parent, false);

        return new RelatedItemAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RelatedItemAdapter.MyViewHolder holder, int position) {
        Glide.with(context).load(RetrofitApiBuilder.BASE_URL_FOODIMAGE+relatedItmes.get(position).food_image).skipMemoryCache(true).into(holder.ivItem);
        holder.tvItemName.setText(relatedItmes.get(position).name);
        holder.tvItemPrice.setText(appSession.getCurrency()+""+relatedItmes.get(position).price);

    }

    @Override
    public int getItemCount() {
        return relatedItmes.size();
    }



}
