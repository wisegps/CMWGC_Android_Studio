package com.wgc.cmwgc.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.wgc.cmwgc.R;
import com.wgc.cmwgc.app.Config;
import com.wgc.cmwgc.fragment.CarMasterFourFragment;
import com.wgc.cmwgc.fragment.CarMasterOneFragment;
import com.wgc.cmwgc.fragment.CarMasterThreeFragment;
import com.wgc.cmwgc.fragment.CarMasterTwoFragment;
import com.wgc.cmwgc.widget.CustomerDialog;
import com.wicare.wistorm.api.WVehicleApi;
import com.wicare.wistorm.http.BaseVolley;
import com.wicare.wistorm.http.OnSuccess;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/11/3.
 */
public class CarMasterActivity extends AppCompatActivity {

    @Bind(R.id.master_viewpager)
    ViewPager masterViewpager;
    @Bind(R.id.tv_account)
    TextView tvAccount;
    @Bind(R.id.tv_date)
    TextView tvDate;
    @Bind(R.id.rl_register_info)
    RelativeLayout rlRegisterInfo;

    private List<Fragment> mFragment = new ArrayList<>();
    private ViewPagerFragmentAdatper adatper;
    private Context mContext;
    private SharedPreferences spf;
    private SharedPreferences.Editor editor;
    private boolean isBinded = false;
    private String bindedDate = "";
    private String bindedUid = "";
    private String carBand = "";
    private WVehicleApi vehicleApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_master);
        ButterKnife.bind(this);
        initWistorm();
        initView();
    }

    /**
     * Wistorm
     */
    private void initWistorm() {
        BaseVolley.init(this);
        vehicleApi = new WVehicleApi(this);
    }

    private void initView() {
        mContext = this;
        spf = getSharedPreferences(Config.SPF_MY,Activity.MODE_PRIVATE);
        editor = spf.edit();
        isBinded = spf.getBoolean(Config.BINDED,false);
        bindedDate = spf.getString(Config.BINDED_DATE,"");
        bindedUid = spf.getString(Config.BINDED_UID,"");
        carBand = spf.getString(Config.CAR_BAND,"");
        if(isBinded){
            rlRegisterInfo.setVisibility(View.VISIBLE);
            masterViewpager.setVisibility(View.GONE);
            getCarInfo();
            tvAccount.setText("车牌号码：" + carBand);
            tvDate.setText("注册日期：" + bindedDate);
        }else {
            rlRegisterInfo.setVisibility(View.GONE);
            masterViewpager.setVisibility(View.VISIBLE);
            mFragment.add(CarMasterOneFragment.getInstance());
            mFragment.add(CarMasterTwoFragment.getInstance());
            mFragment.add(CarMasterThreeFragment.getInstance());
            mFragment.add(CarMasterFourFragment.getInstance());
            adatper = new ViewPagerFragmentAdatper(getSupportFragmentManager());
            masterViewpager.setAdapter(adatper);
        }
//        MagicIndicator magicIndicator = (MagicIndicator)findViewById(R.id.indicator_container);
//        CircleNavigator circleNavigator = new CircleNavigator(this);
//        circleNavigator.setCircleCount(mFragment.size());
//        circleNavigator.setCircleColor(0XFFFD7400);
//        circleNavigator.setCircleSpacing(100);//设置指示器之间的距离
//        circleNavigator.setCircleClickListener(new CircleNavigator.OnCircleClickListener() {
//            @Override
//            public void onClick(int index) {
//                masterViewpager.setCurrentItem(index);
//            }
//        });
//        magicIndicator.setNavigator(circleNavigator);
//        ViewPagerHelper.bind(magicIndicator, masterViewpager);
    }


    /**
     * @param context
     */
    public static void startAction(Activity context) {
        Intent intent = new Intent(context, CarMasterActivity.class);
        context.startActivity(intent);
    }

    @OnClick(R.id.tv_unbind)
    public void onClick() {
        showUnbindDialog();
    }

    private void showUnbindDialog(){
        CustomerDialog.Builder builder = new CustomerDialog.Builder(this);
        builder.setMessage("请在公众号进行解除绑定");

        builder.setNegativeButton( new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }



    private void getCarInfo(){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("access_token", Config.ACCESS_TOKEN);
        params.put("uid",bindedUid);//459432808122817
        String fields = "name";
        vehicleApi.get(params, fields, new OnSuccess() {
            @Override
            protected void onSuccess(String response) {
                Logger.d("获取车辆返回的信息：" + response);
//                {"status_code":0,"data":{"name":"新NZN569","objectId":795852343127183400}}
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject object1 = new JSONObject(jsonObject.getString("data").toString());
                    if ("0".equals(jsonObject.getString("status_code"))) {
                        if(object1.has("name")){
                            editor.putString(Config.CAR_BAND,object1.getString("name"));
                            editor.commit();
                            tvAccount.setText("车牌号码：" + object1.getString("name"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },null);
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
