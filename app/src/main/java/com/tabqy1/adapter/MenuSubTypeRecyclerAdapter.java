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

public class MenuSubTypeRecyclerAdapter extends RecyclerView.Adapter<MenuSubTypeRecyclerAdapter.MyViewHolder> {

    private AppCompatActivity context;
    private ArrayList<FoodType> foods;
    private AppSession appSession;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivItem,ivCheck;
        public TextView tvItemPrice, tvItemDesc,tvItemName, tvCount;
        public ImageButton btnAdd, btnRemove;
        private TextView tvAvailableTime;


        public MyViewHolder(View view) {
            super(view);

            tvItemName = (TextView) view.findViewById(R.id.tvItemName);
            ivItem = (ImageView) view.findViewById(R.id.iv_item);
            ivCheck = (ImageView) view.findViewById(R.id.ivCheck);
            tvItemPrice= (TextView) view.findViewById(R.id.tvItemPrice);
            tvItemDesc = (TextView) view.findViewById(R.id.tvItemDesc);
            tvCount =  (TextView) view.findViewById(R.id.tvCount);
            tvAvailableTime =  (TextView) view.findViewById(R.id.tv_available);

            btnAdd = (ImageButton) view.findViewById(R.id.ivAdd);
            btnRemove = (ImageButton) view.findViewById(R.id.ivRemove);



            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(!AppUtils.isAvailable(foods.get(getAdapterPosition()).food_availability)){
                        Toast.makeText(context,R.string.food_status,Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Log.e("Qty","qty"+foods.get(getAdapterPosition()).qty);
                    if(foods.get(getAdapterPosition()).qty >0) {
                        foods.get(getAdapterPosition()).positionID=foods.get(getAdapterPosition()).food_id+"_"+foods.get(getAdapterPosition()).qty;
                        foods.get(getAdapterPosition()).qty = foods.get(getAdapterPosition()).qty-1;
                        tvCount.setText(String.valueOf(foods.get(getAdapterPosition()).qty));
                        ((DashBoradActivity)context).setCountOfFood(foods.get(getAdapterPosition()));
                        if(foods.get(getAdapterPosition()).isSelected){
                            ((DashBoradActivity)context).removeFoodToOrder(foods.get(getAdapterPosition()));
                            if( foods.get(getAdapterPosition()).qty==0)
                            foods.get(getAdapterPosition()).isSelected = false;

                        }
                        notifyDataSetChanged();
                    }
                }
            });

            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(!AppUtils.isAvailable(foods.get(getAdapterPosition()).food_availability)){
                        Toast.makeText(context,R.string.food_status,Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(foods.get(getAdapterPosition()).associated_food!= null && foods.get(getAdapterPosition()).associated_food.size()>0) {
                        new ViewDialog().showAssociateDialog(context, "Associate Items", foods.get(getAdapterPosition()));
                    }
                    foods.get(getAdapterPosition()).qty = foods.get(getAdapterPosition()).qty+1;
                    Log.e("Qty","qty"+foods.get(getAdapterPosition()).qty);
                    tvCount.setText(String.valueOf(foods.get(getAdapterPosition()).qty));
                    foods.get(getAdapterPosition()).positionID=foods.get(getAdapterPosition()).food_id+"_"+foods.get(getAdapterPosition()).qty;
                    ((DashBoradActivity)context).setCountOfFood(foods.get(getAdapterPosition()));
                     foods.get(getAdapterPosition()).isSelected=true;
                     ((DashBoradActivity) context).addFoodToOrder(foods.get(getAdapterPosition()));
                      notifyDataSetChanged();

                }
            });

            ivCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if(!AppUtils.isAvailable(foods.get(getAdapterPosition()).food_availability)){
                        Toast.makeText(context,R.string.food_status,Toast.LENGTH_SHORT).show();
                        return;
                    }

                    foods.get(getAdapterPosition()).isSelected = !foods.get(getAdapterPosition()).isSelected;
                    if(foods.get(getAdapterPosition()).isSelected) {
                        if(foods.get(getAdapterPosition()).qty>0) {
                            ((DashBoradActivity) context).addFoodToOrder(foods.get(getAdapterPosition()));
                        }else {
                            foods.get(getAdapterPosition()).isSelected=false;
                            Toast.makeText(context,"Please select at least one item",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        foods.get(getAdapterPosition()).qty=0;
                        foods.get(getAdapterPosition()).isSelected=false;
                        ((DashBoradActivity)context).removeFoodToOrderUncheck(foods.get(getAdapterPosition()));
                    }
                    notifyDataSetChanged();
                }
            });


            ivItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DishDetailsFragment detailsFragment= DishDetailsFragment.newInstance(foods.get(getAdapterPosition()).food_id);
                    AppUtils.setFragment(detailsFragment,false,context,R.id.fragmentContainer);
                }
            });
        }


    }


    public MenuSubTypeRecyclerAdapter(AppCompatActivity context, ArrayList<FoodType> foods) {
        this.context = context;
        this.foods = foods;
        this.appSession= new AppSession(context);
    }

    @Override
    public MenuSubTypeRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_menu_search_order_list, parent, false);

        return new MenuSubTypeRecyclerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MenuSubTypeRecyclerAdapter.MyViewHolder holder, final int position) {
        String imageUrl= RetrofitApiBuilder.BASE_URL_FOODIMAGE + foods.get(position).food_image;
        Glide.with(context).load(imageUrl).into(holder.ivItem);
        holder.tvItemName.setText(foods.get(position).name);
        holder.tvItemPrice.setText(appSession.getCurrency()+foods.get(position).price);
        holder.tvItemDesc.setText(foods.get(position).description);
        holder.tvCount.setText(String.valueOf(foods.get(position).qty));
        if(foods.get(position).isSelected) {
            holder.ivCheck.setImageResource(R.drawable.tick_icon);
        }else{
            holder.ivCheck.setImageResource(R.drawable.tick_icon_unselected);
        }

        if(AppUtils.isAvailable(foods.get(position).food_availability)){
            holder.tvAvailableTime.setVisibility(View.VISIBLE);
            holder.tvAvailableTime.setText(foods.get(position).food_availability_msg);
            holder.btnAdd.setVisibility(View.VISIBLE);
            holder.btnRemove.setVisibility(View.VISIBLE);
            holder.ivCheck.setVisibility(View.VISIBLE);
        }else{
            holder.tvAvailableTime.setVisibility(View.VISIBLE);
            holder.tvAvailableTime.setText(foods.get(position).food_availability_msg);
            holder.btnAdd.setVisibility(View.INVISIBLE);
            holder.btnRemove.setVisibility(View.INVISIBLE);
            holder.ivCheck.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }





}
