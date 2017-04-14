package com.wgc.cmwgc.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.wgc.cmwgc.R;
import com.wgc.cmwgc.app.Config;
import com.wicare.wistorm.api.WDeviceApi;
import com.wicare.wistorm.http.BaseVolley;
import com.wicare.wistorm.http.OnSuccess;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/11/3.
 */
public class DeviceActivity extends AppCompatActivity {

    @Bind(R.id.tv_model)
    TextView tvModel;
    @Bind(R.id.tv_imei)
    TextView tvImei;
    @Bind(R.id.tv_iccid)
    TextView tvIccid;
    @Bind(R.id.tv_ver_of_Android)
    TextView tvVer;

    private Context mContext;
    private SharedPreferences spf;
    private SharedPreferences.Editor editor;
    private String model = "";
    private WDeviceApi deviceApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        ButterKnife.bind(this);
        initSpf();
        initView();
        initWistorm();
        getDeviceInfo();
    }

    private void initWistorm() {
        BaseVolley.init(this);
        deviceApi = new WDeviceApi(this);
    }
    private void initView() {
        mContext = this;
        spf = getSharedPreferences(Config.SPF_MY,Activity.MODE_PRIVATE);
        model = spf.getString(Config.MODEL,"unknow");
        tvIccid.setText(Config.con_iccid);
        tvModel.setText(model);
        tvImei.setText(Config.con_serial);
        tvVer.setText("" + Build.DISPLAY);
    }

    /**
     * @param context
     */
    public static void startAction(Activity context) {
        Intent intent = new Intent(context, DeviceActivity.class);
        context.startActivity(intent);
    }

    private void initSpf(){
        spf = getSharedPreferences(Config.SPF_MY,Activity.MODE_PRIVATE);
        editor = spf.edit();
    }
    private void getDeviceInfo(){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("access_token", Config.ACCESS_TOKEN);
        params.put("did",Config.con_serial);//459432808550306 Config.con_serial 459432808108543

        String fields = "did,binded,bindDate,uid,model";

        if(!TextUtils.isEmpty(Config.con_serial))
            deviceApi.get(params, fields, new OnSuccess() {
                @Override
                protected void onSuccess(String response) {
                    Logger.d("设备信息 ：" + response);
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject object1 = new JSONObject(object.getString("data"));
                        if(object1.has("model")){
                            String model = object1.getString("model").toString();
                            editor.putString(Config.MODEL,model);
                            editor.commit();
                            Logger.d("设备型号 ：" + model);
                            tvModel.setText(model);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },null);
    }


}
