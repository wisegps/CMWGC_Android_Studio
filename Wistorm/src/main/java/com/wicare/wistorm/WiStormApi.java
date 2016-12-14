package com.wicare.wistorm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * WToolKit
 * 
 * @author c
 * @date 2015-10-10
 * @desc API采用 REST风格，只需将所需 系统参数 和 应用参数 拼装成http请求，即可调用
 */

/**
 * <pre>
 * 除了customer表有一些比较特殊的操作,
 * 比如登陆,注册,重置密码之外,
 * 大部分的数据表都具有create,update,delete,list,get五个通用操作,
 * 根据数据表,传入字段名key及字段值value即可实现相应操作.
 * create接口参数格式: 新增参数:key=value,比如cust_name=测试&address=测试 
 * update接口参数格式: 条件参数: _key=value,
 * 比如_obj_id=1 更新参数:key=value, 
 * 比如obj_name=修改 
 * 
 * delete接口参数格式: 条件参数: key=value,
 * 比如obj_id=1 
 * 
 * get接口参数格式: 条件参数: key=value, 
 * 比如obj_id=1 fields: 返回字段,格式为key1,key2,key3, 比如cust_id,cust_name 
 * 
 * list接口参数格式: 查询参数: 一般格式: key=value
 * 模糊搜索: key=^value, 比如obj_name=^粤B1234 时间段: key=begin_time@end_time,
 * 比如create_time=2015-11-01@2015-12-01 fields: 返回字段, 格式为key1,key2,key3,
 * 比如cust_id,cust_name sorts: 排序字段, 格式为key1,key2,key3, 如果为倒序在字段名称前加-,
 * 比如-key1,key2 page: 分页字段, 一般为数据表的唯一ID min_id: 本页最小分页ID max_id: 本页最大分页ID limit:
 * 返回数量
 * 
 * 访问信令access_token: 除了个别接口, 大部分的接口是需要传入access_token, 开发者需要在登录之后保存access_token,
 * 之后在调用其他接口的时候传入, access_token的有效期为24小时, 过期之后需要重新获取.
 * 
 * <pre>
 */
public class WiStormApi{
	
//	public String basePath = "http://o.bibibaba.cn/router/rest?method=";//
	public String basePath = "http://wop-api.chease.cn/router/rest?method=";//这个测试
	public HashMap<String, String> hashParam = new HashMap<String, String>();

	/*----------------------------系统参数--------------------------------*/
	// WOP分配给应用的AppKey ，创建应用时可获得
	private String appSecret = "";
	private String appKey = "";
	private  String devkey = "";/**系统表不用传dev_key*/
	// 时间戳，格式为yyyy-mm-dd HH:mm:ss，例如：2013-05-06 13:52:03。
	private String timestamp = "";
	// 可选，指定响应格式。目前支持格式为json
	private String format = "json";
	// API协议版本，可选值: 2.0。
	private String v = "2.0";
	// 对 API 调用参数（除sign外）进行 md5 加密获得
	private String sign = "";
	// 参数的加密方法选择，可选值是：目前为md5
	private String signMethod = "md5";
	/*----------------------------系统参数--------------------------------*/

