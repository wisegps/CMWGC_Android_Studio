package com.wgc.cmwgc.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.wgc.cmwgc.R;
import com.wgc.cmwgc.fragment.CarTeamFiveFragment;
import com.wgc.cmwgc.fragment.CarTeamFourFragment;
import com.wgc.cmwgc.fragment.CarTeamOneFragment;
import com.wgc.cmwgc.fragment.CarTeamThreeFragment;
import com.wgc.cmwgc.fragment.CarTeamTwoFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 车队页面
 * Created by Administrator on 2016/11/3.
 */
public class CarTeamActivity extends AppCompatActivity {

    @Bind(R.id.team_viewpager)
    ViewPager teamViewpager;

    private Context mContext;
    private List<Fragment> mFragment = new ArrayList<>();
    private ViewPagerFragmentAdatper adatper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_team);
        ButterKnife.bind(this);
        initView();

    }

    private void initView() {
        mContext = this;
        mFragment.add(CarTeamOneFragment.getInstance());
        mFragment.add(CarTeamTwoFragment.getInstance());
        mFragment.add(CarTeamThreeFragment.getInstance());
        mFragment.add(CarTeamFourFragment.getInstance());
        mFragment.add(CarTeamFiveFragment.getInstance());
        adatper = new ViewPagerFragmentAdatper(getSupportFragmentManager());
        teamViewpager.setAdapter(adatper);
    }


    /**
     * @param context
     */
    public static void startAction(Activity context) {
        Intent intent = new Intent(context, CarTeamActivity.class);
        context.startActivity(intent);
    }

    /**
     * ViewPager 适配器
     */
    class ViewPagerFragmentAdatper extends FragmentPagerAdapter {

//        String titles[] = {"美力秘籍", "美力等级", "等级特权"};

        public ViewPagerFragmentAdatper(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragment.size();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragment.get(position);
        }

//        @Override
//        public CharSequence getPageTitle(int position) {
//            return titles[position];
//        }
    }
}
