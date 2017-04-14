package com.wgc.cmwgc.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.vilyever.socketclient.SocketClient;
import com.vilyever.socketclient.helper.SocketClientDelegate;
import com.vilyever.socketclient.helper.SocketClientReceivingDelegate;
import com.vilyever.socketclient.helper.SocketResponsePacket;
import com.wgc.cmwgc.JT808.JT808MSG;
import com.wgc.cmwgc.JT808.MsgDecoder;
import com.wgc.cmwgc.JT808.PackageData;
import com.wgc.cmwgc.JT808.TPMSConsts;
import com.wgc.cmwgc.JT808.util.HexStringUtils;
import com.wgc.cmwgc.JT808.util.JT808ProtocolUtils;
import com.wgc.cmwgc.app.Config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.channels.UnresolvedAddressException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * 功能： JT808 提交到客户后台
 * 作者： Administrator
 * 日期： 2017/3/28 14:33
 * 邮箱： descriable
 */
public class BeiDouService extends Service{

    private final String TAG = "BeiDouService";
    private int TIME = 1000 ;
    private Handler objHandler = new Handler();
    private SharedPreferences spf;
    private SharedPreferences.Editor editor;

    private boolean isFirst = true;
//    private String ip = "222.81.173.199";
//    private String port = "6973";

//    private String ip = "182.254.215.229";
//    private String port = "9988";


    private String ip = "";
    private String port = "";

    private int flowId ;
    private SocThread socketThread;
    private JT808MSG jt808MSG;
    private MsgDecoder decoder = new MsgDecoder();

    private int currentMsgId;
    private boolean isAuthentication;
    private boolean isRegister;
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

