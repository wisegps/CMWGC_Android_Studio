package com.wgc.cmwgc.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.wgc.cmwgc.Until.SystemTools;
import com.wgc.cmwgc.Until.Utils;
import com.wgc.cmwgc.app.Config;

/**
 * Created by Administrator on 2016/12/19.
 */
public class CoreServerAgin extends Service {

    private String TAG = "CoreServerAgin";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return (IBinder) startS1;
    }

    private final String START_LOCATION = "start_my_location_service";
    private final String START_SPEED_ALERT = "start_my_speed_alert_service";
    private final String START_BEI_DOU = "start_my_bei_dou_service";


    private final String HEART_BEAT = "MY_HEARTbeat";
    public static final String START_UPLPAD ="start_upload_data_incase_time_incorrect";
    public static final String ERROR_API = "api_error_happen";

    private int ONE_MINUTES = 1000 * 60;// five
    private ServiceBroadcast mServiceBroadcast;
    private Handler objHandler = new Handler();
    private  int numIsRun = 0;
    private  int numAgain = 0;
    private SharedPreferences spf;
    private SharedPreferences.Editor editor;

///////////////////////////////////////////////////////////////////
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    startCoreServer();
                    break;

                default:
                    break;
            }

        };
    };

    /**
     * 使用aidl 启动Service1
     */
    private StrongService startS1 = new StrongService.Stub() {

        @Override
        public void stopService() throws RemoteException {
            Intent i = new Intent(getBaseContext(), CoreServer.class);
            getBaseContext().stopService(i);
        }

        @Override
        public void startService() throws RemoteException {
            Intent i = new Intent(getBaseContext(), CoreServer.class);
            getBaseContext().startService(i);

        }
    };

    /**
     * 在内存紧张的时候，系统回收内存时，会回调OnTrimMemory， 重写onTrimMemory当系统清理内存时从新启动Service1
     */
    @Override
    public void onTrimMemory(int level) {
        startCoreServer();
    }

////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onCreate() {
        super.onCreate();
        initSP();
        // 注册广播接收类
        mServiceBroadcast = new ServiceBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(START_LOCATION);
        filter.addAction(START_SPEED_ALERT);
        filter.addAction(START_BEI_DOU);
        filter.addAction(HEART_BEAT);
        filter.addAction(ERROR_API);
        filter.addAction(Intent.ACTION_SCREEN_ON);
	    filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mServiceBroadcast, filter);
        objHandler.postDelayed(mTasks, 1000);
        Log.w(TAG, "核心服务：" + "onCreate()");

////////////////////////////////////////////////////////////////////////////////////////
        Toast.makeText(CoreServerAgin.this, "CoreServerAgin 启动中...", Toast.LENGTH_SHORT)
                .show();
        startCoreServer();
		/*
		 * 此线程用监听Service2的状态
		 */
        new Thread() {
            public void run() {
                while (true) {
                    boolean isRun = Utils.isServiceWork(CoreServerAgin.this,
                            "com.lzg.strongservice.service.Service1");
                    if (!isRun) {
                        Message msg = Message.obtain();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
        }.start();
    }

    /**
     * 判断Service1是否还在运行，如果不是则启动Service1
     */
    private void startCoreServer() {
        boolean isRun = Utils.isServiceWork(CoreServerAgin.this,
                "com.lzg.strongservice.service.Service1");
        if (isRun == false) {
            try {
                startS1.startService();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

////////////////////////////////////////////////////////////////////////////////////////
    private void initSP(){
        spf = getSharedPreferences(Config.SPF_MY,MODE_PRIVATE);
        editor = spf.edit();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        objHandler.removeCallbacks(mTasks);
        Intent service_again =new Intent(getApplicationContext(),CoreServerAgin.class);
		startService(service_again);
        unregisterReceiver(mServiceBroadcast);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w(TAG, "核心服务：" + "onStartCommand()");
//        return START_STICKY;START_STICKY_COMPATIBILITY
        return START_STICKY_COMPATIBILITY;
    }

    /**
     * @Description:发送广播启动定位服务
     * @param:
     * @return: void
     */
    private void startLocationService(){
        Intent location_service = new Intent(START_LOCATION);
        sendBroadcast(location_service);
    }


    /**
     * @Description:发送广播启动定位服务
     * @param:
     * @return: void
     */
    private void startSpeedAlertService(){
        Intent speed_alert_service = new Intent(START_SPEED_ALERT);
        sendBroadcast(speed_alert_service);
    }

    /**
     * @Description:发送广播启动北斗服务
     * @param:
     * @return: void
     */
    private void startBeiDouService(){
        Intent speed_alert_service = new Intent(START_BEI_DOU);
        sendBroadcast(speed_alert_service);
    }

    private void startUploadData(){
        Intent location_service = new Intent(START_UPLPAD);
        sendBroadcast(location_service);
    }


    boolean HeartBeat = false;
    class ServiceBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(START_LOCATION)){
                Log.w(TAG, "收到广播启动定位服务......" );
                Intent intent_service = new Intent(context,HttpService.class);
                context.startService(intent_service);
            }else if(intent.getAction().equals(START_SPEED_ALERT)){
                Intent intent_speed_enclosure = new Intent(context,SpeedEnclosureService.class);
                context.startService(intent_speed_enclosure);
            }else if(intent.getAction().equals(START_BEI_DOU)){
                Intent intent_beidou = new Intent(context,BeiDouService.class);
                context.startService(intent_beidou);
            }else if(intent.getAction().equals("MY_HEARTbeat")){
                HeartBeat = intent.getBooleanExtra("Heart",false);
                Log.w(TAG, "收到广播 是否在提交数据......" + HeartBeat );
            }
        }
    }


    private Runnable mTasks = new Runnable(){
        public void run(){
            Log.e(TAG, "核心服务心跳包！！！！！！！！--- "+ HeartBeat);
            if (!SystemTools.isWorked(CoreServerAgin.this, "com.wgc.cmwgc.service.HttpService")) {
                Log.e(TAG, "服务没运行" );
                startLocationService();
            }else{
                Log.w(TAG, "服务已经在运行" );
            }
            /*Websocket 上传数据版本*/
            if(!SystemTools.isWorked(CoreServerAgin.this, "com.wgc.cmwgc.service.SpeedEnclosureService")){
                startSpeedAlertService();
                Log.w(TAG, "服务没运行" );
            }else{
                Log.w(TAG, "服务已经在运行" );
            }
            /*JT808协议 上传数据版本*/
            if(!SystemTools.isWorked(CoreServerAgin.this, "com.wgc.cmwgc.service.BeiDouService")){
                startBeiDouService();
                Log.w(TAG, "BeiDouService服务没运行" );
            }else{
                Log.w(TAG, "BeiDouService服务已经在运行" );
            }
            numAgain ++;
            numIsRun ++;
            if(numAgain == 1){//20分钟再启动一次服务，（）
                startLocationService();
                startSpeedAlertService();
                startBeiDouService();
                numAgain = 0;
            }
            if(numIsRun == 3){
                if(!HeartBeat){
                    startUploadData();
                    HeartBeat = false;
                }
                numIsRun = 0;
            }
            objHandler.postDelayed(mTasks, ONE_MINUTES);
        }
    };

}
