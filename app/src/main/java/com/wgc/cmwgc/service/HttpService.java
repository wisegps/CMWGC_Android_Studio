package com.wgc.cmwgc.service;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.volley.VolleyError;
import com.orhanobut.logger.Logger;
import com.wgc.cmwgc.activity.LeadMainActivity;
import com.wgc.cmwgc.app.Config;
import com.wgc.cmwgc.Until.SystemTools;
import com.wgc.cmwgc.app.MyApplication;
import com.wgc.cmwgc.db.DBManager;
import com.wgc.cmwgc.db.DeviceDataEntity;
import com.wgc.cmwgc.event.CheckEvent;
import com.wicare.wistorm.WEncrypt;
import com.wicare.wistorm.WiStormApi;
import com.wicare.wistorm.api.WDeviceApi;
import com.wicare.wistorm.api.WGpsDataApi;
import com.wicare.wistorm.api.WUserApi;
import com.wicare.wistorm.http.BaseVolley;
import com.wicare.wistorm.http.OnFailure;
import com.wicare.wistorm.http.OnSuccess;
import com.wicare.wistorm.versionupdate.VersionUpdate;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

/**
 * 2016-09-12
 */
public class HttpService extends Service {
	
	private static final String TAG = "HttpService";
	private int ONE_SECOND = 1000  ;// 1s
    private Boolean isFirst = true;
    private double latt  = 0 ;
    private double lonn  = 0 ;
    private int speedGps = 0;
    private int gpsType  = 2;
    private int singnal  = 0;
	private double mileage = 0;
	private float bearing = 0;
	private String status = "[]";

    private WDeviceApi deviceApi;
	private WGpsDataApi gpsDataApi;
	private Handler objHandler = new Handler();
	private TelephonyManager Tel;  
	private MyPhoneStateListener myListener;
	private LocationManager locationManager = null;
	private LocationListner gpsListner = null;
	private SharedPreferences spf;
	private SharedPreferences.Editor editor;

	private boolean isNetwork = false;
	private boolean isRegister = false;
	private boolean isHadOfflineData = false;
	private boolean isRunning = false;
	private DBManager dbManager;

	@Override
	public void onCreate() {
		super.onCreate();
		initSpf();
		initWistorm();
		initDevice();
		initBorcast();
		Log.d(TAG, "onCreate:  服务。。。。" + Config.con_serial);
		checkIsCreate();
	}

