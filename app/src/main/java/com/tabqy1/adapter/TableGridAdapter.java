package com.tabqy1.adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tabqy1.R;
import com.tabqy1.activity.DashBoradActivity;
import com.tabqy1.apputils.AppSession;
import com.tabqy1.apputils.AppUtils;
import com.tabqy1.fragments.MenuFragment;
import com.tabqy1.models.Waitertable;

import java.util.ArrayList;

public class TableGridAdapter extends RecyclerView.Adapter<TableGridAdapter.ViewHolder> {
    private final AppSession appSession;
    private ArrayList<Waitertable> tables;
    private FragmentActivity context;
 
    public TableGridAdapter(FragmentActivity context, ArrayList<Waitertable> tables) {
        this.tables = tables;
        this.context = context;
        appSession =new AppSession(context);
    }
 
    @Override
    public TableGridAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_table_grid, viewGroup, false);
        return new ViewHolder(view);
    }
 
    @Override
    public void onBindViewHolder(TableGridAdapter.ViewHolder viewHolder,final int i) {
 
        viewHolder.tvTable.setText(tables.get(i).table_name);
        final String isBooked = tables.get(i).isBooked.toLowerCase() ;
        viewHolder.frameContainer.setSelected(isBooked.equals("booked"));
        viewHolder.frameContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isBooked.equals("booked")) {
                    ((DashBoradActivity) context).setTableNumber(tables.get(i));
                    MenuFragment menuFragment = MenuFragment.newInstance("", "");
                    AppUtils.setFragment(menuFragment, false, context, R.id.fragmentContainer);
                }else {
                    Toast.makeText(context, tables.get(i).table_name +" already booked.Please choose another table", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
 
    @Override
    public int getItemCount() {
        return tables.size();
    }
 
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTable;
        private ImageView ivTable;
        private FrameLayout frameContainer;


        public ViewHolder(View view) {
            super(view);

            tvTable = (TextView)view.findViewById(R.id.tvTable);
            ivTable = (ImageView) view.findViewById(R.id.ivTable);
            frameContainer = (FrameLayout) view.findViewById(R.id.container);
        }
    }
 
}
