package com.wgc.cmwgc.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.wgc.cmwgc.R;
import com.wgc.cmwgc.Until.SystemTools;
import com.wgc.cmwgc.app.Config;
import com.wgc.cmwgc.app.MyApplication;
import com.wgc.cmwgc.service.HttpService;

import java.util.Iterator;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 隐藏app 记录软件状态
 * Created by Administrator on 2016/12/6.
 */
public class AppInfoActivity extends AppCompatActivity {

    private final String TAG = AppInfoActivity.class.getName();
    private final int ONE_SECOND = 1000;
    private final int UPDATE_UI = 100;
    private LocationManager locationManager;

    @Bind(R.id.tv_app_info)
    TextView tvAppInfo;

    private String isServiceRunning = "否";
    private String isNetworkAvailable = "否";
    private String isGpsAvailable = "否";
    private int numOfSatellites = 0;
    private int useOfSatellites = 0;
    private int HeartBeat = 0;
    private int versionCode=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
        ButterKnife.bind(this);
        initBorcast();
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(getApplication().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            }
        }else {
            locationManager.addGpsStatusListener(listener);//侦听GPS状态
        }
        mHandler.postDelayed(mTasks, 0);
    }


    /**
     * 注册广播
     */
    private void initBorcast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction("MY_HEARTbeat");
        registerReceiver(receiver, filter);
    }

    /**
     * 广播接收器
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            HeartBeat = intent.getIntExtra("Heart",0);
            Log.w(TAG,"收到广播 ：" + HeartBeat);
        }
    };

    /**
     * @param context
     */
    public static void startAction(Activity context) {
        Intent intent = new Intent(context, AppInfoActivity.class);
        context.startActivity(intent);
    }


    /**
     * 检查所有状态
     */
    private void checkAllStatus() {
        versionCode = SystemTools.getVersionCode(AppInfoActivity.this);
        if (!SystemTools.isWorked(this, "com.wgc.cmwgc.service.HttpService")) {
            isServiceRunning = "否";
        }else{
            isServiceRunning = "是";
        }
        if (SystemTools.isNetworkAvailable(AppInfoActivity.this)){
            isNetworkAvailable = "是";
        }else{
            isNetworkAvailable = "否";
        }
        if(getGPSState(AppInfoActivity.this)){
            isGpsAvailable = "是";
        }else{
            isGpsAvailable = "否";
        }
        Log.w(TAG, "服务是否在运行：" + isServiceRunning + "\n"
                +"网络是否可用：" + isNetworkAvailable + "\n"
                + "GPS是否打开：" + isGpsAvailable + "\n"
                + "可见卫星数：" + numOfSatellites + "\n"
                + "心跳包数据：" + HeartBeat + "\n"
                + "版本号：" + versionCode
        );
    }


    private Runnable mTasks = new Runnable(){
        public void run(){
            checkAllStatus();//检查服务是否正在运行
            mHandler.sendEmptyMessage(UPDATE_UI);
            mHandler.postDelayed(mTasks, ONE_SECOND);
        }
    };

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE_UI:
                    updateUi();
                    break;
            }
        }
    };

    private void updateUi(){
        StringBuilder sb = new StringBuilder();
        sb.append("服务是否运行 : ");
        sb.append(isServiceRunning);
        sb.append("\n网络是否可用 : ");
        sb.append(isNetworkAvailable);
        sb.append("\n定位是否打开 : ");
        sb.append(isGpsAvailable);
        sb.append("\n可见卫星数 : ");
        sb.append(numOfSatellites);
        sb.append("\n连接卫星数 : ");
        sb.append(useOfSatellites);
        sb.append("\n心跳包数据 : ");
        sb.append(HeartBeat);
        sb.append("\n版本号 : ");
        sb.append(versionCode);
        sb.append("\n时间 ：");
        sb.append(SystemTools.getCurrentStringTime());
        tvAppInfo.setText(sb.toString());
    }


    private boolean getGPSState(Context context){
        boolean on = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return on;
    }



    private GpsStatus.Listener listener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int i) {
            GpsStatus gpsStatus= locationManager.getGpsStatus(null);
            switch (i){
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:

                    Iterable<GpsSatellite> allSatellites = gpsStatus.getSatellites();
                    Iterator<GpsSatellite> iterator = allSatellites.iterator();

                    int satellites = 0;
                    int useInfix = 0;
                    int maxSatellites=gpsStatus.getMaxSatellites();
                    while(iterator.hasNext() && satellites<maxSatellites){
                        satellites++;
//                        iterator.next();
                        GpsSatellite satellite = iterator.next();
                        if (satellite.usedInFix())
                            useInfix++;
                    }
                    useOfSatellites = useInfix;
                    numOfSatellites = satellites;
                    break;
            }
        }
    };



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mTasks);
        unregisterReceiver(receiver);
    }
}
