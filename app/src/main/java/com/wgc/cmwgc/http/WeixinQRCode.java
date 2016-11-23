package com.wgc.cmwgc.http;

import android.content.Context;
import android.util.Log;

import com.wgc.cmwgc.app.Config;
import com.wicare.wistorm.api.WVehicleApi;
import com.wicare.wistorm.http.BaseVolley;
import com.wicare.wistorm.http.OnFailure;
import com.wicare.wistorm.http.OnSuccess;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Administrator on 2016/11/8.
 */
public class WeixinQRCode {

    private Context mContext;
    private BaseVolley volley;


    public WeixinQRCode(Context context){
        this.mContext=context;
        init();
    }

    public void init(){
        if(volley == null){
            volley = new BaseVolley();
        }
    }

    /**
     * @param did 设备id
     * @param onSuccess
     * @param onFailure
     */
    public void getQRCode(String did, OnSuccess onSuccess, OnFailure onFailure){
        String url = Config.URL_WEIXIN_QRCODE + did;
        Log.i("TEST_WISTORM", url);
        volley.request(url, onSuccess,onFailure);
    }


    public void  getNetworkTime(){
        URLConnection uc= null;//生成连接对象

        try {
            URL url=new URL("http://www.bjtime.cn");//取得资源对象
            uc = url.openConnection();
            uc.connect(); //发出连接
            long ld=uc.getDate(); //取得网站日期时间
            Date date=new Date(ld); //转换为标准时间对象
            //分别取得时间中的小时，分钟和秒，并输出
            System.out.print(date.getHours()+"时"+date.getMinutes()+"分"+date.getSeconds()+"秒");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