	private void initSpf(){
		dbManager = DBManager.getInstance(this);//获取数据库实例
		spf = getSharedPreferences(Config.SPF_MY, Activity.MODE_PRIVATE);
		editor = spf.edit();
		mileage = Double.valueOf(spf.getString(Config.TOTAL_MILEAGE,"0"));
		if (mileage==-1){
			mileage = 0.0;
		}
		isRegister = spf.getBoolean(Config.IS_REGISTER,false);
		latt = Double.valueOf(spf.getString(Config.LAST_LAT,"0.0"));
		lonn = Double.valueOf(spf.getString(Config.LAST_LON,"0.0"));
		Logger.d("取出最后保存的 经度： " + lonn + " 纬度 ：" + latt  +  " 里程 ：" + mileage);
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Tel.listen(myListener ,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		Logger.w("==== 服务 === " + "onStartCommand");
		return START_STICKY;
	}

	/**
	 * @author Wu
	 *         <p>
	 *         位置变化监听、
	 */
	private class LocationListner implements LocationListener {

		public void onLocationChanged(Location location) {
			if(isFirst){
				Log.e(TAG,"是否第一次 ：" +isFirst);
				isFirst = false;
				latt = location.getLatitude();
				lonn = location.getLongitude();
				Config.gps_time = WiStormApi.getCurrentTime();
				bearing = location .getBearing();
				if (location.getProvider().equals("gps")) {
					gpsType = 2;
				}else{
					gpsType = 1;
				}
			}else{
				double dis = SystemTools.getDistance(latt,lonn,location.getLatitude(),location.getLongitude());
				latt = location.getLatitude();
				lonn = location.getLongitude();
				Config.gps_time = WiStormApi.getCurrentTime();
				bearing = location .getBearing();
				if (location.getProvider().equals("gps")) {
					gpsType = 2;
				}else{
					gpsType = 1;
				}
				distanceCaculate(dis);
				Log.e(TAG,  " 角度 ："   + location .getBearing() +  " 定位成功----:"  + " 里程 ：" + dis + " 速度 ：" + speedGps  + " type : " + gpsType  +  "   lon：" + location.getLongitude() + "   lat：" + location.getLatitude());

				if(!isNetwork){//没有网络的时候定位到的数据保存起来 等到有网络就上传
					if (time_uptate_data==30){
						time_uptate_data = 0;
						DeviceDataEntity dataEntity = new DeviceDataEntity();
						dataEntity.setCreatedAt(WiStormApi.getCurrentTime());
						dataEntity.setDirect(bearing);
						dataEntity.setFuel(-1);
						dataEntity.setGpsFlag(gpsType);
						dataEntity.setGpsTime(Config.gps_time);
						dataEntity.setLat(latt);
						dataEntity.setLon(lonn);
						dataEntity.setMileage(mileage);
						dataEntity.setRcvTime(WiStormApi.getCurrentTime());
						dataEntity.setSignal(singnal);
						dataEntity.setSpeed(speedGps);
						dataEntity.setStatus(status);
						dbManager.insertDeviceData(dataEntity);
						isHadOfflineData = true;
						Log.d(TAG,  " 保存定位数据 .......... ");
					}
				}
			}
			sendCheckEvent();
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			Logger.w("定位状态onStatusChanged ：" + status + " == " + provider);
		}
		public void onProviderEnabled(String provider) {
			Logger.w("定位状态 onProviderEnabled：" +" == " + provider);
			checkIsCreate();
			go();
			//acc点火之后会执行
		}
		public void onProviderDisabled(String provider) {
			Logger.w("定位状态onProviderDisabled ：" + " == " + provider);
			objHandler.removeCallbacks(mTasks);//休眠的时候会执行
		}
	};

	/**
	 * 里程计算
	 * @param d
	 */
	int lastSpeed = 0;
	double lastD = 0d;
	private void distanceCaculate(double d){
		speedGps =(int) Math.round(d*3600);//公里/小时
		if(speedGps==0){
			status = "[]";
		}else{
			status = "[8196]";
		}
		if(Math.abs(d-lastD)>0.05){
			mileage = mileage + (Math.abs(d-lastD)/2) ;//里程累计
		}else{
			mileage = mileage + d ;//里程累计
			lastD = d;
		}
		editor.putString(Config.TOTAL_MILEAGE,String.valueOf(mileage));
		editor.putString(Config.LAST_LON,String.valueOf(lonn));
		editor.putString(Config.LAST_LAT,String.valueOf(latt));
		editor.commit();
		if(Math.abs(speedGps-lastSpeed)>120){
			if(Math.abs(speedGps-lastSpeed)>200){
				speedGps = lastSpeed;
			}else{
				speedGps = (lastSpeed +speedGps)/2 ;
			}
		}
		lastSpeed = speedGps;
	}

	/**
	 * Wistorm
	 */
	private void initWistorm(){
		BaseVolley.init(this);
		deviceApi = new WDeviceApi(this);
		gpsDataApi = new WGpsDataApi(this);
	}

	/**
	 * 注册广播
	 */
	private void initBorcast(){
		IntentFilter filter = new IntentFilter();
	    filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		filter.addAction(CoreServer.START_UPLPAD);
	    registerReceiver(receiver, filter); 
	}
	
	/**
	 * 初始化设备
	 */
	private void initDevice(){
		/* Update the listener, and start it */  
        myListener = new MyPhoneStateListener();  
        Tel = ( TelephonyManager )getSystemService(Context.TELEPHONY_SERVICE);  
        Tel.listen(myListener ,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
      	Config.con_serial = Tel.getDeviceId();
		Config.con_iccid = Tel.getSimSerialNumber();
      	Log.e(TAG, "IMEA :" + Config.con_serial);
	}

	/**
	 * 检查设备是否注册过
	 */
	private void checkIsCreate(){
		Logger.d("==== 是否注册=== " + isRegister);
		if(isRegister){
			go();
		}else{
			isCreate();
		}
	}
	
	/**
	 * 判断设备是否注册 没有注册自动注册设备
	 */
	private void isCreate(){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("access_token", Config.ACCESS_TOKEN);
		params.put("did", Config.con_serial);
		String fields = "did,activeGpsData";
		deviceApi.get(params, fields, new OnSuccess() {
			
			@Override
			protected void onSuccess(String response) {
				// TODO Auto-generated method stub
				Log.d(TAG, "服务获取设备返回信息 ：  " + response);
				try {
					JSONObject jsonObject = new JSONObject(response);
					if(jsonObject.has("data")){
						JSONObject object1 = new JSONObject(jsonObject.getString("data"));
						getMileage(object1);
					}
					if("0".equals(jsonObject.getString("status_code"))){
						if(jsonObject.isNull("data")){
							Log.e(TAG, "设备没有注册请进行注册");
							createDevice();
						}else{
							Log.e(TAG, "该设备已经注册，开始定位");
							editor.putBoolean(Config.IS_REGISTER,true);
							editor.commit();
							isRegister = true;
							go();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}	
			}
		}, new OnFailure() {
			
			@Override
			protected void onFailure(VolleyError error) {
				// TODO Auto-generated method stub
			}
		});
	}

	/**
	 * 开始定位
	 */
	private void go(){
		objHandler.removeCallbacks(mTasks);
		objHandler.postDelayed(mTasks, 1000);
	}

	private void getMileage(JSONObject jsonObject){
		if(jsonObject.has("activeGpsData")){
			try{
				JSONObject object = new JSONObject(jsonObject.getString("activeGpsData").toString());
				if (object.has("mileage")){
					Logger.d(TAG,"服务-》里程 ：" + object.getString("mileage"));
					editor.putString(Config.TOTAL_MILEAGE,object.getString("mileage"));
					editor.commit();
					mileage = Double.valueOf(object.getString("mileage"));
					if (mileage==-1){
						mileage = 0.0;
					}
				}else {
					editor.putInt(Config.TOTAL_MILEAGE,0);
					editor.commit();
				}
			}catch (JSONException e){
			}
		}
	}

	private void createDevice(){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("access_token", Config.ACCESS_TOKEN);
		params.put("did", Config.con_serial);
		params.put("uid", "0");
		deviceApi.create(params, new OnSuccess() {

			@Override
			protected void onSuccess(String response) {
				// TODO Auto-generated method stub
				Log.d("TEST_WISTORM", response);
				try {
					JSONObject jsonObject = new JSONObject(response);
					if ("0".equals(jsonObject.getString("status_code"))) {
						editor.putBoolean(Config.IS_REGISTER, true);
						editor.commit();
						isRegister = true;
						go();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new OnFailure() {
			@Override
			protected void onFailure(VolleyError error) {
			}
		});
	}

	/**
	 * 广播接收器
	 */	
	private final BroadcastReceiver receiver = new BroadcastReceiver(){  
		  
	    @Override  
	    public void onReceive(final Context context, final Intent intent) {
			if(intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")){
				ConnectivityManager connectivityManager = (ConnectivityManager)
						context.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
				if(networkInfo != null && networkInfo.isAvailable()){
					Log.e(TAG, "有网络服务  : ");
					isNetwork = true;
					checkIsCreate();
					if(isHadOfflineData){
						uploadOfflineData();
					}
				}else{
					isNetwork = false;
					Log.e(TAG, "没有网络连接");
				}
			}else if(intent.getAction().equals(CoreServer.START_UPLPAD)){
				checkIsCreate();
			}
	    }  
	};  

	/**
	 * 是否检查更新
	 */
	boolean isUpdate = true;
 	private void isUpdate() {
		time_uptate_apk = 0;
		if (SystemTools.isSdCardExist()) {
			VersionUpdate updata = new VersionUpdate(this);
			updata.checkInBackService(Config.UPDATA_APK_URL, new VersionUpdate.UpdateListener() {
				@Override
				public void hasNewVersion(boolean isHad, String updateMsg, String apkUrl) {
					if(isHad){
						if (isUpdate){
							isUpdate = false;
							startIntent();
						}
					}
				}
				@Override
				public void finishDownloadApk(String saveFileName) {
				}
			});
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		objHandler.removeCallbacks(mTasks);
		unregisterReceiver(receiver);
		removeLocationListener();
		Tel.listen(myListener, PhoneStateListener.LISTEN_NONE);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	/**
	 * 启动界面
	 */
	private void startIntent(){
		Intent mIntent = new Intent();
		mIntent.setClass(getApplicationContext(), LeadMainActivity.class);
		mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(mIntent);
	}
	
	/**
	 * 监听手机信号
     * @author wu
     */
    private class MyPhoneStateListener extends PhoneStateListener {
    	@Override
    	public void onSignalStrengthsChanged(SignalStrength signalStrength) {
    		super.onSignalStrengthsChanged(signalStrength);  
    		singnal =  signalStrength.getGsmSignalStrength();
    	}  
    };

	/**
	 * @return json activeGpsData
	 */
	private Object getDeviceParams(){
		JSONObject jObject=new JSONObject();
		try {
			jObject.put("version", "v"+ SystemTools.getVersion(this));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jObject;
	}

	int time_uptate_apk  = 0;
	int time_uptate_data = 0;
	/**
	 * 定时提交数据
	 */
	private Runnable mTasks = new Runnable(){
		public void run(){
			isRunning = true;
			time_uptate_apk ++;
			time_uptate_data ++;
				startLocation();
			if(time_uptate_apk%5==0){
				Intent intent = new Intent("MY_HEARTbeat");
				intent.putExtra("Heart",isRunning);
				sendBroadcast(intent);
			}
			if(time_uptate_apk == 7200){
				time_uptate_apk = 0;
				if(isNetwork){
					isUpdate();//一个小时检查更新一次
				}
			}
			if(time_uptate_data == 30){
				if(latt!=0 || lonn!=0){
					if(isNetwork){
//						uploadLocation();
						time_uptate_data = 0;
					}
				}
			}else if(time_uptate_data>30){
				time_uptate_data = 0;//				定位没信号并且没有网络的时候
			}
			objHandler.postDelayed(mTasks, ONE_SECOND);
		}
	};


	/**
	 * 发送数据检查超速和围栏报警
	 */
	private void sendCheckEvent(){
		Intent intent = new Intent(Config.SPEED_ENCLOSURE);
		intent.putExtra("lat",String.valueOf(latt));
		intent.putExtra("lon",String.valueOf(lonn));
		intent.putExtra("speed",speedGps);
		intent.putExtra("network",isNetwork);
		intent.putExtra("gpsTime",WiStormApi.getCurrentTime());
		intent.putExtra("direct", (int)bearing);
		intent.putExtra("mileage",String.valueOf(mileage));
		intent.putExtra("gpsFlag",gpsType);
		intent.putExtra("status",status);
		sendBroadcast(intent);
	}


	private void startLocation(){
		if(locationManager == null){
			locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		}
		if(gpsListner == null){
			gpsListner = new LocationListner();
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if(getApplication().checkSelfPermission(Manifest.permission.CALL_PHONE)==PackageManager.PERMISSION_GRANTED) {
			}
		}else{
			locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,1000 * 30, 0, gpsListner);
		}
	}

	private void removeLocationListener(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if(getApplication().checkSelfPermission(Manifest.permission.CALL_PHONE)==PackageManager.PERMISSION_GRANTED) {
			}
		}else{
			if (gpsListner != null) {
				locationManager.removeUpdates(gpsListner);
			}
			locationManager = null;
		}
	}

/**---------提交离线定位数据---------------------------------------------------------------------------------------------*/

	int currentUploadIndex = 0;
	List<DeviceDataEntity> entityList;
	private void  uploadOfflineData(){
		entityList = dbManager.queryDeviceDataList();
		if(entityList!=null){
			if (entityList.size()>0 && currentUploadIndex <entityList.size()){
				Log.e(TAG, "离线数据 ：" + entityList.size() + "当前提交 : " + currentUploadIndex);
				uploadOfflineLocation(entityList.get(currentUploadIndex));
			}
		}
	}

	/**
	 * @param entity
	 */
	private void uploadOfflineLocation(final DeviceDataEntity entity){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("access_token", Config.ACCESS_TOKEN);
		params.put("did", Config.con_serial);
		params.put("lat", String.valueOf(entity.getLat()));
		params.put("lon", String.valueOf(entity.getLon()));
		params.put("gpsFlag", String.valueOf(entity.getGpsFlag()));
		params.put("speed", String.valueOf(entity.getSpeed()));
		params.put("direct", String.valueOf(entity.getDirect()));
		params.put("signal", String.valueOf(entity.getSignal()));
		params.put("createdAt", entity.getCreatedAt());
		params.put("gpsTime", entity.getGpsTime());
		params.put("rcvTime", entity.getRcvTime());//�
		params.put("mileage", String.valueOf(entity.getMileage()));//�
		params.put("fuel", "-1");//
		params.put("status",entity.getStatus());
		Log.d(TAG, Config.con_serial +  " 定位方式： " + entity.getGpsFlag() + "  信号强度: " + entity.getSignal());
		gpsDataApi.gpsCreate(params, new OnSuccess() {

			@Override
			protected void onSuccess(String response) {
				// TODO Auto-generated method stub
				Log.e(TAG, response);
				try {
					JSONObject jsonObject = new JSONObject(response);
					if("0".equals(jsonObject.getString("status_code"))){
						updataOfflineDevice(entity);//
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new OnFailure() {

			@Override
			protected void onFailure(VolleyError error) {}
		});
	}


	/**
	 * 更新设备信息
	 */
	private void updataOfflineDevice(DeviceDataEntity entity){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("access_token", Config.ACCESS_TOKEN);
		params.put("_did", Config.con_serial);
		params.put("activeGpsData", getOfflineActiveGpsData(entity).toString());
		params.put("params",getDeviceParams().toString());
		deviceApi.updata(params, new OnSuccess() {

			@Override
			protected void onSuccess(String response) {
				// TODO Auto-generated method stub
				Log.w(TAG, "更新设备离线数据返回信息 : " + response);
				currentUploadIndex++;
				if (currentUploadIndex >entityList.size()-1){
					isHadOfflineData = false;
					currentUploadIndex = 0;
					dbManager.deleteDeviceDataAll();
					Log.w(TAG, "离线数据提交完成..............................");
				}else{
					Log.e(TAG, "离线数据 ：" + entityList.size() + "当前提交 : " + currentUploadIndex);
					uploadOfflineLocation(entityList.get(currentUploadIndex));
				}
			}
		}, new OnFailure() {

			@Override
			protected void onFailure(VolleyError error) {
				// TODO Auto-generated method stub
				Log.e(TAG, "更新设备返回信息: " + error.toString());
			}
		});
	}


	/**
	 * @return json activeGpsData
	 */
	private Object getOfflineActiveGpsData(DeviceDataEntity entity){
		JSONObject jObject=new JSONObject();
		try {
			jObject.put("lon", entity.getLon());
			jObject.put("lat", entity.getLat());
			jObject.put("gpsTime", entity.getGpsTime());
			jObject.put("did", Config.con_serial);
			jObject.put("gpsFlag", entity.getGpsFlag());
			jObject.put("speed", entity.getSpeed());
			jObject.put("direct", entity.getDirect());
			jObject.put("signal", entity.getSignal());
			jObject.put("rcvTime", entity.getRcvTime());//�
			jObject.put("mileage", entity.getMileage());//�
			jObject.put("fuel", "-1");//
			jObject.put("status", entity.getStatus());//
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jObject;
	}

//    /**
//	 * 更新位置信息
//	 */
//	String revTime = "";
//	private void uploadLocation(){
//		revTime = WiStormApi.getCurrentTime();
//		HashMap<String, String> params = new HashMap<String, String>();
//		params.put("access_token", Config.ACCESS_TOKEN);
//		params.put("did", Config.con_serial);
//		params.put("lat", String.valueOf(latt));
//		params.put("lon", String.valueOf(lonn));
//		params.put("gpsFlag", String.valueOf(gpsType));
//		params.put("speed", String.valueOf(speedGps));
//		params.put("direct", String.valueOf(bearing));
//		params.put("signal", String.valueOf(singnal));
//		params.put("createdAt", revTime);
//		params.put("gpsTime", Config.gps_time);
//		params.put("rcvTime", revTime);//�
//		params.put("mileage", String.valueOf(mileage));//�
//		params.put("fuel", "-1");//
//		params.put("status",status);
//		Log.d(TAG, Config.con_serial +  " 定位时间： " + Config.gps_time + "  : " + WiStormApi.getCurrentTime());
//		gpsDataApi.gpsCreate(params, new OnSuccess() {
//
//			@Override
//			protected void onSuccess(String response) {
//				// TODO Auto-generated method stub
//				Log.e(TAG, response);
//				try {
//					JSONObject jsonObject = new JSONObject(response);
//					if("0".equals(jsonObject.getString("status_code"))){
//						updataDevice();//
//					}
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
//		}, new OnFailure() {
//
//			@Override
//			protected void onFailure(VolleyError error) {}
//		});
//	}

//	/**
//	 * 更新设备信息
//	 */
//	private void updataDevice(){
//		HashMap<String, String> params = new HashMap<String, String>();
//		params.put("access_token", Config.ACCESS_TOKEN);
////		params.put("uid", Config.USER_ID);//不能更新UID 不然别人绑定会出问题
//		params.put("_did", Config.con_serial);
//		params.put("activeGpsData", getActiveGpsData().toString());
//		params.put("params",getDeviceParams().toString());
//		deviceApi.updata(params, new OnSuccess() {
//
//			@Override
//			protected void onSuccess(String response) {
//				// TODO Auto-generated method stub
//				Log.i(TAG, "更新设备返回信息 : " + response);
//			}
//		}, new OnFailure() {
//
//			@Override
//			protected void onFailure(VolleyError error) {
//				// TODO Auto-generated method stub
//				Log.e(TAG, "更新设备返回信息: " + error.toString());
//			}
//		});
//	}

//	/**
//	 * @return json activeGpsData
//	 */
//	private Object getActiveGpsData(){
//    	JSONObject jObject=new JSONObject();
//        try {
//        	jObject.put("lon", lonn);
//        	jObject.put("lat", latt);
//        	jObject.put("gpsTime", WiStormApi.getCurrentTime());
//        	jObject.put("did", Config.con_serial);
//        	jObject.put("gpsFlag", gpsType);
//        	jObject.put("speed", speedGps);
//        	jObject.put("direct", bearing);
//        	jObject.put("signal", singnal);
//			jObject.put("rcvTime", revTime);//�
//			jObject.put("mileage", mileage);//�
//			jObject.put("fuel", "-1");//
//			jObject.put("status", status);//
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//    	return jObject;
//    }
}
