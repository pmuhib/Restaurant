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

import java.util.ArrayList;

/**
 * Created by jayant on 16-10-2016.
 */

public class SearchListRecyclerAdapter extends RecyclerView.Adapter<SearchListRecyclerAdapter.MyViewHolder> {

    private final AppSession appSession;
    public AppCompatActivity context;
    private ArrayList<FoodType> foodTypes;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivItem,ivCheck;
        public TextView tvItemPrice, tvItemDesc,tvItemName, tvCount;
        public ImageButton btnAdd, btnRemove;
        private TextView tvAvailableTime;

        public MyViewHolder(View view) {
            super(view);

            tvItemName = (TextView) view.findViewById(R.id.tvItemName);
            tvItemPrice= (TextView) view.findViewById(R.id.tvItemPrice);
            tvItemDesc = (TextView) view.findViewById(R.id.tvItemDesc);
            tvCount =  (TextView) view.findViewById(R.id.tvCount);
            ivItem =  (ImageView) view.findViewById(R.id.iv_item);
            ivCheck =  (ImageView) view.findViewById(R.id.ivCheck);
            btnAdd = (ImageButton) view.findViewById(R.id.ivAdd);
            btnRemove = (ImageButton) view.findViewById(R.id.ivRemove);
            tvAvailableTime =  (TextView) view.findViewById(R.id.tv_available);



            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(!AppUtils.isAvailable(foodTypes.get(getAdapterPosition()).food_availability)){
                        Toast.makeText(context,R.string.food_status,Toast.LENGTH_SHORT).show();
                        return;
                    }


                    Log.e("Qty","qty"+foodTypes.get(getAdapterPosition()).qty);
                    if(foodTypes.get(getAdapterPosition()).qty >0) {
                        foodTypes.get(getAdapterPosition()).positionID=foodTypes.get(getAdapterPosition()).food_id+"_"+foodTypes.get(getAdapterPosition()).qty;
                        foodTypes.get(getAdapterPosition()).qty = foodTypes.get(getAdapterPosition()).qty-1;
                        tvCount.setText(String.valueOf(foodTypes.get(getAdapterPosition()).qty));
                        ((DashBoradActivity)context).setCountOfFood(foodTypes.get(getAdapterPosition()));
                        if(foodTypes.get(getAdapterPosition()).isSelected){
                            ((DashBoradActivity)context).removeFoodToOrder(foodTypes.get(getAdapterPosition()));
                            if( foodTypes.get(getAdapterPosition()).qty==0)
                                foodTypes.get(getAdapterPosition()).isSelected = false;

                        }
                        notifyDataSetChanged();
                    }
                }
            });

            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(!AppUtils.isAvailable(foodTypes.get(getAdapterPosition()).food_availability)){
                        Toast.makeText(context,R.string.food_status,Toast.LENGTH_SHORT).show();
                        return;
                    }


                    if(foodTypes.get(getAdapterPosition()).associated_food!= null && foodTypes.get(getAdapterPosition()).associated_food.size()>0) {
                        new ViewDialog().showAssociateDialog(context, "Associate Items", foodTypes.get(getAdapterPosition()));
                    }
                    foodTypes.get(getAdapterPosition()).qty = foodTypes.get(getAdapterPosition()).qty+1;
                    Log.e("Qty","qty"+foodTypes.get(getAdapterPosition()).qty);
                    tvCount.setText(String.valueOf(foodTypes.get(getAdapterPosition()).qty));
                    foodTypes.get(getAdapterPosition()).positionID=foodTypes.get(getAdapterPosition()).food_id+"_"+foodTypes.get(getAdapterPosition()).qty;
                    ((DashBoradActivity)context).setCountOfFood(foodTypes.get(getAdapterPosition()));
                    foodTypes.get(getAdapterPosition()).isSelected=true;
                    ((DashBoradActivity) context).addFoodToOrder(foodTypes.get(getAdapterPosition()));
                    notifyDataSetChanged();

                }
            });

          ivCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!AppUtils.isAvailable(foodTypes.get(getAdapterPosition()).food_availability)){
                        Toast.makeText(context,R.string.food_status,Toast.LENGTH_SHORT).show();
                        return;
                    }

                    foodTypes.get(getAdapterPosition()).isSelected = !foodTypes.get(getAdapterPosition()).isSelected;
                    if(foodTypes.get(getAdapterPosition()).isSelected) {
                        if(foodTypes.get(getAdapterPosition()).qty>0) {
                            ((DashBoradActivity) context).addFoodToOrder(foodTypes.get(getAdapterPosition()));
                        }else {
                            foodTypes.get(getAdapterPosition()).isSelected=false;
                            Toast.makeText(context,"Please select at least one item",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        foodTypes.get(getAdapterPosition()).qty=0;
                        foodTypes.get(getAdapterPosition()).isSelected=false;
                        ((DashBoradActivity)context).removeFoodToOrderUncheck(foodTypes.get(getAdapterPosition()));
                    }
                    notifyDataSetChanged();
                }
            });


            ivItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DishDetailsFragment detailsFragment= DishDetailsFragment.newInstance(foodTypes.get(getAdapterPosition()).food_id);
                    AppUtils.setFragment(detailsFragment,false,context,R.id.fragmentContainer);
                }
            });

        }

    }


    public SearchListRecyclerAdapter(AppCompatActivity context,ArrayList<FoodType> foodTypes) {
        this.context = context;
        this.foodTypes=foodTypes;
        appSession = new AppSession(context);
    }

    @Override
    public SearchListRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_menu_search_order_list, parent, false);

        return new SearchListRecyclerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SearchListRecyclerAdapter.MyViewHolder holder, final int position) {

        String imageUrl= RetrofitApiBuilder.BASE_URL_FOODIMAGE + foodTypes.get(position).food_image;
        Glide.with(context).load(imageUrl).into(holder.ivItem);
        holder.tvItemName.setText(foodTypes.get(position).name);
        holder.tvItemPrice.setText(appSession.getCurrency()+foodTypes.get(position).price);
        holder.tvItemDesc.setText(foodTypes.get(position).description);
        holder.tvCount.setText(String.valueOf(foodTypes.get(position).qty));
        if(foodTypes.get(position).isSelected) {
            holder.ivCheck.setImageResource(R.drawable.tick_icon);
        }else{
            holder.ivCheck.setImageResource(R.drawable.tick_icon_unselected);
        }


        if(AppUtils.isAvailable(foodTypes.get(position).food_availability)){
            holder.tvAvailableTime.setVisibility(View.VISIBLE);
            holder.tvAvailableTime.setText(foodTypes.get(position).food_availability_msg);
            holder.btnAdd.setVisibility(View.VISIBLE);
            holder.btnRemove.setVisibility(View.VISIBLE);
            holder.ivCheck.setVisibility(View.VISIBLE);
        }else{
            holder.tvAvailableTime.setVisibility(View.VISIBLE);
            holder.tvAvailableTime.setText(foodTypes.get(position).food_availability_msg);
            holder.btnAdd.setVisibility(View.INVISIBLE);
            holder.btnRemove.setVisibility(View.INVISIBLE);
            holder.ivCheck.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return foodTypes.size();
    }





}
