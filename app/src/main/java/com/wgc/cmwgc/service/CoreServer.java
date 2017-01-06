package com.wgc.cmwgc.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.wgc.cmwgc.Until.SystemTools;

/**
 * Created by Administrator on 2016/12/19.
 */
public class CoreServer extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final String START_LOCATION = "start_my_location_service";
    private final String HEART_BEAT = "MY_HEARTbeat";
    public static final String START_UPLPAD ="start_upload_data_incase_time_incorrect";
    public static final String ERROR_API = "api_error_happen";

    private int ONE_MINUTES = 1000 * 60;// five
    private ServiceBroadcast mServiceBroadcast;
    private Handler objHandler = new Handler();
    private  int numIsRun = 0;
    private  int numAgain = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        // 注册广播接收类
        mServiceBroadcast = new ServiceBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(START_LOCATION);
        filter.addAction(HEART_BEAT);
        filter.addAction(ERROR_API);
        filter.addAction(Intent.ACTION_SCREEN_ON);
	    filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mServiceBroadcast, filter);
        objHandler.postDelayed(mTasks, 1000);
        Log.w("LocationService", "核心服务：" + "onCreate()");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        objHandler.removeCallbacks(mTasks);
        unregisterReceiver(mServiceBroadcast);
        Intent service_again =new Intent(getApplicationContext(),CoreServer.class);
		startService(service_again);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w("LocationService", "核心服务：" + "onStartCommand()");
        return START_STICKY;
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

    private void startUploadData(){
        Intent location_service = new Intent(START_UPLPAD);
        sendBroadcast(location_service);
    }


    boolean HeartBeat = false;
    class ServiceBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(START_LOCATION)){
                Log.w("LocationService", "收到广播启动定位服务......" );
                Intent intent_service = new Intent(context,HttpService.class);
                context.startService(intent_service);
            }else if(intent.getAction().equals("MY_HEARTbeat")){
                HeartBeat = intent.getBooleanExtra("Heart",false);
                Log.w("LocationService", "收到广播 是否在提交数据......" + HeartBeat );
            }
        }
    }


    private Runnable mTasks = new Runnable(){
        public void run(){
            Log.w("LocationService", "核心服务心跳包！！！！！！！！--- "+ HeartBeat);
            if (!SystemTools.isWorked(CoreServer.this, "com.wgc.cmwgc.service.HttpService")) {
                Log.w("LocationService", "服务没运行" );
                startLocationService();
            }else{
                Log.w("LocationService", "服务已经在运行" );
            }
            numAgain ++;
            numIsRun ++;
            if(numAgain == 20){//20分钟再启动一次服务，（）
                numAgain = 0;
                startLocationService();
            }
            if(numIsRun == 3){
                numIsRun = 0;
                if(!HeartBeat){
                    HeartBeat = false;
                    startUploadData();
                }
            }
            objHandler.postDelayed(mTasks, ONE_MINUTES);
        }
    };

}
