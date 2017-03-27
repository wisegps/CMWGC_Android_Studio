package com.wgc.cmwgc.event;

import com.wgc.cmwgc.app.Config;

/**
 * 功能： descriable
 * 作者： Administrator
 * 日期： 2017/3/14 14:21
 * 邮箱： descriable
 */
public class CheckEvent {

    private String lat;
    private String lon;
    private String gpsFlag;
    private String speed;
    private String direct;
    private String signal;
    private String createdAt;
    private String gpsTime;
    private String rcvTime;
    private String mileage;
    private String fuel;
    private String status;

    public CheckEvent(String lat, String lon, String speed) {
        this.lat = lat;
        this.lon = lon;
        this.speed = speed;
    }

    public CheckEvent(String lat, String lon, String gpsFlag, String speed, String direct, String signal, String createdAt, String gpsTime, String rcvTime, String mileage, String fuel, String status) {
        this.lat = lat;
        this.lon = lon;
        this.gpsFlag = gpsFlag;
        this.speed = speed;
        this.direct = direct;
        this.signal = signal;
        this.createdAt = createdAt;
        this.gpsTime = gpsTime;
        this.rcvTime = rcvTime;
        this.mileage = mileage;
        this.fuel = fuel;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getGpsFlag() {
        return gpsFlag;
    }

    public void setGpsFlag(String gpsFlag) {
        this.gpsFlag = gpsFlag;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getDirect() {
        return direct;
    }

    public void setDirect(String direct) {
        this.direct = direct;
    }

    public String getSignal() {
        return signal;
    }

    public void setSignal(String signal) {
        this.signal = signal;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getGpsTime() {
        return gpsTime;
    }

    public void setGpsTime(String gpsTime) {
        this.gpsTime = gpsTime;
    }

    public String getRcvTime() {
        return rcvTime;
    }

    public void setRcvTime(String rcvTime) {
        this.rcvTime = rcvTime;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }



    @Override
    public String toString() {
        return "CheckEvent{" +
                "lat='" + lat + '\'' +
                ", lon='" + lon + '\'' +
                ", gpsFlag='" + gpsFlag + '\'' +
                ", speed='" + speed + '\'' +
                ", direct='" + direct + '\'' +
                ", signal='" + signal + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", gpsTime='" + gpsTime + '\'' +
                ", rcvTime='" + rcvTime + '\'' +
                ", mileage='" + mileage + '\'' +
                ", fuel='" + fuel + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

}
