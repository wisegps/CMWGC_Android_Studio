package com.wgc.cmwgc.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.orhanobut.logger.Logger;
import com.wgc.cmwgc.R;
import com.wgc.cmwgc.Until.GetSystem;
import com.wgc.cmwgc.Until.SystemTools;
import com.wgc.cmwgc.Until.Utils;
import com.wgc.cmwgc.app.Config;
import com.wgc.cmwgc.service.CoreServer;
import com.wgc.cmwgc.service.HttpService;
import com.wicare.wistorm.WEncrypt;
import com.wicare.wistorm.api.WCustomer;
import com.wicare.wistorm.api.WDeviceApi;
import com.wicare.wistorm.api.WGpsDataApi;
import com.wicare.wistorm.api.WUserApi;
import com.wicare.wistorm.http.BaseVolley;
import com.wicare.wistorm.http.OnFailure;
import com.wicare.wistorm.http.OnSuccess;
import com.wicare.wistorm.versionupdate.VersionUpdate;
import com.wicare.wistorm.widget.CustomerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 注意  LeadMainActivity 不能修改，因为关联了 后视镜的 启动图标
 */
public class LeadMainActivity extends AppCompatActivity{

    static final String TAG = "TEST_CMWGC";

    @Bind(R.id.tv_customer_service_tel)
    TextView tvCustomerServiceTel;
    @Bind(R.id.tv_service_tel)
    TextView tvServiceTel;

    private boolean isRegisterService = false;
    private Activity mContext;
    private TelephonyManager Tel;
    private VersionUpdate updata;
    private WDeviceApi deviceApi;
    private WUserApi userApi;
    private WCustomer customer;
    private WGpsDataApi gpsDataApi;
    private SharedPreferences spf;
    private SharedPreferences.Editor editor;
    private String uidOfdid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initWistorm();

