package com.wgc.cmwgc.fragment;

import com.wgc.cmwgc.R;

/**
 * Created by Administrator on 2016/11/4.
 */
public class CarTeamOneFragment extends BaseFragment{

    public static CarTeamOneFragment instance = null;

    public static CarTeamOneFragment getInstance() {
        if (instance == null) {
            instance = new CarTeamOneFragment();
        }
        return instance;
    }


    @Override
    protected int setContentViewId() {
        return R.layout.fragment_team_one;
    }

    @Override
    protected void setUpView() {

    }

    @Override
    protected void init() {

    }
}
