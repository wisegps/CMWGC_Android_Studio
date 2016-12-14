package com.wicare.wistorm.api;

import android.content.Context;
import android.util.Log;

import com.wicare.wistorm.WiStormApi;
import com.wicare.wistorm.http.BaseVolley;
import com.wicare.wistorm.http.OnFailure;
import com.wicare.wistorm.http.OnSuccess;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/9/27.
 */
public class WGpsDataApi  extends WiStormApi {

    private String Method_Gpsdata_Create = "wicare._iotGpsData.create";//创建历史定位记录
    private String Method_Gpsdata_GetList = "wicare._iotGpsData.list";//获取历史定位记录列表
    private String Method_Gpsdata_Get = "wicare._iotGpsData.get";//获取历史定位记录列表

    private BaseVolley volley;


    public WGpsDataApi(Context context){
        super(context);
        init();
    }

    public void init(){
        if(volley == null){
            volley = new BaseVolley();
        }
    }


    /**
     * 创建GPS 定位记录
     * @param params
     * @param onSuccess
     * @param onFailure
     */
    public void gpsCreate(HashMap<String, String> params, OnSuccess onSuccess, OnFailure onFailure){
        String url = super.getUrl(Method_Gpsdata_Create, "", params);
        Log.i("TEST_WISTORM", url);
        volley.request(url, onSuccess,onFailure);
    }


    /**
     * 获取GPS历史定位记录
     *
     * @param params
     * @param fields
     * @param onSuccess
     * @param onFailure
     */
    public void getGpsList(HashMap<String, String> params,String fields,OnSuccess onSuccess,OnFailure onFailure){
        String url = super.getUrl(Method_Gpsdata_GetList, fields, params);
        Log.i("TEST_WISTORM", url);
        volley.request(url, onSuccess,onFailure);
    }

    public void getGps(HashMap<String, String> params,String fields,OnSuccess onSuccess,OnFailure onFailure){
        String url = super.getUrl(Method_Gpsdata_Get, fields, params);
        Log.i("TEST_WISTORM", url);
        volley.request(url, onSuccess,onFailure);
    }

}
