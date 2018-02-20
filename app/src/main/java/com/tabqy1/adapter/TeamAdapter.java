package com.tabqy1.adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tabqy1.R;
import com.tabqy1.models.TeamMember;
import com.tabqy1.webservices.RetrofitApiBuilder;

import java.util.List;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.ViewHolder> {
    private List<TeamMember> tables;
    private FragmentActivity context;

    public TeamAdapter(FragmentActivity context, List<TeamMember> tables) {
        this.tables = tables;
        this.context = context;
    }

    @Override
    public TeamAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_emp, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TeamAdapter.ViewHolder viewHolder, final int i) {
 
        viewHolder.tvName.setText(tables.get(i).username);
        Glide.with(context).load(RetrofitApiBuilder.BASE_URL_EMPIMAGE+tables.get(i).profile_image).skipMemoryCache(true).into(viewHolder.ivTeam);


    }
 
    @Override
    public int getItemCount() {
        return tables.size();
    }
 
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvName,tvDesignation;
        private ImageView ivTeam;
        private FrameLayout frameContainer;


        public ViewHolder(View view) {
            super(view);
            ivTeam= (ImageView)view.findViewById(R.id.ivTeam);
            tvName= (TextView) view.findViewById(R.id.tvName);
            tvDesignation= (TextView) view.findViewById(R.id.tvDesignation);


        }
    }
 
}
