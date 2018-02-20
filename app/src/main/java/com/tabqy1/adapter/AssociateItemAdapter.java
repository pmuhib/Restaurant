package com.tabqy1.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tabqy1.R;
import com.tabqy1.apputils.AppSession;
import com.tabqy1.apputils.AppUtils;
import com.tabqy1.models.AssociatedFood;
import com.tabqy1.webservices.RetrofitApiBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jayant on 16-10-2016.
 */

public class AssociateItemAdapter extends RecyclerView.Adapter<AssociateItemAdapter.MyViewHolder> {

    private List<AssociatedFood> relatedItmes;
    private Context context;
    private AppSession session;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvItemName,tvDescription,tvPrice,tvAvailable;
        private ImageView ivItem;
        private AppCompatCheckBox checkBox;


        public MyViewHolder(View view) {
            super(view);

            tvItemName = (TextView) view.findViewById(R.id.tv_title);
            tvDescription = (TextView) view.findViewById(R.id.tv_description);
            tvPrice = (TextView) view.findViewById(R.id.tv_price);
            tvAvailable = (TextView) view.findViewById(R.id.tv_available);
            ivItem= (ImageView) view.findViewById(R.id.iv_item);
            checkBox= (AppCompatCheckBox) view.findViewById(R.id.cb_associate);


            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    if(!AppUtils.isAvailable(relatedItmes.get(getAdapterPosition()).associated_food_food_availability)){
                       // Toast.makeText(context,R.string.sold_out,Toast.LENGTH_SHORT).show();
                        return;
                    }

                    relatedItmes.get(getAdapterPosition()).isSelected = b;

                }
            });
        }

    }



    public AssociateItemAdapter(Context context, List<AssociatedFood> relatedItmes) {
        this.context = context;
        this.relatedItmes = relatedItmes;
        this.session= new AppSession(context);
        setDefaultData(this.relatedItmes);
    }

    private void setDefaultData(List<AssociatedFood> relatedItmes) {

        for (AssociatedFood associatedFood: relatedItmes
                ) {

            associatedFood.isSelected=false;
        }

    }


    @Override
    public AssociateItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_associate, parent, false);

        return new AssociateItemAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AssociateItemAdapter.MyViewHolder holder, int position) {
        holder.tvItemName.setText(relatedItmes.get(position).associated_food_name);
        holder.tvDescription.setText(relatedItmes.get(position).associated_food_description);
        holder.tvPrice.setText(session.getCurrency()+relatedItmes.get(position).associated_food_price);
        String imageUrl= RetrofitApiBuilder.BASE_URL_ASSOCIATED_IMAGE + relatedItmes.get(position).associated_food_food_image;
        Glide.with(context).load(imageUrl).into(holder.ivItem);

        if(AppUtils.isAvailable(relatedItmes.get(position).associated_food_food_availability)){
            holder.tvAvailable.setVisibility(View.GONE);
            holder.tvAvailable.setText(relatedItmes.get(position).associated_food_availability_time);
        }else{
            holder.tvAvailable.setVisibility(View.VISIBLE);
            holder.tvAvailable.setText(R.string.sold_out);
        }
        holder.checkBox.setChecked(relatedItmes.get(position).isSelected);
        holder.checkBox.setEnabled(AppUtils.isAvailable(relatedItmes.get(position).associated_food_food_availability));

    }

    @Override
    public int getItemCount() {
        return relatedItmes.size();
    }

    public List<AssociatedFood> getRelatedItmes(){
        List<AssociatedFood> associatedFoods = new ArrayList<>();
        for (AssociatedFood associatedFood: relatedItmes) {
            if(associatedFood.isSelected){
                AssociatedFood  selectedAssociatedFood = new AssociatedFood();
                selectedAssociatedFood.isSelected = associatedFood.isSelected;
                selectedAssociatedFood.associated_food_id = associatedFood.associated_food_id;
                selectedAssociatedFood.asscioted_food_code = associatedFood.asscioted_food_code;
                selectedAssociatedFood.associated_food_food_code = associatedFood.associated_food_food_code;
                selectedAssociatedFood.asscioted_price = associatedFood.asscioted_price;
                selectedAssociatedFood.associated_food_price = associatedFood.associated_food_price;
                selectedAssociatedFood.associated_food_name = associatedFood.associated_food_name;
                selectedAssociatedFood.associated_food_food_image = associatedFood.associated_food_food_image;
                selectedAssociatedFood.associated_food_description = associatedFood.associated_food_description;
                associatedFoods.add(selectedAssociatedFood);
            }
        }
        return associatedFoods;
    }

}
