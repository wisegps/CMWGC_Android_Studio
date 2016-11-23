package com.wgc.cmwgc.fragment;

import com.wgc.cmwgc.R;

/**
 * Created by Administrator on 2016/11/4.
 */
public class CarTeamThreeFragment extends BaseFragment{

    public static CarTeamThreeFragment instance = null;

    public static CarTeamThreeFragment getInstance() {
        if (instance == null) {
            instance = new CarTeamThreeFragment();
        }
        return instance;
    }


    @Override
    protected int setContentViewId() {
        return R.layout.fragment_team_three;
    }

    @Override
    protected void setUpView() {

    }

    @Override
    protected void init() {

    }
}
