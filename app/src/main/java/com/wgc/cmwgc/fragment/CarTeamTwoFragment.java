package com.wgc.cmwgc.fragment;

import com.wgc.cmwgc.R;

/**
 * Created by Administrator on 2016/11/4.
 */
public class CarTeamTwoFragment extends BaseFragment{

    public static CarTeamTwoFragment instance = null;

    public static CarTeamTwoFragment getInstance() {
        if (instance == null) {
            instance = new CarTeamTwoFragment();
        }
        return instance;
    }


    @Override
    protected int setContentViewId() {
        return R.layout.fragment_team_two;
    }

    @Override
    protected void setUpView() {

    }

    @Override
    protected void init() {

    }
}
