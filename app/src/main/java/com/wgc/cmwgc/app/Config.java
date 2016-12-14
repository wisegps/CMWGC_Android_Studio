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
	public static String ACCESS_TOKEN = "";
	/**Uid*/
	public static String USER_ID = "";
	/**用户账号密码*/
	public static final String USER_NAME = "autogps";
	public static final String USER_PASS = "123456";
	/**APK更新的地址*/
	public static final String UPDATA_APK_URL = "http://o.bibibaba.cn/upgrade?app_name=wgc_rearview";
	public static String user_info = null;
	/**设备的IMEI号*/
	public static String con_serial = "";
	/**设备的IMEI号*/
	public static String con_iccid = "";
	/**获取微信二维码*/
	public static String URL_WEIXIN_QRCODE = "http://wx.autogps.cn/server_api.php?method=getQrcode&did=";

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


}
