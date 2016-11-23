package com.wicare.wistorm.api;

import android.content.Context;
import android.util.Log;

import com.wicare.wistorm.WiStormApi;
import com.wicare.wistorm.http.BaseVolley;
import com.wicare.wistorm.http.OnFailure;
import com.wicare.wistorm.http.OnSuccess;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/11/8.
 */
public class WCustomer extends WiStormApi {

    private String Method_Customer_Get    = "wicare.customer.get";//获取客户信息
    private String Method_Customer_Create = "wicare.customer.create";////创建客户
    private String Method_Customer_Updata = "wicare.customer.update";//更新客户数据;
    private String Method_Customer_Delete = "wicare.customer.delete";//删除客户
    private String Method_Customer_List   = "wicare.customer.list";//设备客户

    private BaseVolley volley;


    public WCustomer(Context context){
        super(context);
        init();
    }

    public void init(){
        if(volley == null){
            volley = new BaseVolley();
        }
    }


    /**
     * 获取客户信息的信息
     * @param params 参数字段
     * @param fields 返回信息字段
     * @param onSuccess 连接成功回调
     * @param onFailure 连接失败回调
     */
    public void get(HashMap<String, String> params, String fields, OnSuccess onSuccess, OnFailure onFailure){
        String url = super.getUrl(Method_Customer_Get, fields, params);
        Log.i("TEST_WISTORM", url);
        volley.request(url, onSuccess,onFailure);
    }


}
