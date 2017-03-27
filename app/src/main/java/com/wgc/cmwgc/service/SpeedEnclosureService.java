package com.wgc.cmwgc.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.wgc.cmwgc.app.Config;
import com.wicare.wistorm.WiStormApi;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.UnresolvedAddressException;


/**
 * 功能： 超速报警和地理围栏检查服务
 * 作者： Administrator
 * 日期： 2017/3/14 09:55
 * 邮箱： descriable
 */
public class SpeedEnclosureService extends Service {

    private final String TAG = "SpeedEnclosureService";
    private int msgId =0;
    private int TIME = 1000 ;
    public static int SPEED_ALRET = 12290;
    private Handler objHandler = new Handler();
    private WebSocketClient mWebSocketClient;//WebSocketClient
    private final String address = "ws://m2m.chease.cn:39977";
//    private final String address = "ws://192.168.3.86:39966";
    private SharedPreferences spf;
    private SharedPreferences.Editor editor;
    private boolean isConnected;

    private String alertTime;
    private String gpsTime;
    private double lon;
    private double lat;
    private int speedGPS;//公里/小时
    private int direct;
    private double mileage;
    private int speedLimit=200;
    private int gpsFlag; //2: 精确定位   1：非精确定位
    private String status="[]";
    private String alerts="[]";
    private String did;

    private boolean isFirst = true;



    @Override
    public void onCreate() {
        super.onCreate();
        init();
        initBorcast();
//        try {
//            initSocketClient();
//            connect();
//        } catch (SocketTimeoutException |AssertionError|URISyntaxException | SecurityException | UnresolvedAddressException e) {
//            e.printStackTrace();
//        }
        start();
    }

    private void init() {
        spf = getSharedPreferences(Config.SPF_MY,MODE_PRIVATE);
        editor = spf.edit();
        speedLimit = spf.getInt(Config.SPF_SPEED,200);
        did = spf.getString(Config.DID,"");
        Logger.d(TAG," === DID  == " +  did);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        objHandler.removeCallbacks(mTasks);
        unregisterReceiver(receiver);
    }


