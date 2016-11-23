package com.wicare.wistorm.api;

import android.content.Context;


import com.wicare.wistorm.WiStormApi;
import com.wicare.wistorm.http.BaseVolley;
import com.wicare.wistorm.http.OnSuccess;
import com.wicare.wistorm.http.OnFailure;
import java.util.HashMap;

/**
 * 发送短信验证码
 * 
 * @author WU
 * @date 2015-10-12
 */
public class WCommApi extends WiStormApi {

	private final String Method_Comm_Sms_Send   = "wicare.comm.sms.send";
	private final String Method_Comm_Sms_ValidCode   = "wicare.comm.validCode";

	/* type: 发送短信类型   1: 普通校验码信息 2: 忘记密码校验信息*/
	public final static int Tpye_Nomal = 1,Type_Forget_Password = 2;
	private BaseVolley volley;

	public WCommApi(Context context){
		super(context);
		init();
	}
	
	/**
	 * 初始化BaseVolley
	 */
	public void init(){
		volley = new BaseVolley();
	}

	/**
	 * 发送短信验证码
	 * @param mobile 手机号
	 * @param type type: 发送短信类型   1: 普通校验码信息 2：忘记密码
	 * @param onSuccess 连接成功回调
	 * @param onFailure   连接失败回调
	 */
	public void sendSMS(String mobile, int type, OnSuccess onSuccess, OnFailure onFailure){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("mobile", mobile);
		params.put("type", type+"");
		String url = super.getUrl(Method_Comm_Sms_Send, "", params);
		volley.request(url, onSuccess,onFailure);
	}

	/**
	 * 验证校验码
	 *
	 * @param account   账号
	 * @param validCode 验证码
	 * @param onSuccess 连接成功回调
	 * @param onFailure   连接失败回调a
	 */
	public void validCode(String account, String validCode,OnSuccess onSuccess,OnFailure onFailure) {
		HashMap<String, String> params = new HashMap<String, String>();
		// 1: 通过手机号 2:通过邮箱
		String validType = "1";
		// 判断手机或者邮箱
		if (account.contains("@")) {
			validType = "2";
			params.put("email", account);
		} else {
			validType = "1";
			params.put("mobile", account);
		}
		params.put("valid_type", validType);
		params.put("valid_code", validCode);
		String url = super.getUrl(Method_Comm_Sms_ValidCode, "", params);
		volley.request(url,onSuccess,onFailure);
	}
}
