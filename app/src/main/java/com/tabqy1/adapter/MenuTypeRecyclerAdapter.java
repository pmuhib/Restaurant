package com.tabqy1.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tabqy1.R;
import com.tabqy1.apputils.ItemClickListener;
import com.tabqy1.models.Cusinie;

import java.util.ArrayList;

/**
 * Created by jayant on 16-10-2016.
 */

public class MenuTypeRecyclerAdapter extends RecyclerView.Adapter<MenuTypeRecyclerAdapter.MyViewHolder> {

    private ArrayList<Cusinie> menuList;
    private Context context;
    private ItemClickListener itemClickListener = null;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvMenuType;


        public MyViewHolder(View view) {
            super(view);

            tvMenuType = (TextView) view.findViewById(R.id.tvMenuType);

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


    public MenuTypeRecyclerAdapter(Context context, ArrayList<Cusinie> menuList) {
        this.context = context;
        this.menuList =menuList;
    }

    @Override
    public MenuTypeRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_menu_list_item, parent, false);

        return new MenuTypeRecyclerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuTypeRecyclerAdapter.MyViewHolder holder, int position) {
          holder.tvMenuType.setText(menuList.get(position).name );
          holder.tvMenuType.setSelected(menuList.get(position).isSelected);

    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }





}
