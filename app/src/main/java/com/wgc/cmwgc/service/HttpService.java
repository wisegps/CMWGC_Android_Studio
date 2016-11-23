package com.wgc.cmwgc.service;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.wicare.wistorm.WEncrypt;
import com.wicare.wistorm.WiStormApi;
import com.wicare.wistorm.api.WDeviceApi;
import com.wicare.wistorm.api.WGpsDataApi;
import com.wicare.wistorm.api.WUserApi;
import com.wicare.wistorm.http.BaseVolley;
import com.wicare.wistorm.http.OnFailure;
import com.wicare.wistorm.http.OnSuccess;
import com.wicare.wistorm.versionupdate.VersionUpdate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 2016-09-12
 */
public class HttpService extends Service {
	
	private static final String TAG = "HttpService";
	private int FIVE_SECOND = 5000  ;// 5s
    private Boolean isFirst = true;
    private String latt  = "0";
    private String lonn  ="0";
    private int speedGps = 0;
    private int gpsType  = 1;
    private int singnal  = 0;
    private WDeviceApi deviceApi;
    private WUserApi userApi;
	private WGpsDataApi gpsDataApi;
	private Handler objHandler = new Handler();
	private TelephonyManager Tel;  
	private MyPhoneStateListener myListener;

	private LocationManager locationManager = null;
	private LocationListner gpsListner = null;
//	private LocationListner wifiListner = null;

	@Override
	public void onCreate() {
		super.onCreate();
		initWistorm();
		initDevice();
		initBorcast();
		Log.d(TAG, "onCreate:  服务。。。。" + Config.con_serial);
		checkIsCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "��服务运行��  onStartCommand");
		Tel.listen(myListener ,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		return START_STICKY;
	}

