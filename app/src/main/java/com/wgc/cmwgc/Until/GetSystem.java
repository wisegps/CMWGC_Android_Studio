package com.wgc.cmwgc.Until;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class GetSystem {
	private static String TAG = "GetSystem";
	/**
	 * M5D����
	 * @return m5d
	 */
	public static String getM5DEndo(String s) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			return s;
		}
		char[] charArray = s.toCharArray();
		byte[] byteArray = new byte[charArray.length];
		for (int i = 0; i < charArray.length; i++){
			byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16){
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}
	/**
	 * string 116.000000
	 * @return 116000000
	 */
	public static int StringToInt(String str) {
		try {
			Double point_doub = Double.parseDouble(str);
			return (int) (point_doub * 1000000);
		} catch (NumberFormatException e)
		{
			Log.d(TAG, "NumberFormatException");
			return 0;
		}
	}
	
	/**
	 * �޸�ʱ���ʽ,����8Сʱʱ��
	 * @param str yyyy-mm-ddThh:mm:ssz0000
	 * @param witch 0������ʱ��,1��������,2�����ַ���
	 * @return yyyy-mm-dd hh:mm:ss ��yyyy-mm-dd �� mm-dd hh:mm
	 */
	public static String ChangeTime(String str,int witch){
		String date="";
		int aa=str.length();
		if(str.length()<1||str.equals("null")){
			
		}else{
			
		
		date = str.substring(0, str.length() - 5).replace("T", " ");
		Calendar calendar = Calendar.getInstance();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.util.Date begin = sdf.parse(date);			
			calendar.setTime(begin);
			calendar.add(Calendar.HOUR_OF_DAY, 8);
			date = sdf.format(calendar.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
		if(witch == 0){
			return date;
		}else if(witch == 1){
			return date.substring(0, 10);
		}else{
			return date.substring(5, 16);
		}
		
	}
	/**
	 * @param Data xxxx-xx-xx
	 * @return xxxx-xx-xx 00:00:00
	 */
	public static String CreateTime(String Data){
		if(Data.equals("")){
			return "";
		}
		return Data + " 00:00:00";
	}
	/**
	 * @return yyyy-mm-dd hh:mm:ss
	 */
	public static String GetNowTime() {
		Time time = new Time();
		time.setToNow();
		String year = ChangeTime(time.year);
		String month = ChangeTime(time.month + 1);
		String day = ChangeTime(time.monthDay);
		String minute = ChangeTime(time.minute);
		String hour = ChangeTime(time.hour);
		String sec = ChangeTime(time.second);
		String str = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + sec;
		return str;
	}
	/**
	 * @return yyyy-mm-dd hh:mm:ss
	 */
	public static String GetBeforeTime() {
		Time time = new Time();
		time.setToNow();
		String year = ChangeTime(time.year);
		String month = ChangeTime(time.month+1);
		if(month.contains("01")){
			year=String.valueOf(Integer.parseInt(year)-1);
			month="12";
		}else{
			month="0"+(Integer.parseInt(month)-1);
		}
		String day = ChangeTime(time.monthDay);		
		String str = year + "-" + month + "-" + day;
		return str;
	}
	
	/**
	 * @return 09
	 */
	public static String ChangeTime(int i) {
		String str = null;
		if (i < 10) {
			str = "0" + i;
		} else {
			str = "" + i;
		}
		return str;
	}
	/**
	 * ��ͼ�����ת
	 * @param context ���
	 * @param direct ��ת�Ƕ�
	 * @param ResourceId ͼƬ��Դ
	 * @return ��ת���ͼƬ
	 */
	public static BitmapDrawable GetDrawable(Context context,int direct,int ResourceId){
		Bitmap bitmapOrg = BitmapFactory.decodeResource(context.getResources(),ResourceId);
		int width = bitmapOrg.getWidth();
		int height = bitmapOrg.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale(1, 1);
		matrix.postRotate(direct, (float) width / 2, (float) height / 2);
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width,height, matrix, true);
		BitmapDrawable bmd = new BitmapDrawable(context.getResources(), resizedBitmap);
		return bmd;
	}
	


	/**
	 * @param context
	 * @param ������
	 * @return versionName���汾���ƣ���1.2
	 */
	public static String GetVersion(Context context,String packString) {
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo(packString, 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 检查网络是否可用
	 * @param context
	 * @return result
	 */
	public static boolean checkNetWorkStatus(Context context){
        boolean result;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();
        if (netinfo != null && netinfo.isConnected()) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }
	/**
	 * 获取MAC
	 * @param context
	 * @return
	 */
	public static String getMacAddress(Context context){
    	try {
			WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);		 
			WifiInfo info = wifi.getConnectionInfo();
			String wifiAddress = info.getMacAddress();
			if(wifiAddress != null){
				return wifiAddress;
			}
			String Imei = ((TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE)).getDeviceId();
			if(Imei != null){
				return Imei;
			}
			String BluetoothAddress = BluetoothAdapter.getDefaultAdapter().getAddress();
			return BluetoothAddress;
		} catch (Exception e){
			return "";
		}
    }
	
}