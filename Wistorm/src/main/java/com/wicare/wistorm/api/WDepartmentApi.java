package com.wicare.wistorm.api;

import android.content.Context;

import com.wicare.wistorm.WiStormApi;
import com.wicare.wistorm.http.BaseVolley;
import com.wicare.wistorm.http.OnFailure;
import com.wicare.wistorm.http.OnSuccess;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/9/14.
 */
public class WDepartmentApi extends WiStormApi {

    public String Method_Department_Create = "wicare.department.create";//创建部门信息
    public String Method_Department_Delete = "wicare.department.delete";//删除部门信息
    public String Method_Department_Update = "wicare.department.update";//修改部门信息
    public String Method_Department_List   = "wicare.department.list";//获取部门列表
    public String Method_Department_Get    = "wicare.department.get";//获取部门信息

    public HashMap<String, String> hashParam = new HashMap<String, String>();
    private BaseVolley volley;

    public WDepartmentApi(Context context){
        super(context);
        init();
    }

    /**
     * 初始化网络框架
     */
    public void init(){
        volley = new BaseVolley();
    }
    /**
     * @param params
     * @param onSuccess 连接成功回调
     * @param onFailure 连接失败回调
     */
    public void create(HashMap<String, String>params, OnSuccess onSuccess, OnFailure onFailure){
        String url = super.getUrl(Method_Department_Create, "", params);
        volley.request(url, onSuccess,onFailure);
    }


    /**
     * @param params 创建部门的所有字段参数
     * @param onSuccess 连接成功回调
     * @param onFailure 连接失败回调
     */
    public void update(HashMap<String, String> params,OnSuccess onSuccess,OnFailure onFailure) {
        String url = super.getUrl(Method_Department_Update, "", params);
        volley.request(url, onSuccess,onFailure);
    }



    /**
     * @param params
     * @param fields 部门表的字段（请查看最下面的车辆表字段）
     * @param onSuccess 连接成功回调
     * @param onFailure 连接失败回调
     */
    public void list(HashMap<String, String> params,String fields,OnSuccess onSuccess,OnFailure onFailure) {
        String url = super.getUrl(Method_Department_List, fields, params);
        volley.request(url, onSuccess,onFailure);
    }


    /**
     * @param params objectId：部门id access_token:token(根据objectId 删除该车辆)
     * @param onSuccess 连接成功回调
     * @param onFailure 连接失败回调
     */
    public void delete(HashMap<String, String> params,OnSuccess onSuccess,OnFailure onFailure){
        String url = super.getUrl(Method_Department_Delete, "", params);
        volley.request(url, onSuccess,onFailure);
    }

    /**
     * @param params objectId：部门id access_token:token
     * @param fields 返回字段（请查看最下面的车辆表字段）
     * @param onSuccess 连接成功回调
     * @param onFailure 连接失败回调
     */
    public void get(HashMap<String, String> params,String fields,OnSuccess onSuccess,OnFailure onFailure){
        String url = super.getUrl(Method_Department_Get, fields, params);
        volley.request(url, onSuccess,onFailure);
    }


//    objectId: Number //??ID
//    uid: Number //??????
//    name: String //????
//    adminId: Number //????ID
//    parentId: Number //??????
//    treePath: String //?????
//    ???????????????????????????????
//            ???????????????
//    createdAt: Date
//    updatedAt: Date


}