        initSpf();
        mContext = LeadMainActivity.this;
        initDevice();
        checkServiceIsRunning();
//        initDevice();
        checkDeviceIsBinded();

//        startService(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        startService(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        startService(this);
    }

    public static void startService(final Context context){
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                boolean isRun = Utils.isServiceWork(context, "com.wgc.cmwgc.service.CoreServer");
                if (isRun == false){
                    Intent intent_service = new Intent(context,CoreServer.class);
                    context.startService(intent_service);
                }

            }
        };
        timer.schedule(task, 5000);
    }


    private void initSpf(){
        spf = getSharedPreferences(Config.SPF_MY,Activity.MODE_PRIVATE);
        editor = spf.edit();
    }
    /**
     * Wistorm
     */
    private void initWistorm() {
        BaseVolley.init(this);
        deviceApi = new WDeviceApi(this);
        userApi = new WUserApi(this);
        customer = new WCustomer(this);
        gpsDataApi = new WGpsDataApi(this);
    }

    private void initDevice() {
        /*读取设备的 id  和 sim卡号码*/
        Tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        Config.con_serial = Tel.getDeviceId();
        Config.con_iccid = Tel.getSimSerialNumber();

        Logger.d("ICCID: " + Config.con_iccid);
        updata = new VersionUpdate(this);
        checkAppUpdate();
        tvServiceTel.setText(spf.getString(Config.CUSTOMER_NAME,""));
        tvCustomerServiceTel.setText(spf.getString(Config.CUSTOMER_SERVICE_TEL,""));
        editor.putString(Config.DID, Config.con_serial);
        editor.commit();




        // 方法1 Android获得屏幕的宽和高
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int screenWidth = screenWidth = display.getWidth();
        int screenHeight = screenHeight = display.getHeight();

        // 方法2
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        float width=dm.widthPixels*dm.density;
        float height=dm.heightPixels*dm.density;

        Logger.d(" ----------- " + "First method:"+dm.toString()+"\n"+"Second method:"+"Y="+screenWidth+";X="+screenHeight);


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private String s;
    @OnClick({R.id.ll_about, R.id.ll_car_master, R.id.ll_car_team, R.id.ll_device})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_car_master:
                CarMasterActivity.startAction(mContext);
//                System.out.println(s.equals("any string"));
                break;
            case R.id.ll_device:
                DeviceActivity.startAction(mContext);
                break;
            case R.id.ll_about:
                AboutActivity.startAction(mContext);
                break;
            case R.id.ll_car_team:
                CarTeamActivity.startAction(mContext);
                break;
        }
    }

    private void checkServiceIsRunning() {
        if (!SystemTools.isWorked(this, "com.wgc.cmwgc.service.CoreServer")) {
            Log.e(TAG, "服务没有运行，启动服务");
            Intent intent_service = new Intent(LeadMainActivity.this, CoreServer.class);
            startService(intent_service);
        }
    }

    String apkFileName;
    private void checkAppUpdate() {
        updata.check(Config.UPDATA_APK_URL, new VersionUpdate.UpdateListener() {
            @Override
            public void hasNewVersion(boolean isHad, String updateMsg, String apkUrl) {
            }

            @Override
            public void finishDownloadApk(String saveFileName) {
                //....................................
                Logger.w("下载完成............."+saveFileName);
                apkFileName = saveFileName;
                showInstallDialog(mContext);
            }
        });
    }


    /**
     * 马上安装对话框
     * @param context
     */
    private void showInstallDialog(Context context){
        CustomerDialog.Builder builder = new CustomerDialog.Builder(context);
        builder.setTitle(context.getResources().getString(com.wicare.wistorm.R.string.new_version_install));
        builder.setMessage(context.getResources().getString(com.wicare.wistorm.R.string.if_install));
        builder.setPositiveButton(context.getResources().getString(com.wicare.wistorm.R.string.install_now), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                installApk(apkFileName);
            }
        });
        builder.setNegativeButton( new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }


    /**
     * 安装apk
     *
     */
    private void installApk(String saveFileName) {
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        startActivity(i);
    }


    private void checkDeviceIsBinded() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("access_token", Config.ACCESS_TOKEN);
        params.put("did",Config.con_serial);// 459432808539341 459432808550306 Config.con_serial 459432808108543 459432808117288
        String fields = "did,binded,bindDate,uid,model,activeGpsData,params";
        if(!TextUtils.isEmpty(Config.con_serial))
        deviceApi.get(params, fields, new OnSuccess() {
            @Override
            protected void onSuccess(String response) {
                Logger.d("设备是否已经注册：" + response);
                requestNum = 0;
                try {
                    JSONObject object = new JSONObject(response);
                    JSONObject object1 = new JSONObject(object.getString("data"));
                    getMileage(object1);
                    if (object1.has("binded")) {
                        if (object1.getBoolean("binded")) {
                            editor.putBoolean(Config.BINDED, true);
                            editor.commit();
                            if (object1.has("uid")) {
                                if (!TextUtils.isEmpty(object1.getString("uid"))) {
                                    uidOfdid = object1.getString("uid");
                                    editor.putString(Config.BINDED_UID, uidOfdid);
                                    editor.commit();
                                    getCustomerInfo();
                                    Logger.w("已经绑定===============================");
                                }
                            }
                            if (object1.has("bindDate")) {
                                if (!TextUtils.isEmpty(object1.getString("bindDate"))) {
                                    String date[] = object1.getString("bindDate").toString().split("T");
                                    editor.putString(Config.BINDED_DATE, date[0]);
                                    editor.commit();
                                }
                            }
                        } else {
                            editor.putBoolean(Config.BINDED, false);
                            editor.commit();
                            if (object1.has("uid")) {
                                if (!TextUtils.isEmpty(object1.getString("uid"))) {
                                    uidOfdid = object1.getString("uid");
                                    editor.putString(Config.BINDED_UID, uidOfdid);
                                    editor.commit();
                                    Logger.w("没有绑定===============================");
                                    getServiceInfo(uidOfdid);
                                }
                            }
                        }
                    } else {
                        editor.putBoolean(Config.BINDED, false);
                        editor.commit();
                    }
                    if (object1.has("model")) {
                        editor.putString(Config.MODEL, object1.getString("model").toString());
                        editor.commit();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new OnFailure() {
            @Override
            protected void onFailure(VolleyError error) {
                requestNum ++;
                if(requestNum < 5){
                    checkDeviceIsBinded();
                }
            }
        });
    }


    int requestNum = 0;
//    /**
//     * 获取token
//     */
//    private void getToken() {
//        Log.e(TAG, "获取令牌  ");
//        userApi.getToken(Config.USER_NAME, WEncrypt.MD5(Config.USER_PASS), "2", new OnSuccess() {
//
//            @Override
//            protected void onSuccess(String response) {
//                // TODO Auto-generated method stub
//                Log.d(TAG, response);
//                requestNum = 0;
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    if ("0".equals(jsonObject.getString("status_code"))) {
//                        Config.ACCESS_TOKEN = jsonObject.getString("access_token");
////                        Config.USER_ID = jsonObject.getString("uid");
//                        checkDeviceIsBinded();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new OnFailure() {
//
//            @Override
//            protected void onFailure(VolleyError error) {
//                // TODO Auto-generated method stub
//                requestNum ++;
//                if(requestNum < 5){
//                    getToken();
//                }
//            }
//        });
//    }

    String pid = "";
    private void getCustomerInfo(){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("access_token", Config.ACCESS_TOKEN);
        params.put("objectId",uidOfdid);//获取客户表的信息,uid 就是客户表的objectId
        String fields = "tel,parentId,name";
        customer.get(params, fields, new OnSuccess() {
            @Override
            protected void onSuccess(String response) {
                Logger.d("获取客户信息：" + uidOfdid + " == :" + response);
                requestNum=0;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if ("0".equals(jsonObject.getString("status_code"))) {
                        if (jsonObject.isNull("data")) {
                        } else {
                            JSONObject object = new JSONObject(jsonObject.getString("data"));
                            if (object.has("parentId")) {
                                String strPid = object.getString("parentId");
                                JSONArray jsonArray = new JSONArray(strPid);
                                Logger.d("获取 parentid ：" + strPid + "---" + jsonArray.length());
                                if (jsonArray.length() > 0) {
                                    pid = jsonArray.get(0).toString();
                                }
                                Logger.d("获取 parentid ：" + pid);
                                if (!TextUtils.isEmpty(pid))
                                    getServiceInfo(pid);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new OnFailure() {
            @Override
            protected void onFailure(VolleyError error) {
                requestNum ++;
                if(requestNum < 5){
                    getCustomerInfo();
                }
            }
        });
    }


    private void getServiceInfo(String parentId){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("access_token", Config.ACCESS_TOKEN);
        params.put("objectId",parentId);//获取客户表的信息,uid 就是客户表的objectId
        String fields = "tel,parentId,name";
        customer.get(params, fields, new OnSuccess() {
            @Override
            protected void onSuccess(String response) {
                Logger.d("获取客户parentId信息：" + " == :" + response);
                requestNum =0;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if ("0".equals(jsonObject.getString("status_code"))) {
                        if (jsonObject.isNull("data")) {
                        } else {
                            JSONObject object = new JSONObject(jsonObject.getString("data"));
                            if (object.has("tel")) {
                                String tel = object.getString("tel");
                                editor.putString(Config.CUSTOMER_SERVICE_TEL, tel);
                                editor.commit();
                                tvCustomerServiceTel.setText(tel);
                            }
                            if (object.has("name")) {
                                String strServiceId = object.getString("name");
                                editor.putString(Config.CUSTOMER_NAME, strServiceId);
                                editor.commit();
                                tvServiceTel.setText(strServiceId);
                            }
                        }
                    }
//                    getGpsData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new OnFailure() {
            @Override
            protected void onFailure(VolleyError error) {
                requestNum ++;
                if(requestNum < 5){
                    getServiceInfo(pid);
                }
            }
        });
    }

    /**
     * 取得里程
     * @param jsonObject
     */
    private void getMileage(JSONObject jsonObject){
        if(jsonObject.has("activeGpsData")){
            try{
                JSONObject object = new JSONObject(jsonObject.getString("activeGpsData").toString());
                if (object.has("mileage")){
                    Logger.d(TAG,"里程 ：" + object.getString("mileage"));
                    editor.putString(Config.TOTAL_MILEAGE,object.getString("mileage"));
                    editor.commit();
                }else {
                    editor.putString(Config.TOTAL_MILEAGE,"0");
                    editor.commit();
                }
            }catch (JSONException e){

            }
        }
    }
}