    private boolean isRunning;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        initBorcast();
        start();
    }


    private void init() {
        jt808MSG = new JT808MSG();
        spf = getSharedPreferences(Config.SPF_MY,MODE_PRIVATE);
        editor = spf.edit();
        did = spf.getString(Config.DID,"");

    }

    /**
     * 注册广播
     */
    private void initBorcast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.SPEED_ENCLOSURE);
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("my_bro_is_enable_jt");
        registerReceiver(receiver, filter);

    }


    /**
     * 广播接收器
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Config.SPEED_ENCLOSURE)){
                lon = Double.valueOf(intent.getStringExtra("lon"));
                lat = Double.valueOf(intent.getStringExtra("lat"));
                gpsTime = getCurrentTime();
                speedGPS = intent.getIntExtra("speed",0);
                direct = intent.getIntExtra("direct",0);
                mileage =  Double.valueOf(intent.getStringExtra("mileage"));
                status = intent.getStringExtra("status");
                gpsFlag = intent.getIntExtra("gpsFlag",1);
                gpsTime = gpsTime.replace("-","");
                sendLocation ();
            }else if(intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")){
                ConnectivityManager connectivityManager = (ConnectivityManager)
                        context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if(networkInfo != null && networkInfo.isAvailable()){
                    Log.e(TAG, "北斗----- 有网络服务  : ");
                    start();
                }else{
                    Log.e(TAG, "北斗----- 没有网络连接");
                    stop();
                    stopSocket();
                }
            }else if(intent.getAction().equals("my_bro_is_enable_jt")){// 开始用socket 连接客户服务器

                ip = intent.getStringExtra("ip");
                port = intent.getStringExtra("port");
                Log.e(TAG, "收到广播  : " + ip + " --  " + port);

                stopSocket();
                startSocket();
            }
        }
    };

    /**
     *
     * getCurrentTime 获取当前时间
     *
     * @return
     */
    @SuppressLint("SimpleDateFormat") public static String getCurrentTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd-HH-mm-ss");
        String str = sdf.format(date);
        return str;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        objHandler.removeCallbacks(mTasks);
        stopSocket();
    }

    /**
     * 循环每隔一秒 执行一次
     */
    private Runnable mTasks = new Runnable() {
        @Override
        public void run() {
            ip = spf.getString( Config.SP_SERVICE_IP, "");
            port = spf.getString( Config.SP_SERVICE_PORT, "");
            if(TextUtils.isEmpty(ip)||TextUtils.isEmpty(port)){
                objHandler.postDelayed(mTasks, TIME);
                return;
            }
            did = spf.getString(Config.DID,"");
            if(!did.equals("")){
                if(isFirst){
                    isFirst=false;
                    startSocket();
                }
                checkRegister();
                sendHeartBeat();
                checkHeartBeat();
            }
            objHandler.postDelayed(mTasks, TIME);
        }
    };

    /**
     * @param buffer 解析socket 服务返回数据
     */
    private void parseResponse( byte[] buffer){
        PackageData jt808Msg = null;
        try {
            jt808Msg = this.decoder.queueElement2PackageData(jt808MSG.getMsg(buffer,1,buffer.length-1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(TAG," --- -  返回信息 ！" + jt808Msg.getMsgHeader().getMsgId());
        if(jt808Msg.getMsgHeader().getMsgId() == 0x8100){
            flowId++;
            byte[] temp = jt808Msg.getMsgBodyBytes();
            Log.d(TAG,HexStringUtils.toHexString(temp) + " --- -  注册成功！" + HexStringUtils.toHexString(buffer) );
            if(temp.length>3) {
                /*取出 鉴权码*/
                byte[] msg = new byte[temp.length - 3];
                System.arraycopy(temp, 3, msg, 0, msg.length);
                isRegister= true;
                registerCount = 0;
                /* 告诉服务器鉴权码*/
                sendAuthenticationInfo(msg, flowId);
            }
        }else if(jt808Msg.getMsgHeader().getMsgId() == 0x8001){
            if (currentMsgId == 0x0102){
                byte[] reslutBytes =jt808Msg.getMsgBodyBytes();
                if(reslutBytes[4]==0){
                    Log.d(TAG," --- -  鉴权成功！");
                    isAuthentication = true;
                    flowId++;
                }
            }else if(currentMsgId == 0x0002){
                byte[] reslutBytes =jt808Msg.getMsgBodyBytes();
                if(reslutBytes[4]==0){
                    Log.d(TAG," --- -  心跳包成功返回　" + HexStringUtils.toHexString(jt808Msg.getMsgBodyBytes()));
                    flowId++;
                    heartBeatResponseCount = 0;
                    Log.d(TAG,"webSocket 链路检查情况 ：" + "OK" );
                    isRunning = true;
                    Intent intent = new Intent("MY_JT808_HEARTbeat");
                    intent.putExtra("jt_heart",isRunning);
                    sendBroadcast(intent);
                }
            }else if(currentMsgId == 0x0200){
                Log.e(TAG," --- -  提交定位成功返回　" + HexStringUtils.toHexString(jt808Msg.getMsgBodyBytes()));
                flowId++;
            }
        }
    }


/*------------------------------------------------------------------------------------------------------------------------------------*/

    /**
     * 开始
     */
    private void start(){
        objHandler.removeCallbacks(mTasks);
        objHandler.postDelayed(mTasks, 1000);
    }

    /**
     * 停止心跳
     */
    private void stop(){
        objHandler.removeCallbacks(mTasks);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




    /**
     * 发送地址
     */
    int locationCount;
    private void sendLocation () {
        if (lat == 0 || lon == 0) {
            return;
        }
        locationCount++;
        if (locationCount == 30) {
            locationCount = 0;
            if (isAuthentication) {
                currentMsgId = TPMSConsts.msg_id_terminal_location_info_upload;
                socketThread.sendBuffer(jt808MSG.getLocation(did,0, 0, (int) (lon * 1000000), (int) (lat * 1000000), 0, 0, 0, gpsTime, flowId));
                Log.w(TAG, HexStringUtils.toHexString(jt808MSG.getLocation(did,0, 0, (int) (lon * 1000000), (int) (lat * 1000000), 0, 0, 0, gpsTime, flowId)));
            }
        }
    }
    /**
     * 发送鉴权码
     * @param auto
     * @param flowId
     */
    private void sendAuthenticationInfo(byte[] auto,int flowId){
        currentMsgId = TPMSConsts.msg_id_terminal_authentication;
        Log.e(TAG," --- -  发送授权　");
        socketThread.sendBuffer(jt808MSG.getAuthenticationInfo(did,auto,flowId));
    }

    /**
     * 注册设备
     */
    private void sendRegisterDevice(){
        currentMsgId = TPMSConsts.msg_id_terminal_register;
        socketThread.sendBuffer(jt808MSG.getRegisterInfo(did,flowId));
    }

    /**
     * 发送心跳包
     */
    int heartBeatCount;
    private void sendHeartBeat(){
        heartBeatCount ++;
        if(heartBeatCount==10){
            heartBeatCount=0;
            if(isAuthentication){
                currentMsgId = TPMSConsts.msg_id_terminal_heart_beat;
                socketThread.sendBuffer(jt808MSG.getHeartBeat(did,flowId));
            }
        }
    }


    /**
     * 检查是否注册成功
     */
    int registerCount;
    private void checkRegister(){
        if (!isRegister){
            registerCount++;
            if(registerCount>15){
                registerCount=0;
                Log.e(TAG,"注册没回复 一直注册.......");
                sendRegisterDevice();
            }
        }
    }


    /**
     * 检查心跳
     */
    int heartBeatResponseCount;
    private void checkHeartBeat(){
        heartBeatResponseCount++;
        Log.e(TAG,"链路检查....... " + heartBeatResponseCount);
        if(heartBeatResponseCount>20){
            heartBeatResponseCount = 0;
            isRunning = false;
            Intent intent = new Intent("MY_Websocket_HEARTbeat");
            intent.putExtra("websocket_heart",isRunning);
            sendBroadcast(intent);
            stopSocket();
            startSocket();
        }
    }


/**----------------------------------- socket ------------------------------------------------------------------------------------------------------------------------------*/

    private void resetCount(){
        registerCount=0;
        isAuthentication = false;
        flowId=0;
        isRegister=false;
        heartBeatResponseCount = 0;
        isFirst = true;
    }


    public void startSocket() {
        if(socketThread ==null){
            socketThread = new SocThread(this);
            socketThread.start();
        }
    }

    private void stopSocket() {
        if(socketThread!=null){
            socketThread.isRun = false;
            socketThread.close();
            socketThread = null;
            resetCount();
        }
    }

    public class SocThread extends Thread {
        private int timeout = 10000;
        public Socket client = null;
        private OutputStream out;
        private InputStream in; // /* BufferedReader in; //不合适读取十六进制   DataInputStream dis;*/
        public boolean isRun = true;
        private Context mContext;

        public SocThread( Context context) {
            this.mContext = context;
            Log.i(TAG, "创建线程socket");
        }

        /**
         * 连接socket服务器
         */
        public void connect() {
            try {
                Log.i(TAG, "连接中……" +  ip +":" + port);
                client = new Socket(ip, Integer.valueOf(port).intValue());
                client.setSoTimeout(timeout);// 设置阻塞时间
                Log.i(TAG, "连接成功");
                /*in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                dis = new DataInputStream(client.getInputStream());*/
                in = client.getInputStream();
                out = client.getOutputStream();
                sendRegisterDevice();//注册设备
                Log.i(TAG, "输入输出流获取成功");
            } catch (UnknownHostException e) {
                Log.i(TAG, "连接错误UnknownHostException 重新获取");
                e.printStackTrace();
                connect();
            } catch (IOException e) {
                Log.i(TAG, "连接服务器io错误");
                e.printStackTrace();
            } catch (Exception e) {
                Log.i(TAG, "连接服务器错误Exception" + e.getMessage());
                e.printStackTrace();
            }
        }

        /**
         * 实时接受数据
         */
        @Override
        public void run() {
            connect();
            while (isRun) {
                try {
                    if (client.isConnected()) {
                        byte[] buffer = new byte[in.available()];
                        while(in.read(buffer)>0) {
                            parseResponse(buffer);
                        }
                    } else {
//                        Log.i(TAG, "没有可用连接");
                        connect();
                    }
                } catch (Exception e) {
//                    Log.i(TAG, "数据接收错误" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        /**
         * @param mess 发送字节数组
         */
        public void sendBuffer(byte[] mess) {
            if(socketThread==null)
                return;
            try {
                if (client != null) {
                    out.write(mess);
                    out.flush();
                    Log.i(TAG, "发送成功 -- ");
                } else {
                    Log.i(TAG, "连接不存在重新连接");
                    connect();
                }
            } catch (Exception e) {
                Logger.i(TAG, "send error");
                e.printStackTrace();
            } finally {
                Log.i(TAG, "发送完毕");
            }
        }

        /**
         * 关闭连接
         */
        public void close() {
            try {
                if (client != null) {
                    Log.i(TAG, "close in");
                    in.close();
                    Log.i(TAG, "close out");
                    out.close();
                    Log.i(TAG, "close client");
                    client.close();
                }
            } catch (Exception e) {
                Log.i(TAG, "close err");
                e.printStackTrace();
            }
        }
    }
}
