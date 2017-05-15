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
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.wgc.cmwgc.R;
import com.wgc.cmwgc.Until.SystemTools;
import com.wgc.cmwgc.app.Config;

import java.math.BigDecimal;
import java.util.Iterator;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 隐藏页面 记录软件状态
 * Created by Administrator on 2016/12/6.
 */
public class AppInfoActivity extends AppCompatActivity {

    private final String TAG = AppInfoActivity.class.getName();
    private final int ONE_SECOND = 1000;
    private final int UPDATE_UI = 100;
    private LocationManager locationManager;

    @Bind(R.id.tv_app_info)
    TextView tvAppInfo;

    private boolean isServiceRunning;
    private boolean isNetworkAvailable;
    private String isGpsAvailable = "否";
    private int numOfSatellites = 0;
    private int useOfSatellites = 0;
    private boolean HeartBeat = false;
    private boolean WebSocketHeartBeat = false;
    private boolean JT808HeartBeat = false;
    //经纬度
    private String lat =""+0;
    private String logn = ""+0;
    //速度
    private int speed;
    private String direct = ""+0;
    private String mileage = ""+0;
    private SharedPreferences spf;
    private SharedPreferences.Editor editor;

    private int versionCode = 0;
    private String ip = "";
    private String port = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
        ButterKnife.bind(this);
        initSP();
        initBorcast();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getApplication().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            }
        } else {
            locationManager.addGpsStatusListener(listener);//侦听GPS状态
        }
        mHandler.postDelayed(mTasks, 0);
    }


    /**
     * 注册广播
     */
    private void initBorcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("MY_HEARTbeat");
        filter.addAction("MY_Websocket_HEARTbeat");
        filter.addAction("MY_JT808_HEARTbeat");
        filter.addAction(Config.SPEED_ENCLOSURE);

//        filter.addAction("com.ljq.activity.CountService");
        registerReceiver(receiver, filter);
    }

    /**
     * 广播接收器
     */

    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (intent.getAction().equals("MY_HEARTbeat")) {
                HeartBeat = intent.getBooleanExtra("Heart", false);
                Log.w(TAG, "收到定位广播 ：" + HeartBeat);
            } else if (intent.getAction().equals("MY_Websocket_HEARTbeat")) {
                WebSocketHeartBeat = intent.getBooleanExtra("websocket_heart", false);
                Log.w(TAG, "收到Websocket广播 ：" + " -- " + WebSocketHeartBeat);
            } else if (intent.getAction().equals("MY_JT808_HEARTbeat")) {
                Log.w(TAG, "收到JT808 服务广播 ：" + " -- " + WebSocketHeartBeat);
                JT808HeartBeat = intent.getBooleanExtra("jt_heart", false);
            }

            if (intent.getAction().equals(Config.SPEED_ENCLOSURE)){
                speed = intent.getIntExtra("speed",0);
                lat = intent.getStringExtra("lat");
                logn = intent.getStringExtra("lon");
                mileage = intent.getStringExtra("mileage");
//                direct = intent.getStringExtra("direct");

            }
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
            isServiceRunning = false;
        } else {
            isServiceRunning = true;
        }
        if (SystemTools.isNetworkAvailable(AppInfoActivity.this)) {
            isNetworkAvailable = true;
        } else {
            isNetworkAvailable = false;
        }
        if (getGPSState(AppInfoActivity.this)) {
            isGpsAvailable = "是";
        } else {
            isGpsAvailable = "否";
        }


        ip = spf.getString(Config.SP_SERVICE_IP, "");
        port = spf.getString(Config.SP_SERVICE_PORT, "");
