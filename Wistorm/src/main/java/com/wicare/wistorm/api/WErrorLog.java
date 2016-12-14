package com.wicare.wistorm.api;

import android.content.Context;
import android.util.Log;

import com.wicare.wistorm.WiStormApi;
import com.wicare.wistorm.http.BaseVolley;
import com.wicare.wistorm.http.OnFailure;
import com.wicare.wistorm.http.OnSuccess;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/12/5.
 * 错误日志
 * 字段： fields:'app_key,bug_report,account,objectId,ACL,creator,createdAt,updatedAt'
 */
public class WErrorLog extends WiStormApi {

    private String Method_Error_create  = "wicare.crash.create";//提交错误日志
    private String Method_Error_get  = "wicare.crash.get";//获取错误日志
    private BaseVolley volley;


    public WErrorLog(Context context){
        super(context);
        init();
    }

    public void init(){
        if(volley == null){
            volley = new BaseVolley();
        }
    }


    /**
     * 创建错误日志的信息
     * @param params 参数字段
     * @param fields 返回信息字段
     * @param onSuccess 连接成功回调
     * @param onFailure 连接失败回调
     */
    public void create(HashMap<String, String> params, String fields, OnSuccess onSuccess, OnFailure onFailure){
        String url = super.getUrl(Method_Error_create, fields, params);
        Log.i("TEST_WISTORM", url);
        volley.request(url, onSuccess,onFailure);
    }
    /**
     * 获取错如日志的信息
     * @param params 参数字段
     * @param fields 返回信息字段
     * @param onSuccess 连接成功回调
     * @param onFailure 连接失败回调
     */
    public void get(HashMap<String, String> params, String fields, OnSuccess onSuccess, OnFailure onFailure){
        String url = super.getUrl(Method_Error_get, fields, params);
        Log.i("TEST_WISTORM", url);
        volley.request(url, onSuccess,onFailure);
    }
}
