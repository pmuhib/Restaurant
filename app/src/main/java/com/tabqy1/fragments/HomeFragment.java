package com.tabqy1.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tabqy1.R;
import com.tabqy1.apputils.AppUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView tvMenu,tvTodaySpecial,tvHotDeal,tvOrderProgress,tvOrderHistory,tvFeedback;
    private AppCompatActivity activity;
    private View view;


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        return view;
    }


    private void initView( View  view){
        TextView tvBack = (TextView) activity.findViewById(R.id.tv_back);
        tvBack.setVisibility(View.INVISIBLE);
        TextView tvTitle=(TextView)activity.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.dashboard);

        ImageButton btnOrder = (ImageButton) activity.findViewById(R.id.btnOrder) ;
        btnOrder.setVisibility(View.VISIBLE);


        tvMenu =(TextView)view.findViewById(R.id.tvMenu);
        tvTodaySpecial=(TextView)view.findViewById(R.id.tvTodaySpecial);
        tvHotDeal=(TextView)view.findViewById(R.id.tvHotDeal);
        tvOrderProgress=(TextView)view.findViewById(R.id.tvOrderProgress);
        tvOrderHistory=(TextView)view.findViewById(R.id.tvOrderHistory);
        tvFeedback=(TextView)view.findViewById(R.id.tvFeedback);

        tvMenu.setOnClickListener(this);
        tvTodaySpecial.setOnClickListener(this);
        tvHotDeal.setOnClickListener(this);
        tvOrderProgress.setOnClickListener(this);
        tvOrderHistory.setOnClickListener(this);
        tvFeedback.setOnClickListener(this);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
         this.activity=(AppCompatActivity)context;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tvMenu :
                MenuFragment menuFragment = MenuFragment.newInstance("","");
                AppUtils.setFragment(menuFragment,false,activity,R.id.fragmentContainer);
                break;

            case R.id.tvTodaySpecial :
                TodaySpecialFragment todaySpecialFragment = TodaySpecialFragment.newInstance("","");
                AppUtils.setFragment(todaySpecialFragment,false,activity,R.id.fragmentContainer);

                break;

            case R.id.tvHotDeal:

                HotDealFragment hotDealFragment = HotDealFragment.newInstance("","");
                AppUtils.setFragment(hotDealFragment,false,activity,R.id.fragmentContainer);

                break;

            case R.id.tvOrderProgress :
                OrderProgressFragment orderProgressFragment = OrderProgressFragment.newInstance("","");
                AppUtils.setFragment(orderProgressFragment,false,activity,R.id.fragmentContainer);

                break;

            case R.id.tvOrderHistory :
                OrderHistoryFragment orderHistoryFragment = OrderHistoryFragment.newInstance("","");
                AppUtils.setFragment(orderHistoryFragment,false,activity,R.id.fragmentContainer);
                break;

            case R.id.tvFeedback :

                FeedBackFragmentNew feedBackFragment = FeedBackFragmentNew.newInstance();
                AppUtils.setFragment(feedBackFragment,false,activity,R.id.fragmentContainer);

                break;

        }
    }
}



