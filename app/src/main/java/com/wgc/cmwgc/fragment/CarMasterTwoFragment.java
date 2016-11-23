package com.wgc.cmwgc.fragment;

import android.view.View;

import com.wgc.cmwgc.R;

/**
 * Created by Administrator on 2016/11/3.
 */
public class CarMasterTwoFragment extends BaseFragment{

    public static CarMasterTwoFragment instance = null;

    public static CarMasterTwoFragment getInstance() {
        if (instance == null) {
            instance = new CarMasterTwoFragment();
        }
        return instance;
    }

    @Override
    protected int setContentViewId() {
        return R.layout.fragment_master_two;
    }

    @Override
    protected void setUpView() {

    }

    @Override
    protected void init() {

    }
}
