package com.wgc.cmwgc.app;

/**
 * 2016-11-4
 */
public class Config {

	/**位置信息*/
	public static String location_address="test address";
	/**定位时间*/
	public static String gps_time="";
	/**wistorm token*/
	public static String ACCESS_TOKEN = "3a9557ed4250440ec57b53564e391cb50ada46ae97bc96c6abf0c3a7a3b501c388e0e5763aa116f7c3a93d190f87f11fc3c8c8e3b9c7e26cd3dac0bf04b47841";
	/**Uid*/
	public static String USER_ID = "763993890020790300";
	/**用户账号密码*/
//	public static final String USER_NAME = "autogps";
//	public static final String USER_PASS = "123456";

	/** API版本和websocket版本 APK更新的地址*/
//	public static final String UPDATA_APK_URL = "http://o.bibibaba.cn/upgrade?app_name=wgc_rearview";

	/**JT808 协议版本 APK更新地址*/
	public static final String UPDATA_APK_URL = "http://o.bibibaba.cn/upgrade?app_name=wgc_rearview_jt808";

	public static String user_info = null;
	/**设备的IMEI号*/
	public static String con_serial = "";
	/**设备的IMEI号*/
	public static String con_iccid = "";
	/**获取微信二维码*/
	public static String URL_WEIXIN_QRCODE = "http://wx.autogps.cn/server_api.php?method=getQrcode&did=";

	public static String SPF_SPEED_GEOFENCES = "speedGeofences";
	public static String SPF_SPEED = "speedData";
	public static String SPF_GEOFENCES = "geofencesData";
	public static String SPF_MY = "CMWGC";
	public static String BINDED = "bind";
	public static String MODEL = "model";
	public static String BINDED_DATE = "binded_date";
	public static String CAR_BAND = "car_band";
	public static String BINDED_UID = "binded_uid";
	public static String CUSTOMER_NAME = "customer_name";
	public static String CUSTOMER_SERVICE_TEL = "customer_service_tel";
	public static String TOTAL_MILEAGE = "total_mileage";//里程

	public static String LAST_LAT = "last_lat";
	public static String LAST_LON = "last_lon";

	public static String IS_REGISTER = "is_register";//是否注册

	/*超速和围栏 广播*/
	public static final String SPEED_ENCLOSURE = "My_SpeedEnclosureService_Broadcast";


	public static final String LOGIN = "LOGIN";
	public static final String AT = "AT";
	public static final String ALERT = "ALERT";
	public static final String COMMAND = "COMMAND";

	public static final String DID = "did";
	/*
	报警类型 alertType
	超速报警：12290
	震动报警：12291
	非法移动报警：12292
	进区域报警：12295
	出区域报警：12296
	断电报警：12297
	低电压报警：12298
	*/

	public static int SPEED_ALRET = 12290;

	public static int SHOCK_ALRET = 12291;

	public static final String SP_SERVICE_IP = "JT808serviceIP";
	public static final String SP_SERVICE_PORT = "JT808servicePort";









}