	public WiStormApi(Context context) {
		super();
		this.appSecret = getAppSecret(context);
		this.appKey = getAppkey(context);
		this.devkey = getDevkey(context);
		Log.d("TEST_WISTORM", "appSecret: " +  appSecret + "\n" + "appKey: " + appKey + "\n" +  "devkey: " + devkey);
	}
	
	
	/**
	 * 获取app secret
	 * 
	 * @param context
	 * @return
	 */
	public String getAppSecret(Context context){
		
		ApplicationInfo appInfo = null;
		String msg = null;
		try {
			appInfo = context.getPackageManager()
			        .getApplicationInfo(context.getPackageName(),PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		msg = appInfo.metaData.getString("Wistorm_appSecret");
    	return msg;
	}
	
	/**
	 * 获取 app key
	 * 
	 * @param context
	 * @return
	 */
	public String getAppkey(Context context){
		
		ApplicationInfo appInfo = null;
		try {
			appInfo = context.getPackageManager()
			        .getApplicationInfo(context.getPackageName(),PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	String msg = appInfo.metaData.getString("Wistorm_appKey");
    	return msg;
	}
	
	/**
	 * 获取 dev key
	 * 
	 * @param context
	 * @return
	 */
	public String getDevkey(Context context){
		
		ApplicationInfo appInfo = null;
		try {
			appInfo = context.getPackageManager()
			        .getApplicationInfo(context.getPackageName(),PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	String msg = appInfo.metaData.getString("Wistorm_devKey");
    	return msg;
	}
	
	/**
	 * 根据方法，请求字段，和参数生成http请求
	 * 
	 * @param method
	 *            调用的方法名
	 * @param fields
	 *            请求返回的字段
	 * @param params
	 *            需要传入的参数
	 * @return
	 */
	public String getUrl(String method, String fields,
			HashMap<String, String> params) {
		sign = generateSign(method, fields, params);
		StringBuffer buffer = new StringBuffer();
		// 无返回字段
		if (fields != null && fields.length() > 0) {
			buffer.append("&fields=" + fields);
		}
		// 把params变成字符串参数
		Iterator<String> it = params.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String value = params.get(key);
			value = encodeUTF(value);
			buffer.append("&" + key + "=" + value);
		}
		String url = "";
		if(method.contains("_iotDevice")||method.contains("_iotGpsData")||method.contains("_iotLog")
				||method.contains("_iotCommand")||method.contains("_iotAlert")||method.contains("customer")
				||method.contains("department")||method.contains("employee")||method.contains("vehicle")
				||method.contains("_iotStat")){
			url = basePath + method + "&timestamp=" + timestamp + "&format="
					+ format + "&app_key=" + appKey + "&v=" + v + "&sign_method="
					+ signMethod + buffer.toString() + "&sign=" + sign +  "&dev_key=" + devkey;
		}else{
			url = basePath + method + "&timestamp=" + timestamp + "&format="
					+ format + "&app_key=" + appKey + "&v=" + v + "&sign_method="
					+ signMethod + buffer.toString() + "&sign=" + sign;
		}
		Log.i("WiStormAPI", url);
		return url;
	}

	/**
	 * 生成签名
	 * 
	 * @param method
	 *            调用的方法名
	 * @param fields
	 *            请求返回的字段
	 * @param params
	 *            需要传入的参数
	 * @return
	 */
	public String generateSign(String method, String fields,
			HashMap<String, String> params) {
		hashParam.clear();
		hashParam.put("method", method);// 方法名称
		timestamp = getCurrentTime();// 时间戳yyyy-mm-dd hh:nn:ss
		timestamp = timestamp.replace(" ", "%20");
		hashParam.put("timestamp", timestamp);
		hashParam.put("format", format);// 返回数据格式
		hashParam.put("app_key", appKey);// app key
		if(method.contains("_iotDevice")||method.contains("_iotGpsData")||method.contains("_iotLog")
				||method.contains("_iotCommand")||method.contains("_iotAlert")||method.contains("customer")
				||method.contains("department")||method.contains("employee")||method.contains("vehicle")
				||method.contains("_iotStat")){
			hashParam.put("dev_key", devkey);//dev_key
		}
		hashParam.put("v", v);// 接口版本
		hashParam.put("sign_method", signMethod);// 签名方式
		// 需要返回的字段
		if (fields != null && fields.length() > 0) {
			hashParam.put("fields", fields);
		}
		// 参数字段放进去
		Iterator<String> it = params.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String value = params.get(key);
			value = encodeUTF(value);
			Log.i("LoginTest", key + ": " + value);
			hashParam.put(key, value);
		}
		// 把参数排序并进行拼接
		String s = raw(hashParam);
		Log.i("LoginTest",s);
		String sign = WEncrypt.MD5(appSecret + s + appSecret).toUpperCase();
		return sign;
	}

	/**
	 * @param value
	 * @return
	 */
	public static String encodeUTF(String value) {
		final char strMao = ':';
		final char strAite= '@';
		final char strNull= ' ';
		final char strDouh= ',';
		final char strLeftKuohao = '(';
		final char strRightKuohao = ')';
		String str = "";
		for(int i=0;i<value.length();i++){
			if(value.charAt(i) == strMao){
				str += strMao;
			}else if(value.charAt(i) == strAite){
				str += strAite;
			}else if(value.charAt(i) == strNull){
				str += "%20";
			}
			else if(value.charAt(i) == strDouh){
				str += strDouh;
			}
			else if(value.charAt(i) == strLeftKuohao){
				str += "%28";
			}
			else if(value.charAt(i) == strRightKuohao){
				str += "%29";
			}
			else{
				str +=Uri.encode(value.charAt(i)+"");
			}
		}
		return str;
	}

	/**
	 * raw 把参数排序并进行拼接
	 * 
	 * @param param
	 */
	public String raw(HashMap<String, String> param) {
		List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(
				param.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
			public int compare(Map.Entry<String, String> o1,
					Map.Entry<String, String> o2) {
				return (o1.getKey()).toString().compareTo(o2.getKey());
			}
		});
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			Map.Entry<String, String> entry = list.get(i);
			buffer.append(entry.getKey());
			buffer.append(entry.getValue());
		}
		return buffer.toString();
	}

	/**
	 * 
	 * getCurrentTime 获取当前时间
	 * 
	 * @return
	 */
	@SuppressLint("SimpleDateFormat") public static String getCurrentTime() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = sdf.format(date);
		return str;
	}
}
