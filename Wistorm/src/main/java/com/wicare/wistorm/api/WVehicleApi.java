package com.wicare.wistorm.api;

import android.content.Context;
import com.wicare.wistorm.WiStormApi;
import com.wicare.wistorm.http.BaseVolley;
import com.wicare.wistorm.http.OnSuccess;
import com.wicare.wistorm.http.OnFailure;

import java.util.HashMap;

/**
 * @ClassName:  WVehicleApi   
 * @Description:车辆接口   
 * @author: Wu
 * @date:   2016-5-6 上午10:38:32   
 *      
 */
public class WVehicleApi extends WiStormApi {
	
	public String Method_Vehicle_Create = "wicare.vehicle.create";//创建车辆信息
	public String Method_Vehicle_Delete = "wicare.vehicle.delete";//删除车辆信息
	public String Method_Vehicle_Update = "wicare.vehicle.update";//修改车辆
	public String Method_Vehicle_List   = "wicare.vehicle.list";//获取车辆列表
	public String Method_Vehicle_Get    = "wicare.vehicle.get";//获取车辆信息

	public HashMap<String, String> hashParam = new HashMap<String, String>();

	private BaseVolley volley;

	public WVehicleApi(Context context){
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
	public void create(HashMap<String, String>params,OnSuccess onSuccess,OnFailure onFailure){
		String url = super.getUrl(Method_Vehicle_Create, "", params);
		volley.request(url, onSuccess,onFailure);
	}
	
	 
	/**
	 * @param params 创建车辆的所有字段参数
	 * @param onSuccess 连接成功回调 
	 * @param onFailure 连接失败回调
	 */
	public void update(HashMap<String, String> params,OnSuccess onSuccess,OnFailure onFailure) {
		String url = super.getUrl(Method_Vehicle_Update, "", params);
		volley.request(url, onSuccess,onFailure);
	}


  
	/**
	 * @param params
	 * @param fields 车辆表的字段（请查看最下面的车辆表字段）
	 * @param onSuccess 连接成功回调  
	 * @param onFailure 连接失败回调
	 */
	public void list(HashMap<String, String> params,String fields,OnSuccess onSuccess,OnFailure onFailure) {
		String url = super.getUrl(Method_Vehicle_List, fields, params);
		volley.request(url, onSuccess,onFailure);
	}
	
	
	/**
	 * @param params obj_id：车辆id access_token:token(根据obj_id 删除该车辆)
	 * @param onSuccess 连接成功回调
	 * @param onFailure 连接失败回调
	 */
	public void delete(HashMap<String, String> params,OnSuccess onSuccess,OnFailure onFailure){
		String url = super.getUrl(Method_Vehicle_Delete, "", params);
		volley.request(url, onSuccess,onFailure);
	}
	

	/**
	 * @param params obj_id：车辆id access_token:token
	 * @param fields 返回字段（请查看最下面的车辆表字段）
	 * @param onSuccess 连接成功回调
	 * @param onFailure 连接失败回调
	 */
	public void get(HashMap<String, String> params,String fields,OnSuccess onSuccess,OnFailure onFailure){
		String url = super.getUrl(Method_Vehicle_Get, fields, params);
		volley.request(url, onSuccess,onFailure);
	}
	
	/*  
	 * 车辆的字段
	 * 
	obj_id: Number,   //车辆id
    cust_id: Number,  //用户id
    cust_name:String, //临时字段
    obj_name: String, //车牌号
    nick_name: String, //车辆名称
    device_id: Number, //终端id：0 表示没有绑定终端
    active_gps_data: {},
    car_brand_id: Number, //品牌id
    car_brand: String, //车辆品牌
    car_series_id: Number, //车型id
    car_series: String, //车型
    car_type_id: Number, //车款id
    car_type: String,   //车款
    vio_city_name: String, //违章城市名称
    vio_location: String, //违章城市代码
    engine_no: String,  //发动机号
    frame_no: String,   //车架号
    reg_no: String,     //登记证书
    last_query: Date,   //最后查询时间，用于控制用户查询周期，vip一天一次，免费用户七天一次。
    insurance_company: String, //保险公司
    insurance_tel: String,     //保险公司电话
    insurance_date: Date,      //保险到期时间
    insurance_no: String,      //保险单号
    annual_inspect_date: Date, //车辆年检日期
    maintain_company: String,  //保养4S店
    maintain_tel: String,      //保养4S店电话
    maintain_last_mileage: Number,  //最后保养里程
    maintain_last_date: Date,   //最后保养时间
    maintain_next_mileage: Number, //下次保养里程
    mileage: Number,           //当前里程，需要动态更新
    gas_no: String,            //汽油标号 0#, 90#, 93#(92#), 97#(95#)
    fuel_ratio: Number,        //油耗修正系数(直接原始数据*该系数得到实际油耗)
    fuel_price: Number,        //加油油价
    buy_date: Date,            //购车时间
    create_time: Date,         //创建时间
    update_time: Date,         //更新时间
    fault_count: Number,       //最新故障计数
    alert_count: Number,       //最新报警计数
    event_count: Number,       //车务提醒计数
    vio_count: Number,         //最新违章计数
    geofence: {},             //围栏
    vio_citys: []            //违章城市
*/
}
