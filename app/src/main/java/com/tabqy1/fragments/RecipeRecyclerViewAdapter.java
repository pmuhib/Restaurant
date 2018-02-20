package com.tabqy1.fragments;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tabqy1.R;
import com.tabqy1.adapter.AssociateItemRecipt;
import com.tabqy1.apputils.AppSession;
import com.tabqy1.models.FoodDetails;

import java.util.List;


public class RecipeRecyclerViewAdapter extends RecyclerView.Adapter<RecipeRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<FoodDetails> foodDetails;
    private AppSession appSession;


    public RecipeRecyclerViewAdapter(Context context, List<FoodDetails> foodDetails, AppSession appSession) {
        this.context = context;
        this.foodDetails=foodDetails;
        this.appSession=appSession;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_receipt, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        FoodDetails foodDetail= foodDetails.get(position);
        holder.tvFoodName.setText(foodDetail.name);
        holder.tvUnitPrice.setText(appSession.getCurrency()+" "+foodDetail.unit_price);
        holder.tvTotalPrice.setText(appSession.getCurrency()+" "+foodDetail.price);
        holder.tvQty.setText(foodDetail.qty);
        holder.tvEa.setText("");
        if(foodDetail.associated_food!=null) {
            holder.recyclerView.setVisibility(View.VISIBLE);
            AssociateItemRecipt associateItemRecipt = new AssociateItemRecipt(context, foodDetail.associated_food);
            holder.recyclerView.setAdapter(associateItemRecipt);
        }else {
            holder.recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return foodDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvFoodName,tvUnitPrice,tvTotalPrice,tvQty,tvEa;
        private RecyclerView recyclerView;

        public ViewHolder(View view) {
            super(view);
            tvFoodName = (TextView) view.findViewById(R.id.tv_item);
            tvUnitPrice = (TextView) view.findViewById(R.id.tv_price);
            tvTotalPrice = (TextView) view.findViewById(R.id.tv_totaprice);
            tvQty = (TextView) view.findViewById(R.id.tv_qty);
            tvEa = (TextView) view.findViewById(R.id.tv_qa);
            recyclerView = (RecyclerView) view.findViewById(R.id.rv_associated);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
    }
}
