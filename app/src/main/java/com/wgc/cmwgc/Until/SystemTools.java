package com.wgc.cmwgc.Until;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.wgc.cmwgc.model.APoints;
import com.wgc.cmwgc.model.Geofences;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/9/2.
 */
public class SystemTools {
//    private static final double EARTH_RADIUS = 6378.137;
//    private static final  double EARTH_RADIUS = 6378137;//赤道半径(单位m)
    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 判断网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
        } else {
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @param context
     * @param msg
     */
    public static void showToast(Context context, String msg){
        Toast.makeText(context,msg, Toast.LENGTH_LONG).show();
    }

    /**
     * int 数组转换成字符串
     * @param status
     * @return
     */
    public static String array2String(int [] status){
        if(status!=null && status.length>0){
            StringBuffer buffer = new StringBuffer();
            for(int i=0;i<status.length;i++){
                buffer.append(status[i]);
                buffer.append(",");
            }
            return buffer.substring(0, buffer.length()-1);
        }
        return "";

    }

    /**
     * 字符串转换成int数组
     * @param str
     * @return
     */
    public static int[] string2Array(String str){
        if(str == null || str.length() ==0){
            return null;
        }
        String[] strArray =  str.split(",");
        int[] ary = null;
        if(strArray!=null& strArray.length>0){
            ary = new int[strArray.length];
            for(int i=0;i<strArray.length;i++){
                ary[i] = Integer.parseInt(strArray[i]);
            }
        }
        return ary;
    }


    public static String getStartTime(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        String str = sdf.format(date);
        return str;
    }

    public static String getStopTime(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 23:59:00");
        String str = sdf.format(date);
        return str;
    }


    public static String getEncodeTime(long current){
        Date date = new Date(current);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = sdf.format(date);
        return URLEncoder.encode(str);
    }

    public static String getStringTime(long current){
        Date date = new Date(current);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = sdf.format(date);
        return str;
    }

    public static String getCurrentStringTime(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = sdf.format(date);
        return str;
    }

    /**
     * �ж�sd���Ƿ����
     *
     * @return
     */
    public static boolean isSdCardExist() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }


    public static String getCurrentTime(){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date = sf.format(new Date());
        return date;
    }


    /***
     * * 检查服务是否运行
     */
    public static boolean isWorked(Context context,String className) {
        ActivityManager myManager = (ActivityManager)context.getApplicationContext().getSystemService(
                Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(200);

        for (int i = 0; i < runningService.size(); i++) {
            String aa = runningService.get(i).service.getClassName().toString();
            Log.i("TAG", aa);
            if (runningService.get(i).service.getClassName().toString()
                    .equals(className)) {
                return true;
            }
        }
        return false;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    private static double EARTH_RADIUS = 6378.137;



    /**
     * 根据两个位置的经纬度，来计算两地的距离（单位为KM）
     * 参数为String类型
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        return s;
    }


    /**
     *
     * @param lat_a 纬度1
     * @param lng_a 经度1
     * @param lat_b 纬度2
     * @param lng_b 经度2
     * @return
     */
    public static   double getAngle111(double lat_a, double lng_a, double lat_b, double lng_b) {

//        double y = Math.sin(lng_b-lng_a) * Math.cos(lat_b);
//        double x = Math.cos(lat_a)*Math.sin(lat_b) - Math.sin(lat_a)*Math.cos(lat_b)*Math.cos(lng_b-lng_a);
//        double brng = Math.atan2(y, x);
//
//        brng = Math.toDegrees(brng);
//        if(brng < 0)
//            brng = brng +360;
//        return brng;
        MyLatLng A=new MyLatLng(lat_a,lng_a);
        MyLatLng B=new MyLatLng(lat_b,lng_b);

        return getAngle(A,B);
    }

    /**
     * 获取AB连线与正北方向的角度
     * @param A  A点的经纬度
     * @param B  B点的经纬度
     * @return  AB连线与正北方向的角度（0~360）
     */
    public  static double getAngle(MyLatLng A,MyLatLng B){
        double dx=(B.m_RadLo-A.m_RadLo)*A.Ed;
        double dy=(B.m_RadLa-A.m_RadLa)*A.Ec;
        double angle=0.0;
        angle=Math.atan(Math.abs(dx/dy))*180./Math.PI;
        double dLo=B.m_Longitude-A.m_Longitude;
        double dLa=B.m_Latitude-A.m_Latitude;
        if(dLo>0&&dLa<=0){
            angle=(90.-angle)+90;
        }
        else if(dLo<=0&&dLa<0){
            angle=angle+180.;
        }else if(dLo<0&&dLa>=0){
            angle= (90.-angle)+270;
        }
        return angle;
    }
    static class MyLatLng {
        final static double Rc=6378137;
        final static double Rj=6356725;
        double m_LoDeg,m_LoMin,m_LoSec;
        double m_LaDeg,m_LaMin,m_LaSec;
        double m_Longitude,m_Latitude;
        double m_RadLo,m_RadLa;
        double Ec;
        double Ed;
        public MyLatLng(double longitude,double latitude){
            m_LoDeg=(int)longitude;
            m_LoMin=(int)((longitude-m_LoDeg)*60);
            m_LoSec=(longitude-m_LoDeg-m_LoMin/60.)*3600;

            m_LaDeg=(int)latitude;
            m_LaMin=(int)((latitude-m_LaDeg)*60);
            m_LaSec=(latitude-m_LaDeg-m_LaMin/60.)*3600;

            m_Longitude=longitude;
            m_Latitude=latitude;
            m_RadLo=longitude*Math.PI/180.;
            m_RadLa=latitude*Math.PI/180.;
            Ec=Rj+(Rc-Rj)*(90.-m_Latitude)/90.;
            Ed=Ec*Math.cos(m_RadLa);
        }
    }


    /**
     * @param ALon
     * @param ALat
     * @param pointsList
     * @return
     */
    public  static boolean isPtInPoly(double ALon, double ALat,List<Geofences.GeofencesBean.PointsBean> pointsList) {
        boolean result;
        int iSum = 0;
        double dLon1, dLon2, dLat1, dLat2, dLon;
        if (pointsList.size() < 3) {
            result = false;
        } else {
            int iCount = pointsList.size();
            for (int i = 0; i < iCount - 1; i++) {
                if (i == iCount - 1) {
                    dLon1 = pointsList.get(i).getLon();
                    dLat1 = pointsList.get(i).getLat();
                    dLon2 = pointsList.get(0).getLon();
                    dLat2 = pointsList.get(0).getLat();
                }else {
                    dLon1 =  pointsList.get(i).getLon();
                    dLat1 = pointsList.get(i).getLat();
                    dLon2 = pointsList.get(i+1).getLon();
                    dLat2 = pointsList.get(i+1).getLat();
                }
                //以下语句判断A点是否在边的两端点的水平平行线之间，在则可能有交点，开始判断交点是否在左射线上
                if (((ALat >= dLat1) && (ALat < dLat2)) || ((ALat >= dLat2) && (ALat < dLat1))) {
                    if (Math.abs(dLat1 - dLat2) > 0) {
                        //得到 A点向左射线与边的交点的x坐标：
                        dLon = dLon1 - ((dLon1 - dLon2) * (dLat1 - ALat)) / (dLat1 - dLat2);

                        // 如果交点在A点左侧（说明是做射线与 边的交点），则射线与边的全部交点数加一：
                        if (dLon < ALon)
                            iSum++;
                    }
                }
            }
            result = iSum % 2 != 0;
        }
        return result;
    }

}