//        lat = spf.getString(Config.LAST_LAT, "0");
//        logn = spf.getString(Config.LAST_LON, "0");
////        speed = spf.getString(Config.SPEED, "0");
//        direct = spf.getString(Config.BEARING, "0");
//        mileage = spf.getString(Config.TOTAL_MILEAGE, "0");


        Log.w(TAG, "服务是否在运行：" + isServiceRunning + "\n"
                + "网络是否可用：" + isNetworkAvailable + "\n"
                + "GPS是否打开：" + isGpsAvailable + "\n"
                + "可见卫星数：" + numOfSatellites + "\n"
                + "定位心跳包：" + HeartBeat + "\n"
                + "WebSocket链路：" + WebSocketHeartBeat + "\n"
                + "版本号：" + versionCode
        );
    }


    private Runnable mTasks = new Runnable() {
        public void run() {
            checkAllStatus();//检查服务是否正在运行
            mHandler.sendEmptyMessage(UPDATE_UI);
            mHandler.postDelayed(mTasks, ONE_SECOND);
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_UI:
                    updateUi();
                    initSP();
                    break;
            }
        }
    };

    private void initSP() {
        spf = getSharedPreferences(Config.SPF_MY, MODE_PRIVATE);
        editor = spf.edit();

        ip = spf.getString(Config.SP_SERVICE_IP, "");
        port = spf.getString(Config.SP_SERVICE_PORT, "");
//        lat = spf.getString(Config.LAST_LAT, "");
//        logn = spf.getString(Config.LAST_LON, "");
////        speed = spf.getString(Config.SPEED, "");
//        direct = spf.getString(Config.BEARING, "");
//        mileage = spf.getString(Config.TOTAL_MILEAGE, "");

    }

    private void updateUi() {
        String dingwei = "";
        String lianlu = "";
        String service = "";
        String network = "";

        String jtlianlu = "";


        if (HeartBeat) {
            dingwei = "正常";
        } else {
            dingwei = "不正常";
        }
        if (WebSocketHeartBeat) {
            lianlu = "正常";
        } else {
            lianlu = "不正常";
        }
        if (isServiceRunning) {
            service = "正常";
        } else {
            service = "不正常";
        }
        if (isNetworkAvailable) {
            network = "正常";
        } else {
            network = "不正常";
        }


        if (JT808HeartBeat) {
            jtlianlu = "正常";
        } else {
            jtlianlu = "不正常";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("IP : ");
        sb.append(ip);
        sb.append("\n端口 : ");
        sb.append(port);
        sb.append("\n服务 : ");
        sb.append(service);
        sb.append("\n网络 : ");
        sb.append(network);
        sb.append("\n定位是否打开 : ");
        sb.append(isGpsAvailable);
//        sb.append("\n可见卫星数 : ");
//        sb.append(numOfSatellites);
        sb.append("\n连接卫星数 : ");
        sb.append(useOfSatellites);
        BigDecimal bdLat = new BigDecimal(lat);
        bdLat = bdLat.setScale(6, BigDecimal.ROUND_HALF_UP);
        sb.append("\n经度 : ");
        sb.append(bdLat);
        BigDecimal bdLogn = new BigDecimal(logn);
        bdLogn = bdLogn.setScale(6, BigDecimal.ROUND_HALF_UP);
        sb.append("\n纬度 : ");
        sb.append(bdLogn+"");
        sb.append("\n速度 : ");
//        BigDecimal bdSpeed = new BigDecimal(speed);
//        bdSpeed = bdSpeed.setScale(0, BigDecimal.ROUND_HALF_UP);
        sb.append(speed);
        BigDecimal bdMileale = new BigDecimal(mileage);
        bdMileale = bdMileale.setScale(2, BigDecimal.ROUND_HALF_UP);
        sb.append("\n里程 : ");
        sb.append(bdMileale);
        sb.append("\n定位 : ");
        sb.append(dingwei);
//        BigDecimal bdDirect = new BigDecimal(direct);
//        bdDirect = bdDirect.setScale(2, BigDecimal.ROUND_HALF_UP);
//        sb.append("\n方向值 : ");
//        sb.append(bdDirect);
        sb.append("\n默认链路 : ");
        sb.append(lianlu);
        sb.append("\n部标链路 : ");
        sb.append(jtlianlu);
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