	/**
	 * @author Wu
	 *         <p>
	 *         位置变化监听、
	 */
	private class LocationListner implements LocationListener {
		public void onLocationChanged(Location location) {
			latt = String.valueOf(location.getLatitude());
			lonn =  String.valueOf(location.getLongitude());
			Config.gps_time = WiStormApi.getCurrentTime();
			speedGps =  (int)location.getSpeed();
			if (location.getProvider().equals("gps")) {
				gpsType = 1;
			}else{
				gpsType = 2;
			}
			Log.e(TAG, "定位成功----:" + "速度 ：" + speedGps  + " type : " + gpsType  +  "   lon：" + location.getLongitude() + "   lat：" + location.getLatitude());
			if(isFirst){
				Log.e(TAG,"是否第一次 ：" +isFirst);
				isFirst = false;
				objHandler.removeCallbacks(mTasks);
				objHandler.postDelayed(mTasks, 1000);
			}
		}
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Logger.e("定位状态 ：" + status + " == " + provider);
		}
		public void onProviderEnabled(String provider) { }
		public void onProviderDisabled(String provider) { }
	};

	/**
	 * Wistorm
	 */
	private void initWistorm(){
		BaseVolley.init(this);
		deviceApi = new WDeviceApi(this);
		userApi = new WUserApi(this);
		gpsDataApi = new WGpsDataApi(this);
	}

	/**
	 * 注册广播
	 */
	private void initBorcast(){
		IntentFilter filter = new IntentFilter();  	   
	    filter.addAction(Intent.ACTION_SCREEN_ON);
	    filter.addAction(Intent.ACTION_SCREEN_OFF); 
	    filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
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
		getToken();	
	}
	
	/**
	 * 获取token
	 */
	private void getToken(){
		Log.e(TAG, "获取令牌  ");
		userApi.getToken(Config.USER_NAME, WEncrypt.MD5(Config.USER_PASS),"2", new OnSuccess() {
			
			@Override
			protected void onSuccess(String response) {
				// TODO Auto-generated method stub
				Log.d(TAG, response);
				try {
					JSONObject jsonObject = new JSONObject(response);
					if("0".equals(jsonObject.getString("status_code"))){
						Config.ACCESS_TOKEN = jsonObject.getString("access_token");
						Config.USER_ID = jsonObject.getString("uid");
						isCreate();	
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}	
			}
		} , new OnFailure() {
			
			@Override
			protected void onFailure(VolleyError error) {
				// TODO Auto-generated method stub
			}
		});
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
				Log.d(TAG, "获取设备返回信息 ：  " + response);
				try {
					JSONObject jsonObject = new JSONObject(response);
					if("0".equals(jsonObject.getString("status_code"))){
						if(jsonObject.isNull("data")){
							Log.e(TAG, "设备没有注册请进行注册");
							createDevice();
						}else{
							Log.e(TAG, "该设备已经注册，开始定位");
							startLocation();
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
					if("0".equals(jsonObject.getString("status_code"))){
						startLocation();
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
	 * 广播接收器
	 */	
	private final BroadcastReceiver receiver = new BroadcastReceiver(){  
		  
	    @Override  
	    public void onReceive(final Context context, final Intent intent) {
	    	ConnectivityManager connectivityManager = (ConnectivityManager)
	    			context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			if(networkInfo != null && networkInfo.isAvailable()){
				Log.e(TAG, "有网络服务");
				if(locationManager == null){
					isFirst = true;
					checkIsCreate();
				}	
			}else{
				Log.e(TAG, "没有网络连接");
				if(locationManager!= null){
					removeLocationListener();
	    			objHandler.removeCallbacks(mTasks);
	    		}
			}
	    	if(intent.getAction()==Intent.ACTION_SCREEN_ON){
	    		Log.d(TAG, "屏幕开启");
	    		isUpdate();//������
	    	}else if(intent.getAction()==Intent.ACTION_SCREEN_OFF){
	    		Log.d(TAG, "屏幕关闭");
	    	}	
	    }  
	};  

	/**
	 * 是否检查更新
	 */
	boolean isUpdate = true;
 	private void isUpdate() {
		if (SystemTools.isSdCardExist()) {
			VersionUpdate updata = new VersionUpdate(this);
			updata.checkInBackService(Config.UPDATA_APK_URL, new VersionUpdate.UpdateListener() {
				@Override
				public void hasNewVersion(boolean isHad, String updateMsg, String apkUrl) {
				if(isHad){
					Logger.d("新版本-----------------------------------");
					if (isUpdate){
						isUpdate = false;
						startIntent();
					}
				}
				}
			});
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e(TAG, "����  onDestroy");
		objHandler.removeCallbacks(mTasks);
		unregisterReceiver(receiver);
		removeLocationListener();
		Tel.listen(myListener, PhoneStateListener.LISTEN_NONE);  
		Intent service_again =new Intent(getApplicationContext(),HttpService.class);
		startService(service_again);
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
	 * 更新位置信息
	 */
	String revTime = "";
	private void uploadLocation(){
		revTime = WiStormApi.getCurrentTime();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("access_token", Config.ACCESS_TOKEN);
		params.put("did", Config.con_serial);
		params.put("lat", latt);//��
		params.put("lon", lonn);//�
		params.put("gpsFlag", String.valueOf(gpsType));
		params.put("speed", String.valueOf(speedGps));
		params.put("direct", String.valueOf(0));
		params.put("signal", String.valueOf(singnal));
		params.put("createdAt", revTime);//�
		params.put("gpsTime", Config.gps_time);//�
		params.put("rcvTime", revTime);//�
		params.put("mileage", "-1");//�
		params.put("fuel", "-1");//
		Log.d(TAG, Config.con_serial +  " 定位时间： " + Config.gps_time + "  : " + WiStormApi.getCurrentTime());
		gpsDataApi.gpsCreate(params, new OnSuccess() {
			
			@Override
			protected void onSuccess(String response) {
				// TODO Auto-generated method stub
				Log.e(TAG, response);
				try {
					JSONObject jsonObject = new JSONObject(response);
					if("0".equals(jsonObject.getString("status_code"))){
						updataDevice();//
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}	
//				{"status_code":0,"id":1}
			}
		}, new OnFailure() {
			
			@Override
			protected void onFailure(VolleyError error) {}
		});
	}

	/**
	 * 更新设备信息
	 */
	private void updataDevice(){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("access_token", Config.ACCESS_TOKEN);
//		params.put("uid", Config.USER_ID);//不能更新UID 不然别人绑定会出问题
		params.put("_did", Config.con_serial);
		params.put("activeGpsData", getActiveGpsData().toString());
		deviceApi.updata(params, new OnSuccess() {
			
			@Override
			protected void onSuccess(String response) {
				// TODO Auto-generated method stub
				Log.i(TAG, "更新设备返回信息 : " + response);
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
	private Object getActiveGpsData(){
    	JSONObject jObject=new JSONObject();  
        try {
        	jObject.put("lon", Float.valueOf(lonn));
        	jObject.put("lat", Float.valueOf(latt));
        	jObject.put("gpsTime", WiStormApi.getCurrentTime());
        	jObject.put("did", Config.con_serial);
        	jObject.put("gpsFlag", gpsType);
        	jObject.put("speed", speedGps);
        	jObject.put("direct", 0);
        	jObject.put("signal", singnal);
			jObject.put("rcvTime", revTime);//�
			jObject.put("mileage", "-1");//�
			jObject.put("fuel", "-1");//
		} catch (JSONException e) {
			e.printStackTrace();
		}  
    	return jObject;
    }

	int time_uptate_apk  = 0;
	int time_location    = 0;
	int time_uptate_data = 0;
	/**
	 * 定时提交数据
	 */
	private Runnable mTasks = new Runnable(){
		public void run(){
			time_uptate_apk ++;
			time_location ++;
			time_uptate_data ++;
			if(time_location == 2){
				time_location = 0;
				Log.i(TAG, "10秒定位一次");
				startLocation();
			}
			if(time_uptate_apk == 720){
				time_uptate_apk = 0;
				isUpdate();//一个小时检查更新一次
			}
			if(time_uptate_data == 6){
				time_uptate_data = 0;
				Log.i(TAG, "30秒提交一次数据" + Config.ACCESS_TOKEN);
				uploadLocation();
			}
			objHandler.postDelayed(mTasks, FIVE_SECOND);
		}
	};

	private void startLocation(){
		//		// 获取系统自带google定位管理对象
		if(locationManager == null){
			locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		}
//		if(wifiListner == null){
//			wifiListner = new LocationListner();
//		}
		if(gpsListner == null){
			gpsListner = new LocationListner();
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if(getApplication().checkSelfPermission(Manifest.permission.CALL_PHONE)==PackageManager.PERMISSION_GRANTED) {
			}
			Log.e(TAG, "android api定位------------111-------");
		}else{
			// 定位
			Log.e(TAG, "android api定位------------222-------");
//			locationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER,100000, 300, wifiListner);
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
//			if (wifiListner != null){
//				locationManager.removeUpdates(wifiListner);
//			}
			locationManager = null;
		}
	}
}
