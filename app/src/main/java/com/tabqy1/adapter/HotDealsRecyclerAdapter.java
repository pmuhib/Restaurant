package com.tabqy1.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tabqy1.R;
import com.tabqy1.activity.DashBoradActivity;
import com.tabqy1.apputils.AppSession;
import com.tabqy1.apputils.AppUtils;
import com.tabqy1.apputils.ViewDialog;
import com.tabqy1.fragments.DishDetailsFragment;
import com.tabqy1.models.FoodType;
import com.tabqy1.webservices.RetrofitApiBuilder;

import java.util.List;

/**
 * Created by jayant on 16-10-2016.
 */

public class HotDealsRecyclerAdapter extends RecyclerView.Adapter<HotDealsRecyclerAdapter.MyViewHolder> {

    public AppCompatActivity context;
    public List<FoodType> data;
    public AppSession appSession;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvItemPrice, tvItemDesc,tvItemName, tvCount;
        public ImageButton btnAdd, btnRemove;
        public ImageView ivItem,ivCheck;
        private TextView tvAvailableTime;



        public MyViewHolder(View view) {
            super(view);

            tvItemName = (TextView) view.findViewById(R.id.tvItemName);
            tvItemPrice= (TextView) view.findViewById(R.id.tvItemPrice);
            tvItemDesc = (TextView) view.findViewById(R.id.tvItemDesc);
            ivItem = (ImageView) view.findViewById(R.id.iv_item);
            ivCheck = (ImageView) view.findViewById(R.id.ivCheck);
            tvCount = (TextView) view.findViewById(R.id.tvCount);
            btnAdd = (ImageButton) view.findViewById(R.id.ivAdd);
            btnRemove = (ImageButton) view.findViewById(R.id.ivRemove);
            tvAvailableTime =  (TextView) view.findViewById(R.id.tv_available);




            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(!AppUtils.isAvailable(data.get(getAdapterPosition()).food_availability)){
                        Toast.makeText(context,R.string.food_status,Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Log.e("Qty","qty"+data.get(getAdapterPosition()).qty);
                    if(data.get(getAdapterPosition()).qty >0) {
                        data.get(getAdapterPosition()).qty = data.get(getAdapterPosition()).qty-1;
                        tvCount.setText(String.valueOf(data.get(getAdapterPosition()).qty));
                        ((DashBoradActivity)context).setCountOfFood(data.get(getAdapterPosition()));
                        if( data.get(getAdapterPosition()).qty==0 && data.get(getAdapterPosition()).isSelected){
                            ((DashBoradActivity)context).removeFoodToOrder(data.get(getAdapterPosition()));
                             data.get(getAdapterPosition()).isSelected = false;
                        }
                        notifyDataSetChanged();
                    }
                }
            });

            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(!AppUtils.isAvailable(data.get(getAdapterPosition()).food_availability)){
                        Toast.makeText(context,R.string.food_status,Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(data.get(getAdapterPosition()).associated_food!=null && data.get(getAdapterPosition()).associated_food.size()>0) {
                        new ViewDialog().showAssociateDialog(context, "Associate Items", data.get(getAdapterPosition()));
                    }
                    data.get(getAdapterPosition()).qty = data.get(getAdapterPosition()).qty+1;
                    Log.e("Qty","qty"+data.get(getAdapterPosition()).qty);
                    data.get(getAdapterPosition()).positionID=data.get(getAdapterPosition()).food_id+"_"+data.get(getAdapterPosition()).qty;
                    tvCount.setText(String.valueOf(data.get(getAdapterPosition()).qty));
                    ((DashBoradActivity)context).setCountOfFood(data.get(getAdapterPosition()));
                     data.get(getAdapterPosition()).isSelected=true;
                     ((DashBoradActivity) context).addFoodToOrder(data.get(getAdapterPosition()));
                     notifyDataSetChanged();

                }
            });


            ivCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!AppUtils.isAvailable(data.get(getAdapterPosition()).food_availability)){
                        Toast.makeText(context,R.string.food_status,Toast.LENGTH_SHORT).show();
                        return;
                    }

                    data.get(getAdapterPosition()).isSelected = !data.get(getAdapterPosition()).isSelected;
                    if(data.get(getAdapterPosition()).isSelected) {
                        if(data.get(getAdapterPosition()).qty>0) {
                            ((DashBoradActivity) context).addFoodToOrder(data.get(getAdapterPosition()));
                        }else {
                            data.get(getAdapterPosition()).isSelected=false;
                            Toast.makeText(context,"Please select atleast one item",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        data.get(getAdapterPosition()).qty=0;
                        data.get(getAdapterPosition()).isSelected=false;
                        ((DashBoradActivity)context).removeFoodToOrderUncheck(data.get(getAdapterPosition()));
                    }
                    notifyDataSetChanged();
                }
            });

            ivItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DishDetailsFragment detailsFragment= DishDetailsFragment.newInstance(data.get(getAdapterPosition()).food_id);
                    AppUtils.setFragment(detailsFragment,false,context,R.id.fragmentContainer);
                }
            });

        }

    }


    public HotDealsRecyclerAdapter(AppCompatActivity context, List<FoodType> data) {
        this.context = context;
        this.data = data;
        appSession = new AppSession(context);
    }

    @Override
    public HotDealsRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_hot_deals, parent, false);

       return new HotDealsRecyclerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final HotDealsRecyclerAdapter.MyViewHolder holder,final int position) {

        String imageUrl= RetrofitApiBuilder.BASE_URL_FOODIMAGE + data.get(position).food_image;
        Glide.with(context).load(imageUrl).into(holder.ivItem);
        holder.tvItemName.setText(data.get(position).name);
        holder.tvItemPrice.setText(appSession.getCurrency()+data.get(position).price);
        holder.tvItemDesc.setText(data.get(position).description);
        holder.tvCount.setText(String.valueOf(data.get(position).qty));
        if(data.get(position).isSelected) {
            holder.ivCheck.setImageResource(R.drawable.tick_icon);
        }else{
            holder.ivCheck.setImageResource(R.drawable.tick_icon_unselected);
        }

        if(AppUtils.isAvailable(data.get(position).food_availability)){
            holder.tvAvailableTime.setVisibility(View.VISIBLE);
            holder.tvAvailableTime.setText(data.get(position).food_availability_msg);
            holder.btnAdd.setVisibility(View.VISIBLE);
            holder.btnRemove.setVisibility(View.VISIBLE);
            holder.ivCheck.setVisibility(View.VISIBLE);
        }else{
            holder.tvAvailableTime.setVisibility(View.VISIBLE);
            holder.tvAvailableTime.setText(data.get(position).food_availability_msg);
            holder.btnAdd.setVisibility(View.INVISIBLE);
            holder.btnRemove.setVisibility(View.INVISIBLE);
            holder.ivCheck.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return data.size();
    }



}
