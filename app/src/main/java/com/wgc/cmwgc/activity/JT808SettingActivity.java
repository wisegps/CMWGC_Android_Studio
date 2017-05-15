package com.wgc.cmwgc.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.wgc.cmwgc.R;
import com.wgc.cmwgc.app.Config;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 功能： descriable
 * 作者： Administrator
 * 日期： 2017/4/13 11:48
 * 邮箱： descriable
 */
public class JT808SettingActivity extends AppCompatActivity {


    @Bind(R.id.edit_ip)
    TextInputEditText editIp;
    @Bind(R.id.edit_port)
    TextInputEditText editPort;

    private String ip;
    private String port;

    private SharedPreferences spf;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jt808_setting);
        ButterKnife.bind(this);
        initSP();
    }

    private void initSP(){
        spf = getSharedPreferences(Config.SPF_MY,MODE_PRIVATE);
        editor = spf.edit();

        ip = spf.getString( Config.SP_SERVICE_IP, "");
        port = spf.getString( Config.SP_SERVICE_PORT, "");

//        if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port)){
//            editPort.setText(getResources().getString(R.string.default_port));
//            editIp.setText(getResources().getString(R.string.default_ip));
//        }else{
//            editIp.setText(ip);
//            editPort.setText(port);
//        }
    }
    /**
     * @param context
     */
    public static void startAction(Activity context) {
        Intent intent = new Intent(context, JT808SettingActivity.class);
        context.startActivity(intent);
    }


    @OnClick(R.id.btn_jt_enable)
    public void onClick() {
        saveAddress();
    }


    private void saveAddress(){
        ip = editIp.getText().toString().trim();
        port = editPort.getText().toString().trim();
        Log.e("---------ip地址-------" ,ip+"");
        Log.e("---------port端口号-------" ,ip+"");
        if(TextUtils.isEmpty(ip) || TextUtils.isEmpty(port)){
            Toast.makeText(this,"IP或者端口不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        editor.putString(Config.SP_SERVICE_IP,ip);
        editor.putString(Config.SP_SERVICE_PORT,port);
        editor.commit();
        Toast.makeText(this,"开始启动部标",Toast.LENGTH_LONG).show();
        Intent intent = new Intent("my_bro_is_enable_jt");
        intent.putExtra("ip",ip);
        intent.putExtra("port",port);
        sendBroadcast(intent);
//
//        editIp.setText("");
//        editPort.setText("");
    }
}