    /**
     * 初始化WebSocketClient
     * @throws URISyntaxException
     */
    private void initSocketClient() throws URISyntaxException,AssertionError,SocketTimeoutException {
        if(mWebSocketClient == null) {
            mWebSocketClient = new WebSocketClient(new URI(address)) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {//连接成功
                    Log.d(TAG,"webSocket 连接成功" );
                    isConnected = true;
                    login();
                }

                @Override
                public void onMessage(String s) { //服务端消息
                    Log.d(TAG,"webSocket 服务器返回信息 ：" + s );
                    JSONObject object= null;
                    String type;
                    try {
                        object = new JSONObject(s);
                        type = object.getString("type");
                        if(type.equals(Config.LOGIN)){
                            parseLogin(object);
                        }else if(type.equals(Config.AT)){
                            parseAT(object);
                        }else if(type.equals(Config.ALERT)){
                            parseAlert(object);
                        }else if(type.equals(Config.COMMAND)){
//                            if (object.getString("cmdType").equals("33027")){//超速报警
                                parseSpeedLimit(object);
//                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onClose(int i, String s, boolean remote) {
                    //连接断开，remote判定是客户端断开还是服务端断开
                    isConnected= false;
                    Logger.e(TAG,"webSocket  onClose " );
                }

                @Override
                public void onError(Exception e) {
                    isConnected= false;
                    Logger.e(TAG,"webSocket 连接失败" );
                }
            };
        }
    }


    /**
     * @param object 解析登录信息
     */
    boolean isLogin;
    private void parseLogin(JSONObject object){
        try {
            String status = object.getString("status");
            if(status.equals("OK")){
                loginResponseCount = 0;//检查登录有回复
                Log.d(TAG,"webSocket 登录情况 ：" + "OK" );
                isLogin = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param object
     */
    private void parseAT(JSONObject object){
        try {
            String status = object.getString("status");
            if(status.equals("OK")){
                checkConnectResponseCount = 0;//检查链路有回复
                Log.d(TAG,"webSocket 链路检查情况 ：" + "OK" );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param object 超速报警提交后服务返回解析
     */
    private void parseAlert(JSONObject object){
        try {
            String status = object.getString("status");
            if(status.equals("OK")){
                spHandler.removeCallbacks(speedRunnable);
                postSpeedAlertCount=0;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param object 服务器下发的速度限制
     */
    private void parseSpeedLimit(JSONObject object){
        try {
            int cmdType = object.getInt("cmdType");
                String did = object.getString("did");
                if (Config.con_serial.equals(did)){
                    JSONObject jsonObject = new JSONObject(object.getString("params"));
                    speedLimit = jsonObject.getInt("param_value");
                    editor.putInt(Config.SPF_SPEED,speedLimit);
                    editor.commit();
                    sendCommand(msgId,cmdType,"OK");
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*--------------------------------------------------------------------------------------------------------------------------------------------*/

    /**
     *  //连接
     */
    private void connect() {
        new Thread(){
            @Override
            public void run() {
                if(mWebSocketClient!=null)
                    mWebSocketClient.connect();
            }
        }.start();
    }

    /**
     *  //断开连接
     */
    private void closeConnect() {
        try {
            if(mWebSocketClient!=null)
                mWebSocketClient.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            mWebSocketClient = null;
        }
        resetCount();
    }

    /**
     * //发送消息
     */
    private void sendMsg(String msg) {
        if(mWebSocketClient!=null){
            if(mWebSocketClient.getConnection().isOpen())
                mWebSocketClient.send(msg);
        }
    }

    /**
     * 注册广播
     */
    private void initBorcast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.SPEED_ENCLOSURE);
        registerReceiver(receiver, filter);
    }

    /**
     * 广播接收器
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            lon = Double.valueOf(intent.getStringExtra("lon"));
            lat = Double.valueOf(intent.getStringExtra("lat"));
            gpsTime = intent.getStringExtra("gpsTime");
            speedGPS = intent.getIntExtra("speed",0);
            direct = intent.getIntExtra("direct",0);
            mileage =  Double.valueOf(intent.getStringExtra("mileage"));
            status = intent.getStringExtra("status");
            gpsFlag = intent.getIntExtra("gpsFlag",1);
        }
    };

    /**
     * 开始
     */
    private void start(){
        objHandler.removeCallbacks(mTasks);
        objHandler.postDelayed(mTasks, 1000);
    }

    /**
     * 定时执行
     */
    private Runnable mTasks = new Runnable() {
        @Override
        public void run() {
//        Log.d(TAG,isFirst + "    -- 设备did -- ： " + did);
        if(!did.equals("")){
            if(isFirst){
                isFirst=false;
                try {
                    initSocketClient();
                    connect();
                } catch (SocketTimeoutException |AssertionError|URISyntaxException | SecurityException | UnresolvedAddressException e) {
                    e.printStackTrace();
                }
            }
            if (msgId==Integer.MAX_VALUE)
                msgId=0;
            checkLoginResponse();
            checkConnection();
            checkConnectResponse();
            checkSpeedAlert();
            sendLocation();
        }
        objHandler.postDelayed(mTasks, TIME);
        }
    };

/*----------------------------------------------------------------------------------------------------------------------------------------------*/
    int locationCount;
    private void sendLocation(){
        if(lat==0||lon==0){
            return;
        }
        locationCount++;
        if(locationCount==30){
            locationCount=0;
            if (isConnected){
                String msg = getLocationJSON(msgId,alerts).toString();
                Log.w(TAG, "------------ " + msg);
                sendMsg(msg);
                msgId++;
            }
        }
    }

/*------------------------以下是链路检查，链路检查没有回复 就重连三次，重发三次没回复 断开重连--------------------------------------------------------------------------------*/
    /**
     * 每隔十秒检查一次链路
     */
    int connectCount;
    private void checkConnection(){
        connectCount++;
        if(connectCount==10){
            connectCount=0;
            Log.w(TAG, isConnected + " -----  十秒检查链路 --------- " + msgId);
            String msg = getCheckJSON(msgId).toString();
            if (isConnected){
                sendMsg(msg);
                msgId++;
            }
        }
    }

    /**
     * 链路检查没有回复 就重连三次，
     */
    int checkConnectResponseCount;
    private void checkConnectResponse(){
        checkConnectResponseCount++;
        if (checkConnectResponseCount>15){
            checkConnectResponseCount=0;
            Log.e(TAG,"服务端没有回复，需断开重连");
            retryCheckConnectThreeTime();
        }
    }

    /**
     * 重发三次没回复 断开重连
     */
    int retryCheckConnectCount;
    private void retryCheckConnectThreeTime(){
        retryCheckConnectCount++;
        checkConnection();
        if (retryCheckConnectCount==3){//3次没回复就重新断开连接
            retryCheckConnectCount=0;
            Log.e(TAG,"-----------------------------断开重连---------------------------");
            closeConnect();
            try {
                initSocketClient();
                    connect();
            } catch (SocketTimeoutException|URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

/*------------------------以下是登录检查--登录没回复一直登陆------------------------------------------------------------------------------*/

    private void login(){
        String msg = getLoginJSON(msgId).toString();
        if (isConnected){
            sendMsg(msg);
            msgId++;
        }
    }

    int loginResponseCount;
    private void checkLoginResponse(){
        if(!isLogin){
            loginResponseCount++;
            if (loginResponseCount>15){
                loginResponseCount=0;
                Log.e(TAG,"登录没回复 一直登陆.......");
                login();
            }
        }
    }

/*----------------------------以下是发送速度报警--------------------------------------------------------------------------*/

    Handler spHandler  = new Handler();
    private void sendSpeedAlert(){
        String msg = getAlertJSON(msgId,SPEED_ALRET).toString();
        Log.d(TAG," ------- " + msg);
        if (isConnected){
            sendMsg(msg);
            msgId++;
        }
    }
    /**
     * 检查8秒之内速度是否超时
     */
    int speedCount;
    String bufferAlert="[]";
    private void checkSpeedAlert() {
        speedCount++;
        Log.i(TAG,speedGPS + "  -- 速度情况 -- ： " + speedLimit);
        if (speedCount<9){
            if(speedGPS<(speedLimit)){//小于80公里/H{}
                speedCount=0;
                alerts = "[]";
                bufferAlert = alerts;
            }
        }else{
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(12290);
            alerts=jsonArray.toString();
            speedCount=0;
            Log.e(TAG,bufferAlert + "  --超速了-- ： " + alerts);
            if(!bufferAlert.equals(alerts)){
                spHandler.postDelayed(speedRunnable,3000);
            }
            bufferAlert = alerts;
        }
    }

    int postSpeedAlertCount;
    private Runnable speedRunnable = new Runnable() {
        @Override
        public void run() {
            postSpeedAlertCount++;
            if(postSpeedAlertCount<3){
                sendSpeedAlert();
                spHandler.postDelayed(speedRunnable,3000);
            }else {
                spHandler.removeCallbacks(speedRunnable);
                postSpeedAlertCount=0;
            }
        }
    };

/*------------------------------------------------------------------------------------------------------*/
    private void resetCount(){
        retryCheckConnectCount=0;
        checkConnectResponseCount=0;
        loginResponseCount=0;
        connectCount=0;
        msgId=0;
        isLogin = false;
        locationCount=0;
        isConnected=false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

/****************************************************************************************************/
    /**
     * @param msgId
     * @return
     */
    private Object getLoginJSON(int msgId){
        JSONObject jObject=new JSONObject();
        try {
            jObject.put("msgId", msgId);
            jObject.put("type", "LOGIN");
            jObject.put("did", did);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jObject;
    }
    /**
     * @param msgId
     * @return
     */
    private Object getCheckJSON(int msgId){
        JSONObject jObject=new JSONObject();
        try {
            jObject.put("msgId", msgId);
            jObject.put("type", "AT");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jObject;
    }

    /**
     * @param msgId
     * @param alertType
     * @return
     */
    private Object getAlertJSON(int msgId,int alertType){
        alertTime = WiStormApi.getCurrentTime();
        JSONObject jObject=new JSONObject();
        try {
            jObject.put("msgId", msgId);
            jObject.put("type", "ALERT");
            jObject.put("alertType", alertType);
            jObject.put("alertTime", alertTime);
            jObject.put("gpsTime", gpsTime);
            jObject.put("lon", lon);
            jObject.put("lat", lat);
            jObject.put("speed",speedGPS);
            jObject.put("direct", direct);
            jObject.put("mileage", mileage);
            jObject.put("speedLimit",speedLimit);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jObject;
    }

    /**
     * 服务器发送指令下来，需要回复一个指令给服务器
     * @param msgId
     * @param commandType
     * @param isOk 成功回复OK  失败回复FAIL
     * @return
     */
    private Object getCommandJSON(int msgId,int commandType,String isOk){
        JSONObject jObject=new JSONObject();
        try {
            jObject.put("msgId", msgId);
            jObject.put("type", "COMMAND");
            jObject.put("did", did);
            jObject.put("cmdType", commandType);
            jObject.put("status", isOk);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jObject;
    }


    /**
     * @param msgId
     * @param alerts
     * @return
     */
    private Object getLocationJSON(int msgId,String alerts){
        JSONObject jObject=new JSONObject();
        try {
            jObject.put("msgId", msgId);
            jObject.put("type", "GPS");
            jObject.put("lon", lon);
            jObject.put("lat", lat);
            jObject.put("gpsFlag", gpsFlag);
            jObject.put("speed",speedGPS);
            jObject.put("direct", direct);
            jObject.put("mileage", mileage);
            jObject.put("alerts", alerts);
            jObject.put("status",status);
            jObject.put("gpsTime", gpsTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jObject;
    }



    /**
     * @param msgId
     * @param commandType
     * @param isOk
     */
    private void sendCommand(int msgId,int commandType,String isOk){
        String msg = getCommandJSON(msgId,commandType,isOk).toString();
        if (isConnected){
            sendMsg(msg);
            msgId++;
        }
    }





/*    *//*-----------------------------------检查围栏--------------------------------------------------------------------------------------------*//*
    Geofences geofences;
    private void checkGeofences(){
        if(isGeofencesSet){
            if(!spf_geofences.equals("")){
                for(int i=0;i<geofences.getGeofences().size();i++){
                    boolean in = SystemTools.isPtInPoly(lon,lat,geofences.getGeofences().get(i).getPoints());
                    if(!isIn[i]&& in){
                        //进入围栏报警
                        sendGeofencesAlert(GEOFENCES_IN);
//                        geoHandler.postDelayed(speedRunnable,3000);
                    }
                    if(isIn[i] && !in){
                        //出围栏报警
                        sendGeofencesAlert(GEOFENCES_OUT);
//                        geoHandler.postDelayed(speedRunnable,3000);
                    }
                }
            }
        }
    }

    boolean [] isIn;
    boolean isGeofencesSet;
    private void checkGeofencesFirst(String strGenfences){
        if(!strGenfences.equals("")){
            geofences = gson.fromJson(strGenfences,Geofences.class);
            isIn = new boolean[geofences.getGeofences().size()];
            for(int i=0;i<geofences.getGeofences().size();i++){
                boolean in = SystemTools.isPtInPoly(lon,lat,geofences.getGeofences().get(i).getPoints());
                isIn[i]=in;
            }
            isGeofencesSet=true;
        }else {
            isGeofencesSet=false;
        }
    }*/


//    /**
//     * 服务器下发的围栏数据
//     * @param object
//     */
//    Gson gson;
//    private void parseGeofences(JSONObject object){
//        try {
//            String status = object.getString("status");
//            int cmdType = object.getInt("cmdType");
//            if(status.equals("OK")){
//                String did = object.getString("did");
//                if (Config.con_serial.equals(did)){
//                    spf_geofences = object.getString("params");
//                    editor.putString(Config.SPF_GEOFENCES,spf_geofences);
//                    editor.commit();
//                    sendCommand(msgId,cmdType,"OK");
//                    checkGeofencesFirst(spf_geofences);
//                }
//            }else{
//                sendCommand(msgId,cmdType,"FAIL");
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }


/*----------------------------以下是发送围栏报警--------------------------------------------------------------------------*/

//    Handler geoHandler  = new Handler();
//    /**
//     * @param type
//     */
//    private void sendGeofencesAlert(int type){
//        String msg = getAlertJSON(msgId,type).toString();
//        if (isConnected){
//            sendMsg(msg);
//            msgId++;
//        }
//    }
//
//    int postGeofencesAlertCount;
//    private Runnable geofencesRunnable = new Runnable() {
//        @Override
//        public void run() {
//            postGeofencesAlertCount++;
//            if(postGeofencesAlertCount<3){
////                sendSpeedAlert();
//                geoHandler.postDelayed(geofencesRunnable,3000);
//            }else {
//                geoHandler.removeCallbacks(geofencesRunnable);
//                postGeofencesAlertCount=0;
//            }
//        }
//    };


}
