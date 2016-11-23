package com.wgc.cmwgc.fragment;

import com.wgc.cmwgc.R;

/**
 * Created by Administrator on 2016/11/4.
 */
public class CarTeamFourFragment extends BaseFragment{

    public static CarTeamFourFragment instance = null;

    public static CarTeamFourFragment getInstance() {
        if (instance == null) {
            instance = new CarTeamFourFragment();
        }
        return instance;
    }


    @Override
    protected int setContentViewId() {
        return R.layout.fragment_team_four;
    }

    @Override
    protected void setUpView() {

    }

    @Override
    protected void init() {

    }
}
