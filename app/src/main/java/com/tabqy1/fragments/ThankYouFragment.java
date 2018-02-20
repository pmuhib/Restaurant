package com.tabqy1.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tabqy1.R;
import com.tabqy1.apputils.AppSession;
import com.tabqy1.apputils.AppUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ThankYouFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThankYouFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;
    private AppCompatActivity context;


    public ThankYouFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ThankYouFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ThankYouFragment newInstance(String param1, String param2) {
        ThankYouFragment fragment = new ThankYouFragment();
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
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=(AppCompatActivity)context;
    }

    AppSession appSession ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_thank_you, container, false);
        appSession = new AppSession(getActivity());
        initView();

        AppUtils.setAppBackGroundImage(getActivity(),(ViewGroup) view,appSession.getRestaurantBackgroundImage());

        return view;
    }



    private void initView(){
       TextView tvBack = (TextView) context.findViewById(R.id.tv_back);
       tvBack.setVisibility(View.VISIBLE);
       LinearLayout llThankYou= (LinearLayout)view.findViewById(R.id.ll_please_visit);
       TextView tvTitle=(TextView)context.findViewById(R.id.tv_title);
       tvTitle.setText(R.string.feedback);

        ImageButton btnOrder = (ImageButton) context.findViewById(R.id.btnOrder) ;
        btnOrder.setVisibility(View.GONE);


       llThankYou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeFragment homeFragment= HomeFragment.newInstance("","");
                AppUtils.setFragment(homeFragment,true,context,R.id.fragmentContainer);
            }
        });
    }

}
