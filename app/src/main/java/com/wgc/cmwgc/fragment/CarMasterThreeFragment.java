package com.wgc.cmwgc.fragment;

import android.view.View;

import com.wgc.cmwgc.R;

/**
 * Created by Administrator on 2016/11/3.
 */
public class CarMasterThreeFragment extends BaseFragment{

    public static CarMasterThreeFragment instance = null;

    public static CarMasterThreeFragment getInstance() {
        if (instance == null) {
            instance = new CarMasterThreeFragment();
        }
        return instance;
    }
    @Override
    protected int setContentViewId() {
        return R.layout.fragment_master_three;
    }

    @Override
    protected void setUpView() {

    }

    @Override
    protected void init() {

    }
}
