package com.wgc.cmwgc.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wgc.cmwgc.R;

/**
 * Created by Administrator on 2016/11/3.
 */
public class CarMasterOneFragment extends BaseFragment{

    public static CarMasterOneFragment instance = null;

    public static CarMasterOneFragment getInstance() {
        if (instance == null) {
            instance = new CarMasterOneFragment();
        }
        return instance;
    }


    @Override
    protected int setContentViewId() {
        return R.layout.fragment_master_one;
    }

    @Override
    protected void setUpView() {

    }

    @Override
    protected void init() {

    }
}
