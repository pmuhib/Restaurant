package com.tabqy1.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tabqy1.R;
import com.tabqy1.activity.DashBoradActivity;
import com.tabqy1.apputils.AppSession;
import com.tabqy1.apputils.AppUtils;
import com.tabqy1.fragments.FeedBackFragmentNew;
import com.tabqy1.fragments.OrderFragment;
import com.tabqy1.fragments.RecipeFragment;
import com.tabqy1.models.OrderHistory;
import com.tabqy1.models.Waitertable;
import com.tabqy1.uiutils.SimpleSectionedAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jayant on 12-10-2016.
 */

public class OrderHistoryRecyclerAdapter extends SimpleSectionedAdapter<OrderHistoryRecyclerAdapter.OrderHistoryViewHolder> {

    private final AppSession appSession;
    public ArrayList<List<OrderHistory>> orderHistoryModels = new ArrayList<>();
    private Context context;
    public OrderHistoryRecyclerAdapter(Context context , ArrayList<List<OrderHistory>> orderHistories){
        this.context = context;
        this.orderHistoryModels = orderHistories;
        this.appSession= new AppSession(context);
    }

    @Override
    protected String getSectionHeaderTitle(int section) {
        return section == 0 ? "Order in Progress" : "Completed Order";
    }



    @Override
    protected int getSectionHeaderColor(int section) {
        return section == 0 ? context.getResources().getColor(R.color.colorPrimaryDark) :context.getResources().getColor(R.color.colorYallow);
    }

    @Override
    protected int getSectionCount() {
        return 2;
    }

    @Override
    protected int getItemCountForSection(int section) {
        return orderHistoryModels.get(section).size();
    }

    @Override
    protected OrderHistoryViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_order, parent, false);
        return new OrderHistoryViewHolder(view);
    }

    @Override
    protected void onBindItemViewHolder(OrderHistoryViewHolder holder, int section, int position) {
        OrderHistory orderHistory = orderHistoryModels.get(section).get(position);
        holder.table_number = orderHistory.table_id ;
        holder.order_number =  orderHistory.order_no  ;
        holder.render(orderHistory,section);
    }

    public class OrderHistoryViewHolder extends RecyclerView.ViewHolder{
        private TextView tvDate;
        private TextView tvTableNo;
        private TextView tvOrderValue;
        private TextView tvOrderTime;
        private TextView tvWaiterName;
        private TextView tvFeedback;
        private TextView tvDetail;
        private TextView tvPrint;
        public String order_number ;
        public String table_number ;
        public OrderHistoryViewHolder(View view) {
            super(view);

            tvDate = (TextView)view.findViewById(R.id.tvDate);
            tvTableNo = (TextView)view.findViewById(R.id.tvTableNo);
            tvOrderValue = (TextView)view.findViewById(R.id.tvOrderValue);
            tvOrderTime = (TextView)view.findViewById(R.id.tvOrderTime);
            tvWaiterName = (TextView)view.findViewById(R.id.tvWaiterName);
            tvFeedback = (TextView)view.findViewById(R.id.tvFeedback);
            tvDetail = (TextView) view.findViewById(R.id.tvDetail);
            tvPrint = (TextView) view.findViewById(R.id.tvPrint);
            tvFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FeedBackFragmentNew feedBackFragment = FeedBackFragmentNew.newInstance();

                    Bundle bundle =  new Bundle() ;
                    bundle.putString("order_number",order_number);
                    bundle.putString("table_number",table_number);
                    feedBackFragment.setArguments(bundle);
                    AppUtils.setFragment(feedBackFragment,false,(FragmentActivity)context,R.id.fragmentContainer);
                }
            });

            tvDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((DashBoradActivity)context).clearOrder();
                    Waitertable waitertable = new Waitertable();
                    waitertable.table_id = orderHistoryModels.get(0).get(getAdapterPosition()-1).table_id;
                    waitertable.table_name = orderHistoryModels.get(0).get(getAdapterPosition()-1).table_name;
                    ((DashBoradActivity)context).setTableNumber(waitertable);
                    String tableId = orderHistoryModels.get(0).get(getAdapterPosition()-1).table_id;
                    OrderFragment orderFragment = OrderFragment.newInstance(orderHistoryModels.get(0).get(getAdapterPosition()-1).order_no,Integer.parseInt(orderHistoryModels.get(0).get(getAdapterPosition()-1).type),tableId, orderHistoryModels.get(0).get(getAdapterPosition()-1).order_value,orderHistoryModels.get(0).get(getAdapterPosition()-1).order_value_no_tax, orderHistoryModels.get(0).get(getAdapterPosition()-1).customer_status);
                    AppUtils.setFragment(orderFragment,false,(FragmentActivity)context,R.id.fragmentContainer);

                }
            });


            tvPrint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isHome =  false ;
                    if(table_number.trim().isEmpty()){
                        isHome = true ;
                    }
                    RecipeFragment homeFragment = RecipeFragment.newInstance(order_number,table_number,isHome,true);
                    AppUtils.setFragment(homeFragment, true, (FragmentActivity)context, R.id.fragmentContainer);
                }
            });
           /*

           view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog(orderHistoryModels.get(getAdapterPosition()).detailsList);
                }
            });
            */

        }

        public void render(OrderHistory model,int section){
            tvDate .setText(model.order_date);
            if(model.table_name!=null && model.table_name.length()>0){
                tvTableNo .setText(model.table_name);
            }else{
                tvTableNo .setText("Home Delivery");
            }

            tvOrderValue.setText(appSession.getCurrency()+model.order_value);
            tvOrderTime .setText(model.order_time);
            tvWaiterName.setText(model.waiter_name);

            if(section==0){
                tvFeedback.setVisibility(View.VISIBLE);
                tvDetail.setVisibility(View.VISIBLE);
                tvPrint.setVisibility(View.GONE);
            }else{
                tvFeedback.setVisibility(View.VISIBLE);
                tvDetail.setVisibility(View.GONE);
                tvPrint.setVisibility(View.VISIBLE);
            }

        }
    }
}
