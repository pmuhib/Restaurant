package com.tabqy1.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tabqy1.R;
import com.tabqy1.apputils.AppSession;
import com.tabqy1.models.OrderAssociateFood;

import java.util.List;

/**
 * Created by jayant on 06-05-2017.
 */

public class AssociateItemRecipt   extends RecyclerView.Adapter<AssociateItemRecipt.ViewHolder> {

    private Context context;
    private List<OrderAssociateFood> associatedFoods;
    private AppSession appSession;

    public AssociateItemRecipt(Context context, List<OrderAssociateFood> associatedFoods){
        this.context=context;
        this.associatedFoods=associatedFoods;
        this.appSession= new AppSession(context);
    }


    @Override
    public AssociateItemRecipt.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_receipt_associate, parent, false);

        return new AssociateItemRecipt.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AssociateItemRecipt.ViewHolder holder, int position) {
        OrderAssociateFood foodDetail= associatedFoods.get(position);
        holder.tvFoodName.setText(foodDetail.name);
        holder.tvUnitPrice.setText(appSession.getCurrency()+" "+foodDetail.unit_price);
        holder.tvTotalPrice.setText(appSession.getCurrency()+" "+foodDetail.price);
        holder.tvQty.setText(foodDetail.qty);
        holder.tvEa.setText("");
    }

    @Override
    public int getItemCount() {
        return associatedFoods.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvFoodName,tvUnitPrice,tvTotalPrice,tvQty,tvEa;

        public ViewHolder(View view) {
            super(view);
            tvFoodName = (TextView) view.findViewById(R.id.tv_item);
            tvUnitPrice = (TextView) view.findViewById(R.id.tv_price);
            tvTotalPrice = (TextView) view.findViewById(R.id.tv_totaprice);
            tvQty = (TextView) view.findViewById(R.id.tv_qty);
            tvEa = (TextView) view.findViewById(R.id.tv_qa);
        }
    }
}
