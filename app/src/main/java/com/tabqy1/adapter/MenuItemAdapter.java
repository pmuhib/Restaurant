package com.tabqy1.adapter;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tabqy1.R;
import com.tabqy1.apputils.AppUtils;
import com.tabqy1.fragments.MenuListFragment;
import com.tabqy1.models.Menu;
import com.tabqy1.webservices.RetrofitApiBuilder;

import java.util.List;

/**
 * Created by jayant on 16-10-2016.
 */

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.MyViewHolder> {

    private List<Menu> relatedItmes;
    private AppCompatActivity context;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvItemName;
        private CardView cvMenu;
        private ImageView ivMenuIcon;


        public MyViewHolder(View view) {
            super(view);

            tvItemName = (TextView) view.findViewById(R.id.menu_data);
            cvMenu= (CardView) view.findViewById(R.id.cv_menu);
            ivMenuIcon= (ImageView) view.findViewById(R.id.menu_image);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MenuListFragment menuListFragment = MenuListFragment.newInstance(relatedItmes.get(getAdapterPosition()).id,relatedItmes.get(getAdapterPosition()).name);
                    AppUtils.setFragment(menuListFragment,false,context,R.id.fragmentContainer);
                }
            });
        }

    }



    public MenuItemAdapter(AppCompatActivity context, List<Menu> relatedItmes) {
        this.context = context;
        this.relatedItmes = relatedItmes;
    }

    @Override
    public MenuItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_menu, parent, false);
        return new MenuItemAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuItemAdapter.MyViewHolder holder, int position) {
        holder.tvItemName.setText(relatedItmes.get(position).name);
        String imageUrl= RetrofitApiBuilder.BASE_URL_MENU_IMAGE + relatedItmes.get(position).image;
        Glide.with(context).load(imageUrl).into(holder.ivMenuIcon);
        holder.cvMenu.setCardBackgroundColor(Color.parseColor(relatedItmes.get(position).color_code));
    }

    @Override
    public int getItemCount() {
        return relatedItmes.size();
    }



}
